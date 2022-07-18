package ene_colombetti_amto.sussysquares.game_elements.attacks;

import ene_colombetti_amto.sussysquares.game_elements.Susser;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class Bullet extends Circle {

    //Movement
    private double hsp;
    private double vsp;

    //Who shot the bullet
    private final Susser shooter;

    public Bullet(double x, double y, Paint color, Susser origin){
        super();
        this.setRadius(10);
        this.setCenterX(x);
        this.setCenterY(y);
        this.setFill(color);
        this.hsp = origin.getHsp() * 0.8; //Make the bullet slightly slower the shooters speed
        this.vsp = origin.getVsp() * 0.5; //Vsp is slower to facilitate shooting towards the opponent
        this.shooter = origin;
    }

    /**
     * Checks if the bullet is colliding with the bullet given as a parameter, this is a CIRCLE-CIRCLE
     * @param bullet Bullet we have to check a collision on
     * @return If there is a collision or not
     */
    public boolean isCollision(Bullet bullet){
        double sideX = Math.abs(this.getCenterX() - bullet.getCenterX()); //Get the sides of the triangle
        double sideY = Math.abs(this.getCenterY() - bullet.getCenterY());
        return this.getRadius() + bullet.getRadius() > Math.sqrt(sideX * sideX + sideY * sideY); //Check if the sum of the circles' radius is greater than the hypotenuse
    }

    /**
     * Same thing as before except it's a CIRCLE-RECTANGLE collision
     * @param opponent Opponent to check collision on
     * @return if a bullet is collides with a player or not
     */
    public boolean isCollision(Susser opponent){

        //If the further checks are unnecessary it means these will suffice already
        double posX = this.getCenterX();
        double posY = this.getCenterY();

        //These if statements find the position of the circle relative to the rectangle
        if (this.getCenterX() < opponent.getX()) {
            posX = opponent.getX();
        } else if (this.getCenterX() > opponent.getX() + opponent.getWidth()){
            posX = opponent.getX() + opponent.getWidth();
        }
        if (this.getCenterY() < opponent.getY()){
            posY = opponent.getY();
        } else if (this.getCenterY() > opponent.getY() + opponent.getHeight()){
            posY = opponent.getY() + opponent.getHeight();
        }

        //Finding the rectangle
        double sideX = Math.abs(this.getCenterX() - posX);
        double sideY = Math.abs(this.getCenterY() - posY);

        //If the circle's radius is greater than hypotenuse there is a collision
        return this.getRadius() > Math.sqrt(sideX * sideX + sideY * sideY);
    }

    /**
     * This is not necessary as I can just check for the radius, however I find it a much more elegant approach, returns false in the Rocket class
     * @return If this is a rocket or not
     */
    public boolean isNotRocket(){
        return true;
    }

    /**
     * Updates the position of the bullet
     * @param opponent We need the opponent here because we are going to override this method in the Rocket class, which needs a target to follow
     */
    public void update(Susser opponent){

        this.setCenterX(this.getCenterX() + this.hsp);
        this.setCenterY(this.getCenterY() + this.vsp);
    }

    //Getters and Setters
    public Susser getShooter() {
        return shooter;
    }
    public double getHsp() {
        return hsp;
    }
    public void setHsp(double hsp) {
        this.hsp = hsp;
    }
    public double getVsp() {
        return vsp;
    }
    public void setVsp(double vsp) {
        this.vsp = vsp;
    }
}

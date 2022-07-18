package ene_colombetti_amto.sussysquares.game_elements;

import ene_colombetti_amto.sussysquares.PlayField;
import ene_colombetti_amto.sussysquares.game_elements.attacks.Bullet;
import ene_colombetti_amto.sussysquares.game_elements.attacks.Rocket;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;

public class Susser extends Rectangle {

    //Movement
    private double hsp;
    private double vsp;
    private final int maxSpeed;

    //Controls
    private boolean left;
    private boolean down;
    private boolean right;
    private boolean up;
    private boolean shoot;

    //Shooting
    private int shootDelay;
    private int rocketCharge;
    private double hp;

    //Handles the animations of either winning or losing
    private double winAnimation = 0;

    //Displaying hp and rocket charge
    private final Arc rocketTelegraph = new Arc();
    private final Arc hpTelegraph = new Arc();

    //Static list of bullets, shared between the 2 players
    private static final ArrayList <Bullet> activeBullets = new ArrayList<>();

    private static boolean lazyFix = true; //I'm too lazy to fix a bug so im just going to use a static boolean

    public Susser(int x, int y, int maxSpeed, Paint color){

        super(x, y + (lazyFix ? 0.1:0), 50, 50);
        lazyFix = false;

        hsp = 0;
        vsp = 0;
        this.maxSpeed = maxSpeed;

        left = false;
        down = false;
        right = false;
        up = false;
        shoot = false;

        this.shootDelay = 60;
        this.rocketCharge = 0;

        this.hp = 3;

        //Adding these elements to the root
        PlayField.root.getChildren().add(rocketTelegraph);
        rocketTelegraph.setStartAngle(90); //Make it start from above the player
        rocketTelegraph.setRadiusY(75); //Setting the radius
        rocketTelegraph.setRadiusX(75);
        rocketTelegraph.setType(ArcType.OPEN); //Setting its type
        rocketTelegraph.setStrokeWidth(5);
        rocketTelegraph.setStroke(Color.GRAY); //Setting its colors
        rocketTelegraph.setFill(Color.rgb(0,0,0,0.5));
        PlayField.root.getChildren().add(hpTelegraph);
        hpTelegraph.setStartAngle(90);
        hpTelegraph.setRadiusY(60);
        hpTelegraph.setRadiusX(60);
        hpTelegraph.setType(ArcType.OPEN);
        hpTelegraph.setStrokeWidth(4);
        hpTelegraph.setStroke(Color.rgb(255,166,183, 0.6));
        hpTelegraph.setFill(Color.rgb(0,0,0,0.3));

        this.setFill(color);
    }

    /**
     * Handles adding bullets to the root and active bullets
     * @param bullet Bullet or rocket given as a parameter from shooting()
     */
    public void shoot(Bullet bullet){
        activeBullets.add(bullet);
        PlayField.root.getChildren().add(activeBullets.get(activeBullets.size() -1)); //Get the last added bullet
    }

    /**
     * Main function of a Susser instance, updates the game every rendered frame
     * @param opponent We need the opponent as a parameter for various operations within this method
     */
    public void update(Susser opponent){

        movement();
        shooting();
        borderCollision();
        objectCollision(opponent);

        //Checking if either played lost, play their respective win animations
        if (opponent.getHp() <= 0 && this.getHp() > 0){
            this.setRotate(winAnimation);
            winAnimation++;
        } else if (this.getHp() <= 0){
            this.setVsp(this.getVsp() + winAnimation / 10);
            winAnimation++;
        }

        //Updating telegraphy, before updating the positions to give the UI a floaty effect
        telegraphy();

        //Updating the positions
        this.setX(this.getX() + this.hsp);
        this.setY(this.getY() + this.vsp);

    }

    /**
     * Checks if there is a collision between bullets, if so, deflect the bullets, deflect the bullets on the rocket, or stop the rockets from going through each other
     * @param opponent We need to check if the bullet actually hits a player as well
     */
    private void objectCollision(Susser opponent){

        ArrayList <Bullet> temps = new ArrayList<>(); //We need a temp because removing an element from an arrayList while we iterate through it causes an exception
        for (Bullet i : activeBullets){
            i.update(opponent);
            for (Bullet activeBullet : activeBullets){ //Comparing i with every active bullet
                if (!i.equals(activeBullet) && i.isCollision(activeBullet)){ //If it's not checking the same bullet, and the bullets are colliding
                    if (i.isNotRocket()) { //If i is not a rocket
                        i.setHsp(-i.getHsp()); //Bounce the bullets off each other
                        activeBullet.setHsp(-activeBullet.getHsp());
                    } else { //If it is a rocket
                        if (activeBullet.isNotRocket()){//Check if the active bullet is not a rocket
                            activeBullet.setRadius(activeBullet.getRadius() -1); //If a bullet hits a rocket almost perfectly, the rocket disintegrates the bullet
                        } else { //If the active bullet is also a rocket
                            //The rockets cannot go through each other
                            activeBullet.setCenterX(activeBullet.getCenterX() + Math.signum(i.getHsp()) * 2);
                            activeBullet.setCenterY(activeBullet.getCenterY() + Math.signum(i.getVsp()) * 2);
                            i.setCenterX(i.getCenterX() + Math.signum(activeBullet.getHsp()) * 2);
                            i.setCenterY(i.getCenterY() + Math.signum(activeBullet.getVsp()) * 2);
                        }
                    }
                }
            }
            if (i.isCollision(opponent) && !i.getShooter().equals(opponent)){ //If a bullet touches a player
                opponent.hp -= 0.1; //Lower his HP every frame by 0.1
            }
            //If a bullet goes too far out, save in a list of temps that are going to be removed later when we exit the for loop
            if (i.getCenterX() > PlayField.scene.getWidth() + 100
                    || i.getCenterX() < PlayField.scene.getX() - 100
                    || i.getCenterY() > PlayField.scene.getHeight()
                    || i.getCenterY() < PlayField.scene.getY()

                    || !i.isNotRocket() && i.getRadius() <= 0 //Removing rockets
            ){
                PlayField.root.getChildren().remove(i);
                temps.add(i);
            }
        }
        //Removing the temps
        for (Bullet temp : temps){
            PlayField.root.getChildren().remove(temp);
            activeBullets.remove(temp);
        }
    }

    /**
     * Forbidding the players from going out of bounds, or trespassing in each other's territory
     */
    private void borderCollision(){
        //Simple rectangle collision condition, no big deal
        if (this.getX() + this.getWidth() + this.hsp > PlayField.half.getEndX() - PlayField.half.getStrokeWidth() / 2
                && this.getX() < PlayField.half.getEndX()
                || this.getX() + this.hsp < PlayField.half.getEndX() + PlayField.half.getStrokeWidth() / 2
                && this.getX() + this.getWidth() > PlayField.half.getEndX()

                || this.getX() + hsp < PlayField.scene.getX() - 8 || this.getX() + this.getWidth() + hsp > PlayField.scene.getWidth()
        ){
            this.hsp = 0;
        }
        if (this.getY() + this.getHeight() + vsp > PlayField.scene.getHeight() || this.getY() + vsp < PlayField.scene.getY() - 32){
            this.vsp = 0;
        }

    }

    /**
     * Updating the arcs displaying rocket charge and hp
     */
    private void telegraphy(){

        rocketTelegraph.setCenterX(this.getX() + this.getWidth() / 2); //Put it in the center of the player
        rocketTelegraph.setCenterY(this.getY() + this.getHeight() / 2);
        rocketTelegraph.setLength(rocketCharge); //Rocket charge handles its length

        hpTelegraph.setCenterX(this.getX() + this.getWidth() / 2);
        hpTelegraph.setCenterY(this.getY() + this.getHeight() / 2);
        if (hp >= 0){
            hpTelegraph.setLength(hp * 120);
        } else { //There was a small bug where the player would lose but his hp wouldn't go down anymore, so I forced it
            hpTelegraph.setLength(0);
        }
    }

    /**
     * Handles everything related to shooting, either bullets or rockets
     */
    private void shooting(){

        //We only want to be able to shoot when we are moving at a certain speed, this is an interesting twist I think
        if (shoot && shootDelay == 0 && Math.abs(hsp) + Math.abs(vsp) > 5 && rocketCharge == 0){
            this.shoot(new Bullet( //Shoot a bullet with these default values
                    this.getX() + (this.getWidth() / 2),
                    this.getY() + (this.getHeight() / 2),
                    this.getFill(), this
            ));

            shootDelay = 60; //Reset the delay of our gun
        } else if (shootDelay > 0) shootDelay--; //Decrement it so we can eventually shoot again

        //If we are holding the shoot button, charge the rocket
        if (shoot){
            rocketCharge += 2;
        } else { //If we stop holding it, check if the charge is enough to shoot a rocket
            if (rocketCharge > 360){
                this.shoot(new Rocket(
                        this.getX() + (this.getWidth() / 2),
                        this.getY() + (this.getHeight() / 2),
                        this.getFill(), this
                ));
            }
            rocketCharge = 0; //And reset the charge after
        }
    }

    /**
     * Handles everything related to the players' movement
     */
    private void movement(){

        //We only want to be able to move if we are holding either arrow key, for example right && !left is good, right && left is bad
        if (hsp < maxSpeed && hsp > -maxSpeed) {
            if (!right && left){
                hsp--;
            } else if (right && !left){
                hsp++;
            }
        }
        if (vsp < maxSpeed && vsp > -maxSpeed) {
            if (!down && up){
                vsp--;
            } else if (down && !up){
                vsp++;
            }
        }

        //Friction
        hsp -= 0.5 * Math.signum(hsp);
        vsp -= 0.5 * Math.signum(vsp);
    }

    //Getters and Setters
    public double getHp() {
        return hp;
    }
    public double getHsp() {
        return hsp;
    }
    public double getVsp() {
        return vsp;
    }
    public void setVsp(double vsp) {
        this.vsp = vsp;
    }
    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }
    public void setLeft(boolean left) {
        this.left = left;
    }
    public void setDown(boolean down) {
        this.down = down;
    }
    public void setRight(boolean right) {
        this.right = right;
    }
    public void setUp(boolean up) {
        this.up = up;
    }
}

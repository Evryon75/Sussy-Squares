package ene_colombetti_amto.sussysquares.game_elements.attacks;

import ene_colombetti_amto.sussysquares.game_elements.Susser;
import javafx.scene.paint.Paint;

public class Rocket extends Bullet{

    private int lifeSpan; //How long the rocket can be on screen

    public Rocket(double x, double y, Paint color, Susser origin) {
        super(x, y, color, origin);
        this.setRadius(20); //Bigger radius than the bullet
        this.lifeSpan = 450;
    }

    /**
     * As I said in the super class, this method is needed to check if the bullet is a rocket or not
     * @return If the bullet is a rocket or not
     */
    @Override
    public boolean isNotRocket() {
        return false;
    }

    /**
     * Updates the rockets speeds relative to its position and the targets position
     * @param opponent We need the opponent here because we are going to override this method in the Rocket class, which needs a target to follow
     */
    @Override
    public void update(Susser opponent) {
        if (!this.getShooter().equals(opponent)) { //If the opponent given as a parameter is not the one who shot the rocket

            //Find the orientations it should follow
            double orientationX = -Math.signum(this.getCenterX() - opponent.getX() - (opponent.getWidth() / 2));
            double orientationY = -Math.signum(this.getCenterY() - opponent.getY() - (opponent.getHeight() / 2));

            //Find the sides of the triangle
            double aB = Math.abs(this.getCenterX() - opponent.getX() - (opponent.getWidth() / 2));
            double bC = Math.abs(this.getCenterY() - opponent.getY() - (opponent.getHeight() / 2));
            double cA = Math.sqrt(aB * aB + bC * bC);

            //Find the angles by calculating the arc cosine of the cosine found from the sides and divide them by 90 to have a 0-1 ratio
            double verticalAngle = (Math.acos(aB / cA) * 180 / Math.PI) / 90;
            double horizontalAngle = (Math.acos(bC / cA) * 180 / Math.PI) / 90;

            //Change the speeds according to the angles found, the speed (which in this case is 4), and the orientation
            this.setHsp(horizontalAngle * 4 * orientationX);
            this.setVsp(verticalAngle * 4 * orientationY);

            //Update the position of the rocket
            this.setCenterX(this.getCenterX() + this.getHsp());
            this.setCenterY(this.getCenterY() + this.getVsp());

            this.lifeSpan--; //Decrease its lifespan

            //If its lifespan is 0, decrease its radius every frame
            if (this.lifeSpan < 0) {
                this.setRadius(this.getRadius() - 1);
            }
        }
    }
}

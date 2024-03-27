package model;

/**
 * (package-private) Class that represents a Fox, a specific implementation of {@link Animal}.
 */
class Fox extends Animal {
    private Bunny usingThisPrey;

    /**
     * (package-private) Constructor for a new Fox with the specified parameters, furthermore sets its herbivore flag false.
     * @param id Assigns ID number, which should be unique.
     * @param sightRange Assigns the range a bunny can "look". Should be a positive value.
     * @param x Assigns the initial spawn position on the x-axis.
     * @param y Assigns the initial spawn position on the y-axis.
     */
    Fox (int id, double sightRange, float x, float y) {super(id, sightRange, x, y, false);}

    /**
     * (package-private) Handles hunting behavior of a fox, if a {@link Bunny}, is in sight of the fox.
     * The Method also contains the eating behavior of the fox, which is activated when the prey is caught.
     * <p>
     * Note: This function should only be called with restrictions based on its {@link AnimalState}, unless the fox should be hunting constantly.
     * @param bunny The prey to be handled.
     */
    void handleHunger(Bunny bunny){
        double distanceToPrey = Math.hypot(bunny.getAnimalAttributes().x() - getAnimalAttributes().x(), bunny.getAnimalAttributes().y() - getAnimalAttributes().y());

        findPrey(bunny, distanceToPrey);

        // if prey is found try to catch it
        if(usingThisPrey != null){
            setObjectiveInSight(true);
            moveTo(usingThisPrey.getAnimalAttributes().x(), usingThisPrey.getAnimalAttributes().y());
            setVxAndVy(getAnimalAttributes().vx() * 1.1f,getAnimalAttributes().vy() * 1.1f);

            // if prey is caught start eating
            if (getAnimalAttributes().objectiveInSight() && Math.hypot(usingThisPrey.getAnimalAttributes().x() - getAnimalAttributes().x(), usingThisPrey.getAnimalAttributes().y() - getAnimalAttributes().y()) <= 5) {
                setEating(true);
                usingThisPrey.setDead();
                stopMoving();
            }
        }
    }

    // is being called to reset values once the fox has eaten

    /**
     * (package-private) Resets every aspect of the fox, which change to hunt or eat a bunny, back to its base values.
     * Only if the fox is eating and the hunger value is depleted.
     */
    void stopHunting(){
        if (getAnimalAttributes().hunger() <= 0 && getAnimalAttributes().eating()){

            setEating(false);

            usingThisPrey = null;
            setHunger(-1);

            setThirst(getAnimalAttributes().thirst() - 20);
            setObjectiveInSight(false);

            setRandomDirection();
        }
    }

    /**
     * (private) Checks if bunny is in sight, if it is, the sighted bunny gets saved to 'usingThisPrey'.
     * Furthermore, it changes its "target" to the nearest bunny.
     * @param prey The bunny to be handled.
     * @param distanceToPrey The distance to the bunny being handled. It should be calculated beforehand.
     */
    private void findPrey(Bunny prey, double distanceToPrey){
        if(usingThisPrey == null){
            if (getAnimalAttributes().sightRange() > distanceToPrey)usingThisPrey = prey;
            else usingThisPrey = null;

        }else{
            double distanceToUsingThisPrey = Math.hypot(usingThisPrey.getAnimalAttributes().x() - getAnimalAttributes().x(), usingThisPrey.getAnimalAttributes().y() - getAnimalAttributes().y());

            if(getAnimalAttributes().sightRange() > distanceToPrey && distanceToUsingThisPrey > distanceToPrey) usingThisPrey = prey;
            if(getAnimalAttributes().sightRange() < distanceToUsingThisPrey) usingThisPrey = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override void handleMating(Animal other) { if(!other.getAnimalAttributes().herbivore() && !this.equals(other)) matingHelper(other); }
    Bunny getUsingThisPrey() {return this.usingThisPrey;}
}
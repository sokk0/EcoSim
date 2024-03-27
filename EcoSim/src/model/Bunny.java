package model;

import java.util.Random;
/**
 * (package-private) Class that represents a Bunny, a specific implementation of {@link Animal}.
 */
class Bunny extends Animal {
    private Random random = new Random();
    private int ponder;
    private Fox runningAwayFromThisFox;

    /**
     * (package-private) Constructor for a new Bunny with the specified parameters, furthermore sets its herbivore flag true.
     * @param id Assigns ID number, which should be unique.
     * @param sightRange Assigns the range a bunny can "look". Should be a positive value.
     * @param x Assigns the initial spawn position on the x-axis.
     * @param y Assigns the initial spawn position on the y-axis.
     */
    Bunny(int id, double sightRange, float x, float y) {super(id, sightRange, x, y, true);}


    /**
     * (package-private) Handles hunger of a bunny by invoking the {@link #handleResource(Resource, boolean)} function and setting its boolean handleWater false.
     * @param resource The resource to be handled.
     */
    void handleHunger(Resource resource) {handleResource(resource, false);}

    /**
     * {@inheritDoc}
     */
    @Override void handleMating(Animal other) {if(other.getAnimalAttributes().herbivore() && !this.equals(other)) matingHelper(other);}


    /**
     * (private) Causes the bunny to stop thinking, which allows it to start moving again (Because animals can only move if there not thinking),
     * if the global ponder counter reaches a randomly set value between 200 and 250.
     * <p>
     * Note: The ponder counter is incrementing in this method whilst the bunny is thinking.
     */
    private void ponderingBunny(){
        // if ponder counter, or rather the bunny stood long enough around it can start moving again and the counter is reset
        if(ponder >= random.nextInt(200,250)){
            setThinking(false);
            ponder = 0;
        }
        // increment ponder, if the bunny is thinking
        if(getAnimalAttributes().thinking()) ponder++;
    }


    /**
     * (private) Randomly changes the direction of a bunny and sets the flag thinking to true, which causes the bunny to stop moving.
     * @param changeDirectionProbability Indicates the probability of this method to work. A Value from '0.0' to '1.0' should be chosen, with '0.0' being a 0% and '1.0' being a 100% chance.
     */
    private void randomDirectionChange(double changeDirectionProbability) {

        //trying to change vx direction
        if (!getAnimalAttributes().objectiveInSight() && !getAnimalAttributes().thinking() && random.nextDouble(0,1) < changeDirectionProbability ){
            setThinking(true);
            setVxAndVy(getAnimalAttributes().vx() * -1, getAnimalAttributes().vy());

        //trying to change vx direction
        }else if (!getAnimalAttributes().objectiveInSight() && !getAnimalAttributes().thinking() && random.nextDouble(0,1) < changeDirectionProbability){
            setThinking(true);
            setVxAndVy(getAnimalAttributes().vx(), getAnimalAttributes().vy() * -1);
        }
    }

    /**
     * (package-private) Restricts in which situation a bunny can change its direction randomly.
     * <p>
     * This method invokes {@link #randomDirectionChange(double)} and {@link #ponderingBunny()}
     * @param changeDirectionProbability Indicates the probability of this method to work. A Value from '0.0' to '1.0' should be chosen, with '0.0' being a 0% and '1.0' being a 100% chance.
     */
    void randomBunnyMovement(double changeDirectionProbability){
        if(!getAnimalAttributes().eating() && !getAnimalAttributes().drinking() && !getAnimalAttributes().mating()){
            randomDirectionChange(changeDirectionProbability);
            ponderingBunny();
        }
    }


    /**
     * (package-private) Handles escaping behavior, if a potential predator {@link Animal}, identified as a {@link Fox}, is in sight of the bunny.
     * The Method also allows to reset its values, once the threat is outside its sight range.
     * @param potentialPredator Any animal which is decided upon internally to be counted as predator or not.
     */
    void dodgeFox(Animal potentialPredator){
        double distanceToFox = Math.hypot(potentialPredator.getAnimalAttributes().x() - getAnimalAttributes().x(), potentialPredator.getAnimalAttributes().y() - getAnimalAttributes().y());

        // check if fox is in sight and not currently running away from another fox
        if(potentialPredator instanceof Fox fox && getAnimalAttributes().sightRange() > distanceToFox && runningAwayFromThisFox == null){
            runningAwayFromThisFox = fox;
            setState(AnimalState.HUNTED);
            setObjectiveInSight(true);
            stopEveryAction();
            setRandomDirection();

        // check if fox is not in sight anymore to return to "normal"
        }else if(runningAwayFromThisFox != null && getAnimalAttributes().sightRange() < Math.hypot(runningAwayFromThisFox.getAnimalAttributes().x() - getAnimalAttributes().x(), runningAwayFromThisFox.getAnimalAttributes().y() - getAnimalAttributes().y())){
            setRandomDirection();
            runningAwayFromThisFox = null;
            setObjectiveInSight(false);
            setState(AnimalState.IDLE);
        }
    }

    /**
     * (private) Stops every action a bunny could be occupied with.
     */
    private void stopEveryAction(){
        if(getAnimalAttributes().eating() || getAnimalAttributes().drinking()) getAnimalAttributes().usingThisResource().setCurrentlyInUse(false);
        setUsingThisMate(null);
        setUsingThisResource(null);
        setActivityBooleans(false);
    }
}
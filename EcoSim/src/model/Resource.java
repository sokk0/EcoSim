package model;

import java.util.Random;


/**
 * Class that represents nutrition for animals.
 * It encapsulates the general attributes and functionalities of resources in the simulation.
 * <p>
 * The class acts as a data container to the controller and is read-only outside the model to avoid changes from the outside.
 * <p>
 * Note: According to the guidelines set by Prof. Dr. Martin Weigel in the javadoc for the class <a href="https://moodle.thm.de/course/view.php?id=10282#section-4">'Pokemon.java' from 'Evolis Adventure' (Final Edition)</a>, this class is intentionally made public.
 * @author Sleman Kakar
 */
public class Resource{
    private final boolean resourceTypeWater;
    private final float x, y;
    private double remainingPercentage;
    private boolean regenerating, unusable, currentlyInUse;


    /**
     * (package-private) Constructor for new Resource with specified parameters.
     * @param resourceTypeWater Assigns water flag to differentiate between water and grass.
     * @param width Assigns the initial spawn position based on the width.
     * @param height Assigns the initial spawn position on the width.
     */
    Resource(boolean resourceTypeWater, int width, int height){
        Random random = new Random();
        this.resourceTypeWater = resourceTypeWater;
        this.remainingPercentage = 100.0;

        // randomly assign position within eco sim
        x = random.nextFloat(10, width - 10);
        y = random.nextFloat(10, height- 10);
    }


    /**
     * (package-private) Checks if this resource is {@link #currentlyInUse}, if it is {@link #remainingPercentage} will be decremented.
     */
    void usage() {
        if(currentlyInUse) this.remainingPercentage -= 0.1;
    }

    /**
     * (package-private) Checks the {@link #remainingPercentage} and based on how low it is the method sets the regenerating and unusable flag.
     * Furthermore, this method lets this resource regenerate if it is not being used.
     */
    void regenerate(){

        //checks remaining percentage
        if(remainingPercentage <= 100){
            regenerating = true;
            if(remainingPercentage <= 0) unusable = true;
        }

        //lets resource regenerate, if it's not being used
        if(regenerating && !currentlyInUse){
            remainingPercentage += 0.1;

            if(remainingPercentage >= 100) regenerating = unusable = false;
        }
    }

    /**
     * (package-private) Getter returning resource attributes accessed with:
     * <pre>{@code
     *  someResource.getResourceAttributes().resourceTypeWater() // returns the resourceTypeWater boolean of 'someResource'
     * }</pre>
     *
     * Note: This method instantiates a new object of the {@link ResourceAttributes} record.
     * The design choice prioritizes code conciseness over minimizing object creation overhead.
     * The garbage collector is expected to handle the disposal of unnecessary objects during usage.
     * @return A new instance of {@link ResourceAttributes} with the current values of this animal.
     */
    ResourceAttributes getResourceAttributes(){return new ResourceAttributes(this.resourceTypeWater, this.x, this.y, this.remainingPercentage, this.unusable, this.currentlyInUse);}
    boolean getUnusable(){return this.unusable;}
    void setCurrentlyInUse(boolean currentlyInUse){this.currentlyInUse = currentlyInUse;}
}

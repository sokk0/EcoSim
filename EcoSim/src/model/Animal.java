package model;


import java.util.Random;

/**
 * Abstract class, which is the base of animals within the simulation.
 * It provides the general attributes and functionalities common to animals.
 * <p>
 * The class acts as a data container to the controller and is read-only outside the model to avoid changes from the outside.
 * <p>
 * Note: According to the guidelines set by Prof. Dr. Martin Weigel in the javadoc for the class <a href="https://moodle.thm.de/course/view.php?id=10282#section-4">'Pokemon.java' from 'Evolis Adventure' (Final Edition)</a>, this class is intentionally made public.
 * @author Sleman Kakar
 */
public abstract class Animal{
    private float x, y, vx, vy;
    private double hunger, thirst, matingUrge, sightRange;
    private Random random = new Random();
    private AnimalState state;
    private boolean dead, eating, drinking, objectiveInSight, thinking, mating, pregnant;
    private final boolean herbivore;
    private Resource usingThisResource;
    private Animal usingThisMate;
    private final int id;


    /**
     * (package-private) Constructor for new Animal with specified parameters, also sets the initial direction randomly.
     * @param id Assigns ID number, which should be unique.
     * @param sightRange Assigns the range an animal can "look". Should be a positive value.
     * @param x Assigns the initial spawn position on the x-axis.
     * @param y Assigns the initial spawn position on the y-axis.
     * @param isHerbivore Assigns if an animal is a Herbivore or not.
     */
    Animal(int id, double sightRange, float x, float y, boolean isHerbivore) {
        this.id = id;

        this.sightRange = sightRange;
        this.herbivore = isHerbivore;

        this.x = x;
        this.y = y;

        state = AnimalState.NEWBORN;

        setRandomDirection();
    }

    /**
     * (package-private) Sets random, positive or negative, direction in the x- and y-axis.
     */
    void setRandomDirection(){
        vx = (random.nextFloat() >= 0.5) ? 1 : -1;
        vy = (random.nextFloat() >= 0.5) ? 1 : -1;
    }

    /**
     * (package-private) Handles the wallCollision.
     *
     * @param width Indicates the width of the simulation.
     * @param height Indicates the height of the simulation.
     */
    void wallCollision(int width, int height){
        vx = (x <= 0 || x >= width) ? -vx : vx;
        vy = (y <= 0 || y >= height) ? -vy : vy;
    }

    /**
     * (private) Updates hunger based on if animal is currently eating or not.
     * <p>
     * Note: hunger is not updated if the Animal is currently drinking or mating.
     */
    private void hungerUpdater(){if(!drinking && !mating) hunger = (hunger >= 0) ? hunger + ((eating) ? -0.1 : 0.05) : 0;}

    /**
     * (private) Updates thirst based on if animal is currently drinking or not.
     * <p>
     * Note: thirst is not updated if the Animal is currently eating or mating.
     */
    private void thirstUpdater(){if(!eating && !mating) thirst = (thirst >= 0) ? thirst + ((drinking) ? -0.1 : 0.1) : 0;}

    /**
     * (private) Updates matingUrge based on if animal is currently mating or not.
     * <p>
     * Note: matingUrge is not updated if the Animal is currently eating or drinking.
     */
    private void matingUrgeUpdater(){matingUrge = (matingUrge >= 0 && matingUrge <= 100) ? matingUrge + (mating ? -0.5 : 0.3) : (matingUrge <= 0) ? 0 : 100;}


    /**
     * (package-private) Decides the {@link AnimalState} of an animal based on its hunger, thirst and current state.
     */
    void decideState(){
        dead = (hunger >= 100) || (thirst >= 100);

        if(state == AnimalState.NEWBORN) state = (hunger >= 50 || thirst >= 50) ? (hunger >= thirst ? AnimalState.HUNGRY : AnimalState.THIRSTY) : AnimalState.NEWBORN;

        if(state == AnimalState.IDLE)state = (hunger >= 50 || thirst >= 50) ? (hunger >= thirst ? AnimalState.HUNGRY : AnimalState.THIRSTY) : AnimalState.IDLE;

        else if(state == AnimalState.THIRSTY) state = (thirst <= 0) ? AnimalState.IDLE : AnimalState.THIRSTY;

        else if(state == AnimalState.HUNGRY) state = (hunger <= 0) ? AnimalState.IDLE : AnimalState.HUNGRY;
    }

    /**
     * (package-private) Changes the vx and vy direction of this animal, which enables an animal to move to specified point.
     * <p>
     * Note: the function also decides how high or low the values should be chosen relative to each other and the base speed 'one'.
     * @param x1 Indicates the x-position of destination point.
     * @param y1 Indicates the y-position of destination point.
     */
    void moveTo(float x1, float y1){
        //calculates the slope between this animal and the destination
        float m = Math.abs((y1 - y) / (x1 - x));
        //calculates the vxUpdate value based on the slope
        float vxUpdate = Math.abs(vx) / Math.abs(m);

        //decides if the distance between the x's or the y's is higher, based on that it assigns the vx value with 'one' or the local vxUpdate variable.
        vx = ((Math.abs(x1 - x)) > (Math.abs(y1 - y))) ? ((x <= x1) ? 1 : -1) : ((x <= x1) ? vxUpdate : -vxUpdate) ;
        //decides if the distance between the x's or the y's is higher, based on that it assigns the vy value with 'one' or the local m variable.
        vy = ((Math.abs(x1 - x)) > (Math.abs(y1 - y))) ? ((y <= y1) ? m : -m) : ((y <= y1) ? 1 : -1);

        //the distance between the x's and the distance between the y's is important, because otherwise an animal would "shoot up" to its destination, which doesn't look good
    }

    /**
     * (package-private) Adds the vx and vy to its x- and y-coordinates.
     * <p>
     * It also calls the functions thirst-, hunger- and matingUrgeUpdater, which creates the illusion that an animal gets hungry or thirsty based on its movements.
     */
    void movement(){
        if(!thinking){
            x += vx;
            y += vy;

            thirstUpdater();
            hungerUpdater();
            matingUrgeUpdater();
        }
    }

    /**
     * (package-private) Handles what an animal should do with a {@link Resource}, if it should move towards a resource and use it or leave it.
     * @param resource The resource to be handled.
     * @param handleWater A boolean deciding, if water or food is handled.
     */
    void handleResource(Resource resource, boolean handleWater){
        //checks the resource type and if its usable
        if(resource.getResourceAttributes().resourceTypeWater() == handleWater && !resource.getUnusable()){
            double distanceToResource = Math.hypot(resource.getResourceAttributes().x() - x, resource.getResourceAttributes().y() - y);

            //checks if the resource is within the animals sight and...
            if(findResource(resource, distanceToResource) != null && !resource.getResourceAttributes().currentlyInUse() && !resource.getUnusable()){
                objectiveInSight = true;
                moveTo(usingThisResource.getResourceAttributes().x(), usingThisResource.getResourceAttributes().y());
            }

            //checks if the animal 'thinks' it is currently using a resource, but is not, based on that values will be reset
            if(usingThisResource != null && usingThisResource.getResourceAttributes().currentlyInUse() && (!drinking && resource.getResourceAttributes().resourceTypeWater()) ^ (!eating && !resource.getResourceAttributes().resourceTypeWater())) {
                usingThisResource = null;
                objectiveInSight = false;
                setRandomDirection();
            }

            //checks if the animal is at the location of the saved resource and starts eating/drinking and stops its movement
            if(usingThisResource != null && objectiveInSight && Math.hypot(usingThisResource.getResourceAttributes().x() - x, resource.getResourceAttributes().y() - y ) <= 1 && !resource.getResourceAttributes().currentlyInUse()){
                stopMoving();

                if(usingThisResource.getResourceAttributes().resourceTypeWater()) drinking = true;
                else eating = true;
                usingThisResource.setCurrentlyInUse(true);
            }
        }
    }

    /**
     * (package-private) Handles thirst by invoking the {@link #handleResource(Resource, boolean)} function and setting its boolean handleWater true.
     * @param resource The resource to be handled.
     */
    void handleThirst(Resource resource) {handleResource(resource, true);}

    /**
     * (package-private) Checks if resource is in sight, if it is, the sighted resource gets saved to 'usingThisResource' and will be also returned.
     * @param resource The resource to be handled.
     * @param distanceToPotentialResource The distance to the resource being handled. It should be calculated beforehand.
     * @return The sighted resource, or null if no resource is in sight.
     */
    Resource findResource(Resource resource, double distanceToPotentialResource) { return (sightRange > distanceToPotentialResource && !objectiveInSight && !resource.getUnusable() && !resource.getResourceAttributes().currentlyInUse()) ? usingThisResource = resource : null; }


    /**
     * (package-private) Resets every aspect of an animal, which change to use a resource, back to its base values.
     * Only if the animals {@link AnimalState} is thirsty/hungry and the respective thirst/hunger value is depleted.
     */
    void stopUsingResource(){
        if (usingThisResource != null && ((state == AnimalState.HUNGRY && hunger <= 0) || (state == AnimalState.THIRSTY && thirst <= 0))){
            usingThisResource.setCurrentlyInUse(false);
            usingThisResource = null;
            drinking = eating = objectiveInSight = thinking = false;
            setRandomDirection();
        }
    }


    /**
     * (package-private) Abstract method since animals need to implement the ability to mate, but only with their own kind.
     * With this method the animals can get seperated in their implementation of this method.
     * <p>
     * @param other Another animal available for mating.
     * @implNote Subclasses should override this method to provide mating behavior for a specific animal type, it should call {@link #matingHelper(Animal)} and restrict it to activate, so only animals of the same type can be passed to {@link #matingHelper(Animal)}
     */
    abstract void handleMating(Animal other);

    /**
     * (package-private) Handles mating behavior, furthermore checks if a mate is in animals sight and both are in the condition to mate. <br>
     * Conditions for mating for both animals:
     * <ul>
     *      <li>They need to be in {@link AnimalState} idle</li>
     *      <li>Their urge to mate needs to be high enough</li>
     * </ul>
     * @param other Another animal, should be of the same type (handled by {@link #handleMating(Animal)}).
     */
    void matingHelper(Animal other){
        //checks the animal state, mating urge, hunger and thirst of both animals
        if(state == AnimalState.IDLE && other.state == AnimalState.IDLE && matingUrge >= 50 && other.matingUrge >= 50 && this.hunger <= 40 && other.hunger <= 40 && this.thirst <= 40 && other.thirst <= 40 ){
            double distanceToMate = Math.hypot(other.x - this.x, other.y - this.y);
            //finds a mate for this animal and links them with decidePairs
            if(findMate(other, distanceToMate) != null) decidePairs(other);
        }
        //checks if two animals are paired and proceeds with actions (see below)
        if((usingThisMate != null && usingThisMate.usingThisMate != null) && (usingThisMate.equals(other) && other.usingThisMate.equals(this)) && !mating && !usingThisMate.mating){

            objectiveInSight = true;
            thinking = false;
            moveTo(usingThisMate.x, usingThisMate.y);

            // checks if the pair has reached each other and proceeds with actions (see below)
            if(objectiveInSight && (Math.hypot(usingThisMate.x - this.x, usingThisMate.y - this.y) <= 10 || Math.hypot(this.x - usingThisMate.x, this.y - usingThisMate.y) <= 10 )){
                stopMoving();
                usingThisMate.stopMoving();
                mating = usingThisMate.mating = true;
            }
        }
    }

    //similar to findResource
    private Animal findMate(Animal mate, double distanceToAnimal) {return (sightRange > distanceToAnimal && !objectiveInSight) ? usingThisMate = mate : null;}

    /**
     * (private) Lock found mate to this animal, so it doesn't go to another animal.
     * <p>
     * Furthermore, letting this animal lose its mate, if its mate found another mate.
     * @param mate The potential mate to be paired with this animal.
     */
    private void decidePairs(Animal mate) {
        if(usingThisMate.equals(mate) && mate.usingThisMate == null) mate.usingThisMate = this;
        else if(usingThisMate.equals(mate) && !mate.usingThisMate.equals(this)) usingThisMate = null;
    }

    //similar to stopUsingResource
    void stopMating(Animal other){
        if (matingUrge <= 0 && usingThisMate != null){
            usingThisMate = other.usingThisMate = null;
            mating = objectiveInSight = other.mating = other.objectiveInSight = false;
            matingUrge = 0;
            pregnant = true;
            setRandomDirection();
            other.setRandomDirection();
        }
    }

    // package getter for almost all values of an animal, this was done to reduce LOC
    // in my opinion the LOC were more important than losing a little performance power
    // but the garbage collector should take care of the unnecessary objects whilst using this
    //  getter providing access to values of an animal
    // This design choice prioritizes code conciseness over minimizing object creation overhead
    // The garbage collector is expected to handle the disposal of unnecessary objects during usage

    /**
     * (package-private) Getter returning animal attributes accessed with:
     * <pre>{@code
     *  someAnimal.getAnimalAttributes().herbivore() // returns the herbivore boolean of 'someAnimal'
     * }</pre>
     *
     * Note: This method instantiates a new object of the {@link AnimalAttributes} record.
     * The design choice prioritizes code conciseness over minimizing object creation overhead.
     * The garbage collector is expected to handle the disposal of unnecessary objects during usage.
     * @return A new instance of {@link AnimalAttributes} with the current values of this animal.
     */
    AnimalAttributes getAnimalAttributes(){return new AnimalAttributes(this.dead, this.eating, this.drinking, this.objectiveInSight, this.thinking, this.mating, this.pregnant, this.herbivore, this.state, this.usingThisResource, this.usingThisMate, this.x, this.y, this.vx, this.vy, this.hunger, this.thirst, this.sightRange, this.id);}

    //Setters, some combined because they would use up too much LOC
    void setDead() {this.dead = true;}
    void setEating(boolean eating){this.eating = eating;}
    void setObjectiveInSight(boolean objectiveInSight){this.objectiveInSight = objectiveInSight;}
    void setThinking(boolean thinking){this.thinking = thinking;}
    void setPregnant(boolean pregnant){this.pregnant = pregnant;}

    /**
     * (package-private) Setter, setting following booleans: mating, thinking, drinking and eating. True or false based on input.
     * @param activity The value to set for mating, thinking, drinking, and eating.
     */
    void setActivityBooleans(boolean activity){mating = thinking = drinking = eating = activity;}
    void setUsingThisResource(Resource resource){this.usingThisResource = resource;}
    void setUsingThisMate(Animal mate){this.usingThisMate = mate;}
    void setVxAndVy(float vx, float vy){
        this.vx = vx;
        this.vy = vy;
    }
    void stopMoving(){vx = vy = 0;}
    void setHunger(double hunger){this.hunger = hunger;}
    void setThirst(double thirst){this.thirst = thirst;}
    void setMatingUrge(double matingUrge){this.matingUrge = matingUrge;}
    void setState(AnimalState newState){this.state = newState;}

    boolean isHerbivore(){return this.herbivore;}
}
public record AnimalAttributes(boolean dead, boolean eating, boolean drinking, boolean objectiveInSight, boolean thinking, boolean mating, boolean pregnant, boolean herbivore, AnimalState state, Resource usingThisResource, Animal usingThisMate, float x, float y, float vx, float vy, double hunger, double thirst, double sightRange, int id){}
public record ResourceAttributes(boolean resourceTypeWater, float x, float y, double remainingPercentage, boolean unusable, boolean currentlyInUse) {}
enum AnimalState {NEWBORN, IDLE, HUNGRY, THIRSTY, HUNTED}

import java.util.Random;
public class Resource{
    private final boolean resourceTypeWater;
    private final float x, y;
    private double remainingPercentage;
    private boolean regenerating, unusable, currentlyInUse;

    Resource(boolean resourceTypeWater, int width, int height){
        Random random = new Random();
        this.resourceTypeWater = resourceTypeWater;
        this.remainingPercentage = 100.0;

        // randomly assign position within eco sim
        x = random.nextFloat(10, width - 10);
        y = random.nextFloat(10, height- 10);
    }


    void usage() {
        if(currentlyInUse) this.remainingPercentage -= 0.1;
    }


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

    ResourceAttributes getResourceAttributes(){return new ResourceAttributes(this.resourceTypeWater, this.x, this.y, this.remainingPercentage, this.unusable, this.currentlyInUse);}
    boolean getUnusable(){return this.unusable;}
    void setCurrentlyInUse(boolean currentlyInUse){this.currentlyInUse = currentlyInUse;}
}



import java.util.Random;


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


    Animal(int id, double sightRange, float x, float y, boolean isHerbivore) {
        this.id = id;

        this.sightRange = sightRange;
        this.herbivore = isHerbivore;

        this.x = x;
        this.y = y;

        state = AnimalState.NEWBORN;

        setRandomDirection();
    }


    void setRandomDirection(){
        vx = (random.nextFloat() >= 0.5) ? 1 : -1;
        vy = (random.nextFloat() >= 0.5) ? 1 : -1;
    }


    void wallCollision(int width, int height){
        vx = (x <= 0 || x >= width) ? -vx : vx;
        vy = (y <= 0 || y >= height) ? -vy : vy;
    }


    private void hungerUpdater(){if(!drinking && !mating) hunger = (hunger >= 0) ? hunger + ((eating) ? -0.1 : 0.05) : 0;}


    private void thirstUpdater(){if(!eating && !mating) thirst = (thirst >= 0) ? thirst + ((drinking) ? -0.1 : 0.1) : 0;}


    private void matingUrgeUpdater(){matingUrge = (matingUrge >= 0 && matingUrge <= 100) ? matingUrge + (mating ? -0.5 : 0.3) : (matingUrge <= 0) ? 0 : 100;}


    void decideState(){
        dead = (hunger >= 100) || (thirst >= 100);

        if(state == AnimalState.NEWBORN) state = (hunger >= 50 || thirst >= 50) ? (hunger >= thirst ? AnimalState.HUNGRY : AnimalState.THIRSTY) : AnimalState.NEWBORN;

        if(state == AnimalState.IDLE)state = (hunger >= 50 || thirst >= 50) ? (hunger >= thirst ? AnimalState.HUNGRY : AnimalState.THIRSTY) : AnimalState.IDLE;

        else if(state == AnimalState.THIRSTY) state = (thirst <= 0) ? AnimalState.IDLE : AnimalState.THIRSTY;

        else if(state == AnimalState.HUNGRY) state = (hunger <= 0) ? AnimalState.IDLE : AnimalState.HUNGRY;
    }

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

    void movement(){
        if(!thinking){
            x += vx;
            y += vy;

            thirstUpdater();
            hungerUpdater();
            matingUrgeUpdater();
        }
    }

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

    void handleThirst(Resource resource) {handleResource(resource, true);}


    Resource findResource(Resource resource, double distanceToPotentialResource) { return (sightRange > distanceToPotentialResource && !objectiveInSight && !resource.getUnusable() && !resource.getResourceAttributes().currentlyInUse()) ? usingThisResource = resource : null; }


    void stopUsingResource(){
        if (usingThisResource != null && ((state == AnimalState.HUNGRY && hunger <= 0) || (state == AnimalState.THIRSTY && thirst <= 0))){
            usingThisResource.setCurrentlyInUse(false);
            usingThisResource = null;
            drinking = eating = objectiveInSight = thinking = false;
            setRandomDirection();
        }
    }


    abstract void handleMating(Animal other);


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


    AnimalAttributes getAnimalAttributes(){return new AnimalAttributes(this.dead, this.eating, this.drinking, this.objectiveInSight, this.thinking, this.mating, this.pregnant, this.herbivore, this.state, this.usingThisResource, this.usingThisMate, this.x, this.y, this.vx, this.vy, this.hunger, this.thirst, this.sightRange, this.id);}

    //Setters, some combined because they would use up too much LOC
    void setDead() {this.dead = true;}
    void setEating(boolean eating){this.eating = eating;}
    void setObjectiveInSight(boolean objectiveInSight){this.objectiveInSight = objectiveInSight;}
    void setThinking(boolean thinking){this.thinking = thinking;}
    void setPregnant(boolean pregnant){this.pregnant = pregnant;}


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



import java.util.Random;

class Bunny extends Animal {
    private Random random = new Random();
    private int ponder;
    private Fox runningAwayFromThisFox;


    Bunny(int id, double sightRange, float x, float y) {super(id, sightRange, x, y, true);}



    void handleHunger(Resource resource) {handleResource(resource, false);}


    @Override void handleMating(Animal other) {if(other.getAnimalAttributes().herbivore() && !this.equals(other)) matingHelper(other);}


    private void ponderingBunny(){
        // if ponder counter, or rather the bunny stood long enough around it can start moving again and the counter is reset
        if(ponder >= random.nextInt(200,250)){
            setThinking(false);
            ponder = 0;
        }
        // increment ponder, if the bunny is thinking
        if(getAnimalAttributes().thinking()) ponder++;
    }


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

    void randomBunnyMovement(double changeDirectionProbability){
        if(!getAnimalAttributes().eating() && !getAnimalAttributes().drinking() && !getAnimalAttributes().mating()){
            randomDirectionChange(changeDirectionProbability);
            ponderingBunny();
        }
    }


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

    private void stopEveryAction(){
        if(getAnimalAttributes().eating() || getAnimalAttributes().drinking()) getAnimalAttributes().usingThisResource().setCurrentlyInUse(false);
        setUsingThisMate(null);
        setUsingThisResource(null);
        setActivityBooleans(false);
    }
}



class Fox extends Animal {
    private Bunny usingThisPrey;


    Fox (int id, double sightRange, float x, float y) {super(id, sightRange, x, y, false);}


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

    @Override void handleMating(Animal other) { if(!other.getAnimalAttributes().herbivore() && !this.equals(other)) matingHelper(other); }
    Bunny getUsingThisPrey() {return this.usingThisPrey;}
}



import java.util.ArrayList;

public interface IEcoModel {
    void startNewSim(int initialFoxes, int initialBunnies, int initialWaterSources, int initialGrassSources);
    void clearOldSim();
    boolean isEcoSimulationThreadAlive();
    void startEcoSimulationThread();
    void playEcoSimulationThread(boolean activeThread);
    void setSimulationSpeed(int simulationSpeed);
    int getAnimalCountOf(boolean getHerbivore);
    int getWidth();
    int getHeight();
    ArrayList<Resource> getResourceList();
    ArrayList<Animal> getAnimalList();
    default AnimalAttributes animalAttributes(Animal animal){return animal.getAnimalAttributes(); }
    default ResourceAttributes resourceAttributes(Resource resource){return resource.getResourceAttributes(); }
}



import java.util.ArrayList;
import java.util.Random;

public class EcoModel implements IEcoModel, Runnable{
    private Random random = new Random();
    private ArrayList<Animal> animals = new ArrayList<>();
    private final ArrayList<Resource> resources = new ArrayList<>();
    private final int width, height;
    private ArrayList<Animal> newAnimals = new ArrayList<>();
    private final Thread ecoSimulationThread = new Thread(this);
    private boolean runSim, inPerformance;
    private int simulationSpeed = 10, animalId;


    public EcoModel(int width, int height){
        this.width = width;
        this.height = height;
    }

    @Override
    public void startNewSim(int initialBunnies, int initialFoxes, int initialWaterSources, int initialGrassSources){
        for(int i = 0; i < initialBunnies; i++) {
            animals.add(new Bunny(animalId, 100, random.nextInt(10, width - 10), random.nextInt(10, height - 10)));
            animalId++;
        }
        for(int i = 0; i < initialFoxes; i++) {
            animals.add(new Fox(animalId, 150, random.nextInt(10, width - 10), random.nextInt(10, height - 10)));
            animalId++;
        }

        for(int i = 0; i < initialWaterSources; i++) resources.add(new Resource(true, width, height));
        for(int i = 0; i < initialGrassSources; i++) resources.add(new Resource(false, width, height));
    }


    @Override
    public void clearOldSim() {
        boolean cleared = false;
        while (!cleared){
            if(!inPerformance){
                animals.clear();
                resources.clear();
                newAnimals.clear();
                runSim = false;
                simulationSpeed = 10;
                cleared = true;
            }
        }
    }

    private void nextPerformance(){
        if(runSim){

            // boolean to ensure the simulation is not running while trying to modify any lists, to prevent "ConcurrentModificationException"
            inPerformance = true;

            // all animals flagged as dead will be removed
            animals.removeIf(animal -> animal.getAnimalAttributes().dead());

            // all animals will be added which were received in the previous iteration of the simulation loop
            animals.addAll(newAnimals);
            newAnimals.clear();


            for(Animal animal : animals){
                // to ensure that dead animals will be skipped
                if(animal.getAnimalAttributes().dead())continue;

                animal.movement();
                animal.wallCollision(width, height);
                animal.stopUsingResource();

                if (animal.getAnimalAttributes().pregnant()) addNewAnimal(animal);


                resources.forEach(resource -> { handleStates(animal, resource);
                                                resource.regenerate();
                                                resource.usage();
                });

                if(animal instanceof Bunny bunny){
                    bunny.randomBunnyMovement(0.002);

                    animals.forEach(other -> {  bunny.dodgeFox(other);
                                                bunny.handleMating(other);
                                                bunny.stopMating(bunny.getAnimalAttributes().usingThisMate());
                    });
                }

                if(animal instanceof Fox fox){
                    fox.stopHunting();
                    animals.forEach(other ->{ if(other instanceof Bunny bunny && fox.getAnimalAttributes().state() == AnimalState.HUNGRY) fox.handleHunger(bunny);
                                              fox.handleMating(other);
                                              fox.stopMating(fox.getAnimalAttributes().usingThisMate());
                    });
                }
            }
            // let other functions know the iteration is done and modifications can happen now, to prevent "ConcurrentModificationException"
            inPerformance = false;
        }
    }

    @Override
    public void run() {
        runSim = true;

        while (true){
            nextPerformance();
            try {Thread.sleep(simulationSpeed);}
            catch (InterruptedException e) {System.err.println("A thread is currently running" + e.getMessage());}
        }
    }


    //getter for tests
    boolean isRunSim(){return this.runSim; }
    //getter for tests
    int getSimulationSpeed(){return this.simulationSpeed;}

    @Override public boolean isEcoSimulationThreadAlive() {return ecoSimulationThread.isAlive();}
    @Override public void startEcoSimulationThread() {if (!ecoSimulationThread.isAlive()) ecoSimulationThread.start();}
    @Override public void playEcoSimulationThread(boolean runSim) { if (ecoSimulationThread.isAlive()) this.runSim = runSim;}
    @Override public void setSimulationSpeed(int simulationSpeed){ this.simulationSpeed = simulationSpeed; }


    @Override public int getAnimalCountOf(boolean getHerbivore) { return getHerbivore ? getAnimalList().stream().filter(Animal :: isHerbivore).toList().size() : getAnimalList().stream().filter(animal -> !animal.isHerbivore()).toList().size();}


    private void addNewAnimal(Animal mother) {
        if(mother instanceof Bunny) {
            newAnimals.add(new Bunny(animalId, 100, mother.getAnimalAttributes().x(), mother.getAnimalAttributes().y()));
            animalId++;
        }
        else if(mother instanceof Fox) {
            newAnimals.add(new Fox(animalId, 150, mother.getAnimalAttributes().x(), mother.getAnimalAttributes().y()));
            animalId++;
        }
        mother.setPregnant(false);
    }


    private void handleStates(Animal animal, Resource resource){
        if(animal.getAnimalAttributes().state() == AnimalState.HUNGRY && animal instanceof Bunny bunny) bunny.handleHunger(resource);
        else if(animal.getAnimalAttributes().state() == AnimalState.THIRSTY) animal.handleThirst(resource);
        animal.decideState();
    }


    @Override public ArrayList<Resource> getResourceList() {return new ArrayList<>(this.resources);}

    @Override public ArrayList<Animal> getAnimalList() {return new ArrayList<>(this.animals);}

    @Override public int getWidth() {return this.width;}

    @Override public int getHeight() {return this.height;}
}

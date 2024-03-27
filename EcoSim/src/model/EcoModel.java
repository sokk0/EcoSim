package model;

import java.util.ArrayList;
import java.util.Random;

/**
 * Class that serves as the central hub connecting all components within the {@link model} package.
 * It simulates an ecosystem with animals and resources, enabling interactions among them.
 * Additionally, it provides methods to control the simulation, adjust simulation speed,
 * and retrieve information about the state of the ecosystem.
 * The simulation works as a separate thread, making continuous updates, adjusting speed, pausing, resuming,
 * and letting the main thread focus on the UI.
 * <p>
 * Example usage:
 * <pre>{@code
 * // 1. Create a new model object for a 1000x1000 ecosystem.
 * EcoModel model = new EcoModel(1000,1000);
 *
 * // 2. Initialize the simulation with a specified number of bunnies, foxes, water sources and grass sources.
 * model.startNewSim(25, 5, 100, 100);
 *
 * // 3. Start a new thread for the simulation.
 * model.startEcoSimulationThread();
 *
 * // Pause the simulation.
 * model.playEcoSimulationThread(false);
 *
 * // Adjust the simulation speed.
 * model.setSimulationSpeed(1);
 *
 * // Play the simulation.
 * model.playEcoSimulationThread(true);
 *
 * // Get the current count of herbivores (true) and carnivores (false).
 * model.getAnimalCountOf(true);
 * model.getAnimalCountOf(false);
 * }</pre>
 * @author Sleman Kakar
 */
public class EcoModel implements IEcoModel, Runnable{
    private Random random = new Random();
    private ArrayList<Animal> animals = new ArrayList<>();
    private final ArrayList<Resource> resources = new ArrayList<>();
    private final int width, height;
    private ArrayList<Animal> newAnimals = new ArrayList<>();
    private final Thread ecoSimulationThread = new Thread(this);
    private boolean runSim, inPerformance;
    private int simulationSpeed = 10, animalId;

    /**
     * Constructs a new EcoModel with the specified width and height for the ecosystem.
     * @param width The width of the ecosystem.
     * @param height The height of the ecosystem.
     */
    public EcoModel(int width, int height){
        this.width = width;
        this.height = height;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
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

    /**
     * (private) This method invokes almost all the methods provided by the {@link model} package, updating lists, states of specific objects and letting objects interact with each other.
     * <p>
     * Note: This method is the "main" simulation loop it should be called iteratively to ensure the progression of the simulation.
     */
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

    /**
     * {@inheritDoc}
     * <p>
     * Note: This thread should invoke a method which, goes through every action animals and resources can make to iteratively work through the simulation (e.g. {@link #nextPerformance()})
     */
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

    /**
     * {@inheritDoc}
     */
    @Override public boolean isEcoSimulationThreadAlive() {return ecoSimulationThread.isAlive();}
    /**
     * {@inheritDoc}
     */
    @Override public void startEcoSimulationThread() {if (!ecoSimulationThread.isAlive()) ecoSimulationThread.start();}
    /**
     * {@inheritDoc}
     */
    @Override public void playEcoSimulationThread(boolean runSim) { if (ecoSimulationThread.isAlive()) this.runSim = runSim;}
    /**
     * {@inheritDoc}
     */
    @Override public void setSimulationSpeed(int simulationSpeed){ this.simulationSpeed = simulationSpeed; }

    /**
     * {@inheritDoc}
     */
    @Override public int getAnimalCountOf(boolean getHerbivore) { return getHerbivore ? getAnimalList().stream().filter(Animal :: isHerbivore).toList().size() : getAnimalList().stream().filter(animal -> !animal.isHerbivore()).toList().size();}

    /**
     * (private) Checks if passed Animals is an instanceof Bunny or Fox, based on that it adds a new animal to {@link #newAnimals}.
     * <p>
     * Note: This method should only be called after mating was successful, which is done by checking the pregnant flag for an animal.
     * @param mother The other animal.
     */
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

    /**
     * (private) This method invokes other methods, based on the animals state, which handle hunger and thirst.
     * @param animal The animal whose state should be handled.
     * @param resource The resource which is used for handling the animals hunger/thirst.
     */
    private void handleStates(Animal animal, Resource resource){
        if(animal.getAnimalAttributes().state() == AnimalState.HUNGRY && animal instanceof Bunny bunny) bunny.handleHunger(resource);
        else if(animal.getAnimalAttributes().state() == AnimalState.THIRSTY) animal.handleThirst(resource);
        animal.decideState();
    }

    /**
     * {@inheritDoc}
     */
    //https://stackoverflow.com/questions/33060592/getters-and-setters-for-arraylists-in-java
    @Override public ArrayList<Resource> getResourceList() {return new ArrayList<>(this.resources);}
    /**
     * {@inheritDoc}
     */
    @Override public ArrayList<Animal> getAnimalList() {return new ArrayList<>(this.animals);}
    /**
     * {@inheritDoc}
     */
    @Override public int getWidth() {return this.width;}
    /**
     * {@inheritDoc}
     */
    @Override public int getHeight() {return this.height;}
}

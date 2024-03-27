package model;

import java.util.ArrayList;

/**
 * Interface defining the contact point between controller and model in the MVC design pattern
 * It provides methods for starting and controlling the simulation, adjusting simulation speed,
 * and retrieving information about animals and resources in the ecosystem.
 *
 * @author Sleman Kakar
 */
public interface IEcoModel {

    /**
     * Starts a new simulation with the specified parameters.
     * <p>
     * This method should be called first to initialize the simulation.
     * @param initialFoxes Initial count of foxes in the simulation.
     * @param initialBunnies Initial count of bunnies in the simulation.
     * @param initialWaterSources Initial count of water sources in the simulation.
     * @param initialGrassSources Initial count of grass sources in the simulation.
     */
    void startNewSim(int initialFoxes, int initialBunnies, int initialWaterSources, int initialGrassSources);

    /**
     * Clears the old simulation state, furthermore it sets everything to the initial base values.
     */
    void clearOldSim();

    /**
     * Checks if the simulation thread is currently alive and running.
     * @return True if the simulation thread is alive, false otherwise.
     */
    boolean isEcoSimulationThreadAlive();

    /**
     * Initial start of the simulation thread.
     */
    void startEcoSimulationThread();

    /**
     * Pauses or resumes the simulation thread based on parameter.
     *
     * @param activeThread True to resume the simulation thread, false to pause it.
     */
    void playEcoSimulationThread(boolean activeThread);

    /**
     * Sets the simulation speed to the specified value.
     *
     * @param simulationSpeed The speed at which the simulation (thread) should run.
     */
    void setSimulationSpeed(int simulationSpeed);

    /**
     * Gets the count of herbivores or carnivores currently in the simulation.
     * @param getHerbivore getHerbivore True to get the count of herbivores, false for carnivores.
     * @return The count of animals based on the specified herbivore status.
     */
    int getAnimalCountOf(boolean getHerbivore);

    /**
     * Gets the width of the ecosystem.
     *
     * @return The width of the ecosystem.
     */
    int getWidth();

    /**
     * Gets the height of the ecosystem.
     *
     * @return The height of the ecosystem.
     */
    int getHeight();

    /**
     * Gets the list of resources in the ecosystem.
     *
     * @return ArrayList of Resource objects representing the resources in the ecosystem.
     */
    ArrayList<Resource> getResourceList();

    /**
     * Gets the list of animals in the ecosystem.
     *
     * @return ArrayList of Animal objects representing the animals in the ecosystem.
     */
    ArrayList<Animal> getAnimalList();

    /**
     * Default method to get the attributes of an animal.
     *
     * @param animal The animal from whom to get the attributes from.
     * @return The attributes of the specified animal as an {@link AnimalAttributes} record.
     */
    default AnimalAttributes animalAttributes(Animal animal){return animal.getAnimalAttributes(); }

    /**
     * Default method to get the attributes of a resource.
     *
     * @param resource The resource from which to get the attributes from.
     * @return The attributes of the specified resource as an {@link ResourceAttributes} record.
     */
    default ResourceAttributes resourceAttributes(Resource resource){return resource.getResourceAttributes(); }
}
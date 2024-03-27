package controller;

/**
 * Interface defining the contact point between controller and view in the MVC design pattern.
 * It provides methods for managing the simulation, handling user input and deciding what should be drawn based on the program or simulation state.
 *
 * @author Sleman Kakar
 */
public interface IEcoController {

    /**
     * Provides the view with information about the size of the simulation, based on that the display size is being changed.
     */
    void setSize();

    /**
     * Decides what should be drawn based on the current state of the program.
     */
    void nextFrame();

    /**
     * Handles the user's mouse input.
     * @param x The passed x position, which needs to be handled.
     * @param y The passed y position, which needs to be handled.
     */
    void handleMouseInput(float x, float y);

    /**
     * Changes the program state based on the current state when the method is called.
     */
    void changeProgramState();

    /**
     * Provides the model with information about the desired initial values for bunnies, foxes, grass sources and water sources.
     *
     * @param initialBunnies Initial count of bunnies in the simulation.
     * @param initialFoxes Initial count of foxes in the simulation.
     * @param initialWaterSources Initial count of water sources in the simulation.
     * @param initialGrassSources Initial count of grass sources in the simulation.
     */
    void setInitialValues(int initialBunnies, int initialFoxes, int initialWaterSources, int initialGrassSources);

    /**
     * Informs the model that the simulation should be paused.
     */
    void pauseSimulation();
    /**
     * Informs the model that the simulation should be played.
     */
    void playSimulation();

    /**
     * Informs the model that the simulation speed needs to change to the specified speed in milliseconds.
     * @param speedInMillis The simulation speed in milliseconds.
     */
    void setSimulationSpeed(int speedInMillis);

    /**
     * Changes the program state to the start menu and informs the model that the simulation should be cleared.
     */
    void changeToStart();

    /**
     * Changes the program state to the info menu.
     */
    void changeToInfo();
}
package view;
/**
 * Interface defining the contact point between view and controller in the MVC design pattern.
 * It provides methods for displaying parts of the program, so how the program should look.
 *
 * @author Sleman Kakar
 */
public interface IEcoView {
    /**
     * For getting the size passed.
     */
    void passSize(int width, int height);

    /**
     * Defines how a bunny is being drawn on the display based on the data input.
     * @param x defines the x-position of the drawn image.
     * @param y defines the y-position of the drawn image.
     * @param direction defines the direction the image should be facing towards (1 for right or 0 for left, the parameter shouldn't be chosen higher or lower).
     * @param inMotion defines if the drawn image should be animated or not.
     */
    void drawBunny(float x, float y, int direction, boolean inMotion);

    /**
     * Defines how a fox is being drawn on the display based on the data input.
     * @param x defines the x-position of the drawn image.
     * @param y defines the y-position of the drawn image.
     * @param direction defines the direction the image should be facing towards (1 for right or 0 for left, the parameter shouldn't be chosen higher or lower).
     * @param inMotion defines if the drawn image should be animated or not.
     */
    void drawFox(float x, float y, int direction, boolean inMotion);

    /**
     * Defines how fox parameters are being drawn on the display based on the data input.
     * @param hungerBar defines how much hunger the image should display.
     * @param thirstBar defines how much thirst the image should display.
     */
    void drawFoxParameters(int hungerBar, int thirstBar);

    /**
     * Defines how bunny parameters are being drawn on the display based on the data input.
     * @param hungerBar defines how much hunger the image should display.
     * @param thirstBar defines how much thirst the image should display.
     */
    void drawBunnyParameters(int hungerBar, int thirstBar);

    /**
     * Defines how a selected animal is being drawn on the display based on the data input.
     * @param x defines the x-position of the drawn image.
     * @param y defines the y-position of the drawn image.
     * @param size defines the size of the marking.
     */
    void drawSelectMarking(float x, float y, float size);

    /**
     * Defines how a grass resource is being drawn on the display based on the data input.
     * @param x defines the x-position of the drawn image.
     * @param y defines the y-position of the drawn image.
     * @param remainingPercentage defines the alpha value of the drawn image.
     */
    void drawGrass(float x, float y, double remainingPercentage);

    /**
     * Defines how a water resource is being drawn on the display based on the data input.
     * @param x defines the x-position of the drawn rectangle.
     * @param y defines the y-position of the drawn rectangle.
     * @param remainingPercentage defines the alpha value of the drawn rectangle.
     */
    void drawWater(float x, float y, double remainingPercentage);

    /**
     * Defines how the start menu should look. Including a background, welcome text and buttons for further action.
     */
    void drawStart();
    /**
     * Defines how the settings menu should look. Including a background, sliders (based on their values animal faces and resources are being drawn row by row) and buttons for further action.
     */
    void drawSettingsMenu();
    /**
     * Defines how the running simulation should look. Including text to overlay over the running simulation, so bunnies won't draw over them and buttons for further action.
     */
    void drawRunningSimulation();

    /**
     * Defines how the info screen should look. Including text to overlay over the running simulation, so bunnies won't draw over them and buttons for further action.
     */
    void drawInfoScreen();

    /**
     * Simple text display for the controller to pass information about current count of animals
     * @param foxCount The current count of foxes.
     * @param bunnyCount The current count of bunnies.
     */
    void drawAnimalCounter(int foxCount, int bunnyCount);

    /**
     * Simple infotext message which can be shown by the controller in certain situations.
     */
    void popUpMessage();
}

import controller.EcoController;
import model.EcoModel;
import view.EcoView;
import processing.core.PApplet;

/**
 * Main class that serves as starting point of the Ecosystem Simulation program, connecting the model,
 * view and controller elements conforming to the MVC (Model-View-Controller) design pattern.
 * <pre>{@code
 * // 1. calling the main method.
 * public static void main(String[]args){
 *      final int width = 1920;
 *      final int height = 1080;
 *
 * // 2. Create new model, view and controller instances.
 *      var view = new EcoView();
 *      var controller = new EcoController();
 *      var model = new EcoModel(width, height); // See the EcoModel constructor documentation for specific implementation information.
 *
 * // 3. Connect MVC elements. conforming to the MVC design pattern.
 *      view.setController(controller);
 *      controller.setView(view);
 *      controller.setModel(model);
 *
 * // 4. Start the GUI of your choosing this example will use processing.
 *      PApplet.runSketch(new String[]{"EcoView"},view);
 * }
 * }</pre>
 */
public class Main{

    /**
     * Main method that serves as starting point of the program, setting the program size,
     * initializing MVC (Model-View-Controller) elements, connecting them in accordance with the design pattern
     * and starting the GUI (view) in this case the processing-sketch.
     */
    public static void main(String[]args){
        final int width = 1920;
        final int height = 1000;

        var view = new EcoView();
        var controller = new EcoController();
        var model = new EcoModel(width, height);

        view.setController(controller);
        controller.setView(view);
        controller.setModel(model);

        PApplet.runSketch(new String[]{"EcoView"},view);
    }
}
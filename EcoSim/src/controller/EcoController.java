package controller;


import model.IEcoModel;
import view.IEcoView;

/**
 * Class that serves as the contact point between the model and the view. It accepts input which it converts to data for the model to process,
 * and it handles the data given by the model to display the correct information with the help of the view.
 * <p>
 * Example usage (restricted to usage in the view, since this example uses processing, actually the controller methods shouldn't be called elsewhere,
 * except the UI is restricting you to use the controller in the view, like processing does):
 * <pre>{@code
 * // 1. Initialize a controller instance inside the view.
 * // (in actual usage it should be initialized through its interface in the main method and used through that, conforming to the MVC design pattern.)
 * var controller = new EcoController();
 * // 2. Now the methods of the EcoController class can be used to, handle the information of the Logic and process it to data for the view to display.
 * public void draw(){
 *  background(75,139,59);
 *  controller.nextFrame();
 * }
 * }</pre>
 * @author Sleman Kakar
 */
public class EcoController implements IEcoController{
    private IEcoView view;
    private IEcoModel model;
    private ProgramState state = ProgramState.START;
    private int initialBunnies, initialFoxes, initialWaterSources, initialGrassSources;
    private final int RIGHT = 1, LEFT = 0;
    private int savedId = -1;
    private Float clickX, clickY;
    private boolean paused, clickedAnimalOnce;
    private int counterForPopUp;


    /**
     * Sets the view element for the MVC design pattern, this method should be called at the start of the program.
     * @param view The view element to be set.
     */
    public void setView(IEcoView view) {
        this.view = view;
    }

    /**
     * Sets the model element for the MVC design pattern, this method should be called at the start of the program.
     * @param model The model element to be set.
     */
    public void setModel(IEcoModel model) {
        this.model = model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSize() {
        view.passSize(model.getWidth(), model.getHeight());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        switch(state){
            case START -> view.drawStart();
            case SETTINGS -> view.drawSettingsMenu();
            case INFORMATION -> view.drawInfoScreen();
            case RUNNING -> {
                checkClickOnAnimal();

                handleResources();
                handleBunny();
                handleFox();

                handleAnimalParameterInfo();

                view.drawRunningSimulation();
                view.drawAnimalCounter(model.getAnimalCountOf(false), model.getAnimalCountOf(true));
                if(!clickedAnimalOnce){
                    view.popUpMessage();
                    counterForPopUp++;
                }
                if(counterForPopUp > 250) clickedAnimalOnce = true;

            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeProgramState() {
        switch(state){
            case START -> state = ProgramState.SETTINGS;
            case SETTINGS -> {
                model.startNewSim(initialBunnies, initialFoxes, initialWaterSources, initialGrassSources);
                state = ProgramState.RUNNING;
                model.startEcoSimulationThread();
                playSimulation();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeToStart(){
        pauseSimulation();
        model.clearOldSim();
        state = ProgramState.START;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeToInfo(){
        state = ProgramState.INFORMATION;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setInitialValues(int initialBunnies, int initialFoxes, int initialWaterSources, int initialGrassSources) {
        this.initialBunnies = initialBunnies;
        this.initialFoxes = initialFoxes;
        this.initialWaterSources = initialWaterSources;
        this.initialGrassSources = initialGrassSources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void pauseSimulation() {
        paused = true;
        model.playEcoSimulationThread(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void playSimulation() {
        paused = false;
        model.playEcoSimulationThread(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSimulationSpeed(int speedInMillis) { model.setSimulationSpeed(speedInMillis);}

    /**
     * {@inheritDoc}
     * <p>
     * Note: the x and y position will be saved to {@link #clickX} and {@link #clickY} and can be processed, to ensure
     * that users can't spam the program with mouse clicks to generate an enormous amount of method calls.
     */
    @Override
    public void handleMouseInput(float x, float y){
        if(state == ProgramState.RUNNING){
            this.clickX = x;
            this.clickY = y;
        }
    }

    /**
     * (private) Checks if a click has been saved in {@link #clickX} and {@link #clickY}, if so all the animals positions will be compared to that position,
     * and if the position of the click matches the position of any animal its id will be saved to {@link #savedId}, the temporarily saved click position will be overwritten with null and the method will be left with return.
     * If the method wasn't left with return, that means no animal was found and the click was misplaced by the user and the temporarily saved click position will be overwritten with null.
     */
    private void checkClickOnAnimal(){
        if(clickX != null && clickY != null){

            for(int i = 0; i < model.getAnimalList().size(); i++){
                var animalGet = model.animalAttributes(model.getAnimalList().get(i));
                var distanceToAnimal = Math.hypot(animalGet.x() - clickX, animalGet.y() - clickY);

                if(distanceToAnimal <= 20){
                    savedId = animalGet.id();
                    clickedAnimalOnce = true;
                    clickX = null;
                    clickY = null;
                    return;
                }
            }
            // if no position has been found to avoid animals walking over the saved position and getting selected.

            clickX = null;
            clickY = null;
        }
    }

    /**
     * (private) Checks if the {@link #savedId} is currently in the animal list, if so it will invoke the view method to draw the parameters of an animal and to draw the marking around the selected animal.
     */
    private void handleAnimalParameterInfo(){
        // base value for not drawing
        if(savedId != -1) {
            boolean idIsContainedInList = false;

            for (int i = 0; i < model.getAnimalList().size(); i++) {
                var animalGet = model.animalAttributes(model.getAnimalList().get(i));

                if(savedId == animalGet.id()){
                    if(animalGet.herbivore()){
                        view.drawBunnyParameters((int)animalGet.hunger(), (int)animalGet.thirst());
                        view.drawSelectMarking(animalGet.x(), animalGet.y(),40);
                    }else{
                        view.drawFoxParameters((int)animalGet.hunger(), (int)animalGet.thirst());
                        view.drawSelectMarking(animalGet.x(), animalGet.y(),50);
                    }
                    // to say the id was found
                    idIsContainedInList = true;
                }
            }
            // basically says stop drawing for the first line of this method
            if(!idIsContainedInList) savedId = -1;
        }
    }

    /**
     * (private) Converts the model data to drawing information for the view to draw the fox.
     */
    private void handleFox(){
        for(int i = 0; i < model.getAnimalList().size(); i++) {
            if (!model.animalAttributes(model.getAnimalList().get(i)).herbivore()) {
                var foxGet = model.animalAttributes(model.getAnimalList().get(i));

                int currentDirection = 0;
                boolean inMotion = true;

                if(foxGet.vx() > 0){
                    inMotion = true;
                    currentDirection = RIGHT;
                }else if(foxGet.vx() < 0){
                    inMotion = true;
                    currentDirection = LEFT;
                }

                if(foxGet.vy() < 0 || foxGet.vy() > 0){
                    inMotion = true;
                }

                if(paused || (foxGet.vx() == 0 && foxGet.vy() == 0)) inMotion = false;

                view.drawFox(foxGet.x(), foxGet.y(), currentDirection, inMotion);
            }
        }
    }

    /**
     * (private) Converts the model data to drawing information for the view to draw the bunny.
     */
    private void handleBunny(){
        for(int i = 0; i < model.getAnimalList().size(); i++) {
            if (model.animalAttributes(model.getAnimalList().get(i)).herbivore()) {
                var bunnyGet = model.animalAttributes(model.getAnimalList().get(i));

                int currentDirection = 1;
                boolean inMotion = true;

                if(bunnyGet.vx() > 0){
                    inMotion = true;
                    currentDirection = RIGHT;
                }else if(bunnyGet.vx() < 0){
                    inMotion = true;
                    currentDirection = LEFT;
                }

                if(paused || (bunnyGet.vx() == 0 && bunnyGet.vy() == 0) || bunnyGet.thinking()) inMotion = false;

                view.drawBunny(bunnyGet.x(), bunnyGet.y(), currentDirection, inMotion);
            }
        }
    }

    /**
     * (private) Converts the model data to drawing information for the view to draw the correct resource.
     */
    private void handleResources(){
        for(int i = 0; i < model.getResourceList().size(); i++){
            var fluid = model.resourceAttributes(model.getResourceList().get(i)).resourceTypeWater();


            if(!fluid){
                var grassGet = model.resourceAttributes(model.getResourceList().get(i));
                view.drawGrass(grassGet.x(), grassGet.y(), grassGet.remainingPercentage() * 2.55);
            }else{
                var waterGet = model.resourceAttributes(model.getResourceList().get(i));
                view.drawWater(waterGet.x(), waterGet.y(), waterGet.remainingPercentage() * 2.55);
            }
        }
    }
}
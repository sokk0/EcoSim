package view;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import controller.IEcoController;
import controlP5.*;
import processing.event.MouseEvent;

import static controlP5.ControlP5Constants.ACTION_RELEASE;

/**
 * Class that serves as the GUI (graphical user interface), it decides how the program should look.
 * It gives the controller in the MVC design pattern methods with parameters to fill with data.
 * In this case <a href="https://processing.org/">processing</a> is being used.
 * <p>
 * Example usage:
 * <pre>{@code
 * // 1. Initialize a view instance inside the controller.
 * // (in actual usage it should be initialized through its interface in the main method and used through that, conforming to the MVC design pattern.)
 * var view = new EcoView();
 * // 2. Now the methods of the EcoView class can be used to, display the information of the Logic.
 * void handleBunny(){ // The actual implementation of this method should look different, because not every parameter is being handled correctly.
 *      for(int i = 0; i < model.getAnimalList().size(); i++) {
 *          if (model.animalAttributes(model.getAnimalList().get(i)).herbivore()) {
 *              var bunnyGet = model.animalAttributes(model.getAnimalList().get(i));
 *
 *              // Draws a bunny at the specified position and with the given direction and motion state.
 *              // See the documentation of drawBunny(...) for concrete implementation information.
 *              view.drawBunny(bunnyGet.x(), bunnyGet.y(), 1, true);
 *          }
 *      }
 * }
 * }</pre>
 * @author Sleman Kakar
 */
public class EcoView extends PApplet implements IEcoView {
    private IEcoController controller;
    private double currentFrame;
    private PImage grassSmall, foxMovementSpriteSheet, foxStandingSpriteSheet, bunnyMovementSpriteSheet, backgroundImage, infoImage;
    private PImage foxIcon, bunnyIcon, grassIcon, thirstIcon, hungerIcon;
    private PImage [][] bunnyMovement, foxMovement;
    private PFont mcFont;
    private ControlP5 cp5;
    private Button setupButton, startButton, readMeButton;
    private Button pauseButton, playButton, homeButton;
    private Slider speedSlider;
    private Slider initialBunniesSlider, initialFoxesSlider, initialWaterSourcesSlider, initialGrassSourcesSlider;


    /**
     * Sets the controller element for the MVC design pattern, this method should be called at the start of the program.
     * @param controller The controller element to be set.
     */
    public void setController(IEcoController controller) {
        this.controller = controller;
    }


    /**
     * The settings method from processing is called once on start of the program, it is used to set initial settings of the sketch,
     * such as the window size.
     */
    public void settings(){
        controller.setSize();
        setSize(width, height);
        setImages();
    }

    /**
     * The setup method from processing is called once on start of the program, it is used for initialization, in this example it is used to,
     * set up the font, load images and configure the buttons and slider from the ControlP5 library.
     */
    public void setup(){
        mcFont = createFont("images/font/Minecraft.ttf", 32);
        textFont(mcFont);
        cp5 = new ControlP5(this);
        setupCp5();
    }

    /**
     * The draw method from processing is iteratively, it is used to actually "draw", in this example a background is being drawn
     * to reset previous drawings, furthermore the draw "logic" is being outsourced to the controller to handle
     */
    public void draw(){
        background(75,139,59);
        controller.nextFrame();
    }


    /**
     * (private) Loads images, resizes them and cuts up sprite sheets.
     */
    private void setImages(){
        backgroundImage = loadImage("images/background/background.jpg");
        backgroundImage.resize(width, height);

        infoImage = loadImage("images/background/infoTab.png");

        grassSmall = loadImage("images/resource/grassSprite.png");
        grassSmall.resize(27,0);

        grassIcon = loadImage("images/resource/grassSprite.png");
        grassIcon.resize(0,height / 30);


        foxIcon = loadImage("images/icons/foxIcon.png");
        foxIcon.resize(0,height / 30);

        bunnyIcon = loadImage("images/icons/bunnyIcon.png");
        bunnyIcon.resize(0,height / 30);

        thirstIcon = loadImage("images/icons/thirstIcon.png");
        thirstIcon.resize(0, 15);

        hungerIcon = loadImage("images/icons/hungerIcon.png");
        hungerIcon.resize(0, 15);

        bunnyMovement = new PImage[2][6];
        bunnyMovementSpriteSheet = loadImage("bunny/bunnySpriteSheetAlpha.png");

        for (int i = 0; i < 6; i++){
            bunnyMovement[0][i] = bunnyMovementSpriteSheet.get(i * 30 + 1 + i, 1, 30, 28);
            bunnyMovement[1][i] = bunnyMovementSpriteSheet.get(i * 30 + 1 + i, 30, 30, 28);
        }

        foxMovement = new PImage[2][9];
        foxMovementSpriteSheet = loadImage("images/fox/foxSpriteSheetWalkingAlpha.png");
        foxStandingSpriteSheet = loadImage("images/fox/foxSpriteSheetStandingAlpha.png");


        foxMovement[0][0] = foxStandingSpriteSheet.get(1,1, 43, 35);
        foxMovement[1][0] = foxStandingSpriteSheet.get(1,36, 43, 35);

        for(int i = 1; i < 9; i++){
            foxMovement[0][i] = foxMovementSpriteSheet.get(i * 46 + 1 + i, 1, 46, 35);
            foxMovement[1][i] = foxMovementSpriteSheet.get(i * 46 + 1 + i, 36, 46, 35);
        }
    }

    /**
     * (private) Sets up all UI-Elements used in this view (as the name suggests from the controlP5 framework).
     */
    private void setupCp5(){
        int buttonWidth = width / 5;
        int buttonHeight = height / 19;
        int sliderWidth = width / 6;
        int sliderHeight = height / 30;

        setupButton = cp5.addButton("Setup Simulation");
        setupButton.setPosition(width / 2.5f, height / 4)
                .setSize(buttonWidth, buttonHeight)
                .setColorBackground(color(80))
                .setColorForeground(color(120))
                .setColorActive(color(200))
                .getCaptionLabel().setFont(mcFont);

        setupButton.addListenerFor(ACTION_RELEASE, callbackEvent -> {
            // hides all other UI-Tools
            hideCp5();
            // changes state to settings screen
            controller.changeProgramState();
        });


        readMeButton = cp5.addButton("Read Me!");
        readMeButton.setPosition(setupButton.getPosition()[0], setupButton.getPosition()[1] + buttonHeight + 30)
                .setSize(buttonWidth, buttonHeight)
                .setColorBackground(color(80))
                .setColorForeground(color(120))
                .setColorActive(color(200))
                .getCaptionLabel().setFont(mcFont);
        readMeButton.addListenerFor(ACTION_RELEASE, callbackEvent -> {
            // hides all other UI-Tools
            hideCp5();
            // changes state to info screen
            controller.changeToInfo();
        });

        //Sliders to set initial values
        initialBunniesSlider = cp5.addSlider("initialBunniesSlider");
        initialBunniesSlider.setPosition(width / 10f, height / 4.3f)
                .setSize(sliderWidth, sliderHeight)
                .setLabel("")
                .setRange(0, 100)
                .setValue(30)
                .setColorBackground(color(80))
                .setColorForeground(color(160))
                .setColorActive(color(200))
                .setNumberOfTickMarks(101)
                .snapToTickMarks(true)
                .showTickMarks(false)
                .setColorValue(color(255, 0))
                .getCaptionLabel().setFont(mcFont);


        initialFoxesSlider = cp5.addSlider("initialFoxesSlider");
        initialFoxesSlider.setPosition(width / 10f + sliderWidth + 65, height / 4.3f)
                .setSize(sliderWidth, sliderHeight)
                .setLabel("")
                .setRange(0, 100)
                .setValue(5)
                .setColorBackground(color(80))
                .setColorForeground(color(160))
                .setColorActive(color(200))
                .setNumberOfTickMarks(101)
                .snapToTickMarks(true)
                .showTickMarks(false)
                .setColorValue(color(255, 0))
                .getCaptionLabel().setFont(mcFont);


        initialGrassSourcesSlider = cp5.addSlider("initialGrassSourcesSlider");
        initialGrassSourcesSlider.setPosition(initialFoxesSlider.getPosition()[0] + sliderWidth + 150, initialFoxesSlider.getPosition()[1])
                .setSize(sliderWidth, sliderHeight)
                .setLabel("")
                .setRange(0, 100)
                .setValue(40)
                .setColorBackground(color(80))
                .setColorForeground(color(160))
                .setColorActive(color(200))
                .setNumberOfTickMarks(101)
                .snapToTickMarks(true)
                .showTickMarks(false)
                .setColorValue(color(255, 0))
                .getCaptionLabel().setFont(mcFont);


        initialWaterSourcesSlider = cp5.addSlider("initialWaterSourcesSlider");
        initialWaterSourcesSlider.setPosition(initialGrassSourcesSlider.getPosition()[0] + sliderWidth + 65, initialGrassSourcesSlider.getPosition()[1])
                .setSize(sliderWidth, sliderHeight)
                .setLabel("")
                .setRange(0, 100)
                .setValue(45)
                .setColorBackground(color(80))
                .setColorForeground(color(160))
                .setColorActive(color(200))
                .setNumberOfTickMarks(101)
                .snapToTickMarks(true)
                .showTickMarks(false)
                .setColorValue(color(255, 0))
                .getCaptionLabel().setFont(mcFont);
        // making one listener for multiple sliders (see the "poke view" exercise)
        var initialSettingsCallback = new ControlListener(){
            @Override
            public void controlEvent(ControlEvent controlEvent) {
                controller.setInitialValues((int) initialBunniesSlider.getValue(),
                        (int) initialFoxesSlider.getValue(),
                        (int) initialWaterSourcesSlider.getValue(),
                        (int) initialGrassSourcesSlider.getValue()) ;

            }
        };
        initialBunniesSlider.addListener(initialSettingsCallback);
        initialFoxesSlider.addListener(initialSettingsCallback);
        initialWaterSourcesSlider.addListener(initialSettingsCallback);
        initialGrassSourcesSlider.addListener(initialSettingsCallback);

        initialSettingsCallback.controlEvent(null);


        startButton = cp5.addButton("startButton");
        startButton.setPosition(initialWaterSourcesSlider.getPosition()[0], height/1.2f)
                .setSize(sliderWidth, sliderHeight + 10)
                .setLabel("Start!")
                .setColorBackground(color(80))
                .setColorForeground(color(75,139,59))
                .setColorActive(color(95, 159, 79))
                .getCaptionLabel().setFont(mcFont);

        startButton.addListenerFor(ACTION_RELEASE, callbackEvent -> {
            // changes state to running screen
            controller.changeProgramState();

            // set initial colors of pause and play button
            playButton.setColorBackground(color(150, 80, 80))
                    .setColorForeground(color(150, 80, 80))
                    .setColorActive(color(150, 80, 80));

            pauseButton.setColorBackground(color(80))
                    .setColorForeground(color(120))
                    .setColorActive(color(200));

            hideCp5();
        });

        homeButton = cp5.addButton("homeButton");
        homeButton.setPosition(10, 10)
                .setSize(width/18, height/28)
                .setLabel("Home")
                .setColorBackground(color(80))
                .setColorForeground(color(120))
                .setColorActive(color(200))
                .getCaptionLabel().setFont(mcFont);

        homeButton.addListenerFor(ACTION_RELEASE, callbackEvent -> {
            hideCp5();
            // changes state to start screen
            controller.changeToStart();
        });




        pauseButton = cp5.addButton("pauseButton");
        pauseButton.setPosition(10, height - pauseButton.getHeight() * 3)
                .setSize(110, 50)
                .setLabel("pause")
                .setColorBackground(color(80))
                .setColorForeground(color(120))
                .setColorActive(color(200))
                .getCaptionLabel().setFont(mcFont);


        pauseButton.addListenerFor(ACTION_RELEASE, callbackEvent -> {

            controller.pauseSimulation();

            // let it seem like button cant be pressed and the other has to be pressed
            pauseButton.setColorBackground(color(150, 80, 80))
                    .setColorForeground(color(150, 80, 80))
                    .setColorActive(color(150, 80, 80));

            playButton.setColorBackground(color(80))
                    .setColorForeground(color(120))
                    .setColorActive(color(200));
        });


        playButton = cp5.addButton("playButton");
        playButton.setPosition(pauseButton.getPosition()[0] + pauseButton.getWidth() + 10, pauseButton.getPosition()[1])
                .setSize(110, 50)
                .setLabel("play")
                .setColorBackground(color(150, 80, 80))
                .setColorForeground(color(150, 80, 80))
                .setColorActive(color(150, 80, 80))
                .getCaptionLabel().setFont(mcFont);

        playButton.addListenerFor(ACTION_RELEASE, callbackEvent -> {

            controller.playSimulation();

            // let it seem like button cant be pressed and the other has to be pressed
            playButton.setColorBackground(color(150, 80, 80))
                    .setColorForeground(color(150, 80, 80))
                    .setColorActive(color(150, 80, 80));

            pauseButton.setColorBackground(color(80))
                    .setColorForeground(color(120))
                    .setColorActive(color(200));
        });

        speedSlider = cp5.addSlider("speedSlider");
        speedSlider.setPosition(playButton.getPosition()[0] + playButton.getWidth() + 20, playButton.getPosition()[1] + playButton.getHeight() / 2)
                .setSize(110, 20)
                .setLabel("")
                .setRange(10, 1)
                .setValue(10)
                .setColorBackground(color(80))
                .setColorForeground(color(160))
                .setColorActive(color(200))
                .setNumberOfTickMarks(10)
                .setColorValue(color(255, 0))
                .snapToTickMarks(true)
                .showTickMarks(false);

        speedSlider.addListener(controlEvent -> controller.setSimulationSpeed((int) speedSlider.getValue()));

        // initially hide all UI-elements
        hideCp5();
    }

    /**
     * (private) Hides all UI-Elements.
     */
    private void hideCp5(){
        setupButton.hide();
        startButton.hide();
        readMeButton.hide();
        pauseButton.hide();
        playButton.hide();
        homeButton.hide();
        speedSlider.hide();
        initialBunniesSlider.hide();
        initialFoxesSlider.hide();
        initialWaterSourcesSlider.hide();
        initialGrassSourcesSlider.hide();

        resetCp5Sliders();
    }
    /**
     * (private) Resets all UI-Element Sliders to their initial values.
     */
    private void resetCp5Sliders(){
        speedSlider.setValue(10);
        initialBunniesSlider.setValue(30);
        initialFoxesSlider.setValue(5);
        initialWaterSourcesSlider.setValue(45);
        initialGrassSourcesSlider.setValue(40);
    }
    /**
     * (private) Shows UI-Elements for the start screen.
     */
    private void showCp5StartScreen(){
        setupButton.show();
        readMeButton.show();
    }
    /**
     * (private) Shows UI-Elements for the settings screen.
     */
    private void showCp5SettingsScreen(){
        startButton.show();
        homeButton.show();

        initialBunniesSlider.show();
        initialFoxesSlider.show();
        initialWaterSourcesSlider.show();
        initialGrassSourcesSlider.show();
    }
    /**
     * (private) Shows UI-Elements for the running screen.
     */
    private void showCp5RunningScreen(){
        pauseButton.show();
        playButton.show();
        homeButton.show();
        speedSlider.show();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void drawStart() {
        showCp5StartScreen();

        background(backgroundImage);

        fill(255);
        textAlign(CENTER, CENTER);
        textSize(width / 25);


        text("ECOSYSTEM SIMULATION", width/2, height/7);
        noFill();

        stroke(0);
        strokeWeight(5);
        rect(setupButton.getPosition()[0], setupButton.getPosition()[1], setupButton.getWidth(), setupButton.getHeight());
        rect(readMeButton.getPosition()[0], readMeButton.getPosition()[1], readMeButton.getWidth(), readMeButton.getHeight());
        strokeWeight(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawSettingsMenu() {
        showCp5SettingsScreen();

        background(244, 169, 111);

        int sliderWidth = width / 6;
        int sliderHeight = height / 30;

        fill(255);
        textAlign(LEFT, BASELINE);
        textSize(40);
        text("Set Up the Initial Simulation State!", initialBunniesSlider.getPosition()[0], height / 7.2f);


        textSize(35);
        text("Bunnies: " + (int) initialBunniesSlider.getValue(), width / 10f, height / 4.3f - 10);
        text("Foxes: " + (int) initialFoxesSlider.getValue(), initialFoxesSlider.getPosition()[0], height / 4.3f - 10);
        text("Water Spots: " + (int) initialWaterSourcesSlider.getValue(), initialWaterSourcesSlider.getPosition()[0], height / 4.3f - 10);
        text("Grass Spots: " + (int) initialGrassSourcesSlider.getValue(), initialGrassSourcesSlider.getPosition()[0], height / 4.3f - 10);


        imageMode(CORNER);

        //calculation for how the image icons should appear in a row
        int counterOne = 0;
        for(int i = 0; i < initialBunniesSlider.getValue(); i++){
                    // start at slider pos                  // 9 icons can be placed next to each other     // with the icon width in between the next placement + padding
            float x = initialBunniesSlider.getPosition()[0] + (i % 9) * (bunnyIcon.width + 10);
            // difference here, the counter sees when 9 have been placed next to each other and then    // with the icon height in between the next placement + padding
            float y = initialBunniesSlider.getPosition()[1] + sliderHeight + 30 + counterOne * (bunnyIcon.height + 10);

            image(bunnyIcon, x, y - 10);
            if((i + 1) % 9 == 0) counterOne++;
        }

        int counterTwo = 0;
        for(int i = 0; i < (int)initialFoxesSlider.getValue(); i++){

            int x = (1920 / 10 + sliderWidth + 65) + (i % 7) * (foxIcon.width + 10);
            float y = initialFoxesSlider.getPosition()[1] + sliderHeight + 30 + counterTwo * (foxIcon.height + 10);

            image(foxIcon, x , y - 10);
            if((i + 1) % 7 == 0) counterTwo++;
        }

        int counterThree = 0;
        for(int i = 0; i < initialWaterSourcesSlider.getValue(); i++){

            float x = initialWaterSourcesSlider.getPosition()[0] + (i % 16) * 20;
            float y = initialWaterSourcesSlider.getPosition()[1] + sliderHeight + 30 + counterThree * 20;

            drawWater(x, y - 10, 255);
            if((i + 1) % 16 == 0) counterThree++;
        }

        int counterFour = 0;
        for(int i = 0; i < initialGrassSourcesSlider.getValue(); i++){

            float x = initialGrassSourcesSlider.getPosition()[0] + (i % 8) * grassIcon.width;
            float y = initialGrassSourcesSlider.getPosition()[1] + sliderHeight + 30 + counterFour * grassIcon.width;

            image(grassIcon, x, y - 10);
            if((i + 1) % 8 == 0) counterFour++;
        }
        noFill();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawRunningSimulation(){
        showCp5RunningScreen();

        textAlign(LEFT, BOTTOM);
        int shownSpeed = 10 - (int) speedSlider.getValue();
        textSize(15);
        fill(255);
        text("current speed: x" + shownSpeed, speedSlider.getPosition()[0], speedSlider.getPosition()[1] - 5);
        noFill();
    }

    /**
     * {@inheritDoc}
     * <p>
     * In this implementation the text shown, is an information for the user, to inform about the functionality to click an animal.
     */
    @Override
    public void popUpMessage(){
        fill(80, 150);
        rect(width/2f - 120, height/3f - 10, 240,50);

        textAlign(CENTER,TOP);
        textSize(30);
        fill(200);
        text("Click An Animal !", width/2f, height/3f);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawInfoScreen() {
        homeButton.show();
        background(infoImage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawAnimalCounter(int foxCount, int bunnyCount) {
        textSize(25);
        fill(255);
        textAlign(RIGHT, TOP);
        text("Fox: " + foxCount + " | Bunny: " + bunnyCount, width - 10, 10);
        noFill();
        textAlign(LEFT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBunny(float x, float y, int direction, boolean inMotion) {
        if(!inMotion){
            imageMode(CENTER);
            image(bunnyMovement[direction][0], x, y);
        }else{
            currentFrame = frameCount * 0.2 % 5;

            imageMode(CENTER);
            image(bunnyMovement[direction][1 + (int) (currentFrame)], x, y);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawFox(float x, float y, int direction, boolean inMotion) {
        if(!inMotion){
            imageMode(CENTER);
            image(foxMovement[direction][0], x, y);
        }else{
            currentFrame = frameCount * 0.2 % 5;

            imageMode(CENTER);
            image(foxMovement[direction][1 + (int) (currentFrame)], x, y);
        }
    }

    /**
     * Defines how parameters are being drawn on the display based on the data input.
     * @param hungerBar defines how much hunger the image should display.
     * @param thirstBar defines how much thirst the image should display.
     */
    private void drawParameters(int hungerBar, int thirstBar) {
        imageMode(CENTER);
        float boxX = width - 140;
        float boxY = 60;

        float boxWidth = 130;
        float boxHeight = 90;

        fill(80, 100);

        // outer box
        rect(boxX, boxY, boxWidth, boxHeight);

        //icons next to inner container
        image(hungerIcon, boxX + 10,boxY + 15);
        image(thirstIcon, boxX + 10,boxY + boxY / 3 + 15);

        // inner container for bars
        rect(boxX + 20, boxY + 10, boxWidth - 30,10);
        rect(boxX + 20, boxY + boxY / 3 + 10, boxWidth - 30,10);

        // filling bars
        fill(172, 114, 51);
        rect(boxX + 20, boxY + 10,100 - hungerBar,10);
        fill(74, 134, 232);
        rect(boxX + 20, boxY + boxY / 3 + 10,100 - thirstBar,10);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawFoxParameters(int hungerBar, int thirstBar) {
        drawParameters(hungerBar, thirstBar);
        imageMode(CENTER);
        image(foxIcon, width - 130 + 60, 60 + 65);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void drawBunnyParameters(int hungerBar, int thirstBar) {
        drawParameters(hungerBar, thirstBar);
        imageMode(CENTER);
        image(bunnyIcon, width - 130 + 60, 60 + 65);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void drawSelectMarking(float x, float y, float size){
        stroke(148, 87, 235);
        strokeWeight(3);
        fill(148, 87, 235,50);
        circle(x, y, size);
        stroke(0);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void drawGrass(float x, float y, double remainingPercentage) {
        tint(255, (float)remainingPercentage);
        image(grassSmall, x, y);
        noTint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drawWater(float x, float y, double remainingPercentage) {
        strokeWeight(1);
        fill(0, 0, 255, (float)remainingPercentage);
        rect(x, y, 10,10);
        noFill();
    }

    /**
     * The method is called once after every time a mouse button is pressed. It invokes a controller function, which handles the data generated by the click.
     * @param event Processing {@link MouseEvent}, which provides the x and y position of the mouse click for the controller.
     */
    @Override
    public void mousePressed(MouseEvent event) {
        controller.handleMouseInput(event.getX(), event.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void passSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
}

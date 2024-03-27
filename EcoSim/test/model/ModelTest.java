package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    EcoModel testModel;
    Bunny testBunny;
    Fox testFox;
    Resource testWater;
    Resource testGrass;

    @BeforeEach
    void setUp(){
        testModel = new EcoModel(1000, 1000);
        testBunny = new Bunny(150, 150, 100, 100);
        testFox = new Fox(150, 150, 100, 100);

        testGrass = new Resource(false, 100,100);
    }
    @AfterEach
    void tearDown(){}

    // the setRandomDirection method is called in the constructor of the abstract Animal class
    // which is why there is no need to call this method in the test
    @Test
    void animal_ShouldChange_SpeedValues_Randomly_WhenCalling_SetRandomDirection(){
        assertNotEquals(0, testBunny.getAnimalAttributes().vx());
        assertNotEquals(0, testBunny.getAnimalAttributes().vy());
    }

    @Test
    void animal_ShouldMirror_SpeedValue_BasedOn_WhichBorderItHits_WhenCalling_WallCollision(){
        float previousVx = testBunny.getAnimalAttributes().vx();
        float previousVy = testBunny.getAnimalAttributes().vy();

        // set the borders that the bunny will hit the x-axis
        testBunny.wallCollision(100, 900);
        assertNotEquals(previousVx, testBunny.getAnimalAttributes().vx());

        // set the borders that the bunny will hit the y-axis
        testBunny.wallCollision(900, 100);
        assertNotEquals(previousVy, testBunny.getAnimalAttributes().vy());
    }

    @Test
    void animal_ShouldChangeState_BasedOn_ItsOwnValues_WhenCalling_DecideState(){
        // all thresholds are 50 to change an animals state

        // initially bunny's state should be NEWBORN (without movement or artificial interference)
        testBunny.decideState();
        assertEquals(AnimalState.NEWBORN, testBunny.getAnimalAttributes().state());

        // changing hunger value, so that the method has to react accordingly
        testBunny.setHunger(55);
        testBunny.decideState();
        assertEquals(AnimalState.HUNGRY, testBunny.getAnimalAttributes().state());

        // changing hunger value, so that the method has to change to IDLE
        testBunny.setHunger(0);
        testBunny.decideState();
        assertEquals(AnimalState.IDLE, testBunny.getAnimalAttributes().state());

        // changing thirst value, so that the method has to react accordingly
        testBunny.setThirst(60);
        testBunny.decideState();
        assertEquals(AnimalState.THIRSTY, testBunny.getAnimalAttributes().state());

        // also changing hunger value, so that the method has to react and decide which state is more important
        // the method goes for thirst, because it was initially thirsty (same effect with hungry)
        testBunny.setThirst(60);
        testBunny.setHunger(60);
        testBunny.decideState();
        assertEquals(AnimalState.THIRSTY, testBunny.getAnimalAttributes().state());

        //resetting values
        testBunny.setThirst(0);
        testBunny.setHunger(0);
        testBunny.decideState();
        assertEquals(AnimalState.IDLE, testBunny.getAnimalAttributes().state());

        // setting thirst and hunger value to 60 (so the method thinks the got to 60 at the same time),
        // so that the method has to react and decide which state is more important
        // the method goes for hunger which is more important for an animal
        testBunny.setThirst(60);
        testBunny.setHunger(60);
        testBunny.decideState();
        assertEquals(AnimalState.HUNGRY, testBunny.getAnimalAttributes().state());

        // setting either value above or at 100 the animal gets flagged as dead

        //checking if animal is currently flagged as dead
        assertFalse(testBunny.getAnimalAttributes().dead());

        //checking if animal is flagged as dead after thirst goes beyond 100
        testBunny.setThirst(120);
        testBunny.decideState();
        assertTrue(testBunny.getAnimalAttributes().dead());
    }

    @Test
    void animal_ShouldMove_BasedOn_ItsCurrentDirection_WhenCalling_Movement(){
        var previousX = testBunny.getAnimalAttributes().x();
        var previousY = testBunny.getAnimalAttributes().y();

        // let it move 10 times, so difference will be apparent
        for (int i = 0; i < 10; i++) testBunny.movement();

        // check if it moved away from previous position
        assertNotEquals(previousX, testBunny.getAnimalAttributes().x());
        assertNotEquals(previousY, testBunny.getAnimalAttributes().y());
    }

    @Test
    void animal_ShouldChangeHungerAndThirst_BasedOn_Movement(){
        var previousHunger = testBunny.getAnimalAttributes().hunger();
        var previousThirst = testBunny.getAnimalAttributes().thirst();

        // let it move 10 times, so the difference will be apparent
        for (int i = 0; i < 10; i++) testBunny.movement();

        // check if it has gained hunger/thirst
        assertNotEquals(previousHunger, testBunny.getAnimalAttributes().hunger());
        assertNotEquals(previousThirst, testBunny.getAnimalAttributes().thirst());
    }

    @Test
    void animal_ShouldNotChangeAnyValues_BasedOnIfIts_Thinking(){
        var previousHunger = testBunny.getAnimalAttributes().hunger();
        var previousThirst = testBunny.getAnimalAttributes().thirst();
        var previousX = testBunny.getAnimalAttributes().x();
        var previousY = testBunny.getAnimalAttributes().y();

        // set thinking to true
        testBunny.setThinking(true);

        // let it move 10 times, so the difference will be apparent or not
        for (int i = 0; i < 10; i++) testBunny.movement();

        // check if it has gained hunger/thirst or not
        assertEquals(previousHunger, testBunny.getAnimalAttributes().hunger());
        assertEquals(previousThirst, testBunny.getAnimalAttributes().thirst());

        // check if it moved away from previous position or not
        assertEquals(previousX, testBunny.getAnimalAttributes().x());
        assertEquals(previousY, testBunny.getAnimalAttributes().y());
    }


    @Test
    void animal_ShouldChangeDirection_BasedOn_InputCoordinates_WhenCalling_MoveTo(){
        float previousX = testBunny.getAnimalAttributes().x();
        float previousY = testBunny.getAnimalAttributes().y();

        float destinationX = 10;
        float destinationY = 10;

        while(testBunny.getAnimalAttributes().x() != destinationX && testBunny.getAnimalAttributes().y() != destinationY){
            // changing direction based on input
            testBunny.moveTo(destinationX, destinationY);

            // letting bunny move
            testBunny.movement();

            // setting survival values to 0, so bunny doesn't die on the way
            testBunny.setThirst(0);
            testBunny.setHunger(0);
        }

        // checking if animal moved away from original position
        assertTrue(previousX != testBunny.getAnimalAttributes().x() && previousY != testBunny.getAnimalAttributes().y());
        // checking if animal reached its destination
        assertEquals(destinationX, testBunny.getAnimalAttributes().x());
        assertEquals(destinationY, testBunny.getAnimalAttributes().y());
    }

    @Test
    void animal_handleThirst_ShouldChangeObjectiveInSightToTrueAndDirection(){
        testWater = new Resource(true, 100,100);

        float previousVx = testBunny.getAnimalAttributes().vx();
        float previousVy = testBunny.getAnimalAttributes().vy();

        testBunny.handleThirst(testWater);
        assertTrue(testBunny.getAnimalAttributes().objectiveInSight());
        // checking if animal is "looking" to resource
        assertTrue(previousVy != testBunny.getAnimalAttributes().vy() || previousVx != testBunny.getAnimalAttributes().vx());
    }

    @Test
    void animal_handleThirst_ShouldResetValues_IfResourceWasTakenAway(){
        testWater = new Resource(true, 100,100);

        // let bunny register resource
        testBunny.handleThirst(testWater);
        assertNotNull(testBunny.getAnimalAttributes().usingThisResource());

        // taking resource away
        testWater.setCurrentlyInUse(true);

        testBunny.handleThirst(testWater);
        assertNull(testBunny.getAnimalAttributes().usingThisResource());
    }

    @Test
    void animal_ShouldStartDrinking_IfWaterIsReached(){
        testWater = new Resource(true, 100,100);

        // let bunny register resource
        testBunny.handleThirst(testWater);
        assertNotNull(testBunny.getAnimalAttributes().usingThisResource());

        // using method in optimal environment to see if animal goes to drink eventually
        while(!testBunny.getAnimalAttributes().drinking()){

            testBunny.handleThirst(testWater);

            // letting bunny move
            testBunny.movement();

            // setting survival values to 0, so bunny doesn't die on the way
            testBunny.setThirst(0);
            testBunny.setHunger(0);
        }

        testBunny.handleThirst(testWater);
        assertTrue(testBunny.getAnimalAttributes().drinking() && testWater.getResourceAttributes().currentlyInUse());
        assertEquals(0, testBunny.getAnimalAttributes().vx());
        assertEquals(0, testBunny.getAnimalAttributes().vy());
    }

    @Test
    void animal_ShouldResetValuesFromDrinkingState_IfStopUsingResourceIsCalled(){
        //setup animal drinking state
        testBunny.setState(AnimalState.THIRSTY);
        testBunny.setUsingThisResource(new Resource(true,100,100));

        testBunny.stopUsingResource();

        var bunnyGet = testBunny.getAnimalAttributes();
        assertNull(bunnyGet.usingThisResource());
    }

    @Test
    void bunny_ShouldStartEating_IfGrassIsReached(){
        testGrass = new Resource(false, 100,100);

        // let bunny register resource
        testBunny.handleHunger(testGrass);
        assertNotNull(testBunny.getAnimalAttributes().usingThisResource());

        // using method in optimal environment to see if animal goes to drink eventually
        while(!testBunny.getAnimalAttributes().eating()){

            testBunny.handleHunger(testGrass);

            // letting bunny move
            testBunny.movement();

            // setting survival values to 0, so bunny doesn't die on the way
            testBunny.setThirst(0);
            testBunny.setHunger(0);
        }

        testBunny.handleHunger(testGrass);
        assertTrue(testBunny.getAnimalAttributes().eating() && testGrass.getResourceAttributes().currentlyInUse());
        assertEquals(0, testBunny.getAnimalAttributes().vx());
        assertEquals(0, testBunny.getAnimalAttributes().vy());
    }

    @Test
    void animal_ShouldFindPartner_BasedOn_CurrentState_WhenCalling_MatingHelper(){
        // setup animal mating state
        // since I spawned the bunny directly on to "testBunny" it will be easier, so
        // I don't have to move them together with a loop
        Bunny other = new Bunny(150,150, 100, 100);
        other.setState(AnimalState.IDLE);
        testBunny.setState(AnimalState.IDLE);
        other.setMatingUrge(100);
        testBunny.setMatingUrge(100);

        testBunny.matingHelper(other);

        assertNotNull(testBunny.getAnimalAttributes().usingThisMate());
        assertNotNull(other.getAnimalAttributes().usingThisMate());
        assertTrue(testBunny.getAnimalAttributes().mating());
        assertTrue(other.getAnimalAttributes().mating());
    }

    @Test
    void animal_ShouldResetPartner_IfPartnerAlreadyHasOtherPartner(){
        Bunny other = new Bunny(150, 150, 100, 100);
        Bunny other2 = new Bunny(150, 150, 100, 100);
        other.setState(AnimalState.IDLE);
        testBunny.setState(AnimalState.IDLE);
        other.setMatingUrge(100);
        testBunny.setMatingUrge(100);
        other2.setState(AnimalState.IDLE);
        other2.setMatingUrge(100);

        //give other animal different mate
        other.setUsingThisMate(other2);
        testBunny.matingHelper(other);

        assertNotNull(other.getAnimalAttributes().usingThisMate());

        testBunny.matingHelper(other);
        assertNull(testBunny.getAnimalAttributes().usingThisMate());
        assertFalse(testBunny.getAnimalAttributes().objectiveInSight());
    }

    @Test
    void animal_ShouldResetValuesFromMatingState_IfStopMatingIsCalled(){
        Bunny other = new Bunny(150,150, 100, 100);
        other.setState(AnimalState.IDLE);
        testBunny.setState(AnimalState.IDLE);
        other.setMatingUrge(100);
        testBunny.setMatingUrge(100);

        testBunny.matingHelper(other);

        // check if matingHelper did its job
        assertNotNull(testBunny.getAnimalAttributes().usingThisMate());
        assertNotNull(other.getAnimalAttributes().usingThisMate());

        // setup "animals are done mating" state
        other.setMatingUrge(0);
        testBunny.setMatingUrge(0);

        testBunny.stopMating(other);

        assertNull(testBunny.getAnimalAttributes().usingThisMate());
        assertNull(other.getAnimalAttributes().usingThisMate());
    }

    @Test
    void animal_ShouldSetBooleanDeadTrue(){
        assertFalse(testBunny.getAnimalAttributes().dead());
        testBunny.setDead();
        assertTrue(testBunny.getAnimalAttributes().dead());
    }

    @Test
    void animal_ShouldSetBooleanEating_BasedOnInput(){
        assertFalse(testBunny.getAnimalAttributes().eating());
        testBunny.setEating(true);
        assertTrue(testBunny.getAnimalAttributes().eating());
    }

    @Test
    void animal_ShouldSetBooleanObjectiveInSight_BasedOnInput(){
        assertFalse(testBunny.getAnimalAttributes().objectiveInSight());
        testBunny.setObjectiveInSight(true);
        assertTrue(testBunny.getAnimalAttributes().objectiveInSight());
    }

    @Test
    void animal_ShouldSetBooleanThinking_BasedOnInput(){
        assertFalse(testBunny.getAnimalAttributes().thinking());
        testBunny.setThinking(true);
        assertTrue(testBunny.getAnimalAttributes().thinking());
    }

    @Test
    void animal_ShouldSetBooleanPregnant_BasedOnInput(){
        assertFalse(testBunny.getAnimalAttributes().pregnant());
        testBunny.setPregnant(true);
        assertTrue(testBunny.getAnimalAttributes().pregnant());
    }

    @Test
    void animal_ShouldSetActivityBooleans_BasedOnInput(){
        var bunnyBefore = testBunny.getAnimalAttributes();

        assertFalse(bunnyBefore.mating() && bunnyBefore.thinking() && bunnyBefore.drinking() && bunnyBefore.eating());
        testBunny.setActivityBooleans(true);

        var bunnyAfter = testBunny.getAnimalAttributes();
        assertTrue(bunnyAfter.mating() && bunnyAfter.thinking() && bunnyAfter.drinking() && bunnyAfter.eating());
    }

    @Test
    void animal_ShouldSetCurrentResourceInUsage_BasedOnInputResource(){
        assertNull(testBunny.getAnimalAttributes().usingThisResource());
        testBunny.setUsingThisResource(new Resource(true, 50 ,100));
        assertNotNull(testBunny.getAnimalAttributes().usingThisResource());
    }

    @Test
    void animal_ShouldSetCurrentMate_BasedOnInputAnimal(){
        assertNull(testBunny.getAnimalAttributes().usingThisMate());
        testBunny.setUsingThisMate(new Bunny(1,1, 50 ,100));
        assertNotNull(testBunny.getAnimalAttributes().usingThisMate());
    }

    @Test
    void animal_ShouldReturnBoolean_BasedOnAnimal(){
        assertTrue(testBunny.isHerbivore());
        assertFalse(testFox.isHerbivore());
    }

    @Test
    void animal_ShouldSetVxAndVy_BasedOnInput(){
        testBunny.setVxAndVy(10, 10);

        assertEquals(10, testBunny.getAnimalAttributes().vx());
        assertEquals(10, testBunny.getAnimalAttributes().vy());
    }

    @Test
    void animal_ShouldSetVxAndVyToZero(){
        testBunny.stopMoving();

        assertEquals(0, testBunny.getAnimalAttributes().vx());
        assertEquals(0, testBunny.getAnimalAttributes().vy());
    }

    @Test
    void animal_ShouldSetHunger_BasedOnInput(){
        testBunny.setHunger(10);

        assertEquals(10, testBunny.getAnimalAttributes().hunger());
    }

    @Test
    void animal_ShouldSetThirst_BasedOnInput(){
        testBunny.setThirst(10);

        assertEquals(10, testBunny.getAnimalAttributes().thirst());
    }

    @Test
    void animal_ShouldSetState_BasedOnInput(){
        testBunny.setState(AnimalState.HUNGRY);

        assertEquals(AnimalState.HUNGRY, testBunny.getAnimalAttributes().state());
    }

    @Test
    void bunny_ShouldFilterFoxesWhilstSearchingForMate_BasedOnInputAnimal(){
        Bunny otherBunny = new Bunny(150,150, 100, 100);
        otherBunny.setState(AnimalState.IDLE);
        otherBunny.setMatingUrge(100);

        testBunny.setState(AnimalState.IDLE);
        testBunny.setMatingUrge(100);

        testFox.setState(AnimalState.IDLE);
        testFox.setMatingUrge(100);

        testBunny.handleMating(testFox);
        assertNull(testBunny.getAnimalAttributes().usingThisMate());
        assertNull(testFox.getAnimalAttributes().usingThisMate());
        assertFalse(testBunny.getAnimalAttributes().mating());
        assertFalse(testFox.getAnimalAttributes().mating());

        testBunny.handleMating(otherBunny);
        assertNotNull(testBunny.getAnimalAttributes().usingThisMate());
        assertNotNull(otherBunny.getAnimalAttributes().usingThisMate());
        assertTrue(testBunny.getAnimalAttributes().mating());
        assertTrue(otherBunny.getAnimalAttributes().mating());
    }

    @Test
    void bunny_ShouldChangeDirection_BasedOnChance_WhichIsBasedOnInput(){
        // the passed "1" is acting as 100% chance to change direction when this method is called
        // once the function is called (and it goes through, by chance) the flag thinking is set true
        // to let the bunny stand around
        testBunny.randomBunnyMovement(1);

        // I can't test for the currentDirection, since it's random
        assertTrue(testBunny.getAnimalAttributes().thinking());
    }

    @Test
    void bunny_ShouldChangeBooleanThinkingFalse_BasedOnRandomWaitTime(){
        var previousVx = testBunny.getAnimalAttributes().vx();
        var previousVy = testBunny.getAnimalAttributes().vy();

        // the passed "1" is acting as 100% chance to change direction when this method is called
        // once the function is called (and it goes through, by chance) the flag thinking is set
        // to let the bunny stand around
        testBunny.randomBunnyMovement(1);

        // I can't test for the currentDirection, since it's random
        assertTrue(testBunny.getAnimalAttributes().thinking());

        while(testBunny.getAnimalAttributes().thinking()){
            testBunny.randomBunnyMovement(0);
        }

        assertFalse(testBunny.getAnimalAttributes().thinking());
    }

    @Test
    void bunny_ShouldChangeStateToHuntedOrIdle_BasedOnInputFoxInSightRange(){
        assertEquals(AnimalState.NEWBORN, testBunny.getAnimalAttributes().state());

        // since initial fox in setup is initialized at same position as bunny, method should trigger
        testBunny.dodgeFox(testFox);
        assertEquals(AnimalState.HUNTED, testBunny.getAnimalAttributes().state());

        // move fox out of bunny's sight range
        for(int i = 0; i < 1000; i++) testFox.movement();

        testBunny.dodgeFox(testFox);
        assertNotEquals(AnimalState.HUNTED, testBunny.getAnimalAttributes().state());
    }

    @Test
    void fox_ShouldRegisterBunnyToHuntAndSetObjectiveInSightTrue_BasedOnInputBunnyInSightRange(){
        assertFalse(testFox.getAnimalAttributes().objectiveInSight() && testFox.getAnimalAttributes().eating());

        // since initial bunny in setup is initialized at same position as fox, method should go through completely
        // until fox is eating
        testFox.handleHunger(testBunny);

        assertTrue(testFox.getAnimalAttributes().objectiveInSight() && testFox.getAnimalAttributes().eating());
    }

    @Test
    void fox_ShouldRegisterBunnyAndSwitchToNearestBunny_BasedOnInputBunnyInSightRange(){
        testFox.handleHunger(new Bunny(100,100, 150,150));

        // save current movement direction
        float previousVx = testFox.getAnimalAttributes().vx();
        float previousVy = testFox.getAnimalAttributes().vy();

        // test bunny position is nearer to fox than the anonymous bunny, which is why fox should change its target
        // and should also change movement direction to testBunny
        testFox.handleHunger(testBunny);

        assertNotEquals(previousVx, testFox.getAnimalAttributes().vx());
        assertNotEquals(previousVy, testFox.getAnimalAttributes().vy());
    }

    @Test
    void fox_ShouldLeaveBunnyIfItsTooFar_BasedOnInputBunnyInSightRange(){
        testFox.handleHunger(new Bunny(100,100, 1000,1000));
        assertNull(testFox.getUsingThisPrey());
    }

    @Test
    void fox_ShouldLeaveTargetIfItRanTooFar_BasedOnInputBunnyInSightRange(){
        Bunny other = new Bunny(100,100, 0,0);

        testFox.handleHunger(other);
        assertNotNull(testFox.getUsingThisPrey());

        other.setVxAndVy(1, 1);
        for (int i = 0; i < 1000; i++) other.movement();

        testFox.handleHunger(other);
        assertNull(testFox.getUsingThisPrey());
    }

    @Test
    void fox_ShouldSetValuesAfterHunt_BasedOnFoxesStateAndHunger(){
        testFox.setEating(true);

        // this method is only called after a hunt, so the fox has to be eating, but also has to have hunger lower than 0
        // or equal to 0, which it has when first initializing
        testFox.stopHunting();

        assertEquals(-1, testFox.getAnimalAttributes().hunger());
        assertFalse(testFox.getAnimalAttributes().eating());
    }

    @Test
    void fox_ShouldFilterBunniesWhilstSearchingForMate_BasedOnInputAnimal(){
        Fox otherFox = new Fox(1,150, 100, 100);
        otherFox.setState(AnimalState.IDLE);
        otherFox.setMatingUrge(100);

        testFox.setState(AnimalState.IDLE);
        testFox.setMatingUrge(100);

        testBunny.setState(AnimalState.IDLE);
        testBunny.setMatingUrge(100);

        testFox.handleMating(testBunny);
        assertNull(testBunny.getAnimalAttributes().usingThisMate());
        assertNull(testFox.getAnimalAttributes().usingThisMate());
        assertFalse(testBunny.getAnimalAttributes().mating());
        assertFalse(testFox.getAnimalAttributes().mating());

        testFox.handleMating(otherFox);
        assertNotNull(testFox.getAnimalAttributes().usingThisMate());
        assertNotNull(otherFox.getAnimalAttributes().usingThisMate());
        assertTrue(testFox.getAnimalAttributes().mating());
        assertTrue(otherFox.getAnimalAttributes().mating());
    }

    @Test
    void resource_ShouldChangeRemainingPercentage_BasedOnBoolean(){
        var previousPercentageUsed = testGrass.getResourceAttributes().remainingPercentage();

        // put the flag up that resource is being used
        testGrass.setCurrentlyInUse(true);
        // check if resource is being used and act accordingly
        testGrass.usage();
        assertTrue(previousPercentageUsed > testGrass.getResourceAttributes().remainingPercentage());
    }

    @Test
    void resource_ShouldRegenerateRemainingPercentage_BasedOnAttributes(){
        var previousPercentageUsed = testGrass.getResourceAttributes().remainingPercentage();

        // put the flag up that resource is being used
        testGrass.setCurrentlyInUse(true);
        // check if resource is being used and act accordingly
        testGrass.usage();
        assertTrue(previousPercentageUsed > testGrass.getResourceAttributes().remainingPercentage());

        // resource only regenerates if it is not in use
        testGrass.setCurrentlyInUse(false);
        previousPercentageUsed = testGrass.getResourceAttributes().remainingPercentage();
        testGrass.regenerate();
        assertTrue(previousPercentageUsed < testGrass.getResourceAttributes().remainingPercentage());
    }

    @Test
    void resource_ShouldBeFlaggedUnusableAndUsable_BasedOnRemainingPercentage(){

        testGrass.setCurrentlyInUse(true);
        while (testGrass.getResourceAttributes().remainingPercentage() >= 0) testGrass.usage();

        testGrass.setCurrentlyInUse(false);
        testGrass.regenerate();

        assertTrue(testGrass.getResourceAttributes().unusable());

        while (testGrass.getResourceAttributes().remainingPercentage() <= 100) testGrass.regenerate();

        assertFalse(testGrass.getResourceAttributes().unusable());
    }

    @Test
    void ecoModel_arrayLists_AnimalsAndResources_shouldContainSameAmountOfAnimalsAndResourcesAsPassedIn_WhenCalling_StartNewSim(){
        int initialBunnies = 2;
        int initialFoxes = 3;
        int initialWaterSources = 1;
        int initialGrassSources = 1;

        assertEquals(0, testModel.getAnimalList().size());
        assertEquals(0, testModel.getResourceList().size());

        testModel.startNewSim(initialBunnies, initialFoxes, initialWaterSources, initialGrassSources);

        assertEquals(initialBunnies + initialFoxes, testModel.getAnimalList().size());
        assertEquals(initialGrassSources + initialWaterSources, testModel.getResourceList().size());
    }

    @Test
    void ecoModel_ShouldClearOldSimulation(){
        int initialBunnies = 2;
        int initialFoxes = 3;
        int initialWaterSources = 1;
        int initialGrassSources = 1;

        testModel.startNewSim(initialBunnies, initialFoxes, initialWaterSources, initialGrassSources);

        assertEquals(initialBunnies + initialFoxes, testModel.getAnimalList().size());
        assertEquals(initialGrassSources + initialWaterSources, testModel.getResourceList().size());

        testModel.clearOldSim();

        assertEquals(0, testModel.getAnimalList().size());
        assertEquals(0, testModel.getResourceList().size());
    }

    @Test
    void animalThread_ShouldStart_WhenCalling_StartEcoSimulationThread(){
        assertFalse(testModel.isEcoSimulationThreadAlive());
        testModel.startEcoSimulationThread();
        assertTrue(testModel.isEcoSimulationThreadAlive());
    }

    @Test
    void booleanActiveThread_ShouldChangeThreadStateBasedOnInput_WhenCalling_PlayEcoSimulationThread(){
        assertFalse(testModel.isEcoSimulationThreadAlive());

        testModel.startNewSim(5, 5, 5, 5);
        testModel.startEcoSimulationThread();
        assertTrue(testModel.isEcoSimulationThreadAlive());
    }

    @Test
    void ecoModel_ShouldReturnFalse_WhenCalling_IsEcoSimulationThreadAlive(){
        assertFalse(testModel.isEcoSimulationThreadAlive());
    }

    @Test
    void ecoModel_ShouldReturnAnimalCountOfBunniesOrFoxes_BasedOn_BooleanInput(){
        testModel.startNewSim(5, 10, 0, 0);

        assertEquals(5, testModel.getAnimalCountOf(true));
        assertEquals(10, testModel.getAnimalCountOf(false));
    }

    @Test
    void ecoModel_ShouldReturnCorrectHeightAndWidthWhichWasPassed(){
        assertEquals(1000, testModel.getHeight());
        assertEquals(1000, testModel.getWidth());
    }

    @Test
    void ecoModel_ShouldSetCorrectActiveThreadBoolean_BasedOn_Input(){
        testModel.startEcoSimulationThread();

        assertFalse(testModel.isRunSim());
        testModel.playEcoSimulationThread(true);
        assertTrue(testModel.isRunSim());
    }

    @Test
    void ecoModel_ShouldSetCorrectValueForSimulationSpeed_BasedOn_Input(){
        assertNotEquals(120, testModel.getSimulationSpeed());

        testModel.setSimulationSpeed(120);

        assertEquals(120, testModel.getSimulationSpeed());
    }

    @Test
    void iEcoModel_ShouldReturnCorrectInformationAboutAnimal(){
        var foxGet = testModel.animalAttributes(testFox);

        assertEquals(100, foxGet.x());
        assertEquals(100, foxGet.y());
        assertEquals(150, foxGet.sightRange());

        var foxVx = foxGet.vx();
        var foxVy = foxGet.vy();

        assertEquals(foxVx, foxGet.vx());
        assertEquals(foxVy, foxGet.vy());

        //foxes don't think & aren't herbivores
        assertFalse(foxGet.thinking());
        assertFalse(foxGet.herbivore());
    }

    @Test
    void iEcoModel_ShouldReturnCorrectInformationAboutResource(){
        var grassGet = testModel.resourceAttributes(testGrass);

        assertFalse(grassGet.resourceTypeWater());

        var grassX = testGrass.getResourceAttributes().x();
        var grassY = testGrass.getResourceAttributes().y();

        assertEquals(grassX, grassGet.x());
        assertEquals(grassY, grassGet.y());

        assertTrue(1 <= grassGet.remainingPercentage());
    }
}
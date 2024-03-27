package model;

/**
 * Record that represents an animals attributes in the Ecosystem Simulation.
 * <p>
 * The record acts as a data container to the controller and is read-only.
 * <p>
 * Note: According to the guidelines set by Prof. Dr. Martin Weigel in the javadoc for the class <a href="https://moodle.thm.de/course/view.php?id=10282#section-4">'Pokemon.java' from 'Evolis Adventure' (Final Edition)</a>, this record is intentionally made public.
 *
 * @param dead Indicates if an animal is dead.
 * @param eating Indicates if an animal is eating.
 * @param drinking Indicates if an animal is drinking.
 * @param objectiveInSight Indicates if an animal has an objective in its sight.
 * @param thinking Indicates if an animal is thinking.
 * @param mating Indicates if an animal is mating.
 * @param pregnant Indicates if an animal is pregnant.
 * @param herbivore Indicates if an animal is a herbivore.
 * @param state Current state of an animal.
 * @param usingThisResource Current resource which is used by an animal.
 * @param usingThisMate Current mate which is used by an animal.
 * @param x Current x-position of an animal.
 * @param y Current y-position of an animal.
 * @param vx Current vx of an animal.
 * @param vy Current vy of an animal.
 * @param hunger Current hunger of an animal.
 * @param thirst Current thirst of an animal.
 * @param sightRange Sight range of an animal.
 * @param id Identification number of an animal.
 *
 * @author Sleman Kakar
 */
public record AnimalAttributes(boolean dead, boolean eating, boolean drinking, boolean objectiveInSight, boolean thinking, boolean mating, boolean pregnant, boolean herbivore, AnimalState state, Resource usingThisResource, Animal usingThisMate, float x, float y, float vx, float vy, double hunger, double thirst, double sightRange, int id){}
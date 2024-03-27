package model;

/**
 * Defines a Resource in the Ecosystem Simulation.
 * <p>
 * The record acts as a data container to the controller and is read-only.
 * <p>
 * Note: According to the guidelines set by Prof. Dr. Martin Weigel in the javadoc for the class <a href="https://moodle.thm.de/course/view.php?id=10282#section-4">'Pokemon.java' from 'Evolis Adventure' (Final Edition)</a>, this record is intentionally made public.
 *
 * @param resourceTypeWater Indicates the type of resource.
 * @param x x-position of resource.
 * @param y y-position of resource.
 * @param remainingPercentage remaining percentage of resource.
 * @param unusable Indicates if resource is not usable.
 * @param currentlyInUse Indicates if resource is currently in use.
 *
 * @author Sleman Kakar
 */
public record ResourceAttributes(boolean resourceTypeWater, float x, float y, double remainingPercentage, boolean unusable, boolean currentlyInUse) {}

package model;

/**
 * (package-private) Enumeration that represents different states an animal can take.
 * <ul>
 *     <li>NEWBORN: Initial state when an animal is born.</li>
 *     <li>IDLE: State when an animal isn't in any other state.</li>
 *     <li>HUNGRY: State when an animal is hungry.</li>
 *     <li>THIRSTY: State when an animal is thirsty.</li>
 *     <li>HUNTED: State when an animal is "scared".</li>
 * </ul>
 */
enum AnimalState {NEWBORN, IDLE, HUNGRY, THIRSTY, HUNTED}
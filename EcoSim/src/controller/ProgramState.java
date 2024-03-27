package controller;

/**
 * Enumeration that represents different states of the program.
 * <ul>
 *     <li>{@link #START}</li>
 *     <li>{@link #SETTINGS}</li>
 *     <li>{@link #RUNNING}</li>
 *     <li>{@link #INFORMATION}</li>
 * </ul>
 */
enum ProgramState {
    /**
     * The state for the start screen.
     */
    START,
    /**
     * The state for the settings screen.
     */
    SETTINGS,
    /**
     * The state for the running simulation screen.
     */
    RUNNING,
    /**
     * The state for the information screen.
     */
    INFORMATION
}

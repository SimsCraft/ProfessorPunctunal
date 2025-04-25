package com.simcraft.graphics.states;

/**
 * Represents the current game state.
 * <p>
 * Encapsulates logic for tracking and verifying transitions between
 * predefined states such as RUNNING, PAUSED, GAME_OVER, etc.
 */
public class GameState {

    /**
     * Enum representing possible states of the game.
     */
    public enum State {
        NOT_INITIALIZED,
        INITIALIZING,
        RUNNING,
        PAUSED,
        GAME_OVER,
        STOPPED
    }

    // ----- INSTANCE VARIABLE -----
    private State currentState;

    // ----- CONSTRUCTOR -----
    /**
     * Constructs a new GameState and defaults to NOT_INITIALIZED.
     */
    public GameState() {
        currentState = State.NOT_INITIALIZED;
    }

    // ----- SETTERS -----
    /**
     * Sets the current game state to the specified new state.
     *
     * @param newState The new state to transition to.
     */
    public void setState(State newState) {
        if (newState == null)
            throw new IllegalArgumentException("Game state cannot be null.");
        this.currentState = newState;
    }

    // ----- GETTERS -----
    /**
     * Returns the current game state.
     *
     * @return The current game state.
     */
    public State getState() {
        return currentState;
    }

    // ----- HELPER METHODS FOR COMMON STATE CHECKS -----
    public boolean isRunning() {
        return currentState == State.RUNNING;
    }

    public boolean isPaused() {
        return currentState == State.PAUSED;
    }

    public boolean isInitializing() {
        return currentState == State.INITIALIZING;
    }

    public boolean isStopped() {
        return currentState == State.STOPPED;
    }

    public boolean canInitialize() {
        return currentState == State.NOT_INITIALIZED || currentState == State.INITIALIZING;
    }

    /**
     * Utility method to directly compare current state to a given one.
     * Used in switch statements or conditional checks.
     *
     * @param state The state to compare against.
     * @return true if current state matches the given state.
     */
    public boolean is(State state) {
        return this.currentState == state;
    }

    // ----- STATE VALIDATION HELPERS -----
    /**
     * Throws an exception if the game is not initialized.
     * Useful for ensuring safe access to resources that depend on init state.
     *
     * @param methodName The name of the method making the call (for debugging).
     */
    public void ensureInitialized(String methodName) {
        if (currentState == State.NOT_INITIALIZED || currentState == State.INITIALIZING) {
            throw new IllegalStateException("Cannot call " + methodName + " before game is initialized.");
        }
    }

    /**
     * Throws an exception if the game is not currently running.
     *
     * @param methodName The name of the method making the call (for debugging).
     */
    public void ensureRunning(String methodName) {
        if (currentState != State.RUNNING) {
            throw new IllegalStateException("Cannot call " + methodName + " unless game is running.");
        }
    }
}
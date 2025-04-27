package com.simcraft.graphics.effects;

import com.simcraft.interfaces.VisualEffect;

/**
 * An abstract base class for time-based visual effects, providing common
 * functionality such as tracking duration and completion. Subclasses of
 * TimedVisualEffect will automatically handle the timing aspects of an effect.
 */
public abstract class TimedVisualEffect implements VisualEffect {

    // ----- INSTANCE VARIABLES -----
    /**
     * The time in milliseconds when the effect started.
     */
    protected long startTime;

    /**
     * The total duration of the effect in milliseconds.
     */
    protected long durationMillis;

    /**
     * A flag indicating whether the effect has completed its duration.
     */
    protected boolean isEffectFinished = false;

    /**
     * A flag indicating whether the effect is currently active and running.
     */
    protected boolean isEffectActive = false;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a TimedVisualEffect with a specified duration.
     *
     * @param durationMillis The duration of the effect in milliseconds.
     */
    protected TimedVisualEffect(long durationMillis) {
        this.durationMillis = durationMillis;
    }

    // ----- GETTERS -----
    /**
     * Checks if the visual effect is currently active and running.
     *
     * @return true if the effect is active, false otherwise.
     */
    @Override
    public boolean isEffectActive() {
        return isEffectActive;
    }

    /**
     * Checks if the visual effect has completed its intended duration.
     *
     * @return true if the effect has finished, false otherwise.
     */
    @Override
    public boolean isEffectFinished() {
        return isEffectFinished;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Hook method called when the visual effect starts. Subclasses can override
     * this method to perform any setup or initialization required when the
     * effect begins.
     */
    protected void onStartEffect() {
        // Subclasses can implement
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Starts the visual effect, setting it to active and recording the start
     * time. Subclasses should override the {@link #onStartEffect()} method to
     * implement any specific initialization logic.
     */
    @Override
    public void startEffect() {
        if (!isEffectActive) {
            isEffectActive = true;
            isEffectFinished = false;
            startTime = System.currentTimeMillis();
            onStartEffect(); // Hook for subclasses
        }
    }

    /**
     * Updates the visual effect based on the elapsed time since it started.
     * This method manages the effect's lifecycle based on its duration and
     * calls the abstract {@link #updateTimedEffect(long)} method for
     * subclass-specific updates.
     */
    @Override
    public void updateEffect() {
        if (isEffectActive && !isEffectFinished) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            if (elapsedTime >= durationMillis) {
                isEffectFinished = true;
                isEffectActive = false;
                onEffectEnd(); // Hook for subclasses
            } else {
                updateTimedEffect(elapsedTime); // Subclass-specific update
            }
        }
    }

    // ----- ABSTRACT METHODS -----
    /**
     * Hook method called when the visual effect ends (reaches its duration).
     * Subclasses can override this method to perform any cleanup or final
     * actions.
     */
    protected abstract void onEffectEnd();

    /**
     * Subclasses must implement this method to define how the visual effect
     * changes over time, based on the elapsed time since the effect started.
     *
     * @param elapsedTime The time elapsed since the effect started in
     * milliseconds.
     */
    protected abstract void updateTimedEffect(long elapsedTime);
}

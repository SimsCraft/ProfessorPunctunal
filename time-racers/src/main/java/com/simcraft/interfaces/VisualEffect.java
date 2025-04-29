package com.simcraft.interfaces;

import java.awt.Graphics2D;

/**
 * The {@code VisualEffect} interface defines the contract for visual effects
 * that can be applied within the game. Implementations of this interface will
 * be responsible for updating their state over time, rendering themselves onto
 * a {@link Graphics2D} context, and managing their active and finished states.
 * This interface provides a common structure for various visual enhancements
 * such as animations, transitions, and status indicators.
 */
public interface VisualEffect {

    /**
     * Updates the state of the visual effect. This method is typically called
     * in each frame of the game loop to advance the effect's animation, modify
     * its properties (e.g., position, color, alpha), or check for completion.
     */
    void updateEffect();

    /**
     * Draws the visual effect onto the provided {@link Graphics2D} context. The
     * implementation of this method will define how the effect is rendered on
     * the screen, taking into account its current state.
     *
     * @param g2d The graphics context to draw on. Implementations should use
     * this context to perform drawing operations.
     */
    void draw(Graphics2D g2d);

    /**
     * Starts the visual effect. This method is called to initialize the
     * effect's state, set any starting parameters, and begin its visual process
     * or animation. An effect might need to be started before its
     * {@code updateEffect()} and {@code draw(Graphics2D)} methods are called.
     */
    void startEffect();

    /**
     * Checks if the visual effect has completed its intended duration,
     * animation, or process. This method is used to determine when the effect
     * should be considered finished and potentially removed or stopped.
     *
     * @return {@code true} if the effect has finished, {@code false} otherwise.
     */
    boolean isEffectFinished();

    /**
     * Checks if the visual effect is currently active and running. An effect
     * might be started but not yet finished, in which case it would be active.
     * This can be used to determine if the effect should be updated and drawn.
     *
     * @return {@code true} if the effect is active, {@code false} otherwise.
     */
    boolean isEffectActive();
}

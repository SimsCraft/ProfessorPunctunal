package com.simcraft.interfaces;

import java.awt.Graphics2D;

public interface VisualEffect {

    /**
     * Updates the state of the visual effect. This method is typically called
     * in the game loop to advance the effect's animation or properties.
     */
    void updateEffect();

    /**
     * Draws the visual effect onto the provided Graphics2D context. The
     * implementation will define how the effect is rendered on the screen.
     *
     * @param g2d The graphics context to draw on.
     */
    void draw(Graphics2D g2d);

    /**
     * Starts the visual effect, initializing its state and beginning its
     * animation or process.
     */
    void startEffect();

    /**
     * Checks if the visual effect has completed its intended duration or
     * process.
     *
     * @return true if the effect has finished, false otherwise.
     */
    boolean isEffectFinished();

    /**
     * Checks if the visual effect is currently active and running.
     *
     * @return true if the effect is active, false otherwise.
     */
    boolean isEffectActive();
}

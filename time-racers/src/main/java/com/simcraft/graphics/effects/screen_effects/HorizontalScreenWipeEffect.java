package com.simcraft.graphics.effects.screen_effects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.simcraft.graphics.effects.TimedVisualEffect;

/**
 * A visual effect that creates a horizontal wipe across the screen, typically
 * used as a transition between different game states or screens. The wipe
 * starts from the left and expands to the right, covering the current content
 * with a solid black color over a specified duration. Once the wipe is
 * complete, an optional callback can be executed.
 */
public class HorizontalScreenWipeEffect extends TimedVisualEffect {

    private final JPanel panel;
    private BufferedImage currentScreenImage;
    private Runnable onWipeComplete;

    /**
     * Constructs a {@code HorizontalScreenWipeEffect} associated with a
     * specific {@link JPanel}. The effect will use the dimensions of this panel
     * for the wipe animation.
     *
     * @param panel The {@code JPanel} on which the wipe effect will occur.
     * @param durationMillis The duration of the wipe effect in milliseconds.
     */
    public HorizontalScreenWipeEffect(JPanel panel, long durationMillis) {
        super(durationMillis);
        this.panel = panel;
    }

    /**
     * Starts the horizontal screen wipe effect. Before starting, it captures
     * the current content of the associated panel into an image. An optional
     * {@link Runnable} can be provided to be executed once the wipe effect is
     * complete. The effect will only start if it's not already active and the
     * panel has valid dimensions.
     *
     * @param onComplete A {@link Runnable} to be executed when the wipe effect
     * ends. Can be {@code null} if no action is needed upon completion.
     */
    public void startEffect(Runnable onComplete) {
        if (!isEffectActive() && panel.getWidth() > 0 && panel.getHeight() > 0) {
            super.startEffect();
            this.currentScreenImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = this.currentScreenImage.createGraphics();
            this.panel.paintAll(g2d); // Render the current state of the panel onto the image
            g2d.dispose();
            this.onWipeComplete = onComplete;
        }
    }

    /**
     * This method is called during each frame update while the effect is
     * active. For a simple horizontal wipe, no specific timed updates to
     * internal state are needed as the progress is directly calculated in the
     * {@link #draw(Graphics2D)} method based on the elapsed time.
     *
     * @param elapsedTime The time elapsed in milliseconds since the effect
     * started.
     */
    @Override
    protected void updateTimedEffect(long elapsedTime) {
        // No specific timed updates needed for this effect
    }

    /**
     * This method is called once the duration of the effect has elapsed. It
     * cleans up any resources used by the effect (in this case, the captured
     * screen image) and executes the completion callback if one was provided.
     */
    @Override
    protected void onEffectEnd() {
        this.currentScreenImage = null; // Release the captured screen image
        if (this.onWipeComplete != null) {
            this.onWipeComplete.run(); // Execute the completion callback
        }
    }

    /**
     * Draws the horizontal wipe effect on the provided {@link Graphics2D}
     * context. If the effect is active, it calculates the current progress of
     * the wipe based on the elapsed time and draws a black rectangle that
     * expands horizontally from the left edge of the panel. The underlying
     * content (captured in {@code currentScreenImage} before the wipe started)
     * remains visible to the right of the expanding black rectangle until it is
     * fully covered.
     *
     * @param g2d The {@link Graphics2D} context to draw on.
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (isEffectActive()) {
            int width = panel.getWidth();
            int height = panel.getHeight();
            long elapsedTime = System.currentTimeMillis() - startTime;
            double progress = Math.min(1.0, (double) elapsedTime / durationMillis); // Ensure progress doesn't exceed 1.0
            int wipePosition = (int) (width * progress);

            // Draw a black rectangle that expands from left to right
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, wipePosition, height);

            // Note: The underlying panel (or the captured image of it) is what will be
            // visible in the area not yet covered by the black rectangle. This effect
            // simply overlays a growing black area. For a true "wipe" revealing a new
            // screen, the content of the panel being wiped would typically be changed
            // as the wipe progresses or immediately after it completes.
        }
    }
}

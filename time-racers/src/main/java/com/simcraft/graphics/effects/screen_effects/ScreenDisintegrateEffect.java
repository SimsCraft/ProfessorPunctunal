package com.simcraft.graphics.effects.screen_effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JPanel;

import com.simcraft.graphics.effects.TimedVisualEffect;

/**
 * A visual effect that simulates the disintegration of a screen. It captures a
 * snapshot of a JPanel and gradually makes parts of it transparent over a
 * specified duration, creating a disintegration animation.
 */
public class ScreenDisintegrateEffect extends TimedVisualEffect {
    // ----- INSTANCE VARIABLES -----
    /**
     * The {@link JPanel} that this disintegration effect is applied to. Used to capture
     * the initial screen image and for dimension information.
     */
    private final JPanel panel;

    /**
     * A random number generator used to determine which pixels are erased.
     */
    private final Random random;

    /**
     * A snapshot of the {@link JPanel}'s content at the start of the effect, which is
     * manipulated to create the disintegration.
     */
    private BufferedImage screenImage;

    /**
     * A {@link Runnable} to be executed when the disintegration effect is complete,
     * typically used to transition to the next screen.
     */
    private Runnable onTransitionComplete;

    /**
     * A counter used to control the frequency and intensity of the
     * disintegration.
     */
    private int time;

    // 
    /**
     * Constructs a ScreenDisintegrateEffect with the target {@link JPanel} and the
     * duration of the disintegration effect.
     *
     * @param panel The {@link JPanel} to disintegrate.
     * @param durationMillis The duration of the disintegration effect in
     * milliseconds.
     */
    public ScreenDisintegrateEffect(JPanel panel, long durationMillis) {
        super(durationMillis);
        this.panel = panel;
        this.random = new Random();
        time = 0;
    }

    /**
     * Starts the disintegration effect. It captures a snapshot of the provided
     * {@link JPanel} and initializes the effect's state.
     *
     * @param onComplete A {@link Runnable} to be executed when the effect finishes.
     */
    public void startEffect(Runnable onComplete) {
        if (!isEffectActive() && panel.getWidth() > 0 && panel.getHeight() > 0) {
            super.startEffect();
            this.time = 0;
            this.screenImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = this.screenImage.createGraphics();
            this.panel.paintAll(g2d);
            g2d.dispose();
            this.onTransitionComplete = onComplete;
        }
    }

    /**
     * Randomly makes parts of the provided {@link BufferedImage} transparent based on
     * the given interval. A smaller interval results in more pixels being
     * erased.
     *
     * @param im The {@link BufferedImage} to modify.
     * @param interval The frequency at which pixels are made transparent (1
     * erases most, larger values erase fewer).
     */
    public void eraseImageParts(BufferedImage im, int interval) {
        if (im == null) {
            return;
        }
        int imWidth = im.getWidth();
        int imHeight = im.getHeight();
        int[] pixels = new int[imWidth * imHeight];
        im.getRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);

        for (int i = 0; i < pixels.length; i++) {
            if (this.random.nextInt(interval) == 0) {
                pixels[i] = 0; // Make transparent (alpha = 0)
            }
        }
        im.setRGB(0, 0, imWidth, imHeight, pixels, 0, imWidth);
    }

    /**
     * Updates the disintegration effect based on the elapsed time. It controls
     * the frequency and intensity of pixel erasure to create the disintegration
     * animation over the specified duration.
     *
     * @param elapsedTime The time elapsed since the effect started in
     * milliseconds.
     */
    @Override
    protected void updateTimedEffect(long elapsedTime) {
        this.time++;
        if (this.time % 5 == 0) { // Adjust disintegration frequency based on elapsed time
            int interval;
            if (elapsedTime < this.durationMillis / 3) {
                interval = 10 + (int) (elapsedTime / (this.durationMillis / 30));
            } else if (elapsedTime < 2 * this.durationMillis / 3) {
                interval = 5 + (int) ((elapsedTime - this.durationMillis / 3) / (this.durationMillis / 60));
            } else {
                interval = 1 + (int) ((elapsedTime - 2 * this.durationMillis / 3) / (this.durationMillis / 90));
            }
            eraseImageParts(this.screenImage, interval);
        }
    }

    /**
     * Called when the disintegration effect has completed its duration. It
     * disposes of the captured screen image and executes the
     * {@link #onTransitionComplete} (a {@link Runnable}), if one was provided.
     */
    @Override
    protected void onEffectEnd() {
        this.screenImage = null;
        if (this.onTransitionComplete != null) {
            this.onTransitionComplete.run();
        }
    }

    /**
     * Draws the current state of the disintegration effect onto the provided
     * Graphics2D context. It draws the manipulated snapshot of the screen.
     *
     * @param g2d The Graphics2D context to draw on.
     */
    @Override
    public void draw(Graphics2D g2d) {
        if (isEffectActive() && this.screenImage != null) {
            g2d.drawImage(this.screenImage, 0, 0, this.panel.getWidth(), this.panel.getHeight(), null);
        }
    }
}

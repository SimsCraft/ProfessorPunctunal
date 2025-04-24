package com.simcraft.graphics.animations;

import java.awt.image.BufferedImage;

import com.simcraft.managers.ImageManager;

/**
 * Represents a single frame in the animation
 */
public class AnimationFrame {

    // ----- INSTANCE VARIABLES -----
    /**
     * The image displayed by the frame.
     */
    final BufferedImage image;
    /**
     * How many milliseconds to display the frame for.
     */
    final long displayDurationMs;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a new animation frame using a pre-loaded
     * {@link BufferedImage}.
     *
     * @param image The displayed image.
     * @param displayDurationMs How many milliseconds to display the frame for.
     */
    public AnimationFrame(final BufferedImage image, final long displayDurationMs) {
        if (image == null) {
            throw new IllegalArgumentException(String.format(
                    "%s: Must provide an image.",
                    this.getClass().getName()
            ));
        }

        if (displayDurationMs <= 0) {
            throw new IllegalArgumentException(String.format(
                    "%s: Must provide a positive duration.",
                    this.getClass().getName()
            ));
        }

        this.image = image;
        this.displayDurationMs = displayDurationMs;
    }

    /**
     * Constructs a new animation frame using the given filepath.
     *
     * @param imageFilepath The filepath of the displayed image.
     * @param displayDurationMs How many milliseconds to display the frame for.
     */
    public AnimationFrame(final String imageFilepath, final long displayDurationMs) {
        if (imageFilepath == null || imageFilepath.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "%s: Must provide a filepath.",
                    this.getClass().getName()
            ));
        }

        if (displayDurationMs <= 0) {
            throw new IllegalArgumentException(String.format(
                    "%s: Must provide a positive duration.",
                    this.getClass().getName()
            ));
        }

        this.image = ImageManager.loadBufferedImage(imageFilepath);
        this.displayDurationMs = displayDurationMs;
    }

    // ----- GETTERS -----
    /**
     * Returns the image displayed by the frame.
     * 
     * @return The frame image.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the frame's display duration in milliseconds.
     * 
     * @return The display duration.
     */
    public long getDisplayDurationMs() {
        return displayDurationMs;
    }
}

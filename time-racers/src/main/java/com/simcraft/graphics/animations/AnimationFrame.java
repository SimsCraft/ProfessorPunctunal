package com.simcraft.graphics.animations;

import java.awt.image.BufferedImage;

import com.simcraft.managers.ImageManager;

/**
 * Represents a single frame in an animation sequence. Each frame consists of a
 * {@link BufferedImage} to be displayed and a duration for how long it should
 * be shown.
 */
public class AnimationFrame {

    // ----- INSTANCE VARIABLES -----
    /**
     * The image displayed by the frame. This is the visual content of the
     * frame.
     */
    private final BufferedImage image;

    /**
     * The duration in milliseconds for which this frame should be displayed
     * during the animation.
     */
    private final long displayDurationMs;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a new {@code AnimationFrame} using a pre-loaded
     * {@link BufferedImage}.
     *
     * @param image The {@code BufferedImage} to be displayed for this frame.
     * Must not be {@code null}.
     * @param displayDurationMs The duration in milliseconds to display this
     * frame. Must be a positive value.
     * @throws IllegalArgumentException If the provided image is {@code null} or
     * if the display duration is not positive.
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
     * Constructs a new {@code AnimationFrame} by loading the image from the
     * given file path.
     *
     * @param imageFilepath The file path of the image to be loaded and
     * displayed for this frame. Must not be {@code null} or empty.
     * @param displayDurationMs The duration in milliseconds to display this
     * frame. Must be a positive value.
     * @throws IllegalArgumentException If the provided file path is
     * {@code null} or empty, or if the display duration is not positive.
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
     * Returns the image displayed by this animation frame.
     *
     * @return The {@link BufferedImage} of the frame.
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the duration in milliseconds for which this frame should be
     * displayed.
     *
     * @return The display duration in milliseconds.
     */
    public long getDisplayDurationMs() {
        return displayDurationMs;
    }
}

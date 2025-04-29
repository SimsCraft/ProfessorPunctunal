package com.simcraft.graphics.animations;

import java.awt.Image;
import java.util.ArrayList;

import com.simcraft.graphics.screens.subpanels.GamePanel;

/**
 * The {@code Animation} class manages a sequence of images (frames) to create
 * an animation. It controls the timing of each frame and provides methods to
 * start, stop, update, and retrieve the current frame of the animation.
 */
public class Animation {

    // ----- INSTANCE VARIABLES -----
    /**
     * The {@link GamePanel} on which the animation might be displayed
     * (currently unused).
     */
    @SuppressWarnings("unused")
    private GamePanel panel;

    /**
     * A collection of {@link AnimFrame} objects, each containing an image and
     * its display duration.
     */
    private ArrayList<AnimFrame> frames;

    /**
     * The index of the current frame being displayed in the animation sequence.
     */
    private int currFrameIndex;

    /**
     * The total time that the animation has been running, in milliseconds.
     */
    private long animTime;

    /**
     * The time in milliseconds when the last frame update occurred.
     */
    private long startTime;

    /**
     * The total duration of the entire animation sequence, in milliseconds.
     */
    private long totalDuration;

    /**
     * Flag indicating whether the animation should loop back to the beginning
     * after completion.
     */
    private boolean loop;

    /**
     * Flag indicating whether the animation is currently active (running).
     */
    private boolean isActive;

    // ----- CONSTRUCTORS -----
    /**
     * Creates a new, empty {@code Animation} with the specified looping
     * behavior.
     *
     * @param loop {@code true} if the animation should loop, {@code false}
     * otherwise.
     */
    public Animation(boolean loop) {
        frames = new ArrayList<>();
        totalDuration = 0;
        this.loop = loop;
        isActive = false;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new image (frame) to the animation sequence with the specified
     * display duration.
     *
     * @param image The {@link Image} to add as a frame.
     * @param duration The duration in milliseconds to display this frame.
     */
    public synchronized void addFrame(Image image, long duration) {
        totalDuration += duration;
        frames.add(new AnimFrame(image, totalDuration));
    }

    /**
     * Starts the animation from the beginning. Resets the animation time,
     * current frame index, and start time.
     */
    public synchronized void start() {
        isActive = true;
        animTime = 0;
        currFrameIndex = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * Stops the animation. The animation will no longer update or advance
     * frames.
     */
    public synchronized void stop() {
        isActive = false;
    }

    /**
     * Updates the current frame of the animation based on the elapsed time. If
     * the animation is active, it calculates the elapsed time since the last
     * update and advances the animation time. It then determines the current
     * frame to display based on the accumulated animation time and the duration
     * of each frame. If the animation reaches its end and looping is enabled,
     * it resets to the beginning.
     */
    public synchronized void update() {
        if (!isActive) {
            return;
        }

        long currTime = System.currentTimeMillis();
        long elapsedTime = currTime - startTime;
        startTime = currTime;

        if (frames.size() > 1) {
            animTime += elapsedTime;
            if (animTime >= totalDuration) {
                if (loop) {
                    animTime %= totalDuration;
                    currFrameIndex = 0;
                } else {
                    isActive = false;
                    return;
                }
            }

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;
            }
        }
    }

    /**
     * Gets the current image (frame) of the animation.
     *
     * @return The current {@link Image} of the animation, or {@code null} if
     * the animation has no frames.
     */
    public synchronized Image getImage() {
        if (frames.isEmpty()) {
            return null;
        } else {
            return getFrame(currFrameIndex).image;
        }
    }

    /**
     * Returns the total number of frames in the animation.
     *
     * @return The number of frames.
     */
    public int getNumFrames() {
        return frames.size();
    }

    /**
     * Checks if the animation is currently active (running).
     *
     * @return {@code true} if the animation is active, {@code false} otherwise.
     */
    public boolean isStillActive() {
        return isActive;
    }

    // ----- INNER CLASS -----
    /**
     * An inner class representing a single frame of the animation. It stores
     * the image of the frame and the time at which this frame should end during
     * the animation sequence.
     */
    private static class AnimFrame {

        /**
         * The image of the animation frame.
         */
        Image image;

        /**
         * The end time (cumulative duration) of this frame in the animation
         * sequence.
         */
        long endTime;

        /**
         * Constructs an {@code AnimFrame} with the specified image and end
         * time.
         *
         * @param image The {@link Image} of the frame.
         * @param endTime The cumulative duration up to and including this
         * frame.
         */
        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }

    /**
     * Returns the {@link AnimFrame} at the specified index in the frames list.
     *
     * @param i The index of the frame to retrieve.
     * @return The {@link AnimFrame} at the given index.
     */
    private AnimFrame getFrame(int i) {
        return frames.get(i);
    }
}

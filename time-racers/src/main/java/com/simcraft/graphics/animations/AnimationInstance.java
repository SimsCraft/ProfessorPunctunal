package com.simcraft.graphics.animations;

import java.awt.image.BufferedImage;
import java.util.Objects;

import com.rikuthin.interfaces.Updateable;

/**
 * Represents an individual animation instance for an entity. Each instance has
 * its own playback state while sharing the same {@link AnimationTemplate}.
 */
public class AnimationInstance implements Updateable {

    // ----- INSTANCE VARIABLES -----
    /**
     * Reference to the shared {@link AnimationTemplate}.
     */
    private AnimationTemplate template;
    /**
     * Index of the current frame being displayed.
     */
    private int currentFrameIndex;
    /**
     * Time elapsed since the current frame was first displayed (in
     * milliseconds).
     */
    private long elapsedFrameTime;
    /**
     * Last recorded update time (in milliseconds).
     */
    private long lastUpdateTime;
    /**
     * Indicates if the animation is currently playing. Defaults to
     * {@code false}.
     */
    private boolean isPlaying;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs an {@link AnimationInstance} based on a shared
     * {@link AnimationTemplate}.
     *
     * @param template The shared animation template.
     */
    public AnimationInstance(AnimationTemplate template) {
        setTemplate(template);
    }

    // ----- GETTERS -----
    /**
     * Retrieves the current frame image for rendering.
     *
     * @return The current animation frame as a {@link BufferedImage}.
     */
    public BufferedImage getCurrentFrameImage() {
        return template.getFrames().get(currentFrameIndex).getImage();
    }

    /**
     * Retrieves the reference to the shared {@link AnimationTemplate}.
     *
     * @return The animation template.
     */
    public AnimationTemplate getTemplate() {
        return template;
    }

    /**
     * Retrieves the index of the current frame.
     *
     * @return the current frame's index.
     */
    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    /**
     * Retrieves the time elapsed since the current frame was first displayed.
     *
     * @return The elapsed display time of the current frame.
     */
    public long getElapsedFrameTime() {
        return elapsedFrameTime;
    }

    /**
     * Retrieves the last recorded time the update() method was called (in milliseconds).
     *
     * @return The last recorded update time.
     */
    public boolean getLastUpdateTime() {
        return isPlaying;
    }

    // ----- SETTERS -----
    /**
     * Sets a new {@AnimationTemplate} and initializes the animation.
     * 
     * @param template
     */
    public final void setTemplate(final AnimationTemplate template) {
        this.template = template;
        init();
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Initialises values in preparation for animation playback from the beginning.
     */
    public final void init() {
        currentFrameIndex = 0;
        elapsedFrameTime = 0;
        lastUpdateTime = System.currentTimeMillis();
        isPlaying = false;
    }
    
    /**
     * Starts the animation playback.
     */
    public void start() {
        isPlaying = true;
    }

    /**
     * Stops the animation playback.
     */
    public void stop() {
        isPlaying = false;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates the animation frame based on elapsed time. Should be called in
     * the game loop.
     */
    @Override
    public void update() {
        if (!isPlaying || template.getFrames().isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        elapsedFrameTime += currentTime - lastUpdateTime;
        lastUpdateTime = currentTime;

        if (elapsedFrameTime >= template.getFrames().get(currentFrameIndex).getDisplayDurationMs()) {
            elapsedFrameTime = 0;
            nextFrame();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        AnimationInstance that = (AnimationInstance) obj;
        return Objects.equals(template, that.getTemplate())
                && currentFrameIndex == that.getCurrentFrameIndex()
                && elapsedFrameTime == that.getElapsedFrameTime()
                && isPlaying == that.getLastUpdateTime();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                template,
                currentFrameIndex,
                elapsedFrameTime,
                isPlaying
        );
    }

    // ----- HELPER METHODS -----
    /**
     * Advances to the next animation frame.
     */
    private void nextFrame() {
        if (currentFrameIndex >= template.getFrames().size() - 1) {
            if (template.isLooping()) {
                currentFrameIndex = 0;
            } else {
                stop();
            }
        } else {
            currentFrameIndex++;
        }
    }
}

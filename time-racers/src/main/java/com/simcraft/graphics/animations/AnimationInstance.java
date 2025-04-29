package com.simcraft.graphics.animations;

import java.awt.image.BufferedImage;
import java.util.Objects;

import com.simcraft.interfaces.Updateable;

/**
 * Represents an individual animation instance for an entity. Each instance has
 * its own playback state while sharing the same {@link AnimationTemplate}. This
 * allows multiple entities to use the same animation data but play it
 * independently.
 */
public class AnimationInstance implements Updateable {

    // ----- INSTANCE VARIABLES -----
    /**
     * Reference to the shared {@link AnimationTemplate} that defines the
     * sequence of frames and their durations.
     */
    private AnimationTemplate template;

    /**
     * Index of the current frame being displayed within the animation
     * template's frame list.
     */
    private int currentFrameIndex;

    /**
     * Time elapsed in milliseconds since the current frame started being
     * displayed. This is used to determine when to advance to the next frame.
     */
    private long elapsedFrameTime;

    /**
     * The last recorded time in milliseconds when the {@link #update()} method
     * was called. This is used to calculate the elapsed time since the last
     * update.
     */
    private long lastUpdateTime;

    /**
     * Indicates whether the animation is currently playing. If {@code true},
     * the animation will advance frames in the {@link #update()} method.
     * Defaults to {@code false}.
     */
    private boolean isPlaying;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs an {@code AnimationInstance} based on a shared
     * {@link AnimationTemplate}. This instance will use the frames and timing
     * defined in the provided template.
     *
     * @param template The shared animation template containing the animation
     * data. Must not be {@code null}.
     * @throws NullPointerException If the provided template is {@code null}.
     */
    public AnimationInstance(AnimationTemplate template) {
        setTemplate(template);
    }

    // ----- GETTERS -----
    /**
     * Retrieves the current frame image for rendering based on the current
     * frame index in the shared {@link AnimationTemplate}.
     *
     * @return The current animation frame as a {@link BufferedImage}. Returns
     * {@code null} if the template is {@code null} or has no frames.
     */
    public BufferedImage getCurrentFrameImage() {
        return (template != null && !template.getFrames().isEmpty())
                ? template.getFrames().get(currentFrameIndex).getImage()
                : null;
    }

    /**
     * Retrieves the reference to the shared {@link AnimationTemplate} used by
     * this animation instance.
     *
     * @return The animation template.
     */
    public AnimationTemplate getTemplate() {
        return template;
    }

    /**
     * Retrieves the index of the current frame being displayed. This index
     * corresponds to the position of the frame in the
     * {@link AnimationTemplate}'s frame list.
     *
     * @return The current frame's index.
     */
    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }

    /**
     * Retrieves the time elapsed in milliseconds since the current frame
     * started being displayed.
     *
     * @return The elapsed display time of the current frame.
     */
    public long getElapsedFrameTime() {
        return elapsedFrameTime;
    }

    /**
     * Retrieves the current playing state of the animation.
     *
     * @return {@code true} if the animation is currently playing, {@code false}
     * otherwise.
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    // ----- SETTERS -----
    /**
     * Sets a new {@link AnimationTemplate} for this instance and initializes
     * the animation playback state to the beginning.
     *
     * @param template The new animation template to use. Must not be
     * {@code null}.
     * @throws NullPointerException If the provided template is {@code null}.
     */
    public final void setTemplate(final AnimationTemplate template) {
        if (template == null) {
            throw new NullPointerException("Animation template cannot be null.");
        }
        this.template = template;
        init();
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Initializes the animation playback state to the beginning. This sets the
     * current frame index to 0, resets the elapsed frame time, records the
     * initial update time, and stops the animation.
     */
    public final void init() {
        currentFrameIndex = 0;
        elapsedFrameTime = 0;
        lastUpdateTime = System.currentTimeMillis();
        isPlaying = false;
    }

    /**
     * Starts the animation playback. The {@link #update()} method will now
     * advance the animation frames based on the elapsed time.
     */
    public void start() {
        isPlaying = true;
    }

    /**
     * Stops the animation playback. The {@link #update()} method will no longer
     * advance the animation frames.
     */
    public void stop() {
        isPlaying = false;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates the animation frame based on the time elapsed since the last
     * update. This method should be called periodically (e.g., in the game
     * loop). It advances to the next frame if the elapsed time for the current
     * frame exceeds its display duration.
     */
    @Override
    public void update() {
        if (!isPlaying || template == null || template.getFrames().isEmpty()) {
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

    /**
     * Compares this {@code AnimationInstance} to another object for equality.
     * Two {@code AnimationInstance} objects are considered equal if they have
     * the same {@link AnimationTemplate}, current frame index, elapsed frame
     * time, and playing state.
     *
     * @param obj The object to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AnimationInstance that = (AnimationInstance) obj;
        return Objects.equals(template, that.template)
                && currentFrameIndex == that.currentFrameIndex
                && elapsedFrameTime == that.elapsedFrameTime
                && isPlaying == that.isPlaying;
    }

    /**
     * Returns the hash code for this {@code AnimationInstance}. The hash code
     * is based on the {@link AnimationTemplate}, current frame index, elapsed
     * frame time, and playing state.
     *
     * @return The hash code for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(template, currentFrameIndex, elapsedFrameTime, isPlaying);
    }

    // ----- HELPER METHODS -----
    /**
     * Advances to the next animation frame. If the current frame is the last
     * frame and the animation template is set to loop, it will go back to the
     * first frame. Otherwise, it will stop the animation.
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

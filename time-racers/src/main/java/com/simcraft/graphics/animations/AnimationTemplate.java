package com.simcraft.graphics.animations;

import java.util.List;
import java.util.Objects;

/**
 * Represents a reusable animation template that defines a sequence of
 * {@link AnimationFrame} objects and whether the animation should loop.
 * Instances of this class are intended to be shared among multiple entities to
 * efficiently manage animation data.
 */
public class AnimationTemplate {

    // ----- INSTANCE VARIABLES -----
    /**
     * The ordered list of {@link AnimationFrame} objects that constitute the
     * animation sequence.
     */
    private final List<AnimationFrame> frames;

    /**
     * A boolean flag indicating whether the animation should loop back to the
     * first frame after the last frame has been displayed.
     */
    private final boolean isLooping;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs an {@code AnimationTemplate} with the specified list of
     * animation frames and looping behavior.
     *
     * @param frames The {@link List} of {@link AnimationFrame} objects that
     * make up the animation sequence. Must not be {@code null} or empty.
     * @param isLooping {@code true} if the animation should loop indefinitely
     * after reaching the last frame, {@code false} otherwise.
     * @throws IllegalArgumentException If the provided list of frames is
     * {@code null} or empty.
     */
    public AnimationTemplate(List<AnimationFrame> frames, boolean isLooping) {
        if (frames == null || frames.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "%s: Cannot create a template without any frames.",
                    this.getClass().getName()
            ));
        }

        this.frames = frames;
        this.isLooping = isLooping;
    }

    // ----- GETTERS -----
    /**
     * Returns the ordered list of {@link AnimationFrame} objects in this
     * template.
     *
     * @return The {@link List} of {@link AnimationFrame} objects.
     */
    public List<AnimationFrame> getFrames() {
        return frames;
    }

    /**
     * Returns whether this animation template is set to loop.
     *
     * @return {@code true} if the animation should loop, {@code false}
     * otherwise.
     */
    public boolean isLooping() {
        return isLooping;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Compares this {@code AnimationTemplate} to another object for equality.
     * Two {@code AnimationTemplate} objects are considered equal if they
     * contain the same list of frames in the same order and have the same
     * looping behavior.
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
        AnimationTemplate that = (AnimationTemplate) obj;
        return isLooping == that.isLooping && Objects.equals(frames, that.frames);
    }

    /**
     * Returns the hash code for this {@code AnimationTemplate}. The hash code
     * is based on the list of frames and the looping behavior.
     *
     * @return The hash code for this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(frames, isLooping);
    }
}

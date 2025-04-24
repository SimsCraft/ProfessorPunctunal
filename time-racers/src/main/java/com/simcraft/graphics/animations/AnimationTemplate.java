package com.simcraft.graphics.animations;

import java.util.List;
import java.util.Objects;

/**
 * Represents a reusable animation template containing a sequence of frames.
 * This class is shared among multiple entities, ensuring memory efficiency.
 */
public class AnimationTemplate {

    // ----- INSTANCE VARIABLES -----
    private final List<AnimationFrame> frames;
    private final boolean isLooping;

    /**
     * Constructs an AnimationTemplate with a list of frames and a looping flag.
     *
     * @param frames List of animation frames.
     * @param isLooping True if the animation should loop, false otherwise.
     */
    public AnimationTemplate(List<AnimationFrame> frames, boolean isLooping) {
        if (frames == null || frames.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                    "%s: Cannot create a without any frames.",
                    this.getClass().getName()
            ));
        }

        this.frames = frames;
        this.isLooping = isLooping;
    }

    // ----- GETTERS -----
    /**
     * Returns the list of animation frames.
     *
     * @return List of AnimationFrame objects.
     */
    public List<AnimationFrame> getFrames() {
        return frames;
    }

    /**
     * Checks if the animation is set to loop.
     *
     * @return True if looping, false otherwise.
     */
    public boolean isLooping() {
        return isLooping;
    }

    // ----- OVERRIDDEN METHODS -----
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        AnimationTemplate that = (AnimationTemplate) obj;
        return Objects.equals(frames, that.getFrames()) && isLooping == that.isLooping();
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                frames,
                isLooping
        );
    }
}

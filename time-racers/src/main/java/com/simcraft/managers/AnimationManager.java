package com.simcraft.managers;

import java.util.HashMap;
import java.util.Map;

import com.simcraft.graphics.animations.AnimationTemplate;

/**
 * Singleton manager responsible for storing and retrieving shared animation
 * templates. This ensures that animation data is loaded only once and can be
 * efficiently reused across multiple game entities, optimizing resource usage
 * and potentially improving performance.
 */
public class AnimationManager {

    /**
     * The single instance of the {@code AnimationManager} class, enforcing
     * the singleton pattern. This instance is lazily initialized when
     * {@link #getInstance()} is first called.
     */
    private static final AnimationManager INSTANCE = new AnimationManager();

    /**
     * A static {@code Map} that stores animation templates. The keys of the
     * map are unique identifiers ({@code String}) for each animation, and the
     * values are the corresponding {@link AnimationTemplate} objects.
     */
    private static final Map<String, AnimationTemplate> animations = new HashMap<>();

    // ----- CONSTRUCTORS ------
    /**
     * Private constructor to enforce the singleton pattern. This prevents
     * direct instantiation of the {@code AnimationManager} from outside
     * the class.
     */
    private AnimationManager() {
        // Initialization logic for the AnimationManager, if any, would go here.
    }

    // ----- GETTERS -----
    /**
     * Retrieves the singleton instance of the {@link AnimationManager}. This
     * is the standard way to access the manager throughout the game.
     *
     * @return The unique {@link AnimationManager} instance.
     */
    public static AnimationManager getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves an animation template based on its unique key.
     *
     * @param key The {@code String} that uniquely identifies the desired
     * animation template.
     * @return The corresponding {@link AnimationTemplate} object if found in
     * the manager; otherwise, {@code null} is returned.
     */
    public AnimationTemplate getAnimation(String key) {
        return animations.get(key);
    }

    /**
     * Retrieves an unmodifiable view of all the animation templates currently
     * stored in the manager. This allows access to all animations without
     * the risk of accidentally modifying the internal map.
     *
     * @return An unmodifiable {@code Map} where the keys are animation
     * identifiers and the values are the corresponding
     * {@link AnimationTemplate} objects.
     */
    public Map<String, AnimationTemplate> getAllAnimations() {
        return Map.copyOf(animations); // Return an unmodifiable copy
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Adds a new animation template to the manager, associating it with a
     * unique key. If an animation template with the given key already exists,
     * it is not replaced, ensuring that the first loaded animation for a key
     * is retained.
     *
     * @param key       The {@code String} that will serve as the unique
     * identifier for the animation template.
     * @param animation The {@link AnimationTemplate} object to be stored and
     * managed.
     * @throws NullPointerException If either the {@code key} or the
     * {@code animation} is {@code null}.
     */
    public void addAnimation(String key, AnimationTemplate animation) {
        if (key == null) {
            throw new NullPointerException("Animation key cannot be null.");
        }
        if (animation == null) {
            throw new NullPointerException("Animation template cannot be null.");
        }
        animations.putIfAbsent(key, animation);
    }
}
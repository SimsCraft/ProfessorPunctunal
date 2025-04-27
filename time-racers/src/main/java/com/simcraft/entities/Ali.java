package com.simcraft.entities;

import java.awt.Point;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

import com.simcraft.entities.enemies.Enemy;
import com.simcraft.graphics.effects.sprite_effects.HitFlashEffect;

/**
 * Represents the main playable character, Mr. Ali. Extends the
 * {@link MobileEntity} class and includes functionality for a hit flash effect.
 */
public class Ali extends MobileEntity {

    // ----- INSTANCE VARIABLES -----
    /**
     * The visual effect displayed when Ali is hit.
     */
    private HitFlashEffect hitFlashEffect;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs an {@code Ali} object using the provided {@link AliBuilder}.
     * Initializes the animation keys and sets the initial animation and speed.
     * Also initializes the {@link HitFlashEffect} for Ali.
     *
     * @param builder The {@code AliBuilder} containing the properties of the
     * Ali object.
     */
    public Ali(AliBuilder builder) {
        super(builder);

        HashSet<String> aliAnimationKeys = Stream.of(
                "ali_walk_down",
                "ali_walk_left",
                "ali_walk_right",
                "ali_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(aliAnimationKeys);

        // Temporary initial animation; will be updated dynamically during movement
        setAnimation("ali_walk_down");

        setSpeed(6); // Fast (he's in a hurry)

        this.hitFlashEffect = new HitFlashEffect(this, 500);
    }

    // ----- GETTERS -----
    /**
     * Returns the current {@link HitFlashEffect} associated with Ali.
     *
     * @return The {@code HitFlashEffect} instance.
     */
    public HitFlashEffect getHitFlashEffect() {
        return hitFlashEffect;
    }

    // ----- SETTERS -----
    /**
     * Sets the {@link HitFlashEffect} for Ali. This can be used to provide a
     * custom flash effect.
     *
     * @param hitFlashEffect The new {@code HitFlashEffect} to set.
     */
    public void setHitFlashEffect(HitFlashEffect hitFlashEffect) {
        this.hitFlashEffect = hitFlashEffect;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Resets Ali's position to a predefined starting point.
     */
    public void resetPosition() {
        setPosition(new Point(350, 550));
    }

    /**
     * Starts the hit flash visual effect.
     */
    public void startHitFlash() {
        if (this.hitFlashEffect != null) {
            System.out.println("Ali.startHitFlash() called at: " + System.currentTimeMillis());
            this.hitFlashEffect.startEffect(); // Always call startEffect to reset the timer
        }
    }

    /**
     * Updates the hit flash effect, progressing its animation based on the
     * elapsed time. This should be called in the game's update loop.
     */
    public void updateHitFlash() {
        if (hitFlashEffect != null && hitFlashEffect.isEffectActive()) {
            hitFlashEffect.updateEffect();
        }
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Moves Ali based on the current velocity and then corrects his position to
     * ensure they remain within the game boundaries.
     */
    @Override
    public void move() {
        super.move();
        correctPosition();
    }

    /**
     * Extends {@link MobileEntity#update} to manage Ali's hit flash effect when
     * colliding with an {@link Enemy}.
     */
    @Override
    public void update() {
        super.update();
        updateHitFlash();
    }

    /**
     * Compares this Ali to another object for equality.
     *
     * @param obj The {@link Object} to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Ali other = (Ali) obj;
        return super.equals(other) && Objects.equals(hitFlashEffect, other.getHitFlashEffect());
    }

    /**
     * Returns a hash code for this Ali.
     *
     * @return The hash code of the Ali.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                hitFlashEffect
        );
    }

    // ----- BUILDER PATTERN -----
    /**
     * The {@code AliBuilder} class provides a fluent API for constructing an
     * {@link Ali} object. It extends {@link MobileEntityBuilder} and allows
     * setting properties specific to Ali.
     */
    public static class AliBuilder extends MobileEntityBuilder<AliBuilder> {

        // ------ CONSTRUCTORS -----
        /**
         * Constructs an {@code AliBuilder} with the specified {@link JPanel} as
         * the parent container for the entity.
         *
         * @param panel The {@code JPanel} that will contain Ali.
         */
        public AliBuilder(final JPanel panel) {
            super(panel);
        }

        // ----- BUSINESS LOGIC METHODS ----
        /**
         * Builds and returns a new {@link Ali} object with the properties
         * configured in this builder.
         *
         * @return A new {@code Ali} instance.
         */
        public Ali build() {
            return new Ali(this);
        }
    }
}

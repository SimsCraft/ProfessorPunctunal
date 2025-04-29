package com.simcraft.entities;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

import com.simcraft.entities.enemies.Enemy;
import com.simcraft.graphics.effects.sprite_effects.HitFlashEffect;
import com.simcraft.managers.GameManager;

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

    // PATCH: Add world coordinates
    protected double worldX;
    protected double worldY;

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

        this.sprite = getCurrentSprite();

        setSpeed(6); // Fast (he's in a hurry)

        this.hitFlashEffect = new HitFlashEffect(this, 500);

        // PATCH: Initialize world position
        this.worldX = builder.startX;
        this.worldY = builder.startY;
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
        setWorldPosition(350, 550);
    }

    public void setWorldPosition(double x, double y) {
        this.worldX = x;
        this.worldY = y;
    }

    public double getWorldX() {
        return worldX;
    }

    public double getWorldY() {
        return worldY;
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
        updateScreenPosition();
    }

    public void updateScreenPosition() {
        // PATCH: Calculate screen position based on scroll offset
        int scrollOffset = (int) GameManager.getInstance().getGamePanel().getScrollOffset();
        position.x = (int) (worldX - scrollOffset);
        position.y = (int) (worldY);
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

    @Override
    public void render(Graphics2D g2d) {
        BufferedImage currentSprite = getCurrentSprite();
        if (currentSprite == null) {
            return;
        }
        int width = (int) (currentSprite.getWidth() * getScale());
        int height = (int) (currentSprite.getHeight() * getScale());
        g2d.drawImage(currentSprite, getX(), getY(), width, height, null);
    }

    // ----- BUILDER PATTERN -----
    /**
     * The {@code AliBuilder} class provides a fluent API for constructing an
     * {@link Ali} object. It extends {@link MobileEntityBuilder} and allows
     * setting properties specific to Ali.
     */
    public static class AliBuilder extends MobileEntityBuilder<AliBuilder> {

        // PATCH: Add start positions
        private int startX = 0;
        private int startY = 0;

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

        public AliBuilder startPosition(int x, int y) {
            this.startX = x;
            this.startY = y;
            return this;
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

    public boolean isJumping() {
        return jumping; // Or however you track jumps
    }

    public void updateScreenPosition(double scrollOffset) {
        position.x = (int) (worldX - scrollOffset);
        position.y = (int) worldY;
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, (int) (sprite.getWidth() * getScale()), (int) (sprite.getHeight() * getScale()));
    }

}

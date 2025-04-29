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

    /**
     * Flag indicating if Ali is currently jumping.
     */
    private boolean jumping = false; // Assuming a basic jumping implementation

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

    /**
     * Returns wither Mr. Ali is currently jumping.
     * 
     * @return {@code true if he's jumping}, {@code false otherwise}
     */
    public boolean isJumping() {
        return jumping;
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

    /**
     * Sets the world X-coordinate of Ali.
     *
     * @param worldX The new world X-coordinate.
     */
    public void setWorldX(double worldX) {
        this.worldX = worldX;
    }

    /**
     * Sets the world Y-coordinate of Ali.
     *
     * @param worldY The new world Y-coordinate.
     */
    public void setWorldY(double worldY) {
        this.worldY = worldY;
    }

    /**
     * Sets whether Ali is currently jumping.
     *
     * @param jumping {@code true} if Ali is jumping, {@code false} otherwise.
     */
    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Resets Ali's position to a predefined starting point in the game world.
     * This method updates Ali's world coordinates.
     *
     * @param x The starting world X-coordinate.
     * @param y The starting world Y-coordinate.
     */
    public void resetPosition(double x, double y) {
        setWorldPosition(x, y);
    }

    /**
     * Sets Ali's position in the game world. This updates Ali's world
     * coordinates.
     *
     * @param x The new world X-coordinate.
     * @param y The new world Y-coordinate.
     */
    public void setWorldPosition(double x, double y) {
        this.worldX = x;
        this.worldY = y;
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
     * Moves Ali based on the current velocity and then updates his screen
     * position based on the game's scroll offset.
     */
    @Override
    public void move() {
        super.move();
        updateScreenPosition();
    }

    /**
     * Updates Ali's screen position based on the current scroll offset of the
     * game panel. This ensures Ali is rendered correctly relative to the game
     * world.
     */
    public void updateScreenPosition() {
        // PATCH: Calculate screen position based on scroll offset
        if (GameManager.getInstance().getGamePanel() != null) {
            int scrollOffset = (int) GameManager.getInstance().getGamePanel().getScrollOffset();
            position.x = (int) (worldX - scrollOffset);
            position.y = (int) (worldY);
        }
    }

    /**
     * Extends {@link MobileEntity#update} to manage Ali's hit flash effect.
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
        if (!(obj instanceof Ali)) {
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

    /**
     * Renders Ali on the provided graphics context.
     *
     * @param g2d The graphics context to draw on.
     */
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

    /**
     * Returns the bounding rectangle of Ali for collision detection, based on
     * his current screen position and sprite dimensions.
     *
     * @return A {@link Rectangle} representing Ali's bounds on the screen.
     */
    public Rectangle getBounds() {
        BufferedImage currentSprite = getCurrentSprite();
        if (currentSprite == null) {
            return new Rectangle(getX(), getY(), 0, 0); // Or some default size
        }
        return new Rectangle(getX(), getY(), (int) (currentSprite.getWidth() * getScale()), (int) (currentSprite.getHeight() * getScale()));
    }

    // ----- STATIC BUILDER FOR ALI -----
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

        /**
         * Sets the starting position of Ali in the game world.
         *
         * @param x The starting world X-coordinate.
         * @param y The starting world Y-coordinate.
         * @return The builder instance.
         */
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
}

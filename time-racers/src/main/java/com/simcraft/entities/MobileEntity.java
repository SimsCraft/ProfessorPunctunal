package com.simcraft.entities;

import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JPanel;

import com.simcraft.managers.GameManager;

/**
 * Represents a mobile entity in the game that can move around the game world.
 * Extends the {@link Entity} class to add movement-related behavior.
 */
public abstract class MobileEntity extends Entity {

    // ----- INSTANCE VARIABLES -----
    /**
     * The horizontal velocity of the entity, in pixels per frame.
     * <p>
     * Velocity represents the rate of change of the entity's position in the
     * horizontal direction (x-axis) over time. It is a vector quantity that
     * defines both the speed and the direction of movement:
     * <ul>
     * <li>A negative value indicates movement towards the left of the
     * screen.</li>
     * <li>A positive value indicates movement towards the right of the
     * screen.</li>
     * </ul>
     * The magnitude of this value determines how fast the entity moves
     * horizontally in each frame, while the sign (positive or negative)
     * determines the direction of movement.
     * <p>
     * The value of this variable is dynamically influenced by the entity’s
     * speed and any external forces (e.g., player input, game mechanics).
     */
    protected double velocityX;

    /**
     * The vertical velocity of the entity, in pixels per frame.
     * <p>
     * Similar to horizontal velocity, vertical velocity represents the rate of
     * change of the entity's position in the vertical direction (y-axis) over
     * time. This is a vector quantity that determines the direction and
     * magnitude of vertical movement:
     * <ul>
     * <li>A negative value indicates movement upwards (toward the top of the
     * screen).</li>
     * <li>A positive value indicates movement downwards (toward the bottom of
     * the screen).</li>
     * </ul>
     * The magnitude of the vertical velocity determines how fast the entity
     * moves vertically in each frame, while the sign determines the direction.
     * <p>
     * Just like horizontal velocity, vertical velocity is influenced by speed
     * and external factors, such as gravity or player input.
     */
    protected double velocityY;

    /**
     * The overall movement speed of the entity, in pixels per frame.
     * <p>
     * Speed is a scalar value that defines the rate at which the entity moves,
     * but unlike velocity, it only specifies the magnitude of the movement, not
     * the direction. It is a non-negative value that represents how many pixels
     * the entity moves per frame, independent of direction. Speed is a key
     * factor in controlling how quickly the entity responds to movement
     * commands, and it affects both horizontal (x-axis) and vertical (y-axis)
     * velocities.
     * <p>
     * The entity's speed can be adjusted dynamically depending on various
     * factors, such as player input (walking or sprinting), game mechanics
     * (buffs, debuffs), or environmental factors (wind, gravity). This speed
     * value is used to calculate both `velocityX` and `velocityY`, typically by
     * multiplying the speed by a factor based on the desired direction of
     * movement.
     */
    protected double speed;

    /**
     * The scaling factor for rendering the entity's sprite. Default is 1.0.
     */
    private double scale = 1.0;

    /**
     * Flag indicating if movement is restricted to the horizontal axis. Default
     * is false (free movement).
     */
    private boolean horizontalOnly = false;

    /**
     * The baseline Y-coordinate for entities with horizontal-only movement
     * (e.g., for jumping).
     */
    private int yOrigin;

    /**
     * Flag indicating if the entity is currently in a jumping state. Default is
     * false.
     */
    protected boolean jumping = false;

    /**
     * The current vertical velocity during a jump.
     */
    private double jumpVelocity = 0;

    /**
     * The acceleration due to gravity affecting jumps.
     */
    private final double gravity = 0.6;

    /**
     * The actual (potentially sub-pixel) x-coordinate of the entity on the
     * screen.
     */
    protected double currentX;

    /**
     * The actual (potentially sub-pixel) y-coordinate of the entity on the
     * screen.
     */
    protected double currentY;

    /**
     * The current sprite image of the entity.
     */
    public BufferedImage sprite;

    /**
     * The entity's x-coordinate in the game world.
     */
    protected double worldX;

    /**
     * The entity's y-coordinate in the game world.
     */
    protected double worldY;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor used by the builder pattern to instantiate a
     * MobileEntity.
     *
     * @param builder The builder used to construct the entity. Inherits
     * properties from {@link EntityBuilder}.
     */
    protected MobileEntity(MobileEntityBuilder<?> builder) {
        super(builder);
        this.currentX = position.x;
        this.currentY = position.y;
    }

    // ----- GETTERS -----
    /**
     * Returns the current horizontal velocity of the entity.
     *
     * @return The horizontal velocity in pixels per frame.
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Returns the current vertical velocity of the entity.
     *
     * @return The vertical velocity in pixels per frame.
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Returns the current overall movement speed of the entity.
     *
     * @return The speed in pixels per frame.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Returns the current scaling factor for the entity's sprite.
     *
     * @return The scale factor.
     */
    public double getScale() {
        return scale;
    }

    /**
     * Returns whether the entity's movement is restricted to the horizontal
     * axis.
     *
     * @return {@code true} if movement is horizontal-only, {@code false}
     * otherwise.
     */
    public boolean isHorizontalOnly() {
        return horizontalOnly;
    }

    /**
     * Returns the baseline Y-coordinate for horizontal-only movement.
     *
     * @return The Y-origin.
     */
    public double getYOrigin() {
        return yOrigin;
    }

    /**
     * Returns the current screen x-coordinate of the entity (integer part).
     *
     * @return The integer x-coordinate.
     */
    public int getCurrentX() {
        return (int) currentX;
    }

    /**
     * Returns the current screen y-coordinate of the entity (integer part).
     *
     * @return The integer y-coordinate.
     */
    public int getCurrentY() {
        return (int) currentY;
    }

    /**
     * Returns the entity's current x-coordinate in the game world.
     *
     * @return The world x-coordinate.
     */
    public double getWorldX() {
        return worldX;
    }

    /**
     * Returns the entity's current y-coordinate in the game world.
     *
     * @return The world y-coordinate.
     */
    public double getWorldY() {
        return worldY;
    }

    // ----- SETTERS -----
    /**
     * Sets the horizontal velocity of the entity.
     *
     * @param velocityX The new horizontal velocity in pixels per frame.
     */
    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    /**
     * Sets the vertical velocity of the entity.
     *
     * @param velocityY The new vertical velocity in pixels per frame.
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Sets the overall movement speed of the entity.
     *
     * @param speed The new speed in pixels per frame.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * Sets the scaling factor for the entity's sprite.
     *
     * @param scale The new scale factor.
     */
    public void setScale(double scale) {
        this.scale = scale;
    }

    /**
     * Sets whether the entity's movement should be restricted to the horizontal
     * axis.
     *
     * @param horizontalOnly {@code true} to restrict to horizontal movement,
     * {@code false} otherwise.
     */
    public void setHorizontalOnly(boolean horizontalOnly) {
        this.horizontalOnly = horizontalOnly;
    }

    /**
     * Sets the baseline Y-coordinate for horizontal-only movement.
     *
     * @param yOrigin The new Y-origin.
     */
    public void setYOrigin(int yOrigin) {
        this.yOrigin = yOrigin;
    }

    /**
     * Sets the screen x-coordinate of the entity.
     *
     * @param currentX The new x-coordinate.
     */
    public void setCurrentX(double currentX) {
        this.currentX = currentX;
    }

    /**
     * Sets the screen y-coordinate of the entity.
     *
     * @param currentY The new y-coordinate.
     */
    public void setCurrentY(double currentY) {
        this.currentY = currentY;
    }

    /**
     * Sets the entity's x-coordinate in the game world.
     *
     * @param worldX The new world x-coordinate.
     */
    public void setWorldX(double worldX) {
        this.worldX = worldX;
    }

    /**
     * Sets the entity's y-coordinate in the game world.
     *
     * @param worldY The new world y-coordinate.
     */
    public void setWorldY(double worldY) {
        this.worldY = worldY;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Initiates a jump for the entity if it is enabled for horizontal-only
     * movement and is not currently jumping.
     *
     * @param initialVelocity The initial upward velocity of the jump.
     */
    public void jump(double initialVelocity) {
        if (!jumping && horizontalOnly) {
            jumping = true;
            jumpVelocity = initialVelocity;
        }
    }

    /**
     * Updates the entity's screen position based on its world coordinates and
     * the game's current scroll offset.
     */
    public void updateScreenPosition() {
        if (GameManager.getInstance().getGamePanel() != null) {
            position.x = (int) (worldX - GameManager.getInstance().getGamePanel().getScrollOffset());
            position.y = (int) worldY;
            this.currentX = position.x;
            this.currentY = position.y;
        }
    }

    /**
     * Updates the entity's position based on its current velocities and handles
     * jumping mechanics if the entity is set to move horizontally only.
     */
    public void move() {
        worldX += velocityX;
        if (horizontalOnly) {
            if (jumping) {
                worldY -= jumpVelocity;
                jumpVelocity -= gravity;
                if (worldY >= yOrigin) {
                    worldY = yOrigin;
                    jumping = false;
                    jumpVelocity = 0;
                }
            }
        } else {
            worldY -= velocityY;
        }
        updateScreenPosition();
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Compares this entity to another object for equality.
     *
     * @param obj The {@link Object} to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     * Considers the horizontal and vertical velocities in addition to the
     * properties compared in the parent {@link Entity} class.
     * @see Entity#equals(Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MobileEntity)) {
            return false;
        }
        MobileEntity other = (MobileEntity) obj;
        return super.equals(other)
                && Double.compare(velocityX, other.getVelocityX()) == 0
                && Double.compare(velocityY, other.getVelocityY()) == 0;
    }

    /**
     * Returns a hash code for this entity.
     *
     * @return The hash code of the entity, incorporating the hash codes of the
     * velocities in addition to the properties hashed in the parent
     * {@link Entity} class.
     * @see Entity#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), velocityX, velocityY);
    }

    /**
     * Updates the state of the entity, including its position based on its
     * velocity. Also calls the {@link Entity#update()} method for base entity
     * updates.
     *
     * @see Entity#update()
     */
    @Override
    public void update() {
        super.update(); // Update animations and hitbox
        move();
        // Update hitbox position based on the current sprite's dimensions and the entity's position.
        setHitboxFromCurrentSprite();
    }

    // ----- BUILDER PATTERN -----
    /**
     * The MobileEntityBuilder class provides a fluent API for constructing a
     * {@link MobileEntity} object. Extends {@link EntityBuilder} to inherit
     * common entity properties.
     *
     * @param <T> The specific type of the builder, allowing for method chaining
     * in subclasses.
     */
    public static class MobileEntityBuilder<T extends MobileEntityBuilder<T>> extends EntityBuilder<T> {

        // ----- INSTANCE VARIABLES -----
        private double velocityX = 0;
        private double velocityY = 0;
        private double speed = 0;

        // ------ CONSTRUCTORS -----
        /**
         * Constructs a {@code MobileEntityBuilder} with the specified
         * {@link JPanel}.
         *
         * @param panel The {@code JPanel} where the entity will be rendered.
         * Inherited from {@link EntityBuilder}.
         * @throws IllegalArgumentException If the panel is null. Inherited from
         * {@link EntityBuilder}.
         * @see EntityBuilder#EntityBuilder(JPanel)
         */
        public MobileEntityBuilder(JPanel panel) {
            super(panel);
        }

        // ----- SETTERS -----
        /**
         * Sets the horizontal velocity of the entity.
         *
         * @param velocityX The new horizontal velocity in pixels per frame.
         * @return The builder instance.
         */
        public T velocityX(double velocityX) {
            this.velocityX = velocityX;
            return self();
        }

        /**
         * Sets the vertical velocity of the entity.
         *
         * @param velocityY The new vertical velocity in pixels per frame.
         * @return The builder instance.
         */
        public T velocityY(double velocityY) {
            this.velocityY = velocityY;
            return self();
        }

        /**
         * Sets the overall movement speed of the entity.
         *
         * @param speed The new speed in pixels per frame.
         * @return The builder instance.
         */
        public T speed(double speed) {
            this.speed = speed;
            return self();
        }
    }
}

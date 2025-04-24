package com.simcraft.entities;

import java.util.Objects;

import javax.swing.JPanel;

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

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor used by the builder pattern to instantiate a
     * MobileEntity.
     *
     * @param builder The builder used to construct the entity.
     */
    protected MobileEntity(MobileEntityBuilder<?> builder) {
        super(builder);
    }

    // ----- GETTERS -----
    /**
     * Returns the current horizontal velocity of the entity, in pixels per
     * frame.
     * <p>
     * Velocity represents the rate of change of the entity's position in the
     * horizontal direction (x-axis) over time. This value is a vector, meaning
     * it indicates both the direction and magnitude of movement:
     * <ul>
     * <li>A negative value moves the entity to the left of the screen.</li>
     * <li>A positive value moves the entity to the right of the screen.</li>
     * </ul>
     * The value of `velocityX` is influenced by the entity's speed and any
     * external forces (e.g., player input or in-game mechanics).
     *
     * @return The current horizontal velocity in pixels per frame.
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Returns the current vertical velocity of the entity, in pixels per frame.
     * <p>
     * Similar to `velocityX`, `velocityY` represents the rate of change of the
     * entity's position in the vertical direction (y-axis) over time. This
     * value is a vector, and it determines the direction and speed of vertical
     * movement:
     * <ul>
     * <li>A negative value moves the entity upwards (towards the top of the
     * screen).</li>
     * <li>A positive value moves the entity downwards (towards the bottom of
     * the screen).</li>
     * </ul>
     * The value of `velocityY` is determined by the entity's speed and other
     * game factors (e.g., gravity, player input).
     *
     * @return The current vertical velocity in pixels per frame.
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Returns the current overall movement speed of the entity, in pixels per
     * frame.
     * <p>
     * Speed is a scalar value, meaning it only represents the magnitude of
     * movement and not the direction. It defines how many pixels the entity
     * moves in one frame. The speed value is used to calculate both horizontal
     * (`velocityX`) and vertical (`velocityY`) velocities, with the direction
     * being determined separately. The entity's speed can be adjusted
     * dynamically, such as through player input (e.g., walking vs. sprinting)
     * or game mechanics.
     *
     * @return The current speed of the entity in pixels per frame.
     */
    public double getSpeed() {
        return speed;
    }

    // ----- SETTERS -----
    /**
     * Sets the horizontal velocity of the entity, in pixels per frame.
     * <p>
     * This method sets the rate of change of the entity's position on the
     * x-axis. It defines how fast the entity moves horizontally, while the
     * direction of movement is indicated by the sign of the value:
     * <ul>
     * <li>A negative value moves the entity left.</li>
     * <li>A positive value moves the entity right.</li>
     * </ul>
     * The value of `velocityX` is influenced by the entity's speed and any
     * other game logic.
     *
     * @param velocityX The new horizontal velocity in pixels per frame.
     */
    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    /**
     * Sets the vertical velocity of the entity, in pixels per frame.
     * <p>
     * This method sets the rate of change of the entity's position on the
     * y-axis. It determines how fast the entity moves vertically, while the
     * direction is indicated by the sign of the value:
     * <ul>
     * <li>A negative value moves the entity upwards.</li>
     * <li>A positive value moves the entity downwards.</li>
     * </ul>
     * The value of `velocityY` is influenced by the entity's speed and other
     * game factors.
     *
     * @param velocityY The new vertical velocity in pixels per frame.
     */
    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    /**
     * Sets the overall movement speed of the entity, in pixels per frame.
     * <p>
     * Speed is a scalar value that defines the magnitude of movement,
     * independent of direction. It is used to calculate the `velocityX` and
     * `velocityY` values by multiplying the speed with direction-specific
     * factors. The speed value can be adjusted depending on various factors
     * such as player actions (e.g., walking or sprinting), in-game power-ups,
     * or other gameplay mechanics.
     *
     * @param speed The new movement speed of the entity in pixels per frame.
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

// ---- BUSINESS LOGIC METHODS -----
    /**
     * Updates the entity's position based on its current velocities along the x
     * and y axes.
     * <p>
     * This method uses the `velocityX` and `velocityY` values to modify the
     * entity's position. The x-axis velocity is added to the entity's current
     * horizontal position, while the y-axis velocity is subtracted (because
     * screen coordinates typically have the y-axis inverted, with y increasing
     * downwards).
     * <p>
     * The entity's movement is based on its velocity, which is influenced by
     * its speed and any other factors like user input or environmental
     * conditions.
     */
    public void move() {
        position.x += velocityX;
        position.y -= velocityY; // Inverted for screen coordinates
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Compares this entity to another object for equality.
     *
     * @param obj The {@link Object} to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
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
     * @return The hash code of the entity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), velocityX, velocityY);
    }

    /**
     * Updates the state of the entity, including its position based on its
     * velocity.
     */
    @Override
    public void update() {
        super.update(); // Update animations and hitbox

        move();

        // Update hitbox position
        setHitboxFromCurrentSprite();
    }

    // ----- BUILDER PATTERN -----
    /**
     * The MobileEntityBuilder class provides a fluent API for constructing an
     * MobileEntity object.
     */
    public static class MobileEntityBuilder<T extends MobileEntityBuilder<T>> extends EntityBuilder<T> {

        // ----- INSTANCE VARIABLES -----
        private double velocityX = 0;
        private double velocityY = 0;
        private double speed = 0;

        // ------ CONSTRUCTORS -----
        public MobileEntityBuilder(JPanel panel) {
            super(panel);
        }

        // ----- SETTERS -----
        /**
         * Sets the horizontal velocity of the entity, in pixels per frame.
         * <p>
         * This method sets the rate of change of the entity's position on the
         * x-axis. It defines how fast the entity moves horizontally, while the
         * direction of movement is indicated by the sign of the value:
         * <ul>
         * <li>A negative value moves the entity left.</li>
         * <li>A positive value moves the entity right.</li>
         * </ul>
         * The value of `velocityX` is influenced by the entity's speed and any
         * other game logic.
         *
         * @param velocityX The new horizontal velocity in pixels per frame.
         * @return The builder instance.
         */
        public T velocityX(double velocityX) {
            this.velocityX = velocityX;
            return self();
        }

        /**
         * Sets the vertical velocity of the entity, in pixels per frame.
         * <p>
         * This method sets the rate of change of the entity's position on the
         * y-axis. It determines how fast the entity moves vertically, while the
         * direction is indicated by the sign of the value:
         * <ul>
         * <li>A negative value moves the entity upwards.</li>
         * <li>A positive value moves the entity downwards.</li>
         * </ul>
         * The value of `velocityY` is influenced by the entity's speed and
         * other game factors.
         *
         * @param velocityY The new vertical velocity in pixels per frame.
         * @return The builder instance.
         */
        public T velocityY(double velocityY) {
            this.velocityY = velocityY;
            return self();
        }

        /**
         * Sets the overall movement speed of the entity, in pixels per frame.
         * <p>
         * Speed is a scalar value that defines the magnitude of movement,
         * independent of direction. It is used to calculate the `velocityX` and
         * `velocityY` values by multiplying the speed with direction-specific
         * factors. The speed value can be adjusted depending on various factors
         * such as player actions (e.g., walking or sprinting), in-game
         * power-ups, or other gameplay mechanics.
         *
         * @param speed The new movement speed of the entity in pixels per
         * frame.
         * @return The builder instance.
         */
        public T speed(double speed) {
            this.speed = speed;
            return self();
        }
    }
}

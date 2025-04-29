package com.simcraft.entities.enemies;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Random;

import javax.swing.JPanel;

import com.simcraft.entities.Ali;
import com.simcraft.entities.MobileEntity;

/**
 * Represents an enemy in the game.
 * <p>
 * This class extends {@link MobileEntity} and includes functionality for
 * interacting with the player ({@link Ali}).
 */
public abstract class Enemy extends MobileEntity {

    /**
     * The duration (in milliseconds) an enemy can continuously attack before
     * needing to cool down.
     */
    protected long attackTimerMs;
    /**
     * The elapsed time (in milliseconds) of the current attack wave.
     */
    protected long elapsedAttackTimeMs;
    /**
     * The cooldown duration (in milliseconds) before the enemy can attack again
     * after an attack wave ends.
     */
    protected long attackCooldownMs;
    /**
     * The elapsed time (in milliseconds) since the enemy started cooling down.
     */
    protected long elapsedAttackCooldownMs;
    /**
     * The timestamp (in milliseconds) of the last update call.
     */
    protected long lastUpdateTime;
    /**
     * Internally used random number generator.
     */
    protected Random random;
    /**
     * The cooldown duration before the enemy can move again after a previous
     * movement opportunity (in game ticks).
     */
    protected int moveDelay;
    /**
     * Flag indicating if the enemy has recently collided with another entity.
     */
    protected boolean hasCollided;
    /**
     * How many seconds to remove from the game timer if {@link Ali} collides
     * with this enemy.
     */
    protected int timePenalty;
    /**
     * Whether the enemy is currently not performing any movement.
     */
    protected boolean isIdling;
    /**
     * The percentage chance (0.0 to 1.0) that the enemy will start idling if
     * left uninterrupted by the player.
     */
    protected double chanceToIdle;
    /**
     * How many milliseconds the enemy should remain idle for if the idling
     * condition is met and the player is not within attack range.
     */
    protected long idleDuration;
    /**
     * The distance (in pixels) within which the enemy will detect {@link Ali}
     * and potentially initiate an attack.
     */
    protected int detectionRadius;

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create an {@code Enemy} instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the enemy.
     */
    protected Enemy(EnemyBuilder<?> builder) {
        super(builder);
        this.sprite = getCurrentSprite();
        random = new Random();
        hasCollided = false;
        this.timePenalty = builder.timePenalty;
        this.isIdling = builder.isIdling;
    }

    // ---- GETTERS -----
    /**
     * Returns the cooldown duration (in game ticks) before the enemy can move
     * again after a previous movement opportunity.
     *
     * @return The movement delay.
     */
    public int getMoveDelay() {
        return moveDelay;
    }

    /**
     * Returns whether the enemy has recently collided with another entity.
     *
     * @return {@code true} if a collision occurred, {@code false} otherwise.
     */
    public boolean hasCollided() {
        return hasCollided;
    }

    /**
     * Returns whether the enemy is currently in an attacking state.
     *
     * @return {@code true} if attacking, {@code false} otherwise.
     */
    public boolean isAttacking() {
        // TODO Write attack check logic based on the specific enemy type
        return false;
    }

    /**
     * Gets the cooldown time in milliseconds before the enemy can initiate
     * another attack.
     *
     * @return The attack cooldown time in milliseconds.
     */
    public long getAttackCooldownMs() {
        return attackCooldownMs;
    }

    /**
     * Returns how many milliseconds of the attack cooldown have passed.
     *
     * @return The elapsed cooldown time in milliseconds.
     */
    public long getElapsedAttackCooldownMs() {
        return elapsedAttackCooldownMs;
    }

    /**
     * Returns the last time the enemy was updated in milliseconds.
     *
     * @return The timestamp of the last update.
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Returns how many seconds will be removed from the game timer if
     * {@link Ali} collides with this enemy.
     *
     * @return The time penalty in seconds.
     */
    public int getTimePenalty() {
        return timePenalty;
    }

    /**
     * Returns whether the enemy is currently not performing any movement.
     *
     * @return {@code true} if the enemy is idling, {@code false} otherwise.
     */
    public boolean isIdling() {
        return isIdling;
    }

    // ---- SETTERS -----
    /**
     * Sets the cooldown duration (in game ticks) before the enemy can move
     * again after a previous movement opportunity.
     *
     * @param moveDelay The movement delay.
     */
    public void setMoveDelay(final int moveDelay) {
        this.moveDelay = moveDelay;
    }

    /**
     * Sets whether the enemy has recently collided with another entity.
     *
     * @param hasCollided {@code true} if a collision occurred, {@code false}
     * otherwise.
     */
    public void setHasCollided(final boolean hasCollided) {
        this.hasCollided = hasCollided;
    }

    /**
     * Sets whether the enemy is attacking. The specific implementation of
     * attacking will depend on the enemy's behavior.
     *
     * @param isAttacking {@code true} if the enemy should start attacking,
     * {@code false} to stop.
     * @throws IllegalArgumentException If the attack state is invalid for the
     * enemy type.
     */
    public void setIsAttacking(final boolean isAttacking) throws IllegalArgumentException {
        // Implementation specific to the enemy type
    }

    /**
     * Sets the entity's attack cooldown duration in milliseconds. The minimum
     * value is 1 millisecond.
     *
     * @param attackCooldownMs The attack cooldown duration.
     */
    public void setAttackCooldownMs(final long attackCooldownMs) {
        this.attackCooldownMs = Math.max(attackCooldownMs, 1);
    }

    /**
     * Sets the coordinates on screen that the enemy should aim their attacks
     * towards. The specific targeting logic depends on the enemy type.
     *
     * @param target The {@link Point} representing the target coordinates.
     */
    public void setTarget(final Point target) {
        if (target == null) {
            return;
        }
        // Targeting logic specific to the enemy type
    }

    /**
     * Sets how many seconds will be removed from the game timer if {@link Ali}
     * collides with this enemy.
     *
     * @param timePenalty The time penalty in seconds.
     */
    public void setTimePenalty(final int timePenalty) {
        this.timePenalty = timePenalty;
    }

    /**
     * Sets whether the entity should currently be idle (i.e., stop moving).
     *
     * @param isIdling {@code true} if they should be idle, {@code false}
     * otherwise.
     */
    public void setIsIdling(final boolean isIdling) {
        this.isIdling = isIdling;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Checks whether the enemy is currently able to perform an attack. This
     * typically involves checking if the enemy is not on attack cooldown and
     * has attack time remaining in the current wave.
     *
     * @return {@code true} if the enemy can attack, {@code false} otherwise.
     */
    public boolean canAttack() {
        return !isOnAttackCooldown() && elapsedAttackTimeMs < attackTimerMs;
    }

    /**
     * Checks if the attack cooldown period is currently active.
     *
     * @return {@code true} if the attack cooldown has not expired,
     * {@code false} otherwise.
     */
    public boolean isOnAttackCooldown() {
        return elapsedAttackCooldownMs < attackCooldownMs;
    }

    /**
     * Initiates an attack if the enemy is allowed to do so. The specific attack
     * behavior is implemented by the concrete enemy subclasses. This method
     * also manages the attack timer and cooldown.
     */
    public void attack() {
        if (!canAttack()) {
            return;
        }

        long attackStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - attackStartTime < attackTimerMs) {
            updateAttackTimer();
            // Perform attack action specific to the enemy type
        }

        updateAttackCooldownTimer();
    }

    /**
     * Reverses the enemy's current movement direction.
     */
    public void reverseMovementDirection() {
        setVelocityX(-getVelocityX());
        setVelocityY(-getVelocityY());
    }

    /**
     * Sets the enemy's world position directly.
     *
     * @param x The new x-coordinate in the game world.
     * @param y The new y-coordinate in the game world.
     */
    public void setWorldPosition(double x, double y) {
        setWorldX(x);
        setWorldY(y);
    }

    /**
     * Returns the bounding rectangle of the enemy for collision detection. The
     * size of the rectangle is based on the current sprite and scale.
     *
     * @return A {@link Rectangle} representing the enemy's bounds.
     */
    public Rectangle getBounds() {
        BufferedImage currentSprite = getCurrentSprite();
        if (currentSprite == null) {
            return new Rectangle((int) getX(), (int) getY(), 32, 32); // Default small size
        }
        return new Rectangle((int) getX(), (int) getY(),
                (int) (currentSprite.getWidth() * getScale()),
                (int) (currentSprite.getHeight() * getScale()));
    }

    /**
     * Randomly sets the enemy's movement direction. Each component of the
     * velocity (x and y) is set to -speed, 0, or +speed with equal probability,
     * ensuring that the enemy will move in one of the eight cardinal or
     * diagonal directions.
     */
    public void setRandomDirection() {
        int[] directions = {-1, 0, 1};
        int vx;
        int vy;
        do {
            vx = directions[random.nextInt(3)];
            vy = directions[random.nextInt(3)];
        } while (vx == 0 && vy == 0); // Ensure the enemy is not stationary

        setVelocityX(vx * getSpeed());
        setVelocityY(vy * getSpeed());
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
        if (!(obj instanceof Enemy)) {
            return false;
        }
        Enemy other = (Enemy) obj;
        return super.equals(other)
                && moveDelay == other.moveDelay
                && hasCollided == other.hasCollided
                && isAttacking() == other.isAttacking()
                && attackCooldownMs == other.attackCooldownMs
                && elapsedAttackCooldownMs == other.elapsedAttackCooldownMs
                && lastUpdateTime == other.lastUpdateTime
                && timePenalty == other.timePenalty
                && isIdling == other.isIdling;
    }

    /**
     * Returns a hash code for this entity.
     *
     * @return The hash code of the entity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                moveDelay,
                hasCollided,
                isAttacking(),
                attackCooldownMs,
                elapsedAttackCooldownMs,
                lastUpdateTime,
                timePenalty,
                isIdling
        );
    }

    /**
     * Updates the entity's current position based on its velocity. Also
     * includes a chance for the enemy to randomly change direction and checks
     * for screen boundaries, reversing the direction if necessary.
     */
    @Override
    public void move() {
        if (moveDelay % 4 == 0) { // Control movement speed
            super.move(); // Applies screen-coordinates adjusted movement

            // Chance to change direction randomly
            if (random.nextInt(50) == 1) {
                setRandomDirection();
            }

            // Boundary check & direction reversal
            if (!isFullyWithinPanel()) {
                correctPosition();
                reverseMovementDirection();
            }
        }
        moveDelay++;
    }

    /**
     * Updates the state of the enemy, including any specific behaviors like
     * attacking or idling.
     */
    @Override
    public void update() {
        super.update();
        // lastUpdateTime = System.currentTimeMillis(); // Moved to specific update methods
        // attack(); // Attack logic should be triggered based on game state, not just update
    }

    /**
     * Ensures the enemy remains within the bounds of its containing panel. If
     * the enemy reaches a vertical boundary, its vertical velocity is reversed,
     * creating a bouncing effect.
     */
    @Override
    protected void correctPosition() {
        super.correctPosition(); // Use inherited boundary correction
        verticalScreenBounce(); // Add vertical bouncing behavior
    }

    /**
     * Renders the enemy at its current position, taking into account its scale.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    @Override
    public void safeRender(Graphics2D g2d) {
        BufferedImage currentSprite = getCurrentSprite();
        if (currentSprite == null) {
            return;
        }

        int width = (int) (currentSprite.getWidth() * getScale());
        int height = (int) (currentSprite.getHeight() * getScale());

        g2d.drawImage(currentSprite, (int) getX(), (int) getY(), width, height, null);
    }

    // ---- HELPER METHODS -----
    /**
     * Reverses the enemy's vertical velocity if it reaches the top or bottom
     * boundary of the screen, creating a bounce effect.
     */
    private void verticalScreenBounce() {
        int panelHeight = panel.getHeight();
        int spriteHeight = getSpriteHeight();

        if (position.y <= 0 || position.y >= panelHeight - spriteHeight) {
            velocityY = -velocityY; // Reverse direction
            position.y = Math.max(0, Math.min(position.y, panelHeight - spriteHeight)); // Keep within bounds
        }
    }

    /**
     * Updates the attack cooldown timer by calculating the elapsed time since
     * the last update. If the cooldown period has passed, the elapsed cooldown
     * time is reset.
     */
    private void updateAttackCooldownTimer() {
        long currentTime = System.currentTimeMillis();
        elapsedAttackCooldownMs += currentTime - (lastUpdateTime > 0 ? lastUpdateTime : currentTime);
        lastUpdateTime = currentTime;

        // Reset if cooldown has passed
        if (elapsedAttackCooldownMs >= attackCooldownMs) {
            elapsedAttackCooldownMs = 0;
        }
    }

    /**
     * Updates the attack timer by calculating the elapsed time since the last
     * update.
     */
    private void updateAttackTimer() {
        long currentTime = System.currentTimeMillis();
        elapsedAttackTimeMs += currentTime - (lastUpdateTime > 0 ? lastUpdateTime : currentTime);
        lastUpdateTime = currentTime;
    }

    // ----- STATIC BUILDER FOR ENEMY -----
    /**
     * A builder class for creating instances of {@link Enemy}. Subclasses of
     * {@code Enemy} should extend this builder to add their specific
     * properties.
     *
     * @param <T> The specific type of the builder, allowing for method chaining
     * in subclasses.
     */
    public static class EnemyBuilder<T extends MobileEntityBuilder<T>> extends MobileEntityBuilder<T> {

        // ----- INSTANCE VARIABLES -----
        /**
         * The time penalty to apply upon collision with this enemy. Defaults to
         * 0.
         */
        private int timePenalty = 0;
        /**
         * Whether the enemy should start in an idling state. Defaults to false.
         */
        private boolean isIdling = false;

        // ----- CONSTRUCTOR -----
        /**
         * Constructs a new {@code EnemyBuilder} with the specified containing
         * panel.
         *
         * @param panel The {@link JPanel} that will contain the enemy.
         */
        public EnemyBuilder(JPanel panel) {
            super(panel);
        }

        // ----- SETTERS -----
        /**
         * Sets the time penalty (in seconds) to apply to the game timer if
         * {@link Ali} collides with the enemy being built.
         *
         * @param timePenalty The time penalty.
         * @return The builder instance.
         */
        public T timePenalty(final int timePenalty) {
            this.timePenalty = timePenalty;
            return self();
        }

        /**
         * Sets whether the enemy being built should start in an idling state
         * (not moving).
         *
         * @param isIdling {@code true} if the enemy should be idle initially,
         * {@code false} otherwise.
         * @return The builder instance.
         */
        public T setIsIdling(final boolean isIdling) {
            this.isIdling = isIdling;
            return self();
        }
    }
}

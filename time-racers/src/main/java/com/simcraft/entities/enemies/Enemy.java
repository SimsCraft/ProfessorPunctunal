package com.simcraft.entities.enemies;

import java.awt.Graphics2D;
import java.awt.Point;
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
 * attacking the player/{@link Ali}.
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
     * Internally used random numbe generator.
     */
    protected Random random;
    /**
     * The cooldown duration before the enemy can move again after a previous
     * movement opportunity.
     */
    protected int moveDelay;
    protected boolean hasCollided;
    /**
     * How many seconds to remove from the timer if Ali collides with this
     * enemy.
     */
    protected int timePenalty;
    /**
     * Whether the enemy is currently idling (not moving).
     */
    protected boolean isIdling;
    /**
     * The percentage chance that the enemy starts idling if left uninterrupted.
     * Should Ali enter within range, this should be ignored in favour of an attack.
     */
    protected double chanceToIdle;
    /**
     * How many milliseconds the enemy should remain idle for if left uninterrupted.
     * Should Ali enter within range, this should be ignored in favour of an attack. 
     */
    protected long idleDuration;
    /**
     * How close Ali must be to the enemy to trigger an attack.
     */
    protected int detectionRadius;

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a Enemy instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the enemy.
     */
    protected Enemy(EnemyBuilder<?> builder) {
        super(builder);
        random = new Random();
        hasCollided = false;
    }

    // ---- GETTERS -----
    /**
     * Returns the cooldown duration before the enemy can move again after a
     * previous movement opportunity.
     *
     * @return the movement delay.
     */
    public int getMoveDelay() {
        return moveDelay;
    }

    public boolean hasCollided() {
        return hasCollided;
    }

    /**
     * Returns whether the enemy is attacking/firing bullets.
     *
     * @return {@code true} if attacking, {@code false} otherwise.
     */
    public boolean isAttacking() {
        // TODO Write attack check logic
        return false;
        // if (bulletSpawner == null) {
        //     return false;
        // }
        // return bulletSpawner.isSpawning();
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
     * @return The elasped time.
     */
    public long getElapsedAttackCooldownMs() {
        return elapsedAttackCooldownMs;
    }

    /**
     * Returns the last time the enemy was updated in milliseconds.
     *
     * @return The last update time.
     */
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * Returns how many seconds will be removed from the timer if Ali collides
     * with this enemy.
     *
     * @return the time penalty.
     */
    public int getTimePenalty() {
        return timePenalty;
    }

    public boolean isIdling() {
        return isIdling;
    }

    // ---- SETTERS -----
    /**
     * Sets the cooldown duration before the enemy can move again after a
     * previous movement opportunity.
     *
     * @param moveDelay The movement delay.
     */
    public void setMoveDelay(final int moveDelay) {
        this.moveDelay = moveDelay;
    }

    public void setHasCollided(final boolean hasCollided) {
        this.hasCollided = hasCollided;
    }

    /**
     * Sets whether the enemy is attacking.
     *
     * @param isAttacking {@code true} if the enemy should start attacking,
     * {@code false} to stop.
     */
    public void setIsAttacking(final boolean isAttacking) throws IllegalArgumentException {
        // TODO attack logic
    }

    /**
     * Sets the entity's attack delay in milliseconds. Minimum value is 1 ms.
     *
     * @param attackDelayMs The attack delay.
     */
    public void setAttackCooldownMs(final long attackDelayMs) {
        this.attackCooldownMs = Math.max(attackDelayMs, 1);
    }

    /**
     * Sets the coordinates on screen that the enemy should attack aim their
     * attacks towards.
     *
     * @param target
     */
    public void setTarget(final Point target) {
        if (target == null) {
            return;
        }
        // TODO set target logic
        // Point startCoords = getCentreCoordinates();
        // Dimension bulletSpriteDimensions = bulletSpawner.getBulletSpriteDimensions();
        // startCoords.x -= bulletSpriteDimensions.width / 2;
        // startCoords.y -= bulletSpriteDimensions.height / 2;
        // Bearing2D bearing = new Bearing2D(startCoords.x, startCoords.y, target.x, target.y);
        // double radians = Math.toRadians(bearing.getDegrees());
        // double currentBulletVelocityX = bulletSpawner.getBulletVelocityX();
        // double currentBulletVelocityY = bulletSpawner.getBulletVelocityY();
        // bulletSpawner.setBulletVelocityX(currentBulletVelocityX * Math.cos(radians));
        // bulletSpawner.setBulletVelocityY(currentBulletVelocityY * Math.sin(radians));
    }

    /**
     * Sets how many seconds will be removed from the timer if Ali collides with
     * this enemy.
     *
     * @param timePenalty The time penalty.
     */
    public void setTimePenalty(final int timePenalty) {
        this.timePenalty = timePenalty;
    }

    /**
     * Sets whether the entity should currently idle (i.e., stop moving).
     *
     * @param isIdling {@code true} is they should be idle, {@code false}
     * otherwise.
     */
    public void setIsIdling(final boolean isIdling) {
        this.isIdling = isIdling;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Checks whether the enemy can perform an attack.
     *
     * @return {@code true} if the enemy has a bullet spawner, is not on
     * cooldown, and has remaining attack time in the current wave.
     */
    public boolean canAttack() {
        return !isOnAttackCooldown() && elapsedAttackTimeMs < attackTimerMs;
    }

    /**
     * Checks if the attack cooldown is active.
     *
     * @return {@code true} if the attack cooldown has not expired,
     * {@code false} otherwise.
     */
    public boolean isOnAttackCooldown() {
        return elapsedAttackCooldownMs < attackCooldownMs;
    }

    /**
     * Performs an attack if the enemy is allowed to do so. Automatically stops
     * attacking when the attack timer runs out.
     */
    public void attack() {
        if (!canAttack()) {
            return;
        }

        // bulletSpawner.start();  // Start firing bullets
        long attackStartTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - attackStartTime < attackTimerMs) {
            updateAttackTimer();
        }

        // bulletSpawner.stop();
        updateAttackCooldownTimer();
    }

    /**
     * Reverses the enemy's movement direction.
     */
    public void reverseMovementDirection() {
        setVelocityX(-getVelocityX());
        setVelocityY(-getVelocityY());
    }

    /**
     * Randomly sets the enemy's movement direction.
     */
    public void setRandomDirection() {
        int[] directions = {-1, 0, 1};
        int vx;
        int vy;
        do {
            vx = directions[random.nextInt(3)];
            vy = directions[random.nextInt(3)];
        } while (vx == 0 && vy == 0);

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
                && Integer.compare(moveDelay, getMoveDelay()) == 0
                && hasCollided == other.hasCollided()
                && isAttacking() == other.isAttacking()
                && Long.compare(attackCooldownMs, other.getAttackCooldownMs()) == 0
                && Long.compare(elapsedAttackCooldownMs, other.getElapsedAttackCooldownMs()) == 0
                && Long.compare(lastUpdateTime, other.getLastUpdateTime()) == 0
                && Integer.compare(timePenalty, getTimePenalty()) == 0;
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
                timePenalty
        );
    }

    /**
     * Updates the enntity's current position using their current movement speed
     * values.
     * <p>
     * Extends {@link MobileEntity#move()} by ensuring the enemy still within
     * screen boundaries.
     */
    @Override
    public void move() {
        if (moveDelay % 4 == 0) {
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
     * Updates the state of the entity, including movement and attack state.
     */
    @Override
    public void update() {
        super.update();
        // lastUpdateTime = System.currentTimeMillis();
        // attack();
    }

    /**
     * Ensures the entity remains within screen boundaries.
     * <p>
     * Extends {@link MobileEntity#correctPosition()} by adding vertical bounce
     * behavior when the enemy reaches the screen's edges.
     */
    @Override
    protected void correctPosition() {
        super.correctPosition(); // Use inherited boundary correction
        verticalScreenBounce(); // Add bouncing behavior
    }

    @Override
    public void safeRender(Graphics2D g2d) {
        BufferedImage currentSprite = getCurrentSprite();
        if (currentSprite == null) return;

        int width = (int) (currentSprite.getWidth() * getScale());
        int height = (int) (currentSprite.getHeight() * getScale());

        g2d.drawImage(currentSprite, (int) getX(), (int) getY(), width, height, null);
    }

    // ---- HELPER METHODS -----
    /**
     * Reverses the enemy's horizontal velocity when hitting the left or right
     * screen boundary, simulating a wall bounce.
     */
    private void verticalScreenBounce() {
        int panelHeight = panel.getHeight();
        int spriteHeight = getSpriteHeight();

        if (position.y <= 0 || position.y >= panelHeight - spriteHeight) {
            velocityY = -velocityY; // Reverse direction
            position.y = Math.max(Math.min(position.y, 0), panelHeight - spriteHeight); // Keep within bounds
        }
    }

    /**
     * Updates the attack cooldown timer. This tracks how much time has passed
     * since the last attack.
     */
    private void updateAttackCooldownTimer() {
        long currentTime = System.currentTimeMillis();
        elapsedAttackCooldownMs += currentTime - lastUpdateTime;

        // Reset if cooldown has passed
        if (elapsedAttackCooldownMs >= attackCooldownMs) {
            elapsedAttackCooldownMs = 0;
        }
    }

    /**
     * Updates the attack timer. This tracks how long the enemy has been
     * continuously attacking.
     */
    private void updateAttackTimer() {
        long currentTime = System.currentTimeMillis();
        elapsedAttackTimeMs += currentTime - lastUpdateTime;
    }

    // ----- STATIC BUILDER FOR ENEMY -----
    public static class EnemyBuilder<T extends MobileEntityBuilder<T>> extends MobileEntityBuilder<T> {

        // ----- INSTANCE VARIABLES -----
        private int timePenalty = 0;
        private boolean isIdling = false;

        // ----- CONSTRUCTOR -----
        public EnemyBuilder(JPanel panel) {
            super(panel);
        }

        // ----- SETTERS -----
        /**
         * Sets how many seconds to remove from the timer if Ali collides with
         * this enemy.
         *
         * @param timePenalty the time penalty.
         * @return the builder instance.
         */
        public T timePenalty(final int timePenalty) {
            this.timePenalty = timePenalty;
            return self();
        }

        /**
         * Sets whether the entity should currently idle (i.e., stop moving).
         *
         * @param isIdling {@code true} is they should be idle, {@code false}
         * otherwise.
         * @return the builder instance.
         */
        public T setIsIdling(final boolean isIdling) {
            this.isIdling = isIdling;
            return self();
        }
    }
}

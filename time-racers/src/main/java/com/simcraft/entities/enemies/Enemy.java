package com.simcraft.entities.enemies;

import java.awt.Point;
import java.util.Objects;

import javax.swing.JPanel;

import com.simcraft.entities.MobileEntity;
import com.simcraft.graphics.GameFrame;

/**
 * Represents an enemy in the game that is mobile and can move within the game
 * world.
 * <p>
 * This class extends {@link MobileEntity} and includes functionality for
 * attacking the player/{@link Ali}.
 */
public class Enemy extends MobileEntity {

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
     * <p>
     * This is separate from the {@link BulletSpawner}'s spawn delay, as it
     * controls the waiting time between attack waves rather than the firing
     * rate within a wave.
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

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a Enemy instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the enemy.
     */
    public Enemy(EnemyBuilder builder) {
        super(builder);
    }

    // ---- GETTERS -----
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

    // ---- SETTERS -----
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
                && isAttacking() == other.isAttacking()
                && attackCooldownMs == other.getAttackCooldownMs()
                && elapsedAttackCooldownMs == other.getElapsedAttackCooldownMs()
                && lastUpdateTime == other.lastUpdateTime;
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
                isAttacking(),
                attackCooldownMs,
                elapsedAttackCooldownMs,
                lastUpdateTime
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
        super.move();
        correctPosition();
    }

    /**
     * Updates the state of the entity, including movement and attack state.
     */
    @Override
    public void update() {
        super.update();
        lastUpdateTime = System.currentTimeMillis();
        attack();
    }

    /**
     * Ensures the entity remains within screen boundaries.
     * <p>
     * Extends {@link MobileEntity#correctPosition()} by adding horizontal
     * bounce behavior when the enemy reaches the screen's edges.
     */
    @Override
    protected void correctPosition() {
        super.correctPosition(); // Use inherited boundary correction
        horizontalScreenBounce(); // Add bouncing behavior
    }

    // ---- HELPER METHODS -----
    /**
     * Reverses the enemy's horizontal velocity when hitting the left or right
     * screen boundary, simulating a wall bounce.
     */
    private void horizontalScreenBounce() {
        if (position.x <= 0 || position.x >= panel.getWidth() - getSpriteWidth()) {
            velocityX = -velocityX; // Reverse direction
            position.x = Math.max(Math.min(position.x, 0), GameFrame.FRAME_HEIGHT - getSpriteWidth()); // Keep within bounds
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
    public static class EnemyBuilder extends MobileEntityBuilder<EnemyBuilder> {

        // ----- CONSTRUCTOR -----
        public EnemyBuilder(JPanel panel) {
            super(panel);
        }

        // ----- BUSINESS LOGIC METHODS -----
        public Enemy build() {
            return new Enemy(this);
        }
    }
}

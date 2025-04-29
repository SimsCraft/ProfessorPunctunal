package com.simcraft.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

/**
 * Represents a Yapper enemy in the game.
 * <p>
 * This class extends {@link Enemy} and initializes attributes specific to the
 * Yapper.
 */
public class Yapper extends Enemy {

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a Yapper instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the Yapper.
     */
    public Yapper(YapperBuilder builder) {
        super(builder);

        HashSet<String> yapperAnimationKeys = Stream.of(
                "yapper_walk_down",
                "yapper_walk_left",
                "yapper_walk_right",
                "yapper_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));
        setAnimationKeys(yapperAnimationKeys);

        // Temporary initial animation; will be updated dynamically during movement
        setAnimation("yapper_walk_down");

        setSpeed(5); // Fast, though slightly slower than Ali to allow him to get away.
        setTimePenalty(10);
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Extends {@link Enemy#move()} to update the yapper's animation based on
     * their current movement direction.
     */
    @Override
    public void move() {
        super.move();

        double vx = getVelocityX();
        double vy = getVelocityY();
        double threshold = 0.1; // Minimum velocity to trigger animation change

        if (Math.abs(vx) > threshold || Math.abs(vy) > threshold) {
            if (Math.abs(vx) > Math.abs(vy)) {
                if (vx > 0) {
                    setAnimation("yapper_walk_right");
                } else if (vx < 0) {
                    setAnimation("yapper_walk_left");
                }
            } else {
                if (vy > 0) {
                    setAnimation("yapper_walk_down");
                } else if (vy < 0) {
                    setAnimation("yapper_walk_up");
                }
            }
        }
    }

    // ----- STATIC BUILDER FOR YAPPER -----
    /**
     * A builder class for creating instances of {@link Yapper}. Extends
     * {@link EnemyBuilder} to inherit common enemy properties.
     */
    public static class YapperBuilder extends EnemyBuilder<YapperBuilder> {

        // ----- CONSTRUCTOR -----
        /**
         * Constructs a new {@code YapperBuilder} with the specified panel.
         *
         * @param panel The {@link JPanel} that will contain the Yapper.
         */
        public YapperBuilder(JPanel panel) {
            super(panel);
        }

        // ----- BUSINESS LOGIC METHODS -----
        /**
         * Builds and returns a new {@link Yapper} instance using the properties
         * configured in this builder.
         *
         * @return A new {@link Yapper} instance.
         */
        public Yapper build() {
            return new Yapper(this);
        }
    }
}

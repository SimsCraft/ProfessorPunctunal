package com.simcraft.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

/**
 * Represents a Lecturer enemy in the game.
 * <p>
 * This class extends {@link Enemy} and initializes attributes specific to the
 * Lecturer.
 */
public class Lecturer extends Enemy {

    // ----- INSTANCE VARIABLES -----
    // Used to track last direction and prevent constant animation resets
    private String currentDirectionKey = "";

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a Lecturer instance.
     *
     * @param builder The {@link LecturerBuilder} used to construct the
     * Lecturer.
     */
    public Lecturer(LecturerBuilder builder) {
        super(builder);

        HashSet<String> lecturerAnimationKeys = Stream.of(
                "female_lecturer_walk_down",
                "female_lecturer_walk_left",
                "female_lecturer_walk_right",
                "female_lecturer_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));
        setAnimationKeys(lecturerAnimationKeys);

        // Temporary initial animation; will be updated dynamically during movement
        setAnimation("female_lecturer_walk_down");
        currentDirectionKey = "female_lecturer_walk_down";

        setSpeed(4); // Medium speed
        setTimePenalty(3);
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Extends {@link Enemy#move()} to update the lecturer's animation based on
     * their current movement direction. It prioritizes vertical movement if
     * it's significantly larger than horizontal movement.
     */
    @Override
    public void move() {
        super.move();

        double vx = getVelocityX();
        double vy = getVelocityY();
        double threshold = 0.1; // Increased threshold for meaningful movement
        double verticalBiasFactor = 1.5; // How much larger vertical needs to be to prioritize

        // If not moving meaningfully, skip direction update
        if (Math.abs(vx) < threshold && Math.abs(vy) < threshold) {
            return;
        }

        String newDirectionKey;

        // Determine primary direction with a bias towards vertical movement
        if (Math.abs(vy) > Math.abs(vx) * verticalBiasFactor) {
            // Prioritize vertical movement
            if (vy < -threshold) {
                newDirectionKey = "female_lecturer_walk_up";
            } else {
                newDirectionKey = "female_lecturer_walk_down";
            }
        } else if (Math.abs(vx) > threshold) {
            // Horizontal movement
            if (vx < -threshold) {
                newDirectionKey = "female_lecturer_walk_left";
            } else {
                newDirectionKey = "female_lecturer_walk_right";
            }
        } else {
            // If not clearly horizontal or vertical, maintain the last direction
            return;
        }

        // Set animation only if the direction has changed
        if (!newDirectionKey.equals(currentDirectionKey)) {
            setAnimation(newDirectionKey);
            currentDirectionKey = newDirectionKey;
        }
    }

    // ----- STATIC BUILDER FOR LECTURER -----
    /**
     * A builder class for creating instances of {@link Lecturer}. Extends
     * {@link EnemyBuilder} to inherit common enemy properties.
     */
    public static class LecturerBuilder extends EnemyBuilder<LecturerBuilder> {

        // ----- CONSTRUCTOR -----
        /**
         * Constructs a new {@code LecturerBuilder} with the specified panel.
         *
         * @param panel The {@link JPanel} that will contain the Lecturer.
         */
        public LecturerBuilder(JPanel panel) {
            super(panel);
        }

        // ----- BUSINESS LOGIC METHODS -----
        /**
         * Builds and returns a new {@link Lecturer} instance using the
         * properties configured in this builder.
         *
         * @return A new {@link Lecturer} instance.
         */
        public Lecturer build() {
            return new Lecturer(this);
        }
    }
}

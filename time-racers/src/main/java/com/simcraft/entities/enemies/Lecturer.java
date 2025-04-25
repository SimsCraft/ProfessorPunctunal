package com.simcraft.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

// public class Lecturer extends Enemy {
//     public Lecturer(JPanel panel, int xPos, int yPos, Image backgroundImage) {
//         super(panel, xPos, yPos, 3, "Lecturers/FemLec", backgroundImage); // Medium speed
//     }
// }
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
     * @param builder The {@link EnemyBuilder} used to construct the Lecturer.
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

        setSpeed(4); // Medium
        setTimePenalty(3);
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Extends {@link Enemy#move()} by applying a cosine function to oscillate
     * the y-coordinate with a given amplitude.
     * <p>
     * The final y-coordinate is clamped to stay within the desired range of 1/5
     * to 3/5 of the screen height.
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

        // Determine primary direction with a bias
        if (Math.abs(vy) > Math.abs(vx) * verticalBiasFactor) {
            // Prioritize vertical movement
            if (vy < -threshold) {
                newDirectionKey = "female_lecturer_walk_up";
            } else {
                newDirectionKey = "female_lecturer_walk_down";
            }
        } else if (Math.abs(vx) > threshold) { // Only consider horizontal if moving horizontally
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

        // Set animation only if it's changed
        if (!newDirectionKey.equals(currentDirectionKey)) {
            setAnimation(newDirectionKey);
            currentDirectionKey = newDirectionKey;
        }
    }

    // ----- STATIC BUILDER FOR LECTURER -----
    public static class LecturerBuilder extends EnemyBuilder<LecturerBuilder> {

        // ----- CONSTRUCTOR -----
        public LecturerBuilder(JPanel panel) {
            super(panel);
        }

        // ----- OVERRIDDEN METHODS -----
        public Lecturer build() {
            return new Lecturer(this);
        }
    }
}

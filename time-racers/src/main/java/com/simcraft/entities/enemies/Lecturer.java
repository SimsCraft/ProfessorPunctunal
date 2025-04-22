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

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a Lecturer instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the Lecturer.
     */
    public Lecturer(EnemyBuilder builder) {
        super(builder);

        setUpLecturerAnimations();
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

        // TODO Replace with actual movement logic
        // // Get the normalized cosine value in the range [-1, 1]
        // double normalizedCos = Math.cos(position.y);
        // double amplitude = 50;
        // // Oscillate within the range, using the amplitude to determine the oscillation
        // double oscillatedY = amplitude * GameFrame.FRAME_HEIGHT * normalizedCos;
        // // Clamp the result to stay within the limits [1/5 * GameFrame.FRAME_HEIGHT, 3/5 * GameFrame.FRAME_HEIGHT]
        // position.y = (int) Math.max(
        //         Math.min(
        //                 oscillatedY + (1.0 / 5.0) * GameFrame.FRAME_HEIGHT,
        //                 (1.0 / 5.0) * GameFrame.FRAME_HEIGHT
        //         ),
        //         (3.0 / 5.0) * GameFrame.FRAME_HEIGHT
        // );
    }

    // ----- HELPER METHODS -----
    /**
     * Set up the animations for the Blue Mage, including animation keys and
     * initial animation.
     */
    private void setUpLecturerAnimations() {
        HashSet<String> studentAnimationKeys = Stream.of(
                "female-lecturer/female_lecturer_walk_down.png",
                "female-lecturer/female_lecturer_walk_left.png",
                "female-lecturer/female_lecturer_walk_right.png",
                "female-lecturer/female_lecturer_walk_up.png"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(studentAnimationKeys);
        setAnimation("female_l/female_lecturer_walk_down.png");
        setMaxHitPoints(20);
        setCurrentHitPoints(20);
    }

    // ----- STATIC BUILDER FOR ENEMY -----
    public static class LecturerBuilder extends EnemyBuilder {

        // ----- CONSTRUCTOR -----
        public LecturerBuilder(JPanel panel) {
            super(panel);
        }

        // ----- OVERRIDDEN METHODS -----
        @Override
        public Lecturer build() {
            return new Lecturer(this);
        }
    }
}

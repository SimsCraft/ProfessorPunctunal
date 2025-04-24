package com.simcraft.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

// public class Student extends Enemy {
//     public Student(JPanel panel, int xPos, int yPos, Image backgroundImage) {
//         super(panel, xPos, yPos, 2, "Student/FStud", backgroundImage); // Slow speed
//     }
// }
/**
 * Represents a Student enemy in the game.
 * <p>
 * This class extends {@link Enemy} and initializes attributes specific to the
 * Student.
 */
public class Student extends Enemy {

    // ----- CONSTRUCTORS -----
    /**
     * Constructor used to create a Student instance.
     *
     * @param builder The {@link EnemyBuilder} used to construct the Student.
     */
    public Student(StudentBuilder builder) {
        super(builder);

        HashSet<String> studentAnimationKeys = Stream.of(
                "female_student_walk_down",
                "female_student_walk_left",
                "female_student_walk_right",
                "female_student_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(studentAnimationKeys);

        // Temporary initial animation; will be updated dynamically during movement
        setAnimation("female_student_walk_down");
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

    // ----- STATIC BUILDER FOR ENEMY -----
    public static class StudentBuilder extends EnemyBuilder<StudentBuilder> {

        // ----- CONSTRUCTOR -----
        public StudentBuilder(JPanel panel) {
            super(panel);
        }

        // ----- OVERRIDDEN METHODS -----
        public Student build() {
            return new Student(this);
        }
    }
}

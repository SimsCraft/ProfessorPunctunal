package com.simcraft.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

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

        setSpeed(3); // Slow speed
        setTimePenalty(5);
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Extends {@link Enemy#move()} to update the student's animation based on
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
                    setAnimation("female_student_walk_right");
                } else if (vx < 0) {
                    setAnimation("female_student_walk_left");
                }
            } else {
                if (vy > 0) {
                    setAnimation("female_student_walk_down");
                } else if (vy < 0) {
                    setAnimation("female_student_walk_up");
                }
            }
        }
    }

    // ----- STATIC BUILDER FOR STUDENT -----
    /**
     * A builder class for creating instances of {@link Student}. Extends
     * {@link EnemyBuilder} to inherit common enemy properties.
     */
    public static class StudentBuilder extends EnemyBuilder<StudentBuilder> {

        // ----- CONSTRUCTOR -----
        /**
         * Constructs a new {@code StudentBuilder} with the specified panel.
         *
         * @param panel The {@link JPanel} that will contain the Student.
         */
        public StudentBuilder(JPanel panel) {
            super(panel);
        }

        // ----- BUSINESS LOGIC METHODS -----
        /**
         * Builds and returns a new {@link Student} instance using the
         * properties configured in this builder.
         *
         * @return A new {@link Student} instance.
         */
        public Student build() {
            return new Student(this);
        }
    }
}

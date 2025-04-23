package com.simcraft.entities.enemies;

import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

// public class Yapper extends Enemy {
//     public Yapper(JPanel panel, int xPos, int yPos, Image backgroundImage) {
//         super(panel, xPos, yPos, 4, "Yapper/yap", backgroundImage); // Fast speed
//     }
// }

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
    public Yapper(EnemyBuilder builder) {
        super(builder);

        setUpYapperAnimations();
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Replace with actual movement logic
     * 
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
    private void setUpYapperAnimations() {
        HashSet<String> studentAnimationKeys = Stream.of(
                "yapper_walk_down",
                "yapper_walk_left",
                "yapper_walk_right",
                "yapper_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(studentAnimationKeys);
        setAnimation("yapper_walk_down");
        setMaxHitPoints(20);
        setCurrentHitPoints(20);
    }

    // ----- STATIC BUILDER FOR ENEMY -----
    public static class YapperBuilder extends EnemyBuilder {

        // ----- CONSTRUCTOR -----
        public YapperBuilder(JPanel panel) {
            super(panel);
        }

        // ----- OVERRIDDEN METHODS -----
        @Override
        public Yapper build() {
            return new Yapper(this);
        }
    }
}

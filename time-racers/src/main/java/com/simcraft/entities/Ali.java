package com.simcraft.entities;

import java.awt.Point;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

/**
 * Represents the main playable character, Mr. Ali. Extends the MobileEntity
 * class.
 */
public class Ali extends MobileEntity {

    public Ali(AliBuilder builder) {
        super(builder);

        HashSet<String> aliAnimationKeys = Stream.of(
                "ali_walk_down",
                "ali_walk_left",
                "ali_walk_right",
                "ali_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(aliAnimationKeys);

        // Temporary initial animation; will be updated dynamically during movement
        setAnimation("ali_walk_down");
    }

    // ----- BUSINESS LOGIC -----
    public void resetPosition() {
        setPosition(new Point(350, 550));
    }

    // ----- OVERRIDDEN METHODS -----
    @Override
    public void move() {
        super.move();
        correctPosition();
    }

    // ----- BUILDER PATTERN -----
    /**
     * The MobileEntityBuilder class provides a fluent API for constructing an
     * MobileEntity object.
     */
    public static class AliBuilder extends MobileEntityBuilder<AliBuilder> {

        // ------ CONSTRUCTORS -----
        public AliBuilder(final JPanel panel) {
            super(panel);
        }
        // ----- BUSINESS LOGIC METHODS -----

        public Ali build() {
            return new Ali(this);
        }
    }
}

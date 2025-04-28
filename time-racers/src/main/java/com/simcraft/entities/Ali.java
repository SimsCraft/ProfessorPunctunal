package com.simcraft.entities;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
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

        setSpeed(6); // Fast (he's in a hurry)
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

    @Override
    public void safeRender(Graphics2D g2d) {
        BufferedImage currentSprite = getCurrentSprite();
        if (currentSprite == null) return;
        int width = (int) (currentSprite.getWidth() * getScale());
        int height = (int) (currentSprite.getHeight() * getScale());
        g2d.drawImage(currentSprite, (int) getX(), (int) getY(), width, height, null);
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

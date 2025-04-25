package com.simcraft.entities;

import java.awt.Point;
import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Represents the main playable character, Mr. Ali. Extends the MobileEntity
 * class.
 */
public class Ali extends MobileEntity {

    public Ali(AliBuilder builder) {
        super(builder);
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


    public void getBounds() {}
    public void draw (Graphics g) {}
    public void keyPressed(int keyCode) {}
    public void keyReleased(int keyCode) {}
}

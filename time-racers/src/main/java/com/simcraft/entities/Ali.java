package com.simcraft.entities;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import java.util.HashSet;
import java.util.Set;

import com.simcraft.graphics.animations.AliAnimation;
import com.simcraft.managers.SoundManager;

/**
 * Represents the main playable character, Mr. Ali. Extends the MobileEntity
 * class.
 */
public class Ali extends MobileEntity {

    private final int SPEED = 4;
    private Set<Integer> pressedKeys = new HashSet<>();
    private AliAnimation aliAnimation;
    private Image backgroundImage;
    private SoundManager soundManager;

    public Ali(AliBuilder builder) {
        super(builder);
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

    public Ali(JPanel p, int xPos, int yPos, SoundManager soundManager, Image bgImage) {
        panel = p;
        x = xPos;
        y = yPos;
        width = 32;
        height = 46;
        aliAnimation = new AliAnimation();
        this.soundManager = soundManager;
        this.backgroundImage = bgImage;
    }

    public void resetPosition() {
        setPosition(new Point(350, 550));
    }

    public void draw(Graphics g) {
        aliAnimation.draw(getX(), getY(), g);
    }

    public void keyPressed(int keyCode) {
        pressedKeys.add(keyCode);
        updateAnimationState();
    }

    public void keyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
        updateAnimationState();
    }

    public void move() {
        int dx = 0, dy = 0;

        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            dx = -1;
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            dx = 1;
        }
        if (pressedKeys.contains(KeyEvent.VK_UP)) {
            dy = -1;
        }
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
            dy = 1;
        }

        // Normalize the movement vector to ensure diagonal movement is not faster
        double length = Math.sqrt(dx * dx + dy * dy);
        if (length != 0) {  // Avoid division by zero
            dx = (int) Math.round((dx / length) * SPEED);
            dy = (int) Math.round((dy / length) * SPEED);
        }

        int newX = x + dx;
        int newY = y + dy;

        // Keep Mr. Ali within screen boundaries
        newX = Math.max(0, Math.min(panel.getWidth() - width, newX));
        newY = Math.max(0, Math.min(panel.getHeight() - height, newY));

        x = newX;
        y = newY;
    }

    private void updateAnimationState() {
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
            aliAnimation.setDirection("down");
        } else if (pressedKeys.contains(KeyEvent.VK_UP)) {
            aliAnimation.setDirection("up");
        } else if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
            aliAnimation.setDirection("right");
        } else if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
            aliAnimation.setDirection("left");
        } else {
            aliAnimation.stopWalking();
        }
    }
}

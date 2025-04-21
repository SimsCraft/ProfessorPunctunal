package com.simcraft.entities;
import java.awt.*;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;
import java.util.HashSet;
import java.util.Set;

public class Player {
    private JPanel panel;
    private int x, y;
    private int width, height;
    private final int SPEED = 4;
    private Set<Integer> pressedKeys = new HashSet<>();
    private AliAnimation aliAnimation;
    private Image backgroundImage;
    private SoundManager soundManager;

    public Player(JPanel p, int xPos, int yPos, SoundManager soundManager, Image bgImage) {
        panel = p;
        x = xPos;
        y = yPos;
        width = 32;
        height = 46;
        aliAnimation = new AliAnimation();
        this.soundManager = soundManager;
        this.backgroundImage = bgImage;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public void resetPosition() {
        x = 350;
        y = 550;
    }

    public void draw(Graphics g) {
        aliAnimation.draw(x, y, g);
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

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

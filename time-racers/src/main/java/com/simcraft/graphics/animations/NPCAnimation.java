package com.simcraft.graphics.animations;
import java.awt.Graphics;
import java.awt.Image;
import java.util.HashMap;

import javax.swing.ImageIcon;

public class NPCAnimation {
    private HashMap<String, Image[]> animations;
    private int frameIndex;
    private String currentDirection;
    private long lastFrameTime;
    private final int frameDelay = 200;

    public NPCAnimation(String type) {
        animations = new HashMap<>();
        loadFrames(type);
        currentDirection = "down";
        frameIndex = 0;
    }

    private void loadFrames(String type) {
        animations.put("down", new Image[]{new ImageIcon(type + "_front1.png").getImage(), new ImageIcon(type + "_front3.png").getImage()});
        animations.put("up", new Image[]{new ImageIcon(type + "_back1.png").getImage(), new ImageIcon(type + "_back3.png").getImage()});
        animations.put("right", new Image[]{new ImageIcon(type + "_right1.png").getImage(), new ImageIcon(type + "_right3.png").getImage()});
        animations.put("left", new Image[]{new ImageIcon(type + "_left1.png").getImage(), new ImageIcon(type + "_left3.png").getImage()});
    }

    public void draw(int x, int y, Graphics g, int dx, int dy) {
        if (dx > 0) {
            currentDirection = "right";
        } else if (dx < 0) {
            currentDirection = "left";
        } else if (dy > 0) {
            currentDirection = "down";
        } else if (dy < 0) {
            currentDirection = "up";
        }
    
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > frameDelay) {
            frameIndex = (frameIndex + 1) % 2;
            lastFrameTime = currentTime;
        }
    
        Image[] frames = animations.get(currentDirection);
        g.drawImage(frames[frameIndex], x, y, null);
    }
}

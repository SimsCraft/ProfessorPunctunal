package com.simcraft.entities;
import java.awt.*;
import java.util.Random;
import javax.swing.JPanel;

public class NPC {
    private int width = 32, height = 46;
    private int x, y;
    private int dx, dy;
    private int speed;
    private JPanel panel;
    private Random random = new Random();
    public boolean hasCollided = false;
    private int moveDelay = 0;
    private NPCAnimation npcAnimation;
    private Image backgroundImage;

    public NPC(JPanel p, int xPos, int yPos, int speed, String type, Image bgImage) {
        this.panel = p;
        this.x = xPos;
        this.y = yPos;
        this.speed = speed;
        this.npcAnimation = new NPCAnimation(type);
        this.backgroundImage = bgImage;
        setRandomDirection();
    }

    protected void setRandomDirection() {
        int[] directions = {-1, 0, 1};
        dx = directions[random.nextInt(3)] * speed;
        dy = directions[random.nextInt(3)] * speed;
        if (dx == 0 && dy == 0) {
            setRandomDirection();
        }
    }

    public void move() {
        if (moveDelay % 4 == 0) {
            x += dx;
            y += dy;
            if (random.nextInt(50) == 1) {
                setRandomDirection();
            }
            if (x < 0 || x > panel.getWidth() - width) {
                dx = -dx;
                setRandomDirection();
            }
            if (y < 0 || y > panel.getHeight() - height) {
                dy = -dy;
                setRandomDirection();
            }
        }
        moveDelay++;
    }

    public void reverseDirection() {
        dx = -dx;
        dy = -dy;
    }

    public void draw(Graphics g) {
        npcAnimation.draw(x, y, g, dx, dy);
    }

    // public void erase(Graphics g) {
    //     if (backgroundImage != null) {
    //         g.drawImage(backgroundImage, x - 5, y - 5, x + width + 5, y + height + 5, 
    //                     x - 5, y - 5, x + width + 5, y + height + 5, null);
    //     }
    // }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

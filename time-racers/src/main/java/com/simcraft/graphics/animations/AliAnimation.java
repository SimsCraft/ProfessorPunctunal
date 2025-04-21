package com.simcraft.graphics.animations;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class AliAnimation extends JPanel implements KeyListener, ActionListener {

    private Image idleDown, idleUp, idleRight, idleLeft;
    private Image[] walkDown, walkUp, walkRight, walkLeft;
    private String currentDirection;
    private boolean isWalking;
    private Timer timer;
    private int frameIndex;
    private Set<Integer> pressedKeys;
    

    public AliAnimation() {
        loadFrames();
        currentDirection = "down";
        isWalking = false;
        frameIndex = 0;
        pressedKeys = new HashSet<>();
        timer = new Timer(1000 / 6, this); // 6 FPS
        timer.start();
        setFocusable(true);
        addKeyListener(this);
    }

    private void loadFrames() {
        idleDown = new ImageIcon("Player/ali_front_2.png").getImage();
        idleUp = new ImageIcon("Player/ali_back_2.png").getImage();
        idleRight = new ImageIcon("Player/ali_right_2.png").getImage();
        idleLeft = new ImageIcon("Player/ali_left_2.png").getImage();

        walkDown = new Image[]{new ImageIcon("Player/ali_front_1.png").getImage(), new ImageIcon("Player/ali_front_3.png").getImage()};
        walkUp = new Image[]{new ImageIcon("Player/ali_back_1.png").getImage(), new ImageIcon("Player/ali_back_3.png").getImage()};
        walkRight = new Image[]{new ImageIcon("Player/ali_right_1.png").getImage(), new ImageIcon("Player/ali_right_3.png").getImage()};
        walkLeft = new Image[]{new ImageIcon("Player/ali_left_1.png").getImage(), new ImageIcon("Player/ali_left_3.png").getImage()};
    }

    public Image getCurrentFrame() {
        if (isWalking) {
            switch (currentDirection) {
                case "down": return walkDown[frameIndex];
                case "up": return walkUp[frameIndex];
                case "right": return walkRight[frameIndex];
                case "left": return walkLeft[frameIndex];
            }
        } else {
            switch (currentDirection) {
                case "down": return idleDown;
                case "up": return idleUp;
                case "right": return idleRight;
                case "left": return idleLeft;
            }
        }
        return null;
    }

    public void setDirection(String direction) {
        currentDirection = direction;
        isWalking = true;
    }

    public void stopWalking() {
        isWalking = false;
        frameIndex = 0;
    }

    public void draw(int x, int y, Graphics g) {
        Image currentImage = getCurrentFrame();
        g.drawImage(currentImage, x, y, null);
    }

    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        switch (e.getKeyCode()) {
            case KeyEvent.VK_DOWN:
                setDirection("down");
                break;
            case KeyEvent.VK_UP:
                setDirection("up");
                break;
            case KeyEvent.VK_LEFT:
                setDirection("left");
                break;
            case KeyEvent.VK_RIGHT:
                setDirection("right");
                break;
        }
    }

    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
        if (pressedKeys.isEmpty()) {
            stopWalking();
        }
    }

    public void keyTyped(KeyEvent e) {}

    public void actionPerformed(ActionEvent e) {
        if (isWalking) {
            frameIndex = (frameIndex + 1) % 2; // Loop between 0 and 1
        }
    }
}

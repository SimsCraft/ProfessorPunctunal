package com.simcraft.graphics.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.simcraft.entities.Player;
import com.simcraft.entities.Lecturer;
import com.simcraft.entities.NPC;
import com.simcraft.entities.Student;
import com.simcraft.entities.Yapper;
import com.simcraft.managers.SoundManager;

public class GamePanel extends JPanel implements Runnable {

    private Player ali;
    private ArrayList<NPC> npcs;
    private Random random = new Random();
    private Thread gameThread;
    private TimerPanel timerPanel;
    private SoundManager soundManager;

    private int timeLeft = 60;
    private boolean gameOver = false;
    private Image backgroundImage;
    private Font arcadeFont;

    public GamePanel(Font arcadeFont) {
        this.arcadeFont = arcadeFont;
        this.soundManager = SoundManager.getInstance();
        this.timerPanel = new TimerPanel(timeLeft, arcadeFont);
        soundManager.playBackgroundMusic();

        backgroundImage = new ImageIcon("Background/Background1.png").getImage();

        setLayout(new BorderLayout());
        add(timerPanel, BorderLayout.NORTH);
    }

    public void startGame() {
        ali = new Player(this, getWidth() / 2 - 16, getHeight() - 50, soundManager, backgroundImage);

        npcs = new ArrayList<>();
        createGameEntities();
        timeLeft = 60;
        timerPanel.setTimeLeft(timeLeft);
        gameOver = false;

        requestFocusInWindow(); // Fix keyboard input issues
        startGameThread();
        startTimer();
    }

    public void createGameEntities() {
        npcs.clear(); // Make sure no duplicate NPCs appear
        for (int i = 0; i < 5; i++) {
            npcs.add(new Student(this, random.nextInt(getWidth()), random.nextInt(getHeight()), backgroundImage));
        }
        for (int i = 0; i < 4; i++) {
            npcs.add(new Lecturer(this, random.nextInt(getWidth()), random.nextInt(getHeight()), backgroundImage));
        }
        for (int i = 0; i < 2; i++) {
            npcs.add(new Yapper(this, random.nextInt(getWidth()), random.nextInt(getHeight()), backgroundImage));
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // Draw exit door
        g.setColor(Color.DARK_GRAY);
        g.fillRect(getWidth() / 2 - 50, 5, 100, 40);
        g.setColor(Color.WHITE);
        g.drawString("EXIT", getWidth() / 2 - 15, 30);

        if (ali != null) {
            ali.draw(g);
        }
        for (NPC npc : npcs) {
            npc.draw(g);
        }
    }

    public void updateGameEntities() {
        if (!gameOver && ali != null) {
            ali.move();
            for (NPC npc : npcs) {
                npc.move();
            }
            checkCollisions();
            repaint();
        }
    }

    public void startTimer() {
        new Thread(() -> {
            while (timeLeft > 0 && !gameOver) {
                try {
                    Thread.sleep(1000);
                    SwingUtilities.invokeLater(() -> timerPanel.setTimeLeft(timeLeft--));
                    checkGameOver();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void checkCollisions() {
        for (NPC npc : npcs) {
            for (NPC other : npcs) {
                if (npc != other && npc.getBounds().intersects(other.getBounds())) {
                    npc.reverseDirection();
                    other.reverseDirection();
                }
            }

            if (ali.getBounds().intersects(npc.getBounds())) {
                if (!npc.hasCollided) {
                    int timePenalty = (npc instanceof Student) ? 3
                            : (npc instanceof Lecturer) ? 5
                                    : (npc instanceof Yapper) ? 10 : 0;

                    timerPanel.setTimeLeft(timeLeft -= timePenalty);
                    System.out.println("Ali hit an NPC! -" + timePenalty + " seconds!");
                    npc.hasCollided = true;
                }
                npc.reverseDirection();
            } else {
                npc.hasCollided = false;
            }
        }
    }

    public void checkGameOver() {
        if (timeLeft <= 0 && !gameOver) {
            gameOver = true;
            soundManager.stopBackgroundMusic();
            soundManager.playGameOver();
            JOptionPane.showMessageDialog(this, "Game Over! Time ran out.", "Game Over", JOptionPane.WARNING_MESSAGE);
            System.exit(0);
        }
    }

    public void handleKeyPress(int keyCode) {
        if (ali != null) {
            ali.keyPressed(keyCode);
        }
    }

    public void handleKeyRelease(int keyCode) {
        if (ali != null) {
            ali.keyReleased(keyCode);
        }
    }

    public void startGameThread() {
        if (gameThread == null || !gameThread.isAlive()) {
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    @Override
    public void run() {
        while (!gameOver) {
            updateGameEntities();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

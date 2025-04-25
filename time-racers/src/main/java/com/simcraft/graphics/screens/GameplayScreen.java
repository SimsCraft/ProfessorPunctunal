package com.simcraft.graphics.screens;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.simcraft.entities.Ali;
import com.simcraft.graphics.GameFrame;
//import com.simcraft.graphics.states.GameState.State;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.graphics.screens.subpanels.InfoPanel;
import com.simcraft.graphics.screens.subpanels.TimerPanel;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.SoundManager;

/**
 * The main gameplay screen where the game logic and rendering occur.
 *
 * Components:
 * - GamePanel (center)
 * - InfoPanel (right)
 * - TimerPanel (top)
 *
 * Handles input and screen rendering logic.
 */
public final class GameplayScreen extends AbstractScreen {

    // ----- STATIC CONSTANTS -----
    private static final int BASE_SPEED = 5;

    // ----- INSTANCE VARIABLES -----
    private final transient GameManager gameManager;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final TimerPanel timerPanel;
    private final Map<Integer, Boolean> keyStates;

    // ----- CONSTRUCTOR -----
    public GameplayScreen(GameFrame gameFrame) {
        super(gameFrame);
        setLayout(new BorderLayout());

        int frameWidth = gameFrame.getWidth();
        int frameHeight = gameFrame.getHeight();

        // Create all 3 subpanels
        gamePanel = new GamePanel(frameHeight, frameHeight, "/images/backgrounds/game-panel.png");
        infoPanel = new InfoPanel(frameWidth - frameHeight, frameHeight, "/images/backgrounds/info-panel.png");

        // Get the level time & font for the timer panel
        int levelTime = GameManager.getInstance().getLevelState().getCurrentConfig().getLevelTimeLimitSeconds();
        timerPanel = new TimerPanel(levelTime, gameFrame.getArcadeFont());

        // Add all panels to screen layout
        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.LINE_END);
        add(timerPanel, BorderLayout.NORTH);

        // Initialize GameManager with subpanels
        gameManager = GameManager.getInstance();
        gameManager.init(gamePanel, infoPanel, timerPanel);

        keyStates = new HashMap<>();
        addKeyListener(createKeyListener());

        // Play background music (can be overridden by level config)
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.stopAll();
        soundManager.playClip("background", true);
    }

    // ----- GETTERS -----
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public TimerPanel getTimerPanel() {
        return timerPanel;
    }

    // ----- UPDATE + RENDER -----
    @Override
    public void update() {
        switch (gameManager.getGameState().getState()) {
            case RUNNING -> gameManager.update();
            case PAUSED -> {
                // Optional: show pause screen overlay
            }
            case GAME_OVER -> {
                // Optional: show game over screen
            }
            case STOPPED, NOT_INITIALIZED, INITIALIZING -> {
                // No-op
            }
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        if (gameManager.getGameState().isRunning()) {
            gamePanel.safeRender(g2d);
            infoPanel.safeRender(g2d);
            // TimerPanel is automatically painted by Swing
        }
    }

    // ----- INPUT LISTENER -----
    private KeyAdapter createKeyListener() {
        return new KeyAdapter() {
            private void updateAliMovement() {
                Ali ali = gameManager.getAli();
                int speed = keyStates.getOrDefault(KeyEvent.VK_SHIFT, false) ? BASE_SPEED / 2 : BASE_SPEED;

                int velocityX = 0;
                int velocityY = 0;
                String animationKey = "ali_walk_down";

                if (keyStates.getOrDefault(KeyEvent.VK_W, false) || keyStates.getOrDefault(KeyEvent.VK_UP, false)) {
                    velocityY = speed;
                    animationKey = "ali_walk_up";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_S, false) || keyStates.getOrDefault(KeyEvent.VK_DOWN, false)) {
                    velocityY = -speed;
                    animationKey = "ali_walk_down";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_A, false) || keyStates.getOrDefault(KeyEvent.VK_LEFT, false)) {
                    velocityX = -speed;
                    animationKey = "ali_walk_up_left";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_D, false) || keyStates.getOrDefault(KeyEvent.VK_RIGHT, false)) {
                    velocityX = speed;
                    animationKey = "ali_walk_up_right";
                }

                double length = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
                if (length != 0) {
                    velocityX = (int) Math.round((velocityX / length) * speed);
                    velocityY = (int) Math.round((velocityY / length) * speed);
                }

                ali.setVelocityX(velocityX);
                ali.setVelocityY(velocityY);
                ali.setAnimation((velocityX == 0 && velocityY == 0) ? "ali_walk_down" : animationKey);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyStates.put(e.getKeyCode(), true);
                updateAliMovement();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyStates.put(e.getKeyCode(), false);
                updateAliMovement();
            }
        };
    }
}
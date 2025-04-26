package com.simcraft.graphics.screens;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import com.simcraft.entities.Ali;
import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.graphics.screens.subpanels.InfoPanel;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;
import com.simcraft.managers.SoundManager;

/**
 * The main gameplay screen where the game logic and rendering occur. Handles
 * player input and updates the game state accordingly. Adds horizontal
 * background scrolling using multiple tiles.
 */
public final class GameplayScreen extends AbstractScreen {

    // INSTANCE VARIABLES -----
    private final transient GameManager gameManager;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final Map<Integer, Boolean> keyStates;

    // ----- CONSTRUCTORS -----
    /**
     * Initializes the gameplay screen, setting up the game panels and input
     * listeners.
     *
     * @param gameFrame The parent frame containing this screen.
     */
    public GameplayScreen(GameFrame gameFrame) {
        super(gameFrame);
        setLayout(new BorderLayout());

        // Load base tile + repeat tiles (background_0, background_0.1 repeated, background_1)
        BufferedImage[] backgroundTiles = new BufferedImage[7];
        backgroundTiles[0] = ImageManager.loadBufferedImage("/images/backgrounds/background_0.png");
        for (int i = 1; i <= 5; i++) {
            backgroundTiles[i] = ImageManager.loadBufferedImage("/images/backgrounds/background_0.1.png");
        }
        backgroundTiles[6] = ImageManager.loadBufferedImage("/images/backgrounds/background_1.png");

        int infoPanelHeight = 100;
        infoPanel = new InfoPanel(
                GameFrame.FRAME_WIDTH,
                infoPanelHeight,
                "/images/backgrounds/info-panel.png"
        );

        gamePanel = new GamePanel(
                GameFrame.FRAME_WIDTH,
                GameFrame.FRAME_HEIGHT - infoPanelHeight,
                backgroundTiles // set as tile sequence
        );

        add(infoPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        gameManager = GameManager.getInstance();
        gameManager.init(gamePanel, infoPanel);

        keyStates = new HashMap<>();
        addKeyListener(createKeyListener());

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

    // ----- OVERRIDDEN METHODS -----
    @Override
    public void update() {
        if (gameManager.isRunning()) {
            gameManager.update();
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        if (gameManager.isRunning()) {
            gamePanel.safeRender(g2d);
            infoPanel.safeRender(g2d);
        }
    }

    // ----- HELPER METHODS -----
    private KeyAdapter createKeyListener() {
        return new KeyAdapter() {
            private void updateAliMovement() {
                Ali ali = gameManager.getAli();
                double speed = ali.getSpeed();
                double velocityX = 0;
                double velocityY = 0;
                String animationKey = "ali_walk_down";

                if (keyStates.getOrDefault(KeyEvent.VK_W, false)) {
                    velocityY = speed;
                    animationKey = "ali_walk_up";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_S, false)) {
                    velocityY = -speed;
                    animationKey = "ali_walk_down";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_A, false)) {
                    velocityX = -speed;
                    animationKey = "ali_walk_left";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_D, false)) {
                    velocityX = speed;
                    animationKey = "ali_walk_right";
                }

                double length = Math.sqrt(velocityX * velocityX + velocityY * velocityY);
                if (length != 0) {
                    velocityX = (velocityX / length) * speed;
                    velocityY = (velocityY / length) * speed;
                }
                ali.setVelocityX(velocityX);
                ali.setVelocityY(velocityY);
                ali.setAnimation(animationKey);

                // Trigger background scroll based on Ali's movement
                gamePanel.setScrollOffset((int)(gamePanel.getScrollOffset() + velocityX));
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

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
 * player input and updates the game state accordingly.
 */
public final class GameplayScreen extends AbstractScreen {

    // ----- STATIC VARIABLES -----
    private static final int BASE_SPEED = 5;

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

        BufferedImage backgroundImage = ImageManager.loadBufferedImage("/images/backgrounds/background_0.png");
        int infoPanelHeight = 100;
        infoPanel = new InfoPanel(
                GameFrame.FRAME_WIDTH,
                infoPanelHeight,
                "/images/backgrounds/info-panel.png"
        );
        gamePanel = new GamePanel(
                GameFrame.FRAME_WIDTH,
                GameFrame.FRAME_HEIGHT - infoPanelHeight,
                backgroundImage
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
    /**
     * Retrieves the {@link GamePanel}.
     *
     * @return The main game panel.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Retrieves the {@link InfoPanel}.
     *
     * @return The information panel.
     */
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates the game state if it is running.
     */
    @Override
    public void update() {
        if (gameManager.isRunning()) {
            gameManager.update();
        }
    }

    /**
     * Renders the game screen.
     *
     * @param g2d The graphics context used for rendering.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (gameManager.isRunning()) {
            gamePanel.safeRender(g2d);
            infoPanel.safeRender(g2d);
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Creates the key listener that handles player input.
     *
     * @return A KeyAdapter instance that listens for key events.
     */
    private KeyAdapter createKeyListener() {
        return new KeyAdapter() {
            /**
             * Updates the player/Ali's movement based on the current key
             * states.
             */
            private void updateAliMovement() {
                Ali ali = gameManager.getAli();
                double speed = ali.getSpeed();
                double velocityX = 0;
                double velocityY = 0;
                // TODO Add proper idle sprite
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
                    animationKey = "ali_walk_left";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_D, false) || keyStates.getOrDefault(KeyEvent.VK_RIGHT, false)) {
                    velocityX = speed;
                    animationKey = "ali_walk_right";
                }

                // Normalize the movement vector to ensure diagonal movement is not faster
                // double length = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
                // if (length != 0) {  // Avoid division by zero
                //     velocityX = (int) Math.round((velocityX / length) * speed);
                //     velocityY = (int) Math.round((velocityX / length) * speed);
                // }
                ali.setVelocityX(velocityX);
                ali.setVelocityY(velocityY);
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

package com.simcraft.graphics.screens;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.simcraft.entities.Ali;
import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.graphics.screens.subpanels.InfoPanel;
import com.simcraft.managers.GameManager;
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

        int frameWidth = gameFrame.getWidth();
        int frameHeight = gameFrame.getHeight();
        gamePanel = new GamePanel(frameHeight, frameHeight, "/images/backgrounds/game-panel.png");
        infoPanel = new InfoPanel(frameWidth - frameHeight, frameHeight, "/images/backgrounds/info-panel.png");

        add(gamePanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.LINE_END);

        gameManager = GameManager.getInstance();
        gameManager.init(gamePanel, infoPanel);

        keyStates = new HashMap<>();
        addKeyListener(createKeyListener());

        SoundManager soundManager = SoundManager.getInstance();
        soundManager.stopAll();
        soundManager.playClip("goblinsDance", true);
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
             * Updates the player/Ali's movement based on the current key states.
             */
            private void updateAliMovement() {
                Ali ali = gameManager.getAli();
                int speed = keyStates.getOrDefault(KeyEvent.VK_SHIFT, false) ? BASE_SPEED / 2 : BASE_SPEED;
                int velocityX = 0;
                int velocityY = 0;
                String animationKey = "player-idle";

                if (keyStates.getOrDefault(KeyEvent.VK_W, false) || keyStates.getOrDefault(KeyEvent.VK_UP, false)) {
                    velocityY = speed;
                    animationKey = "player-walk-up";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_S, false) || keyStates.getOrDefault(KeyEvent.VK_DOWN, false)) {
                    velocityY = -speed;
                    animationKey = "player-walk-up";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_A, false) || keyStates.getOrDefault(KeyEvent.VK_LEFT, false)) {
                    velocityX = -speed;
                    animationKey = "player-walk-up-left";
                }
                if (keyStates.getOrDefault(KeyEvent.VK_D, false) || keyStates.getOrDefault(KeyEvent.VK_RIGHT, false)) {
                    velocityX = speed;
                    animationKey = "player-walk-up-right";
                }

                ali.setVelocityX(velocityX);
                ali.setVelocityY(velocityY);
                ali.setAnimation((velocityX == 0 && velocityY == 0) ? "player-idle" : animationKey);
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

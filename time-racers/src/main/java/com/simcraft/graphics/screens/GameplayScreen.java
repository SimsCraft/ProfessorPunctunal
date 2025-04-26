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
import com.simcraft.graphics.HorizontalScrollingBackground;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.graphics.screens.subpanels.InfoPanel;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;

/**
 * The main gameplay screen where the game logic and rendering occur. Handles
 * player input and updates the game state accordingly. This screen manages the
 * game panel, info panel, and the horizontal scrolling background,
 * orchestrating the gameplay experience.
 */
public final class GameplayScreen extends AbstractScreen {

    // ----- INSTANCE VARIABLES -----
    private final transient GameManager gameManager;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final Map<Integer, Boolean> keyStates;
    private final HorizontalScrollingBackground scrollingBackground;
    private final int levelWidth = 2000;

    // ----- CONSTRUCTORS -----
    public GameplayScreen(GameFrame gameFrame) {
        super(gameFrame);
        setLayout(new BorderLayout());

        infoPanel = createInfoPanel();
        gamePanel = createGamePanel();

        add(infoPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        gameManager = GameManager.getInstance();
        gameManager.init(gamePanel, infoPanel);

        Ali ali = gameManager.getAli();
        gamePanel.addRenderable(ali);
        gamePanel.addUpdateable(ali);

        scrollingBackground = createScrollingBackground(ali);
        gamePanel.setHorizontalScrollingBackground(scrollingBackground); // Use the specific setter

        keyStates = new HashMap<>();
        addKeyListener(createKeyListener());
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
            updateScrollingBackground();
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
    private InfoPanel createInfoPanel() {
        int infoPanelHeight = 100;
        return new InfoPanel(
                GameFrame.FRAME_WIDTH,
                infoPanelHeight,
                "/images/backgrounds/info-panel.png"
        );
    }

    private GamePanel createGamePanel() {
        BufferedImage backgroundImage = ImageManager.loadBufferedImage("/images/backgrounds/background_0.png");
        int infoPanelHeight = 100;
        return new GamePanel(
                GameFrame.FRAME_WIDTH,
                GameFrame.FRAME_HEIGHT - infoPanelHeight,
                backgroundImage
        );
    }

    private HorizontalScrollingBackground createScrollingBackground(Ali ali) {
        HorizontalScrollingBackground background = new HorizontalScrollingBackground(
                "/images/backgrounds/background_0.png",
                0,
                0,
                (int) ali.getSpeed(),
                gamePanel
        );
        background.setLevelWidth(levelWidth);
        return background;
    }

    private void updateScrollingBackground() {
        if (scrollingBackground != null) {
            scrollingBackground.update();
            scrollingBackground.setPlayerX(gameManager.getAli().getX());
        }
    }

    private KeyAdapter createKeyListener() {
        return new KeyAdapter() {
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

            private void updateAliMovement() {
                Ali ali = gameManager.getAli();
                double speed = ali.getSpeed();
                double velocityX = 0;
                double velocityY = 0;
                String animationKey = "ali_walk_down";

                boolean movingUp = isKeyPressed(KeyEvent.VK_W) || isKeyPressed(KeyEvent.VK_UP);
                boolean movingDown = isKeyPressed(KeyEvent.VK_S) || isKeyPressed(KeyEvent.VK_DOWN);
                boolean movingLeft = isKeyPressed(KeyEvent.VK_A) || isKeyPressed(KeyEvent.VK_LEFT);
                boolean movingRight = isKeyPressed(KeyEvent.VK_D) || isKeyPressed(KeyEvent.VK_RIGHT);

                if (movingUp) {
                    velocityY = speed;
                    animationKey = "ali_walk_up";
                }
                if (movingDown) {
                    velocityY = -speed;
                    animationKey = "ali_walk_down";
                }
                if (movingLeft) {
                    velocityX = -speed;
                    animationKey = "ali_walk_left";
                }
                if (movingRight) {
                    velocityX = speed;
                    animationKey = "ali_walk_right";
                }

                // Normalize diagonal movement
                double length = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
                if (length > 0) {
                    double normalizedSpeed = speed / length;
                    velocityX *= normalizedSpeed;
                    velocityY *= normalizedSpeed;
                }

                ali.setVelocityX(velocityX);
                ali.setVelocityY(velocityY);
                ali.setAnimation(animationKey);

                // Handle horizontal background scrolling based on Ali's position
                handleHorizontalBackgroundScroll(ali, speed, movingLeft, movingRight);
            }

            private boolean isKeyPressed(int keyCode) {
                return keyStates.getOrDefault(keyCode, false);
            }
        };
    }

    private void handleHorizontalBackgroundScroll(Ali ali, double speed, boolean movingLeft, boolean movingRight) {
        int aliX = ali.getX();
        int panelWidth = gamePanel.getWidth();
        int backgroundWidth = scrollingBackground.getImageWidth();
        int backgroundX = scrollingBackground.getBackgroundX();
        int scrollThreshold = panelWidth / 3;
        int backgroundScrollSpeed = 0;

        // Scroll right if Ali is moving right and near the right threshold
        if (movingRight && aliX > panelWidth - scrollThreshold && backgroundX > -(backgroundWidth - panelWidth)) {
            backgroundScrollSpeed = (int) -speed;
        } // Scroll left if Ali is moving left and near the left threshold
        else if (movingLeft && aliX < scrollThreshold && backgroundX < 0) {
            backgroundScrollSpeed = (int) speed;
        } // Stop scrolling if Ali is not moving or outside the thresholds
        else {
            backgroundScrollSpeed = 0;
        }

        scrollingBackground.setScrollSpeed(backgroundScrollSpeed);
    }
}

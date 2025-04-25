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
import com.simcraft.graphics.ScrollingBackground;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.graphics.screens.subpanels.InfoPanel;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;

/**
 * The main gameplay screen where the game logic and rendering occur. Handles
 * player input and updates the game state accordingly. This screen manages the
 * game panel, info panel, and the scrolling background, orchestrating the
 * gameplay experience.
 */
public final class GameplayScreen extends AbstractScreen {

    // ----- INSTANCE VARIABLES -----
    private final transient GameManager gameManager;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final Map<Integer, Boolean> keyStates;
    private final ScrollingBackground scrollingBackground;
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
        gamePanel.setScrollingBackground(scrollingBackground);

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

    private ScrollingBackground createScrollingBackground(Ali ali) {
        ScrollingBackground background = new ScrollingBackground(
                "/images/backgrounds/background_0.png",
                0,
                0,
                (int) ali.getSpeed(),
                "horizontal",
                gamePanel
        );
        background.setLevelWidth(levelWidth);
        return background;
    }

    private void updateScrollingBackground() {
        if (scrollingBackground.isScrollingHorizontally()) {
            scrollingBackground.setPlayerX((int) gameManager.getAli().getX());
        }
        if (scrollingBackground.isScrollingVertically()) {
            scrollingBackground.setPlayerY((int) gameManager.getAli().getY());
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

                if (scrollingBackground.isScrollingHorizontally()) {
                    velocityX = handleHorizontalScrolling(ali, speed, movingLeft, movingRight);
                    animationKey = getHorizontalAnimation(movingLeft, movingRight, animationKey);
                } else {
                    velocityX = handleBasicHorizontalMovement(ali, speed, movingLeft, movingRight);
                    animationKey = getHorizontalAnimation(movingLeft, movingRight, animationKey);
                }

                if (scrollingBackground.isScrollingVertically()) {
                    velocityY = handleVerticalScrolling(ali, speed, movingUp, movingDown);
                    animationKey = getVerticalAnimation(movingUp, movingDown, animationKey);
                } else {
                    velocityY = handleBasicVerticalMovement(ali, speed, movingUp, movingDown);
                    animationKey = getVerticalAnimation(movingUp, movingDown, animationKey);
                }

                double length = Math.sqrt(Math.pow(velocityX, 2) + Math.pow(velocityY, 2));
                if (length != 0) {
                    velocityX = (int) Math.round((velocityX / length) * speed);
                    velocityY = (int) Math.round((velocityY / length) * speed);
                }
                ali.setVelocityX(velocityX);
                ali.setVelocityY(velocityY);
                ali.setAnimation(animationKey);
            }

            private boolean isKeyPressed(int keyCode) {
                return keyStates.getOrDefault(keyCode, false);
            }
        };
    }

    private String getHorizontalAnimation(boolean movingLeft, boolean movingRight, String currentAnimation) {
        if (movingLeft) {
            return "ali_walk_left";
        }
        if (movingRight) {
            return "ali_walk_right";
        }
        return currentAnimation;
    }

    private String getVerticalAnimation(boolean movingUp, boolean movingDown, String currentAnimation) {
        if (movingUp) {
            return "ali_walk_up";
        }
        if (movingDown) {
            return "ali_walk_down";
        }
        return currentAnimation;
    }

    private double handleBasicHorizontalMovement(Ali ali, double speed, boolean movingLeft, boolean movingRight) {
        double velocityX = 0;
        if (movingLeft) {
            velocityX = -speed;
        }
        if (movingRight) {
            velocityX = speed;
        }
        ali.setX((int) Math.max(0, Math.min(ali.getX() + velocityX, gamePanel.getWidth() - ali.getSpriteWidth())));
        return velocityX;
    }

    private double handleBasicVerticalMovement(Ali ali, double speed, boolean movingUp, boolean movingDown) {
        double velocityY = 0;
        if (movingUp) {
            velocityY = speed;
        }
        if (movingDown) {
            velocityY = -speed;
        }
        ali.setY((int) Math.max(0, Math.min(ali.getY() - velocityY, gamePanel.getHeight() - ali.getSpriteHeight())));
        return velocityY;
    }

    private double handleHorizontalScrolling(Ali ali, double speed, boolean movingLeft, boolean movingRight) {
        double velocityX = 0;
        int aliX = (int) ali.getX();
        int panelWidth = gamePanel.getWidth();
        int backgroundWidth = scrollingBackground.getImageWidth();
        int backgroundX = scrollingBackground.getBackgroundX();
        int levelRightBoundary = this.levelWidth;

        int scrollStart = panelWidth / 3;
        int scrollEnd = levelRightBoundary - panelWidth / 3;

        if (movingRight && aliX > scrollStart && aliX < scrollEnd && backgroundX > -(backgroundWidth - panelWidth)) {
            velocityX = -speed; // Move background left
        } else if (movingLeft && aliX < scrollEnd && aliX > scrollStart && backgroundX < 0) {
            velocityX = speed; // Move background right
        } else {
            velocityX = 0; // Stop background movement
        }

        if (!movingRight && aliX > panelWidth - ali.getSpriteWidth() && backgroundX >= -(backgroundWidth - panelWidth)) {
            ali.setX(panelWidth - ali.getSpriteWidth());
        }
        if (!movingLeft && aliX < 0 && backgroundX <= 0) {
            ali.setX(0);
        }

        return velocityX;
    }

    private double handleVerticalScrolling(Ali ali, double speed, boolean movingUp, boolean movingDown) {
        int aliY = (int) ali.getY();
        int thresholdY = gamePanel.getHeight() / 2;
        int backgroundY = scrollingBackground.getBackgroundY();
        int backgroundHeight = scrollingBackground.getImageHeight();
        double velocityY = 0;

        if (aliY <= thresholdY) {
            if (movingUp) {
                if (backgroundY < 0) {
                    velocityY = speed; // Scroll down
                } else {
                    velocityY = speed; // Move player up
                }
            } else if (movingDown) {
                velocityY = -speed; // Move player down
            }
        } else {
            if (movingUp) {
                velocityY = speed; // Move player up
            } else if (movingDown) {
                if (backgroundY > -(backgroundHeight - gamePanel.getHeight())) {
                    velocityY = -speed; // Scroll up
                } else {
                    velocityY = -speed; // Move player down
                }
            }
        }
        return velocityY;
    }
}

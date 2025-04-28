package com.simcraft.graphics.screens;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simcraft.entities.Ali;
import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.graphics.screens.subpanels.InfoPanel;
import com.simcraft.levels.LevelConfig;
import com.simcraft.levels.LevelLibrary;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;
import com.simcraft.managers.SoundManager;

/**
 * Main gameplay screen managing scrolling, player input, and level transitions.
 */
public final class GameplayScreen extends AbstractScreen {

    private final transient GameManager gameManager;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final Map<Integer, Boolean> keyStates;
    private int currentLevelIndex = 0;
    private boolean atLevelEnd = false; // Shows "Enter" arrow when true

    public GameplayScreen(GameFrame gameFrame) {
        super(gameFrame);
        setLayout(new BorderLayout());

        LevelConfig levelConfig = LevelLibrary.getLevel(currentLevelIndex);

        // Load all background tiles
        List<String> backgroundPaths = levelConfig.getBackgroundImagePaths();
        BufferedImage[] backgroundTiles = backgroundPaths.stream()
                .map(ImageManager::loadBufferedImage)
                .toArray(BufferedImage[]::new);

        int infoPanelHeight = 100;
        infoPanel = new InfoPanel(
                GameFrame.FRAME_WIDTH,
                infoPanelHeight,
                "/images/backgrounds/info-panel.png"
        );

        gamePanel = new GamePanel(
                GameFrame.FRAME_WIDTH,
                GameFrame.FRAME_HEIGHT - infoPanelHeight,
                backgroundTiles
        );

        add(infoPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        gameManager = GameManager.getInstance();
        gameManager.init(gamePanel, infoPanel);

        keyStates = new HashMap<>();
        addKeyListener(createKeyListener());

        SoundManager soundManager = SoundManager.getInstance();
        soundManager.stopAll();
        soundManager.playClip(levelConfig.getMusicClipName(), true);
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

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

            if (atLevelEnd) {
                gamePanel.drawEnterArrow(g2d);
            }
        }
    }

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

                // Scroll logic
                int centerThreshold = gamePanel.getWidth() / 2;
                int aliX = (int) ali.getX();
                double scrollOffset = gamePanel.getScrollOffset();
                int totalScrollableWidth = (gamePanel.getTileCount() * gamePanel.getTileWidth()) - gamePanel.getWidth();

                if (velocityX > 0) {
                    if (aliX >= centerThreshold && scrollOffset < totalScrollableWidth) {
                        gamePanel.setScrollOffset(scrollOffset + velocityX);
                        ali.setVelocityX(0);
                    } else if (scrollOffset >= totalScrollableWidth) {
                        atLevelEnd = true;
                        ali.setVelocityX(0); // Stop Ali from moving
                    }
                } else if (velocityX < 0 && scrollOffset > 0) {
                    if (aliX <= centerThreshold) {
                        gamePanel.setScrollOffset(scrollOffset + velocityX);
                        ali.setVelocityX(0);
                    }
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {
                keyStates.put(e.getKeyCode(), true);
                updateAliMovement();

                if (e.getKeyCode() == KeyEvent.VK_ENTER && atLevelEnd) {
                    loadNextLevel();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                keyStates.put(e.getKeyCode(), false);
                updateAliMovement();
            }
        };
    }

    /**
     * Loads the next level if available.
     */
    private void loadNextLevel() {
        if (currentLevelIndex + 1 >= LevelLibrary.getTotalLevels()) {
            System.out.println("You've finished all available levels!");
            return;
        }

        currentLevelIndex++;
        atLevelEnd = false;

        // Load new level
        LevelConfig levelConfig = LevelLibrary.getLevel(currentLevelIndex);
        List<String> backgroundPaths = levelConfig.getBackgroundImagePaths();
        BufferedImage[] backgroundTiles = backgroundPaths.stream()
                .map(ImageManager::loadBufferedImage)
                .toArray(BufferedImage[]::new);

        gamePanel.loadNewBackground(backgroundTiles);
        SoundManager.getInstance().stopAll();
        SoundManager.getInstance().playClip(levelConfig.getMusicClipName(), true);
    }
}
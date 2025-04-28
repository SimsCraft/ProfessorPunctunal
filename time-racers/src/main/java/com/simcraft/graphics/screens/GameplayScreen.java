package com.simcraft.graphics.screens;

import java.awt.*;
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
import com.simcraft.levels.LevelType;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;
import com.simcraft.managers.SoundManager;

public final class GameplayScreen extends AbstractScreen {

    private boolean cinematicWalk = false;
    private boolean jumping = false;
    private double jumpProgress = 0;

    private final transient GameManager gameManager;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final Map<Integer, Boolean> keyStates;
    private int currentLevelIndex = 0;
    private boolean atLevelEnd = false;
    private boolean fadingOut = false;
    private float fadeOpacity = 0f;
    private final float fadeSpeed = 0.02f;

    private boolean showLevelText = false;
    private float levelTextOpacity = 0f;
    private String nextLevelName = "";

    private LevelType currentLevelType = LevelType.TOP_DOWN; // NEW

    public GameplayScreen(GameFrame gameFrame) {
        super(gameFrame);
        setLayout(new BorderLayout());

        LevelConfig levelConfig = LevelLibrary.getLevel(currentLevelIndex);
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
        soundManager.playClip("background", true);

        currentLevelType = levelConfig.getLevelType();
        applyLevelSettings();
    }

    private void loadLevel(int index) {
        LevelConfig levelConfig = LevelLibrary.getLevel(index);
        List<String> backgroundPaths = levelConfig.getBackgroundImagePaths();
        BufferedImage[] backgroundTiles = backgroundPaths.stream()
                .map(ImageManager::loadBufferedImage)
                .toArray(BufferedImage[]::new);

        gamePanel.loadNewBackground(backgroundTiles);
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.stopAll();
        soundManager.playClip("background", true);

        nextLevelName = "LEVEL " + (currentLevelIndex + 1);
        showLevelText = true;
        levelTextOpacity = 1.0f;

        currentLevelType = levelConfig.getLevelType();
        applyLevelSettings();
    }

    private void applyLevelSettings() {
        Ali ali = gameManager.getAli();
    
        if (currentLevelType == LevelType.SIDE_SCROLLING) {
            ali.setScale(4.0);
            ali.setHorizontalOnly(true);
            ali.setYOrigin(ali.getY()); // Save current Y for jumping
    
            // Update all enemies
            gameManager.getEnemyManager().getEnemies().forEach(enemy -> {
                enemy.setScale(4.0);
                enemy.setHorizontalOnly(true);
                enemy.setYOrigin(enemy.getY());
            });
    
        } else { // TOP_DOWN
            ali.setScale(1.0);
            ali.setHorizontalOnly(false);
    
            // Update all enemies
            gameManager.getEnemyManager().getEnemies().forEach(enemy -> {
                enemy.setScale(1.0);
                enemy.setHorizontalOnly(false);
            });
        }
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
            handleFade();
            handleCinematicWalk();
            handleLevelTextFade();
            handleJump();
            checkLevelEnd();
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        if (gameManager.isRunning()) {
            gamePanel.safeRender(g2d);
            infoPanel.safeRender(g2d);

            if (fadingOut) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, fadeOpacity));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }

            if (showLevelText) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, levelTextOpacity));
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 48));
                String text = nextLevelName;
                int textWidth = g2d.getFontMetrics().stringWidth(text);
                g2d.drawString(text, (getWidth() - textWidth) / 2, getHeight() / 2);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            }
        }
    }

    private KeyAdapter createKeyListener() {
        return new KeyAdapter() {
            private void updateAliMovement() {
                if (cinematicWalk) return;
                Ali ali = gameManager.getAli();
                double speed = ali.getSpeed();
                double velocityX = 0;
                double velocityY = 0;
                String animationKey = "ali_walk_right";

                if (currentLevelType == LevelType.TOP_DOWN) {
                    if (keyStates.getOrDefault(KeyEvent.VK_W, false)) {
                        velocityY = speed;
                        animationKey = "ali_walk_up";
                    }
                    if (keyStates.getOrDefault(KeyEvent.VK_S, false)) {
                        velocityY = -speed;
                        animationKey = "ali_walk_down";
                    }
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

                if (keyStates.getOrDefault(KeyEvent.VK_SPACE, false) && !jumping && currentLevelType == LevelType.SIDE_SCROLLING) {
                    startJump();
                }
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

    private void startJump() {
        jumping = true;
        jumpProgress = 0;
    }

    private void handleJump() {
        if (jumping) {
            jumpProgress += 0.05;

            double jumpHeight = -Math.pow((jumpProgress - 1), 2) + 1;
            double scaledJump = 10 * jumpHeight;

            Ali ali = gameManager.getAli();
            ali.setY(ali.getYOrigin() - scaledJump);

            if (jumpProgress >= 2.0) {
                jumping = false;
                ali.setY(ali.getYOrigin());
            }
        }
    }

    private void handleCinematicWalk() {
        if (!cinematicWalk) return;
    
        Ali ali = gameManager.getAli();
        double speed = 2.0; // Slow walking speed
        ali.setAnimation("ali_walk_right");
    
        double aliWorldX = ali.getX() + gamePanel.getScrollOffset();
        int totalWorldWidth = gamePanel.getTileCount() * gamePanel.getTileWidth();
        double scrollOffset = gamePanel.getScrollOffset();
    
        // If Ali reaches near the end of level, stop cinematic
        if (aliWorldX >= totalWorldWidth - 50) {
            ali.setVelocityX(0);
            cinematicWalk = false;
            startFadeOut();
            return;
        }
    
        if (ali.getX() >= gamePanel.getWidth() / 2) {
            if (scrollOffset < totalWorldWidth - gamePanel.getWidth()) {
                // If we can still scroll background
                gamePanel.setScrollOffset(scrollOffset + speed);
                ali.setVelocityX(0); // Stop Ali while background scrolls
            } else {
                // Can't scroll background anymore -> Move Ali normally
                ali.setVelocityX(speed);
            }
        } else {
            // Ali still moving towards center of screen
            ali.setVelocityX(speed);
        }
    
        ali.setVelocityY(0);
    }

    private void handleFade() {
        if (fadingOut) {
            fadeOpacity += fadeSpeed;
            if (fadeOpacity >= 1f) {
                fadeOpacity = 1f;
                completeLevelTransition();
            }
        }
    }

    private void startFadeOut() {
        fadingOut = true;
    }

    private void completeLevelTransition() {
        fadingOut = false;
        fadeOpacity = 0f;

        currentLevelIndex++;
        atLevelEnd = false;

        if (currentLevelIndex >= LevelLibrary.getTotalLevels()) {
            System.out.println("You've finished all levels!");
            return;
        }

        removeAll();
        loadLevel(currentLevelIndex);
        revalidate();
        repaint();
    }

    private void checkLevelEnd() {
        Ali ali = gameManager.getAli();
        double aliX = ali.getX();
        double scrollOffset = gamePanel.getScrollOffset();

        int totalScrollableWidth = (gamePanel.getTileCount() * gamePanel.getTileWidth());
        int playerOffsetX = (int) (aliX + scrollOffset);

        if (playerOffsetX >= totalScrollableWidth - 50) {
            atLevelEnd = true;
            startFadeOut();
        }
    }

    private void handleLevelTextFade() {
        if (showLevelText) {
            levelTextOpacity -= 0.01f;
            if (levelTextOpacity <= 0f) {
                showLevelText = false;
                levelTextOpacity = 0f;
            }
        }
    }
}
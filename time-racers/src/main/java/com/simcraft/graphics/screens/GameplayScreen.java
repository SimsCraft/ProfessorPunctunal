package com.simcraft.graphics.screens;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
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

public final class GameplayScreen extends AbstractScreen {

    private boolean cinematicWalk = false;

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
        soundManager.playClip(levelConfig.getMusicClipName(), true);
    }

    private void loadLevel(int index) {
        LevelConfig levelConfig = LevelLibrary.getLevel(index);
        List<String> backgroundPaths = levelConfig.getBackgroundImagePaths();
        BufferedImage[] backgroundTiles = backgroundPaths.stream()
                .map(ImageManager::loadBufferedImage)
                .toArray(BufferedImage[]::new);
    
        gamePanel.loadNewBackground(backgroundTiles);
        gamePanel.setScrollOffset(0);
    
    
        gameManager.init(gamePanel, infoPanel);
    
        SoundManager soundManager = SoundManager.getInstance();
        soundManager.stopAll();
        soundManager.playClip(levelConfig.getMusicClipName(), true);
    
        nextLevelName = "LEVEL " + (currentLevelIndex + 1);
        showLevelText = true;
        levelTextOpacity = 1.0f;
        cinematicWalk = true;
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
            handleLevelTextFade();

        if (!cinematicWalk) {
            checkPlayerCollisionWithArrow(); // <- ADD THIS!!!
        } else {
    handleCinematicWalk();
}               
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
                if (cinematicWalk) return; // disable input during cinematic

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

                int aliX = (int) ali.getX();
                double scrollOffset = gamePanel.getScrollOffset();
                int totalScrollableWidth = (gamePanel.getTileCount() * gamePanel.getTileWidth());

                if (velocityX > 0 && scrollOffset < totalScrollableWidth - gamePanel.getWidth()) {
                    if (aliX >= gamePanel.getWidth() / 2) {
                        gamePanel.setScrollOffset(scrollOffset + velocityX);
                        ali.setVelocityX(0);
                    }
                } else if (velocityX < 0 && scrollOffset > 0) {
                    if (aliX <= gamePanel.getWidth() / 2) {
                        gamePanel.setScrollOffset(scrollOffset + velocityX);
                        ali.setVelocityX(0);
                    }
                }
                // Ali can move freely if scrolling is over
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
            gameManager.setGameOver();
            return;
        }
    
        // REMOVE old panels before adding new ones
        remove(infoPanel);
        remove(gamePanel);
    
        loadLevel(currentLevelIndex);
        revalidate();
        repaint();
    }

    private void checkPlayerCollisionWithArrow() {
        Ali ali = gameManager.getAli();
        double aliX = ali.getX();
        double scrollOffset = gamePanel.getScrollOffset();

        int totalScrollableWidth = (gamePanel.getTileCount() * gamePanel.getTileWidth());
        int playerOffsetX = (int) (aliX + scrollOffset);

        int arrowX = totalScrollableWidth - 100;

        if (playerOffsetX >= arrowX - 30) {
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

    private void handleCinematicWalk() {
        if (!cinematicWalk) return;
    
        Ali ali = gameManager.getAli();
        ali.setVelocityX(2.0); // slow walking speed
        ali.setVelocityY(0);
        ali.setAnimation("ali_walk_right");
    
        double aliWorldX = ali.getX() + gamePanel.getScrollOffset();
        int totalWorldWidth = gamePanel.getTileCount() * gamePanel.getTileWidth();
    
        if (aliWorldX >= totalWorldWidth - 50) { // small margin
            ali.setVelocityX(0);
            cinematicWalk = false;
            startFadeOut(); // trigger fade immediately
        }
    
        // Scroll background if Ali is at center of screen
        if (ali.getX() >= gamePanel.getWidth() / 2 && gamePanel.getScrollOffset() < totalWorldWidth - gamePanel.getWidth()) {
            double scrollOffset = gamePanel.getScrollOffset();
            gamePanel.setScrollOffset(scrollOffset + ali.getVelocityX());
            ali.setVelocityX(0); // stop Ali itself if background scrolling
        }
    }
}
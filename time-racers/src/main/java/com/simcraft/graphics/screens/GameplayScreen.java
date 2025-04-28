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
import com.simcraft.levels.LevelType;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;
import com.simcraft.managers.SoundManager;

/**
 * The {@code GameplayScreen} class represents the primary screen where the
 * actual gameplay takes place. It manages the game world, player input, level
 * transitions, and rendering of game elements.
 */
public final class GameplayScreen extends AbstractScreen {

    // ----- INSTANCE VARIABLES -----
    private final transient GameManager gameManager;
    private final transient SoundManager soundManager;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private final Map<Integer, Boolean> keyStates;
    private final int levelWidth = 2000;
    private final float fadeSpeed = 0.02f;

    private boolean atLevelEnd = false;
    private boolean fadingOut = false;
    private boolean cinematicWalk = false;
    private boolean jumping = false;
    private boolean showLevelText = false;
    private double jumpProgress = 0;
    private int currentLevelIndex = 0;
    private float fadeOpacity = 0f;
    private float levelTextOpacity = 0f;
    private String nextLevelName = "";
    private LevelType currentLevelType = LevelType.TOP_DOWN; // NEW

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a new {@code GameplayScreen}.
     *
     * @param gameFrame The main {@link GameFrame} that contains this screen.
     * Initializes the layout, sub-panels ({@link GamePanel},
     * {@link InfoPanel}), game manager, key listener, and loads the first
     * level.
     */
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
                "/images/backgrounds/info_panel.png"
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

        soundManager = SoundManager.getInstance();
        soundManager.stopAll();
        soundManager.playClip("background", true);

        currentLevelType = levelConfig.getLevelType();
        applyLevelSettings();
    }

    // ----- GETTERS -----
    /**
     * Returns the {@link GamePanel} instance used by this screen.
     *
     * @return The {@code GamePanel} responsible for rendering the game world.
     */
    public GamePanel getGamePanel() {
        return gamePanel;
    }

    /**
     * Returns the {@link InfoPanel} instance used by this screen.
     *
     * @return The {@code InfoPanel} responsible for displaying game
     * information.
     */
    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates the state of the gameplay screen. This includes updating the game
     * manager, handling fade effects, cinematic sequences, level text display,
     * player jumping, and checking for the end of the level.
     */
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

    /**
     * Renders the elements of the gameplay screen. This includes rendering the
     * {@link GamePanel}, {@link InfoPanel}, fade effects during level
     * transitions, and the level name text at the start of each level.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
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

    // ---- HELPER METHODS -----
    /**
     * Creates a {@link KeyAdapter} to handle keyboard input. Updates the
     * {@code keyStates} map with the pressed and released keys and calls
     * {@link #updateAliMovement()} to adjust the player's movement accordingly.
     *
     * @return A new {@code KeyAdapter} for handling key events.
     */
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
        };
    }

    /**
     * Updates the movement of the player character ({@link Ali}) based on the
     * current state of the pressed keys. It determines the velocity and
     * animation of Ali based on the input and the current {@link LevelType}.
     * Also handles playing and stopping the player's movement sound and
     * initiates jumping in side-scrolling levels when the space key is pressed.
     */
    private void updateAliMovement() {
        if (cinematicWalk) {
            return;
        }
        Ali ali = gameManager.getAli();
        double speed = ali.getSpeed();
        double velocityX = 0;
        double velocityY = 0;
        String animationKey = "ali_walk_right";
        boolean isMoving = false;
        String playerMovingSoundKey = "person_running";

        if (currentLevelType == LevelType.TOP_DOWN) {
            if (keyStates.getOrDefault(KeyEvent.VK_W, false)) {
                velocityY = speed;
                animationKey = "ali_walk_up";
                isMoving = true;
            }
            if (keyStates.getOrDefault(KeyEvent.VK_S, false)) {
                velocityY = -speed;
                animationKey = "ali_walk_down";
                isMoving = true;
            }
        }

        if (keyStates.getOrDefault(KeyEvent.VK_A, false)) {
            velocityX = -speed;
            animationKey = "ali_walk_left";
            isMoving = true;
        }
        if (keyStates.getOrDefault(KeyEvent.VK_D, false)) {
            velocityX = speed;
            animationKey = "ali_walk_right";
            isMoving = true;
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
        gamePanel.setScrollOffset((int) (gamePanel.getScrollOffset() + velocityX));

        if (isMoving && !soundManager.isClipPlaying(playerMovingSoundKey)) {
            soundManager.playClip(playerMovingSoundKey, true);
        } else if (!isMoving) {
            soundManager.stopClip(playerMovingSoundKey);
            if (keyStates.getOrDefault(KeyEvent.VK_SPACE, false) && !jumping && currentLevelType == LevelType.SIDE_SCROLLING) {
                startJump();
            }
        }
    }

    /**
     * Initiates a jump for the player character in side-scrolling levels. Sets
     * the {@code jumping} flag to true and resets the {@code jumpProgress}.
     */
    private void startJump() {
        jumping = true;
        jumpProgress = 0;
    }

    /**
     * Handles the jumping motion of the player character. Updates the player's
     * vertical position based on a parabolic curve defined by
     * {@code jumpProgress}. Resets the {@code jumping} flag when the jump is
     * complete.
     */
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

    /**
     * Handles a cinematic walking sequence, typically triggered at the
     * beginning of a level or after certain events. During this sequence, the
     * player character moves automatically to the right, and the background may
     * scroll to keep the player in view. Once the player reaches near the end
     * of the visible level area, the cinematic walk ends, and a fade-out
     * transition begins.
     */
    private void handleCinematicWalk() {
        if (!cinematicWalk) {
            return;
        }

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
                gamePanel.setScrollOffset((int) (scrollOffset + speed));
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

    /**
     * Handles the fade-out effect used during level transitions. Gradually
     * increases the opacity of a black overlay. Once the overlay is fully
     * opaque, it triggers the completion of the level transition.
     */
    private void handleFade() {
        if (fadingOut) {
            fadeOpacity += fadeSpeed;
            if (fadeOpacity >= 1f) {
                fadeOpacity = 1f;
                completeLevelTransition();
            }
        }
    }

    /**
     * Initiates the fade-out process for a level transition by setting the
     * {@code fadingOut} flag to true.
     */
    private void startFadeOut() {
        fadingOut = true;
    }

    /**
     * Completes the level transition process. Resets the fade opacity,
     * increments the current level index, resets the level end flag, and loads
     * the next level if available. If all levels are completed, it prints a
     * completion message. Finally, it removes all components from the screen,
     * loads the new level, and revalidates and repaints the screen.
     */
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

    /**
     * Checks if the player character has reached the end of the current level.
     * The end of the level is determined by the player's horizontal position
     * relative to the total scrollable width of the game panel. If the player
     * is near the end, the {@code atLevelEnd} flag is set, and a fade-out
     * transition is initiated.
     */
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

    /**
     * Handles the fading out of the level name text displayed at the beginning
     * of each level. Gradually decreases the opacity of the text until it
     * becomes fully transparent, at which point the text is no longer shown.
     */
    private void handleLevelTextFade() {
        if (showLevelText) {
            levelTextOpacity -= 0.01f;
            if (levelTextOpacity <= 0f) {
                showLevelText = false;
                levelTextOpacity = 0f;
            }
        }
    }

    /**
     * Loads a specific level based on its index.
     *
     * @param index The index of the level to load from {@link LevelLibrary}.
     * Updates the background of the {@link GamePanel}, stops all existing
     * sounds, plays the background music for the new level, sets the next level
     * name for display, and applies level-specific settings.
     */
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

    /**
     * Applies level-specific settings, such as scaling and movement constraints
     * for the player character ({@link Ali}) and enemies, based on the
     * {@link LevelType} of the current level.
     */
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

}

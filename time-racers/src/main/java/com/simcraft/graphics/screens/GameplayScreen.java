package com.simcraft.graphics.screens;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.simcraft.entities.*;
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

    // ----- CONSTANTS -----
    /**
     * The height of the information panel at the top of the screen.
     */
    private static final int INFO_PANEL_HEIGHT = 100;
    /**
     * The speed at which the fade-out effect occurs.
     */
    private static final float FADE_SPEED = 0.02f;
    /**
     * The duration of the damage flash effect in frames.
     */
    private static final int FLASH_DURATION = 30; // frames
    /**
     * The speed of the player character during a cinematic walk sequence.
     */
    private static final double CINEMATIC_WALK_SPEED = 2.0;
    /**
     * The initial upward velocity applied when the player jumps.
     */
    private static final double JUMP_INITIAL_VELOCITY = 12.0;
    /**
     * The increment value for the jump progress, controlling the jump curve.
     */
    private static final double JUMP_PROGRESS_INCREMENT = 0.04;
    /**
     * The maximum height reached during a jump.
     */
    private static final double MAX_JUMP_HEIGHT = 80.0;
    /**
     * The horizontal offset from the end of the level to trigger level
     * completion.
     */
    private static final int LEVEL_END_OFFSET = 50;
    /**
     * The horizontal offset for placing the teleport arrow at the end of a
     * top-down level.
     */
    private static final int TELEPORT_ARROW_OFFSET_X = 150;
    /**
     * The horizontal offset for placing the enter classroom object at the end
     * of a side-scrolling level.
     */
    private static final int CLASSROOM_OFFSET_X = 300;
    /**
     * The vertical offset from the bottom of the screen to position entities in
     * side-scrolling levels.
     */
    private static final int SIDE_SCROLLING_GROUND_Y_OFFSET = 150;

    // ----- INSTANCE VARIABLES -----
    /**
     * The singleton instance of the game manager.
     */
    private final transient GameManager gameManager;
    /**
     * The singleton instance of the sound manager.
     */
    private final transient SoundManager soundManager;
    /**
     * The panel responsible for rendering the game world.
     */
    private GamePanel gamePanel;
    /**
     * The panel displaying game information (score, time, etc.).
     */
    private final InfoPanel infoPanel;
    /**
     * A map storing the current state (pressed or released) of each relevant
     * key.
     */
    private final Map<Integer, Boolean> keyStates;
    /**
     * The fixed width of the current level in pixels.
     */
    private final int levelWidth = 2000;
    /**
     * Flag indicating if the player has reached the end of the current level.
     */
    private boolean atLevelEnd = false;
    /**
     * Flag indicating if a fade-out transition is currently in progress.
     */
    private boolean fadingOut = false;
    /**
     * Flag indicating if a cinematic walk sequence is currently active.
     */
    private boolean cinematicWalk = false;
    /**
     * Flag indicating if the player character is currently jumping.
     */
    private boolean jumping = false;
    /**
     * Flag indicating if the level name text should be displayed.
     */
    private boolean showLevelText = false;
    /**
     * The progress of the current jump animation (0.0 to 1.0).
     */
    private double jumpProgress = 0;
    /**
     * The index of the currently loaded level in the {@link LevelLibrary}.
     */
    private int currentLevelIndex = 0;
    /**
     * The current opacity of the fade-out overlay (0.0 to 1.0).
     */
    private float fadeOpacity = 0f;
    /**
     * The current opacity of the level name text (0.0 to 1.0).
     */
    private float levelTextOpacity = 0f;
    /**
     * The name of the next level to be displayed.
     */
    private String nextLevelName = "";
    /**
     * The type of the currently loaded level (TOP_DOWN or SIDE_SCROLLING).
     */
    private LevelType currentLevelType = LevelType.TOP_DOWN;
    /**
     * A timer used to control the duration of the damage flash effect.
     */
    private int flashTimer = 0;

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

        infoPanel = new InfoPanel(
                GameFrame.FRAME_WIDTH,
                INFO_PANEL_HEIGHT,
                "/images/backgrounds/info_panel.png"
        );
        add(infoPanel, BorderLayout.NORTH);

        gameManager = GameManager.getInstance();
        soundManager = SoundManager.getInstance();

        keyStates = new HashMap<>();
        addKeyListener(createKeyListener());

        loadLevel(currentLevelIndex);
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
     * damage effects, player jumping, and collisions.
     */
    @Override
    public void update() {
        if (gameManager.isRunning()) {
            gameManager.update();
            handleFade();
            handleCinematicWalk();
            handleLevelTextFade();
            handleDamageEffects();
            handleJump();
            handleCollisions();
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
                int textWidth = g2d.getFontMetrics().stringWidth(nextLevelName);
                g2d.drawString(nextLevelName, (getWidth() - textWidth) / 2, getHeight() / 2);
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
            /**
             * Updates the movement of the player character ({@link Ali}) based
             * on the current state of the pressed keys. It determines the
             * velocity and animation of Ali based on the input and the current
             * {@link LevelType}. Also handles initiating jumping in
             * side-scrolling levels when the space key is pressed.
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

                if (keyStates.getOrDefault(KeyEvent.VK_SPACE, false) && !ali.isJumping()
                        && currentLevelType == LevelType.SIDE_SCROLLING) {
                    ali.jump(JUMP_INITIAL_VELOCITY);
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

    /**
     * Handles visual effects when the player character takes damage, such as a
     * brief flash, and applies the damage (e.g., reducing time).
     */
    private void handleDamageEffects() {
        Ali ali = gameManager.getAli();
        if (flashTimer > 0) {
            flashTimer--;
        }

        gameManager.getEnemyManager().getEnemies().forEach(enemy -> {
            if (ali.getBounds().intersects(enemy.getBounds())) {
                if (flashTimer == 0) {
                    flashTimer = FLASH_DURATION;
                    gamePanel.spawnFloatingText("-10s", (int) ali.getX(), (int) ali.getY() - 40, Color.RED);
                    gameManager.reduceTime(10); // Decrease time
                }
            }
        });
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
        ali.setAnimation("ali_walk_right");

        double aliWorldX = ali.getX() + gamePanel.getScrollOffset();
        int totalWorldWidth = gamePanel.getTileCount() * gamePanel.getTileWidth();
        double scrollOffset = gamePanel.getScrollOffset();

        if (aliWorldX >= totalWorldWidth - LEVEL_END_OFFSET) {
            ali.setVelocityX(0);
            cinematicWalk = false;
            startFadeOut();
            return;
        }

        if (ali.getX() >= gamePanel.getWidth() / 2) {
            if (scrollOffset < totalWorldWidth - gamePanel.getWidth()) {
                gamePanel.setScrollOffset((int) (scrollOffset + CINEMATIC_WALK_SPEED));
                ali.setVelocityX(0);
            } else {
                ali.setVelocityX(CINEMATIC_WALK_SPEED);
            }
        } else {
            ali.setVelocityX(CINEMATIC_WALK_SPEED);
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
            fadeOpacity += FADE_SPEED;
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

        gameManager.setRemainingSeconds(300);

        if (currentLevelIndex >= LevelLibrary.getTotalLevels()) {
            System.out.println("You've finished all levels!");
            return;
        }

        removeAll();
        loadLevel(currentLevelIndex);
        add(infoPanel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    /**
     * Handles the jumping motion of the player character. Updates the player's
     * vertical position based on a sine wave to create a smooth jump arc.
     * Resets the jumping state when the jump is complete.
     */
    private void handleJump() {
        if (jumping) {
            jumpProgress += JUMP_PROGRESS_INCREMENT;

            double jumpHeight = Math.sin(Math.PI * jumpProgress);
            double scaledJump = MAX_JUMP_HEIGHT * jumpHeight;

            Ali ali = gameManager.getAli();
            ali.setY((int) (ali.getYOrigin() - scaledJump));

            if (jumpProgress >= 1.0) {
                jumping = false;
                ali.setY((int) ali.getYOrigin());
            }
        }
    }

    /**
     * Checks for collisions between the player character and special objects
     * within the current level, such as the teleport arrow or the enter
     * classroom trigger, and initiates actions based on these collisions.
     */
    private void handleCollisions() {
        Ali ali = gameManager.getAli();
        Rectangle aliBounds = ali.getBounds();

        if (gamePanel.getTeleportArrow() != null && aliBounds.intersects(gamePanel.getTeleportArrowBounds())) {
            startFadeOut();
        }

        if (gamePanel.getEnterClassroom() != null && aliBounds.intersects(gamePanel.getEnterClassroomBounds())) {
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

        gamePanel = new GamePanel(
                GameFrame.FRAME_WIDTH,
                GameFrame.FRAME_HEIGHT - INFO_PANEL_HEIGHT,
                backgroundTiles
        );
        add(gamePanel, BorderLayout.CENTER);

        gameManager.init(gamePanel, infoPanel);
        gameManager.getEnemyManager().clear();

        nextLevelName = "LEVEL " + (index + 1);
        infoPanel.updateLevelCounter(index + 1);
        showLevelText = true;
        levelTextOpacity = 1.0f;

        currentLevelType = levelConfig.getLevelType();
        applyLevelSettings();

        soundManager.stopAll();
        soundManager.playClip("background", true);
    }

    /**
     * Applies level-specific settings, such as scaling and movement constraints
     * for the player character ({@link Ali}) and enemies, and places special
     * objects within the game world based on the current {@link LevelType}.
     */
    private void applyLevelSettings() {
        Ali ali = gameManager.getAli();
        ali.resetPosition(0, gamePanel.getHeight() / 2.0);
        gamePanel.clearSpecialObjects();

        if (currentLevelType == LevelType.SIDE_SCROLLING) {
            setupSideScrollingLevel(ali);
        } else {
            setupTopDownLevel(ali);
        }
    }

    /**
     * Sets up the game world for a side-scrolling level. This includes setting
     * the player's scale and movement constraints, positioning the player at
     * the start of the level, and placing the "enter classroom" object at the
     * end.
     *
     * @param ali The player character ({@link Ali}).
     */
    private void setupSideScrollingLevel(Ali ali) {
        ali.setScale(4.0);
        ali.setHorizontalOnly(true);
        ali.setYOrigin(gamePanel.getHeight() - SIDE_SCROLLING_GROUND_Y_OFFSET);
        ali.setWorldPosition(0, ali.getYOrigin());

        gameManager.getEnemyManager().getEnemies().forEach(enemy -> {
            enemy.setScale(4.0);
            enemy.setHorizontalOnly(true);
            enemy.setYOrigin(gamePanel.getHeight() - SIDE_SCROLLING_GROUND_Y_OFFSET);
            enemy.setWorldPosition(enemy.getWorldX(), enemy.getYOrigin());
        });

        double endX = (gamePanel.getTileCount() * gamePanel.getTileWidth()) - CLASSROOM_OFFSET_X;
        gamePanel.setEnterClassroom(new EnterClassroom((int) endX, (int) ali.getYOrigin()));
    }

    /**
     * Sets up the game world for a top-down level. This includes setting the
     * player's scale and movement constraints, positioning the player at the
     * start of the level, and placing the teleport arrow at the end.
     *
     * @param ali The player character ({@link Ali}).
     */
    private void setupTopDownLevel(Ali ali) {
        ali.setScale(1.0);
        ali.setHorizontalOnly(false);
        ali.setWorldPosition(0, gamePanel.getHeight() / 2.0);

        gameManager.getEnemyManager().getEnemies().forEach(enemy -> {
            enemy.setScale(1.0);
            enemy.setHorizontalOnly(false);
        });

        double endX = (gamePanel.getTileCount() * gamePanel.getTileWidth()) - TELEPORT_ARROW_OFFSET_X;
        gamePanel.setTeleportArrow(new TeleportArrow((int) endX, (int) (gamePanel.getHeight() / 2.0 - 50)));
    }
}

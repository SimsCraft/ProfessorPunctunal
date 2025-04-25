package com.simcraft.managers;

import java.awt.Point;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.Timer;

import com.simcraft.entities.Ali;
import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.graphics.screens.subpanels.InfoPanel;
import com.simcraft.graphics.states.GameState;
import com.simcraft.graphics.states.GameState.State;
import com.simcraft.graphics.states.LevelState;
import com.simcraft.interfaces.Updateable;

/**
 * GameManager serves as the central controller for managing the overall game state,
 * active level, player character, and enemy logic.
 *
 * Responsibilities include:
 * - Initializing and updating game entities
 * - Managing game state transitions (e.g., RUNNING, PAUSED)
 * - Tracking and updating the current level
 * - Handling game timers and game over logic
 * - Providing global access through Singleton pattern
 */
public class GameManager implements Updateable {

    // ----- STATIC VARIABLES -----
    /**
     * Singleton instance of the {@link GameManager}.
     * <p>
     * This static field holds the unique instance of the {@link GameManager}.
     * It is lazily initialized when {@link #getInstance()} is first called. The
     * singleton pattern ensures that only one instance of the
     * {@link GameManager} exists throughout the lifetime of the application.
     */
    private static GameManager instance;

    // ----- INSTANCE VARIABLES -----

    /**
     * Tracks the game's lifecycle state (RUNNING, PAUSED, etc.)
     */
    private final GameState gameState;

    /**
     * Tracks the current level of the game.
     */
    private final LevelState levelState;

    /**
     * Manages all enemy-related logic, including spawning, tracking, and
     * updating enemies. This instance is responsible for handling enemy
     * creation cooldowns, updating enemy states, and ensuring that the maximum
     * enemy limit is enforced.
     */
    private final EnemyManager enemyManager;

    /**
     * Measures how long the current game has been active.
     */
    private Timer gameplayTimer;

    /**
     * The player character.
     */
    private Ali ali;

    /**
     * Reference to the panel where all game entities are displayed.
     */
    private GamePanel gamePanel;

    /**
     * Reference to the panel where game information is displayed.
     */
    private InfoPanel infoPanel;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent direct instantiation. Singleton pattern.
     */
    private GameManager() {
        this.gameState = new GameState();
        this.levelState = new LevelState();
        this.enemyManager = new EnemyManager();
    }

    // ----- GETTERS -----

    /**
     * Returns the singleton instance of the {@link GameManager}.
     *
     * @return The single instance of GameManager.
     */
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    /**
     * Returns the current active {@link Ali} instance (the player).
     *
     * @return The player.
     */
    public Ali getAli() {
        gameState.ensureInitialized("getAli");
        return ali;
    }

    public EnemyManager getEnemyManager() {
        gameState.ensureRunning("getEnemyManager");
        return enemyManager;
    }

    public GameState getGameState() {
        return gameState;
    }

    public LevelState getLevelState() {
        return levelState;
    }

    // ----- INITIALIZATION -----

    /**
     * Initializes the GameManager for a new game. This method must be called
     * before the game can run. It sets up all the necessary objects to start
     * the game.
     *
     * @param gamePanel The panel where the game is displayed.
     * @param infoPanel The panel where the game information is displayed.
     */
    public final void init(final GamePanel gamePanel, final InfoPanel infoPanel) {
        if (!gameState.canInitialize()) {
            System.err.println(String.format(
                    "%s: Cannot initialize unless the game is in the NOT_INITIALIZED or INITIALIZING state.",
                    this.getClass().getName()
            ));
            return;
        }

        if (gamePanel == null || infoPanel == null) {
            throw new IllegalStateException(String.format(
                    "%s: GamePanel and InfoPanel must be provided.",
                    this.getClass().getName()
            ));
        }

        this.gamePanel = gamePanel;
        this.infoPanel = infoPanel;

        // Transition to initializing state during setup
        gameState.setState(State.INITIALIZING);

        initialiseAli();
        enemyManager.init();

        // Initialization complete. Begin running.
        gameState.setState(State.RUNNING);
        startGameplayTimer();
    }

    /**
     * Clears game data (used when transitioning back to the main menu).
     */
    public final void clear() {
        if (!gameState.isPaused() && !gameState.isStopped()) {
            stopGameplayTimer();
            gamePanel = null;
            infoPanel = null;
            ali = null;
            enemyManager.clear();
            gameState.setState(State.NOT_INITIALIZED);
        }
    }

    // ----- UPDATE METHODS -----

    /**
     * Updates all managed objects and the current game state.
     */
    @Override
    public void update() {
        gameState.ensureInitialized("update");

        if (ali != null) {
            ali.update();
        }

        enemyManager.update();
    }

    // ----- PLAYER INITIALIZATION -----

    /**
     * Initialises the player character, Mr. {@link Ali}.
     */
    private void initialiseAli() {
        if (!gameState.isInitializing()) {
            throw new IllegalStateException("Cannot initialise player/Ali without being in the INITIALIZING state.");
        }

        HashSet<String> playerAnimationKeys = Stream.of(
                "ali_walk_down",
                "ali_walk_left",
                "ali_walk_right",
                "ali_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));

        ali = new Ali.AliBuilder(gamePanel)
                .collidability(true)
                .animationKeys(playerAnimationKeys)
                .currentAnimationKey("ali_walk_right")
                .maxHitPoints(20)
                .currentHitPoints(20)
                .speed(4)
                .build();

        int x = (GameFrame.FRAME_HEIGHT / 2) - (ali.getSpriteWidth() / 2);
        int y = GameFrame.FRAME_HEIGHT - (2 * ali.getSpriteHeight());

        ali.setPosition(new Point(x, y));
    }

    // ----- GAME TIMER CONTROL -----

    /**
     * Starts the current gameplay timer.
     */
    private void startGameplayTimer() {
        if (gameplayTimer == null) {
            gameplayTimer = new Timer(1000 / 60, e -> update());
        }
        gameplayTimer.start();
    }

    /**
     * Stops the gameplay timer.
     */
    private void stopGameplayTimer() {
        if (gameplayTimer != null) {
            gameplayTimer.stop();
        }
    }
}
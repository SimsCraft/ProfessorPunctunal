package com.simcraft.managers;

import java.awt.Graphics2D;
import java.awt.Point;
import java.lang.StackWalker.StackFrame;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.simcraft.entities.Ali;
import com.simcraft.entities.enemies.Enemy;
import com.simcraft.entities.enemies.Lecturer;
import com.simcraft.entities.enemies.Student;
import com.simcraft.entities.enemies.Yapper;
import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.interfaces.Renderable;
import com.simcraft.interfaces.Updateable;

/**
 * Manages the creation, lifespan, and behaviour of all enemies in the game.
 * This class is responsible for spawning new enemies, updating their state,
 * handling collisions with the player ({@link Ali}), and removing enemies that
 * are no longer within the game boundaries. It triggers the
 * {@link com.simcraft.graphics.effects.sprite_effects.HitFlashEffect} on Ali
 * upon collision.
 */
public class EnemyManager implements Updateable, Renderable {

    // ----- STATIC VARIABLES -----
    /**
     * The maximum number of enemies that can exist simultaneously on the
     * screen.
     */
    private static final int MAX_ENEMY_COUNT = 10;
    /**
     * The cooldown duration (in milliseconds) before another {@link Enemy} can
     * be created. This prevents overwhelming the player with too many enemies
     * at once.
     */
    private static final long ENEMY_CREATION_COOLDOWN_MS = 5000; // 5 seconds
    /**
     * An array of keys for the random collision sound clips.
     */
    private static final String[] COLLISION_SOUND_KEYS = {
        "aight_later",
        "ey_ey_ey",
        "i_hadda_go",
        "i_hafta_go",
        "no_later_boi",
        "sorry_i_cah_stay"
    };

    // ----- INSTANCE VARIABLES -----
    /**
     * Time-stamp formatter for debugging purposes, allowing for easy logging of
     * enemy creation times.
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Stores references to all active enemies currently present on the screen.
     * Using a {@link HashSet} ensures that each enemy is unique and provides
     * efficient addition and removal.
     */
    private HashSet<Enemy> enemies;
    /**
     * Random generator used by various methods within the {@code EnemyManager},
     * such as determining enemy spawn locations and types.
     */
    private Random random;
    /**
     * The timestamp (in milliseconds) of the last time a new enemy was created.
     * This is used to enforce the {@link #ENEMY_CREATION_COOLDOWN_MS}.
     */
    private long lastEnemyCreationTime;
    /**
     * The singleton instance of the {@link SoundManager}.
     */
    private final SoundManager soundManager;
    /**
     * *
     * The key of the sound clip that is currently playing due to a an
     * {@link Enemy} colliding with {@link Ali}. {@code null} if no such sound
     * is playing.
     */
    private String currentAliCollisionSoundKey;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs an {@code EnemyManager}. Initializes the manager by clearing
     * any existing enemies and setting the last enemy creation time to zero.
     */
    public EnemyManager() {
        init();
        soundManager = SoundManager.getInstance();
    }

    // ----- GETTERS -----
    /**
     * Returns all active {@link Enemy} instances currently managed by this
     * {@code EnemyManager}.
     *
     * @return A {@link Set} containing all active enemy entities.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    public Set<Enemy> getEnemies() {
        ensureRunning("getEnemies");
        return enemies;
    }

    /**
     * Gets the cooldown time in milliseconds before a new enemy can be created.
     *
     * @return The cooldown time in milliseconds.
     */
    public long getEnemyCreationCooldownMs() {
        return ENEMY_CREATION_COOLDOWN_MS;
    }

    /**
     * Returns the timestamp (in milliseconds) of the last time an enemy was
     * successfully created.
     *
     * @return The timestamp of the last enemy creation.
     */
    public long getLastEnemyCreationTime() {
        return lastEnemyCreationTime;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * A utility method for testing purposes to quickly add a small group of
     * different enemy types to the game at random locations.
     */
    public void addEnemiesTest() {
        GamePanel gamePanel = GameManager.getInstance().getGamePanel();

        for (int i = 0; i < 5; i++) {
            enemies.add(new Student.StudentBuilder(gamePanel)
                    .position(gamePanel.getRandomPoint())
                    .build());
        }
        for (int i = 0; i < 4; i++) {
            enemies.add(new Lecturer.LecturerBuilder(gamePanel)
                    .position(gamePanel.getRandomPoint())
                    .build());
        }
        for (int i = 0; i < 2; i++) {
            enemies.add(new Yapper.YapperBuilder(gamePanel)
                    .position(gamePanel.getRandomPoint())
                    .build());
        }
    }

    /**
     * Initializes the {@code EnemyManager} for a new game. This method resets
     * the random number generator, clears the list of active enemies, and sets
     * the last enemy creation time to zero.
     */
    public final void init() {
        this.random = new Random();
        clear();
        lastEnemyCreationTime = 0;
        currentAliCollisionSoundKey = null;
    }

    /**
     * Clears the set of currently managed enemies. This is typically called
     * when starting a new game or resetting the game state.
     */
    public void clear() {
        enemies = new HashSet<>();
    }

    /**
     * Checks if a new enemy can be created based on the maximum enemy count and
     * the enemy creation cooldown.
     *
     * @return {@code true} if a new {@link Enemy} can be created, otherwise
     * {@code false}.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    public boolean canCreateEnemy() {
        ensureRunning("canCreateEnemy");
        return (enemies.size() < MAX_ENEMY_COUNT)
                && (System.currentTimeMillis() - lastEnemyCreationTime >= ENEMY_CREATION_COOLDOWN_MS);
    }

    /**
     * Adds a specific {@link Enemy} instance to the set of managed enemies.
     * This method also updates the last enemy creation time if the addition is
     * successful and allowed by {@link #canCreateEnemy()}.
     *
     * @param enemy The new enemy to add.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    public void addEnemy(final Enemy enemy) {
        ensureRunning("addEnemy");

        if (canCreateEnemy()) {
            enemies.add(enemy);
            lastEnemyCreationTime = System.currentTimeMillis();
        }
    }

    /**
     * Creates a new enemy of a random type at a random spawn point (if allowed
     * by {@link #canCreateEnemy()}) and adds it to the managed list of enemies.
     * The newly created enemy will target the player ({@link Ali}).
     *
     * @param ali The player ({@link Ali}) that the created enemy will target.
     * @throws IllegalStateException If the {@link GameManager} is not in the
     * {@code RUNNING} state.
     */
    public void createRandomEnemy(final Ali ali) {
        ensureRunning("createRandomEnemy");
    
        long currentTime = System.currentTimeMillis();
        String currentTimeFormatted = dateFormat.format(new Date(currentTime));
    
        if (!canCreateEnemy()) {
            return;
        }
    
        GamePanel gamePanel = GameManager.getInstance().getGamePanel();
        Point spawnPoint = null;
        try {
            spawnPoint = gamePanel.getRandomPoint();
        } catch (IllegalStateException e) {
            System.err.println("Error getting spawn point: " + e.getMessage());
            return;
        }
    
        if (spawnPoint != null) {
            Enemy newEnemy;
            switch (random.nextInt(3)) {
                case 0 -> newEnemy = new Lecturer.LecturerBuilder(gamePanel).build();
                case 1 -> newEnemy = new Student.StudentBuilder(gamePanel).build();
                case 2 -> newEnemy = new Yapper.YapperBuilder(gamePanel).build();
                default -> throw new IllegalStateException("Unexpected value in createRandomEnemy switch-case.");
            }
    
            newEnemy.sprite = newEnemy.getCurrentSprite(); // <-- ADD THIS LINE!!
    
            newEnemy.setPosition(spawnPoint);
            newEnemy.setTarget(ali.getPosition());
    
            int xMoveSpeed = random.nextInt(5);
            boolean moveLeft = random.nextBoolean();
    
            newEnemy.setVelocityX(moveLeft ? -xMoveSpeed : xMoveSpeed);
    
            enemies.add(newEnemy);
            lastEnemyCreationTime = currentTime; // Update creation time *after* creating
    
            System.out.println("Created a new enemy at " + currentTimeFormatted);
            System.out.println("Total active enemies: : " + enemies.size());
        }
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates all managed objects and the current game state. This includes
     * attempting to create new enemies, updating the state of existing enemies,
     * and checking for collisions. The hit flash effect for Ali is managed
     * within the {@link Ali} class itself.
     */
    @Override
    public void update() {
        ensureRunning("update");
        createRandomEnemy(GameManager.getInstance().getAli());
        updateEnemies();
        checkCollisions();
    }

    /**
     * Renders all currently active enemies on the provided {@link Graphics2D}
     * context.
     *
     * @param g2d The {@code Graphics2D} context to draw on.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (enemies != null) {
            for (Enemy e : enemies) {
                e.safeRender(g2d);
            }
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Ensures that the {@link GameManager} is in the {@code RUNNING} state
     * before allowing certain methods to execute. If not, an
     * {@link IllegalStateException} is thrown.
     *
     * @param methodName The name of the method calling this helper, used for
     * error reporting.
     * @throws IllegalStateException If the {@code GameManager} is not running.
     */
    private void ensureRunning(String methodName) {
        if (!GameManager.getInstance().isRunning()) {
            StackWalker walker = StackWalker.getInstance();
            StackFrame caller = walker.walk(frames -> frames.skip(1).findFirst().orElse(null));

            throw new IllegalStateException(String.format(
                    "%s.%s: Cannot call %s() when GameManager is not in the RUNNING state.",
                    caller != null ? caller.getClassName() : "UnknownClass",
                    caller != null ? caller.getMethodName() : "UnknownMethod",
                    methodName
            ));
        }
    }

    /**
     * Updates the list of managed enemies. This includes calling the
     * {@code update()} method on each enemy and removing any enemies that are
     * fully outside the game panel.
     */
    private void updateEnemies() {
        ensureRunning("updateEnemies");

        if (enemies.isEmpty()) {
            return;
        }

        enemies.removeIf(enemy -> {
            enemy.update();
            return enemy.isFullyOutsidePanel();
        });
    }

    /**
     * Handles collision checks between the player ({@link Ali}) and all active
     * enemies, and between pairs of enemies.
     */
    private void checkCollisions() {
        handleAliCollisions();
        handleEnemyCollisions();
    }

    /**
     * Handles collisions between the player ({@link Ali}) and active enemies.
     * Upon collision, applies game penalties, triggers Ali's hit flash, and
     * plays a random collision sound effect if one is not already playing.
     */
    private void handleAliCollisions() {
        GameManager gameManager = GameManager.getInstance();
        Ali ali = gameManager.getAli();
        for (Enemy enemy : enemies) {
            if (ali.collides(enemy)) {
                gameManager.getInfoPanel().showCollisionNotification(enemy.getClass().getSimpleName(), enemy.getTimePenalty());
                gameManager.getGamePanel().showFloatingText("-" + enemy.getTimePenalty() + "s");
                if (!enemy.hasCollided()) {
                    enemy.setHasCollided(true);
                    gameManager.subtractTimePenalty(enemy.getTimePenalty());
                    ali.startHitFlash();

                    if (!isCollisionSoundPlaying()) {
                        playRandomCollisionSound();
                    }
                }
                enemy.reverseMovementDirection();
            } else {
                enemy.setHasCollided(false);
            }
        }
    }

    /**
     * Handles collisions between pairs of active enemies. Upon collision, the
     * involved enemies reverse their movement direction.
     */
    private void handleEnemyCollisions() {
        List<Enemy> enemyList = new ArrayList<>(enemies);
        for (int i = 0; i < enemyList.size(); i++) {
            Enemy e1 = enemyList.get(i);
            for (int j = i + 1; j < enemyList.size(); j++) {
                Enemy e2 = enemyList.get(j);
                if (e1.collides(e2)) {
                    e1.reverseMovementDirection();
                    e2.reverseMovementDirection();
                }
            }
        }
    }

    /**
     * Plays a random sound clip from the available collision sounds.
     */
    private void playRandomCollisionSound() {
        System.out.println("playRandomCollisionSound() called.");
        if (COLLISION_SOUND_KEYS.length > 0) {
            int randomIndex = random.nextInt(COLLISION_SOUND_KEYS.length);
            String soundKey = COLLISION_SOUND_KEYS[randomIndex];
            System.out.println("Trying to play sound with key: " + soundKey);
            soundManager.playClip(soundKey, false, 1.0f);
        } else {
            System.out.println("No collision sound keys available.");
        }
    }

    /**
     * Checks if any of the collision sound effects are currently playing.
     *
     * @return {@code true} if any collision sound is playing, {@code false}
     * otherwise.
     */
    private boolean isCollisionSoundPlaying() {
        for (String key : COLLISION_SOUND_KEYS) {
            if (soundManager.isClipPlaying(key)) {
                return true;
            }
        }
        return false;
    }
}

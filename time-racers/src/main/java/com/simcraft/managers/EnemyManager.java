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
 */
public class EnemyManager implements Updateable, Renderable {

    // ----- STATIC VARIABLES -----
    /**
     * The maximum number of enemies that can exist simultaneously.
     */
    private static final int MAX_ENEMY_COUNT = 10;
    /**
     * The cooldown duration (in milliseconds) before another {@link Enemy} can
     * be created.
     */
    private static final long ENEMY_CREATION_COOLDOWN_MS = 5000; // 5 seconds

    // ----- INSTANCE VARIABLES -----
    /**
     * Time-stamp formatter for debugging purposes.
     */
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /**
     * Stores references to all active enemies on screen.
     */
    private HashSet<Enemy> enemies;
    /**
     * Random generator used by various methods.
     */
    private Random random;
    /**
     * The timestamp (in milliseconds) of the last time a new enemy was created.
     */
    private long lastEnemyCreationTime;

    // ----- CONSTRUCTORS -----
    public EnemyManager() {
        init();
    }

    // ----- GETTERS -----
    /**
     * Returns all active {@link Enemy} instances.
     *
     * @return The enemies.
     */
    public Set<Enemy> getEnemies() {
        ensureRunning("getEnemies");
        return enemies;
    }

    /**
     * Gets the cooldown time in milliseconds before another enemy can be
     * created.
     *
     * @return The cooldown time.
     */
    public long getEnemyCreationCooldownMs() {
        return ENEMY_CREATION_COOLDOWN_MS;
    }

    /**
     * Returns the last time an enemy was created in milliseconds.
     *
     * @return The last update time.
     */
    public long getLastEnemyCreationTime() {
        return lastEnemyCreationTime;
    }

    // ----- BUSINESS LOGIC METHODS -----
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
     * Initializes the EnemyManager for a new game. This method sets up all the
     * necessary objects to manage enemies and clears old enemy data.
     */
    public final void init() {
        random = new Random();
        clear();
        lastEnemyCreationTime = 0;
    }

    /**
     * Clears old enemy data.
     */
    public void clear() {
        enemies = new HashSet<>();
    }

    /**
     * Checks if a new enemy can be created.
     *
     * @return {@code true} if a new {@link Enemy} can be created, otherwise
     * {@code false}.
     */
    public boolean canCreateEnemy() {
        ensureRunning("canCreateEnemy");
        return (enemies.size() < MAX_ENEMY_COUNT)
                && (System.currentTimeMillis() - lastEnemyCreationTime >= ENEMY_CREATION_COOLDOWN_MS);
    }

    /**
     * Adds a new {@link Enemy} instance to the managed list.
     *
     * @param enemy The new enemy.
     */
    public void addEnemy(final Enemy enemy) {
        ensureRunning("addEnemy");

        if (canCreateEnemy()) {
            enemies.add(enemy);
            lastEnemyCreationTime = System.currentTimeMillis();
        }
    }

    /**
     * Creates a random {@link Enemy} (if allowed) and adds it to the managed
     * list.
     *
     * @param ali The player ({@link Ali}) the {@link Enemy} will target.
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
                case 0 ->
                    newEnemy = new Lecturer.LecturerBuilder(gamePanel).build();
                case 1 ->
                    newEnemy = new Student.StudentBuilder(gamePanel).build();
                case 2 ->
                    newEnemy = new Yapper.YapperBuilder(gamePanel).build();
                default ->
                    throw new IllegalStateException("Unexpected value in createRandomEnemy switch-case.");
            }
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
     * Updates all managed objects and the current game state.
     */
    @Override
    public void update() {
        ensureRunning("update");
        createRandomEnemy(GameManager.getInstance().getAli());
        updateEnemies();
        checkCollisions();
    }

    /**
     * Renders all currently active enemies
     *
     *
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
     * Updates the list of managed enemies and removes any who are fully
     * off-screen.
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
     * Checks for collisions between the player and enemies, and between
     * enemies.
     */
    private void checkCollisions() {
        GameManager gameManager = GameManager.getInstance();
        // Ali vs Enemy
        Ali ali = gameManager.getAli();
        for (Enemy enemy : enemies) {
            if (ali.collides(enemy)) {
                gameManager.getInfoPanel().showCollisionNotification(enemy.getClass().getSimpleName(), enemy.getTimePenalty());
                gameManager.getGamePanel().showFloatingText("-" + enemy.getTimePenalty() + "s");
                if (!enemy.hasCollided()) {
                    enemy.setHasCollided(true);
                    gameManager.subtractTimePenalty(enemy.getTimePenalty());
                }
                enemy.reverseMovementDirection();
            } else {
                enemy.setHasCollided(false);
            }
        }

        // Enemy vs Enemy
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
}

package com.simcraft.levels;

import java.awt.Color;
import java.util.List;

/**
 * Represents the configuration settings for a specific game level.
 * <p>
 * This class acts as a blueprint for all visual and gameplay parameters
 * associated with each level, such as:
 * - Background appearance
 * - Background music
 * - Time limits
 * - Enemy count and behavior
 * - Player settings
 * </p>
 */


public class LevelConfig {

    private final List<String> backgroundImagePaths;   // Path to the background image file
    private final String musicClipName;         // Name of the sound clip to play during this level
    private final int levelTimeLimitSeconds;    // Time (in seconds) allowed to complete this level
    private final int maxEnemies;               // Maximum number of enemies allowed on screen
    private final int playerSpeed;              // Speed of the player character in this level
    private final Color backgroundColor;        // Fallback background color (if image fails to load)
    private final LevelType levelType;

    /**
     * Constructor to initialize all configuration properties for a level.
     *
     * @param backgroundImagePath  File path to the level's background image.
     * @param musicClipName        Name of the sound clip to play.
     * @param levelTimeLimitSeconds Time limit for this level in seconds.
     * @param maxEnemies           Maximum number of enemies allowed on screen.
     * @param playerSpeed          Player movement speed during this level.
     * @param backgroundColor      Background color if image doesn't load.
     */
    public LevelConfig(List<String> backgroundImagePaths,
                       String musicClipName,
                       int levelTimeLimitSeconds,
                       int maxEnemies,
                       int playerSpeed,
                       Color backgroundColor,
                       LevelType levelType) {
        this.backgroundImagePaths = backgroundImagePaths;
        this.musicClipName = musicClipName;
        this.levelTimeLimitSeconds = levelTimeLimitSeconds;
        this.maxEnemies = maxEnemies;
        this.playerSpeed = playerSpeed;
        this.backgroundColor = backgroundColor;
        this.levelType = levelType;
    }

    // ----- GETTERS -----


    public List<String> getBackgroundImagePaths() {
        return backgroundImagePaths;
    }

    public String getMusicClipName() {
        return musicClipName;
    }

    public int getLevelTimeLimitSeconds() {
        return levelTimeLimitSeconds;
    }

    public int getMaxEnemies() {
        return maxEnemies;
    }

    public int getPlayerSpeed() {
        return playerSpeed;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public LevelType getLevelType() {
        return levelType;
    }
}
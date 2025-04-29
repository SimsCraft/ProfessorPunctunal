package com.simcraft.levels;

import java.awt.Color;
import java.util.List;

/**
 * Represents the configuration settings for a specific game level.
 * <p>
 * This class acts as a blueprint, holding all the necessary visual and gameplay
 * parameters that define a particular level within the game. These parameters
 * include aspects such as the background's appearance, the music played during
 * the level, the time limit for completion, the maximum number of enemies
 * present, and the player's movement speed.
 */
public class LevelConfig {

    /**
     * Paths to the background image files used in this level. This list can
     * contain multiple paths for scrolling background effects.
     */
    private final List<String> backgroundImagePaths;

    /**
     * Name of the sound clip to be played as background music during this
     * level.
     */
    private final String musicClipName;

    /**
     * Time (in seconds) allowed to complete this level.
     */
    private final int levelTimeLimitSeconds;

    /**
     * Maximum number of enemy entities that can be present on the game screen
     * simultaneously during this level.
     */
    private final int maxEnemies;

    /**
     * Movement speed of the player character within this level. Higher values
     * indicate faster movement.
     */
    private final int playerSpeed;

    /**
     * Fallback background color for this level. This color will be used if
     * loading the background image(s) fails.
     */
    private final Color backgroundColor;

    /**
     * The specific type of this level, as defined by the {@link LevelType}
     * enum.
     */
    private final LevelType levelType;

    /**
     * Constructs a {@code LevelConfig} object with the specified configuration
     * parameters for a game level.
     *
     * @param backgroundImagePaths A {@code List} of {@code String} file paths
     * to the background images used in this level. This list can contain
     * multiple paths for scrolling background effects. Must not be {@code null}
     * or empty.
     * @param musicClipName A {@code String} representing the name of the sound
     * clip to be played as background music during this level. Can be
     * {@code null} if no music is intended.
     * @param levelTimeLimitSeconds An {@code int} specifying the time limit for
     * completing this level, measured in seconds. Must be a non-negative value.
     * @param maxEnemies An {@code int} representing the maximum number of enemy
     * entities that can be present on the game screen simultaneously during
     * this level. Must be a non-negative value.
     * @param playerSpeed An {@code int} indicating the movement speed of the
     * player character within this level. Higher values typically mean faster
     * movement. Must be a positive value.
     * @param backgroundColor A {@code Color} object specifying the fallback
     * background color to be used if loading the background image(s) from
     * {@code backgroundImagePaths} fails. Must not be {@code null}.
     * @param levelType A {@code LevelType} enum constant indicating the
     * specific type of this level (e.g., classroom, hallway). Must not be
     * {@code null}.
     * @throws NullPointerException If {@code backgroundImagePaths},
     * {@code backgroundColor}, or {@code levelType} is {@code null}.
     * @throws IllegalArgumentException If {@code levelTimeLimitSeconds} or
     * {@code maxEnemies} is negative, or if {@code playerSpeed} is not
     * positive.
     */
    public LevelConfig(List<String> backgroundImagePaths,
            String musicClipName,
            int levelTimeLimitSeconds,
            int maxEnemies,
            int playerSpeed,
            Color backgroundColor,
            LevelType levelType) {
        if (backgroundImagePaths == null) {
            throw new NullPointerException("backgroundImagePaths cannot be null.");
        }
        if (backgroundColor == null) {
            throw new NullPointerException("backgroundColor cannot be null.");
        }
        if (levelType == null) {
            throw new NullPointerException("levelType cannot be null.");
        }
        if (levelTimeLimitSeconds < 0) {
            throw new IllegalArgumentException("levelTimeLimitSeconds cannot be negative.");
        }
        if (maxEnemies < 0) {
            throw new IllegalArgumentException("maxEnemies cannot be negative.");
        }
        if (playerSpeed <= 0) {
            throw new IllegalArgumentException("playerSpeed must be a positive value.");
        }
        this.backgroundImagePaths = backgroundImagePaths;
        this.musicClipName = musicClipName;
        this.levelTimeLimitSeconds = levelTimeLimitSeconds;
        this.maxEnemies = maxEnemies;
        this.playerSpeed = playerSpeed;
        this.backgroundColor = backgroundColor;
        this.levelType = levelType;
    }

    // ----- GETTERS -----
    /**
     * Returns the list of file paths for the background images used in this
     * level. This list is used to create a scrolling background effect.
     *
     * @return A {@code List} of {@code String} paths to the background image
     * files. May be empty if no background image is specified.
     */
    public List<String> getBackgroundImagePaths() {
        return backgroundImagePaths;
    }

    /**
     * Returns the name of the music clip to be played as background music
     * during this level.
     *
     * @return A {@code String} representing the name of the music clip, or
     * {@code null} if no background music is intended for this level.
     */
    public String getMusicClipName() {
        return musicClipName;
    }

    /**
     * Returns the time limit for completing this level, in seconds.
     *
     * @return An {@code int} representing the time limit in seconds.
     */
    public int getLevelTimeLimitSeconds() {
        return levelTimeLimitSeconds;
    }

    /**
     * Returns the maximum number of enemy entities that are allowed to be
     * present on the game screen simultaneously during this level.
     *
     * @return An {@code int} representing the maximum number of enemies.
     */
    public int getMaxEnemies() {
        return maxEnemies;
    }

    /**
     * Returns the movement speed of the player character within this level.
     * Higher values indicate faster movement.
     *
     * @return An {@code int} representing the player's movement speed.
     */
    public int getPlayerSpeed() {
        return playerSpeed;
    }

    /**
     * Returns the fallback background color for this level. This color will be
     * used if loading the background image(s) fails.
     *
     * @return A {@code Color} object representing the fallback background
     * color. Will not be {@code null}.
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Returns the specific type of this level, as defined by the
     * {@link LevelType} enum.
     *
     * @return A {@code LevelType} enum constant representing the type of this
     * level. Will not be {@code null}.
     */
    public LevelType getLevelType() {
        return levelType;
    }
}

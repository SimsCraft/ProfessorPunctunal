package com.simcraft.states;

import com.simcraft.levels.LevelConfig;
import com.simcraft.levels.LevelLibrary;

/**
 * Tracks the current level and retrieves its configuration.
 *
 * LevelState stores the active level number and provides access to its
 * predefined settings (background, time limit, sound, movement speed, etc).
 *
 * The configurations themselves are defined in {@link LevelLibrary}.
 */
public class LevelState {

    /**
     * The index of the currently active level.
     * Level 1 would be index 0, Level 2 would be index 1, and so on.
     */
    private int currentLevelIndex;

    public LevelState() {
        this.currentLevelIndex = 0; // Start at level 1 (index 0)
    }

    /**
     * Returns the configuration for the current level.
     * This includes background, sound, duration, movement settings, etc.
     *
     * @return A {@link LevelConfig} representing the current level’s data.
     */
    public LevelConfig getCurrentConfig() {
        return LevelLibrary.getLevel(currentLevelIndex);
    }

    /**
     * Advances to the next level.
     * If the next level does not exist, stays on current level.
     */
    public void advanceToNextLevel() {
        if (currentLevelIndex + 1 < LevelLibrary.getTotalLevels()) {
            currentLevelIndex++;
        }
    }

    /**
     * Sets the current level to the specified index (0-based).
     *
     * @param levelIndex The index to set as the current level.
     */
    public void setLevel(int levelIndex) {
        if (levelIndex >= 0 && levelIndex < LevelLibrary.getTotalLevels()) {
            this.currentLevelIndex = levelIndex;
        } else {
            throw new IllegalArgumentException("Invalid level index: " + levelIndex);
        }
    }

    /**
     * Returns the index of the current level.
     *
     * @return The current level index.
     */
    public int getCurrentLevelIndex() {
        return currentLevelIndex;
    }
}
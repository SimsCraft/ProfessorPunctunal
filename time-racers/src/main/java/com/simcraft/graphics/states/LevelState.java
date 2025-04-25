package com.simcraft.graphics.states;

/**
 * Represents the current level in the game.
 */
public class LevelState {

    public enum Level {
        LEVEL_1,
        LEVEL_2,
        LEVEL_3,
        ALT_REALITY,
        CLASSROOM_FINAL
    }

    private Level currentLevel;

    public LevelState() {
        currentLevel = Level.LEVEL_1;
    }

    public void setLevel(Level level) {
        if (level == null) throw new IllegalArgumentException("Level cannot be null.");
        this.currentLevel = level;
    }

    public Level getLevel() {
        return currentLevel;
    }

    public boolean isLevel(Level level) {
        return this.currentLevel == level;
    }
}
package com.simcraft.levels;

/**
 * Enumerates the different types of game levels supported by the game. Each
 * constant in this enum represents a distinct style of level, which can
 * influence gameplay mechanics, camera perspective, and environmental design.
 */
public enum LevelType {
    /**
     * Represents a game level with a top-down perspective. In this type of
     * level, the player typically views the game world from directly above,
     * controlling movement in a 2D plane (horizontally and vertically).
     */
    TOP_DOWN,
    /**
     * Represents a game level with a side-scrolling perspective. In this type
     * of level, the player views the game world from a side-on angle, typically
     * moving horizontally across the screen.
     */
    SIDE_SCROLLING
}

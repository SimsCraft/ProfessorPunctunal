package com.simcraft.levels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores static definitions for each game level.
 *
 * The {@code LevelLibrary} class provides centralized access to level-specific
 * configurations. These configurations include visual settings like background
 * images and colors, audio settings such as background music, and gameplay
 * parameters like movement speed modifiers and timer durations.
 *
 * This approach centralizes all level setup, making it easier for developers to
 * manage and adjust level properties consistently throughout the game.
 */
public class LevelLibrary {

    /**
     * Private constructor to hide the public one.
     */
    private LevelLibrary() {
    }

    /**
     * A static {@code List} that holds all the defined {@link LevelConfig}
     * objects for the game. Each element in this list represents the
     * configuration for a specific game level.
     */
    private static final List<LevelConfig> levels = new ArrayList<>();

    static {
        // -------- LEVEL 1 --------
        levels.add(new LevelConfig(
                List.of(
                        "/images/backgrounds/background_0.png"//,
                //"/images/backgrounds/background_0.1.png"
                ),
                "background.wav",
                60,
                5,
                4,
                new Color(200, 170, 170),
                LevelType.TOP_DOWN
        ));

        // -------- LEVEL 2 --------
        levels.add(new LevelConfig(
                List.of(
                        "/images/backgrounds/background(lvl 3).jpg",
                        "/images/backgrounds/background(lvl 3).jpg",
                        "/images/backgrounds/ba3.png"
                ),
                "background.wav",
                75,
                8,
                5,
                new Color(180, 140, 160),
                LevelType.SIDE_SCROLLING
        ));

        // -------- LEVEL 3 --------
        levels.add(new LevelConfig(
                List.of(
                        "/images/backgrounds/bg2.jpeg"
                ),
                "background.wav",
                60,
                5,
                4,
                new Color(200, 170, 170),
                LevelType.TOP_DOWN
        ));
    }

    /**
     * Returns the level configuration for the given index.
     *
     * @param index The zero-based index of the level to retrieve.
     * @return The {@link LevelConfig} object associated with the specified
     * index.
     * @throws IllegalArgumentException If the provided {@code index} is out of
     * bounds (less than 0 or greater than or equal to the total number of
     * levels).
     */
    public static LevelConfig getLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IllegalArgumentException("Invalid level index: " + index);
        }
        return levels.get(index);
    }

    /**
     * Returns the total number of levels currently defined in the library.
     *
     * @return An {@code int} representing the total number of levels available.
     */
    public static int getTotalLevels() {
        return levels.size();
    }
}

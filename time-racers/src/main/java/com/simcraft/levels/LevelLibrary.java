package com.simcraft.levels;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Stores static definitions for each game level.
 *
 * The LevelLibrary class provides access to level-specific configurations such as:
 * - Background image
 * - Sound settings
 * - Movement speed modifier
 * - Timer duration
 *
 * This centralizes all level setup and makes it easier for teammates to tweak values.
 */
public class LevelLibrary {

    private static final List<LevelConfig> levels = new ArrayList<>();

    static {
        // -------- LEVEL 1 --------
        levels.add(new LevelConfig(
            List.of(
                "/images/backgrounds/background_0.png",
                "/images/backgrounds/background_0.1.png"
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
     * @param index The index of the level (0-based)
     * @return The {@link LevelConfig} object for the level
     */
    public static LevelConfig getLevel(int index) {
        if (index < 0 || index >= levels.size()) {
            throw new IllegalArgumentException("Invalid level index: " + index);
        }
        return levels.get(index);
    }

    /**
     * Returns the total number of levels currently available.
     *
     * @return number of levels
     */
    public static int getTotalLevels() {
        return levels.size();
    }
}
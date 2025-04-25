package com.simcraft.graphics.levels;

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

    // ----- STATIC INITIALIZATION BLOCK -----
    static {
        // -------- LEVEL 1 --------
        levels.add(new LevelConfig(
            "/images/backgrounds/classroom_day.png",  // background image path
            "bg_music_1",                             // background music
            60,                                       // level duration
            5,                                        // max enemies
            4,                                        // player speed
            new Color(200, 170, 170)                  // background color
        ));

        // -------- LEVEL 2 --------
        levels.add(new LevelConfig(
            "/images/backgrounds/classroom_evening.png",
            "bg_music_2",
            75,
            8,
            5,
            new Color(180, 140, 160)
        ));

        // -------- LEVEL 3 --------
        levels.add(new LevelConfig(
            "/images/backgrounds/exam_hall.png",
            "bg_music_3",
            90,
            10,
            6,
            new Color(150, 150, 180)
        ));

        // -------- ALT REALITY LEVEL (Optional) --------
        levels.add(new LevelConfig(
            "/images/backgrounds/alt_reality.png",
            "distorted_theme",
            120,
            12,
            6,
            new Color(50, 50, 50)
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
package com.simcraft.graphics.levels;

import com.simcraft.graphics.states.LevelState.Level;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Factory class responsible for providing pre-configured {@link LevelConfig}
 * objects based on the current game level.
 * <p>
 * This approach ensures that level definitions are centralized and easy
 * to manage, edit, or extend.
 * </p>
 */
public class LevelFactory {

    // A map storing level configurations keyed by their corresponding Level enum.
    private static final Map<Level, LevelConfig> levelConfigs = new HashMap<>();

    // ----- STATIC INITIALIZATION BLOCK -----
    static {
        // Level 1 Configuration
        levelConfigs.put(Level.LEVEL_1, new LevelConfig(
                "/images/backgrounds/level1.png",  // Background image
                "background_lvl1",                 // Music clip name
                60,                                // Time limit in seconds
                5,                                 // Max number of enemies
                4,                                 // Player speed
                new Color(200, 170, 170)           // Background color
        ));

        // Level 2 Configuration
        levelConfigs.put(Level.LEVEL_2, new LevelConfig(
                "/images/backgrounds/level2.png",
                "background_lvl2",
                90,
                8,
                5,
                new Color(170, 190, 220)
        ));

        // Alternate Reality Configuration
        levelConfigs.put(Level.ALT_REALITY, new LevelConfig(
                "/images/backgrounds/alt_reality.png",
                "distorted_theme",
                120,
                10,
                6,
                new Color(50, 50, 50)
        ));

        // Additional levels can be added here following the same pattern.
    }

    /**
     * Returns the {@link LevelConfig} corresponding to the given level.
     * If no config exists for that level, it defaults to {@code LEVEL_1}.
     *
     * @param level The current game level.
     * @return A valid {@link LevelConfig} for the specified level.
     */
    public static LevelConfig getConfig(Level level) {
        return levelConfigs.getOrDefault(level, levelConfigs.get(Level.LEVEL_1));
    }
}
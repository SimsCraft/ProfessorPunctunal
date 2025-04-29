package com.simcraft.graphics;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import com.simcraft.managers.FontManager;

/**
 * A utility class that holds constant values related to the user interface (UI)
 * of the game, such as fonts used for different text elements. This ensures a
 * consistent look and feel throughout the application. The fonts are
 * initialized as static final variables for easy access.
 */
public class UIConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private UIConstants() {
    }

    /**
     * The primary font used for titles on various screens and dialogs.
     */
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);

    /**
     * A standard font used for the main body text content in the UI.
     */
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 20);

    /**
     * A smaller version of the body font, used for less prominent text or
     * labels.
     */
    public static final Font BODY_FONT_SMALL = new Font("Arial", Font.PLAIN, 12);

    /**
     * The font used for text displayed on interactive buttons.
     */
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 24);

    /**
     * A custom font loaded from a resource file, intended to give an "arcade
     * classic" style to certain UI elements like level counters and timers. It
     * is initialized in a static block to ensure it's loaded when the class is
     * loaded.
     */
    public static Font ARCADE_FONT;

    static {
        // Attempt to load the arcade classic font from a resource file
        Font loadedFont = FontManager.loadFontFromResource("fonts/arcade_classic.ttf", Font.PLAIN, 18f);
        if (loadedFont != null) {
            // If loading is successful, assign the loaded font to ARCADE_FONT
            ARCADE_FONT = loadedFont;
            // Register the loaded font with the local graphics environment so it can be used by name
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(loadedFont);
        } else {
            // If loading fails (e.g., file not found or corrupted), fall back to the BUTTON_FONT
            System.err.println("Failed to load ARCADE_FONT. Falling back to default button font.");
            ARCADE_FONT = BUTTON_FONT;
        }
    }
}

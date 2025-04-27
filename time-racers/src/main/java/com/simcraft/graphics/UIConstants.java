package com.simcraft.graphics;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

import com.simcraft.managers.FontManager;

public class UIConstants {

    /**
     * Private constructor to prevent instantiation.
     */
    private UIConstants() {
    }

    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 48);
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 20);
    public static final Font BODY_FONT_SMALL = new Font("Arial", Font.PLAIN, 12);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 24);

    // Will be initialized in static block
    public static Font ARCADE_FONT;

    static {
        Font loadedFont = FontManager.loadFontFromResource("fonts/arcade_classic.ttf", Font.PLAIN, 18f);
        if (loadedFont != null) {
            ARCADE_FONT = loadedFont;
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(loadedFont);
        } else {
            // Fallback if loading fails
            System.err.println("Failed to load ARCADE_FONT. Falling back to default.");
            ARCADE_FONT = BUTTON_FONT;
        }
    }
}

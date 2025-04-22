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

    public static final Font TITLE_FONT = new Font("Garamond", Font.BOLD, 48);
    public static final Font BODY_FONT = new Font("Garamond", Font.PLAIN, 20);
    public static final Font BUTTON_FONT = new Font("Garamond", Font.BOLD, 20);

    // Will be initialized in static block
    public static Font ARCADE_FONT;

    static {
        Font loadedFont = FontManager.loadFontFromResource("fonts/arcade_classic.ttf", Font.PLAIN, 18f);
        if (loadedFont != null) {
            ARCADE_FONT = loadedFont;
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(loadedFont);
        } else {
            // Fallback if loading fails
            System.err.println("Failed to load arcade_classic.ttf. Falling back to default.");
            ARCADE_FONT = new Font("Arcade Classic", Font.PLAIN, 20);
        }
    }
}

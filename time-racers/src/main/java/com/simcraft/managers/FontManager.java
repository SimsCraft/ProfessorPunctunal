package com.simcraft.managers;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * The FontManager class manages the loading and processing of fonts.
 */
public class FontManager {

    /**
     * Private constructor to prevent instantiation.
     */
    private FontManager() {
    }

    /**
     * Loads a font from a file on the filesystem.
     *
     * @param filePath The path to the font file (.ttf or .otf).
     * @param fontStyle The style of the font (Font.PLAIN, Font.BOLD, etc.).
     * @param fontSize The size of the font.
     * @return The loaded Font object, or null if loading fails.
     */
    public static Font loadFontFromFile(String filePath, int fontStyle, float fontSize) {
        try {
            File fontFile = new File(filePath);
            Font font = Font.createFont(Font.TRUETYPE_FONT, fontFile);
            return font.deriveFont(fontStyle, fontSize);
        } catch (IOException | FontFormatException e) {
            System.err.println("Failed to load font from file: " + filePath + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Loads a font from the classpath (e.g., from a resource inside a JAR).
     *
     * @param resourcePath The classpath resource path (e.g.,
     * "/fonts/myfont.ttf").
     * @param fontStyle The style of the font (Font.PLAIN, Font.BOLD, etc.).
     * @param fontSize The size of the font.
     * @return The loaded Font object, or null if loading fails.
     */
    public static Font loadFontFromResource(final String resourcePath, final int fontStyle, final float fontSize) {
        try (InputStream is = FontManager.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Resource not found: " + resourcePath);
                return null;
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(fontStyle, fontSize);
        } catch (IOException | FontFormatException e) {
            System.err.println("Failed to load font from resource: " + resourcePath + " - " + e.getMessage());
            return null;
        }
    }
}

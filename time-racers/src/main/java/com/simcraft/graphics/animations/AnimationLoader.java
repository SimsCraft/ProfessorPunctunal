package com.simcraft.graphics.animations;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rikuthin.App;

import managers.AnimationManager;
import managers.ImageManager;

/**
 * Utility class for loading animations from sprite sheets (strip files).
 */
public class AnimationLoader {

    // ----- STATIC VARIABLES -----
    /**
     * Directory where animations are stored
     */
    private static final String ANIMATION_FOLDER = "/images/animations/";

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent instantiation.
     */
    private AnimationLoader() {
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Loads an animation from a sprite strip file.
     * <p>
     * Valid sprite sheets expect each frame to be the same size and have no
     * space in-between them or along the sheet's margins.
     *
     * @param filePath Path to the sprite sheet image.
     * @param frameDurationMs Duration of each frame in milliseconds. (Minimum value: 1)
     * @param numRows The number of rows in the sprite sheet. (Minimum value: 1)
     * @param numColumns The number of columns in the sprite sheet. (Minimum value: 1)
     * @return List of AnimationFrame objects extracted from the sprite sheet.
     * @throws IllegalArgumentException If the file path is empty or {@code null}.
     * @throws IOException If the image file cannot be loaded.
     */
    public static List<AnimationFrame> loadFromSpriteSheet(final String filePath, long frameDurationMs, int numRows, int numColumns) throws IllegalArgumentException, IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("AnimationLoader: Must provide a valid file path for the sprite sheet.");
        }
        
        frameDurationMs = Math.max(frameDurationMs, 1);        
        numRows = Math.max(numRows, 1);
        numColumns = Math.max(numColumns, 1);

        BufferedImage spriteSheet = ImageManager.loadBufferedImage(filePath);
        if (spriteSheet == null) {
            throw new IOException("AnimationLoader: Failed to load sprite sheet: " + filePath);
        }

        ArrayList<AnimationFrame> frames = new ArrayList<>();
        int frameWidth = spriteSheet.getWidth() / numColumns;
        int frameHeight = spriteSheet.getHeight() / numRows;

        for (int row = 0; row < numRows; row++) {
            for (int column = 0; column < numColumns; column++) {
                int x = column * frameWidth;
                int y = row * frameHeight;

                BufferedImage frame = extractFrame(spriteSheet, x, y, frameWidth, frameHeight);
                frames.add(new AnimationFrame(frame, frameDurationMs));
            }
        }
        return frames;
    }

    /**
     * Loads multiple animations at startup.
     * <p>
     * Hard-coding this for now. Might try to make it more dynamic later.
     */
    public static void loadDefaultAnimations() {
        AnimationMetadata[] animationMetadata = {
            new AnimationMetadata("enemy-bullet.png", 1, 8, App.FRAME_RATE_MS * 4, true),
            new AnimationMetadata("mage-guardian-blue.png", 1, 14, App.FRAME_RATE_MS * 2, true),
            new AnimationMetadata("mage-guardian-magenta.png", 1, 14, App.FRAME_RATE_MS * 2, true),
            new AnimationMetadata("mage-guardian-red.png", 1, 14, App.FRAME_RATE_MS * 2, true),
            new AnimationMetadata("player-bullet.png", 1, 8, App.FRAME_RATE_MS  * 4, true),
            new AnimationMetadata("player-death.png", 1, 8, App.FRAME_RATE_MS * 4, false),
            new AnimationMetadata("player-idle.png", 1, 8, App.FRAME_RATE_MS * 4, true),
            new AnimationMetadata("player-walk-up-left.png", 1, 8, App.FRAME_RATE_MS * 4, true),
            new AnimationMetadata("player-walk-up-right.png", 1, 8, App.FRAME_RATE_MS * 4, true),
            new AnimationMetadata("player-walk-up.png", 1, 8, App.FRAME_RATE_MS * 4, true)
        };

        // Load each animation and add it to the AnimationManager
        for (AnimationMetadata md : animationMetadata) {
            try {
                // Load the frames from the sprite sheet
                List<AnimationFrame> frames = AnimationLoader.loadFromSpriteSheet(
                        ANIMATION_FOLDER + md.fileName, md.frameDurationMs, md.numGridRows, md.numGridColumns
                );

                // Create the animation template
                AnimationTemplate animationTemplate = new AnimationTemplate(frames, md.isLooping);

                // Generate a unique key for the animation from the sprite sheet filename (minus the extension, i.e., "bullet-1" instead of "bullet-1.png").
                String animationKey = md.fileName.substring(md.fileName.lastIndexOf('/') + 1, md.fileName.lastIndexOf('.'));

                // Add the animation template to the AnimationManager
                AnimationManager.getInstance().addAnimation(animationKey, animationTemplate);

                System.out.println(String.format(
                        "AnimationLoader: Loaded animation <'%s'> with <%d> frames.",
                        animationKey,
                        animationTemplate.getFrames().size()
                ));
            } catch (IOException | IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Extracts a frame from a sprite sheet.
     *
     * @param source The source sprite sheet.
     * @param x X coordinate of the frame.
     * @param y Y coordinate of the frame.
     * @param width Width of the frame.
     * @param height Height of the frame.
     * @return A new BufferedImage containing the extracted frame.
     */
    private static BufferedImage extractFrame(BufferedImage source, int x, int y, int width, int height) {
        BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();
        g.drawImage(source.getSubimage(x, y, width, height), 0, 0, null);
        g.dispose();
        return frame;
    }

    // ----- PRIVATE INNER CLASSES -----
    /**
     * Represents metadata for a sprite sheet used in animations.
     */
    private static class AnimationMetadata {

        /**
         * File name of the sprite sheet.
         */
        String fileName;
        /**
         * Number of rows in the sprite sheet. (Minimum value of 1.)
         */
        int numGridRows;
        /**
         * Number of columns in the sprite sheet. (Minimum value of 1.)
         */
        int numGridColumns;
        /**
         * How many milliseconds each animation frame is displayed for. (Minimum value of 1.)
         */
        long frameDurationMs;
        /**
         * Whether the animation should loop.
         */
        boolean isLooping;

        /**
         * Creates a new AnimationMetadata object.
         *
         * @param fileName Name of the sprite sheet file.
         * @param numGridRows Number of rows in the sprite sheet's grid.
         * (Minimum value of 1.)
         * @param numGridColumns Number of columns in the sprite sheet's grid.
         * (Minimum value of 1.)
         *  @param frameDurationMs The duration the animation's frames in milliseconds. (Minimum value of 1).
         * @param isLooping Whether the animation should loop.
         */
        public AnimationMetadata(final String fileName, final int numGridRows, final int numGridColumns, final long frameDurationMs, final boolean isLooping) throws IllegalArgumentException {
            if (fileName == null || fileName.trim().isBlank()) {
                throw new IllegalArgumentException(String.format(
                        "%s: File namme cannot be null nor blank.",
                        this.getClass().getName()
                ));
            }
            this.fileName = fileName;
            this.numGridRows = Math.max(numGridRows, 1);
            this.numGridColumns = Math.max(numGridColumns, 1);
            this.frameDurationMs = Math.max(frameDurationMs, 1);
            this.isLooping = isLooping;
        }
    }

}

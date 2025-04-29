package com.simcraft.graphics.animations;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.simcraft.App.FRAME_RATE_MS;
import com.simcraft.managers.AnimationManager;
import com.simcraft.managers.ImageManager;

/**
 * Utility class for loading animations from sprite sheets (strip files) and
 * from JSON configuration files. This class provides static methods to parse
 * image files and JSON data to create {@link AnimationFrame} lists and register
 * {@link AnimationTemplate} objects with the {@link AnimationManager}.
 */
public class AnimationLoader {

    // ----- STATIC VARIABLES -----
    /**
     * Base folder where animation image assets are located within the
     * resources.
     */
    private static final String ANIMATION_FOLDER = "/images/animations/";

    /**
     * The base duration of one frame in milliseconds, calculated based on the
     * application's target frame rate. This value is used as a reference point
     * for defining the duration of individual animation frames using
     * multipliers.
     */
    private static final double BASE_FRAME_TIME_MS = 1000.0 / FRAME_RATE_MS;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AnimationLoader() {
        // Utility class, should not be instantiated
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Loads a list of animation frames from a sprite sheet image. The sprite
     * sheet is divided into a grid of frames based on the specified number of
     * rows and columns. Each extracted frame will have the same specified
     * duration.
     * <p>
     * It is assumed that the frames in the sprite sheet are of equal size and
     * there is no spacing or margin between them or the edges of the image.
     *
     * @param filePath Path to the sprite sheet image file within the resources.
     * Must not be {@code null} or empty.
     * @param numRows The number of rows in the sprite sheet grid. Must be at
     * least 1.
     * @param numColumns The number of columns in the sprite sheet grid. Must be
     * at least 1.
     * @param frameDurationMs The duration in milliseconds for which each
     * extracted frame should be displayed. Must be at least 1.
     * @return A {@link List} of {@link AnimationFrame} objects, where each
     * frame corresponds to a section of the sprite sheet.
     * @throws IllegalArgumentException If the file path is {@code null} or
     * empty.
     * @throws IOException If an error occurs while loading the image file.
     * @see <a href="resources/images/README.md">resources/images/README.md</a>
     * for guidelines on constructing sprite sheets.
     */
    public static List<AnimationFrame> loadFromSpriteSheet(final String filePath, int numRows, int numColumns, long frameDurationMs) throws IllegalArgumentException, IOException {
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("AnimationLoader: Must provide a valid file path for the sprite sheet.");
        }

        numRows = Math.max(numRows, 1);
        numColumns = Math.max(numColumns, 1);
        frameDurationMs = Math.max(frameDurationMs, 1);

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

                BufferedImage frameImage = ImageManager.scaleBufferedImageSize(
                        extractFrameImage(spriteSheet, x, y, frameWidth, frameHeight), 1.5);
                frames.add(new AnimationFrame(frameImage, frameDurationMs));
            }
        }
        return frames;
    }

    /**
     * Loads animation templates dynamically from a JSON configuration file
     * named {@code animations_config.json} located in the
     * {@code /resources/images/animations/} directory.
     * <p>
     * The JSON file should contain a list of animation configurations, where
     * each configuration specifies the necessary information to load an
     * animation from a sprite sheet, including the file name, grid dimensions
     * (rows and columns), a frame time multiplier, and whether the animation
     * should loop.
     * <p>
     * For each valid animation configuration, this method loads the frames from
     * the corresponding sprite sheet using
     * {@link #loadFromSpriteSheet(String, int, int, long)}, creates an
     * {@link AnimationTemplate}, and registers it with the
     * {@link AnimationManager} using the base file name (without extension) as
     * the key.
     *
     * @throws JsonProcessingException If the {@code animations_config.json}
     * file contains invalid JSON format.
     * @throws IOException If an error occurs while reading the
     * {@code animations_config.json} file or any of the sprite sheet images.
     */
    public static void loadAnimationsFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Load the JSON configuration file
            InputStream input = AnimationLoader.class.getResourceAsStream("/images/animations/animations_config.json");
            if (input == null) {
                throw new IOException("animations_config.json not found in " + ANIMATION_FOLDER);
            }

            List<AnimationConfig> configs = mapper.readValue(input, new TypeReference<List<AnimationConfig>>() {
            });

            for (AnimationConfig config : configs) {
                // Load frames from the sprite sheet
                List<AnimationFrame> frames = AnimationLoader.loadFromSpriteSheet(
                        ANIMATION_FOLDER + config.fileName(),
                        config.numRows(),
                        config.numColumns(),
                        (long) (config.frameTimeMultiplier() * BASE_FRAME_TIME_MS)
                );

                // Create an AnimationTemplate
                AnimationTemplate animationTemplate = new AnimationTemplate(frames, config.isLooping());

                // Generate a unique key from the file name
                String fileNameOnly = java.nio.file.Paths.get(config.fileName()).getFileName().toString();
                String animationKey = fileNameOnly.substring(0, fileNameOnly.lastIndexOf('.'));

                // Add the animation to the AnimationManager
                AnimationManager.getInstance().addAnimation(animationKey, animationTemplate);

                System.out.printf("Loaded animation <%s> with <%d> frames.%n", animationKey, frames.size());
            }
        } catch (JsonProcessingException e) {
            System.err.println("Invalid JSON format in animations_config.json: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to load animations: " + e.getMessage());
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Extracts a single frame image from a larger sprite sheet.
     *
     * @param source The source {@link BufferedImage} representing the sprite
     * sheet.
     * @param x The x-coordinate of the top-left corner of the frame to extract.
     * @param y The y-coordinate of the top-left corner of the frame to extract.
     * @param width The width of the frame to extract.
     * @param height The height of the frame to extract.
     * @return A new {@link BufferedImage} containing the extracted frame.
     */
    private static BufferedImage extractFrameImage(BufferedImage source, int x, int y, int width, int height) {
        BufferedImage frame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = frame.createGraphics();
        g.drawImage(source.getSubimage(x, y, width, height), 0, 0, null);
        g.dispose();
        return frame;
    }

    // ----- PRIVATE INNER CLASSES -----
    /**
     * An immutable record representing the configuration for a single
     * animation, used for deserializing animation metadata from a JSON file.
     *
     * @param fileName The name of the sprite sheet file (e.g.,
     * "player_walk.png").
     * @param numRows The number of rows in the sprite sheet grid.
     * @param numColumns The number of columns in the sprite sheet grid.
     * @param frameTimeMultiplier A multiplier to apply to the base frame
     * duration to determine the actual display time for each frame. A value of
     * 1.0 means the frame duration is equal to {@link #BASE_FRAME_TIME_MS}.
     * @param isLooping {@code true} if the animation should loop after the last
     * frame, {@code false} otherwise.
     */
    public static record AnimationConfig(
            String fileName,
            int numRows,
            int numColumns,
            double frameTimeMultiplier,
            boolean isLooping
            ) {

        /**
         * Creates a validated {@code AnimationConfig} instance from JSON
         * properties. This constructor performs basic validation on the input
         * parameters to ensure they are within acceptable ranges.
         *
         * @param fileName The name of the sprite sheet file. Must not be
         * {@code null} or blank.
         * @param numRows The number of rows in the sprite grid. Must be greater
         * than or equal to 1.
         * @param numColumns The number of columns in the sprite grid. Must be
         * greater than or equal to 1.
         * @param frameTimeMultiplier A multiplier for the base frame duration.
         * Must be greater than 0.
         * @param isLooping Whether the animation should loop.
         * @throws IllegalArgumentException If any of the input parameters fail
         * the validation checks.
         */
        @JsonCreator
        public AnimationConfig(
                @JsonProperty("fileName") String fileName,
                @JsonProperty("numRows") int numRows,
                @JsonProperty("numColumns") int numColumns,
                @JsonProperty("frameTimeMultiplier") double frameTimeMultiplier,
                @JsonProperty("isLooping") boolean isLooping
        ) {
            if (fileName == null || fileName.isBlank()) {
                throw new IllegalArgumentException("fileName cannot be null or blank");
            }
            if (numRows < 1 || numColumns < 1) {
                throw new IllegalArgumentException("Rows/columns must be >= 1");
            }
            if (frameTimeMultiplier <= 0) {
                throw new IllegalArgumentException("Multiplier must be > 0");
            }

            this.fileName = fileName;
            this.numRows = numRows;
            this.numColumns = numColumns;
            this.frameTimeMultiplier = frameTimeMultiplier;
            this.isLooping = isLooping;
        }
    }
}

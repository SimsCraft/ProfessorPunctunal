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
 * from JSON configuration files.
 */
public class AnimationLoader {

    // ----- STATIC VARIABLES -----
    /**
     * Base folder where animation image assets are located.
     */
    private static final String ANIMATION_FOLDER = "/images/animations/";
    /**
     * The base duration of one frame at the app's set frame rate (default is
     * ~60 FPS). Used as the reference for computing frame duration via
     * multipliers.
     */
    private static final double BASE_FRAME_TIME_MS = 1000.0 / FRAME_RATE_MS;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor to prevent instantiation.
     */
    private AnimationLoader() {
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Loads frames from a sprite sheet image based on rows and columns.
     * <p>
     * Each frame is assumed to have equal dimensions with no spacing between
     * frames. Additionally, the image is assumed to have no margins/space
     * between the edge and the frames.
     * <p>
     * The animation is formed by slicing the image evenly based on the
     * specified number of rows and columns.
     *
     * @param filePath Path to the sprite sheet image.
     * @param numRows The number of rows in the sprite sheet. (Minimum value: 1)
     * @param numColumns The number of columns in the sprite sheet. (Minimum
     * value: 1)
     * @param frameDurationMs Duration of each frame in milliseconds. (Minimum
     * value: 1)
     *
     * @return List of AnimationFrame objects extracted from the sprite sheet.
     *
     * @throws IllegalArgumentException If the file path is empty or
     * {@code null}.
     * @throws IOException If the image file cannot be loaded.
     *
     * @see <a href="resources/images/README.md">resources/images/README.md</a>
     * for sprite sheet construction guidelines.
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

                BufferedImage frame = extractFrame(spriteSheet, x, y, frameWidth, frameHeight);
                frames.add(new AnimationFrame(frame, frameDurationMs));
            }
        }
        return frames;
    }

    /**
     * Dynamically loads animation templates from a JSON configuration file.
     * <p>
     * This method reads a list of animation definitions from a file named
     * {@code animations_config.json} located in the
     * {@code /resources/images/animations/} directory.
     * <p>
     * Each entry in the file specifies the metadata required to construct an
     * animation from a sprite strip, including the file name, number of rows
     * and columns, frame duration, and whether the animation should loop.
     * <p>
     * The method then loads each animation, creates an
     * {@code AnimationTemplate}, and registers it with the
     * {@code AnimationManager} using the file name (without the extension) as
     * the animation key.
     * <p>
     * This approach allows for easier configuration and extension of animations
     * without modifying source code.
     *
     * @throws JsonProcessingException if the configuration file is of an
     * invalid format
     * @throws IOException if the configuration file cannot be read
     */
    public static void loadAnimationsFromJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();

            // Load the JSON configuration file containing animation data
            InputStream input = AnimationLoader.class.getResourceAsStream("/images/animations/animations_config.json");
            if (input == null) {
                throw new IOException("animations_config.json not found in " + ANIMATION_FOLDER);
            }

            List<AnimationConfig> configs = mapper.readValue(input, new TypeReference<List<AnimationConfig>>() {
            });

            for (AnimationConfig config : configs) {
                // Load the frames from the sprite sheet using the data from the JSON
                List<AnimationFrame> frames = AnimationLoader.loadFromSpriteSheet(
                        ANIMATION_FOLDER + config.fileName,
                        config.numRows,
                        config.numColumns,
                        (long) (config.frameTimeMultiplier * BASE_FRAME_TIME_MS)
                );

                // Create an AnimationTemplate
                AnimationTemplate animationTemplate = new AnimationTemplate(frames, config.isLooping);

                // Generate a unique key for the animation from the file name (without the extension or parent directory)
                String fileNameOnly = java.nio.file.Paths.get(config.fileName).getFileName().toString();
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
     * Extracts a frame from a sprite sheet.
     *
     * @param source The source sprite sheet.
     * @param x X coordinate of the frame.
     * @param y Y coordinate of the frame.
     * @param width Width of the frame.
     * @param height Height of the frame.
     *
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
     * Immutable record representing the configuration for a single animation.
     *
     * Used for deserialization of animation metadata from JSON files.
     *
     * @param fileName Name of the sprite sheet file.
     * @param numRows Number of rows in the sprite grid.
     * @param numColumns Number of columns in the sprite grid.
     * @param frameTimeMultiplier Multiplier to apply to the base frame
     * duration.
     * @param isLooping Whether the animation should loop.
     */
    public static record AnimationConfig(
            String fileName,
            int numRows,
            int numColumns,
            double frameTimeMultiplier,
            boolean isLooping
            ) {

        /**
         * Constructs a validated {@code AnimationConfig} from JSON.
         *
         * @param fileName Name of the sprite sheet file.
         * @param numGridRows Number of grid rows.
         * @param numGridColumns Number of grid columns.
         * @param frameTimeMultiplier Frame time multiplier (> 0).
         * @param isLooping Whether the animation loops.
         * @throws IllegalArgumentException if any argument is invalid.
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

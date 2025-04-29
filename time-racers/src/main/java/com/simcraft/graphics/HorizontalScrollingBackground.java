package com.simcraft.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.interfaces.Renderable;
import com.simcraft.interfaces.Updateable;
import com.simcraft.managers.ImageManager;

/**
 * This class is currently unused.
 * 
 * Represents a background image that scrolls seamlessly in a horizontal
 * direction. This effect is achieved by rendering two identical images,
 * positioned to create a continuous loop as they are translated horizontally.
 */
public class HorizontalScrollingBackground implements Updateable, Renderable {

    // ----- INSTANCE VARIABLES -----
    /**
     * The image to be displayed and scrolled.
     */
    private BufferedImage image;
    /**
     * The width of the background image in pixels.
     */
    private int imageWidth;
    /**
     * The height of the background image in pixels.
     */
    private int imageHeight;

    /**
     * The x-coordinate of the first instance of the background image.
     */
    private int image1X;
    /**
     * The y-coordinate of the first instance of the background image.
     */
    private int image1Y;
    /**
     * The x-coordinate of the second instance of the background image, used for
     * scrolling.
     */
    private int image2X;
    /**
     * The y-coordinate of the second instance of the background image. This
     * remains the same as image1Y for horizontal scrolling.
     */
    private int image2Y;

    /**
     * The horizontal scroll speed in pixels per frame. Positive values scroll
     * right.
     */
    private int scrollSpeedX;

    /**
     * The current horizontal position of the player, used for threshold-based
     * scrolling.
     */
    private int playerX;

    /**
     * Reference to the game panel, used for context and scrolling threshold.
     */
    private GamePanel gamePanel;

    /**
     * The right boundary of the level in pixels. Scrolling stops when the
     * background's left edge reaches this when moving left, or when the player
     * reaches a certain point before this boundary.
     */
    private int levelRightBoundary;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a HorizontalScrollingBackground object.
     *
     * @param imageFilepath The file path to the background image.
     * @param initialX The initial x-coordinate of the top-left corner of the
     * first background image.
     * @param initialY The initial y-coordinate of the top-left corner of the
     * first background image.
     * @param scrollSpeed The base speed of scrolling. The sign determines the
     * actual scrolling direction.
     * @param gamePanel The game panel on which this background is drawn. Cannot
     * be {@code null}.
     * @throws IllegalArgumentException if {@code gamePanel} is {@code null}.
     */
    public HorizontalScrollingBackground(
            final String imageFilepath,
            final int initialX,
            final int initialY,
            final int scrollSpeed,
            final GamePanel gamePanel) {
        if (gamePanel == null) {
            throw new IllegalArgumentException("gamePanel cannot be a null reference.");
        }

        setImage(imageFilepath);
        this.gamePanel = gamePanel;
        this.levelRightBoundary = Integer.MAX_VALUE; // Initialize to a very large value

        image1X = initialX;
        image1Y = initialY;
        image2Y = initialY; // For horizontal scrolling, Y coordinates are the same

        this.scrollSpeedX = scrollSpeed;
        image2X = initialX + imageWidth;
    }

    /**
     * Constructs a non-scrolling HorizontalScrollingBackground object.
     *
     * @param imageFilepath The path to the background image file.
     * @param x The initial x-coordinate of the top-left corner of the image.
     * @param y The initial y-coordinate of the top-left corner of the image.
     * @param panel The {@link GamePanel} on which this background is drawn.
     * Cannot be {@code null}.
     */
    public HorizontalScrollingBackground(String imageFilepath, int x, int y, GamePanel panel) {
        this(imageFilepath, x, y, 0, panel);
    }

    // ----- GETTERS -----
    /**
     * Gets the current horizontal scroll speed.
     *
     * @return The horizontal scroll speed in pixels per frame.
     */
    public int getScrollSpeedX() {
        return scrollSpeedX;
    }

    /**
     * Gets the current horizontal position of the background.
     *
     * @return The x-coordinate of the first background image.
     */
    public int getBackgroundX() {
        return image1X;
    }

    /**
     * Gets the current vertical position of the background.
     *
     * @return The y-coordinate of the first background image.
     */
    public int getBackgroundY() {
        return image1Y;
    }

    /**
     * Gets the width of the background image.
     *
     * @return The width of the background image in pixels.
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * Gets the height of the background image.
     *
     * @return The height of the background image in pixels.
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Gets the right boundary of the level.
     *
     * @return The right boundary in pixels.
     */
    public int getLevelRightBoundary() {
        return levelRightBoundary;
    }

    // ----- SETTERS -----
    /**
     * Sets the scrolling speed for the horizontal direction.
     *
     * @param scrollSpeed The new horizontal scrolling speed in pixels per
     * frame. Positive means scrolling right, negative means scrolling left.
     */
    public void setScrollSpeed(final int scrollSpeed) {
        this.scrollSpeedX = scrollSpeed;
    }

    /**
     * Sets the current horizontal position of the player. This is used to
     * determine when to start or stop the background scrolling based on a
     * threshold.
     *
     * @param playerX The player's current horizontal position in the game
     * world.
     */
    public void setPlayerX(int playerX) {
        this.playerX = playerX;
    }

    /**
     * Sets the right boundary of the level.
     *
     * @param levelRightBoundary The right boundary in pixels.
     */
    public void setLevelRightBoundary(int levelRightBoundary) {
        this.levelRightBoundary = levelRightBoundary;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Loads and sets the background image from a file path.
     *
     * @param imageFilepath The file path to the background image. If loading
     * fails, an error is logged, and image dimensions are set to zero.
     */
    public final void setImage(String imageFilepath) {
        image = ImageManager.loadBufferedImage(imageFilepath);
        if (image != null) {
            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);
        } else {
            imageWidth = 0;
            imageHeight = 0;
            System.err.println("Error loading background image: " + imageFilepath);
        }
    }

    /**
     * Sets the background image using a pre-loaded {@link BufferedImage}.
     *
     * @param image The pre-loaded background image. If {@code null}, an error
     * is logged, and image dimensions are set to zero.
     */
    public final void setImage(final BufferedImage image) {
        if (image != null) {
            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);
        } else {
            imageWidth = 0;
            imageHeight = 0;
            System.err.println("Error loading null background image");
        }
    }

    /**
     * Sets the total width of the level in pixels.
     *
     * @param levelWidth The total width of the level in pixels.
     */
    public void setLevelWidth(int levelWidth) {
        this.levelRightBoundary = levelWidth;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates the position of the background images to create the infinite
     * horizontal scrolling effect based on the current horizontal scroll speed.
     */
    @Override
    public void update() {
        image1X += scrollSpeedX;
        image2X += scrollSpeedX;
        wrapHorizontal();
    }

    /**
     * Draws the two instances of the background image onto the provided
     * {@link Graphics2D} context. The second image is drawn to create the
     * seamless horizontal scrolling illusion.
     *
     * @param g2d The {@link Graphics2D} object used for drawing. The background
     * is only drawn if the image has been loaded successfully.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, image1X, image1Y, null);
            g2d.drawImage(image, image2X, image2Y, null);
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Handles the horizontal wrapping of the background images to create the
     * infinite loop effect.
     */
    private void wrapHorizontal() {
        if (imageWidth <= 0 || gamePanel == null) {
            return;
        }
        int panelWidth = gamePanel.getWidth();

        if (scrollSpeedX > 0) {
            if (image1X > imageWidth) {
                image1X = image2X - imageWidth;
            }
            if (image2X > imageWidth * 2) {
                image2X = image1X - imageWidth;
            }
        } else if (scrollSpeedX < 0) {
            if (image2X < 0) {
                image2X = image1X + imageWidth;
            }
            if (image1X < -imageWidth) {
                image1X = image2X + imageWidth;
            }
        }

        // Boundary checks
        if (image1X > 0) {
            image1X = 0;
            image2X = imageWidth;
        }
        if (image1X < -(imageWidth - panelWidth)) {
            image1X = -(imageWidth - panelWidth);
            image2X = image1X + imageWidth;
        }
    }
}

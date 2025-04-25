package com.simcraft.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.simcraft.graphics.screens.subpanels.GamePanel;
import com.simcraft.interfaces.Renderable;
import com.simcraft.interfaces.Updateable;
import com.simcraft.managers.ImageManager;

/**
 * Represents a background image that scrolls seamlessly in either a horizontal
 * or vertical direction. This effect is achieved by rendering two identical
 * images, positioned to create a continuous loop as they are translated.
 */
public class ScrollingBackground implements Updateable, Renderable {

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
     * The y-coordinate of the second instance of the background image, used for
     * scrolling.
     */
    private int image2Y;

    /**
     * The horizontal scroll speed in pixels per frame. Positive values scroll
     * right.
     */
    private int scrollSpeedX;
    /**
     * The vertical scroll speed in pixels per frame. Positive values scroll
     * down.
     */
    private int scrollSpeedY;

    /**
     * Flag indicating if horizontal scrolling is enabled.
     */
    private boolean horizontalScrolling;
    /**
     * Flag indicating if vertical scrolling is enabled.
     */
    private boolean verticalScrolling;

    /**
     * The current horizontal position of the player, used for threshold-based
     * scrolling.
     */
    private int playerX;
    /**
     * The current vertical position of the player, used for threshold-based
     * scrolling.
     */
    private int playerY;

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
     * Constructs a ScrollingBackground object with specified scrolling
     * behavior.
     *
     * @param imageFilepath The file path to the background image.
     * @param initialX The initial x-coordinate of the top-left corner of the
     * first background image.
     * @param initialY The initial y-coordinate of the top-left corner of the
     * first background image.
     * @param scrollSpeed The base speed of scrolling. The sign and the
     * {@code scrollDirection} determine the actual scrolling.
     * @param scrollDirection The direction of scrolling: "horizontal" or
     * "vertical" (case-insensitive). Any other value disables scrolling with a
     * warning.
     * @param gamePanel The game panel on which this background is drawn. Cannot
     * be {@code null}.
     *
     * @throws IllegalArgumentException if {@code gamePanel} is {@code null}.
     */
    public ScrollingBackground(
            final String imageFilepath,
            final int initialX,
            final int initialY,
            final int scrollSpeed,
            final String scrollDirection,
            final GamePanel gamePanel
    ) {
        if (gamePanel == null) {
            throw new IllegalArgumentException("gamePanel cannot be a null reference.");
        }

        setImage(imageFilepath);
        this.gamePanel = gamePanel;
        this.levelRightBoundary = Integer.MAX_VALUE; // Initialize to a very large value

        if ("horizontal".equalsIgnoreCase(scrollDirection)) {
            horizontalScrolling = true;
            verticalScrolling = false;
            this.scrollSpeedX = scrollSpeed;
            this.scrollSpeedY = 0;
            image2X = initialX + imageWidth;
            image2Y = initialY;
        } else if ("vertical".equalsIgnoreCase(scrollDirection)) {
            verticalScrolling = true;
            horizontalScrolling = false;
            this.scrollSpeedY = scrollSpeed;
            this.scrollSpeedX = 0;
            image2X = initialX;
            image2Y = initialY + imageHeight;
        } else {
            horizontalScrolling = false;
            verticalScrolling = false;
            this.scrollSpeedX = 0;
            this.scrollSpeedY = 0;
            image2X = initialX + imageWidth;
            image2Y = initialY;
            System.err.println("Warning: Invalid scroll direction '" + scrollDirection + "'. Background will not scroll.");
        }
        image1X = initialX;
        image1Y = initialY;
    }

    /**
     * Constructs a non-scrolling ScrollingBackground object.
     *
     * @param imageFilepath The path to the background image file.
     * @param x The initial x-coordinate of the top-left corner of the image.
     * @param y The initial y-coordinate of the top-left corner of the image.
     * @param panel The {@link GamePanel} on which this background is drawn.
     * Cannot be {@code null}.
     */
    public ScrollingBackground(String imageFilepath, int x, int y, GamePanel panel) {
        this(imageFilepath, x, y, 0, "none", panel);
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
     * Gets the current vertical scroll speed.
     *
     * @return The vertical scroll speed in pixels per frame.
     */
    public int getScrollSpeedY() {
        return scrollSpeedY;
    }

    /**
     * Checks if horizontal scrolling is currently enabled.
     *
     * @return {@code true} if horizontal scrolling is enabled, {@code false}
     * otherwise.
     */
    public boolean isScrollingHorizontally() {
        return horizontalScrolling;
    }

    /**
     * Checks if vertical scrolling is currently enabled.
     *
     * @return {@code true} if vertical scrolling is enabled, {@code false}
     * otherwise.
     */
    public boolean isScrollingVertically() {
        return verticalScrolling;
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
     * Sets the scrolling direction to horizontal-only (vertical speed is set to
     * 0).
     * <p>
     * Should be used in conjunction with {@link #setScrollSpeed(int)}.
     */
    public void setScrollingHorizontal() {
        horizontalScrolling = true;
        verticalScrolling = false;
        scrollSpeedY = 0;
    }

    /**
     * Sets the scrolling direction to vertical-only (horizontal speed is set to
     * 0).
     * <p>
     * Should be used in conjunction with {@link #setScrollSpeed(int)}.
     */
    public void setScrollingVertical() {
        horizontalScrolling = false;
        verticalScrolling = true;
        scrollSpeedX = 0;
    }

    /**
     * Sets the scrolling speed for the current scrolling direction.
     *
     * @param scrollSpeed The new scrolling speed in pixels per frame. The sign
     * determines the direction of scroll.
     */
    public void setScrollSpeed(final int scrollSpeed) {
        if (verticalScrolling) {
            scrollSpeedY = scrollSpeed;
        } else if (horizontalScrolling) {
            scrollSpeedX = scrollSpeed;
        }
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
     * Sets the current vertical position of the player. This is used to
     * determine when to start or stop the background scrolling based on a
     * threshold.
     *
     * @param playerY The player's current vertical position in the game world.
     */
    public void setPlayerY(int playerY) {
        this.playerY = playerY;
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

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates the position of the background images to create the infinite
     * scrolling effect based on the current scroll speeds and directions.
     */
    @Override
    public void update() {
        scrollHotizontally();
        scrollVertically();
    }

    /**
     * Draws the two instances of the background image onto the provided
     * {@link Graphics2D} context. The second image is drawn to create the
     * seamless scrolling illusion.
     *
     * @param g2d The {@link Graphics2D} object used for drawing. The background
     * is only drawn if the image has been loaded successfully.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, image1X, image1Y, null);
            if (horizontalScrolling || verticalScrolling) {
                g2d.drawImage(image, image2X, image2Y, null);
            }
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Handles the horizontal scrolling and wrapping of the background images to
     * create the infinite loop effect. The scrolling speed might be influenced
     * by the player's horizontal position.
     */
    private void scrollHotizontally() {
        if (!horizontalScrolling || imageWidth <= 0 || gamePanel == null) {
            return;
        }

        int panelWidth = gamePanel.getWidth();
        int threshold = panelWidth / 2; // Start scrolling when player reaches the middle

        // Adjust scrolling logic based on player movement and level boundaries
        if (playerX > threshold && playerX < levelRightBoundary - threshold) {
            image1X += scrollSpeedX;
            image2X += scrollSpeedX;

            // Wrap-around logic
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
        } else {
            // Keep background still if player is at the start or near the end
            if ((playerX <= threshold && image1X < 0) || (playerX >= levelRightBoundary - threshold && image1X > -(imageWidth - panelWidth))) {
                // Optionally, smoothly stop scrolling
                if (scrollSpeedX > 0 && image1X > 0) {
                    image1X -= scrollSpeedX;
                    image2X -= scrollSpeedX;
                    if (image2X < imageWidth) {
                        image2X = image1X + imageWidth;
                    }
                } else if (scrollSpeedX < 0 && image1X < 0) {
                    image1X -= scrollSpeedX;
                    image2X -= scrollSpeedX;
                    if (image2X > -imageWidth) {
                        image2X = image1X - imageWidth;
                    }
                }
            }
            // Ensure the background doesn't go beyond its boundaries
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

    /**
     * Handles the vertical scrolling and wrapping of the background images to
     * create the infinite loop effect. The scrolling speed might be influenced
     * by the player's vertical position.
     */
    private void scrollVertically() {
        if (!verticalScrolling || imageHeight <= 0 || gamePanel == null) {
            return;
        }

        int panelHeight = gamePanel.getHeight();
        int threshold = panelHeight / 2; // Start scrolling when player reaches the middle

        // If the player is before the threshold, the background doesn't scroll
        if (playerY > threshold && playerY < imageHeight - threshold) {
            image1Y += scrollSpeedY;
            image2Y += scrollSpeedY;

            // Wrap-around logic
            if (scrollSpeedY > 0) {
                if (image1Y > imageHeight) {
                    image1Y = image2Y - imageHeight;
                }
                if (image2Y > imageHeight * 2) {
                    image2Y = image1Y - imageHeight;
                }
            } else if (scrollSpeedY < 0) {
                if (image2Y < 0) {
                    image2Y = image1Y + imageHeight;
                }
                if (image1Y < -imageHeight) {
                    image1Y = image2Y + imageHeight;
                }
            }
        } else {
            // When the player is at or before the threshold, ensure the background doesn't scroll
            if (scrollSpeedY > 0 && image1Y > 0) {
                image1Y -= scrollSpeedY;
                image2Y -= scrollSpeedY;
                if (image2Y < imageHeight) {
                    image2Y = image1Y + imageHeight;
                }
            } else if (scrollSpeedY < 0 && image1Y < 0) {
                image1Y -= scrollSpeedY;
                image2Y -= scrollSpeedY;
                if (image2Y > -imageHeight) {
                    image2Y = image1Y - imageHeight;
                }
            }
            // Optionally, you might want to keep the background at a fixed position
            // when the player is near the top.
            if (playerY <= 0) {
                image1Y = 0;
                image2Y = imageHeight;
            }
            if (image1Y > 0) {
                image1Y = 0;
                image2Y = imageHeight;
            }
            if (image1Y < -(imageHeight - panelHeight)) {
                image1Y = -(imageHeight - panelHeight);
                image2Y = image1Y + imageHeight;
            }
        }
    }

    /**
     * Sets the right boundary of the level.
     *
     * @param levelWidth The total width of the level in pixels.
     */
    public void setLevelWidth(int levelWidth) {
        this.levelRightBoundary = levelWidth;
    }
}

package com.simcraft.graphics.screens.subpanels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.simcraft.entities.Ali;
import com.simcraft.entities.FloatingText;
import com.simcraft.entities.TeleportArrow;
import com.simcraft.entities.EnterClassroom;
import com.simcraft.managers.GameManager;

/**
 * A component that displays the scrolling background and all entities within
 * the game world, including the player, enemies, and interactive objects. It
 * also manages and renders floating text notifications.
 */
public class GamePanel extends Subpanel {

    // ----- STATIC VARIABLES -----
    /**
     * The default color used for rendering floating text notifications.
     */
    private static final Color FLOATING_TEXT_COLOUR = Color.RED;
    /**
     * The default duration in milliseconds for which a floating text message is
     * displayed in the game world.
     */
    private static final long FLOATING_TEXT_DURATION_MS = 2000; // Display for 2 seconds

    // ----- INSTANCE VARIABLES -----
    /**
     * The font used for rendering floating text messages.
     */
    private final Font floatingTextFont = new Font("Arial", Font.BOLD, 16);
    /**
     * The width of a single background tile in pixels. This is determined by
     * the width of the first tile in the {@code backgroundTiles} array.
     */
    private int tileWidth;
    /**
     * An array of {@link BufferedImage} representing the individual tiles that
     * make up the scrolling background. These tiles are drawn repeatedly to
     * create the illusion of a continuous background.
     */
    private BufferedImage[] backgroundTiles;
    /**
     * The current horizontal scroll offset of the background in pixels. This
     * value determines which part of the potentially larger background is
     * currently visible.
     */
    private int scrollOffset;
    /**
     * @deprecated Use the {@link #floatingTexts} list to manage multiple
     * floating text notifications. This field is kept for backward
     * compatibility with older code that might still use it.
     */
    @Deprecated
    private String floatingText = "";
    /**
     * @deprecated Use the timestamp stored within {@link FloatingText} objects
     * to manage their display duration. This field is kept for backward
     * compatibility with older code.
     */
    @Deprecated
    private long floatingTextStartTime = 0;

    // ----- NEW: Special objects -----
    /**
     * A special interactive object in the game world that, when interacted
     * with, triggers a teleport action for the player.
     */
    private TeleportArrow teleportArrow;
    /**
     * A special interactive object that, when interacted with, triggers a
     * transition to a classroom level or scene.
     */
    private EnterClassroom enterClassroom;
    /**
     * A list to hold and manage multiple {@link FloatingText} objects that are
     * currently displayed in the game world. Each object contains its message,
     * position, color, and lifespan.
     */
    private final List<FloatingText> floatingTexts = new ArrayList<>();

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a new {@code GamePanel} with the specified dimensions and an
     * array of background tiles. The background will initially be scrolled to
     * an offset of 0.
     *
     * @param width The width of the game panel in pixels.
     * @param height The height of the game panel in pixels.
     * @param backgroundTiles An array of {@link BufferedImage} to be used as
     * the scrolling background. If the array is not null and not empty, the
     * width of the first tile will be used as the {@code tileWidth}.
     */
    public GamePanel(final int width, final int height, final BufferedImage[] backgroundTiles) {
        super(width, height, (BufferedImage) null); // We handle background rendering directly

        this.backgroundTiles = backgroundTiles;
        this.scrollOffset = 0;
        if (backgroundTiles != null && backgroundTiles.length > 0) {
            this.tileWidth = backgroundTiles[0].getWidth();
        } else {
            this.tileWidth = 0; // Handle the case where no background tiles are provided
        }

        setBackground(new Color(200, 170, 170)); // A default background color, might be overridden
    }

    // ----- GETTERS -----
    /**
     * Returns the total number of background tiles currently loaded.
     *
     * @return The number of background tiles. Returns 0 if
     * {@code backgroundTiles} is null.
     */
    public int getTileCount() {
        return backgroundTiles == null ? 0 : backgroundTiles.length;
    }

    /**
     * Returns the width of a single background tile in pixels.
     *
     * @return The width of a background tile. Returns 0 if no background tiles
     * are loaded.
     */
    public int getTileWidth() {
        return tileWidth;
    }

    /**
     * Returns the current horizontal scroll offset of the background in pixels.
     *
     * @return The scroll offset.
     */
    public double getScrollOffset() {
        return scrollOffset;
    }

    /**
     * Returns the {@link TeleportArrow} object present in this game panel.
     *
     * @return The {@code TeleportArrow} object, or {@code null} if none is set.
     */
    public TeleportArrow getTeleportArrow() {
        return teleportArrow;
    }

    /**
     * Returns the {@link EnterClassroom} object present in this game panel.
     *
     * @return The {@code EnterClassroom} object, or {@code null} if none is
     * set.
     */
    public EnterClassroom getEnterClassroom() {
        return enterClassroom;
    }

    /**
     * Returns the bounding rectangle of the {@link TeleportArrow} object,
     * adjusted for the current background scroll offset. This gives the
     * position of the teleport arrow in the game world coordinates.
     *
     * @return A {@link Rectangle} representing the bounds of the teleport arrow
     * in the game world. If no teleport arrow is present, an empty rectangle is
     * returned.
     */
    public Rectangle getTeleportArrowBounds() {
        if (teleportArrow == null) {
            return new Rectangle();
        }
        return teleportArrow.getBoundsWithScroll(scrollOffset);
    }

    /**
     * Returns the bounding rectangle of the {@link EnterClassroom} object,
     * adjusted for the current background scroll offset. This gives the
     * position of the enter classroom object in the game world coordinates.
     *
     * @return A {@link Rectangle} representing the bounds of the enter
     * classroom object in the game world. If no enter classroom object is
     * present, an empty rectangle is returned.
     */
    public Rectangle getEnterClassroomBounds() {
        if (enterClassroom == null) {
            return new Rectangle();
        }
        return enterClassroom.getBoundsWithScroll(scrollOffset);
    }

    // ----- SETTERS -----
    /**
     * Sets the horizontal scroll offset of the background in pixels.
     *
     * @param offset The new scroll offset.
     */
    public void setScrollOffset(final int offset) {
        this.scrollOffset = offset;
    }

    /**
     * Sets the horizontal scroll offset of the background using a double value,
     * which is then cast to an integer.
     *
     * @param offset The new scroll offset.
     */
    public void setScrollOffset(double offset) {
        this.scrollOffset = (int) offset;
    }

    /**
     * Replaces the current background tiles with a new set and resets the
     * horizontal scroll offset to 0. If the new set of tiles is not null and
     * not empty, the {@code tileWidth} will be updated based on the width of
     * the first new tile.
     *
     * @param newBackgroundTiles An array of {@link BufferedImage} to be used as
     * the new background.
     */
    public void loadNewBackground(final BufferedImage[] newBackgroundTiles) {
        this.backgroundTiles = newBackgroundTiles;
        this.scrollOffset = 0;
        if (newBackgroundTiles != null && newBackgroundTiles.length > 0) {
            this.tileWidth = newBackgroundTiles[0].getWidth();
        } else {
            this.tileWidth = 0;
        }
    }

    /**
     * Sets the {@link TeleportArrow} object to be displayed and managed by this
     * game panel.
     *
     * @param teleportArrow The {@code TeleportArrow} object.
     */
    public void setTeleportArrow(TeleportArrow teleportArrow) {
        this.teleportArrow = teleportArrow;
    }

    /**
     * Sets the {@link EnterClassroom} object to be displayed and managed by
     * this game panel.
     *
     * @param enterClassroom The {@code EnterClassroom} object.
     */
    public void setEnterClassroom(EnterClassroom enterClassroom) {
        this.enterClassroom = enterClassroom;
    }

    // ----- BUSINESS LOGIC -----
    /**
     * Increases the horizontal scroll offset by a given delta value, causing
     * the background to scroll. A positive delta scrolls to the right, and a
     * negative delta scrolls to the left.
     *
     * @param dx The amount to scroll horizontally in pixels.
     */
    public void scroll(final int dx) {
        this.scrollOffset += dx;
    }

    /**
     * @deprecated Use {@link #spawnFloatingText(String, int, int, Color)} to
     * manage multiple floating texts. Triggers the display of a single line of
     * floating text above Ali's head for a default duration and color.
     *
     * @param message The message to display (e.g., "-10s").
     */
    @Deprecated
    public void showFloatingText(final String message) {
        this.floatingText = message;
        this.floatingTextStartTime = System.currentTimeMillis();
    }

    /**
     * Spawns a new floating text notification with the given text at the
     * specified world coordinates and with the specified color. The floating
     * text will be added to the list of active floating texts and will be
     * rendered in the next frame.
     *
     * @param text The text to display.
     * @param x The x-coordinate in the game world where the text should appear.
     * @param y The y-coordinate in the game world where the text should appear.
     * @param color The color of the floating text.
     */
    public void spawnFloatingText(String text, int x, int y, Color color) {
        floatingTexts.add(new FloatingText(text, x, y, color));
    }

    /**
     * Clears all special interactive objects (teleport arrow, enter classroom)
     * and all currently active floating text notifications from the game panel.
     * This is typically used when transitioning between different game levels
     * or states.
     */
    public void clearSpecialObjects() {
        this.teleportArrow = null;
        this.enterClassroom = null;
        this.floatingTexts.clear();
    }

    /**
     * Draws a visual cue, such as an arrow, at a specific location on the
     * screen to prompt the player to press the Enter key for interaction. This
     * is a simple placeholder and can be customized for better visual feedback.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    public void drawEnterArrow(final Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // semi-transparent
        g2d.setColor(Color.CYAN);
        int x = getWidth() - 100;
        int y = getHeight() / 2;
        g2d.fillRect(x, y, 30, 30); // Arrow as a block
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset alpha
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Overrides the {@link Subpanel#render(Graphics2D)} method to render the
     * scrolling background, all game entities (player, enemies), and any active
     * floating text notifications.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    @Override
    public void render(final Graphics2D g2d) {
        renderScrollingBackground(g2d);

        GameManager gameManager = GameManager.getInstance();
        if (!gameManager.isRunning()) {
            return; // Do not render game elements if the game is not running
        }

        // Render special interactive objects
        if (teleportArrow != null) {
            teleportArrow.safeRender(g2d, scrollOffset);
        }
        if (enterClassroom != null) {
            enterClassroom.render(g2d, scrollOffset);
        }

        // Render the player character (Ali)
        Ali ali = gameManager.getAli();
        if (ali != null) {
            ali.safeRender(g2d);
            renderSingleFloatingText(g2d, ali); // Render the deprecated single floating text (if active)
        }

        // Render enemies managed by the EnemyManager
        gameManager.getEnemyManager().render(g2d);

        // Render all active floating text notifications
        renderFloatingTexts(g2d);
    }

    /**
     * Overrides {@link Subpanel#paintComponent(Graphics)} to perform custom
     * painting. This implementation includes a placeholder for an exit door.
     *
     * @param g The {@link Graphics} object used for painting.
     */
    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(getWidth() / 2 - 50, 5, 100, 40);
        g.setColor(Color.WHITE);
        g.drawString("EXIT", getWidth() / 2 - 15, 30);
    }

    // ----- HELPER METHODS -----
    /**
     * Draws the background tiles horizontally, creating a scrolling effect
     * based on the current {@code scrollOffset}. Tiles are repeated seamlessly
     * to provide a continuous background.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    private void renderScrollingBackground(final Graphics2D g2d) {
        if (backgroundTiles == null || tileWidth == 0) {
            return; // Exit if no background tiles are loaded or tile width is invalid
        }
        int numTiles = backgroundTiles.length;
        double offsetWithinTile = scrollOffset % tileWidth;
        int startTileIndex = (int) Math.floor(scrollOffset / tileWidth);

        int x = (int) (-offsetWithinTile);
        int tileIndex = startTileIndex;

        // Loop to draw tiles until the entire width of the panel is covered (and a bit beyond for seamless scrolling)
        while (x < getWidth() + tileWidth) {
            BufferedImage tile = backgroundTiles[tileIndex % numTiles]; // Cycle through tiles if the scroll goes beyond the number of tiles
            g2d.drawImage(tile, x, 0, null);
            x += tileWidth;
            tileIndex++;
        }
    }

    /**
     * @deprecated Use {@link #renderFloatingTexts(Graphics2D)} for rendering
     * multiple floating texts. Renders a single line of floating text above the
     * player character (Ali) if a message is set and its display time has not
     * expired.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     * @param ali The player character ({@link Ali}) to position the text above.
     */
    @Deprecated
    private void renderSingleFloatingText(final Graphics2D g2d, final Ali ali) {
        if (floatingTextStartTime > 0) {
            long elapsedTime = System.currentTimeMillis() - floatingTextStartTime;
            if (elapsedTime <= FLOATING_TEXT_DURATION_MS) {
                g2d.setFont(floatingTextFont);
                g2d.setColor(FLOATING_TEXT_COLOUR);
                String text = floatingText;
                int textWidth = g2d.getFontMetrics().stringWidth(text);

                // Position the text centered above Ali's sprite
                int x = ali.getX() + (ali.getSpriteWidth() - textWidth) / 2;
                int y = ali.getY() - 10;
                g2d.drawString(text, x, y);
            } else {
                floatingText = ""; // Clear the text after its duration
                floatingTextStartTime = 0; // Reset the start time
            }
        }
    }

    /**
     * Renders all active floating text notifications in the game world. It
     * iterates through the list of {@link FloatingText} objects, calls their
     * render method, and removes any text that has exceeded its lifespan.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    private void renderFloatingTexts(Graphics2D g2d) {
        List<FloatingText> toRemove = new ArrayList<>();

        for (FloatingText ft : floatingTexts) {
            ft.render(g2d);
            if (ft.isExpired()) {
                toRemove.add(ft);
            }
        }

        floatingTexts.removeAll(toRemove);
    }
}

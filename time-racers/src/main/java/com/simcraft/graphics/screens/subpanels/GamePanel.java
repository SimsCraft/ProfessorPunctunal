package com.simcraft.graphics.screens.subpanels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.simcraft.entities.Ali;
import com.simcraft.managers.GameManager;

/**
 * A component that displays all the game entities and a scrolling background.
 */
public class GamePanel extends Subpanel {

    // ----- STATIC VARIABLES -----
    private static final Color FLOATING_TEXT_COLOUR = Color.RED;
    private static final long FLOATING_TEXT_DURATION_MS = 2000; // Display for 2 seconds

    // ----- INSTANCE VARIABLES -----
    private final Font floatingTextFont = new Font("Arial", Font.BOLD, 16);
    private int tileWidth;
    private BufferedImage[] backgroundTiles;
    private int scrollOffset;
    private String floatingText = "";
    private long floatingTextStartTime = 0;

    // ----- CONSTRUCTORS -----
    public GamePanel(final int width, final int height, final BufferedImage[] backgroundTiles) {
        super(width, height, (BufferedImage) null); // We'll render the tiles ourselves

        this.backgroundTiles = backgroundTiles;
        this.scrollOffset = 0;
        this.tileWidth = backgroundTiles[0].getWidth();

        setBackground(new Color(200, 170, 170));
    }

    // ----- GETTERS -----
    public int getTileCount() {
        return backgroundTiles.length;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public double getScrollOffset() {
        return scrollOffset;
    }

    // ----- SETTERS -----
    /**
     * Sets the horizontal scroll offset (in pixels).
     *
     * @param offset The new scroll offset.
     */
    public void setScrollOffset(final int offset) {
        this.scrollOffset = offset;
    }

    /**
     * Replaces background tiles and resets scroll.
     */
    public void loadNewBackground(final BufferedImage[] newBackgroundTiles) {
        backgroundTiles = newBackgroundTiles;
        scrollOffset = 0;
        if (newBackgroundTiles.length > 0) {
            tileWidth = newBackgroundTiles[0].getWidth();
        }
    }

    // ----- BUSINESS LOGIC -----
    /**
     * Increases scroll offset based on player's movement. Called externally by
     * GameplayScreen or GameManager.
     */
    public void scroll(final int dx) {
        this.scrollOffset += dx;
    }

    /**
     * Triggers the floating text notification above Ali's head.
     *
     * @param message The message to display (e.g., "-10s").
     */
    public void showFloatingText(final String message) {
        this.floatingText = message;
        this.floatingTextStartTime = System.currentTimeMillis();
    }

    /**
     * Draws an arrow prompting the player to press Enter.
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
     * Renders the background tiles and game entities.
     */
    @Override
    public void render(final Graphics2D g2d) {
        renderScrollingBackground(g2d);

        GameManager gameManager = GameManager.getInstance();
        if (!gameManager.isRunning()) {
            return;
        }

        Ali ali = gameManager.getAli();
        if (ali != null) {
            ali.safeRender(g2d);
            renderFloatingText(g2d, ali); // Render the floating text after Ali
        }

        gameManager.getEnemyManager().render(g2d);
    }

    /**
     * Paints additional components (like exit door).
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
     * Draws background tiles side-by-side with scroll offset.
     */
    private void renderScrollingBackground(final Graphics2D g2d) {
        int numTiles = backgroundTiles.length;
        double offsetWithinTile = scrollOffset % tileWidth;
        int startTileIndex = scrollOffset / tileWidth;

        int x = (int) (-offsetWithinTile);
        int tileIndex = startTileIndex;

        while (x < getWidth() + tileWidth) { // +tileWidth ensures full coverage
            BufferedImage tile = backgroundTiles[tileIndex % numTiles];
            g2d.drawImage(tile, x, 0, null);
            x += tileWidth;
            tileIndex++;
        }
    }

    /**
     * Renders the floating text above Ali's head.
     *
     * @param g2d The Graphics2D object for drawing.
     * @param ali The Ali entity.
     */
    private void renderFloatingText(final Graphics2D g2d, final Ali ali) {
        if (floatingTextStartTime > 0) {
            long elapsedTime = System.currentTimeMillis() - floatingTextStartTime;
            if (elapsedTime <= FLOATING_TEXT_DURATION_MS) {
                g2d.setFont(floatingTextFont);
                g2d.setColor(FLOATING_TEXT_COLOUR);
                String text = floatingText;
                int textWidth = g2d.getFontMetrics().stringWidth(text);

                // Position relative to Ali's sprite, no scrollOffset subtraction
                int x = ali.getX() + (ali.getSpriteWidth() - textWidth) / 2;
                int y = ali.getY() - 10;
                g2d.drawString(text, x, y);
            } else {
                floatingText = "";
                floatingTextStartTime = 0;
            }
        }
    }
}

package com.simcraft.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.simcraft.entities.Ali;
import com.simcraft.managers.GameManager;

/**
 * A specialized {@link Subpanel} responsible for displaying and updating the
 * main game world. It manages the rendering of game entities, including the
 * player and enemies, and handles the horizontal scrolling background.
 */
public class GamePanel extends Subpanel {

    private final GameManager gameManager;

    private BufferedImage[] backgroundTiles;
    private int scrollOffset;
    private int tileWidth;

    // ----- CONSTRUCTORS -----
    public GamePanel(final int width, final int height, final BufferedImage[] backgroundTiles) {
        super(width, height, (BufferedImage) null); // We'll render the tiles ourselves

        this.backgroundTiles = backgroundTiles;
        this.scrollOffset = 0;
        this.tileWidth = backgroundTiles[0].getWidth();
        gameManager = GameManager.getInstance();

        setBackground(new Color(200, 170, 170));
    }

    // ----- BUSINESS LOGIC -----
    /**
     * Increases scroll offset based on player's movement. Called externally by
     * GameplayScreen or GameManager.
     */
    public void scroll(int dx) {
        this.scrollOffset += dx;
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Renders the background tiles and game entities.
     */
    @Override
    public void render(Graphics2D g2d) {
        super.render(g2d);

        if (!gameManager.isRunning()) {
            return;
        }

        renderScrollingBackground(g2d);

        // Render the player
        Ali ali = gameManager.getAli();
        if (ali != null) {
            ali.safeRender(g2d);
        }

        // Render enemies
        gameManager.getEnemyManager().render(g2d);
    }

    /**
     * Overrides the {@link JPanel#paintComponent(Graphics)} method to handle
     * custom drawing. It calls the {@link #render(Graphics2D)} method to draw
     * game elements and also draws the exit door as a temporary UI element.
     *
     * @param g The {@link Graphics} object used for painting. It is cast to
     * {@link Graphics2D} for more advanced drawing operations.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        render(g2d); // Call the render method for drawing game elements

        // Draw exit door (temporary UI element)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(getWidth() / 2 - 50, 5, 100, 40);
        g.setColor(Color.WHITE);
        g.drawString("EXIT", getWidth() / 2 - 15, 30);
    }

    // ----- HELPER METHODS -----
    /**
     * Draws background tiles side-by-side with scroll offset.
     */
    private void renderScrollingBackground(Graphics2D g2d) {
        int numTiles = backgroundTiles.length;

        for (int i = 0; i < numTiles; i++) {
            int x = (i * tileWidth) - scrollOffset;
            g2d.drawImage(backgroundTiles[i], x, 0, null);
        }
    }

    /**
     * Sets the horizontal scroll offset (in pixels).
     *
     * @param offset The new scroll offset.
     */
    public void setScrollOffset(int offset) {
        this.scrollOffset = offset;
    }
}

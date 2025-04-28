package com.simcraft.graphics.screens.subpanels;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.simcraft.entities.Ali;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;

/**
 * A component that displays all the game entities and a scrolling background.
 */
public class GamePanel extends Subpanel {

    private BufferedImage[] backgroundTiles;
    private double scrollOffset;
    private int tileWidth;

    // ----- CONSTRUCTORS -----
    public GamePanel(final int width, final int height, final BufferedImage[] backgroundTiles) {
        super(width, height, (BufferedImage) null); // We'll render the tiles ourselves

        this.backgroundTiles = backgroundTiles;
        this.scrollOffset = 0;
        this.tileWidth = backgroundTiles[0].getWidth();

        setBackground(new Color(200, 170, 170));
    }

    // ----- BUSINESS LOGIC -----

    /**
     * Increases scroll offset based on player's movement.
     * Called externally by GameplayScreen or GameManager.
     */
    public void scroll(int dx) {
        this.scrollOffset += dx;
    }

    public double getScrollOffset() {
        return scrollOffset;
    }

    // ----- OVERRIDDEN METHODS -----

    /**
     * Renders the background tiles and game entities.
     */
    @Override
    public void render(Graphics2D g2d) {
        renderScrollingBackground(g2d);

        GameManager gameManager = GameManager.getInstance();
        if (!gameManager.isRunning()) {
            return;
        }

        Ali ali = gameManager.getAli();
        if (ali != null) {
            ali.safeRender(g2d);
        }

        gameManager.getEnemyManager().render(g2d);
    }

    /**
     * Paints additional components (like exit door).
     */
    @Override
    protected void paintComponent(Graphics g) {
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
    private void renderScrollingBackground(Graphics2D g2d) {
        int panelWidth = getWidth();
        int numTiles = backgroundTiles.length;
        double offsetWithinTile = scrollOffset % tileWidth;
        int startTileIndex = (int)Math.floor(scrollOffset / tileWidth);
    
        int x = (int)(-offsetWithinTile);
        int tileIndex = startTileIndex;
    
        while (x < panelWidth + tileWidth) { // +tileWidth ensures full coverage
            BufferedImage tile = backgroundTiles[tileIndex % numTiles];
            g2d.drawImage(tile, x, 0, null);
            x += tileWidth;
            tileIndex++;
        }
    }
    /**
    * Sets the horizontal scroll offset (in pixels).
    *
    * @param offset
    */
    public void setScrollOffset(double offset) {
        this.scrollOffset = offset;
    }

    public int getTileCount() {
        return backgroundTiles.length;
    }
    
    public int getTileWidth() {
        return tileWidth;
    }
    /**
    * Replaces background tiles and resets scroll.
     */
    public void loadNewBackground(BufferedImage[] newBackgroundTiles) {
        this.backgroundTiles = newBackgroundTiles;
        this.scrollOffset = 0;
        if (newBackgroundTiles.length > 0) {
            this.tileWidth = newBackgroundTiles[0].getWidth();
        }
    }
    /**
    * Draws an arrow prompting the player to press Enter.
    */
    public void drawEnterArrow(Graphics2D g2d) {
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // semi-transparent
        g2d.setColor(Color.CYAN);
        int x = getWidth() - 100;
        int y = getHeight() / 2;
        g2d.fillRect(x, y, 30, 30); // Arrow as a block
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // reset alpha
    }


}

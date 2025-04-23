package com.simcraft.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Set;

import com.simcraft.entities.Ali;
import com.simcraft.entities.enemies.Enemy;
import com.simcraft.managers.GameManager;
import com.simcraft.managers.ImageManager;

/**
 * A component that displays all the game entities
 */
public class GamePanel extends Subpanel {

    // ----- CONSTRUCTORS -----
    public GamePanel(final int width, final int height, final String backgroundImageFilepath) {
        super(width, height, backgroundImageFilepath);

        // Background colour used as a backup in case the image deosn't load.
        setBackground(new Color(200, 170, 170));
        backgroundImage = ImageManager.loadBufferedImage("background/background_1.png");

    }

    // ----- BUSINESS LOGIC -----
    // ----- OVERRIDDEN METHODS -----
    /**
     * Renders the screen's graphical components.
     */
    @Override
    public void render(Graphics2D g2d) {
        super.render(g2d);

        GameManager gameManager = GameManager.getInstance();
        if (!gameManager.isRunning()) {
            return;
        }

        Ali ali = gameManager.getAli();
        if (ali != null) {
            ali.safeRender(g2d);
        }

        Set<Enemy> enemies = gameManager.getEnemyManager().getEnemies();
        if (enemies != null) {
            for (Enemy e : enemies) {
                e.safeRender(g2d);
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw exit door
        g.setColor(Color.DARK_GRAY);
        g.fillRect(getWidth() / 2 - 50, 5, 100, 40);
        g.setColor(Color.WHITE);
        g.drawString("EXIT", getWidth() / 2 - 15, 30);
    }

    // ----- HELPER METHODS -----
}

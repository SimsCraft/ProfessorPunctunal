package com.simcraft.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Set;

import com.simcraft.entities.Ali;
import com.simcraft.entities.NPC;
import com.simcraft.managers.GameManager;

/**
 * A component that displays all the game entities
 */
public class GamePanel extends Subpanel {

    // ----- CONSTRUCTORS -----
    public GamePanel(final int width, final int height, final String backgroundImageFilepath) {
        super(width, height, backgroundImageFilepath);

        // Background colour used as a backup in case the image deosn't load.
        setBackground(new Color(200, 170, 170));
    }

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

        Set<NPC> npcs = gameManager.getNpcManager().getEnemies();
        if (npcs != null) {
            for (NPC n : npcs) {
                n.safeRender(g2d);
            }
        }
    }
}

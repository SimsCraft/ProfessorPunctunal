package com.simcraft.graphics.screens;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import com.simcraft.graphics.GameFrame;
import com.simcraft.interfaces.Renderable;
import com.simcraft.interfaces.Updateable;

/**
 * Abstract base class for all game screens (e.g., main menu, gameplay).
 * Provides a structure for updating and rendering screens.
 */
public abstract class Screen extends JPanel implements Updateable, Renderable {

    // ----- INSTANCE VARIABLES -----
    /**
     * The parent {@link GameFrame} to which this screen belongs
     */
    protected final GameFrame gameFrame;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a screen panel and sets its dimensions to match the game
     * window.
     *
     * @param gameFrame The parent {@link GameFrame} to which this screen
     * belongs.
     */
    protected Screen(GameFrame gameFrame) {
        this.gameFrame = gameFrame;

        // Set the panel size to match the game window dimensions.
        Dimension panelSize = new Dimension(GameFrame.FRAME_WIDTH, GameFrame.FRAME_HEIGHT);
        setPreferredSize(panelSize);
        setMinimumSize(panelSize);
        setMaximumSize(panelSize);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Cleans up resources and UI elements when switching screens.
     */
    public void cleanup() {
        removeAll();
        revalidate();
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Override the paintComponent method to render the game on the screen. This
     * is where custom rendering will occur.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (g instanceof Graphics2D g2d) {
            safeRender(g2d);
        }
    }
}

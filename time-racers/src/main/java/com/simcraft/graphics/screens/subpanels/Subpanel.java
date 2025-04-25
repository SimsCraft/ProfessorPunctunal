package com.simcraft.graphics.screens.subpanels;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JPanel;

import com.simcraft.interfaces.Renderable;
import com.simcraft.managers.ImageManager;

/**
 * Abstract base class for all game screens (e.g., main menu, gameplay).
 * Provides a structure for updating and rendering screens.
 */
public abstract class Subpanel extends JPanel implements Renderable {

    // ----- INSTANCE VARIABLES -----
    protected String backgroundImageFilepath;
    protected BufferedImage backgroundImage;
    protected Random random;

    private boolean sizeInitialized = false; // Flag to track if size is valid

    // ----- CONSTRUCTORS -----
    protected Subpanel(final int width, final int height, final String backgroundImageFilepath) {
        commonInit(width, height);
        setBackgroundImage(backgroundImageFilepath);
    }

    protected Subpanel(final int width, final int height, final BufferedImage backgroundImage) {
        commonInit(width, height);
        this.backgroundImage = backgroundImage;
    }

    // ----- GETTERS -----
    public boolean isSizeInitialized() {
        return sizeInitialized;
    }

    // ----- SETTERS -----
    /**
     * Sets the background image (and its filepath) for this subpanel.
     *
     * @param backgroundImageFilepath The image filepath.
     */
    public final void setBackgroundImage(final String backgroundImageFilepath) {
        this.backgroundImageFilepath = backgroundImageFilepath;
        backgroundImage = ImageManager.loadBufferedImage(backgroundImageFilepath);

        if (backgroundImage == null) {
            System.err.println(String.format(
                    "%s: Could not load background image <'%s'>.",
                    this.getClass().getName(),
                    backgroundImageFilepath
            ));
        }
    }

    // ----- BUSINESS LOGIC METHODS -----
    public Point getRandomPoint() {
        if (!sizeInitialized) {
            throw new IllegalStateException(this.getClass().getName() + " size not initialized, cannot get random point.");
        }
        return new Point(
                random.nextInt(getWidth()),
                random.nextInt(getHeight())
        );
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

    /**
     * Renders the screen's graphical components.
     *
     * By default, only renders the background image (if one is set).
     */
    @Override
    public void render(Graphics2D g2d) {
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // ----- HELPER METHODS -----
    private void commonInit(int width, int height) {
        Dimension panelSize = new Dimension(width, height);
        setPreferredSize(panelSize);
        setMinimumSize(panelSize);
        setMaximumSize(panelSize);
        random = new Random();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                sizeInitialized = true;
                Dimension size = getSize();
                System.err.println(String.format(
                        "%s: resized to: %dx%d.",
                        this.getClass().getName(),
                        size.width,
                        size.height
                ));
            }
        });
    }
}

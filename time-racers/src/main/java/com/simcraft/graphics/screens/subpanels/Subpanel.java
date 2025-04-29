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
 * An abstract base class for all subpanels that are part of game screens (e.g.,
 * main menu, game panel, info panel). It extends {@link JPanel} and implements
 * the {@link Renderable} interface, providing a common structure for handling
 * updates and rendering. Subclasses should override the
 * {@link #render(Graphics2D)} method to draw their specific content.
 */
public abstract class Subpanel extends JPanel implements Renderable {

    // ----- INSTANCE VARIABLES -----
    /**
     * The filepath to the background image for this subpanel.
     */
    protected String backgroundImageFilepath;
    /**
     * The {@link BufferedImage} representing the background image of this
     * subpanel.
     */
    protected BufferedImage backgroundImage;
    /**
     * A {@link Random} number generator instance that can be used by subclasses
     * for various purposes, such as generating random positions or values.
     */
    protected Random random;

    /**
     * A flag to indicate whether the size of the subpanel has been initialized
     * and is valid. This is useful for operations that depend on the panel's
     * dimensions.
     */
    private boolean sizeInitialized = false;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a {@code Subpanel} with the specified width, height, and the
     * filepath to a background image. The background image will be loaded using
     * the {@link ImageManager}.
     *
     * @param width The initial width of the subpanel.
     * @param height The initial height of the subpanel.
     * @param backgroundImageFilepath The filepath to the background image.
     */
    protected Subpanel(final int width, final int height, final String backgroundImageFilepath) {
        commonInit(width, height);
        setBackgroundImage(backgroundImageFilepath);
    }

    /**
     * Constructs a {@code Subpanel} with the specified width, height, and a
     * pre-loaded background image.
     *
     * @param width The initial width of the subpanel.
     * @param height The initial height of the subpanel.
     * @param backgroundImage The pre-loaded background {@link BufferedImage}.
     */
    protected Subpanel(final int width, final int height, final BufferedImage backgroundImage) {
        commonInit(width, height);
        this.backgroundImage = backgroundImage;
    }

    // ----- GETTERS -----
    /**
     * Returns whether the size of this subpanel has been initialized.
     *
     * @return {@code true} if the size is initialized, {@code false} otherwise.
     */
    public boolean isSizeInitialized() {
        return sizeInitialized;
    }

    // ----- SETTERS -----
    /**
     * Sets the background image for this subpanel using the provided filepath.
     * The image is loaded using the {@link ImageManager}. If the image cannot
     * be loaded, an error message is printed to the standard error stream, and
     * the {@code backgroundImage} field will remain {@code null}.
     *
     * @param backgroundImageFilepath The filepath to the image file.
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
    /**
     * Returns a random {@link Point} within the bounds of this subpanel. This
     * method should only be called after the size of the panel has been
     * initialized (i.e., after the first layout event). Calling it before
     * initialization will result in an {@link IllegalStateException}.
     *
     * @return A random {@code Point} within the panel's dimensions.
     * @throws IllegalStateException If the size of the panel has not been
     * initialized.
     */
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
     * Overrides the {@link JPanel#paintComponent(Graphics)} method to perform
     * custom rendering of the subpanel. It casts the {@link Graphics} object to
     * {@link Graphics2D} and calls the {@link #safeRender(Graphics2D)} method,
     * which in turn calls the abstract {@link #render(Graphics2D)} method that
     * subclasses must implement.
     *
     * @param g The {@link Graphics} object to draw on.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (g instanceof Graphics2D g2d) {
            safeRender(g2d);
        }
    }

    /**
     * Renders the graphical components of this subpanel. By default, this
     * method only renders the background image if one is set. Subclasses should
     * override this method to implement their specific rendering logic for game
     * elements, UI components, etc.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Initializes common properties of the subpanel, such as setting the
     * preferred, minimum, and maximum size, initializing the {@link Random}
     * instance, and adding a {@link ComponentListener} to track when the
     * panel's size is initialized (after the first resize event).
     *
     * @param width The initial width of the subpanel.
     * @param height The initial height of the subpanel.
     */
    private void commonInit(int width, int height) {
        Dimension panelSize = new Dimension(width, height);
        setPreferredSize(panelSize);
        setMinimumSize(panelSize);
        setMaximumSize(panelSize);
        random = new Random();
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (!sizeInitialized) {
                    sizeInitialized = true;
                    Dimension size = getSize();
                    System.err.println(String.format(
                            "%s: resized to: %dx%d (initialization).",
                            this.getClass().getName(),
                            size.width,
                            size.height
                    ));
                } else {
                    Dimension size = getSize();
                    System.err.println(String.format(
                            "%s: resized to: %dx%d.",
                            this.getClass().getName(),
                            size.width,
                            size.height
                    ));
                }
            }
        });
    }
}

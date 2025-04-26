package com.simcraft.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.simcraft.entities.Ali;
import com.simcraft.graphics.HorizontalScrollingBackground;
import com.simcraft.interfaces.Renderable;
import com.simcraft.interfaces.Updateable;
import com.simcraft.managers.GameManager;

/**
 * A specialized {@link Subpanel} responsible for displaying and updating the
 * main game world. It manages the rendering of game entities, including the
 * player and enemies, and handles the horizontal scrolling background.
 */
public class GamePanel extends Subpanel implements Updateable {

    /**
     * A list of objects that need to be rendered on this panel.
     */
    private final List<Renderable> renderables = new ArrayList<>();
    /**
     * A list of objects that need to be updated on each game tick.
     */
    private final List<Updateable> updateables = new ArrayList<>();
    /**
     * The background that scrolls horizontally to create the illusion of a
     * larger world.
     */
    private HorizontalScrollingBackground horizontalScrollingBackground;

    /**
     * Manages the overall game state and entities.
     */
    private final GameManager gameManager;

    // ----- CONSTRUCTORS -----
    /**
     * Initializes the GamePanel with specified dimensions and a background
     * image loaded from a file path.
     *
     * @param width The width of the game panel in pixels.
     * @param height The height of the game panel in pixels.
     * @param backgroundImageFilepath The file path to the background image.
     */
    public GamePanel(final int width, final int height, final String backgroundImageFilepath) {
        super(width, height, backgroundImageFilepath);
        setBackground(new Color(200, 170, 170)); // Backup colour if image loading fails
        gameManager = GameManager.getInstance();
        initHorizontalScrollingBackground(backgroundImageFilepath);
    }

    /**
     * Initializes the GamePanel with specified dimensions and a pre-loaded
     * background image.
     *
     * @param width The width of the game panel in pixels.
     * @param height The height of the game panel in pixels.
     * @param backgroundImage The pre-loaded background image.
     */
    public GamePanel(final int width, final int height, final BufferedImage backgroundImage) {
        super(width, height, backgroundImage);
        setBackground(new Color(200, 170, 170)); // Backup colour if image is null
        gameManager = GameManager.getInstance();
        initHorizontalScrollingBackground(backgroundImage);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Retrieves the horizontal scrolling background instance managed by this
     * panel.
     *
     * @return The {@link HorizontalScrollingBackground} object.
     */
    public HorizontalScrollingBackground getHorizontalScrollingBackground() {
        return horizontalScrollingBackground;
    }

    /**
     * Sets the horizontal scrolling background instance for this panel.
     *
     * @param horizontalScrollingBackground The
     * {@link HorizontalScrollingBackground} object to set.
     */
    public void setHorizontalScrollingBackground(HorizontalScrollingBackground horizontalScrollingBackground) {
        this.horizontalScrollingBackground = horizontalScrollingBackground;
        if (!updateables.contains(horizontalScrollingBackground)) {
            updateables.add(horizontalScrollingBackground);
        }
        if (!renderables.contains(horizontalScrollingBackground)) {
            renderables.add(horizontalScrollingBackground);
        }
    }

    /**
     * Adds a {@link Renderable} object to the list of objects to be rendered on
     * this panel.
     *
     * @param renderable The object that needs to be rendered.
     */
    public void addRenderable(Renderable renderable) {
        this.renderables.add(renderable);
    }

    /**
     * Adds an {@link Updateable} object to the list of objects to be updated on
     * each game tick.
     *
     * @param updateable The object that needs to be updated.
     */
    public void addUpdateable(Updateable updateable) {
        this.updateables.add(updateable);
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Updates all the {@link Updateable} objects managed by this panel,
     * including the horizontal scrolling background and other game entities. It
     * also updates the horizontal scrolling background's player X position.
     */
    @Override
    public void update() {
        for (Updateable updatable : updateables) {
            updatable.update();
        }
        Ali ali = gameManager.getAli();
        if (horizontalScrollingBackground != null && ali != null) {
            horizontalScrollingBackground.setPlayerX(ali.getX());
        }
    }

    /**
     * Renders all the graphical components of the game panel, including the
     * background, scrolling layers, player, and enemies.
     *
     * @param g2d The {@link Graphics2D} object used for drawing.
     */
    @Override
    public void render(Graphics2D g2d) {
        super.render(g2d); // Render the base subpanel (e.g., static background)

        if (!gameManager.isRunning()) {
            return;
        }

        // Render all renderable objects
        for (Renderable renderable : renderables) {
            renderable.render(g2d);
        }

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
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(getWidth() / 2 - 50, 5, 100, 40);
        g2d.setColor(Color.WHITE);
        g2d.drawString("EXIT", getWidth() / 2 - 15, 30);
    }

    // ----- HELPER METHODS -----
    /**
     * Initializes the horizontal scrolling background with an image loaded from
     * a file path.
     *
     * @param backgroundImageFilepath The file path to the background image.
     */
    private void initHorizontalScrollingBackground(String backgroundImageFilepath) {
        horizontalScrollingBackground = new HorizontalScrollingBackground(
                backgroundImageFilepath, 0, 0, 5, this // Default horizontal scroll speed of 5
        );
        updateables.add(horizontalScrollingBackground);
        renderables.add(horizontalScrollingBackground);
    }

    /**
     * Initializes the horizontal scrolling background with a pre-loaded
     * {@link BufferedImage}.
     *
     * @param backgroundImage The pre-loaded background image.
     */
    private void initHorizontalScrollingBackground(BufferedImage backgroundImage) {
        horizontalScrollingBackground = new HorizontalScrollingBackground(
                "unused_path", 0, 0, 5, this // Path not used as we provide BufferedImage
        );
        horizontalScrollingBackground.setImage(backgroundImage); // Set the loaded BufferedImage
        updateables.add(horizontalScrollingBackground);
        renderables.add(horizontalScrollingBackground);
    }
}

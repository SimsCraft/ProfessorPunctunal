package com.simcraft.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import com.simcraft.managers.ImageManager;

/**
 * Represents a teleport arrow entity in the game world. When the player
 * interacts with this entity, it typically triggers a level transition.
 */
public class TeleportArrow extends Entity {

    /**
     * The visual representation (sprite) of the teleport arrow.
     */
    private BufferedImage sprite;

    /**
     * Constructs a new {@code TeleportArrow} at the specified coordinates.
     *
     * @param x The initial x-coordinate of the teleport arrow.
     * @param y The initial y-coordinate of the teleport arrow.
     */
    public TeleportArrow(int x, int y) {
        super(x, y);
        sprite = ImageManager.loadBufferedImage("/images/arrow.png");
    }

    /**
     * Renders the teleport arrow at its current position.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    @Override
    public void render(Graphics2D g2d) {
        g2d.drawImage(sprite, position.x, position.y, null);
    }

    /**
     * Renders the teleport arrow at its position, adjusted for the given
     * horizontal scroll offset. This is used when rendering within a scrolling
     * game world.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     * @param scrollOffset The current horizontal scroll offset of the game
     * world.
     */
    public void safeRender(Graphics2D g2d, double scrollOffset) {
        g2d.drawImage(sprite, (int) (position.x - scrollOffset), position.y, null);
    }

    /**
     * Returns the bounding rectangle of the teleport arrow, adjusted for the
     * given horizontal scroll offset. This is used for collision detection
     * within a scrolling game world.
     *
     * @param scrollOffset The current horizontal scroll offset of the game
     * world.
     * @return A {@link Rectangle} representing the bounds of the teleport arrow
     * in the scrolled game world.
     */
    public Rectangle getBoundsWithScroll(double scrollOffset) {
        return new Rectangle((int) (position.x - scrollOffset), position.y, sprite.getWidth(), sprite.getHeight());
    }
}

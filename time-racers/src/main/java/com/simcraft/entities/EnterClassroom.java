package com.simcraft.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import com.simcraft.managers.ImageManager;

/**
 * Represents an interactive "Enter Classroom" entity in the game. This entity
 * has a visual representation of a door and provides a method to get its bounds
 * considering the game's scroll offset, making it suitable for collision
 * detection with other game elements in a scrolling environment.
 */
public class EnterClassroom extends Entity {

    private BufferedImage sprite;

    /**
     * Constructs an {@code EnterClassroom} entity at the specified coordinates.
     * It loads the door sprite from the {@link ImageManager}.
     *
     * @param x The initial x-coordinate of the entity.
     * @param y The initial y-coordinate of the entity.
     */
    public EnterClassroom(int x, int y) {
        super(x, y);
        sprite = ImageManager.loadBufferedImage("/images/door.png");
        if (sprite != null) {
            setHitboxFromRectangle(new Rectangle(x, y, sprite.getWidth(), sprite.getHeight()));
        }
    }

    /**
     * Renders the door sprite at its current position on the graphics context.
     *
     * @param g2d The graphics context to draw on.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (sprite != null) {
            g2d.drawImage(sprite, position.x, position.y, null);
        }
    }

    /**
     * Renders the door sprite at its position, adjusted by the game's scroll
     * offset. This is useful for rendering entities within the visible viewport
     * of a scrolling game.
     *
     * @param g2d The graphics context to draw on.
     * @param scrollOffset The current horizontal scroll offset of the game
     * world.
     */
    public void render(Graphics2D g2d, double scrollOffset) {
        if (sprite != null) {
            g2d.drawImage(sprite, (int) (position.x - scrollOffset), position.y, null);
        }
    }

    /**
     * Returns the bounding rectangle of the "Enter Classroom" entity, adjusted
     * for the game's scroll offset. This is used for accurate collision
     * detection in a scrolling environment.
     *
     * @param scrollOffset The current horizontal scroll offset of the game
     * world.
     * @return A {@link Rectangle} representing the entity's bounds in the
     * scrolled view.
     */
    public Rectangle getBoundsWithScroll(double scrollOffset) {
        if (sprite != null) {
            return new Rectangle((int) (position.x - scrollOffset), position.y, sprite.getWidth(), sprite.getHeight());
        }
        return new Rectangle((int) (position.x - scrollOffset), position.y, 0, 0); // Return an empty rectangle if sprite is not loaded
    }

    /**
     * Overrides the {@link Entity#update()} method. Currently, this entity does
     * not have any dynamic behavior, so the method is empty. If the
     * "EnterClassroom" entity needs to perform updates in the future (e.g.,
     * animation, interaction feedback), the logic should be added here.
     */
    @Override
    public void update() {
        // No dynamic behavior for EnterClassroom entity yet
    }
}

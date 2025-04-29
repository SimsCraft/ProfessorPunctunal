package com.simcraft.entities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Represents a piece of text that floats upwards and fades out over time. This
 * is often used to display damage numbers, status effects, or other temporary
 * information in a game.
 */
public class FloatingText extends Entity {

    // ----- INSTANCE VARIABLES -----
    private String text;
    private Color color;
    private int lifetime;
    private final Font font = new Font("Arial", Font.BOLD, 20);
    private final int floatSpeed = 1; // Pixels to move up per frame

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a {@code FloatingText} object with the specified text,
     * position, and color. The text will have a default lifetime of 50 frames.
     *
     * @param text The text to display.
     * @param x The initial x-coordinate of the text.
     * @param y The initial y-coordinate of the text.
     * @param color The color of the text.
     */
    public FloatingText(String text, int x, int y, Color color) {
        super(x, y);
        this.text = text;
        this.color = color;
        this.lifetime = 50;
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Checks if the floating text has expired (its lifetime has reached zero).
     *
     * @return {@code true} if the lifetime is zero or less, {@code false}
     * otherwise.
     */
    public boolean isExpired() {
        return lifetime <= 0;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Renders the floating text on the graphics context. The text's y-position
     * moves upwards as its lifetime decreases, creating a floating effect. The
     * rendering stops once the lifetime reaches zero.
     *
     * @param g2d The graphics context to draw on.
     */
    @Override
    public void render(Graphics2D g2d) {
        if (lifetime <= 0) {
            return;
        }
        g2d.setColor(color);
        g2d.setFont(font);
        // Move the text upwards based on the remaining lifetime
        int yOffset = (50 - lifetime) * floatSpeed;
        g2d.drawString(text, position.x, position.y - yOffset);
        lifetime--;
    }

    /**
     * Updates the state of the floating text. In this implementation, it only
     * decrements the lifetime. The movement is handled directly in the render
     * method for simplicity. If more complex behavior is needed, it can be
     * added here.
     */
    @Override
    public void update() {
        // Lifetime is decremented in the render method
    }

}

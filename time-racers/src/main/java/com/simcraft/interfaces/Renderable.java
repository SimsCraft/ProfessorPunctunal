package com.simcraft.interfaces;

import java.awt.Graphics2D;

/**
 * Represents objects that can be rendered on the screen.
 * <p>
 * Any class implementing this interface must define a method to render itself
 * using a {@link Graphics2D} object.
 * </p>
 */
public interface Renderable {

    /**
     * Renders the object onto the provided graphics context.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    void render(Graphics2D g2d);

    /**
     * Ensures that the provided {@link Graphics2D} instance is valid before
     * rendering.
     * <p>
     * If {@code g2d} is {@code null}, an error message is logged, and rendering
     * is skipped. Otherwise, the {@link #render(Graphics2D)} method is called.
     * <p>
     * NOTE: NEVER CALL super.safeRender() WITHIN render(); IT WILL CAUSE INFINITE RECURSION ISSUES!!!
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    default void safeRender(Graphics2D g2d) {
        if (g2d == null) {
            System.err.println(String.format(
                    "%s: Could not render due to missing graphics context. Ensure the rendering context is properly initialized.",
                    this.getClass().getName()
            ));
        }

        this.render(g2d);
    }
}

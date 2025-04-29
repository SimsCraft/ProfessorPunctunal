package com.simcraft.interfaces;

import java.awt.Graphics2D;

/**
 * Represents objects that can be rendered on the screen.
 * <p>
 * Any class implementing this interface must define a method to render itself
 * using a {@link Graphics2D} object. The interface also provides a default
 * method, {@link #safeRender(Graphics2D)}, to handle null checks for the
 * graphics context.
 * </p>
 */
public interface Renderable {

    /**
     * Renders the object onto the provided graphics context.
     * <p>
     * Implementations of this method should contain the specific drawing logic
     * for the object. This method is called by {@link #safeRender(Graphics2D)}
     * after ensuring the graphics context is valid.
     * </p>
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    void render(Graphics2D g2d);

    /**
     * Safely renders the object onto the provided graphics context.
     * <p>
     * This default implementation checks if the provided {@code g2d} is not
     * {@code null}. If it is {@code null}, an error message is logged, and rendering is
     * skipped. Otherwise, the {@link #render(Graphics2D)} method of the
     * implementing class is called.
     * </p>
     * <p>
     * <b>Important:</b> DO NOT call {@code super.safeRender()} within your
     * implementation of {@link #render(Graphics2D)}; this WILL lead to infinite
     * recursion.
     * </p>
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    default void safeRender(Graphics2D g2d) {
        if (g2d == null) {
            System.err.println(String.format(
                    "%s: Could not render due to missing graphics context. Ensure the rendering context is properly initialized.",
                    this.getClass().getName()
            ));
            return; // Skip rendering if the graphics context is null
        }

        this.render(g2d);
    }
}

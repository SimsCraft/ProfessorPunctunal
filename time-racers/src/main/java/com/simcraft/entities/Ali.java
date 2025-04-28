package com.simcraft.entities;

import java.awt.Graphics2D;
import java.awt.Point;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JPanel;

import com.simcraft.managers.GameManager;

/**
 * Represents the main playable character, Mr. Ali. Extends the MobileEntity
 * class.
 */
public class Ali extends MobileEntity {

    // PATCH: Add world coordinates
    protected double worldX;
    protected double worldY;

    public Ali(AliBuilder builder) {
        super(builder);

        HashSet<String> aliAnimationKeys = Stream.of(
                "ali_walk_down",
                "ali_walk_left",
                "ali_walk_right",
                "ali_walk_up"
        ).collect(Collectors.toCollection(HashSet::new));

        setAnimationKeys(aliAnimationKeys);

        // Temporary initial animation; will be updated dynamically during movement
        setAnimation("ali_walk_down");

        setSpeed(6); // Fast (he's in a hurry)

        // PATCH: Initialize world position
        this.worldX = builder.startX;
        this.worldY = builder.startY;
    }

    // ----- BUSINESS LOGIC -----
    public void resetPosition() {
        setWorldPosition(350, 550);
    }

    public void setWorldPosition(double x, double y) {
        this.worldX = x;
        this.worldY = y;
    }

    public double getWorldX() {
        return worldX;
    }

    public double getWorldY() {
        return worldY;
    }

    // ----- OVERRIDDEN METHODS -----
    @Override
    public void move() {
        super.move();
        correctPosition();
        updateScreenPosition();
    }

    private void updateScreenPosition() {
        // PATCH: Calculate screen position based on scroll offset
        int scrollOffset = (int) GameManager.getInstance().getScrollOffset();
        position.x = (int) (worldX - scrollOffset);
        position.y = (int) (worldY);
    }

    @Override
    public void safeRender(Graphics2D g2d) {
        if (sprite == null) return;

        int width = (int) (sprite.getWidth() * getScale());
        int height = (int) (sprite.getHeight() * getScale());

        g2d.drawImage(sprite, position.x, position.y, width, height, null);
    }

    // ----- BUILDER PATTERN -----
    public static class AliBuilder extends MobileEntityBuilder<AliBuilder> {

        // PATCH: Add start positions
        private int startX = 0;
        private int startY = 0;

        public AliBuilder(final JPanel panel) {
            super(panel);
        }

        public AliBuilder startPosition(int x, int y) {
            this.startX = x;
            this.startY = y;
            return this;
        }

        public Ali build() {
            return new Ali(this);
        }
    }
}
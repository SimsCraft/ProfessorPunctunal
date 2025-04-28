package com.simcraft.entities;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import com.simcraft.managers.ImageManager;

public class TeleportArrow extends Entity {
    private BufferedImage sprite;

    public TeleportArrow(int x, int y) {
        super(x, y);
        sprite = ImageManager.loadBufferedImage("/images/arrow.png");
    }

    @Override
    public void render(Graphics2D g2d) {
        g2d.drawImage(sprite, position.x, position.y, null);
    }

    public void safeRender(Graphics2D g2d, double scrollOffset) {
        g2d.drawImage(sprite, (int)(position.x - scrollOffset), position.y, null);
    }
    
    public Rectangle getBoundsWithScroll(double scrollOffset) {
        return new Rectangle((int)(position.x - scrollOffset), position.y, sprite.getWidth(), sprite.getHeight());
    }
}
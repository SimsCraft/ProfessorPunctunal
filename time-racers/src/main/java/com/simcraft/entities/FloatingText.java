package com.simcraft.entities;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class FloatingText extends Entity {
    private String text;
    private Color color;
    private int lifetime;

    public FloatingText(String text, int x, int y, Color color) {
        super(x, y);
        this.text = text;
        this.color = color;
        this.lifetime = 50;
    }

    @Override
    public void render(Graphics2D g2d) {
        if (lifetime <= 0) return;
        g2d.setColor(color);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        g2d.drawString(text, position.x, position.y - (50 - lifetime));
        lifetime--;
    }

    public boolean isExpired() {
        return lifetime <= 0;
    }
}
package com.simcraft.graphics.panels;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

public class TimerPanel extends JPanel {
    private int timeLeft;
    private Font arcadeFont;

    public TimerPanel(int startTime, Font arcadeFont) {
        this.timeLeft = startTime;
        this.arcadeFont = arcadeFont != null ? arcadeFont : new Font("Arial", Font.BOLD, 24);
        setPreferredSize(new Dimension(800, 50)); // Fixed size at the top
        setBackground(new Color(0, 0, 0, 100)); // Semi-transparent black background
        setDoubleBuffered(true); // Enable double buffering
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        repaint(); // Only repaint when time changes
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Enable anti-aliasing for smooth text
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set font & color
        g2d.setFont(arcadeFont);
        g2d.setColor(Color.WHITE);

        // Draw text centered
        String text = "Time Left: " + timeLeft + "s";
        FontMetrics fm = g2d.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();
        g2d.drawString(text, x, y);
    }
}
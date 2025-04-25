package com.simcraft.graphics.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.UIConstants;

/**
 * The WelcomeScreen is the initial screen shown to the player when launching
 * the game. It displays a title and a prompt to start, and transitions to the
 * gameplay screen when the ENTER key is pressed.
 */
public class WelcomeScreen extends AbstractScreen {

    private final String title = "WELCOME TO PROFESSOR PUNCTUAL!";
    private final String prompt = "Press ENTER to Start";
    private final Font titleFont = UIConstants.TITLE_FONT;
    private final Font promptFont = UIConstants.BODY_FONT;

    public WelcomeScreen(GameFrame gameFrame) {
        super(gameFrame);
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gameFrame.setScreen(new GameplayScreen(gameFrame));
                }
            }
        });
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Renders the welcome screen, displaying the game title and start prompt.
     *
     * @param g The graphics context to draw on.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render((Graphics2D) g);
    }

    @Override
    public void render(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        g2d.setColor(Color.WHITE);

        // Center the title
        g2d.setFont(titleFont);
        FontMetrics titleMetrics = g2d.getFontMetrics(titleFont);
        int titleWidth = titleMetrics.stringWidth(title);
        int titleX = (width - titleWidth) / 2;
        int titleY = height / 2 - titleMetrics.getHeight();
        g2d.drawString(title, titleX, titleY);

        // Center the prompt
        g2d.setFont(promptFont);
        FontMetrics promptMetrics = g2d.getFontMetrics(promptFont);
        int promptWidth = promptMetrics.stringWidth(prompt);
        int promptX = (width - promptWidth) / 2;
        int promptY = height / 2 + promptMetrics.getAscent() + 20; // Adjust vertical spacing
        g2d.drawString(prompt, promptX, promptY);
    }

    @Override
    public void update() {
        // No dynamic updates required on the welcome screen (yet).
        // Could potentially have animations in future, like a blinking "PRESS ENTER".
    }
}

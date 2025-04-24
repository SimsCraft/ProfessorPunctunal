package com.simcraft.graphics.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.simcraft.graphics.GameFrame;
import com.simcraft.managers.GameManager;

/**
 * The WelcomeScreen is the initial screen shown to the player when launching
 * the game. It displays a title and a prompt to start, and transitions to the
 * gameplay screen when the ENTER key is pressed.
 */
public class WelcomeScreen extends AbstractScreen {

    public WelcomeScreen(GameFrame gameFrame) {
        super(gameFrame);
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    gameFrame.setScreen(new GameplayScreen(gameFrame));
                    GameManager.getInstance().getEnemyManager().addEnemiesTest();
                    // startGame(); // ✅ This will properly close WelcomeScreen and start GameWindow
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
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        g2d.drawString("WELCOME TO PROFESSOR PUNCTUAL!", 100, 250);
        g2d.drawString("Press ENTER to Start", 250, 350);
    }

    @Override
    public void update() {
        // No dynamic updates required on the welcome screen (yet).
        // Could potentially have animations in future, like a blinking "PRESS ENTER".
    }

    // private void startGame() {
    //     welcomeFrame.dispose(); // ✅ Close the WelcomeScreen frame
    //     SwingUtilities.invokeLater(() -> {
    //         JFrame gameFrame = new GameWindow(); // ✅ Open GameWindow in a new JFrame
    //         gameFrame.setVisible(true);
    //     });
    // }
}

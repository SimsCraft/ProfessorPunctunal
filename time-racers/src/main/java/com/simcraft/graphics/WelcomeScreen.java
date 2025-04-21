package com.simcraft.graphics;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WelcomeScreen extends JPanel implements KeyListener {
    private JFrame welcomeFrame;

    public WelcomeScreen(JFrame welcomeFrame) {
        this.welcomeFrame = welcomeFrame;
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("WELCOME TO PROFESSOR PUNCTUAL!", 100, 250);
        g.drawString("Press ENTER to Start", 250, 350);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            startGame(); // ✅ This will properly close WelcomeScreen and start GameWindow
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    private void startGame() {
        welcomeFrame.dispose(); // ✅ Close the WelcomeScreen frame

        SwingUtilities.invokeLater(() -> {
            JFrame gameFrame = new GameWindow(); // ✅ Open GameWindow in a new JFrame
            gameFrame.setVisible(true);
        });
    }
}
package com.simcraft.graphics.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.GameWindow;

public class WelcomeScreen extends Screen implements KeyListener {


    public WelcomeScreen(GameFrame gameFrame) {
        super(gameFrame);
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
            gameFrame.setScreen(new GameplayScreen(gameFrame));
            // startGame(); // ✅ This will properly close WelcomeScreen and start GameWindow
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    // private void startGame() {
    //     welcomeFrame.dispose(); // ✅ Close the WelcomeScreen frame

    //     SwingUtilities.invokeLater(() -> {
    //         JFrame gameFrame = new GameWindow(); // ✅ Open GameWindow in a new JFrame
    //         gameFrame.setVisible(true);
    //     });
    // }
}

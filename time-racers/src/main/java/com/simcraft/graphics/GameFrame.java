package com.simcraft.graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class GameFrame extends JFrame {

    private final JPanel welcomeScreen;

    public GameFrame() {
        setTitle("Professor Punctual");
        setSize(800, 650);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        welcomeScreen = new WelcomeScreen(this);
        welcomeScreen.requestFocusInWindow();
        setContentPane(welcomeScreen);

        setVisible(true);
    }

}

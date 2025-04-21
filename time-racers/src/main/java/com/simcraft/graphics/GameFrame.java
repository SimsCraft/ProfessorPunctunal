package com.simcraft.graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class GameFrame extends JFrame {
    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 650;

    private final JPanel welcomeScreen;

    public GameFrame() {
        setTitle("Professor Punctual");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        welcomeScreen = new WelcomeScreen(this);
        welcomeScreen.requestFocusInWindow();
        setContentPane(welcomeScreen);

        setVisible(true);
    }

}

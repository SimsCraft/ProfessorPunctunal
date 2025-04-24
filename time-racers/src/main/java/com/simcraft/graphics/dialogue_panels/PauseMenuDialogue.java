package com.simcraft.graphics.dialogue_panels;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.UIConstants;
import static com.simcraft.utility.ButtonUtil.createButtonWithText;

public class PauseMenuDialogue extends Dialog {

    private final GameFrame gameFrame;
    private final JLabel menuLabel;
    private final JButton resumeButton;
    private final JButton settingsButton;
    private final JButton restartButton;
    private final JButton quitGameButton;

    /**
     * Constructs a new PauseMenuDialogue.
     *
     * /**
     * Constructs the main menu screen panel with buttons for resuming,
     * restarting, and quitting the game, as well as opening the settings menu.
     *
     * @param gameFrame The parent {@link GameFrame} to which this screen
     * belongs.
     * @param onResumeCallback A callback to resume the game when the "Resume"
     * button is clicked.
     */
    public PauseMenuDialogue(GameFrame gameFrame, Runnable onResumeCallback) {
        super(gameFrame, "Pause Menu", Dialog.ModalityType.APPLICATION_MODAL);

        this.gameFrame = gameFrame;

        setSize(250, 300); // Adjusted size for better readability
        setLocationRelativeTo(gameFrame);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        menuLabel = new JLabel("PAUSE");
        menuLabel.setFont(UIConstants.TITLE_FONT);
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initialize buttons with a callback for their respective actions
        resumeButton = createButtonWithText("RESUME", UIConstants.BUTTON_FONT, 200, 40, true, e -> onResume(onResumeCallback));
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingsButton = createButtonWithText("SETTINGS", UIConstants.BUTTON_FONT, 200, 40, false, this::onSettings);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        restartButton = createButtonWithText("RESTART", UIConstants.BUTTON_FONT, 200, 40, false, this::onRestart);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        quitGameButton = createButtonWithText("QUIT GAME", UIConstants.BUTTON_FONT, 200, 40, true, this::onQuit);
        quitGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add spacing between components
        add(Box.createVerticalStrut(20));
        add(menuLabel);
        add(Box.createVerticalStrut(20));
        add(resumeButton);
        add(Box.createVerticalStrut(10));
        add(settingsButton);
        add(Box.createVerticalStrut(10));
        add(restartButton);
        add(Box.createVerticalStrut(10));
        add(quitGameButton);
        add(Box.createVerticalStrut(20));

        // Close dialog and resume the game if the window is closed directly
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                onResume(onResumeCallback);  // Ensure callback is called even if dialog is closed
                dispose();
            }
        });
    }

    /**
     * Invoked when the "Resume" button is clicked. It executes the callback and
     * closes the dialog.
     *
     * @param onResumeCallback The callback to resume the game.
     */
    private void onResume(Runnable onResumeCallback) {
        onResumeCallback.run();
        dispose(); // Close the dialog after resuming the game
    }

    /**
     * Invoked when the "Settings" button is clicked. For now, it prints a
     * message to the console.
     */
    private void onSettings(ActionEvent e) {
        System.out.println("Settings button clicked.");
        // Future functionality can be implemented here.
    }

    /**
     * Invoked when the "Restart" button is clicked. For now, it prints a
     * message to the console.
     */
    private void onRestart(ActionEvent e) {
        System.out.println("Restart button clicked.");
        // Future functionality can be implemented here.
    }

    /**
     * Invoked when the "Quit" button is clicked. Closes the application.
     */
    private void onQuit(ActionEvent e) {
        System.exit(0);
    }
}

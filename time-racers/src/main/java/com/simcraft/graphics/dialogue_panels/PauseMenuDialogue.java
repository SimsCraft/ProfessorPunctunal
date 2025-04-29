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
import com.simcraft.managers.GameManager;
import static com.simcraft.utility.ButtonUtil.createButtonWithText;

/**
 * A modal dialog displayed when the game is paused, offering options to resume
 * the game, open settings (currently unimplemented), restart the game, or quit
 * the application.
 */
public class PauseMenuDialogue extends Dialog {

    private final JLabel menuLabel;
    private final JButton resumeButton;
    private final JButton settingsButton;
    private final JButton restartButton;
    private final JButton quitGameButton;

    /**
     * Constructs a new {@code PauseMenuDialogue}. This dialog is initially
     * invisible and will block user input to other top-level windows until it
     * is dismissed.
     *
     * @param gameFrame The parent {@link GameFrame} to which this dialog
     * belongs. Used for setting the dialog's owner and its relative position on
     * the screen.
     * @param onResumeCallback A {@link Runnable} that will be executed when the
     * "Resume" button is clicked. This typically contains the logic to unpause
     * the game.
     */
    public PauseMenuDialogue(GameFrame gameFrame, Runnable onResumeCallback) {
        super(gameFrame, "Pause Menu", Dialog.ModalityType.APPLICATION_MODAL);

        setSize(250, 350);
        setLocationRelativeTo(gameFrame);
        setResizable(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        menuLabel = new JLabel("PAUSE");
        menuLabel.setFont(UIConstants.TITLE_FONT);
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        resumeButton = createButtonWithText("RESUME", UIConstants.BUTTON_FONT, 200, 40, true, e -> onResume(onResumeCallback));
        resumeButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        settingsButton = createButtonWithText("SETTINGS", UIConstants.BUTTON_FONT, 200, 40, false, this::onSettings);
        settingsButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        restartButton = createButtonWithText("RESTART", UIConstants.BUTTON_FONT, 200, 40, true, this::onRestart);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        quitGameButton = createButtonWithText("QUIT GAME", UIConstants.BUTTON_FONT, 200, 40, true, this::onQuit);
        quitGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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

        // Handle window closing event to ensure the game is resumed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                onResume(onResumeCallback); // Execute resume callback if the dialog is closed directly
                dispose(); // Close the pause menu dialog
            }
        });
    }

    /**
     * Invoked when the "Resume" button is clicked. It executes the provided
     * callback (typically to unpause the game) and then closes the pause menu
     * dialog.
     *
     * @param onResumeCallback The {@link Runnable} containing the logic to
     * resume the game.
     */
    private void onResume(Runnable onResumeCallback) {
        onResumeCallback.run();
        dispose();
    }

    /**
     * Invoked when the "Settings" button is clicked. Currently, this method
     * only prints a message to the console, as the settings functionality is
     * not yet implemented.
     *
     * @param e The {@link ActionEvent} triggered by the button click.
     */
    private void onSettings(ActionEvent e) {
        System.out.println("Settings button clicked.");
        // Future implementation for opening the settings menu
    }

    /**
     * Invoked when the "Restart" button is clicked. This method triggers the
     * game restart logic in the {@link GameManager} and then closes the pause
     * menu dialog.
     *
     * @param e The {@link ActionEvent} triggered by the button click.
     */
    private void onRestart(ActionEvent e) {
        GameManager.getInstance().restartGame();
        dispose();
    }

    /**
     * Invoked when the "Quit Game" button is clicked. This method terminates
     * the entire application by calling {@code System.exit(0)}.
     *
     * @param e The {@link ActionEvent} triggered by the button click.
     */
    private void onQuit(ActionEvent e) {
        System.exit(0);
    }
}

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
 * A modal dialog displayed when the game ends, offering options to restart the
 * game or quit the application.
 */
public class GameOverDialogue extends Dialog {

    private final JLabel gameOverLabel;
    private final JButton restartButton;
    private final JButton quitGameButton;

    /**
     * Constructs a new {@code GameOverDialogue}. This dialog is initially
     * invisible and will block user input to other top-level windows until it
     * is dismissed.
     *
     * @param gameFrame The parent {@link GameFrame} to which this dialog
     * belongs. Used for setting the dialog's owner and its relative position on
     * the screen.
     */
    public GameOverDialogue(GameFrame gameFrame) {
        super(gameFrame, "Game Over", Dialog.ModalityType.APPLICATION_MODAL);

        setSize(400, 250);
        setLocationRelativeTo(gameFrame);
        setResizable(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(UIConstants.TITLE_FONT);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        restartButton = createButtonWithText("RESTART", UIConstants.BUTTON_FONT, 200, 40, true, this::onRestart);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        quitGameButton = createButtonWithText("QUIT GAME", UIConstants.BUTTON_FONT, 200, 40, true, this::onQuit);
        quitGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(20));
        add(gameOverLabel);
        add(Box.createVerticalStrut(20));
        add(restartButton);
        add(Box.createVerticalStrut(10));
        add(quitGameButton);
        add(Box.createVerticalStrut(20));

        // Handle window closing event
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dispose(); // Close the dialog when the window's close button is pressed
            }
        });
    }

    /**
     * Invoked when the "Restart" button is clicked. This method disposes of the
     * game over dialog and triggers the game restart logic in the
     * {@link GameManager}.
     *
     * @param e The {@link ActionEvent} triggered by the button click.
     */
    private void onRestart(ActionEvent e) {
        System.out.println("Restart button clicked from Game Over.");
        dispose();
        GameManager.getInstance().restartGame();
    }

    /**
     * Invoked when the "Quit" button is clicked. This method terminates the
     * entire application by calling {@code System.exit(0)}.
     *
     * @param e The {@link ActionEvent} triggered by the button click.
     */
    private void onQuit(ActionEvent e) {
        System.exit(0);
    }
}

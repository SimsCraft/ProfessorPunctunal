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

public class GameOverDialogue extends Dialog {

    private final GameFrame gameFrame;
    private final JLabel gameOverLabel;
    private final JButton restartButton;
    private final JButton quitGameButton;

    /**
     * Constructs a new GameOverDialogue.
     *
     * @param gameFrame The parent {@link GameFrame} to which this dialog
     * belongs.
     */
    public GameOverDialogue(GameFrame gameFrame) {
        super(gameFrame, "Game Over", Dialog.ModalityType.APPLICATION_MODAL);

        this.gameFrame = gameFrame;

        setSize(400, 250); // Adjusted size
        setLocationRelativeTo(gameFrame);
        setResizable(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        gameOverLabel = new JLabel("GAME OVER");
        gameOverLabel.setFont(UIConstants.TITLE_FONT);
        gameOverLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Initialize buttons
        restartButton = createButtonWithText("RESTART", UIConstants.BUTTON_FONT, 200, 40, true, this::onRestart);
        restartButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        quitGameButton = createButtonWithText("QUIT GAME", UIConstants.BUTTON_FONT, 200, 40, true, this::onQuit);
        quitGameButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add spacing and components
        add(Box.createVerticalStrut(20));
        add(gameOverLabel);
        add(Box.createVerticalStrut(20));
        add(restartButton);
        add(Box.createVerticalStrut(10));
        add(quitGameButton);
        add(Box.createVerticalStrut(20));

        // Close dialog on window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                dispose();
            }
        });
    }

    /**
     * Invoked when the "Restart" button is clicked. Restarts the game.
     *
     * @param e The ActionEvent triggered by the button click.
     */
    private void onRestart(ActionEvent e) {
        System.out.println("Restart button clicked from Game Over.");
        dispose(); // Close the game over dialog
        GameManager.getInstance().restartGame(); // Call restart logic in GameManager
    }

    /**
     * Invoked when the "Quit" button is clicked. Closes the application.
     *
     * @param e The ActionEvent triggered by the button click.
     */
    private void onQuit(ActionEvent e) {
        System.exit(0);
    }
}

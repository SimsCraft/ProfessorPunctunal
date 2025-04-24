package com.simcraft.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

import com.simcraft.graphics.UIConstants;
import com.simcraft.managers.GameManager;
import com.simcraft.utility.ButtonUtil;

/**
 * The InfoPanel displays game information (e.g., player health, elapsed time,
 * and score) pause button for the game.
 * <p>
 * This panel is displayed at the top of the gameplay screen.
 * </p>
 */
public final class InfoPanel extends Subpanel {

    // ----- INSTANCE VARIABLES -----
    private final JButton pauseButton;
    private final JLabel levelLabel;
    private final JLabel timerLabel;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs the InfoPanel.
     */
    public InfoPanel(final int width, final int height, final String backgroundImageFilepath) {
        super(width, height, backgroundImageFilepath);

        // Background colour used as a backup in case the image doesn't load.
        setBackground(new Color(87, 73, 100));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setAlignmentX(Component.CENTER_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);

        // ----- Initialise elements -----
        pauseButton = ButtonUtil.createButtonWithIcon(
                "/images/icons/pause-button.png",
                64,
                64,
                false,
                GameManager.getInstance()::onPause
        );

        levelLabel = new JLabel("LEVEL <no.>");
        levelLabel.setFont(UIConstants.BODY_FONT);

        timerLabel = new JLabel("MM.SS");
        timerLabel.setFont(UIConstants.BODY_FONT);

        // ----- Add to InfoPanel -----
        add(pauseButton);
        add(levelLabel);
        add(timerLabel);
    }

    // ----- BUSINESS LOGIC METHODS -----
    public void updateLevelCounter(final int levelCounter) {
        timerLabel.setText(String.format("LEVEL %d", levelCounter));
    }

    public void updateTimerDisplay(final int remainingSeconds) {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Remaining Time: %d:%02d", minutes, seconds));
    }

    //     score = 0;
    //     elapsedSeconds = 0;
    //     // Create the font used for the button and labels.
    //     Font buttonFont = new Font(GameFrame.BODY_TYPEFACE, Font.PLAIN, 16);
    //     // Create the pause button.
    //     pauseMenuButton = createButton(
    //             "PAUSE", buttonFont, 100, 40, true,
    //             GameManager.getInstance()::onPause
    //     );
    //     // Create the score and timer labels.
    //     scoreLabel = createStatusLabel();
    //     timerLabel = createStatusLabel();
    //     // Arrange components with horizontal spacing.
    //     add(Box.createHorizontalStrut(20));
    //     add(pauseMenuButton);
    //     add(Box.createHorizontalGlue());
    //     add(scoreLabel);
    //     add(Box.createHorizontalGlue());
    //     add(timerLabel);
    //     add(Box.createHorizontalStrut(20));
    //     // Initialise the score display.
    //     updateScoreDisplay(0);
    // }
    // /**
    //  * Updates the displayed score
    //  *
    //  * @param score The new score.
    //  */
    // public final void updateScoreDisplay(final int score) {
    //     scoreLabel.setText(String.format("Score: %d", score));
    // }
    // /**
    //  * Updates the timer label with a formatted elapsed time string.
    //  *
    //  * @param elapsedSeconds The elapsed time in seconds.
    //  */
    // public void updateTimerDisplay(final int elapsedSeconds) {
    //     int minutes = elapsedSeconds / 60;
    //     int seconds = elapsedSeconds % 60;
    //     timerLabel.setText(String.format("Elapsed Time: %d:%02d", minutes, seconds));
    // }
    // /**
    //  * Creates a JLabel for displaying status information (score or timer).
    //  *
    //  * Buggy at the moment
    //  *
    //  * @return A configured JLabel.
    //  */
    // private JLabel createStatusLabel() {
    //     JLabel label = new JLabel();
    //     label.setFont(new Font(GameFrame.BODY_TYPEFACE, Font.BOLD, 16));
    //     label.setForeground(Color.WHITE);
    //     // Will try to fix for the next assignment
    //     // label.setForeground(new Color(70, 0, 50));
    //     // label.setBorder(new RoundedBorder(Color.BLACK, Color.WHITE, 10));
    //     return label;
    // }
}

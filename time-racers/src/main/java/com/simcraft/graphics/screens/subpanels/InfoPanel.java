package com.simcraft.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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

        setBackground(new Color(87, 73, 100));
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // ----- Initialise elements -----
        pauseButton = ButtonUtil.createButtonWithIcon(
                "/images/icons/pause-button.png",
                64,
                64,
                true,
                GameManager.getInstance()::onPause
        );

        levelLabel = new JLabel("LEVEL <no.>");
        levelLabel.setFont(UIConstants.ARCADE_FONT);
        levelLabel.setForeground(Color.WHITE);

        timerLabel = new JLabel("Remaining Time: MM.SS");
        timerLabel.setFont(UIConstants.ARCADE_FONT);
        timerLabel.setForeground(Color.WHITE);

        // Common constraint setup
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.CENTER;

        // ----- Left-aligned pause button -----
        gbc.gridx = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(0, 20, 0, 0);
        add(pauseButton, gbc);

        // ----- Centered level label -----
        gbc.gridx = 1;
        gbc.weightx = 0.34;
        gbc.anchor = GridBagConstraints.CENTER;
        add(levelLabel, gbc);

        // ----- Right-aligned timer label -----
        gbc.gridx = 2;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 0, 0, 20);
        add(timerLabel, gbc);
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

    // ----- OVERRIDDEN METHODS -----
    // @Override
    // protected void paintComponent(Graphics g) {
    //     super.paintComponent(g);
    //     Graphics2D g2d = (Graphics2D) g;
    //     // Enable anti-aliasing for smooth text
    //     g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    //     // Set font & color
    //     g2d.setFont(arcadeFont);
    //     g2d.setColor(Color.WHITE);
    //     // Draw text centered
    //     String text = "Time Left: " + timeLeft + "s";
    //     FontMetrics fm = g2d.getFontMetrics();
    //     int x = (getWidth() - fm.stringWidth(text)) / 2;
    //     int y = (getHeight() + fm.getAscent()) / 2 - fm.getDescent();
    //     g2d.drawString(text, x, y);
    // }
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

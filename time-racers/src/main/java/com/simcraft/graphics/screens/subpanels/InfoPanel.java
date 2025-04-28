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
}

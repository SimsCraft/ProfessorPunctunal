package com.simcraft.graphics.screens.subpanels;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JLabel;

import com.simcraft.graphics.UIConstants;
import com.simcraft.interfaces.Updateable;
import com.simcraft.managers.GameManager;
import com.simcraft.utility.ButtonUtil;

/**
 * The InfoPanel displays game information (e.g., player health, elapsed time,
 * and score) pause button for the game.
 * <p>
 * This panel is displayed at the top of the gameplay screen.
 * </p>
 */
public final class InfoPanel extends Subpanel implements Updateable {

    // ----- STATIC VARIABLES -----
    private static final long NOTIFICATION_DURATION_MS = 2000; // Display for 2 seconds

    // ----- INSTANCE VARIABLES -----
    private final JButton pauseButton;
    private final JLabel levelLabel;
    private final JLabel timerLabel;
    private final JLabel collisionNotificationLabel;
    private String currentNotification = "";
    private long notificationDisplayStartTime = 0;

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

        collisionNotificationLabel = new JLabel("");
        collisionNotificationLabel.setFont(UIConstants.BODY_FONT_SMALL);
        collisionNotificationLabel.setForeground(Color.YELLOW);

        // Common constraint setup
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.5; // Adjust weighty for better vertical distribution
        gbc.anchor = GridBagConstraints.CENTER;

        // ----- Left-aligned pause button -----
        gbc.gridx = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(10, 20, 0, 0); // Add some top padding
        add(pauseButton, gbc);

        // ----- Centered level label -----
        gbc.gridx = 1;
        gbc.weightx = 0.34;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 0, 0); // Add some top padding
        add(levelLabel, gbc);

        // ----- Right-aligned timer label -----
        gbc.gridx = 2;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(10, 0, 0, 20); // Add some top padding
        add(timerLabel, gbc);

        // ----- Notification label underneath the timer -----
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 0, 10, 20); // Padding below the timer
        add(collisionNotificationLabel, gbc);
    }

    // ----- BUSINESS LOGIC METHODS -----
    public void updateLevelCounter(final int levelCounter) {
        levelLabel.setText(String.format("LEVEL %d", levelCounter));
    }

    public void updateTimerDisplay(final int remainingSeconds) {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Remaining Time: %d:%02d", minutes, seconds));
        updateCollisionNotificationDisplay(); // Update the notification visibility
    }

    public void showCollisionNotification(final String enemyName, final int timeLost) {
        currentNotification = String.format("Stopped by %s! Lost %d seconds!", enemyName, timeLost);
        notificationDisplayStartTime = System.currentTimeMillis();
        collisionNotificationLabel.setText(currentNotification);
    }

    private void updateCollisionNotificationDisplay() {
        if (notificationDisplayStartTime > 0) {
            long elapsedTime = System.currentTimeMillis() - notificationDisplayStartTime;
            if (elapsedTime > NOTIFICATION_DURATION_MS) {
                collisionNotificationLabel.setText("");
                currentNotification = "";
                notificationDisplayStartTime = 0;
            } else {
                collisionNotificationLabel.setText(currentNotification);
            }
        }
    }

    // ----- OVERRIDDEN METHODS -----
    @Override
    public void update() {
        updateCollisionNotificationDisplay();
    }
}

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
 * The {@code InfoPanel} is a subpanel displayed at the top of the gameplay
 * screen. It provides real-time game information to the player, such as the
 * current level, the remaining time, and temporary collision notifications. It
 * also includes a pause button to allow the player to pause the game.
 */
public final class InfoPanel extends Subpanel implements Updateable {

    // ----- STATIC VARIABLES -----
    /**
     * The duration in milliseconds for which collision notifications are
     * displayed on the info panel.
     */
    private static final long NOTIFICATION_DURATION_MS = 2000; // Display for 2 seconds

    // ----- INSTANCE VARIABLES -----
    /**
     * A button that, when clicked, triggers the game pause action.
     */
    private final JButton pauseButton;
    /**
     * A label that displays the current game level.
     */
    private final JLabel levelLabel;
    /**
     * A label that displays the remaining time in the current game session.
     */
    private final JLabel timerLabel;
    /**
     * A label used to display temporary notifications, such as when the player
     * collides with an enemy and loses time.
     */
    private final JLabel collisionNotificationLabel;
    /**
     * Stores the text of the current notification being displayed.
     */
    private String currentNotification = "";
    /**
     * Stores the timestamp (in milliseconds) when the current notification
     * started being displayed.
     */
    private long notificationDisplayStartTime = 0;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs an {@code InfoPanel} with the specified width, height, and
     * background image. Initializes the layout and all the UI components (pause
     * button, level label, timer label, and notification label).
     *
     * @param width The width of the info panel.
     * @param height The height of the info panel.
     * @param backgroundImageFilepath The filepath to the background image for
     * this panel.
     */
    public InfoPanel(final int width, final int height, final String backgroundImageFilepath) {
        super(width, height, backgroundImageFilepath);

        setBackground(new Color(87, 73, 100)); // Background colour in case of issues with the image
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // ----- Initialise UI elements -----
        pauseButton = ButtonUtil.createButtonWithIcon(
                "/images/icons/pause_button.png",
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

        // ----- Common GridBagConstraints setup -----
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.CENTER;

        // ----- Left-aligned pause button -----
        gbc.gridx = 0;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.insets = new Insets(10, 20, 0, 0);
        add(pauseButton, gbc);

        // ----- Centered level label -----
        gbc.gridx = 1;
        gbc.weightx = 0.34;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(levelLabel, gbc);

        // ----- Right-aligned timer label -----
        gbc.gridx = 2;
        gbc.weightx = 0.33;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(10, 0, 0, 20);
        add(timerLabel, gbc);

        // ----- Notification label underneath the timer -----
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weighty = 0.5;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 0, 10, 20);
        add(collisionNotificationLabel, gbc);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Updates the displayed level counter on the {@code levelLabel}.
     *
     * @param levelCounter The current game level number.
     */
    public void updateLevelCounter(final int levelCounter) {
        levelLabel.setText(String.format("LEVEL %d", levelCounter));
    }

    /**
     * Updates the displayed remaining time on the {@code timerLabel}. The time
     * is formatted as "MM:SS". Also calls
     * {@link #updateCollisionNotificationDisplay()} to handle the visibility of
     * collision notifications.
     *
     * @param remainingSeconds The remaining time in seconds.
     */
    public void updateTimerDisplay(final int remainingSeconds) {
        int minutes = remainingSeconds / 60;
        int seconds = remainingSeconds % 60;
        timerLabel.setText(String.format("Remaining Time: %d:%02d", minutes, seconds));
        updateCollisionNotificationDisplay(); // Ensure notification visibility is updated
    }

    /**
     * Displays a notification on the {@code collisionNotificationLabel} when a
     * collision occurs. The notification includes the name of the enemy and the
     * amount of time lost. The notification will be displayed for a duration
     * defined by {@link #NOTIFICATION_DURATION_MS}.
     *
     * @param enemyName The name of the enemy involved in the collision.
     * @param timeLost The amount of time lost (in seconds) due to the
     * collision.
     */
    public void showCollisionNotification(final String enemyName, final int timeLost) {
        currentNotification = String.format("Stopped by %s! Lost %d seconds!", enemyName, timeLost);
        notificationDisplayStartTime = System.currentTimeMillis();
        collisionNotificationLabel.setText(currentNotification);
    }

    /**
     * Updates the display of the collision notification based on the elapsed
     * time since it was first shown. If the elapsed time exceeds the
     * notification duration, the label is cleared.
     */
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
    /**
     * Implements the {@link Updateable} interface. This method is called
     * periodically to update the state of the {@code InfoPanel}, specifically
     * handling the display and timing of collision notifications.
     */
    @Override
    public void update() {
        updateCollisionNotificationDisplay();
    }
}

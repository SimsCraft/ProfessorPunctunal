package com.simcraft.utility;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;

import com.simcraft.managers.ImageManager;

/**
 * Utility class for creating various buttons in the UI.
 */
public class ButtonUtil {

    private ButtonUtil() {
        // Private constructor to prevent instantiation.
    }

    /**
     * Creates a regular JButton with the specified properties.
     *
     * @param text The text displayed on the button.
     * @param font The font to be used on the button.
     * @param buttonWidth The width of the button.
     * @param buttonHeight The height of the button.
     * @param enabled The initial enabled state of the button.
     * @param actionListener The ActionListener to be added to the button.
     * @return A configured JButton.
     */
    public static JButton createButtonWithIcon(final String imageFilepath, final int buttonWidth, final int buttonHeight, boolean enabled, final ActionListener actionListener) {
        JButton button = createGenericButton(new JButton(), buttonWidth, buttonHeight, enabled, actionListener);
        try {
            BufferedImage image = ImageManager.loadBufferedImage(imageFilepath);
            button.setIcon(new ImageIcon (image));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return button;
    }

    /**
     * Creates a regular JButton with the specified properties.
     *
     * @param text The text displayed on the button.
     * @param font The font to be used on the button.
     * @param buttonWidth The width of the button.
     * @param buttonHeight The height of the button.
     * @param enabled The initial enabled state of the button.
     * @param actionListener The ActionListener to be added to the button.
     * @return A configured JButton.
     */
    public static JButton createButtonWithText(final String text, final Font font, final int buttonWidth, final int buttonHeight, boolean enabled, final ActionListener actionListener) {
        if (font == null) {
            throw new IllegalArgumentException("Font must not be null");
        }

        JButton button = createGenericButton(new JButton(text), buttonWidth, buttonHeight, enabled, actionListener);
        button.setFont(font);

        return button;
    }

    /**
     * Creates a JToggleButton with the specified properties.
     *
     * @param text The text displayed on the button.
     * @param font The font to be used on the button.
     * @param buttonWidth The width of the button.
     * @param buttonHeight The height of the button.
     * @param enabled The initial enabled state of the button.
     * @param actionListener The ActionListener to be added to the button.
     * @return A configured JToggleButton.
     */
    public static JToggleButton createToggleButtonWithText(final String text, final Font font, final int buttonWidth, final int buttonHeight, boolean enabled, final ActionListener actionListener) {
        if (font == null) {
            throw new IllegalArgumentException("Font must not be null");
        }

        JToggleButton button = createGenericButton(new JToggleButton(text), buttonWidth, buttonHeight, enabled, actionListener);
        button.setFont(font);

        return button;
    }

    /**
     * Creates a generic button with the specified properties. Used internally
     * for button creation.
     *
     * @param button The type of button to be created (JButton or
     * JToggleButton).
     * @param buttonWidth The width of the button.
     * @param buttonHeight The height of the button.
     * @param enabled The initial enabled state of the button.
     * @param actionListener The ActionListener to be added to the button.
     * @return A configured button (JButton or JToggleButton).
     */
    private static <T extends AbstractButton> T createGenericButton(T button, int buttonWidth, int buttonHeight, boolean enabled, ActionListener actionListener) {
        if (actionListener == null) {
            throw new IllegalArgumentException("Action listener must not be null");
        }

        // Validate dimensions
        if (buttonWidth <= 0 || buttonHeight <= 0) {
            throw new IllegalArgumentException("Button width and height must be positive values.");
        }

        Dimension buttonSize = new Dimension(buttonWidth, buttonHeight);

        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);
        button.setEnabled(enabled);
        button.addActionListener(actionListener);

        return button;
    }

}

package com.simcraft.graphics.screens;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import com.simcraft.graphics.GameFrame;
import com.simcraft.graphics.UIConstants;
import com.simcraft.graphics.effects.screen_effects.HorizontalScreenWipeEffect;

/**
 * The {@code WelcomeScreen} is the initial screen displayed to the player when
 * the game starts. It presents the game's title and a prompt instructing the
 * player to press the ENTER key to begin the gameplay. Upon pressing ENTER, it
 * initiates a visual transition effect (horizontal screen wipe) before
 * switching to the {@link GameplayScreen}.
 */
public class WelcomeScreen extends AbstractScreen {

    // ----- STATIC VARIABLES -----
    /**
     * The title of the game displayed on the welcome screen.
     */
    private static final String TITLE = "WELCOME TO PROFESSOR PUNCTUAL!";
    /**
     * The prompt displayed to instruct the player on how to start the game.
     */
    private static final String START_PROMPT = "Press ENTER to Start";
    /**
     * The font used for rendering the game title.
     */
    private static final Font TITLE_FONT = UIConstants.TITLE_FONT;
    /**
     * The font used for rendering the start prompt.
     */
    private static final Font PROMPT_FONT = UIConstants.BODY_FONT;

    // ----- INSTANCE VARIABLES -----
    /**
     * An effect that creates a horizontal wipe across the screen, used as a
     * transition to the next screen.
     */
    private HorizontalScreenWipeEffect screenWipeEffect;

    /**
     * Constructs a {@code WelcomeScreen} associated with the main
     * {@link GameFrame}. It sets the background to black, makes the screen
     * focusable to receive key events, and initializes the screen wipe effect
     * and key listener.
     *
     * @param gameFrame The main {@link GameFrame} of the game.
     */
    public WelcomeScreen(GameFrame gameFrame) {
        super(gameFrame);
        setBackground(Color.BLACK);
        setFocusable(true); // Ensure the screen can receive key events
        screenWipeEffect = new HorizontalScreenWipeEffect(WelcomeScreen.this, 500); // 500ms wipe duration

        // Add a key listener to handle the ENTER key press
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    // Start the screen wipe effect. Once the wipe is complete,
                    // the callback function will set the game screen to GameplayScreen.
                    screenWipeEffect.startEffect(() -> {
                        gameFrame.setScreen(new GameplayScreen(gameFrame));
                    });
                }
            }
        });
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Overrides the {@link AbstractScreen#paintComponent(Graphics)} method to
     * render the welcome screen elements and the screen wipe effect if it's
     * active.
     *
     * @param g The {@link Graphics} object to draw on.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        render(g2d); // Render the static elements of the welcome screen

        // If the screen wipe effect is active, draw it on top
        if (screenWipeEffect != null && screenWipeEffect.isEffectActive()) {
            screenWipeEffect.draw(g2d);
        }
    }

    /**
     * Implements the {@link AbstractScreen#render(Graphics2D)} method to draw
     * the title and the start prompt centered on the screen.
     *
     * @param g2d The {@link Graphics2D} object used for rendering.
     */
    @Override
    public void render(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // Set background color
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Draw the title
        g2d.setColor(Color.WHITE);
        g2d.setFont(TITLE_FONT);
        FontMetrics titleMetrics = g2d.getFontMetrics(TITLE_FONT);
        int titleWidth = titleMetrics.stringWidth(TITLE);
        int titleX = (width - titleWidth) / 2;
        int titleY = height / 2 - titleMetrics.getHeight();
        g2d.drawString(TITLE, titleX, titleY);

        // Draw the start prompt
        g2d.setFont(PROMPT_FONT);
        FontMetrics promptMetrics = g2d.getFontMetrics(PROMPT_FONT);
        int promptWidth = promptMetrics.stringWidth(START_PROMPT);
        int promptX = (width - promptWidth) / 2;
        int promptY = height / 2 + promptMetrics.getAscent() + 20;
        g2d.drawString(START_PROMPT, promptX, promptY);
    }

    /**
     * Implements the {@link AbstractScreen#update()} method to update any
     * dynamic elements on the welcome screen, such as the screen wipe effect.
     */
    @Override
    public void update() {
        // Update the screen wipe effect if it's currently active
        if (screenWipeEffect != null && screenWipeEffect.isEffectActive()) {
            screenWipeEffect.updateEffect();
            repaint(); // Trigger a repaint to show the effect's progress
        }
    }
}

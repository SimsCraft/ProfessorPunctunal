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
 * The WelcomeScreen is the initial screen shown to the player when launching
 * the game. It displays a title and a prompt to start, and transitions to the
 * gameplay screen when the ENTER key is pressed.
 */
public class WelcomeScreen extends AbstractScreen {
    // ----- STATIC VARIABLES -----
    private static final String TITLE = "WELCOME TO PROFESSOR PUNCTUAL!";
    private static final String START_PROMPT = "Press ENTER to Start";
    private static final Font TITLE_FONT = UIConstants.TITLE_FONT;
    private static final Font PROMPT_FONT = UIConstants.BODY_FONT;

    // ----- INSTANCE VARIABLES -----
    private HorizontalScreenWipeEffect screenWipeEffect;

    public WelcomeScreen(GameFrame gameFrame) {
        super(gameFrame);
        setBackground(Color.BLACK);
        setFocusable(true);
        screenWipeEffect = new HorizontalScreenWipeEffect(WelcomeScreen.this, 500);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    screenWipeEffect.startEffect(() -> {
                        gameFrame.setScreen(new GameplayScreen(gameFrame));
                    });
                }
            }
        });
    }

    // ----- OVERRIDDEN METHODS -----
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        render(g2d);

        if (screenWipeEffect != null && screenWipeEffect.isEffectActive()) {
            screenWipeEffect.draw(g2d);
        }
    }

    @Override
    public void render(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE);
        g2d.setFont(TITLE_FONT);
        FontMetrics titleMetrics = g2d.getFontMetrics(TITLE_FONT);
        int titleWidth = titleMetrics.stringWidth(TITLE);
        int titleX = (width - titleWidth) / 2;
        int titleY = height / 2 - titleMetrics.getHeight();
        g2d.drawString(TITLE, titleX, titleY);
        g2d.setFont(PROMPT_FONT);
        FontMetrics promptMetrics = g2d.getFontMetrics(PROMPT_FONT);
        int promptWidth = promptMetrics.stringWidth(START_PROMPT);
        int promptX = (width - promptWidth) / 2;
        int promptY = height / 2 + promptMetrics.getAscent() + 20;
        g2d.drawString(START_PROMPT, promptX, promptY);
    }

    @Override
    public void update() {
        if (screenWipeEffect != null && screenWipeEffect.isEffectActive()) {
            screenWipeEffect.updateEffect();
        }
    }
}

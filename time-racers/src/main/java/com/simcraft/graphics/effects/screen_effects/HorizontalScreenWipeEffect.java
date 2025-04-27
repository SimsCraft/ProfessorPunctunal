package com.simcraft.graphics.effects.screen_effects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import com.simcraft.graphics.effects.TimedVisualEffect;

public class HorizontalScreenWipeEffect extends TimedVisualEffect {

    private final JPanel panel;
    private BufferedImage currentScreenImage;
    private Runnable onWipeComplete;

    public HorizontalScreenWipeEffect(JPanel panel, long durationMillis) {
        super(durationMillis);
        this.panel = panel;
    }

    public void startEffect(Runnable onComplete) {
        if (!isEffectActive() && panel.getWidth() > 0 && panel.getHeight() > 0) {
            super.startEffect();
            this.currentScreenImage = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = this.currentScreenImage.createGraphics();
            this.panel.paintAll(g2d);
            g2d.dispose();
            this.onWipeComplete = onComplete;
        }
    }

    @Override
    protected void updateTimedEffect(long elapsedTime) {
        // No specific timed updates needed for controlling the wipe directly in draw
    }

    @Override
    protected void onEffectEnd() {
        this.currentScreenImage = null;
        if (this.onWipeComplete != null) {
            this.onWipeComplete.run();
        }
    }

    @Override
    public void draw(Graphics2D g2d) {
        if (isEffectActive()) {
            int width = panel.getWidth();
            int height = panel.getHeight();
            long elapsedTime = System.currentTimeMillis() - startTime;
            double progress = (double) elapsedTime / durationMillis;
            int wipePosition = (int) (width * progress);

            // Draw a black rectangle that expands to the right
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, wipePosition, height);

            // The area to the right of the wipePosition will be the underlying
            // WelcomeScreen (as it was before the wipe started) until it's fully
            // covered by black. The transition to GameplayScreen happens at the end.
        }
    }
}

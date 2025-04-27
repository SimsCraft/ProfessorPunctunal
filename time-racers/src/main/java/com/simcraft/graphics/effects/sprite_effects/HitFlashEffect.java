package com.simcraft.graphics.effects.sprite_effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.simcraft.entities.Entity;
import com.simcraft.graphics.effects.TimedVisualEffect;
import com.simcraft.managers.ImageManager;

/**
 * A visual effect that applies a brief white flash to an entity's sprite when
 * it is hit. The flash effect brightens the sprite for a short duration and
 * then reverts it to its original appearance.
 */
public class HitFlashEffect extends TimedVisualEffect {

    // ----- STATIC VARIABLES -----
    /**
     * The amount by which the brightness of the sprite is increased during the
     * flash.
     */
    private static final int BRIGHTNESS_INCREASE = 150;

    // ----- INSTANCE VARIABLES
    /**
     * The entity whose sprite this flash effect is applied to.
     */
    private final Entity entity;

    /**
     * A copy of the entity's original sprite before the flash is applied, used
     * to revert the sprite after the flash duration.
     */
    private BufferedImage originalSprite = null;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a HitFlashFX for a specific entity with a given flash
     * duration.
     *
     * @param entity The entity to apply the flash effect to.
     * @param durationMillis The duration of the flash effect in milliseconds.
     */
    public HitFlashEffect(Entity entity, long durationMillis) {
        super(durationMillis);
        this.entity = entity;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Called when the hit flash effect starts. It captures the entity's current
     * sprite and applies the white flash by increasing the RGB values of its
     * pixels.
     */
    @Override
    protected void onStartEffect() {
        if (entity.getCurrentSprite() != null) {
            originalSprite = ImageManager.copyImage(entity.getCurrentSprite());
            applyWhiteFlash(BRIGHTNESS_INCREASE);
        }
    }

    /**
     * This method is called during each update cycle of the effect. For a
     * simple hit flash, no continuous updating of the sprite is needed; the
     * change is applied at the start and reverted at the end.
     *
     * @param elapsedTime The time elapsed since the effect started in
     * milliseconds.
     */
    @Override
    protected void updateTimedEffect(long elapsedTime) {
        // No continuous update needed for a simple flash
    }

    /**
     * Called when the hit flash effect has completed its duration. It reverts
     * the entity's sprite back to its original appearance.
     */
    @Override
    protected void onEffectEnd() {
        revertFlash();
    }

    /**
     * This method is called when the entity is rendered. The flash effect is
     * applied directly to the entity's current sprite, so this effect doesn't
     * need to draw anything additional.
     *
     * @param g2d The Graphics2D context to draw on (not directly used by this
     * effect).
     */
    @Override
    public void draw(Graphics2D g2d) {
        // The flash is handled by directly modifying the entity's sprite.
    }

    // ----- HELPER METHODS -----
    /**
     * Increases the red, green, and blue color components of the sprite's
     * pixels to create a white flash effect. The alpha component is preserved.
     *
     * @param increase The amount by which to increase the RGB values (clamped
     * at 255).
     */
    private void applyWhiteFlash(int increase) {
        BufferedImage currentSprite = entity.getCurrentSprite();
        if (currentSprite != null) {
            int width = currentSprite.getWidth();
            int height = currentSprite.getHeight();
            int[] pixels = new int[width * height];
            currentSprite.getRGB(0, 0, width, height, pixels, 0, width);

            for (int i = 0; i < pixels.length; i++) {
                int alpha = (pixels[i] >> 24) & 0xFF;
                int red = Math.min(255, (pixels[i] >> 16) & 0xFF + increase);
                int green = Math.min(255, (pixels[i] >> 8) & 0xFF + increase);
                int blue = Math.min(255, pixels[i] & 0xFF + increase);
                pixels[i] = (alpha << 24) | (red << 16) | (green << 8) | blue;
            }
            currentSprite.setRGB(0, 0, width, height, pixels, 0, width);
        }
    }

    /**
     * Reverts the entity's sprite back to its original state before the flash
     * was applied, using the captured originalSprite.
     */
    private void revertFlash() {
        if (originalSprite != null && entity.getCurrentSprite() != null
                && originalSprite.getWidth() == entity.getCurrentSprite().getWidth()
                && originalSprite.getHeight() == entity.getCurrentSprite().getHeight()) {
            entity.getCurrentSprite().setData(originalSprite.getData());
        }
        originalSprite = null;
    }

}

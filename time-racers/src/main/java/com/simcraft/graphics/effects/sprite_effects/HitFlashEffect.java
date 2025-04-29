package com.simcraft.graphics.effects.sprite_effects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.simcraft.entities.Entity;
import com.simcraft.graphics.effects.TimedVisualEffect;
import com.simcraft.managers.ImageManager;

/**
 * A visual effect that applies a brief white flash to an entity's sprite when
 * it is hit. The flash effect brightens the sprite for a short duration and
 * then reverts it to its original appearance, providing visual feedback to the
 * player.
 */
public class HitFlashEffect extends TimedVisualEffect {

    // ----- STATIC VARIABLES -----
    /**
     * The amount by which the brightness of the sprite's color components (RGB)
     * is increased during the flash. This value is clamped to ensure the color
     * components do not exceed 255.
     */
    private static final int BRIGHTNESS_INCREASE = 150;

    // ----- INSTANCE VARIABLES
    /**
     * The {@link Entity} whose current sprite this flash effect is applied to.
     */
    private final Entity entity;

    /**
     * A copy of the entity's original sprite {@link BufferedImage} before the
     * flash effect is applied. This is used to restore the sprite to its
     * original appearance once the flash duration has ended.
     */
    private BufferedImage originalSprite = null;

    // ----- CONSTRUCTORS -----
    /**
     * Constructs a {@code HitFlashEffect} for a specific {@link Entity} with a
     * given flash duration.
     *
     * @param entity The {@code Entity} to which the flash effect will be
     * applied. Must not be {@code null}.
     * @param durationMillis The duration of the flash effect in milliseconds.
     * Must be a positive value.
     * @throws IllegalArgumentException If the provided entity is {@code null}
     * or the duration is not positive.
     */
    public HitFlashEffect(Entity entity, long durationMillis) {
        super(durationMillis);
        if (entity == null) {
            throw new IllegalArgumentException("Entity cannot be null.");
        }
        if (durationMillis <= 0) {
            throw new IllegalArgumentException("Duration must be positive.");
        }
        this.entity = entity;
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Called when the hit flash effect is started. This method captures the
     * entity's current sprite and then applies the white flash effect by
     * increasing the red, green, and blue color components of each pixel. The
     * alpha component (transparency) is preserved.
     */
    @Override
    protected void onStartEffect() {
        BufferedImage currentSprite = entity.getCurrentSprite();
        if (currentSprite != null) {
            originalSprite = ImageManager.copyImage(currentSprite); // Capture the original sprite
            applyWhiteFlash(BRIGHTNESS_INCREASE); // Apply the flash effect
        }
    }

    /**
     * This method is called during each update cycle of the effect. For a
     * simple hit flash, the visual change is applied at the start and reverted
     * at the end, so no continuous updating of the sprite is needed based on
     * the elapsed time.
     *
     * @param elapsedTime The time elapsed in milliseconds since the effect
     * started.
     */
    @Override
    protected void updateTimedEffect(long elapsedTime) {
        // No continuous update needed for this simple flash effect
    }

    /**
     * Called when the hit flash effect has completed its specified duration.
     * This method reverts the entity's sprite back to the original sprite that
     * was captured at the start of the effect.
     */
    @Override
    protected void onEffectEnd() {
        revertFlash(); // Restore the original sprite
    }

    /**
     * This method is called when the entity associated with this effect is
     * being rendered. The flash effect is applied directly to the entity's
     * current sprite in the {@link #onStartEffect()} method, so this draw
     * method does not need to perform any additional drawing. The entity's
     * sprite, which is modified by the flash, will be drawn as usual by the
     * entity's rendering logic.
     *
     * @param g2d The {@link Graphics2D} context used for drawing (not directly
     * used by this effect's draw method).
     */
    @Override
    public void draw(Graphics2D g2d) {
        // The flash is handled by directly modifying the entity's sprite.
    }

    // ----- HELPER METHODS -----
    /**
     * Increases the red, green, and blue color components of each pixel in the
     * entity's current sprite by a specified amount, creating a white flash
     * effect. The alpha (transparency) of the pixels is preserved. The color
     * components are clamped to a maximum value of 255.
     *
     * @param increase The amount by which to increase the RGB values of each
     * pixel.
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
     * Reverts the entity's current sprite back to the original sprite that was
     * captured before the flash effect was applied. This ensures that the
     * entity's appearance is restored to normal after the flash duration. The
     * reversion only occurs if an original sprite was successfully captured and
     * if its dimensions match the current sprite's dimensions to avoid
     * potential errors.
     */
    private void revertFlash() {
        BufferedImage currentSprite = entity.getCurrentSprite();
        if (originalSprite != null && currentSprite != null
                && originalSprite.getWidth() == currentSprite.getWidth()
                && originalSprite.getHeight() == currentSprite.getHeight()) {
            currentSprite.setData(originalSprite.getData()); // Restore pixel data from the original sprite
        }
        originalSprite = null; // Release the reference to the original sprite
    }
}

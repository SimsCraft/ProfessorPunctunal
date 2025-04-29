package com.simcraft.entities;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.swing.JPanel;

import com.simcraft.graphics.animations.AnimationInstance;
import com.simcraft.graphics.animations.AnimationTemplate;
import com.simcraft.interfaces.Renderable;
import com.simcraft.interfaces.Updateable;
import com.simcraft.managers.AnimationManager;

/**
 * Represents a base entity in the game, providing common functionality for all
 * objects within the game world. This class handles entity position, animation,
 * collision detection, hit points, and rendering.
 * <p>
 * The entity can be fully customized through the builder pattern, supporting
 * features such as animations, speed, invisibility, and collision handling.
 * <p>
 * Entities may interact with other entities and are capable of rendering
 * themselves within a game panel.
 */
public abstract class Entity implements Updateable, Renderable {

    // ----- INSTANCE VARIABLES -----
    /**
     * The {@link JPanel} where the entity will be rendered.
     */
    protected final JPanel panel;

    /**
     * The position of the entity in the game world.
     */
    protected Point position;

    /**
     * A set of all the animation keys (names) associated with the entity.
     */
    protected HashSet<String> animationKeys;

    /**
     * The key of the currently active animation.
     */
    protected String currentAnimationKey;

    /**
     * The currently active animation. Set through a query to
     * {@link AnimationManager}.
     */
    protected AnimationInstance currentAnimation;

    /**
     * The hitbox used for collision detection.
     */
    protected Rectangle hitbox;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor used by the builder pattern to instantiate an Entity.
     *
     * @param builder The builder instance used to construct the entity.
     * @throws IllegalArgumentException If the builder is null.
     */
    protected Entity(EntityBuilder<?> builder) throws IllegalArgumentException {
        if (builder == null) {
            throw new IllegalArgumentException(String.format(
                    "%s: Builder cannot be null.",
                    this.getClass().getName()
            ));
        }

        this.panel = builder.panel;
        this.position = builder.position;
        this.animationKeys = builder.animationKeys;
        this.hitbox = builder.hitbox;

        // Set initial animation and hitbox
        setAnimation(builder.currentAnimationKey);
        setHitboxFromCurrentSprite();
    }

    /**
     * Constructor for creating an entity at a specific (x, y) coordinate. This
     * constructor does not require a builder and can be used for simple entity
     * creation. Note that the panel association and animations might need to be
     * configured separately.
     *
     * @param x The initial x-coordinate of the entity.
     * @param y The initial y-coordinate of the entity.
     */
    protected Entity(int x, int y) {
        this.panel = null; // No specific panel assigned (optional, fix if needed)
        this.position = new Point(x, y);
        this.animationKeys = new HashSet<>();
        this.hitbox = new Rectangle(x, y, 0, 0); // We'll assume empty hitbox unless set
    }

    // ----- GETTERS -----
    /**
     * Returns the panel where the entity is rendered.
     *
     * @return The {@link JPanel} for rendering.
     */
    public JPanel getPanel() {
        return panel;
    }

    /**
     * Returns the current position of the entity.
     *
     * @return The position as a {@link Point}.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Returns the X-coordinate of the entity's position.
     *
     * @return The X-coordinate.
     */
    public int getX() {
        return position.x;
    }

    /**
     * Returns the Y-coordinate of the entity's position.
     *
     * @return The Y-coordinate.
     */
    public int getY() {
        return position.y;
    }

    /**
     * Returns the set of keys this entity can use to query
     * {@link AnimationManager} for an {@link AnimationTemplate}.
     *
     * @return The set of animation keys.
     */
    public Set<String> getAnimationKeys() {
        return animationKeys;
    }

    /**
     * Returns the key associated with the current {@link AnimationInstance}.
     *
     * @return The key of the current animation.
     */
    public String getCurrentAnimationKey() {
        return currentAnimationKey;
    }

    /**
     * Returns the entity's current animation.
     *
     * @return The current {@link AnimationInstance}.
     */
    public AnimationInstance getCurrentAnimation() {
        return currentAnimation;
    }

    /**
     * Returns the current sprite image of the entity.
     *
     * @return The current sprite of the {@AnimationInstance} as a
     * {@link BufferedImage} (if one is set); {@code null} otherwise.
     */
    public BufferedImage getCurrentSprite() {
        if (currentAnimation == null) {
            return null;
        }
        return currentAnimation.getCurrentFrameImage();
    }

    /**
     * Returns the width of the current sprite.
     *
     * @return The sprite width.
     */
    public int getSpriteWidth() {
        BufferedImage currentSprite = getCurrentAnimation() != null ? getCurrentAnimation().getCurrentFrameImage() : null;
        return currentSprite != null ? currentSprite.getWidth() : 0;
    }

    /**
     * Returns the height of the current sprite.
     *
     * @return The sprite height.
     */
    public int getSpriteHeight() {
        BufferedImage currentSprite = getCurrentAnimation() != null ? getCurrentAnimation().getCurrentFrameImage() : null;
        return currentSprite != null ? currentSprite.getHeight() : 0;
    }

    /**
     * Returns the hitbox of the entity.
     *
     * @return The hitbox as a {@link Rectangle}.
     */
    public Rectangle getHitbox() {
        return hitbox;
    }

    // ----- SETTERS -----
    /**
     * Sets the X-coordinate of the entity's position.
     *
     * @param x The X-coordinate.
     */
    public void setX(final int x) {
        position.x = x;
    }

    /**
     * Sets the Y-coordinate of the entity's position.
     *
     * @param y The Y-coordinate.
     */
    public void setY(final int y) {
        position.y = y;
    }

    /**
     * Sets the position of the entity.
     *
     * @param position The new position to set.
     */
    public void setPosition(Point position) {
        this.position = new Point(position);
    }

    /**
     * Sets the set of keys this entity can query {@link AnimationManager} with.
     *
     * @param animationKeys The set of animation keys.
     * @throws IllegalArgumentException if, when a non-{@code null} set is
     * passed, it contains {@code null} or blank keys, or contains a key that
     * doesn't exist in {@link AnimationManager}'s key set.
     */
    public final void setAnimationKeys(final Set<String> animationKeys) throws IllegalArgumentException {
        if (animationKeys == null) {
            this.animationKeys = new HashSet<>();
            return;
        }

        for (String key : animationKeys) {
            if (key == null || key.isBlank()) {
                throw new IllegalArgumentException(String.format(
                        "%s: Keys cannot be null nor empty.",
                        this.getClass().getName()
                ));
            }
            if (AnimationManager.getInstance().getAnimation(key) == null) {
                throw new IllegalArgumentException(String.format(
                        "%s: Key '%s' does not exist within AnimationManager's key set.",
                        this.getClass().getName(),
                        key
                ));
            }
        }

        this.animationKeys = new HashSet<>(animationKeys);
    }

    /**
     * Sets the animation for the entity.
     * <p>
     * Key must either be {@code null} (for no animation) or a valid (non-blank)
     * string.
     * <p>
     * Valid strings must be a {@link AnimationTemplate} key within
     * {@link AnimationManager} and must exist within the entity's current key
     * set.
     *
     * @param key The key identifying the animation.
     * @throws IllegalArgumentException if the provided key (when not
     * {@code null}) is blank, is not within the entity's animation key set, or
     * does not map to a loaded template within {@link AnimationManager}.
     */
    public final void setAnimation(final String key) throws IllegalArgumentException {
        if (key == null) {
            currentAnimation = null;
            currentAnimationKey = null;
            return;
        }

        if (key.isBlank()) {
            throw new IllegalArgumentException(String.format(
                    "%s: Non-null keys cannot be blank.",
                    this.getClass().getName()
            ));
        }

        if (!animationKeys.contains(key)) {
            throw new IllegalArgumentException(String.format(
                    "%s: Animation key <'%s'> not found within key set. Please add key to set.",
                    this.getClass().getName(),
                    key
            ));
        }

        AnimationTemplate template = AnimationManager.getInstance().getAnimation(key);
        if (template == null) {
            throw new IllegalArgumentException(String.format(
                    "%s: Could not find template in AnimationManager mapped to key <'%s'>.",
                    this.getClass().getName(),
                    key
            ));
        }
        this.currentAnimation = new AnimationInstance(template);
        this.currentAnimationKey = key;
        currentAnimation.start();
    }

    /**
     * Updates the hitbox dimensions based on the current sprite. The hitbox is
     * adjusted to match the width and height of the sprite.
     */
    public final void setHitboxFromCurrentSprite() {
        BufferedImage currentSprite = getCurrentSprite();

        int width = currentSprite != null ? currentSprite.getWidth() : 0;
        int height = currentSprite != null ? currentSprite.getHeight() : 0;

        hitbox = new Rectangle(position.x, position.y, width, height);
    }

    /**
     * Updates the hitbox dimensions based on the provided {@link Rectangle}.
     *
     * @param rectangle The new hitbox dimensions.
     */
    public final void setHitboxFromRectangle(final Rectangle rectangle) {
        hitbox = new Rectangle(rectangle);
    }

    // ----- BUSINESS LOGIC METHODS -----
    /**
     * Determines if the entity is fully within the bounds of the panel.
     *
     * @return {@code true} if the entity is fully within the panel,
     * {@code false} otherwise.
     */
    public boolean isFullyWithinPanel() {
        return position.x >= 0 && position.y >= 0
                && position.x + getSpriteWidth() <= panel.getWidth()
                && position.y + getSpriteHeight() <= panel.getHeight();
    }

    /**
     * Determines if the entity is fully outside the bounds of the panel.
     *
     * @return {@code true} if the entity is fully outside the panel,
     * {@code false} otherwise.
     */
    public boolean isFullyOutsidePanel() {
        Rectangle spriteBounds = new Rectangle(position.x, position.y, getSpriteWidth(), getSpriteHeight());
        Rectangle panelBounds = new Rectangle(0, 0, panel.getWidth(), panel.getHeight());
        return !spriteBounds.intersects(panelBounds);
    }

    /**
     * Checks if the entity's hitbox intersects with a given rectangular space.
     *
     * @param rectangle The {@link Rectangle} to check for intersection.
     * @return {@code true} if the entity collides with the rectangle,
     * {@code false} otherwise.
     */
    public boolean collides(final Rectangle rectangle) {
        return hitbox != null && rectangle != null && hitbox.intersects(rectangle);
    }

    /**
     * Checks if the entity's hitbox intersects with another entity's hitbox.
     *
     * @param entity The other entity to check for collision.
     * @return {@code true} if the two entities collide, {@code false}
     * otherwise.
     */
    public boolean collides(final Entity entity) {
        return collides(entity.getHitbox());
    }

    /**
     * Adds a new key to the set of keys this entity can query
     * {@link AnimationManager} with.
     *
     * @param key The new animation key.
     * @throws IllegalArgumentException if a {@code null} or blank key is
     * passed, or the key doesn't exist in {@link AnimationManager}'s key set.
     */
    public void addAnimationKey(final String key) throws IllegalArgumentException {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(String.format(
                    "%s: Keys cannot be null nor empty.",
                    this.getClass().getName()
            ));
        }
        if (AnimationManager.getInstance().getAnimation(key) == null) {
            throw new IllegalArgumentException(String.format(
                    "%s: Keys must exist within AnimationManager's key set.",
                    this.getClass().getName()
            ));
        }
        animationKeys.add(key);
    }

    /**
     * Returns the central coordinates of the entity's sprite.
     *
     * @return A {@link Point} representing the center of the sprite.
     */
    public Point getCentreCoordinates() {
        return new Point(
                position.x + getSpriteWidth() / 2,
                position.y + getSpriteHeight() / 2
        );
    }

    // ----- OVERRIDDEN METHODS -----
    /**
     * Compares this entity to another object for equality.
     *
     * @param obj The {@link Object} to compare with.
     * @return {@code true} if the objects are equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Entity)) {
            return false;
        }
        Entity other = (Entity) obj;
        return Objects.equals(panel, other.getPanel())
                && Objects.equals(position, other.getPosition())
                && Objects.equals(animationKeys, other.getAnimationKeys())
                && Objects.equals(currentAnimationKey, other.getCurrentAnimationKey())
                && Objects.equals(hitbox, other.getHitbox());
    }

    /**
     * Returns a hash code for this entity.
     *
     * @return The hash code of the entity.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                panel,
                position,
                animationKeys,
                currentAnimationKey,
                hitbox
        );
    }

    /**
     * Updates the entity's state, primarily by updating its current animation
     * frame and adjusting the hitbox to match the current sprite.
     */
    @Override
    public void update() {
        if (currentAnimation != null) {
            currentAnimation.update();
            setHitboxFromCurrentSprite();
        }
    }

    /**
     * Renders the entity on the provided graphics context. Draws the current
     * sprite at the entity's position.
     *
     * @param g2d The graphics context to draw on.
     */
    @Override
    public void render(final Graphics2D g2d) {
        BufferedImage currentSprite = getCurrentSprite();

        if (currentSprite != null) {
            g2d.drawImage(currentSprite, position.x, position.y, getSpriteWidth(), getSpriteHeight(), null);
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Ensures the entity's position remains within the visible screen
     * boundaries. If the entity goes outside the bounds, its position is
     * adjusted to the nearest valid position at the edge of the screen.
     */
    protected void correctPosition() {
        if (panel != null) {
            position.x = Math.max(0, Math.min(position.x, panel.getWidth() - getSpriteWidth()));
            position.y = Math.max(0, Math.min(position.y, panel.getHeight() - getSpriteHeight()));
        }
    }

    // ----- BUILDER PATTERN -----
    /**
     * The EntityBuilder class provides a fluent API for constructing an Entity
     * object. Subclasses of {@link Entity} should extend this builder to
     * include their specific properties.
     *
     * @param <T> The specific type of the builder, allowing for method chaining
     * in subclasses.
     */
    public static class EntityBuilder<T extends EntityBuilder<T>> {

        private JPanel panel = null;
        private Point position = new Point(0, 0);
        private HashSet<String> animationKeys = new HashSet<>();
        private String currentAnimationKey = null;
        private Rectangle hitbox = new Rectangle(0, 0, 0, 0);

        /**
         * Creates a EntityBuilder for constructing an Entity.
         *
         * @param panel The {@link JPanel} where the entity will be rendered.
         * @throws IllegalArgumentException If the panel is null.
         */
        public EntityBuilder(final JPanel panel) throws IllegalArgumentException {
            if (panel == null) {
                throw new IllegalArgumentException("Panel cannot be null.");
            }
            this.panel = panel;
        }

        /**
         * Sets the initial position of the entity.
         *
         * @param position The position to set.
         * @return The builder instance.
         */
        public T position(final Point position) {
            this.position = new Point(position);
            return self();
        }

        /**
         * Sets the initial position of the entity.
         *
         * @param x The initial x-coordinate.
         * @param y The initial y-coordinate.
         * @return The builder instance.
         */
        public T position(final int x, final int y) {
            this.position = new Point(x, y);
            return self();
        }

        /**
         * Sets the set of keys this entity can use to query
         * {@link AnimationManager}.
         *
         * @param animationKeys The set of animation keys.
         * @return The builder instance.
         */
        public T animationKeys(final Set<String> animationKeys) {
            this.animationKeys = (animationKeys == null) ? new HashSet<>() : new HashSet<>(animationKeys);
            return self();
        }

        /**
         * Sets the key for the initial animation of the entity. This key must
         * be present in the set provided by {@link #animationKeys(Set)}.
         *
         * @param currentAnimationKey The key identifying the initial animation.
         * @return The builder instance.
         */
        public T currentAnimationKey(final String currentAnimationKey) {
            this.currentAnimationKey = currentAnimationKey;
            return self();
        }

        /**
         * Sets the initial hitbox for the entity.
         *
         * @param hitbox The hitbox to set.
         * @return The builder instance.
         */
        public T hitbox(final Rectangle hitbox) {
            this.hitbox = hitbox == null ? new Rectangle() : new Rectangle(hitbox);
            return self();
        }

        /**
         * Sets the initial hitbox for the entity.
         *
         * @param x The x-coordinate of the hitbox.
         * @param y The y-coordinate of the hitbox.
         * @param width The width of the hitbox.
         * @param height The height of the hitbox.
         * @return The builder instance.
         */
        public T hitbox(final int x, final int y, final int width, final int height) {
            this.hitbox = new Rectangle(x, y, width, height);
            return self();
        }

        /**
         * Helper method to cast the builder to its specific type, enabling
         * method chaining in subclasses.
         *
         * @return The builder instance cast to its specific type.
         */
        @SuppressWarnings("unchecked")
        protected T self() {
            return (T) this;
        }
    }
}

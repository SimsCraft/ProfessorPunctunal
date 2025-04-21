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

    /**
     * Flag indicating whether the entity can collide with other entities.
     */
    protected boolean isCollidable;

    /**
     * The maximum hit points of the entity.
     */
    protected int maxHitPoints;

    /**
     * The current hit points of the entity.
     */
    protected int currentHitPoints;

    // ----- CONSTRUCTORS -----
    /**
     * Private constructor used by the builder pattern to instantiate an Entity.
     *
     * @param builder The builder instance used to construct the entity.
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
        this.isCollidable = builder.isCollidable;
        setMaxHitPoints(builder.maxHitPoints);
        setCurrentHitPoints(builder.currentHitPoints);

        // Set initial animation and hitbox
        setAnimation(builder.currentAnimationKey);
        setHitboxFromCurrentSprite();
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
     * @return
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
        BufferedImage currentSprite = currentAnimation.getCurrentFrameImage();
        return currentSprite != null ? currentSprite.getWidth() : 0;
    }

    /**
     * Returns the height of the current sprite.
     *
     * @return The sprite height.
     */
    public int getSpriteHeight() {
        BufferedImage currentSprite = currentAnimation.getCurrentFrameImage();
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

    /**
     * Returns whether the entity is collidable with others.
     *
     * @return {@code true} if the entity is collidable, {@code false}
     * otherwise.
     */
    public boolean isCollidable() {
        return isCollidable;
    }

    /**
     * Returns the maximum hit points of the entity.
     *
     * @return The maximum hit points.
     */
    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    /**
     * Returns the current hit points of the entity.
     *
     * @return The current hit points.
     */
    public int getCurrentHitPoints() {
        return currentHitPoints;
    }

    // ----- SETTERS -----
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
                        "%s: Keys must exist within AnimationManager's key set.",
                        this.getClass().getName()
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
     * @throws IllegalAccessException if the provided key (when not
     * {@code null}) is blank, is not within the entity's animation key set, or
     * does not map to a loaded template within {@link AnimationManager}.
     */
    public final void setAnimation(final String key) throws IllegalArgumentException {
        if (key == null) {
            currentAnimation = null;
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
     */
    public final void setHitboxFromRectangle(final Rectangle rectangle) {
        hitbox = new Rectangle(rectangle);
    }

    /**
     * Sets the maximum hit points of the entity. Requires a non-negative value.
     */
    protected final void setMaxHitPoints(final int maxHitPoints) throws IllegalArgumentException {
        if (maxHitPoints < 0) {
            throw new IllegalArgumentException(String.format(
                    "%s: Maximum hit points cannot be less than zero (0).",
                    this.getClass().getName()
            ));
        }
        this.maxHitPoints = maxHitPoints;
    }

    /**
     * Sets the current hit points of the entity. Requires a non-negative value
     * less than or equal to the maximum value.
     */
    protected final void setCurrentHitPoints(final int currentHitPoints) throws IllegalArgumentException {
        if (currentHitPoints < 0) {
            throw new IllegalArgumentException(String.format(
                    "%s: Current hit points cannot be less than zero (0).",
                    this.getClass().getName()
            ));
        }

        if (currentHitPoints > maxHitPoints) {
            throw new IllegalArgumentException(String.format(
                    "%s: Current hit points cannot be greater than maximum hit points <%d>.",
                    this.getClass().getName(),
                    maxHitPoints
            ));
        }
        this.currentHitPoints = currentHitPoints;
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
     * Checks if the entity collides with a given rectanglular space.
     *
     * @param rectangle The {@link rectangle} to check for intersection.
     * @return {@code true} if the entity collides with the rectangle,
     * {@code false} otherwise.
     */
    public boolean collides(final Rectangle rectangle) {
        return isCollidable && hitbox != null && rectangle != null && hitbox.intersects(rectangle);
    }

    /**
     * Checks if the entity collides with another entity.
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
     */
    public Point getCentreCoordinates() {
        return new Point(
                (position.x + getSpriteWidth()) / 2,
                (position.y + getSpriteHeight()) / 2
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
                && Objects.equals(hitbox, other.getHitbox())
                && isCollidable == other.isCollidable()
                && maxHitPoints == other.getMaxHitPoints()
                && currentHitPoints == other.getCurrentHitPoints();
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
                hitbox,
                isCollidable,
                maxHitPoints,
                currentHitPoints
        );
    }

    /**
     * Updates the entity's state.
     */
    @Override
    public void update() {
        if (currentAnimation != null) {
            currentAnimation.update();
            setHitboxFromCurrentSprite();
        }
    }

    /**
     * Renders the entity on the provided graphics context.
     *
     * @param g2d The graphics context to draw on.
     */
    @Override
    public void render(final Graphics2D g2d) {
        BufferedImage currentSprite = currentAnimation.getCurrentFrameImage();

        if (currentSprite != null) {
            g2d.drawImage(currentSprite, position.x, position.y, getSpriteWidth(), getSpriteHeight(), null);
        }
    }

    // ----- HELPER METHODS -----
    /**
     * Ensures the entity remains within the visible screen boundaries.
     */
    protected void correctPosition() {
        // Trying to use Math.clamp gave out of bounds issues or something. This is simpler.
        position.x = Math.max(0, Math.min(position.x, panel.getWidth() - getSpriteWidth()));
        position.y = Math.max(0, Math.min(position.y, panel.getHeight() - getSpriteHeight()));
    }

    // ----- BUILDER PATTERN -----
    /**
     * The EntityBuilder class provides a fluent API for constructing an Entity
     * object.
     */
    public static class EntityBuilder<T extends EntityBuilder<T>> {

        private JPanel panel = null;
        private Point position = new Point(0, 0);
        private HashSet<String> animationKeys = new HashSet<>();
        private String currentAnimationKey = null;
        private Rectangle hitbox = new Rectangle(0, 0, 0, 0);
        private boolean isCollidable = false;
        private int maxHitPoints = 0;
        private int currentHitPoints = 0;

        /**
         * Creates a EntityBuilder for constructing an Entity.
         *
         * @param panel The {@link JPanel} where the entity will be rendered.
         */
        public EntityBuilder(final JPanel panel) throws IllegalArgumentException {
            if (panel == null) {
                throw new IllegalArgumentException("Panel cannot be null.");
            }
            this.panel = panel;
        }

        /**
         * Sets the position of the entity.
         *
         * @param position The position to set.
         * @return The builder instance.
         */
        public T position(final Point position) {
            this.position = position;
            return self();
        }

        /**
         * Sets the set of keys this entity can query {@link AnimationManager}
         * with.
         *
         * @param animationKeys The set of animation keys.
         * @return The builder instance.
         */
        public T animationKeys(final Set<String> animationKeys) {
            this.animationKeys = (animationKeys == null) ? new HashSet<>() : new HashSet<>(animationKeys);
            return self();
        }

        /**
         * Sets the animation for the entity. Must set a collection of available
         * keys using animationKeys() first.
         *
         * @param currentAnimationKey The key identifying the current animation.
         * @return The builder instance.
         */
        public T currentAnimationKey(final String currentAnimationKey) {
            this.currentAnimationKey = currentAnimationKey;
            return self();
        }

        /**
         * Sets the collidability flag for the entity.
         *
         * @param isCollidable The collidability flag.
         * @return The builder instance.
         */
        public T collidability(final boolean isCollidable) {
            this.isCollidable = isCollidable;
            return self();
        }

        /**
         * Sets the maximum hit points for the entity.
         *
         * @param maxHitPoints The maximum hit points.
         * @return The builder instance.
         */
        public T maxHitPoints(final int maxHitPoints) {
            this.maxHitPoints = maxHitPoints;
            return self();
        }

        /**
         * Sets the current hit points for the entity.
         *
         * @param currentHitPoints The current hit points.
         * @return The builder instance.
         */
        public T currentHitPoints(final int currentHitPoints) {
            this.currentHitPoints = currentHitPoints;
            return self();
        }

        /**
         * Sets the hitbox for the entity.
         *
         * @param hitbox The hitbox to set.
         * @return The builder instance.
         */
        public T hitbox(final Rectangle hitbox) {
            this.hitbox = hitbox;
            return self();
        }

        // ----- HELPER METHODS -----
        @SuppressWarnings("unchecked")
        protected T self() {
            // cast to the generic type, ensures correct builder is returned
            return (T) this;
        }

    }
}

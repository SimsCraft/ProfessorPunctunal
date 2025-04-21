package com.simcraft.graphics;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.swing.WindowConstants;

import static com.simcraft.App.FRAME_RATE_MS;
import com.simcraft.graphics.animations.AnimationLoader;
import com.simcraft.graphics.screens.Screen;
import com.simcraft.graphics.screens.WelcomeScreen;

/**
 * GameFrame serves as the primary window and core rendering engine for the
 * game.
 *
 * This class extends {@link JFrame} and is responsible for:
 * <ul>
 * <li>Hosting and displaying all in-game screens, such as menus and gameplay
 * panels</li>
 * <li>Managing the game loop via a {@link Timer}, which handles updates and
 * rendering</li>
 * <li>Implementing double buffering with a {@link BufferedImage} back buffer
 * for smooth graphics</li>
 * <li>Dynamically swapping active screens through the {@code setScreen()}
 * method</li>
 * </ul>
 *
 * GameFrame initializes by loading default animations and presenting the
 * {@link WelcomeScreen}. It provides methods to update game logic, render
 * frames, and stop the game loop when needed.
 *
 * This class is the central hub of the game's visual and logical flow, and
 * should be instantiated once at the application's entry point.
 *
 * @see Screen
 * @see com.simcraft.graphics.screens.WelcomeScreen
 * @see AnimationLoader
 */
public final class GameFrame extends JFrame {

    // ----- STATIC VARIABLES -----
    /**
     * The fixed width of the application window in pixels.
     */
    public static final int FRAME_WIDTH = 800;
    /**
     * The fixed height of the application window in pixels.
     */
    public static final int FRAME_HEIGHT = 650;

    // ----- INSTANCE VARIABLES -----
    /**
     * The timer that drives the game loop, triggering updates and rendering at
     * a fixed interval.
     */
    private final Timer gameLoopTimer;

    /**
     * The off-screen image used for double buffering to reduce flickering
     * during rendering. All graphics are first drawn to this back buffer, then
     * painted onto the screen.
     */
    private final transient BufferedImage backBuffer;

    /**
     * The {@link Graphics2D} context used to draw onto the back buffer.
     * Reacquired each frame to ensure it references the current back buffer
     * state.
     */
    private transient Graphics2D g2d;

    /**
     * The currently active screen (e.g. menu, game, pause) being displayed and
     * updated. Swapped dynamically using the {@code setScreen()} method.
     */
    private Screen currentScreen;

    // ----- CONSTRUCTORS -----
    /**
     * Constructor to initialize the game frame, set the size, title, and add
     * the main menu and gameplay panels. Also initializes the GameManager
     * instance and sets the blaster and bubble panels.
     */
    public GameFrame() {
        // ----- WINDOW SETUP ----- 
        setTitle("Professor Punctual");
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);  // Center the window on screen
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        // ----- GAME INITIALIZATION ----- 
        AnimationLoader.loadDefaultAnimations();
        setScreen(new WelcomeScreen(this));

        // ----- RENDERING SETUP -----
        // Initialize double buffering
        backBuffer = new BufferedImage(FRAME_WIDTH, FRAME_HEIGHT, BufferedImage.TYPE_INT_ARGB);
        g2d = backBuffer.createGraphics();

        // ----- GAME LOOP ----- 
        gameLoopTimer = new Timer((int) FRAME_RATE_MS, e -> {
            updateGame();
            renderGame();
            currentScreen.repaint();
        });
        gameLoopTimer.start();

        // ----- MAKE VISIBLE (FINAL) ----- 
        setVisible(true);
    }

    /**
     * Paints the back buffer onto the JFrame.
     */
    @Override
    public void paint(java.awt.Graphics g) {
        super.paint(g);
        if (backBuffer != null) {
            g.drawImage(backBuffer, 0, 0, this);
        }
    }

    /**
     * Dynamically switches to a new screen, removing the old one to free up
     * memory.
     *
     * @param newScreen The new screen to display.
     */
    public void setScreen(final Screen newScreen) {
        if (currentScreen != null) {
            remove(currentScreen);
            currentScreen.cleanup();
            currentScreen = null;
        }

        currentScreen = newScreen;
        add(currentScreen);

        currentScreen.revalidate();
        currentScreen.repaint();
        currentScreen.setFocusable(true);
        currentScreen.requestFocusInWindow();
    }

    /**
     * Updates the game logic.
     */
    public void updateGame() {
        if (currentScreen != null) {
            currentScreen.update();
        }
    }

    /**
     * Renders the game onto the back buffer.
     */
    private void renderGame() {
        if (backBuffer == null || currentScreen == null) {
            return;
        }

        g2d = backBuffer.createGraphics();
        g2d.clearRect(0, 0, FRAME_WIDTH, FRAME_HEIGHT);
        currentScreen.safeRender(g2d);
    }

    /**
     * Stops game loop.
     */
    public void stopGameLoop() {
        gameLoopTimer.stop();
    }
}

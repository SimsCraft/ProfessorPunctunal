/**
 * NOTE FOR GRADING AND FUTURE REFLECTION:
 * This class fulfills the role of `GameWindow.java` from the original lecture code.
 * All responsibilities for game window setup, loop management, and game screen 
 * control are now handled here (as part of a more modular design).
 */



package com.simcraft.graphics;

import com.simcraft.graphics.screens.WelcomeScreen;
import com.simcraft.graphics.screens.AbstractScreen;
import com.simcraft.graphics.animations.AnimationLoader;

import static com.simcraft.App.FRAME_RATE_MS;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;

import javax.swing.Timer;
import javax.swing.JFrame;
import javax.swing.WindowConstants;





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
 * @see AbstractScreen
 * @see com.simcraft.graphics.screens.WelcomeScreen
 * @see AnimationLoader
 */

public final class GameFrame extends JFrame {

    // ----- STATIC VARIABLES -----
    //                                                    | The fixed width and height of the application window in pixels.
    public static final int FRAME_WIDTH = 800;
    public static final int FRAME_HEIGHT = 650;

    // ----- INSTANCE VARIABLES -----
    private Font arcadeFont;
    private final Timer gameLoopTimer;//                  | Timer that drives the game loop
    private final transient BufferedImage backBuffer;//   | Off-screen image used for double buffering
    private transient Graphics2D g2d;//                   | Context used to draw onto the back buffer.
    private AbstractScreen currentScreen;//               | Active screen (e.g. menu, game, pause) being displayed and updated.

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
        loadArcadeFont();

        // ----- GAME INITIALIZATION ----- 
        AnimationLoader.loadAnimationsFromJson();
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

    private void loadArcadeFont() {
        try {
            File fontFile = new File("fonts/PressStart2P.ttf");
            arcadeFont = Font.createFont(Font.TRUETYPE_FONT, fontFile).deriveFont(18f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(arcadeFont);
        } catch (IOException | FontFormatException e) {
            System.out.println("Arcade font failed to load.");
            e.printStackTrace();
        }
    }
    
    public Font getArcadeFont() {
        return arcadeFont;
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

    
    //Dynamically switches to a new screen, removing the old one to free up
    public void setScreen(final AbstractScreen newScreen) {
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

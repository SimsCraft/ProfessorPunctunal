import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class GameWindow extends JFrame implements ActionListener, KeyListener, MouseListener {
    private JButton startB, exitB;
    private JPanel mainPanel;
    private GamePanel gamePanel;
    private Font arcadeFont;

    public GameWindow() {
        setTitle("Professor Punctual");
        setSize(800, 650);
        setLocationRelativeTo(null);
        loadArcadeFont();

        startB = new JButton("Start");
        exitB = new JButton("Exit");
        startB.addActionListener(this);
        exitB.addActionListener(this);

        mainPanel = new JPanel(new BorderLayout());
        gamePanel = new GamePanel(arcadeFont);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(startB);
        buttonPanel.add(exitB);

        mainPanel.add(gamePanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();

        add(mainPanel);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(startB.getText())) {
            gamePanel.startGame(); // Start game when clicking "Start"
            gamePanel.requestFocusInWindow();
        } else if (command.equals(exitB.getText())) {
            System.exit(0);
        }
        gamePanel.requestFocusInWindow(); // Ensure keyboard input works
    }

    @Override
    public void keyPressed(KeyEvent e) {
        gamePanel.handleKeyPress(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        gamePanel.handleKeyRelease(e.getKeyCode());
    }

    public void keyTyped(KeyEvent e) {}

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
}
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import javax.swing.JPanel;

public class NPC {
    private int width = 32, height = 32;
    private Color npcColor;
    private Color backgroundColour;
    private int x, y;
    private int dx, dy;
    private int speed;
    private JPanel panel;
    private Random random = new Random();
    public boolean hasCollided = false;
    private int moveDelay = 0;

    public NPC(JPanel p, int xPos, int yPos, int speed, Color color) {
        this.panel = p;
        this.x = xPos;
        this.y = yPos;
        this.speed = speed;
        this.npcColor = color;
        setRandomDirection();
    }

    // Set a random direction for movement
    protected void setRandomDirection() {
        // Pick a random direction from 8 possible ones
        int[] directions = {-1, 0, 1}; // -1 = left/up, 1 = right/down, 0 = stay
        dx = directions[random.nextInt(3)] * speed;
        dy = directions[random.nextInt(3)] * speed;

        // Ensure NPC actually moves
        if (dx == 0 && dy == 0) {
            setRandomDirection();
        }
    }

    // Move NPCs in their current direction
    public void move() {
        if (moveDelay % 4 == 0) { // NPCs move every 4 frames
            x += dx;
            y += dy;

            if (random.nextInt(50) == 1) {
                setRandomDirection();
            }

            if (x < 0 || x > panel.getWidth() - width) {
                dx = -dx;
                setRandomDirection();
            }
            if (y < 0 || y > panel.getHeight() - height) {
                dy = -dy;
                setRandomDirection();
            }
        }
        moveDelay++;
    }


    public void reverseDirection() {
        dx = -dx;
        dy = -dy;
    }


    public void draw() {
        Graphics g = panel.getGraphics();
        if (g != null) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(npcColor);
            g2.fill(new Rectangle2D.Double(x, y, width, height)); 
            g.dispose();
        }
    }

    public void erase(){
        Graphics g = panel.getGraphics ();
        Graphics2D g2 = (Graphics2D) g;
        
        backgroundColour = panel.getBackground ();
        g2.setColor (backgroundColour);
        g2.fill (new Rectangle2D.Double (x-10, y-10, 30+20, 45+20));
  
        g.dispose();
    }

    // Get NPC position for collision detection
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }
}
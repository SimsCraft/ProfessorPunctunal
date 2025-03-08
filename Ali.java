import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")

public class Ali {
    private JPanel panel;
    private int x;
    private int y;
    private int width;
    private int height;

    private int dx;
    private int dy;
    private final int SPEED = 4;
    private Set<Integer> pressedKeys = new HashSet<>();

    private Dimension dimension;
    private Color backgroundColour;

    private Rectangle2D.Double ali;

    public Ali (JPanel p, int xPos, int yPos){
        panel = p;
        dimension = panel.getSize();

        x = xPos;
        y = yPos;

        dx = 10;	// make bigger (smaller) to increase (decrease) speed
        dy = 0;	// no movement along y-axis allowed (i.e., move left to right only)

        width = 32;
        height = 32;
    }

    public void draw(){
        Graphics g = panel.getGraphics ();
        Graphics2D g2 = (Graphics2D) g;

        ali = new Rectangle2D.Double(x, y, width, height);
        g2.setColor(Color.BLUE);
        g2.fill(ali);

        g.dispose();
    }

    public void erase(){
        Graphics g = panel.getGraphics ();
        Graphics2D g2 = (Graphics2D) g;
        
        backgroundColour = panel.getBackground ();
        g2.setColor (backgroundColour);
        g2.fill (new Rectangle2D.Double (x-10, y-10, 30+20, 45+20));
  
        g.dispose();
    }

    public void keyPressed(int keyCode) {
        pressedKeys.add(keyCode);
    }

    public void keyReleased(int keyCode) {
        pressedKeys.remove(keyCode);
    }

    public void move() {
        int newX = x;
        int newY = y;
    
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) newX -= SPEED;
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) newX += SPEED;
        if (pressedKeys.contains(KeyEvent.VK_UP)) newY -= SPEED;
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) newY += SPEED;
    
        // Check if Ali's new position collides with an NPC
        Rectangle newBounds = new Rectangle(newX, newY, width, height);
        boolean collision = false;
    
        for (NPC npc : ((GamePanel) panel).npcs) {
            if (newBounds.intersects(npc.getBounds())) {
                collision = true;
                break;
            }
        }
    
        // Only move if there's no collision
        if (!collision) {
            x = newX;
            y = newY;
        }
    
        // Keep Mr. Ali inside screen boundaries
        if (x < 0) x = 0;
        if (x > panel.getWidth() - width) x = panel.getWidth() - width;
        if (y < 0) y = 0;
        if (y > panel.getHeight() - height) y = panel.getHeight() - height;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

}

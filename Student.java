import java.awt.*;
import javax.swing.JPanel;

public class Student extends NPC {
    public Student(JPanel panel, int xPos, int yPos) {
        super(panel, xPos, yPos, 2, Color.GREEN);; // Slow speed
    }
}
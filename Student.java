import javax.swing.JPanel;
import java.awt.Image;

public class Student extends NPC {
    public Student(JPanel panel, int xPos, int yPos, Image backgroundImage) {
        super(panel, xPos, yPos, 2, "Student/FStud", backgroundImage); // Slow speed
    }
}

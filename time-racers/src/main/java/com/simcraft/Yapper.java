import javax.swing.JPanel;
import java.awt.Image;

public class Yapper extends NPC {
    public Yapper(JPanel panel, int xPos, int yPos, Image backgroundImage) {
        super(panel, xPos, yPos, 4, "Yapper/yap", backgroundImage); // Fast speed
    }
}

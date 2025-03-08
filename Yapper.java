import java.awt.*;

public class Yapper extends NPC {
    @SuppressWarnings("unused")
    private final int chaseRange = 100;

    public Yapper(GamePanel panel, int xPos, int yPos) {
        super(panel, xPos, yPos, 4, Color.RED); // Fast speed
    }
}

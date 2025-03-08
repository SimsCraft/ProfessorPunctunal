import javax.swing.*;
import java.awt.*;

public class TimerPanel extends JPanel {
    private int timeLeft;

    public TimerPanel(int startTime) {
        this.timeLeft = startTime;
        setPreferredSize(new Dimension(800, 50)); // Fixed size for top panel
        setBackground(Color.ORANGE);
    }

    // Update the displayed time
    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
        repaint(); // Refresh panel
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Time Left: " + timeLeft + "s", 20, 30);
    }
}
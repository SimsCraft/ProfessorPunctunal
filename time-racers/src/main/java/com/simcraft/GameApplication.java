
import javax.swing.*;
@SuppressWarnings("unused")

public class GameApplication
{
	public static void main (String[] args) {

		JFrame window = new JFrame("Professor Punctual");
		window.setSize(800, 650);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
		window.setLocationRelativeTo(null);

		WelcomeScreen welcomeScreen = new WelcomeScreen(window);
        window.setContentPane(welcomeScreen);
        window.setVisible(true);
        welcomeScreen.requestFocusInWindow();
		

		//JFrame gameWindow = new GameWindow();
	}

}

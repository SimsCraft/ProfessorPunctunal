import javax.swing.*;			// need this for GUI objects
import java.awt.*;			// need this for Layout Managers
import java.awt.event.*;		// need this to respond to GUI events
	
public class GameWindow extends JFrame 
				implements ActionListener,
					   KeyListener,
					   MouseListener
{
	// declare instance variables for user interface objects

	// declare labels 

	
	// declare text fields

	private JTextField statusBarTF;
	private JTextField keyTF;
	private JTextField mouseTF;

	// declare buttons
	private JButton startB;
	private JButton exitB;

	private Container c;

	private JPanel mainPanel;
	private GamePanel gamePanel;
	private TimerPanel timerPanel;

	GridLayout gridLayout;


	// declare new colours
	public static final Color LIGHT_GREY = new Color(153,153,153);


	//@SuppressWarnings({"unchecked"})
	public GameWindow() {
 
		setTitle ("Professor Punctunal");
		setSize (800, 650);
		setLocationRelativeTo(null); // Window will appear in the middle of screen

		// create user interface objects

		// create text fields and set their colour, etc.

		statusBarTF = new JTextField (25);
		keyTF = new JTextField (25);
		mouseTF = new JTextField (25);

		statusBarTF.setEditable(false);
		keyTF.setEditable(false);
		mouseTF.setEditable(false);

		statusBarTF.setBackground(Color.CYAN);
		keyTF.setBackground(Color.YELLOW);
		mouseTF.setBackground(Color.GREEN);

		// create buttons

		startB = new JButton ("Start");
		exitB = new JButton ("Exit");

		// add listener to each button (same as the current object)

		startB.addActionListener(this);
		exitB.addActionListener(this);

		
		// create mainPanel
		mainPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		mainPanel.setLayout(flowLayout);

		

		// TimerPanel
		timerPanel = new TimerPanel(60);
		timerPanel.setSize(200, 200);

		// create the gamePanel for game entities

		gamePanel = new GamePanel(timerPanel);
        gamePanel.setPreferredSize(new Dimension(700, 500));
		gamePanel.setBackground(LIGHT_GREY);
		gamePanel.createGameEntities();

		
		// create buttonPanel

		JPanel buttonPanel = new JPanel();
		gridLayout = new GridLayout(1, 4);
		buttonPanel.setLayout(gridLayout);

		// add buttons to buttonPanel

		buttonPanel.add (startB);
		buttonPanel.add (exitB);

		// add sub-panels with GUI objects to mainPanel and set its colour

		mainPanel.add(timerPanel);
		mainPanel.add(gamePanel);
		mainPanel.add(buttonPanel);
		mainPanel.setBackground(Color.LIGHT_GRAY);

		
		// set up mainPanel to respond to keyboard and mouse

		gamePanel.addMouseListener(this);
		mainPanel.addKeyListener(this);

		// add mainPanel to window surface

		c = getContentPane();
		c.add(mainPanel);

		// set properties of window

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);

		// set status bar message

		statusBarTF.setText("Application started.");
	}


	// implement single method in ActionListener interface
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		statusBarTF.setText(command + " button clicked.");
	
		if (command.equals(startB.getText())) {
			gamePanel.startGameThread(); // 🔥 Start NPC movement & game loop
		}
	
		if (command.equals(exitB.getText())) {
			System.exit(0);
		}
	
		mainPanel.requestFocus();
	}


	// implement methods in KeyListener interface
	public void keyPressed(KeyEvent e) {

		gamePanel.ali.keyPressed(e.getKeyCode());
		gamePanel.updateGameEntities();
		gamePanel.drawGameEntities();

	}

	public void keyReleased(KeyEvent e) {
		gamePanel.ali.keyReleased(e.getKeyCode());
		gamePanel.updateGameEntities();
		gamePanel.drawGameEntities();
	}

	public void keyTyped(KeyEvent e) {

	}

	// implement methods in MouseListener interface

	public void mouseClicked(MouseEvent e) {

		int x = e.getX();
		int y = e.getY();

		mouseTF.setText("(" + x +", " + y + ")");

	}


	public void mouseEntered(MouseEvent e) {
	
	}

	public void mouseExited(MouseEvent e) {
	
	}

	public void mousePressed(MouseEvent e) {
	
	}

	public void mouseReleased(MouseEvent e) {
	
	}

}
import javax.swing.*;			// need this for GUI objects
import java.awt.*;			// need this for Layout Managers
import java.awt.event.*;		// need this to respond to GUI events
import javax.swing.Timer;
	
public class GameWindow extends JFrame 
				implements ActionListener,
					   KeyListener
{
	// declare instance variables for user interface objects

	// declare labels 

	private JLabel scoreL;
	private JLabel livesL;
	private JLabel levelL;

	// declare text fields

	private JTextField scoreTF;
	private JTextField livesTF;
	private JTextField levelTF;

	// declare buttons

	private JButton startB;
	private JButton pauseB;
	private JButton exitB;

	private Container c;

	private JPanel mainPanel;
	private GamePanel gamePanel;

	private ScoreManager scoreManager;
	private SoundManager soundManager;

	private Timer gameTimer;
	private Timer alienFireTimer;

	@SuppressWarnings({"unchecked"})
	public GameWindow() {
 
		setTitle ("Space Invaders");
		setSize (600, 650);

		scoreManager = ScoreManager.getInstance();
		soundManager = SoundManager.getInstance();

		// create labels

		scoreL = new JLabel ("Score: ");
		livesL = new JLabel ("Lives: ");
		levelL = new JLabel ("Level: ");

		// create text fields and set their colour, etc.

		scoreTF = new JTextField(10);
		livesTF = new JTextField(5);
		levelTF = new JTextField(5);

		scoreTF.setEditable(false);
		livesTF.setEditable(false);
		levelTF.setEditable(false);

		scoreTF.setBackground(Color.CYAN);
		livesTF.setBackground(Color.YELLOW);
		levelTF.setBackground(Color.GREEN);

		updateScoreDisplay();

		// create buttons

		startB = new JButton ("Start");
	        pauseB = new JButton ("Pause");
	        exitB = new JButton ("Exit");

		// add listener to each button (same as the current object)

		startB.addActionListener(this);
		pauseB.addActionListener(this);
		exitB.addActionListener(this);

		
		// create mainPanel

		mainPanel = new JPanel();
		FlowLayout flowLayout = new FlowLayout();
		mainPanel.setLayout(flowLayout);

		GridLayout gridLayout;

		// create the gamePanel for game entities

		gamePanel = new GamePanel();
        	gamePanel.setPreferredSize(new Dimension(580, 500));


		// create infoPanel

		JPanel infoPanel = new JPanel();
		gridLayout = new GridLayout(1, 6);
		infoPanel.setLayout(gridLayout);
		infoPanel.setBackground(Color.ORANGE);

		// add user interface objects to infoPanel
	
		infoPanel.add (scoreL);
		infoPanel.add (scoreTF);

		infoPanel.add (livesL);
		infoPanel.add (livesTF);		

		infoPanel.add (levelL);
		infoPanel.add (levelTF);

		
		// create buttonPanel

		JPanel buttonPanel = new JPanel();
		gridLayout = new GridLayout(1, 3);
		buttonPanel.setLayout(gridLayout);

		// add buttons to buttonPanel

		buttonPanel.add (startB);
		buttonPanel.add (pauseB);
		buttonPanel.add (exitB);

		// add sub-panels with GUI objects to mainPanel and set its colour

		mainPanel.add(infoPanel);
		mainPanel.add(gamePanel);
		mainPanel.add(buttonPanel);
		mainPanel.setBackground(Color.PINK);

		// set up mainPanel to respond to keyboard

		mainPanel.addKeyListener(this);

		// add mainPanel to window surface

		c = getContentPane();
		c.add(mainPanel);

		// set properties of window

		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		setVisible(true);

		// Create game timer for collision checking and updates
		gameTimer = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gamePanel.isGameRunning() && !gamePanel.isGamePaused()) {
					gamePanel.checkCollisions();
					updateScoreDisplay();
				}
			}
		});

		// Create timer for alien firing
		alienFireTimer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (gamePanel.isGameRunning() && !gamePanel.isGamePaused()) {
					gamePanel.fireAlienBullet();
				}
			}
		});
	}


	// implement single method in ActionListener interface

	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();

		if (command.equals(startB.getText())) {
			scoreManager.reset();
			updateScoreDisplay();
			gamePanel.startGame();
			gamePanel.drawGameEntities();
			gameTimer.start();
			alienFireTimer.start();
			mainPanel.requestFocus();
		}

		if (command.equals(pauseB.getText())) {
			gamePanel.pauseGame();
			if (gamePanel.isGamePaused()) {
				pauseB.setText("Resume");
				gameTimer.stop();
				alienFireTimer.stop();
			}
			else {
				pauseB.setText("Pause");
				gameTimer.start();
				alienFireTimer.start();
			}
			mainPanel.requestFocus();
		}

		if (command.equals(exitB.getText()))
			System.exit(0);

		mainPanel.requestFocus();
	}


	// implement methods in KeyListener interface

	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		if (keyCode == KeyEvent.VK_RIGHT) {
			gamePanel.updateGameEntities(2);
			gamePanel.drawGameEntities();
		}

		if (keyCode == KeyEvent.VK_LEFT) {
			gamePanel.updateGameEntities(1);
			gamePanel.drawGameEntities();
		}

		if (keyCode == KeyEvent.VK_SPACE) {
			gamePanel.firePlayerBullet();
		}
	}

	public void keyReleased(KeyEvent e) {

	}

	public void keyTyped(KeyEvent e) {

	}


	private void updateScoreDisplay() {
		scoreTF.setText(String.valueOf(scoreManager.getScore()));
		livesTF.setText(String.valueOf(scoreManager.getLives()));
		levelTF.setText(String.valueOf(scoreManager.getLevel()));
	}

}

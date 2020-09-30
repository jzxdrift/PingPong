import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Pong extends JPanel
{
	private static final int WINDOW_WIDTH = 800, WINDOW_HEIGHT = 500;
	private static final int FIELD_WIDTH = WINDOW_WIDTH - 7, FIELD_HEIGHT = WINDOW_HEIGHT - 61;
	private static final int SPEED = 10;
	private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 60);
	private static final Font START_FONT = new Font("Arial", Font.PLAIN, 20);
	private static final Font SOUND_FONT = new Font("Symbola", Font.PLAIN, 30);
	private static final String HOME_DIRECTORY = "user.home";
	private static final String FILENAME = "pong_records.txt";
	private static final File FILE = new File(System.getProperty(HOME_DIRECTORY), FILENAME);
	private BufferedReader in;
	private BufferedWriter out;
	private static Random random;
	private static JPanel topPanel;
	private static boolean scored;
	private static boolean gameOver;
	private static boolean soundOn;
	private static AudioInputStream scoreStream = null;
	private static Clip scoreClip = null;
	private Paddle player1, player2;
	private Ball ball;
	private Timer timer;
	private JLabel scoreLabel;
	private JLabel soundLabel;
	private boolean w, s, up, down;
	private boolean gameStarted;
	private boolean newGame;
	private boolean gamePaused;
	private int pauseClick;
	private int startClick;
	private int countDown;
	private int onOffClick;

	public Pong() throws IOException, UnsupportedAudioFileException, LineUnavailableException
	{
		random = new Random();
		topPanel = new JPanel(new GridLayout(1, 3));
		topPanel.setBackground(Color.BLACK);
		scoreLabel = new JLabel();
		scoreLabel.setForeground(Color.WHITE);
		scoreLabel.setFont(START_FONT);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		soundLabel = new JLabel();
		soundLabel.setForeground(Color.WHITE);
		soundLabel.setFont(SOUND_FONT);
		soundLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		topPanel.add(new JLabel());
		topPanel.add(scoreLabel);
		topPanel.add(soundLabel);
		scored = false;
		player1 = new Paddle(1);
		player2 = new Paddle(2);
		ball = new Ball();
		gameOver = false;
		gameStarted = false;
		newGame = false;
		gamePaused = false;
		pauseClick = 0;
		startClick = 0;
		countDown = 3;
		soundOn = true;
		onOffClick = 0;
		scoreStream = AudioSystem.getAudioInputStream(getClass().getClassLoader().getResource("score.wav"));
		scoreClip = AudioSystem.getClip();
		scoreClip.open(scoreStream);
		readFile();
		timer = new Timer(40, new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				// actual count down 3 2 1
				if(newGame || scored)
				{
					repaint();
					try
					{
						// 1 second delay between count down numbers
						Thread.sleep(1000);
					}
					catch(InterruptedException e1)
					{
					}
					if(countDown == 0)
					{
						newGame = false;
						scored = false;
						countDown = 3;
					}
				}
				// objects movements
				if(gameStarted && !gamePaused && !newGame && !scored && !gameOver)
				{
					if(w)
						player1.movePaddle(true);
					if(s)
						player1.movePaddle(false);
					if(up)
						player2.movePaddle(true);
					if(down)
						player2.movePaddle(false);
					ball.moveBall(player1, player2);
					scoreLabel.setText(player1.getScore() + " - " + player2.getScore());
					soundLabel.setText((soundOn) ? "\uD83D\uDD0A" : "\uD83D\uDD07");
				}
				repaint();
			}
		});
		this.setFocusable(true);
		this.addKeyListener(new KeyListener()
		{
			@Override
			public void keyPressed(KeyEvent e)
			{
				if(gameStarted)
				{
					// enable controls only when game is active
					if(!gamePaused && !newGame && !scored)
					{
						if(e.getKeyCode() == KeyEvent.VK_W)
							w = true;
						if(e.getKeyCode() == KeyEvent.VK_S)
							s = true;
						if(e.getKeyCode() == KeyEvent.VK_UP)
							up = true;
						if(e.getKeyCode() == KeyEvent.VK_DOWN)
							down = true;
						// sound on/off
						if(e.getKeyCode() == KeyEvent.VK_O)
						{
							if(onOffClick == 0)
							{
								soundOn = false;
								onOffClick++;
							}
							else
							{
								soundOn = true;
								onOffClick = 0;
							}
						}
					}
					// pause the game
					if(e.getKeyCode() == KeyEvent.VK_ESCAPE && !newGame && !scored)
					{
						if(pauseClick == 1)
						{
							gamePaused = false;
							pauseClick = 0;
							return;
						}
						gamePaused = true;
						pauseClick++;
					}
				}
				// start the game from main screen
				if(e.getKeyCode() == KeyEvent.VK_SPACE && startClick == 0)
				{
					player1.setSpeed(SPEED);
					player2.setSpeed(SPEED);
					int xSpeed = (random.nextInt() % 2 == 0) ? SPEED : -SPEED;
					int ySpeed = (random.nextInt() % 2 == 0) ? SPEED : -SPEED;
					ball.setSpeed(xSpeed, ySpeed);
					gameStarted = true;
					newGame = true;
					scoreLabel.setText(player1.getScore() + " - " + player2.getScore());
					soundLabel.setText((soundOn) ? "\uD83D\uDD0A" : "\uD83D\uDD07");
					startClick++;
					repaint();
				}
				// game over
				if(gameOver)
				{
					// restart game
					if(e.getKeyCode() == KeyEvent.VK_R)
					{
						player1.setDefaultPosition(1);
						player1.setScore(0);
						player2.setDefaultPosition(2);
						player2.setScore(0);
						scoreLabel.setText(player1.getScore() + " - " + player2.getScore());
						soundLabel.setText((soundOn) ? "\uD83D\uDD0A" : "\uD83D\uDD07");
						scored = false;
						gameOver = false;
						gameStarted = true;
						newGame = true;
						gamePaused = false;
					}
					// quit game
					if(e.getKeyCode() == KeyEvent.VK_Q)
					{
						try
						{
							saveFile();
							scoreStream.close();
							scoreClip.close();
						}
						catch(IOException e1)
						{
						}
						System.exit(0);
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e)
			{
				// allow both paddles move at the same time
				if(gameStarted)
				{
					if(e.getKeyCode() == KeyEvent.VK_W)
						w = false;
					if(e.getKeyCode() == KeyEvent.VK_S)
						s = false;
					if(e.getKeyCode() == KeyEvent.VK_UP)
						up = false;
					if(e.getKeyCode() == KeyEvent.VK_DOWN)
						down = false;
				}
			}

			@Override
			public void keyTyped(KeyEvent e)
			{
			}
		});
		timer.start();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
		g.setColor(Color.WHITE);
		if(gameOver)
		{
			scoreLabel.setText("");
			soundLabel.setText("");
			g.setFont(TITLE_FONT);
			g.drawString("Game Over", WINDOW_WIDTH / 2 - 170, WINDOW_HEIGHT / 2 - 150);
			String player = (player1.getScore() == Paddle.getMaxScore()) ? "Player1" : "Player2";
			g.setFont(START_FONT);
			g.drawString(player + " Wins", WINDOW_WIDTH / 2 - 65, WINDOW_HEIGHT / 2 - 100);
			g.drawString("R - Restart", WINDOW_WIDTH / 2 - 120, WINDOW_HEIGHT / 2 - 40);
			g.drawString("Q - Quit", WINDOW_WIDTH / 2 + 30, WINDOW_HEIGHT / 2 - 40);
			return;
		}
		if(gameStarted)
		{
			g.drawRect(0, 0, FIELD_WIDTH, FIELD_HEIGHT);
			// remove middle line in case of pause, score, or new game
			if(gamePaused || newGame || scored)
				g.setColor(Color.BLACK);
			g.drawLine(FIELD_WIDTH / 2, 1, FIELD_WIDTH / 2, FIELD_HEIGHT - 1);
			g.setColor(Color.WHITE);
			player1.paintPaddle(g);
			player2.paintPaddle(g);
			ball.paintBall(g);
			if(gamePaused)
			{
				g.setFont(START_FONT);
				g.drawString("PAUSE", WINDOW_WIDTH / 2 - 36, WINDOW_HEIGHT / 2 - 50);
			}
			if(newGame || scored)
			{
				// draw count down 3 2 1
				g.setFont(TITLE_FONT);
				g.drawString(Integer.toString(countDown), WINDOW_WIDTH / 2 - 20, WINDOW_HEIGHT / 2 - 80);
				countDown--;
			}
		}
		// main screen
		else
		{
			g.setFont(TITLE_FONT);
			g.drawString("PONG", WINDOW_WIDTH / 2 - 85, WINDOW_HEIGHT / 2 - 150);
			g.setFont(START_FONT);
			g.drawString("Press Space to start...", WINDOW_WIDTH / 2 - 90, WINDOW_HEIGHT / 2 - 100);
			// players records (wins)
			g.drawString("Player1 Wins: " + player1.getWins(), WINDOW_WIDTH / 2 - 65, WINDOW_HEIGHT / 2);
			g.drawString("Player2 Wins: " + player2.getWins(), WINDOW_WIDTH / 2 - 65, WINDOW_HEIGHT / 2 + 30);
		}
	}

	// read players records (wins) from file
	private void readFile() throws IOException
	{
		if(FILE.exists())
		{
			in = new BufferedReader(new FileReader(FILE));
			player1.setWins(Integer.parseInt(in.readLine()));
			player2.setWins(Integer.parseInt(in.readLine()));
			in.close();
		}
	}

	// save players records (wins) to file
	private void saveFile() throws IOException
	{
		out = new BufferedWriter(new FileWriter(FILE));
		out.write(Integer.toString(player1.getWins()));
		out.newLine();
		out.write(Integer.toString(player2.getWins()));
		out.close();
	}

	public static int getFieldWidth()
	{
		return FIELD_WIDTH;
	}

	public static int getFieldHeight()
	{
		return FIELD_HEIGHT;
	}

	public static int getSpeed()
	{
		return SPEED;
	}

	public static Random getRandom()
	{
		return random;
	}

	public static void setGameOver(boolean gameOver)
	{
		Pong.gameOver = gameOver;
	}

	public static void setScored(boolean scored)
	{
		Pong.scored = scored;
	}

	public static Clip getScoreClip()
	{
		return scoreClip;
	}

	public static boolean isSoundOn()
	{
		return soundOn;
	}

	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				JFrame game = new JFrame("Pong");
				game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				game.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
				game.setResizable(false);
				try
				{
					final Pong pong = new Pong();
					game.getContentPane().add(pong, BorderLayout.CENTER);
					game.addWindowListener(new WindowAdapter()
					{
						@Override
						public void windowClosing(WindowEvent e)
						{
							try
							{
								// save players records (wins) to file on
								// program closing
								pong.saveFile();
								Pong.scoreClip.close();
								Pong.scoreStream.close();
							}
							catch(IOException e1)
							{
							}
						}
					});
				}
				catch(IOException | UnsupportedAudioFileException | LineUnavailableException e)
				{
				}
				game.getContentPane().add(topPanel, BorderLayout.NORTH);
				game.setVisible(true);
			}
		});
	}
}
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Ball
{
	private static final int DIAMETER = 20;
	private Rectangle ball;
	private int xSpeed, ySpeed;

	public Ball()
	{
		ball = new Rectangle(DIAMETER, DIAMETER);
		this.setDefaultPosition();
		this.xSpeed = 0;
		this.ySpeed = 0;
	}

	public void paintBall(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillOval(ball.x, ball.y, DIAMETER, DIAMETER);
	}

	public void moveBall(Paddle player1, Paddle player2)
	{
		ball.x += xSpeed;
		ball.y += ySpeed;
		checkCollision(player1, player2);
	}

	private void checkCollision(Paddle player1, Paddle player2)
	{
		// hit top or bottom wall
		if(ball.y <= 0 || ball.y >= Pong.getFieldHeight() - DIAMETER)
			ySpeed = -ySpeed;
		// hit left wall (score)
		if(ball.x <= 0)
		{
			// play score sound
			if(Pong.isSoundOn())
			{
				Pong.getScoreClip().setMicrosecondPosition(0);
				Pong.getScoreClip().start();
			}
			player2.setScore(player2.getScore() + 1);
			// player2 reaches maximum score and wins
			if(player2.getScore() == Paddle.getMaxScore())
			{
				player2.setWins(player2.getWins() + 1);
				Pong.setGameOver(true);
			}
			// set ball direction towards player1
			int speed = (Pong.getRandom().nextInt() % 2 == 0) ? Pong.getSpeed() : -Pong.getSpeed();
			this.setSpeed(-Pong.getSpeed(), speed);
			this.setDefaultPosition();
			Pong.setScored(true);
		}
		// hit right wall (score)
		else if(ball.x >= Pong.getFieldWidth() - DIAMETER)
		{
			// play score sound
			if(Pong.isSoundOn())
			{
				Pong.getScoreClip().setMicrosecondPosition(0);
				Pong.getScoreClip().start();
			}
			player1.setScore(player1.getScore() + 1);
			// player1 reaches maximum score and wins
			if(player1.getScore() == Paddle.getMaxScore())
			{
				player1.setWins(player1.getWins() + 1);
				Pong.setGameOver(true);
			}
			// set ball direction towards player2
			int speed = (Pong.getRandom().nextInt() % 2 == 0) ? Pong.getSpeed() : -Pong.getSpeed();
			this.setSpeed(Pong.getSpeed(), speed);
			this.setDefaultPosition();
			Pong.setScored(true);
		}
		// hit left or right paddle
		if(ball.intersects(player1.getPaddle()) || ball.intersects(player2.getPaddle()))
			xSpeed = -xSpeed;
	}

	private void setDefaultPosition()
	{
		// middle of the screen
		ball.x = Pong.getFieldWidth() / 2 - DIAMETER / 2;
		ball.y = Pong.getFieldHeight() / 2 - DIAMETER / 2;
	}

	public void setSpeed(int xSpeed, int ySpeed)
	{
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
	}
}
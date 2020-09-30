import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Paddle
{
	private static final int WIDTH = 20, HEIGHT = 80;
	private static final int MAX_SCORE = 7;
	private Rectangle paddle;
	private int ySpeed, score, wins;

	public Paddle(int number)
	{
		paddle = new Rectangle(WIDTH, HEIGHT);
		this.setDefaultPosition(number);
		this.ySpeed = 0;
		this.score = 0;
		this.wins = 0;
	}

	public void paintPaddle(Graphics g)
	{
		g.setColor(Color.WHITE);
		g.fillRect(paddle.x, paddle.y, WIDTH, HEIGHT);
	}

	public void movePaddle(boolean up)
	{
		// up
		if(up)
		{
			if(paddle.y <= 0)
				paddle.y = 0;
			else
				paddle.y -= ySpeed;
		}
		// down
		else
		{
			if(paddle.y >= Pong.getFieldHeight() - HEIGHT)
				paddle.y = Pong.getFieldHeight() - HEIGHT;
			else
				paddle.y += ySpeed;
		}
	}

	public void setDefaultPosition(int number)
	{
		// left side
		if(number == 1)
		{
			paddle.x = WIDTH;
			paddle.y = Pong.getFieldHeight() / 2 - HEIGHT / 2;
		}
		// right side
		else
		{
			paddle.x = Pong.getFieldWidth() - (WIDTH + WIDTH);
			paddle.y = Pong.getFieldHeight() / 2 - HEIGHT / 2;
		}
	}

	public static int getMaxScore()
	{
		return MAX_SCORE;
	}

	public Rectangle getPaddle()
	{
		return paddle;
	}

	public void setSpeed(int ySpeed)
	{
		this.ySpeed = ySpeed;
	}

	public int getScore()
	{
		return score;
	}

	public void setScore(int score)
	{
		this.score = score;
	}

	public int getWins()
	{
		return wins;
	}

	public void setWins(int wins)
	{
		this.wins = wins;
	}
}
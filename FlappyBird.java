import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;

class FlappyBirdGraphics extends JPanel 
{
    // Game Constants
    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 600;
    static final int PIPE_WIDTH = 100;
    static final int PIPE_GAP = 150;  
    static final int PIPE_DISTANCE = 300; 
    static final int BIRD_SIZE = 40; // bumped up a bit so the sprite is visible
    
    static final int GRAVITY = 1;
    static final int FLAP_STRENGTH = -10;
    static final int PIPE_SPEED = 5;

    // Bird sprite
    BufferedImage birdImage;

    // Game Variables (Bird & Pipes)
    int birdX = SCREEN_WIDTH / 4;
    int birdY = SCREEN_HEIGHT / 2;
    int birdVelocity = 2; //Bird's Falling Speed
    
	// Pipe Positions
    int pipeX = SCREEN_WIDTH;
    int nextPipeX = pipeX + PIPE_DISTANCE;
    int thirdPipeX = nextPipeX + PIPE_DISTANCE;  
    
	// Random pipe heights
    int pipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;
    int nextPipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;
    int thirdPipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50; // Third pipe height
    
	// Score & game over flag
    int score = 0;
    boolean gameOver = false;
	
    // Constructor (Setup game & key controls)
    FlappyBirdGraphics() 
	{
        setFocusable(true);

        // Load the bird sprite once, at startup
        try 
		{
            birdImage = ImageIO.read(new File("bird.png"));
        } 
		catch (IOException e) 
		{
            System.out.println("Could not load bird.png - make sure it's in the same folder as the .java file. Falling back to a yellow circle.");
            birdImage = null;
        }

        addKeyListener(new KeyAdapter() 
		{
            @Override
            public void keyPressed(KeyEvent e) 
			{
                if (e.getKeyCode() == KeyEvent.VK_SPACE && !gameOver) 
				{
                    birdVelocity = FLAP_STRENGTH;
                } 
				else 
					if (e.getKeyCode() == KeyEvent.VK_S && gameOver) 
				{
                    restartGame();
                }
            }
        });
		
		// Timer runs game loop every 20ms
        Timer timer = new Timer(20, e -> gameLoop());
        timer.start();
    }
    
	// Game Loop (Updates every frame)
    void gameLoop() 
	{
        if (!gameOver) 
		{
            birdVelocity += GRAVITY;
            birdY += birdVelocity;

            // Move pipes to the Left
            pipeX -= PIPE_SPEED;
            nextPipeX -= PIPE_SPEED;
            thirdPipeX -= PIPE_SPEED;

            // Reset pipes when they go off-screen
            if (pipeX + PIPE_WIDTH < 0) 
			{
                pipeX = thirdPipeX + PIPE_DISTANCE;
                pipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;
                score++;
            }

            if (nextPipeX + PIPE_WIDTH < 0) 
			{
                nextPipeX = pipeX + PIPE_DISTANCE;
                nextPipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;
                score++;
            }

            if (thirdPipeX + PIPE_WIDTH < 0) 
			{
                thirdPipeX = nextPipeX + PIPE_DISTANCE;
                thirdPipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;
                score++;
            }

            // Collision Detection
            if (birdY < 0 || birdY + BIRD_SIZE > SCREEN_HEIGHT ||
                (birdX + BIRD_SIZE > pipeX && birdX < pipeX + PIPE_WIDTH &&
                        (birdY < pipeHeight || birdY + BIRD_SIZE > pipeHeight + PIPE_GAP)) ||
                (birdX + BIRD_SIZE > nextPipeX && birdX < nextPipeX + PIPE_WIDTH &&
                        (birdY < nextPipeHeight || birdY + BIRD_SIZE > nextPipeHeight + PIPE_GAP)) ||
                (birdX + BIRD_SIZE > thirdPipeX && birdX < thirdPipeX + PIPE_WIDTH &&
                        (birdY < thirdPipeHeight || birdY + BIRD_SIZE > thirdPipeHeight + PIPE_GAP))) {
                gameOver = true;
            }

            repaint();  // Refresh Screen
        }
    }

    void restartGame() 
	{
        birdY = SCREEN_HEIGHT / 2;
        birdVelocity = 0;
        pipeX = SCREEN_WIDTH;
        nextPipeX = pipeX + PIPE_DISTANCE;
        thirdPipeX = nextPipeX + PIPE_DISTANCE;

        pipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;
        nextPipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;
        thirdPipeHeight = new Random().nextInt(SCREEN_HEIGHT / 2) + 50;

        score = 0;
        gameOver = false;
        repaint();
    }
    
	// Render Graphics
    @Override
    public void paintComponent(Graphics g) 
	{
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Background (Sky)
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // Bird - draw the sprite if it loaded, otherwise fall back to a circle
        if (birdImage != null) 
		{
            // Rotate slightly based on velocity, like the real game does
            double angle = Math.toRadians(Math.max(-25, Math.min(70, birdVelocity * 3)));
            AffineTransform old = g2d.getTransform();
            g2d.translate(birdX + BIRD_SIZE / 2, birdY + BIRD_SIZE / 2);
            g2d.rotate(angle);
            g2d.drawImage(birdImage, -BIRD_SIZE / 2, -BIRD_SIZE / 2, BIRD_SIZE, BIRD_SIZE, null);
            g2d.setTransform(old);
        } 
		else 
		{
            g.setColor(Color.YELLOW);
            g.fillOval(birdX, birdY, BIRD_SIZE, BIRD_SIZE);
        }

        // Pipes (Green Rectangle)
        g.setColor(Color.GREEN);
        g.fillRect(pipeX, 0, PIPE_WIDTH, pipeHeight);
        g.fillRect(pipeX, pipeHeight + PIPE_GAP, PIPE_WIDTH, SCREEN_HEIGHT);

        g.fillRect(nextPipeX, 0, PIPE_WIDTH, nextPipeHeight);
        g.fillRect(nextPipeX, nextPipeHeight + PIPE_GAP, PIPE_WIDTH, SCREEN_HEIGHT);

        g.fillRect(thirdPipeX, 0, PIPE_WIDTH, thirdPipeHeight);
        g.fillRect(thirdPipeX, thirdPipeHeight + PIPE_GAP, PIPE_WIDTH, SCREEN_HEIGHT);

        // Ground
        g.setColor(Color.ORANGE);
        g.fillRect(0, SCREEN_HEIGHT - 50, SCREEN_WIDTH, 50);

        // Score Display
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);

        // Game Over Screen
        if (gameOver) 
		{
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Game Over!", SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 - 50);
            g.setFont(new Font("Arial", Font.PLAIN, 18));
            g.drawString("Press 'S' to Restart", SCREEN_WIDTH / 2 - 90, SCREEN_HEIGHT / 2);
        }
    }

    public static void main(String[] args) 
	{
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBirdGraphics gamePanel = new FlappyBirdGraphics();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
        frame.add(gamePanel);
        frame.setResizable(false);
        frame.setVisible(true);
    }
}

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    // Window Settings
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;

    // Game Loop
    private Timer timer;
    private boolean gameOver = false;
    private int score = 0;
    private int highScore = 0;

    // Bird Variables
    private Rectangle bird;
    private int yVelocity = 0;
    private final int GRAVITY = 1; // Pulls bird down
    private final int JUMP_STRENGTH = -12; // Pushes bird up

    // Pipe Variables
    private Rectangle pipe1Top, pipe1Bottom;
    private Rectangle pipe2Top, pipe2Bottom;
    private final int PIPE_WIDTH = 60;
    private final int PIPE_GAP = 150; // Space between top and bottom pipe

    // Speed scaling variables
    private final int BASE_PIPE_SPEED = 5;           // starting speed
    private final int SPEED_INCREASE_INTERVAL = 5;   // every X points, speed increases by 1
    private final int MAX_PIPE_SPEED = 20;           // safety cap on speed

    public FlappyBird() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.CYAN); // Sky blue background
        this.setFocusable(true);
        this.addKeyListener(this);

        // Bird starts in the middle
        bird = new Rectangle(100, HEIGHT / 2, 30, 30);

        // Start Pipe 1 off-screen to the right
        int h1 = 200;
        pipe1Top = new Rectangle(WIDTH, 0, PIPE_WIDTH, h1);
        pipe1Bottom = new Rectangle(WIDTH, h1 + PIPE_GAP, PIPE_WIDTH, HEIGHT - (h1 + PIPE_GAP) - 100);

        // Start Pipe 2 even further back (width + half width)
        int h2 = 300;
        pipe2Top = new Rectangle(WIDTH + WIDTH / 2, 0, PIPE_WIDTH, h2);
        pipe2Bottom = new Rectangle(WIDTH + WIDTH / 2, h2 + PIPE_GAP, PIPE_WIDTH, HEIGHT - (h2 + PIPE_GAP) - 100);

        // Run game at ~60 FPS
        timer = new Timer(20, this);
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Move things
        update();
        // Draw things
        repaint();
    }

    public void update() {
        if (!gameOver) {
            // 1. Apply Gravity
            yVelocity += GRAVITY; // Gravity pulls us down faster every tick
            bird.y += yVelocity;  // Move the bird

            // 2. Stop bird from falling through floor
            if (bird.y > HEIGHT - 120) {
                gameOver = true;
            }
            // Stop bird from flying into space
            if (bird.y < 0) {
                bird.y = 0;
                yVelocity = 0;
            }

            // Compute current pipe speed based on score.
            // The higher the score, the faster pipes move.
            int currentPipeSpeed = Math.min(MAX_PIPE_SPEED, BASE_PIPE_SPEED + (score / SPEED_INCREASE_INTERVAL));

            // Move Pipes Left
            pipe1Top.x -= currentPipeSpeed;
            pipe1Bottom.x -= currentPipeSpeed;
            pipe2Top.x -= currentPipeSpeed;
            pipe2Bottom.x -= currentPipeSpeed;

            // Recycle Pipe 1
            if (pipe1Top.x + PIPE_WIDTH < 0) {
                pipe1Top.x = WIDTH;
                pipe1Bottom.x = WIDTH;
                score++; // You passed a pipe!

                // Randomize Height
                int newHeight = (int) (Math.random() * 300) + 50; // Random height between 50 and 350
                pipe1Top.height = newHeight;
                pipe1Bottom.y = newHeight + PIPE_GAP;
                pipe1Bottom.height = HEIGHT - (newHeight + PIPE_GAP) - 100;
            }

            // Recycle Pipe 2
            if (pipe2Top.x + PIPE_WIDTH < 0) {
                pipe2Top.x = WIDTH;
                pipe2Bottom.x = WIDTH;
                score++;

                int newHeight = (int) (Math.random() * 300) + 50;
                pipe2Top.height = newHeight;
                pipe2Bottom.y = newHeight + PIPE_GAP;
                pipe2Bottom.height = HEIGHT - (newHeight + PIPE_GAP) - 100;
            }

            // Check Collisions
            if (bird.intersects(pipe1Top) || bird.intersects(pipe1Bottom) ||
                bird.intersects(pipe2Top) || bird.intersects(pipe2Bottom)) {
                gameOver = true;
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw Ground (Orange dirt + green grass strip)
        g.setColor(Color.ORANGE);
        g.fillRect(0, HEIGHT - 100, WIDTH, 100);
        g.setColor(Color.GREEN);
        g.fillRect(0, HEIGHT - 100, WIDTH, 20);

        // Draw Pipes
        g.setColor(Color.GREEN.darker());
        g.fillRect(pipe1Top.x, pipe1Top.y, pipe1Top.width, pipe1Top.height);
        g.fillRect(pipe1Bottom.x, pipe1Bottom.y, pipe1Bottom.width, pipe1Bottom.height);

        g.fillRect(pipe2Top.x, pipe2Top.y, pipe2Top.width, pipe2Top.height);
        g.fillRect(pipe2Bottom.x, pipe2Bottom.y, pipe2Bottom.width, pipe2Bottom.height);

        // Draw Bird
        g.setColor(Color.YELLOW);
        g.fillRect(bird.x, bird.y, bird.width, bird.height);

        // Draw Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 36));
        String scoreText = "Score: " + score;
        int scoreWidth = g.getFontMetrics().stringWidth(scoreText);
        g.drawString(scoreText, WIDTH / 2 - scoreWidth / 2, 50);

        // Draw High Score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        String hsText = "High Score: " + highScore;
        g.drawString(hsText, WIDTH - g.getFontMetrics().stringWidth(hsText) - 10, 30);

        // Optionally display current speed for debugging/feedback
        String speedText = "Speed: " + Math.min(MAX_PIPE_SPEED, BASE_PIPE_SPEED + (score / SPEED_INCREASE_INTERVAL));
        g.drawString(speedText, 10, 30);

        if (gameOver) {
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 50));
            String go = "Game Over!";
            int goW = g.getFontMetrics().stringWidth(go);
            g.drawString(go, WIDTH / 2 - goW / 2, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            String r = "Press Space to Restart";
            int rW = g.getFontMetrics().stringWidth(r);
            g.drawString(r, WIDTH / 2 - rW / 2, HEIGHT / 2 + 50);
        }
    }

    // Keyboard Stubs
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            if (gameOver) {
                // Save high score
                if (score > highScore) {
                    highScore = score;
                }

                // Reset Bird
                bird.y = HEIGHT / 2;
                yVelocity = 0;

                // Reset Pipes
                pipe1Top.x = WIDTH;
                pipe1Bottom.x = WIDTH;
                // randomize starting heights when restarting for variety
                int h2 = (int) (Math.random() * 300) + 50;
                pipe2Top.x = WIDTH + WIDTH / 2;
                pipe2Bottom.x = WIDTH + WIDTH / 2;
                pipe2Top.height = h2;
                pipe2Bottom.y = h2 + PIPE_GAP;
                pipe2Bottom.height = HEIGHT - (h2 + PIPE_GAP) - 100;

                score = 0;
                gameOver = false;
            } else {
                // Jump!
                yVelocity = JUMP_STRENGTH;
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Flappy Bird");
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
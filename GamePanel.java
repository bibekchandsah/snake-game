import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * Main game panel that handles game logic, rendering, and user input
 */
public class GamePanel extends JPanel implements ActionListener {
    
    // Game board dimensions
    private static final int BOARD_WIDTH = 600;
    private static final int BOARD_HEIGHT = 600;
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (BOARD_WIDTH * BOARD_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    
    // Game settings
    private int delay = 150; // Timer delay in milliseconds (Easy=150, Medium=100, Hard=50)
    private boolean wallCollisionEnabled = false; // User choice for wall collision (default: disabled)
    
    // Snake body
    private final ArrayList<Point> snake;
    private char direction = 'R'; // R=Right, L=Left, U=Up, D=Down
    
    // Food
    private Point food;
    
    // Game state
    private boolean running = false;
    private boolean paused = false;
    private int score = 0;
    private Timer timer;
    private Random random;
    
    public GamePanel() {
        random = new Random();
        snake = new ArrayList<>();
        
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(new MyKeyAdapter());
        
        // Don't auto-start game - wait for user to click Start button
        initializeSnake();
    }
    
    /**
     * Initialize snake position (called on load, doesn't start game)
     */
    private void initializeSnake() {
        snake.clear();
        int startX = BOARD_WIDTH / 2;
        int startY = BOARD_HEIGHT / 2;
        snake.add(new Point(startX, startY)); // Head
        snake.add(new Point(startX - UNIT_SIZE, startY));
        snake.add(new Point(startX - 2 * UNIT_SIZE, startY));
        
        direction = 'R';
        score = 0;
        spawnFood();
        repaint();
    }
    
    /**
     * Start the game
     */
    public void startGame() {
        // Initialize snake at center with 3 segments
        snake.clear();
        int startX = BOARD_WIDTH / 2;
        int startY = BOARD_HEIGHT / 2;
        snake.add(new Point(startX, startY)); // Head
        snake.add(new Point(startX - UNIT_SIZE, startY));
        snake.add(new Point(startX - 2 * UNIT_SIZE, startY));
        
        direction = 'R';
        score = 0;
        spawnFood();
        running = true;
        paused = false;
        
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(delay, this);
        timer.start();
    }
    
    /**
     * Pause the game
     */
    public void pauseGame() {
        if (running) {
            paused = true;
            if (timer != null) {
                timer.stop();
            }
        }
    }
    
    /**
     * Resume the game
     */
    public void resumeGame() {
        if (running && paused) {
            paused = false;
            if (timer != null) {
                timer.start();
            }
        }
    }
    
    /**
     * Check if game is running
     */
    public boolean isRunning() {
        return running;
    }
    
    /**
     * Check if game is paused
     */
    public boolean isPaused() {
        return paused;
    }
    
    /**
     * Update game speed
     */
    public void setDelay(int newDelay) {
        this.delay = newDelay;
        if (timer != null && running) {
            timer.setDelay(newDelay);
        }
    }
    
    /**
     * Set wall collision mode
     */
    public void setWallCollisionEnabled(boolean enabled) {
        this.wallCollisionEnabled = enabled;
    }
    
    /**
     * Generate food at random location
     */
    public void spawnFood() {
        int x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        food = new Point(x, y);
        
        // Make sure food doesn't spawn on snake body
        for (Point segment : snake) {
            if (segment.equals(food)) {
                spawnFood(); // Recursively find new position
                return;
            }
        }
    }
    
    /**
     * Move the snake forward
     */
    public void move() {
        Point head = snake.get(0);
        Point newHead = new Point(head);
        
        // Move head in current direction
        switch (direction) {
            case 'U':
                newHead.y -= UNIT_SIZE;
                break;
            case 'D':
                newHead.y += UNIT_SIZE;
                break;
            case 'L':
                newHead.x -= UNIT_SIZE;
                break;
            case 'R':
                newHead.x += UNIT_SIZE;
                break;
        }
        
        // Add new head at front
        snake.add(0, newHead);
        
        // Check if snake ate food
        if (newHead.equals(food)) {
            score++;
            spawnFood();
        } else {
            // Remove tail if no food eaten (snake doesn't grow)
            snake.remove(snake.size() - 1);
        }
    }
    
    /**
     * Check for collisions
     */
    public void checkCollisions() {
        Point head = snake.get(0);
        
        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                return;
            }
        }
        
        // Check wall collision (if enabled)
        if (wallCollisionEnabled) {
            if (head.x < 0 || head.x >= BOARD_WIDTH || 
                head.y < 0 || head.y >= BOARD_HEIGHT) {
                running = false;
                return;
            }
        } else {
            // Wrap around if wall collision disabled
            if (head.x < 0) head.x = BOARD_WIDTH - UNIT_SIZE;
            if (head.x >= BOARD_WIDTH) head.x = 0;
            if (head.y < 0) head.y = BOARD_HEIGHT - UNIT_SIZE;
            if (head.y >= BOARD_HEIGHT) head.y = 0;
        }
        
        // Stop timer if game over
        if (!running) {
            timer.stop();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    /**
     * Draw game elements
     */
    public void draw(Graphics g) {
        if (running) {
            // Draw grid (optional)
            g.setColor(new Color(25, 25, 25));
            for (int i = 0; i < BOARD_WIDTH / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, BOARD_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, BOARD_WIDTH, i * UNIT_SIZE);
            }
            
            // Draw food
            g.setColor(Color.RED);
            g.fillOval(food.x, food.y, UNIT_SIZE, UNIT_SIZE);
            
            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                Point segment = snake.get(i);
                if (i == 0) {
                    // Head
                    g.setColor(Color.GREEN);
                } else {
                    // Body
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(segment.x, segment.y, UNIT_SIZE, UNIT_SIZE);
            }
            
            // Draw score
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Score: " + score, 10, 25);
            
            // Draw paused message
            if (paused) {
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, BOARD_WIDTH, BOARD_HEIGHT);
                
                g.setColor(Color.YELLOW);
                g.setFont(new Font("Arial", Font.BOLD, 50));
                FontMetrics metrics = getFontMetrics(g.getFont());
                g.drawString("PAUSED", 
                            (BOARD_WIDTH - metrics.stringWidth("PAUSED")) / 2, 
                            BOARD_HEIGHT / 2);
            }
            
        } else {
            gameOver(g);
        }
    }
    
    /**
     * Display game over screen
     */
    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", 
                    (BOARD_WIDTH - metrics1.stringWidth("Game Over")) / 2, 
                    BOARD_HEIGHT / 2 - 50);
        
        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Final Score: " + score, 
                    (BOARD_WIDTH - metrics2.stringWidth("Final Score: " + score)) / 2, 
                    BOARD_HEIGHT / 2 + 20);
        
        // Restart instruction
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Click 'Start' button to restart", 
                    (BOARD_WIDTH - metrics3.stringWidth("Click 'Start' button to restart")) / 2, 
                    BOARD_HEIGHT / 2 + 70);
    }
    
    /**
     * Game loop - called by timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkCollisions();
        }
        repaint();
    }
    
    /**
     * Handle keyboard input
     */
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();
            
            // Direction controls (cannot reverse instantly)
            if (key == KeyEvent.VK_LEFT && direction != 'R') {
                direction = 'L';
            } else if (key == KeyEvent.VK_RIGHT && direction != 'L') {
                direction = 'R';
            } else if (key == KeyEvent.VK_UP && direction != 'D') {
                direction = 'U';
            } else if (key == KeyEvent.VK_DOWN && direction != 'U') {
                direction = 'D';
            }
        }
    }
}

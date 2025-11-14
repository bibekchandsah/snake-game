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
    private static final int SCORE_PANEL_HEIGHT = 60; // Height for score display below board
    private static final int UNIT_SIZE = 25;
    private static final int GAME_UNITS = (BOARD_WIDTH * BOARD_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    
    // Game settings
    private int delay = 150; // Timer delay in milliseconds (Easy=150, Medium=100, Hard=50)
    private boolean wallCollisionEnabled = false; // User choice for wall collision (default: disabled)
    
    // Snake body
    private final ArrayList<Point> snake;
    private char direction = 'R'; // R=Right, L=Left, U=Up, D=Down
    
    // Food system
    private Food currentFood;
    private long lastGoldenFoodTime = 0;
    private long lastBonusFoodTime = 0;
    private static final long GOLDEN_FOOD_INTERVAL = 15000; // Every 15 seconds
    private static final long BONUS_FOOD_INTERVAL = 20000; // Every 20 seconds
    
    // Speed boost effect
    private boolean speedBoostActive = false;
    private long speedBoostEndTime = 0;
    private static final long SPEED_BOOST_DURATION = 5000; // 5 seconds
    private int originalDelay;
    
    // Game state
    private boolean running = false;
    private boolean paused = false;
    private boolean gameStarted = false; // Track if game has ever been started
    private boolean isNewHighScore = false; // Track if current game achieved new high score
    private int score = 0;
    private Timer timer;
    private Random random;
    private HighScoreManager highScoreManager;
    
    public GamePanel() {
        random = new Random();
        snake = new ArrayList<>();
        highScoreManager = new HighScoreManager();
        
        setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT + SCORE_PANEL_HEIGHT));
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
        originalDelay = delay;
        spawnFood(Food.FoodType.NORMAL);
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
        isNewHighScore = false; // Reset high score flag
        originalDelay = delay;
        speedBoostActive = false;
        lastGoldenFoodTime = System.currentTimeMillis();
        lastBonusFoodTime = System.currentTimeMillis();
        spawnFood(Food.FoodType.NORMAL);
        running = true;
        paused = false;
        gameStarted = true; // Mark that game has been started
        
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
        this.originalDelay = newDelay;
        if (timer != null && running && !speedBoostActive) {
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
     * Generate food at random location with specified type
     */
    public void spawnFood(Food.FoodType type) {
        int x = random.nextInt(BOARD_WIDTH / UNIT_SIZE) * UNIT_SIZE;
        int y = random.nextInt(BOARD_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
        Point position = new Point(x, y);
        
        // Make sure food doesn't spawn on snake body
        for (Point segment : snake) {
            if (segment.equals(position)) {
                spawnFood(type); // Recursively find new position
                return;
            }
        }
        
        currentFood = new Food(position, type);
    }
    
    /**
     * Determine next food type based on game conditions
     */
    private Food.FoodType getNextFoodType() {
        long currentTime = System.currentTimeMillis();
        
        // Check if it's time for bonus food (highest priority)
        if (currentTime - lastBonusFoodTime >= BONUS_FOOD_INTERVAL) {
            lastBonusFoodTime = currentTime;
            return Food.FoodType.BONUS;
        }
        
        // Check if it's time for golden food
        if (currentTime - lastGoldenFoodTime >= GOLDEN_FOOD_INTERVAL) {
            lastGoldenFoodTime = currentTime;
            return Food.FoodType.GOLDEN;
        }
        
        // Random chance for speed boost (10% chance)
        if (random.nextInt(10) == 0) {
            return Food.FoodType.SPEED;
        }
        
        // Default to normal food
        return Food.FoodType.NORMAL;
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
        if (newHead.equals(currentFood.getPosition())) {
            // Add points based on food type
            score += currentFood.getPoints();
            
            // Apply special effects
            if (currentFood.hasEffect() && currentFood.getType() == Food.FoodType.SPEED) {
                activateSpeedBoost();
            }
            
            // Check for new high score
            if (score > highScoreManager.getHighScore()) {
                if (!isNewHighScore) {
                    isNewHighScore = true; // Mark that we achieved a new high score
                }
                highScoreManager.updateHighScore(score);
            }
            
            // Spawn next food
            spawnFood(getNextFoodType());
        } else {
            // Remove tail if no food eaten (snake doesn't grow)
            snake.remove(snake.size() - 1);
        }
    }
    
    /**
     * Activate speed boost effect
     */
    private void activateSpeedBoost() {
        speedBoostActive = true;
        speedBoostEndTime = System.currentTimeMillis() + SPEED_BOOST_DURATION;
        int boostedDelay = (int)(originalDelay * 0.6); // 40% faster
        if (timer != null) {
            timer.setDelay(boostedDelay);
        }
    }
    
    /**
     * Check and deactivate speed boost if expired
     */
    private void checkSpeedBoost() {
        if (speedBoostActive && System.currentTimeMillis() >= speedBoostEndTime) {
            speedBoostActive = false;
            if (timer != null) {
                timer.setDelay(originalDelay);
            }
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
            
            // Check if bonus food expired
            if (currentFood != null && currentFood.isExpired()) {
                spawnFood(getNextFoodType());
            }
            
            // Draw food
            if (currentFood != null) {
                drawFood(g, currentFood);
            }
            
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
            
            // Draw score panel at bottom
            drawScorePanel(g);
            
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
            
        } else if (gameStarted) {
            // Game over screen (only if game was actually played)
            gameOver(g);
        } else {
            // Welcome screen (initial load)
            welcomeScreen(g);
        }
    }
    
    /**
     * Draw food with effects
     */
    private void drawFood(Graphics g, Food food) {
        Point pos = food.getPosition();
        
        // Draw glow effect for special foods
        if (food.getType() != Food.FoodType.NORMAL) {
            g.setColor(new Color(food.getColor().getRed(), 
                                 food.getColor().getGreen(), 
                                 food.getColor().getBlue(), 50));
            g.fillOval(pos.x - 5, pos.y - 5, UNIT_SIZE + 10, UNIT_SIZE + 10);
        }
        
        // Draw main food
        g.setColor(food.getColor());
        g.fillOval(pos.x, pos.y, UNIT_SIZE, UNIT_SIZE);
        
        // Draw emoji
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        g.drawString(food.getEmoji(), pos.x + 3, pos.y + 20);
        
        // Draw timer for bonus food
        if (food.getType() == Food.FoodType.BONUS) {
            long remaining = food.getRemainingTime() / 1000;
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 12));
            g.drawString(String.valueOf(remaining), pos.x + 8, pos.y - 5);
        }
    }
    
    /**
     * Draw score panel below the game board
     */
    private void drawScorePanel(Graphics g) {
        // Draw separator line
        g.setColor(new Color(60, 60, 60));
        g.fillRect(0, BOARD_HEIGHT, BOARD_WIDTH, 2);
        
        // Score panel background
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, BOARD_HEIGHT + 2, BOARD_WIDTH, SCORE_PANEL_HEIGHT - 2);
        
        // Current Score with emoji
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        g.drawString("🎯 Score: " + score, 20, BOARD_HEIGHT + 35);
        
        // High Score with emoji
        g.setColor(Color.YELLOW);
        g.drawString("🏆 High Score: " + highScoreManager.getHighScore(), 250, BOARD_HEIGHT + 35);
        
        // New High Score indicator or Speed Boost indicator
        if (isNewHighScore) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
            g.drawString("⭐ NEW RECORD!", 460, BOARD_HEIGHT + 35);
        } else if (speedBoostActive) {
            long remaining = (speedBoostEndTime - System.currentTimeMillis()) / 1000;
            g.setColor(new Color(0, 191, 255));
            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
            g.drawString("⚡ SPEED BOOST " + remaining + "s", 440, BOARD_HEIGHT + 35);
        }
        
        // Draw current food description
        if (currentFood != null && currentFood.getType() != Food.FoodType.NORMAL) {
            g.setColor(new Color(180, 180, 180));
            g.setFont(new Font("Segoe UI Emoji", Font.ITALIC, 14));
            g.drawString(currentFood.getDescription(), 20, BOARD_HEIGHT + 52);
        }
    }
    
    /**
     * Display welcome screen
     */
    public void welcomeScreen(Graphics g) {
        // Welcome title with emoji
        g.setColor(new Color(0, 200, 0));
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 60));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("🐍 Snake Game", 
                    (BOARD_WIDTH - metrics1.stringWidth("🐍 Snake Game")) / 2, 
                    BOARD_HEIGHT / 2 - 100);
        
        // Welcome message
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Welcome! Configure your settings above ⚙️", 
                    (BOARD_WIDTH - metrics2.stringWidth("Welcome! Configure your settings above ⚙️")) / 2, 
                    BOARD_HEIGHT / 2 - 20);
        
        // Instructions
        g.setColor(new Color(180, 180, 180));
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        
        String[] instructions = {
            "⚡ Select Difficulty: Easy, Medium, or Hard",
            "🧱 Toggle Wall Collision (off = wrap around)",
            "▶️ Click 'Start' button to begin playing",
            "⌨️ Use Arrow Keys to control the snake"
        };
        
        int yPos = BOARD_HEIGHT / 2 + 40;
        for (String instruction : instructions) {
            g.drawString(instruction, 
                        (BOARD_WIDTH - metrics3.stringWidth(instruction)) / 2, 
                        yPos);
            yPos += 35;
        }
        
        // Start prompt
        g.setColor(new Color(0, 255, 0));
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("🎮 Click 'Start' to Play!", 
                    (BOARD_WIDTH - metrics4.stringWidth("🎮 Click 'Start' to Play!")) / 2, 
                    BOARD_HEIGHT / 2 + 200);
    }
    
    /**
     * Display game over screen
     */
    public void gameOver(Graphics g) {
        // Game Over text
        g.setColor(Color.RED);
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 50));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("💀 Game Over", 
                    (BOARD_WIDTH - metrics1.stringWidth("💀 Game Over")) / 2, 
                    BOARD_HEIGHT / 2 - 100);
        
        // New High Score celebration
        if (isNewHighScore) {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 36));
            FontMetrics metricsHS = getFontMetrics(g.getFont());
            g.drawString("⭐ NEW HIGH SCORE! ⭐", 
                        (BOARD_WIDTH - metricsHS.stringWidth("⭐ NEW HIGH SCORE! ⭐")) / 2, 
                        BOARD_HEIGHT / 2 - 40);
        }
        
        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("🎯 Final Score: " + score, 
                    (BOARD_WIDTH - metrics2.stringWidth("🎯 Final Score: " + score)) / 2, 
                    BOARD_HEIGHT / 2 + 20);
        
        // High Score
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("🏆 High Score: " + highScoreManager.getHighScore(), 
                    (BOARD_WIDTH - metrics3.stringWidth("🏆 High Score: " + highScoreManager.getHighScore())) / 2, 
                    BOARD_HEIGHT / 2 + 60);
        
        // Restart instruction
        g.setColor(Color.LIGHT_GRAY);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        g.drawString("🔄 Click 'Start' button to restart", 
                    (BOARD_WIDTH - metrics4.stringWidth("🔄 Click 'Start' button to restart")) / 2, 
                    BOARD_HEIGHT / 2 + 110);
    }
    
    /**
     * Game loop - called by timer
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !paused) {
            move();
            checkCollisions();
            checkSpeedBoost();
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

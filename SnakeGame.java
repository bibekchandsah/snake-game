import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Main class to launch the Snake Game
 */
public class SnakeGame extends JFrame {
    
    private GamePanel gamePanel;
    
    public SnakeGame() {
        setTitle("Snake Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        
        // Set custom icon for window and taskbar
        setIconImage(loadIcon());
        
        // Create game panel
        gamePanel = new GamePanel();
        add(gamePanel, BorderLayout.CENTER);
        
        // Create settings panel
        SettingsPanel settingsPanel = new SettingsPanel(gamePanel);
        add(settingsPanel, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    /**
     * Load custom icon for the application
     */
    private Image loadIcon() {
        try {
            // Try to load PNG icon first (better compatibility)
            File pngIcon = new File("icon.png");
            if (pngIcon.exists()) {
                return ImageIO.read(pngIcon);
            }
            
            // Try ICO file
            File iconFile = new File("icon.ico");
            if (iconFile.exists()) {
                // Read ICO file - get the largest image from it
                return Toolkit.getDefaultToolkit().getImage("icon.ico");
            }
            
            // Try to load from resources (if packaged in JAR)
            InputStream iconStream = getClass().getResourceAsStream("/icon.png");
            if (iconStream == null) {
                iconStream = getClass().getResourceAsStream("/icon.ico");
            }
            if (iconStream != null) {
                return ImageIO.read(iconStream);
            }
            
            // If icon not found, create a simple snake icon
            return createDefaultIcon();
        } catch (Exception e) {
            System.err.println("Could not load icon: " + e.getMessage());
            return createDefaultIcon();
        }
    }
    
    /**
     * Create a default snake icon if custom icon is not available
     */
    private Image createDefaultIcon() {
        BufferedImage icon = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        
        // Enable anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a simple snake shape
        g.setColor(new Color(0, 180, 0));
        g.fillRoundRect(10, 20, 44, 24, 20, 20); // Body
        g.fillOval(40, 15, 20, 20); // Head
        
        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(48, 20, 6, 6);
        g.setColor(Color.BLACK);
        g.fillOval(50, 22, 3, 3);
        
        g.dispose();
        return icon;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}

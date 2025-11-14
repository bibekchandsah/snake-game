import javax.swing.*;
import java.awt.*;

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
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SnakeGame());
    }
}

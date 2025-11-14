import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Settings panel with difficulty selection and wall collision toggle
 */
public class SettingsPanel extends JPanel {
    
    private static final int EASY_DELAY = 150;
    private static final int MEDIUM_DELAY = 100;
    private static final int HARD_DELAY = 50;
    
    private GamePanel gamePanel;
    private JRadioButton easyButton;
    private JRadioButton mediumButton;
    private JRadioButton hardButton;
    private JCheckBox wallCollisionCheckBox;
    private JButton startPauseButton;
    
    public SettingsPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(600, 60));
        setLayout(new FlowLayout(FlowLayout.CENTER, 15, 10));
        
        // Difficulty level section
        JLabel difficultyLabel = new JLabel("Difficulty:");
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(difficultyLabel);
        
        // Radio buttons for difficulty
        easyButton = new JRadioButton("Easy");
        mediumButton = new JRadioButton("Medium");
        hardButton = new JRadioButton("Hard");
        
        // Set default to Easy (current speed was too fast)
        easyButton.setSelected(true);
        gamePanel.setDelay(EASY_DELAY);
        
        // Style radio buttons
        styleRadioButton(easyButton);
        styleRadioButton(mediumButton);
        styleRadioButton(hardButton);
        
        // Prevent SPACE key from triggering radio buttons
        easyButton.setFocusable(false);
        mediumButton.setFocusable(false);
        hardButton.setFocusable(false);
        
        // Group radio buttons
        ButtonGroup difficultyGroup = new ButtonGroup();
        difficultyGroup.add(easyButton);
        difficultyGroup.add(mediumButton);
        difficultyGroup.add(hardButton);
        
        // Add action listeners
        easyButton.addActionListener(e -> gamePanel.setDelay(EASY_DELAY));
        mediumButton.addActionListener(e -> gamePanel.setDelay(MEDIUM_DELAY));
        hardButton.addActionListener(e -> gamePanel.setDelay(HARD_DELAY));
        
        add(easyButton);
        add(mediumButton);
        add(hardButton);
        
        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(2, 30));
        separator.setForeground(Color.GRAY);
        add(separator);
        
        // Wall collision checkbox
        wallCollisionCheckBox = new JCheckBox("Wall Collision", true);
        wallCollisionCheckBox.setForeground(Color.WHITE);
        wallCollisionCheckBox.setBackground(new Color(40, 40, 40));
        wallCollisionCheckBox.setFont(new Font("Arial", Font.BOLD, 14));
        wallCollisionCheckBox.setFocusPainted(false);
        wallCollisionCheckBox.setFocusable(false); // Prevent SPACE key from triggering
        
        wallCollisionCheckBox.addActionListener(e -> 
            gamePanel.setWallCollisionEnabled(wallCollisionCheckBox.isSelected())
        );
        
        add(wallCollisionCheckBox);
        
        // Add separator
        JSeparator separator2 = new JSeparator(SwingConstants.VERTICAL);
        separator2.setPreferredSize(new Dimension(2, 30));
        separator2.setForeground(Color.GRAY);
        add(separator2);
        
        // Start/Pause button
        startPauseButton = new JButton("Start");
        startPauseButton.setFont(new Font("Arial", Font.BOLD, 14));
        startPauseButton.setForeground(Color.WHITE);
        startPauseButton.setBackground(new Color(0, 150, 0));
        startPauseButton.setFocusPainted(false);
        startPauseButton.setPreferredSize(new Dimension(100, 35));
        startPauseButton.setBorderPainted(false);
        startPauseButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        startPauseButton.addActionListener(e -> {
            if (!gamePanel.isRunning()) {
                // Start new game
                gamePanel.startGame();
                startPauseButton.setText("Pause");
                startPauseButton.setBackground(new Color(200, 150, 0));
            } else if (gamePanel.isPaused()) {
                // Resume game
                gamePanel.resumeGame();
                startPauseButton.setText("Pause");
                startPauseButton.setBackground(new Color(200, 150, 0));
            } else {
                // Pause game
                gamePanel.pauseGame();
                startPauseButton.setText("Resume");
                startPauseButton.setBackground(new Color(0, 150, 0));
            }
            gamePanel.requestFocusInWindow(); // Return focus to game panel
        });
        
        add(startPauseButton);
    }
    
    /**
     * Style radio button for dark theme
     */
    private void styleRadioButton(JRadioButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(40, 40, 40));
        button.setFont(new Font("Arial", Font.PLAIN, 13));
        button.setFocusPainted(false);
    }
}

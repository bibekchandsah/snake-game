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
    
    public SettingsPanel(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        
        setBackground(new Color(40, 40, 40));
        setPreferredSize(new Dimension(600, 60));
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
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
        
        wallCollisionCheckBox.addActionListener(e -> 
            gamePanel.setWallCollisionEnabled(wallCollisionCheckBox.isSelected())
        );
        
        add(wallCollisionCheckBox);
        
        // Add info label
        JLabel infoLabel = new JLabel("   [SPACE: Restart]");
        infoLabel.setForeground(Color.LIGHT_GRAY);
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        add(infoLabel);
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

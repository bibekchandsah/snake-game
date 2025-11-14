import java.io.*;
import java.nio.file.*;

/**
 * Manages high score persistence
 */
public class HighScoreManager {
    
    private static final String HIGH_SCORE_FILE = "highscore.dat";
    private int highScore;
    
    public HighScoreManager() {
        loadHighScore();
    }
    
    /**
     * Load high score from file
     */
    private void loadHighScore() {
        try {
            Path path = Paths.get(HIGH_SCORE_FILE);
            if (Files.exists(path)) {
                String content = Files.readString(path);
                highScore = Integer.parseInt(content.trim());
            } else {
                highScore = 0;
            }
        } catch (IOException | NumberFormatException e) {
            highScore = 0;
        }
    }
    
    /**
     * Save high score to file
     */
    private void saveHighScore() {
        try {
            Files.writeString(Paths.get(HIGH_SCORE_FILE), String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
    
    /**
     * Get current high score
     */
    public int getHighScore() {
        return highScore;
    }
    
    /**
     * Update high score if new score is higher
     * @return true if new high score was set
     */
    public boolean updateHighScore(int newScore) {
        if (newScore > highScore) {
            highScore = newScore;
            saveHighScore();
            return true;
        }
        return false;
    }
    
    /**
     * Reset high score
     */
    public void resetHighScore() {
        highScore = 0;
        saveHighScore();
    }
}

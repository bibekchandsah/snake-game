import java.awt.*;

/**
 * Represents different types of food in the game
 */
public class Food {
    
    public enum FoodType {
        NORMAL,     // Red, 1 point
        GOLDEN,     // Gold, 5 points
        BONUS,      // Purple, 3 points, appears briefly
        SPEED       // Blue, 1 point, temporary speed boost
    }
    
    private Point position;
    private FoodType type;
    private long spawnTime;
    private static final long BONUS_DURATION = 5000; // 5 seconds for bonus food
    
    public Food(Point position, FoodType type) {
        this.position = position;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
    }
    
    public Point getPosition() {
        return position;
    }
    
    public FoodType getType() {
        return type;
    }
    
    public int getPoints() {
        switch (type) {
            case GOLDEN:
                return 5;
            case BONUS:
                return 3;
            case SPEED:
            case NORMAL:
            default:
                return 1;
        }
    }
    
    public Color getColor() {
        switch (type) {
            case GOLDEN:
                return new Color(255, 215, 0); // Gold
            case BONUS:
                return new Color(138, 43, 226); // Purple
            case SPEED:
                return new Color(0, 191, 255); // Deep Sky Blue
            case NORMAL:
            default:
                return Color.RED;
        }
    }
    
    public String getEmoji() {
        switch (type) {
            case GOLDEN:
                return "⭐";
            case BONUS:
                return "💎";
            case SPEED:
                return "⚡";
            case NORMAL:
            default:
                return "🍎";
        }
    }
    
    public boolean isExpired() {
        if (type == FoodType.BONUS) {
            return (System.currentTimeMillis() - spawnTime) > BONUS_DURATION;
        }
        return false;
    }
    
    public long getRemainingTime() {
        if (type == FoodType.BONUS) {
            long elapsed = System.currentTimeMillis() - spawnTime;
            long remaining = BONUS_DURATION - elapsed;
            return remaining > 0 ? remaining : 0;
        }
        return 0;
    }
    
    public boolean hasEffect() {
        return type == FoodType.SPEED;
    }
    
    public String getDescription() {
        switch (type) {
            case GOLDEN:
                return "Golden Apple - 5 Points!";
            case BONUS:
                return "Bonus Diamond - 3 Points!";
            case SPEED:
                return "Speed Boost - Faster movement!";
            case NORMAL:
            default:
                return "Apple - 1 Point";
        }
    }
}

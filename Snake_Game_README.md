# 🐍 Snake Game – Java (Concept README)

## 📌 Overview
This project explains the core logic behind creating a simple Snake Game in Java.  
It covers movement, food generation, collision detection, and the game loop mechanism.

## 🎮 How the Game Works (Simple Explanation)

### ✅ 1. Snake Movement
- The snake moves continuously in one direction.
- Player controls movement using arrow keys.
- Snake cannot instantly reverse direction.

### ✅ 2. Snake Body
- Snake has a head and body segments.
- The head moves first.
- Each segment follows the one ahead of it.

### ✅ 3. Food
- Food appears randomly on the board.
- When the snake eats the food:
  - It grows longer.
  - New food appears somewhere else.
  - Score increases.

### ✅ 4. Collision Detection
Two main collisions:

#### ❌ a) Wall Collision (User choice if they enable)
Snake's head hits boundary → Game Over.

#### ❌ b) Self Collision
Snake's head touches its own body → Game Over.

### ✅ 5. Game Loop
A timer repeatedly:
1. Moves the snake  
2. Checks food collision  
3. Checks wall/self collision  
4. Updates the screen  

## 🧠 Core Logic Summary
- Move forward
- Grow when eating food
- End when colliding
- Keep refreshing game frame

## 🛠 Technologies Used
- Java
- Java Swing
- Timer


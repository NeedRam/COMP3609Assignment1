import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
   A component that displays all the game entities
 */

public class GamePanel extends JPanel {
   
   PlayerShip player;
   AlienSwarm alienSwarm;
   ArrayList<Bullet> playerBullets;
   ArrayList<Bullet> alienBullets;

   SoundManager soundManager;
   ScoreManager scoreManager;

   private Image backgroundImage;
   private Image gameOverScreen;
   private Image spaceBackground;

   private boolean gameRunning;
   private boolean gamePaused;
   private boolean gameOver;

   // Key state tracking for smooth movement
   private boolean leftKeyPressed;
   private boolean rightKeyPressed;

   public GamePanel () {
      player = null;
      alienSwarm = null;
      playerBullets = new ArrayList<Bullet>();
      alienBullets = new ArrayList<Bullet>();

      soundManager = SoundManager.getInstance();
      scoreManager = ScoreManager.getInstance();

      gameRunning = false;
      gamePaused = false;
      gameOver = false;

      leftKeyPressed = false;
      rightKeyPressed = false;

      spaceBackground = ImageManager.loadImage("spaceBackground.jpg");
      gameOverScreen = ImageManager.loadImage("gameOverScreen.png");
      backgroundImage = spaceBackground;

      setBackground(Color.BLACK);
      setDoubleBuffered(true);  // Enable double buffering
   }


   public void createGameEntities() {
       // Create player ship at bottom center
       Dimension size = getSize();
       int playerX = size.width / 2 - 25;	// Center horizontally
       int playerY = size.height - 50;		// Near bottom
       player = new PlayerShip(this, playerX, playerY);

       // Create alien swarm
       alienSwarm = new AlienSwarm(this);
       alienSwarm.createAliens();

       // Clear bullets
       playerBullets.clear();
       alienBullets.clear();
   }


   public void drawGameEntities() {
        // With double-buffering, we just request a repaint
        repaint();
    }


    public void updateGameEntities(int direction) {
        if (player == null)
           return;

        player.move(direction);
        repaint();
    }


    public void updatePlayerMovement() {
        if (player == null || !gameRunning || gamePaused)
           return;

        if (leftKeyPressed && !rightKeyPressed) {
           player.move(1);  // move left
        }
        else if (rightKeyPressed && !leftKeyPressed) {
           player.move(2);  // move right
        }
    }


    public void setLeftKeyPressed(boolean pressed) {
        leftKeyPressed = pressed;
    }


    public void setRightKeyPressed(boolean pressed) {
        rightKeyPressed = pressed;
    }


   public void startGame() {
      if (gameRunning)
         return;

      gameRunning = true;
      gamePaused = false;
      gameOver = false;
      backgroundImage = spaceBackground;

      createGameEntities();
      
      // Start background music
      soundManager.playClip("background", true);

      // Start alien swarm thread
      if (alienSwarm != null) {
          alienSwarm.start();
      }
   }


   public void resetGame() {
      // Stop current game
      stopGame();
      
      // Reset game state
      gameOver = false;
      gameRunning = false;
      gamePaused = false;
      backgroundImage = spaceBackground;
      
      // Reset score manager
      scoreManager.reset();
      
      // Clear all entities
      playerBullets.clear();
      alienBullets.clear();
      player = null;
      alienSwarm = null;
      
      // Start new game
      startGame();
   }


   public void pauseGame() {
      gamePaused = !gamePaused;
      
      if (gamePaused) {
         soundManager.stopClip("background");
      }
      else {
         soundManager.playClip("background", true);
      }
   }


   public void stopGame() {
      gameRunning = false;
      soundManager.stopClip("background");

      if (alienSwarm != null) {
          alienSwarm.stopRunning();
      }

      // Stop all bullets
      for (Bullet bullet : playerBullets) {
          bullet.stopRunning();
      }
      for (Bullet bullet : alienBullets) {
          bullet.stopRunning();
      }
   }


   public void triggerGameOver() {
      gameOver = true;
      stopGame();
      backgroundImage = gameOverScreen;
      soundManager.playClip("gameOver", false);
      repaint();
   }


   public void firePlayerBullet() {
      if (player == null || !gameRunning || gamePaused)
         return;

      // Create a new bullet at player position
      int bulletX = player.getX() + player.getWidth() / 2 - 2;
      int bulletY = player.getY() - 12;
      
      Bullet bullet = new Bullet(this, bulletX, bulletY, true);
      playerBullets.add(bullet);
      bullet.start();

      soundManager.playClip("shoot", false);
   }


   /**
    * Handles alien firing - each alien independently checks if it should fire.
    * Multiple aliens can fire in a single update cycle.
    */
   public void fireAlienBullets() {
      if (alienSwarm == null || !gameRunning || gamePaused)
         return;

      // Get all aliens that want to fire this frame
      ArrayList<Alien> firingAliens = alienSwarm.getAliensToFire();
      
      // Create a bullet for each alien that wants to fire
      for (Alien alien : firingAliens) {
         int bulletX = alien.getX() + alien.getWidth() / 2 - 2;
         int bulletY = alien.getY() + alien.getHeight();

         Bullet bullet = new Bullet(this, bulletX, bulletY, false);
         alienBullets.add(bullet);
         bullet.start();
      }
   }


   public void checkCollisions() {
       if (!gameRunning || gamePaused)
          return;

       // Check player bullets vs aliens
       for (int i = playerBullets.size() - 1; i >= 0; i--) {
           Bullet bullet = playerBullets.get(i);
           if (!bullet.isActive()) {
               playerBullets.remove(i);
               continue;
           }

           Rectangle2D.Double bulletRect = bullet.getBoundingRectangle();

           // Check collision with aliens
           ArrayList<Alien> aliens = alienSwarm.getAliens();
           for (int j = aliens.size() - 1; j >= 0; j--) {
               Alien alien = aliens.get(j);
               if (bulletRect.intersects(alien.getBoundingRectangle())) {
                   // Hit!
                   bullet.setActive(false);
                   alienSwarm.removeAlien(alien);

                   scoreManager.addScore(alien.getPointValue());
                   soundManager.playClip("explosion", false);

                   playerBullets.remove(i);
                   break;
               }
           }
       }

       // Check alien bullets vs player
       for (int i = alienBullets.size() - 1; i >= 0; i--) {
           Bullet bullet = alienBullets.get(i);
           if (!bullet.isActive()) {
               alienBullets.remove(i);
               continue;
           }

           Rectangle2D.Double bulletRect = bullet.getBoundingRectangle();

            // Check collision with player
            if (player != null && bulletRect.intersects(player.getBoundingRectangle())) {
                bullet.setActive(false);
                alienBullets.remove(i);

                scoreManager.loseLife();
                soundManager.playRandomPlayerHit();

                if (scoreManager.isGameOver()) {
                   triggerGameOver();
               }
               continue;
           }
       }

       // Check if aliens reached bottom
       if (alienSwarm != null && alienSwarm.checkBottomCollision(getHeight())) {
           scoreManager.loseLife();
           if (scoreManager.isGameOver()) {
               triggerGameOver();
           }
           else {
               // Reset level
               alienSwarm.stopRunning();
               createGameEntities();
               alienSwarm.start();
           }
       }

       // Check if all aliens destroyed (level complete)
       if (alienSwarm != null && alienSwarm.isEmpty()) {
           scoreManager.nextLevel();
           alienSwarm.stopRunning();
           createGameEntities();
           alienSwarm.start();
       }
    }


   @Override
   protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;

      // Draw background image
      if (backgroundImage != null) {
          g2.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
      }

      // Draw game over screen with final score
      if (gameOver) {
          // Draw final score in center of screen
          String finalScoreText = "Final Score: " + scoreManager.getScore();
          Font scoreFont = new Font("Arial", Font.BOLD, 36);
          g2.setFont(scoreFont);
          g2.setColor(Color.WHITE);
          
          // Center the text
          int textWidth = g2.getFontMetrics().stringWidth(finalScoreText);
          int textX = (getWidth() - textWidth) / 2;
          int textY = getHeight() / 2 + 100;
          
          g2.drawString(finalScoreText, textX, textY);
          return;
      }

      // Draw all game entities on top of background
      if (!gameRunning) {
          return;
      }

      // Draw player
      if (player != null) {
          player.draw(g2);
      }

      // Draw alien swarm
      if (alienSwarm != null) {
          alienSwarm.drawAll(g2);
      }

      // Draw player bullets
      for (Bullet bullet : playerBullets) {
          if (bullet.isActive()) {
              bullet.draw(g2);
          }
      }

      // Draw alien bullets
      for (Bullet bullet : alienBullets) {
          if (bullet.isActive()) {
              bullet.draw(g2);
          }
      }
   }


   public boolean isGameRunning() {
      return gameRunning;
   }


   public boolean isGamePaused() {
      return gamePaused;
   }


   public boolean isGameOver() {
      return gameOver;
   }

}

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Color;
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
   ArrayList<Shield> shields;

   SoundManager soundManager;
   ScoreManager scoreManager;

   private Image backgroundImage;

   private boolean gameRunning;
   private boolean gamePaused;

   public GamePanel () {
      player = null;
      alienSwarm = null;
      playerBullets = new ArrayList<Bullet>();
      alienBullets = new ArrayList<Bullet>();
      shields = new ArrayList<Shield>();

      soundManager = SoundManager.getInstance();
      scoreManager = ScoreManager.getInstance();

      gameRunning = false;
      gamePaused = false;

      backgroundImage = ImageManager.loadImage("spaceBackground.jpg");

      setBackground(Color.BLACK);
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

       // Create shields
       shields.clear();
       int shieldSpacing = size.width / 5;
       int shieldY = size.height - 120;
       for (int i = 0; i < 4; i++) {
          int shieldX = shieldSpacing * (i + 1) - 30;
          Shield shield = new Shield(this, shieldX, shieldY);
          shields.add(shield);
       }

       // Clear bullets
       playerBullets.clear();
       alienBullets.clear();
   }


   public void drawGameEntities() {
       // Draw player
       if (player != null) {
           player.draw();
       }

       // Draw alien swarm
       if (alienSwarm != null) {
           alienSwarm.drawAll();
       }

       // Draw shields
       for (Shield shield : shields) {
           shield.draw();
       }

       // Draw player bullets
       for (Bullet bullet : playerBullets) {
           if (bullet.isActive()) {
               bullet.draw();
           }
       }

       // Draw alien bullets
       for (Bullet bullet : alienBullets) {
           if (bullet.isActive()) {
               bullet.draw();
           }
       }
   }


   public void updateGameEntities(int direction) {
       if (player == null)
          return;

       player.erase();
       player.move(direction);
   }


   public void startGame() {
      if (gameRunning)
         return;

      gameRunning = true;
      gamePaused = false;

      createGameEntities();
      
      // Start background music
      soundManager.playClip("background", true);

      // Start alien swarm thread
      if (alienSwarm != null) {
          alienSwarm.start();
      }
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


   public void fireAlienBullet() {
      if (alienSwarm == null || !gameRunning || gamePaused)
         return;

      ArrayList<Alien> aliens = alienSwarm.getAliens();
      if (aliens.isEmpty())
         return;

      // Pick a random alien to fire
      int index = (int) (Math.random() * aliens.size());
      Alien alien = aliens.get(index);

      int bulletX = alien.getX() + alien.getWidth() / 2 - 2;
      int bulletY = alien.getY() + alien.getHeight();

      Bullet bullet = new Bullet(this, bulletX, bulletY, false);
      alienBullets.add(bullet);
      bullet.start();
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
                  bullet.erase();
                  alien.erase();
                  alienSwarm.removeAlien(alien);

                  scoreManager.addScore(alien.getPointValue());
                  soundManager.playClip("explosion", false);

                  playerBullets.remove(i);
                  break;
              }
          }

          // Check collision with shields
          for (Shield shield : shields) {
              if (shield.checkHit(bulletRect)) {
                  bullet.setActive(false);
                  bullet.erase();
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
              bullet.erase();
              alienBullets.remove(i);

              scoreManager.loseLife();
              soundManager.playClip("playerHit", false);

              if (scoreManager.isGameOver()) {
                  stopGame();
              }
              continue;
          }

          // Check collision with shields
          for (Shield shield : shields) {
              if (shield.checkHit(bulletRect)) {
                  bullet.setActive(false);
                  bullet.erase();
                  alienBullets.remove(i);
                  break;
              }
          }
      }

      // Check if aliens reached bottom
      if (alienSwarm != null && alienSwarm.checkBottomCollision(getHeight())) {
          scoreManager.loseLife();
          if (scoreManager.isGameOver()) {
              stopGame();
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
   }


   public boolean isGameRunning() {
      return gameRunning;
   }


   public boolean isGamePaused() {
      return gamePaused;
   }

}

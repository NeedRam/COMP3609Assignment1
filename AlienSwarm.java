import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.util.ArrayList;
import java.util.Random;

public class AlienSwarm extends Thread {

   private JPanel panel;
   private ArrayList<Alien> aliens;

   private int direction;		// 1 = left, 2 = right
   private int moveDelay;
   private int dropDistance;

   private Dimension dimension;

   boolean isRunning;

   private Random random;

   // Sprite arrays for each tier
   private static final String[] TIER1_SPRITES = {"enemyTier1White.png", "enemyTier1Green.png"};
   private static final String[] TIER2_SPRITES = {"enemyTier2Gold.png", "enemyTier2White.png", "enemyTier2Green.png", "enemyTier2Black.png"};
   private static final String[] TIER3_SPRITES = {"enemyTier3Gold.png", "enemyTier3White.png", "enemyTier3Green.png", "enemyTier3Black.png"};

   public AlienSwarm (JPanel p) {
      panel = p;
      dimension = panel.getSize();

      aliens = new ArrayList<Alien>();
      direction = 2;		// start moving right
      moveDelay = 200;		// milliseconds between moves
      dropDistance = 20;
      random = new Random();
   }


   /**
    * Creates a 3-row formation of aliens:
    * Row 0 (top): Tier 3 ships (30 points) - highest fire chance
    * Row 1 (middle): Tier 2 ships (20 points) - medium fire chance
    * Row 2 (bottom): Tier 1 ships (10 points) - lowest fire chance
    */
   public void createAliens() {
      int startX = 30;
      int startY = 50;
      int spacingX = 50;
      int spacingY = 40;
      int shipsPerRow = 10;

      // Create 3 rows of aliens
      for (int row = 0; row < 3; row++) {
         int tier;
         String[] sprites;
         
          // Determine tier based on row
          // Row 0 (top, smallest y) = Tier 3, Row 1 (middle) = Tier 2, Row 2 (bottom, largest y) = Tier 1
          switch (row) {
             case 0:  // Row 0: Top row (smallest y, farthest from player)
                tier = 3;
                sprites = TIER3_SPRITES;
                break;
             case 1:  // Row 1: Middle row
                tier = 2;
                sprites = TIER2_SPRITES;
                break;
             case 2:  // Row 2: Bottom row (largest y, closest to player)
             default:
                tier = 1;
                sprites = TIER1_SPRITES;
                break;
          }

         for (int col = 0; col < shipsPerRow; col++) {
            int x = startX + col * spacingX;
            int y = startY + row * spacingY;
            
            // Randomly select a sprite from the appropriate tier
            String spriteName = sprites[random.nextInt(sprites.length)];
            
            Alien alien = new Alien(panel, x, y, tier, spriteName);
            aliens.add(alien);
         }
      }
   }


   public void drawAll(Graphics2D g2) {
      for (Alien alien : aliens) {
         alien.draw(g2);
      }
   }


   public void moveAll() {
      if (!panel.isVisible ()) return;

      dimension = panel.getSize();

      boolean hitEdge = false;

      // Check if any alien hit the edge
      for (Alien alien : aliens) {
         int x = alien.getX();
         int width = alien.getWidth();

         if (direction == 2 && x + width >= dimension.width - 10) {
            hitEdge = true;
            break;
         }
         if (direction == 1 && x <= 10) {
            hitEdge = true;
            break;
         }
      }

      // If hit edge, reverse direction and drop down
      if (hitEdge) {
         if (direction == 2) {
            direction = 1;
         }
         else {
            direction = 2;
         }

         // Drop all aliens down
         for (Alien alien : aliens) {
            alien.move(direction, true);	// drop down
         }
      }
      else {
         // Move all aliens in current direction
         for (Alien alien : aliens) {
            alien.move(direction, false);
         }
      }
   }


   public void run () {
      isRunning = true;

      try {
        while (isRunning) {
            // Check if game is paused before moving
            if (panel instanceof GamePanel) {
                GamePanel gp = (GamePanel) panel;
                if (!gp.isGamePaused()) {
                    moveAll();
                    gp.repaint();
                }
            }
            sleep (moveDelay);
         }
      }
      catch(InterruptedException e) {}
   }


   public ArrayList<Alien> getAliens() {
      return aliens;
   }


   public void removeAlien(Alien alien) {
      aliens.remove(alien);
   }


   public boolean isEmpty() {
      return aliens.isEmpty();
   }


   public void stopRunning() {
      isRunning = false;
      for (Alien alien : aliens) {
         alien.stopRunning();
      }
   }


   public boolean checkBottomCollision(int panelHeight) {
      for (Alien alien : aliens) {
         if (alien.getY() + alien.getHeight() >= panelHeight - 50) {
            return true;
         }
      }
      return false;
   }


   /**
    * Gets all aliens that want to fire this frame.
    * Each alien independently rolls its own chance to fire.
    * @return ArrayList of aliens that should fire this frame
    */
   public ArrayList<Alien> getAliensToFire() {
      ArrayList<Alien> firingAliens = new ArrayList<Alien>();
      
      for (Alien alien : aliens) {
         if (alien.tryToFire()) {
            firingAliens.add(alien);
         }
      }
      
      return firingAliens;
   }

}


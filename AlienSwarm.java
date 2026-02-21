import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.util.ArrayList;

public class AlienSwarm extends Thread {

   private JPanel panel;
   private ArrayList<Alien> aliens;

   private int direction;		// 1 = left, 2 = right
   private int moveDelay;
   private int dropDistance;

   private Color backgroundColour;
   private Dimension dimension;

   boolean isRunning;

   public AlienSwarm (JPanel p) {
      panel = p;
      dimension = panel.getSize();
      backgroundColour = panel.getBackground ();

      aliens = new ArrayList<Alien>();
      direction = 2;		// start moving right
      moveDelay = 500;		// milliseconds between moves
      dropDistance = 20;
   }


   public void createAliens() {
      int startX = 30;
      int startY = 50;
      int spacingX = 50;
      int spacingY = 40;

      // Create 5 rows x 10 columns of aliens
      for (int row = 0; row < 5; row++) {
         for (int col = 0; col < 10; col++) {
            int tier;
            // Top 2 rows are tier 3 (30 points)
            // Middle 2 rows are tier 2 (20 points)
            // Bottom row is tier 1 (10 points)
            if (row < 2) {
               tier = 3;
            }
            else if (row < 4) {
               tier = 2;
            }
            else {
               tier = 1;
            }

            int x = startX + col * spacingX;
            int y = startY + row * spacingY;
            Alien alien = new Alien(panel, x, y, tier);
            aliens.add(alien);
         }
      }
   }


   public void drawAll() {
      for (Alien alien : aliens) {
         alien.draw();
      }
   }


   public void eraseAll() {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor (backgroundColour);

      for (Alien alien : aliens) {
         g2.fill (new Rectangle2D.Double (alien.getX(), alien.getY(), alien.getWidth(), alien.getHeight()));
      }

      g.dispose();
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
            eraseAll();
            moveAll();
            drawAll();
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

}

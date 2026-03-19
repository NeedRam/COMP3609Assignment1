import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.util.Random;
import javax.swing.JPanel;

public class Alien extends Thread {

   private JPanel panel;

   private int x;
   private int y;

   private int width;
   private int height;

   private int dx;		// increment to move along x-axis
   private int dy;		// increment to move along y-axis when dropping down

   private int pointValue;	// points awarded when destroyed

   private Dimension dimension;

   boolean isRunning;

   private Image alienImage;

   private int tier;		// alien tier (1, 2, or 3)
   
   private int fireChance;	// chance to fire (1 in fireChance per update)
   private static Random fireRandom = new Random();

   public Alien (JPanel p, int xPos, int yPos, int alienTier, String spriteName) {
      panel = p;
      dimension = panel.getSize();

      x = xPos;
      y = yPos;

      tier = alienTier;

      // set dimensions, point value, and fire chance based on tier
      switch (tier) {
         case 1:
            width = 30;
            height = 30;
            pointValue = 10;
            fireChance = 100;	// Lowest fire chance (1 in 100)
            break;
         case 2:
            width = 35;
            height = 30;
            pointValue = 20;
            fireChance = 50;	// Medium fire chance (1 in 50)
            break;
         case 3:
            width = 40;
            height = 30;
            pointValue = 30;
            fireChance = 25;	// Highest fire chance (1 in 25)
            break;
         default:
            width = 30;
            height = 30;
            pointValue = 10;
            fireChance = 200;
      }

      // Load the specified sprite image
      if (spriteName != null && !spriteName.isEmpty()) {
         alienImage = ImageManager.loadImage(spriteName);
      } else {
         // Fallback to default sprites if none specified
         switch (tier) {
            case 1:
               alienImage = ImageManager.loadImage("enemyTier1White.png");
               break;
            case 2:
               alienImage = ImageManager.loadImage("enemyTier2Gold.png");
               break;
            case 3:
               alienImage = ImageManager.loadImage("enemyTier3Gold.png");
               break;
            default:
               alienImage = ImageManager.loadImage("enemyTier1White.png");
         }
      }

      // flip the image vertically so alien faces downward
      alienImage = ImageManager.flipImageVertically(alienImage);

      dx = 0;			// movement along x-axis controlled by swarm
      dy = 0;			// movement along y-axis when dropping down
   }

   // Attempts to fire a bullet.
   // Each alien independently rolls its chance to fire.
   public boolean tryToFire() {
      return fireRandom.nextInt(fireChance) == 0;
   }

   public void draw (Graphics2D g2) {
      g2.drawImage(alienImage, x, y, width, height, null);
   }

   public void move(int direction, boolean dropDown) {
      if (!panel.isVisible ()) return;

      if (direction == 1) {	// move left
          x = x - 5;
      }
      else if (direction == 2) {	// move right
          x = x + 5;
      }

      if (dropDown) {
          y = y + 20;		// drop down one row
      }
   }

   // The actual movement is controlled by AlienSwarm
   // This thread just handles the drawing loop
   public void run () {
      isRunning = true;
      try {
        while (isRunning) {
            sleep (50);
         }
      }
      catch(InterruptedException e) {}
   }

   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double (x, y, width, height);
   }

   public int getPointValue() {
      return pointValue;
   }

   public int getX() {
      return x;
   }

   public int getY() {
      return y;
   }

   public int getWidth() {
      return width;
   }

   public int getHeight() {
      return height;
   }

   public void setX(int newX) {
      x = newX;
   }

   public void setY(int newY) {
      y = newY;
   }

   public void stopRunning() {
      isRunning = false;
   }

   public int getTier() {
      return tier;
   }

   public int getFireChance() {
      return fireChance;
   }
}

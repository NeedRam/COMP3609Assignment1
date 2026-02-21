import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class Alien extends Thread {

   private JPanel panel;

   private int x;
   private int y;

   private int width;
   private int height;

   private int dx;		// increment to move along x-axis
   private int dy;		// increment to move along y-axis

   private int pointValue;	// points awarded when destroyed

   private Color backgroundColour;
   private Dimension dimension;

   boolean isRunning;

   private Image alienImage;

   private int tier;		// alien tier (1, 2, or 3)

   public Alien (JPanel p, int xPos, int yPos, int alienTier) {
      panel = p;
      dimension = panel.getSize();
      backgroundColour = panel.getBackground ();

      x = xPos;
      y = yPos;

      tier = alienTier;

      // set dimensions and point value based on tier
      switch (tier) {
         case 1:
            width = 30;
            height = 30;
            pointValue = 10;
            alienImage = ImageManager.loadImage("enemyTier1White.png");
            break;
         case 2:
            width = 35;
            height = 30;
            pointValue = 20;
            alienImage = ImageManager.loadImage("enemyTier2Gold.png");
            break;
         case 3:
            width = 40;
            height = 30;
            pointValue = 30;
            alienImage = ImageManager.loadImage("enemyTier3Gold.png");
            break;
         default:
            width = 30;
            height = 30;
            pointValue = 10;
            alienImage = ImageManager.loadImage("enemyTier1White.png");
      }

      // flip the image vertically so alien faces downward
      alienImage = ImageManager.flipImageVertically(alienImage);

      dx = 0;			// movement along x-axis controlled by swarm
      dy = 0;			// movement along y-axis when dropping down
   }


   public void draw () {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;
  
      g2.drawImage(alienImage, x, y, width, height, null);

      g.dispose();
   }


   public void erase () {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      // erase alien by drawing a rectangle on top of it
      g2.setColor (backgroundColour);
      g2.fill (new Rectangle2D.Double (x, y, width, height));

      g.dispose();
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


   public void run () {
      isRunning = true;

      // The actual movement is controlled by AlienSwarm
      // This thread just handles the drawing loop
      try {
        while (isRunning) {
            // Drawing is handled by the swarm
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

}

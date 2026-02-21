import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class Bullet extends Thread {

   private JPanel panel;
   private int x;
   private int y;
   private int width;
   private int height;

   private int dy;		// increment to move along y-axis

   private Color backgroundColour;
   private Dimension dimension;

   private boolean isPlayerBullet;	// true if fired by player, false if fired by alien
   private boolean isActive;		// true if bullet is still on screen

   private Rectangle2D.Double bullet;

   public Bullet (JPanel p, int xPos, int yPos, boolean playerBullet) {
      panel = p;
      dimension = panel.getSize();
      backgroundColour = panel.getBackground ();

      x = xPos;
      y = yPos;

      width = 4;
      height = 12;

      isPlayerBullet = playerBullet;
      isActive = true;

      if (isPlayerBullet) {
         dy = -10;	// player bullets move upward
      }
      else {
         dy = 10;	// alien bullets move downward
      }
   }


   public void draw () {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      // draw bullet as a yellow rectangle
      g2.setColor(Color.YELLOW);
      bullet = new Rectangle2D.Double(x, y, width, height);
      g2.fill(bullet);

      g.dispose();
   }


   public void erase () {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      // erase bullet by drawing a rectangle on top of it
      g2.setColor (backgroundColour);
      g2.fill (new Rectangle2D.Double (x, y, width, height));

      g.dispose();
   }


   public void move() {
      if (!panel.isVisible ()) return;

      y = y + dy;
   }


   public void run () {
      isRunning = true;

      try {
        while (isRunning && isActive) {
            erase();
            move();
            
            // check if bullet is off screen
            if (y < 0 || y > panel.getHeight()) {
                isActive = false;
            }
            else {
                draw();
            }
            sleep (30);	// sleep time controls bullet speed
         }
      }
      catch(InterruptedException e) {}
   }


   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double (x, y, width, height);
   }


   public boolean isActive() {
      return isActive;
   }


   public void setActive(boolean active) {
      isActive = active;
   }


   public boolean isPlayerBullet() {
      return isPlayerBullet;
   }

   boolean isRunning;

   public void stopRunning() {
      isRunning = false;
   }

}

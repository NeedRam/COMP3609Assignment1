import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

public class PlayerShip {

   private JPanel panel;
   private int x;
   private int y;
   private int width;
   private int height;

   private int dx;
   private int dy;

   private Rectangle2D.Double ship;

   private Dimension dimension;

   private Image shipImage;

   public PlayerShip (JPanel p, int xPos, int yPos) {
      panel = p;
      dimension = panel.getSize();

      x = xPos;
      y = yPos;

      dx = 50;	// make bigger (smaller) to increase (decrease) speed
      dy = 0;	// no movement along y-axis allowed (i.e., move left to right only)

      width = 50;
      height = 30;

      shipImage = ImageManager.loadImage("playerShip.png");
   }


   public void draw (Graphics2D g2) {
      g2.drawImage(shipImage, x, y, width, height, null);
   }


   public void move (int direction) {

      if (!panel.isVisible ()) return;
      
      dimension = panel.getSize();

      if (direction == 1) {	// move left
          x = x - dx;
	  if (x < 0)
	     x = 0;
      }
      else
      if (direction == 2) {  	// move right
          x = x + dx;
	  if (x + width > dimension.width)
	     x = dimension.width - width;
      }
   }


   public Rectangle2D.Double getBoundingRectangle() {
      return new Rectangle2D.Double (x, y, width, height);
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

}

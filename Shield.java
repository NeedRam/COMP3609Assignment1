import java.awt.Dimension;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;
import java.util.ArrayList;

public class Shield {

   private JPanel panel;
   private int x;
   private int y;
   private int width;
   private int height;

   private Color backgroundColour;
   private Dimension dimension;

   private ArrayList<Rectangle2D.Double> blocks;
   private ArrayList<Boolean> blockActive;

   private int blockSize = 8;


   public Shield (JPanel p, int xPos, int yPos) {
      panel = p;
      dimension = panel.getSize();
      backgroundColour = panel.getBackground ();

      x = xPos;
      y = yPos;

      width = 60;
      height = 40;

      blocks = new ArrayList<Rectangle2D.Double>();
      blockActive = new ArrayList<Boolean>();

      createShieldPattern();
   }


   private void createShieldPattern() {
      // Create a shield pattern using small rectangles
      // Pattern: a bunker shape with a notch at the bottom

      int[][] pattern = {
         {0, 0, 1, 1, 1, 1, 1, 1, 0, 0},
         {0, 1, 1, 1, 1, 1, 1, 1, 1, 0},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
         {1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
         {1, 1, 1, 0, 0, 0, 0, 1, 1, 1},
         {1, 1, 0, 0, 0, 0, 0, 0, 1, 1}
      };

      for (int row = 0; row < pattern.length; row++) {
         for (int col = 0; col < pattern[row].length; col++) {
            if (pattern[row][col] == 1) {
               int blockX = x + col * blockSize;
               int blockY = y + row * blockSize;
               Rectangle2D.Double block = new Rectangle2D.Double(blockX, blockY, blockSize, blockSize);
               blocks.add(block);
               blockActive.add(true);
            }
         }
      }
   }


   public void draw () {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor(Color.GREEN);

      for (int i = 0; i < blocks.size(); i++) {
         if (blockActive.get(i)) {
            g2.fill(blocks.get(i));
         }
      }

      g.dispose();
   }


   public void erase () {
      Graphics g = panel.getGraphics ();
      Graphics2D g2 = (Graphics2D) g;

      g2.setColor (backgroundColour);

      for (int i = 0; i < blocks.size(); i++) {
         if (blockActive.get(i)) {
            g2.fill(blocks.get(i));
         }
      }

      g.dispose();
   }


   public boolean checkHit (Rectangle2D.Double bulletRect) {
      for (int i = 0; i < blocks.size(); i++) {
         if (blockActive.get(i) && blocks.get(i).intersects(bulletRect)) {
            // Deactivate this block
            blockActive.set(i, false);
            return true;
         }
      }
      return false;
   }


   public Rectangle2D.Double getBoundingRectangle() {
      // Return the overall bounding rectangle of active blocks
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int maxX = Integer.MIN_VALUE;
      int maxY = Integer.MIN_VALUE;

      for (int i = 0; i < blocks.size(); i++) {
         if (blockActive.get(i)) {
            Rectangle2D.Double block = blocks.get(i);
            minX = Math.min(minX, (int) block.x);
            minY = Math.min(minY, (int) block.y);
            maxX = Math.max(maxX, (int) (block.x + block.width));
            maxY = Math.max(maxY, (int) (block.y + block.height));
         }
      }

      if (minX == Integer.MAX_VALUE) {
         return new Rectangle2D.Double(0, 0, 0, 0);	// No active blocks
      }

      return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
   }


   public boolean isDestroyed() {
      // Check if all blocks are destroyed
      for (boolean active : blockActive) {
         if (active) {
            return false;
         }
      }
      return true;
   }


   public int getX() {
      return x;
   }


   public int getY() {
      return y;
   }

}

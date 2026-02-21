import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

/**
   The ImageManager class manages the loading and processing of images.
*/

public class ImageManager {
      
   	public ImageManager () {

	}

	public static Image loadImage (String fileName) {
		return new ImageIcon(fileName).getImage();
	}

	/**
	 * Converts an Image to a BufferedImage.
	 * @param image The Image to convert
	 * @return A BufferedImage representation of the image
	 */
	public static BufferedImage toBufferedImage(Image image) {
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		
		// Create a buffered image with transparency
		BufferedImage bufferedImage = new BufferedImage(
			image.getWidth(null),
			image.getHeight(null),
			BufferedImage.TYPE_INT_ARGB
		);
		
		// Draw the image on to the buffered image
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		
		return bufferedImage;
	}

	/**
	 * Flips an image vertically (upside down).
	 * Used to flip enemy ship images for the alien invaders.
	 * @param image The image to flip
	 * @return A new Image that is flipped vertically
	 */
	public static Image flipImageVertically(Image image) {
		BufferedImage buffered = toBufferedImage(image);
		AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
		tx.translate(0, -buffered.getHeight(null));
		AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
		return op.filter(buffered, null);
	}

}

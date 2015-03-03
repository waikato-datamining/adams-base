package adams.data.image.multiimageoperation;

import adams.data.image.BufferedImageContainer;
import adams.data.image.BufferedImageHelper;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 <!-- globalinfo-start -->
 * Performs a logical AND on the pixels of the images, i.e., if both are the same, the resulting pixel is black, otherwise white.<br/>
 * Converts images automatically to type BufferedImage.TYPE_BYTE_BINARY.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class And
  extends AbstractBufferedImageMultiImageOperation {

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical AND on the pixels of the images, i.e., if both are the same, the resulting pixel is black, "
	+ "otherwise white.\n"
	+ "Converts images automatically to type BufferedImage.TYPE_BYTE_BINARY.";
  }

  /**
   * Returns the number of images that are required for the operation.
   *
   * @return		the number of images that are required, <= 0 means any number accepted
   */
  @Override
  public int numImagesRequired() {
    return 2;
  }

  /**
   * Checks the images.
   * <p/>
   * Default implementation only ensures that images are present.
   *
   * @param images	the images to check
   */
  @Override
  protected void check(BufferedImageContainer[] images) {
    super.check(images);

    if (!checkSameDimensions(images[0], images[1]))
      throw new IllegalStateException(
	"Both images need to have the same dimensions: "
	  + images[0].getWidth() + "x" + images[0].getHeight()
	  + " != "
	  + images[1].getWidth() + "x" + images[1].getHeight());
  }

  /**
   * Performs the actual processing of the images.
   *
   * @param images	the images to process
   * @return		the generated image(s)
   */
  @Override
  protected BufferedImageContainer[] doProcess(BufferedImageContainer[] images) {
    BufferedImageContainer[]	result;
    BufferedImage 		img0;
    BufferedImage		img1;
    BufferedImage		output;
    int				x;
    int				y;
    int				and;
    int				match;
    int				mismatch;

    result   = new BufferedImageContainer[1];
    img0     = BufferedImageHelper.convert(images[0].getImage(), BufferedImage.TYPE_BYTE_BINARY);
    img1     = BufferedImageHelper.convert(images[1].getImage(), BufferedImage.TYPE_BYTE_BINARY);
    output   = BufferedImageHelper.deepCopy(img0);
    match    = Color.BLACK.getRGB();
    mismatch = Color.WHITE.getRGB();
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	and = (img0.getRGB(x, y) == img1.getRGB(x, y)) ? match : mismatch;
	output.setRGB(x, y, and);
      }
    }
    result[0] = new BufferedImageContainer();
    result[0].setImage(output);

    return result;
  }
}

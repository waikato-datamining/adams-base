package adams.data.image.multiimageoperation;

import adams.data.image.BufferedImageContainer;
import adams.data.report.Report;

import java.awt.*;

/**
 <!-- globalinfo-start -->
 * Performs a logical AND on the pixels of the images, i.e., if pixels match, the 'match' color is used, otherwise the 'mismatch' color.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-match &lt;java.awt.Color&gt; (property: match)
 * &nbsp;&nbsp;&nbsp;The color to use for pixels where the corresponding pixels in the images 
 * &nbsp;&nbsp;&nbsp;match.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-mismatch &lt;java.awt.Color&gt; (property: mismatch)
 * &nbsp;&nbsp;&nbsp;The color to use for pixels where the corresponding pixels in the images 
 * &nbsp;&nbsp;&nbsp;don't match.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class And
  extends AbstractBufferedImageMultiImageOperation {

  /** color to use when the pixels match. */
  protected Color m_Match;

  /** color to use when the pixels don't match. */
  protected Color m_Mismatch;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Performs a logical AND on the pixels of the images, i.e., if pixels match, "
	+ "the 'match' color is used, otherwise the 'mismatch' color.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "match", "match",
      Color.BLACK);

    m_OptionManager.add(
      "mismatch", "mismatch",
      Color.WHITE);
  }

  /**
   * Sets the color to use for matches.
   *
   * @param value	the color
   */
  public void setMatch(Color value) {
    m_Match = value;
    reset();
  }

  /**
   * Returns the color in use for matches.
   *
   * @return		the color
   */
  public Color getMatch() {
    return m_Match;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String matchTipText() {
    return "The color to use for pixels where the corresponding pixels in the images match.";
  }

  /**
   * Sets the color to use for mismatches.
   *
   * @param value	the color
   */
  public void setMismatch(Color value) {
    m_Mismatch = value;
    reset();
  }

  /**
   * Returns the color in use for mismatches.
   *
   * @return		the color
   */
  public Color getMismatch() {
    return m_Mismatch;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String mismatchTipText() {
    return "The color to use for pixels where the corresponding pixels in the images don't match.";
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
    int				x;
    int				y;
    int				and;
    int				match;
    int				mismatch;

    result    = new BufferedImageContainer[1];
    match     = m_Match.getRGB();
    mismatch  = m_Mismatch.getRGB();
    result[0] = (BufferedImageContainer) images[0].getClone();
    result[0].getNotes().clear();
    result[0].setReport(new Report());
    for (y = 0; y < images[0].getHeight(); y++) {
      for (x = 0; x < images[0].getWidth(); x++) {
	and = (images[0].getImage().getRGB(x, y) == images[1].getImage().getRGB(x, y)) ? match : mismatch;
	result[0].getImage().setRGB(x, y, and);
      }
    }

    return result;
  }
}

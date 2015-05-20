/*
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
  *    JPEGWriter.java
  *    Copyright (C) 2005,2009-2013 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import adams.core.management.LocaleHelper;

/**
 <!-- globalinfo-start -->
 * Outputs JPEG images.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D (property: debug)
 * &nbsp;&nbsp;&nbsp;If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to save the image to.
 * &nbsp;&nbsp;&nbsp;default: .
 * </pre>
 *
 * <pre>-scaling (property: scalingEnabled)
 * &nbsp;&nbsp;&nbsp;If set to true, then scaling will be used.
 * </pre>
 *
 * <pre>-scale-x &lt;double&gt; (property: XScale)
 * &nbsp;&nbsp;&nbsp;The scaling factor for the X-axis.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 * <pre>-scale-y &lt;double&gt; (property: YScale)
 * &nbsp;&nbsp;&nbsp;The scaling factor for the Y axis.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 * <pre>-custom-dimensions (property: useCustomDimensions)
 * &nbsp;&nbsp;&nbsp;Whether to use custom dimensions or use the component's ones.
 * </pre>
 *
 * <pre>-custom-width &lt;int&gt; (property: customWidth)
 * &nbsp;&nbsp;&nbsp;The custom width.
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 * <pre>-custom-height &lt;int&gt; (property: customHeight)
 * &nbsp;&nbsp;&nbsp;The custom height.
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 *
 * <pre>-background &lt;java.awt.Color&gt; (property: background)
 * &nbsp;&nbsp;&nbsp;The background color.
 * &nbsp;&nbsp;&nbsp;default: #ffffff
 * </pre>
 *
 * <pre>-type &lt;RGB|GRAY&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The type of image to create.
 * &nbsp;&nbsp;&nbsp;default: RGB
 * </pre>
 *
 * <pre>-quality &lt;double&gt; (property: quality)
 * &nbsp;&nbsp;&nbsp;The JPEG quality (0.0 - 1.0).
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * </pre>
 *
 <!-- options-end -->
 * <br><br>
 * Based on weka.gui.visualize.JPEGWriter
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class JPEGWriter
  extends BufferedImageBasedWriter {

  /** for serialization. */
  private static final long serialVersionUID = -6501256101213777499L;

  /** the quality of the image. */
  protected double m_Quality;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs JPEG images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "quality", "quality",
	    1.0);
  }

  /**
   * returns the name of the writer, to display in the FileChooser.
   *
   * @return 		the name of the writer
   */
  @Override
  public String getDescription() {
    return "JPEG-Image";
  }

  /**
   * returns the extensions (incl. ".") of the output format, to use in the
   * FileChooser.
   *
   * @return 		the file extensions
   */
  @Override
  public String[] getExtensions() {
    return new String[]{".jpg", ".jpeg"};
  }

  /**
   * sets the quality the JPEG is saved in.
   *
   * @param value 	the quality to use
   */
  public void setQuality(double value) {
    m_Quality = value;
  }

  /**
   * returns the quality the JPEG will be stored in.
   *
   * @return 		the quality
   */
  public double getQuality() {
    return m_Quality;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String qualityTipText() {
    return "The JPEG quality (0.0 - 1.0).";
  }

  /**
   * generates the actual output.
   *
   * @throws Exception	if something goes wrong
   */
  @Override
  public void generateOutput() throws Exception {
    ImageWriter 	writer;
    Iterator 		iter;
    ImageOutputStream 	ios;
    ImageWriteParam 	param;

    // get jpeg writer
    writer = null;
    iter   = ImageIO.getImageWritersByFormatName(getExtensions()[0].replace(".", ""));
    if (iter.hasNext())
      writer = (ImageWriter) iter.next();
    else
      throw new Exception("No writer available for " + getDescription() + "!");

    // prepare output file
    ios = ImageIO.createImageOutputStream(getFile().getAbsoluteFile());
    writer.setOutput(ios);

    // set the quality
    param = new JPEGImageWriteParam(LocaleHelper.getSingleton().getDefault());
    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT) ;
    param.setCompressionQuality((float) getQuality());

    // write the image
    writer.write(null, new IIOImage(createBufferedImage(), null, null), param);

    // cleanup
    ios.flush();
    writer.dispose();
    ios.close();
  }
}

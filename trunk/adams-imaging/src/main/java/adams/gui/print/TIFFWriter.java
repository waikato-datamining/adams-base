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
  *    TIFFWriter.java
  *    Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
  *
  */

package adams.gui.print;

import java.util.HashMap;
import java.util.Map;

import org.apache.sanselan.ImageFormat;
import org.apache.sanselan.Sanselan;
import org.apache.sanselan.SanselanConstants;
import org.apache.sanselan.formats.tiff.constants.TiffConstants;

/**
 <!-- globalinfo-start -->
 * Outputs TIFF images.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-file &lt;adams.core.io.PlaceholderFile&gt; (property: file)
 * &nbsp;&nbsp;&nbsp;The file to save the image to.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
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
 * <pre>-compress (property: compress)
 * &nbsp;&nbsp;&nbsp;If set to true, the image will be compressed (LZW).
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TIFFWriter
  extends BufferedImageBasedWriter {

  /** for serialization. */
  private static final long serialVersionUID = 3324725056211114889L;

  /** whether to compress the image. */
  protected boolean m_Compress;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Outputs TIFF images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "compress", "compress",
	    false);
  }

  /**
   * returns the name of the writer, to display in the FileChooser.
   *
   * @return 		the name of the writer
   */
  public String getDescription() {
    return "TIFF-Image";
  }

  /**
   * returns the extensions (incl. ".") of the output format, to use in the
   * FileChooser.
   *
   * @return 		the file extensions
   */
  public String[] getExtensions() {
    return new String[]{".tif", ".tiff"};
  }

  /**
   * Sets whether to compress the image.
   *
   * @param value 	if true then the image will be compressed
   */
  public void setCompress(boolean value) {
    m_Compress = value;
  }

  /**
   * Returns whether to compress the image.
   *
   * @return 		true if the image gets compressed
   */
  public boolean getCompress() {
    return m_Compress;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String compressTipText() {
    return "If set to true, the image will be compressed (LZW).";
  }

  /**
   * generates the actual output.
   *
   * @throws Exception	if something goes wrong
   */
  public void generateOutput() throws Exception {
    ImageFormat format;
    Map 	params;
    
    format = ImageFormat.IMAGE_FORMAT_TIFF;

    params = new HashMap();
    if (m_Compress)
      params.put(SanselanConstants.PARAM_KEY_COMPRESSION, new Integer(TiffConstants.TIFF_COMPRESSION_UNCOMPRESSED));
    else
      params.put(SanselanConstants.PARAM_KEY_COMPRESSION, new Integer(TiffConstants.TIFF_COMPRESSION_LZW));
    
    Sanselan.writeImage(createBufferedImage(), getFile().getAbsoluteFile(), format, params);
  }
}

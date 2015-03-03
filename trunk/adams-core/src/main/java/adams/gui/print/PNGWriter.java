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
 * PNGWriter.java
 * Copyright (C) 2007,2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.print;

import javax.imageio.ImageIO;

/**
 <!-- globalinfo-start -->
 * Outputs PNG images.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
 <!-- options-end -->
 * <p/>
 * Based on weka.gui.visualize.PNGWriter
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PNGWriter
  extends BufferedImageBasedWriter {

  /** for serialization. */
  private static final long serialVersionUID = 6023849818803480377L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Outputs PNG images.";
  }

  /**
   * returns the name of the writer, to display in the FileChooser.
   *
   * @return 		the name of the writer
   */
  public String getDescription() {
    return "PNG-Image";
  }

  /**
   * returns the extensions (incl. ".") of the output format, to use in the
   * FileChooser.
   *
   * @return 		the file extensions
   */
  public String[] getExtensions() {
    return new String[]{".png"};
  }

  /**
   * generates the actual output.
   *
   * @throws Exception	if something goes wrong
   */
  public void generateOutput() throws Exception {
    ImageIO.write(createBufferedImage(), "png", getFile().getAbsoluteFile());
  }
}

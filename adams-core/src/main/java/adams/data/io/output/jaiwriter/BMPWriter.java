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
 * BMPWriter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output.jaiwriter;

import adams.core.Utils;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.bmp.BMPImageWriteParam;

/**
 * Manages writing BMP images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BMPWriter
  extends AbstractJAIWriter {

  private static final long serialVersionUID = 1366719044935629546L;

  /** whether to write the data in top-down order. */
  protected boolean m_TopDown;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages writing BMP images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "top-down", "topDown",
      false);
  }

  /**
   * Sets whether to write the data in top-down order.
   *
   * @param value 	true if top-down
   */
  public void setTopDown(boolean value) {
    m_TopDown = value;
    reset();
  }

  /**
   * Returns whether to write the data in top-down order.
   *
   * @return 		true if top-down
   */
  public boolean getTopDown() {
    return m_TopDown;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String topDownTipText() {
    return "Whether to write the data in top-down order.";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"bmp"};
  }

  /**
   * Returns the configured writer.
   *
   * @return		the configured writer
   * @throws Exception	if determining of writer fails
   */
  @Override
  public ImageWriter getWriter() throws Exception {
    return ImageIO.getImageWritersByFormatName("bmp").next();
  }

  /**
   * Returns the parameters for the writer.
   *
   * @return		the parameters
   * @throws Exception	if determining of writer or setting of parameters fails
   */
  @Override
  public ImageWriteParam getParameters() throws Exception {
    ImageWriteParam 	result;
    ImageWriter 	writer;

    writer = getWriter();
    result = writer.getDefaultWriteParam();
    result.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
    if (result instanceof BMPImageWriteParam)
      ((BMPImageWriteParam) result).setTopDown(m_TopDown);
    else
      getLogger().warning("Couldn't configure 'TopDown'! Expected parameters object to be of type " + Utils.classToString(BMPImageWriteParam.class) + " but got instead: " + Utils.classToString(result));

    return result;
  }

  /**
   * Returns whether the alpha channel must be removed.
   *
   * @return		true if to remove
   */
  @Override
  public boolean removeAlphaChannel() {
    return true;
  }
}

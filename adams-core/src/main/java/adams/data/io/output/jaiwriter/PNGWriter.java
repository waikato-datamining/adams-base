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
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output.jaiwriter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

/**
 * Manages writing PNG images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class PNGWriter
  extends AbstractJAIWriter {

  private static final long serialVersionUID = 1366719044935629546L;

  /** the compression (0=none to 1=high). */
  protected float m_Compression;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages writing PNG images.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "compression", "compression",
      0.5f, 0f, 1.0f);
  }

  /**
   * Sets the compression to use.
   *
   * @param value 	the compression (0=none to 1=high)
   */
  public void setCompression(float value) {
    if (getOptionManager().isValid("compression", value)) {
      m_Compression = value;
      reset();
    }
  }

  /**
   * Returns the compression to use.
   *
   * @return 		the compression (0=none to 1=high)
   */
  public float getCompression() {
    return m_Compression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String compressionTipText() {
    return "The compression to use (0=none to 1=high).";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"png"};
  }

  /**
   * Returns the configured writer.
   *
   * @return		the configured writer
   * @throws Exception	if determining of writer fails
   */
  @Override
  public ImageWriter getWriter() throws Exception {
    return ImageIO.getImageWritersByFormatName("png").next();
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
    result.setCompressionQuality(m_Compression);

    return result;
  }

  /**
   * Returns whether the alpha channel must be removed.
   *
   * @return		true if to remove
   */
  @Override
  public boolean removeAlphaChannel() {
    return false;
  }
}

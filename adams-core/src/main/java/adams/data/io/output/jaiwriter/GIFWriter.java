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
 * GIFWriter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.io.output.jaiwriter;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

/**
 * Manages writing GIF images.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GIFWriter
  extends AbstractJAIWriter {

  private static final long serialVersionUID = 1366719044935629546L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Manages writing GIF images.";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension(s) (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"gif"};
  }

  /**
   * Returns the configured writer.
   *
   * @return		the configured writer
   * @throws Exception	if determining of writer fails
   */
  @Override
  public ImageWriter getWriter() throws Exception {
    return ImageIO.getImageWritersByFormatName("gif").next();
  }

  /**
   * Returns the parameters for the writer.
   *
   * @return		the parameters
   * @throws Exception	if determining of writer or setting of parameters fails
   */
  @Override
  public ImageWriteParam getParameters() throws Exception {
    return getWriter().getDefaultWriteParam();
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

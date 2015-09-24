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

/**
 * OpenIMAJImageWriter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.data.io.input.AbstractImageReader;
import adams.data.io.input.OpenIMAJImageReader;
import adams.data.openimaj.OpenIMAJImageContainer;
import org.openimaj.image.ImageUtilities;

/**
 <!-- globalinfo-start -->
 * OpenIMAJ image writer for: png, *<br>
 * For more information see:<br>
 * http:&#47;&#47;www.openimaj.org&#47;
 * <br><br>
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OpenIMAJImageWriter
  extends AbstractImageWriter<OpenIMAJImageContainer> {

  private static final long serialVersionUID = -8205381422416173255L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
	"OpenIMAJ image writer for: " + Utils.flatten(getFormatExtensions(), ", ")
	+ "\n"
	+ "For more information see:\n"
	+ "http://www.openimaj.org/";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "OpenIMAJ images";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"png", "*"};
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractImageReader getCorrespondingReader() {
    return new OpenIMAJImageReader();
  }

  /**
   * Performs the actual writing of the image file.
   *
   * @param file	the file to write to
   * @param cont	the image container to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, OpenIMAJImageContainer cont) {
    try {
      ImageUtilities.write(cont.getImage(), file.getAbsoluteFile());
      return null;
    }
    catch (Exception e) {
      return Utils.handleException(this, "Failed to write OpenIMAJ image to: " + file, e);
    }
  }
}

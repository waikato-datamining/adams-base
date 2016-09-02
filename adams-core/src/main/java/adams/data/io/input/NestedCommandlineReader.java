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
 * NestedCommandlineReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.PlaceholderFile;
import adams.core.option.NestedConsumer;
import adams.data.io.output.AbstractObjectWriter;
import adams.data.io.output.NestedCommandlineWriter;

import java.util.logging.Level;

/**
 * Loads objects using the commandline store in the file (nested format).
 * Works only with ADAMS objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedCommandlineReader
  extends AbstractObjectReader {

  private static final long serialVersionUID = -5427726959059688884L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads files with commandlines (nested format).\n"
	+ "NB: Works only with ADAMS objects.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Nested Commandline Object";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"cmdnested"};
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public AbstractObjectWriter getCorrespondingWriter() {
    return new NestedCommandlineWriter();
  }

  /**
   * Performs the actual reading of the object file.
   *
   * @param file	the file to read
   * @return		the object, null if failed to read
   */
  @Override
  protected Object doRead(PlaceholderFile file) {
    try {
      return new NestedConsumer().fromFile(file);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read object from: " + file, e);
      return null;
    }
  }
}

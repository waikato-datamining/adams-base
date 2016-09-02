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
 * NestedCommandlineWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.io.PlaceholderFile;
import adams.core.option.NestedProducer;
import adams.core.option.OptionHandler;
import adams.data.io.input.AbstractObjectReader;
import adams.data.io.input.NestedCommandlineReader;

/**
 * Writes the commandline of objects to disk (in nested format).
 * NB: Only works with ADAMS objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NestedCommandlineWriter
  extends AbstractObjectWriter {

  private static final long serialVersionUID = 7242878829736390245L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Writes commandlines of objects (in nested format).\n"
	+ "NB: Only works with ADAMS objects.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new NestedCommandlineReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new NestedCommandlineReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractObjectReader getCorrespondingReader() {
    return new NestedCommandlineReader();
  }

  /**
   * Performs the actual writing of the object file.
   *
   * @param file	the file to write to
   * @param obj	        the object to write
   * @return		null if successfully written, otherwise error message
   */
  @Override
  protected String doWrite(PlaceholderFile file, Object obj) {
    String		result;
    NestedProducer	producer;

    result = null;

    producer = new NestedProducer();
    producer.setOutputClasspath(false);
    producer.produce((OptionHandler) obj);
    if (!producer.write(file.getAbsolutePath()))
      result = "Failed to write object to: " + file;

    return result;
  }
}

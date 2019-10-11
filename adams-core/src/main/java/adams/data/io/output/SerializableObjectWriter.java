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
 * SerializableObjectWriter.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.SerializableObject;
import adams.core.SerializationHelper;
import adams.core.Utils;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.data.io.input.AbstractObjectReader;
import adams.data.io.input.SerializableObjectReader;

import java.util.logging.Level;

/**
 * Writes objects that implement {@link SerializableObject}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SerializableObjectWriter
  extends AbstractObjectWriter {

  private static final long serialVersionUID = 7242878829736390245L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes objects that implement " + SerializableObject.class.getName() +".";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SerializableObjectReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SerializableObjectReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public AbstractObjectReader getCorrespondingReader() {
    return new SerializableObjectReader();
  }

  /**
   * Performs checks.
   *
   * @param obj	the object to check
   */
  @Override
  protected void check(Object obj) {
    super.check(obj);

    if (!(obj instanceof SerializableObject))
      throw new IllegalStateException(
        "Object does not implement " + SerializableObject.class.getName() + ": " + Utils.classToString(obj));
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
    SerializableObject	sobj;
    Object[]		data;

    result = null;

    try {
      sobj = (SerializableObject) obj;
      data = ((SerializableObject) obj).retrieveSerializationSetup();
      SerializationHelper.write(file.getAbsolutePath(), new Object[]{sobj.getClass().getName(), data});
    }
    catch (Exception e) {
      result = "Failed to write object to: " + file + "\n" + LoggingHelper.throwableToString(e);
      getLogger().log(Level.SEVERE, "Failed to write object to: " + file, e);
    }

    return result;
  }
}

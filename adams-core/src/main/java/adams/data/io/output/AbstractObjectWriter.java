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
 * AbstractObjectWriter.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.ClassLister;
import adams.core.io.FileFormatHandler;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.io.input.AbstractObjectReader;

/**
 * Ancestor for object writers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractObjectWriter
  extends AbstractOptionHandler
  implements FileFormatHandler {

  /** for serialization. */
  private static final long serialVersionUID = -6170592942529644703L;
  
  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public abstract AbstractObjectReader getCorrespondingReader();
  
  /**
   * Returns whether the writer is actually available.
   * 
   * @return		true if available and ready to use
   */
  public boolean isAvailable() {
    return true;
  }

  /**
   * Performs checks.
   * 
   * @param obj	the object to check
   */
  protected void check(Object obj) {
    if (obj == null)
      throw new IllegalStateException("No object provided!");
  }
  
  /**
   * Performs the actual writing of the object file.
   * 
   * @param file	the file to write to
   * @param obj	        the object to write
   * @return		null if successfully written, otherwise error message
   */
  protected abstract String doWrite(PlaceholderFile file, Object obj);
  
  /**
   * Writes the object file.
   * 
   * @param file	the file to write to
   * @param obj	        the object to write
   * @return		null if successfully written, otherwise error message
   */
  public String write(PlaceholderFile file, Object obj) {
    check(obj);
    return doWrite(file, obj);
  }

  /**
   * Returns a list with classnames of writers.
   *
   * @return		the writer classnames
   */
  public static String[] getWriters() {
    return ClassLister.getSingleton().getClassnames(AbstractObjectWriter.class);
  }
}

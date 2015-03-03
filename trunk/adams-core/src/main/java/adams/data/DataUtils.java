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
 * DataUtils.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data;

import java.io.File;

import adams.core.Constants;
import adams.core.io.FileUtils;
import adams.data.id.DatabaseIDHandler;
import adams.data.id.IDHandler;

/**
 * Helper class for data-related classes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DataUtils {

  /**
   * Generates a filename.
   *
   * @param dir		the directory
   * @param name	the name of the file (no path)
   * @param extension	the extension to add, including the dot, can be null
   * @return		the filename
   */
  public static String createFilename(File dir, String name, String extension) {
    return createFilename(dir, name, null, extension);
  }

  /**
   * Generates a filename.
   *
   * @param dir		the directory
   * @param name	the name of the file (no path)
   * @param suffix	an optional suffix to append before the extension, can be null
   * @param extension	the extension to add, including the dot, can be null
   * @return		the filename
   */
  public static String createFilename(File dir, String name, String suffix, String extension) {
    String	result;

    result = dir.getAbsolutePath();
    if (dir.isDirectory())
      result += File.separator;
    result += FileUtils.createFilename(name, "_");
    if (suffix != null)
      result += suffix;
    if (extension != null) {
      // avoid duplicate extensions
      if (!result.endsWith(extension))
	result += extension;
    }

    return result;
  }

  /**
   * Generates a filename for the handler.
   *
   * @param dir		the directory
   * @param handler	the handler to generate the filename for
   * @param extension	the extension to add, including the dot
   * @return		the filename
   */
  public static String createFilename(File dir, DatabaseIDHandler handler, String extension) {
    return createFilename(dir, handler, null, extension);
  }

  /**
   * Generates a filename for the handler.
   *
   * @param dir		the directory
   * @param handler	the handler to generate the filename for
   * @param suffix	an optional suffix to append before the extension, can be null
   * @param extension	the extension to add, including the dot, can be null
   * @return		the filename
   */
  public static String createFilename(File dir, DatabaseIDHandler handler, String suffix, String extension) {
    return createFilename(dir, "" + handler.getDatabaseID(), suffix, extension);
  }

  /**
   * Generates a filename for the handler.
   *
   * @param dir		the directory
   * @param handler	the handler to generate the filename for
   * @param extension	the extension to add, including the dot, can be null
   * @return		the filename
   */
  public static String createFilename(File dir, IDHandler handler, String extension) {
    return createFilename(dir, handler, null, extension);
  }

  /**
   * Generates a filename for the handler.
   *
   * @param dir		the directory
   * @param handler	the handler to generate the filename for
   * @param suffix	an optional suffix to append before the extension, can be null
   * @param extension	the extension to add, including the dot, can be null
   * @return		the filename
   */
  public static String createFilename(File dir, IDHandler handler, String suffix, String extension) {
    return createFilename(dir, handler.getID(), suffix, extension);
  }

  /**
   * Generates a filename for the handler. Must be either an IDHandler
   * or DatabaseIDHandler.
   *
   * @param dir		the directory
   * @param handler	the handler to generate the filename for
   * @param extension	the extension to add, including the dot, can be null
   * @return		the filename or null if not implementing correct interfaces
   * @see		IDHandler
   * @see		DatabaseIDHandler
   */
  public static String createFilename(File dir, Object handler, String extension) {
    return createFilename(dir, handler, null, extension);
  }

  /**
   * Generates a filename for the handler. Must be either an IDHandler
   * or DatabaseIDHandler.
   *
   * @param dir		the directory
   * @param handler	the handler to generate the filename for
   * @param suffix	an optional suffix to append before the extension, can be null
   * @param extension	the extension to add, including the dot, can be null
   * @return		the filename or null if not implementing correct interfaces
   * @see		IDHandler
   * @see		DatabaseIDHandler
   */
  public static String createFilename(File dir, Object handler, String suffix, String extension) {
    String	result;

    result = null;

    if ((handler instanceof DatabaseIDHandler) && (((DatabaseIDHandler) handler).getDatabaseID() != Constants.NO_ID))
      result = createFilename(dir, (DatabaseIDHandler) handler, suffix, extension);
    else if ((handler instanceof IDHandler) && (((IDHandler) handler).getID() != null))
      result = createFilename(dir, (IDHandler) handler, suffix, extension);

    return result;
  }
}

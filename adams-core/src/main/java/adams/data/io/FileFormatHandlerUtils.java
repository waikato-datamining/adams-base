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
 * FileFormatHandlerUtils.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io;

import adams.core.ClassLister;
import adams.core.io.FileFormatHandler;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class for {@link FileFormatHandler}.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileFormatHandlerUtils {

  /** the logger in use. */
  private static Logger LOGGER = LoggingHelper.getLogger(FileFormatHandlerUtils.class);

  /**
   * Returns the list of handlers that can handle the specified file.
   * The classes must implement the {@link FileFormatHandler} interface.
   *
   * @param superclass	the superclass of the handlers
   * @param filename 	the file to get handler(s) for
   * @return		the list of handlers
   */
  public static List<Class> getHandlerForFile(Class superclass, String filename) {
    return getHandlerForExtension(superclass, FileUtils.getExtension(filename));
  }

  /**
   * Returns the list of handlers that can handle the specified file.
   * The classes must implement the {@link FileFormatHandler} interface.
   *
   * @param superclass	the superclass of the handlers
   * @param file 	the file to get handler(s) for
   * @return		the list of handlers
   */
  public static List<Class> getHandlerForFile(Class superclass, File file) {
    return getHandlerForExtension(superclass, FileUtils.getExtension(file));
  }

  /**
   * Returns the list of handlers that can handle the specified extension.
   * The classes must implement the {@link FileFormatHandler} interface.
   *
   * @param superclass	the superclass of the handlers
   * @param extension	the extension to check (no dot)
   * @return		the list of handlers
   */
  public static List<Class> getHandlerForExtension(Class superclass, String extension) {
    List<Class>		result;
    Class[]		classes;
    FileFormatHandler	handler;
    String[]		exts;

    result = new ArrayList<>();
    classes = ClassLister.getSingleton().getClasses(superclass);
    for (Class cls: classes) {
      try {
	handler = (FileFormatHandler) cls.newInstance();
	exts    = handler.getFormatExtensions();
	for (String ext: exts) {
	  if (ext.startsWith("."))
	    ext = ext.substring(1);
	  if (ext.equals(extension)) {
	    result.add(cls);
	    break;
	  }
	}
      }
      catch (Exception e) {
	LOGGER.log(Level.SEVERE, "Failed to process class: " + cls.getName(), e);
      }
    }

    return result;
  }
}

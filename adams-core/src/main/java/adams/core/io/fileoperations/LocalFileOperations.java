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
 * LocalFileOperations.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;

import java.util.logging.Level;

/**
 * Local file operations.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocalFileOperations
  extends AbstractFileOperations {

  private static final long serialVersionUID = -8864793052017151953L;

  /**
   * Checks whether the given operation is supported.
   *
   * @param op		the operation to check
   * @return		true if supported
   */
  public boolean isSupported(Operation op) {
    switch (op) {
      case COPY:
      case MOVE:
      case RENAME:
      case DELETE:
	return true;
      default:
	throw new IllegalStateException("Unhandled operation: " + op);
    }
  }

  /**
   * Copies a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String copy(String source, String target) {
    try {
      if (!FileUtils.copy(new PlaceholderFile(source), new PlaceholderFile(target)))
	return "Failed to copy file: " + source + " -> " + target;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to copy file: " + source + " -> " + target, e);
    }
    return null;
  }

  /**
   * Moves a file.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String move(String source, String target) {
    try {
      if (!FileUtils.move(new PlaceholderFile(source), new PlaceholderFile(target)))
	return "Failed to move file: " + source + " -> " + target;
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to move file: " + source + " -> " + target, e);
    }
    return null;
  }

  /**
   * Renames a file.
   *
   * @param source	the source file (old)
   * @param target	the target file (new)
   * @return		null if successful, otherwise error message
   */
  public String rename(String source, String target) {
    if (!new PlaceholderFile(source).renameTo(new PlaceholderFile(target)))
      return "Failed to rename file: " + source + " -> " + target;
    return null;
  }

  /**
   * Deletes a file.
   *
   * @param file	the file to delete
   * @return		null if successful, otherwise error message
   */
  public String delete(String file) {
    if (!FileUtils.delete(file))
      return "Failed to delete file: " + file;
    return null;
  }
}

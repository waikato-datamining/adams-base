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
 * FileOperations.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileoperations;

/**
 * Interface for file operation facades.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface FileOperations {

  /**
   * Checks whether the given operation is supported.
   *
   * @param op		the operation to check
   * @return		true if supported
   */
  public boolean isSupported(Operation op);

  /**
   * Copies a file/dir.
   *
   * @param source	the source file/dir
   * @param target	the target file/dir
   * @return		null if successful, otherwise error message
   */
  public String copy(String source, String target);

  /**
   * Duplicates a file/dir.
   *
   * @param source	the source file
   * @param target	the target file
   * @return		null if successful, otherwise error message
   */
  public String duplicate(String source, String target);

  /**
   * Moves a file/dir.
   *
   * @param source	the source file/dir
   * @param target	the target file/dir
   * @return		null if successful, otherwise error message
   */
  public String move(String source, String target);

  /**
   * Renames a file/dir.
   *
   * @param source	the source file/dir (old)
   * @param target	the target file/dir (new)
   * @return		null if successful, otherwise error message
   */
  public String rename(String source, String target);

  /**
   * Deletes a file/dir.
   *
   * @param path	the file/dir to delete
   * @return		null if successful, otherwise error message
   */
  public String delete(String path);

  /**
   * Creates the directory.
   *
   * @param dir		the directory to create
   * @return		null if successful, otherwise error message
   */
  public String mkdir(String dir);

  /**
   * Checks whether the path is a directory.
   *
   * @param path	the path to check
   * @return		true if path exists and is a directory
   */
  public boolean isDir(String path);
}

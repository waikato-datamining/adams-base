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
 * RegExpFileSearchHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filesearch;

import adams.core.exception.ExceptionHandler;

/**
 * Interface for file search handlers that handle regular expression matching.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface RegExpFileSearchHandler
  extends FileSearchHandler {

  /**
   * Searches the specified file.
   *
   * @param file	the file to search
   * @param searchText	the search text
   * @param regExp	true if the search text is a regular expression
   * @param handler 	for handling exceptions, can be null
   * @return		true if the search text was found
   */
  public boolean search(String file, String searchText, boolean regExp, boolean caseSensitive, ExceptionHandler handler);
}

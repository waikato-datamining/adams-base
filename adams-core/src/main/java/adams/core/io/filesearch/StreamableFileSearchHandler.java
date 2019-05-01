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
 * StreamableFileSearchHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filesearch;

import adams.core.StoppableWithFeedback;
import adams.core.exception.ExceptionHandler;

import java.io.Reader;

/**
 * Interface for file search handlers that can operate on character streams
 * obtained from readers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface StreamableFileSearchHandler
  extends StoppableWithFeedback {

  /**
   * Searches the specified character stream.
   *
   * @param reader	the reader to search
   * @param searchText	the search text
   * @param handler 	for handling exceptions, can be null
   * @return		true if the search text was found
   */
  public boolean searchStream(Reader reader, String searchText, boolean caseSensitive, ExceptionHandler handler);
}

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
 * FindUtils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io;

import adams.core.exception.ExceptionHandler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;

/**
 * Methods for locating data in files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FindUtils {

  /**
   * Searches the specified file.
   *
   * @param file	the file to search
   * @param searchText	the search text
   * @param regExp	true if the search text is a regular expression
   * @param handler 	for handling exceptions
   * @return		true if the search text was found
   */
  public static boolean searchFile(String file, String searchText, boolean regExp, boolean caseSensitive, ExceptionHandler handler) {
    FileReader freader;
    BufferedReader breader;
    String		line;
    Pattern pattern;
    boolean		result;

    result  = false;
    freader = null;
    breader = null;
    pattern = null;
    if (regExp)
      pattern = Pattern.compile(searchText);
    else if (!caseSensitive)
      searchText = searchText.toLowerCase();

    try {
      freader = new FileReader(file);
      breader = new BufferedReader(freader);
      while ((line = breader.readLine()) != null) {
        if (!caseSensitive)
          line = line.toLowerCase();
        if (pattern != null)
          result = pattern.matcher(line).matches();
        else
          result = line.contains(searchText);
        if (result)
          break;
      }
    }
    catch (Exception e) {
      if (handler != null)
	handler.handleException("Failed to search: " + file, e);
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(freader);
    }

    return result;
  }

}

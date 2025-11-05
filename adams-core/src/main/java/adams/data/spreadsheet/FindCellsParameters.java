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
 * FindCellsParameters.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.data.spreadsheet;

import java.util.regex.Pattern;

/**
 * Container class for search parameters.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class FindCellsParameters {

  /** the search string. */
  public String search;

  /** whether the search is case-sensitive. */
  public boolean caseSensitive;

  /** whether the search string is a regular expression. */
  public boolean regExp;

  /** the compiled regexp pattern. */
  public Pattern pattern;

  /**
   * Initializes the iterator. Case-insensitive search, no regexp.
   *
   * @param search		the search string
   */
  public FindCellsParameters(String search) {
    this(search, false, false);
  }

  /**
   * Initializes the iterator.
   *
   * @param search		the search string
   * @param caseSensitive	whether search is case-sensitive (ignored when using regexp)
   * @param regExp		whether the search string is a regular expression
   */
  public FindCellsParameters(String search, boolean caseSensitive, boolean regExp) {
    this.search        = search;
    this.caseSensitive = caseSensitive;
    this.regExp        = regExp;
    if (this.regExp)
      this.pattern = Pattern.compile(this.search);
    else if (!this.caseSensitive)
      this.search = this.search.toLowerCase();
  }
}

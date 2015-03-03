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
 * SuffixOnlyField.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

/**
 * Interface for fields that only have a suffix.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SuffixOnlyField
  extends Comparable {

  /**
   * Checks whether the name is a compound one.
   *
   * @return		true if name is a compound one
   */
  public boolean isCompound();

  /**
   * Returns the suffix for compound fields.
   *
   * @return		the suffix, null if not compound field
   */
  public String getSuffix();
}

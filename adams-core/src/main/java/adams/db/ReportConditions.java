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
 * ReportConditions.java
 * Copyright (C) 2009-2010 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

/**
 * Interface for conditions classes that use the reports table
 * for retrieving information.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ReportConditions {

  /**
   * Sets whether to exclude data containers with reports flagged as dummies
   * or not.
   *
   * @param value 	if true then dummies are excluded
   */
  public void setExcludeDummies(boolean value);

  /**
   * Returns whether data containers with reports flagged as dummies are
   * excluded or not.
   *
   * @return 		true if dummies are to be excluded
   */
  public boolean getExcludeDummies();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String excludeDummiesTipText();

  /**
   * Sets whether to include only data containers with reports flagged as
   * dummies or not.
   *
   * @param value 	if true then only dummies are included
   */
  public void setOnlyDummies(boolean value);

  /**
   * Returns whether only data containers with reports flagged as dummies are
   * included or not.
   *
   * @return 		true if only dummies are to be included
   */
  public boolean getOnlyDummies();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onlyDummiesTipText();
}

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
 * CheckProcessor.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

/**
 * Processors that perform checks.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface CheckProcessor {

  /**
   * Returns the string that explains the warnings.
   * 
   * @return		the heading for the warnings, null if not available
   */
  public String getWarningHeader();
  
  /**
   * Returns the warnings, if any, resulting from the check.
   * 
   * @return		the warnings, null if no warnings.
   */
  public String getWarnings();
}
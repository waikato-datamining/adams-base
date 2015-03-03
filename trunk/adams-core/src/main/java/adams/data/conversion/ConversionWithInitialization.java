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
 * ConversionWithInitialization.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;


/**
 * For conversions that require an initialization first.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ConversionWithInitialization
  extends Conversion {
  
  /**
   * Checks whether we still need to perform a setup.
   * 
   * @return		true if {@link #setUp()} call is necessary
   */
  public boolean requiresSetUp();
  
  /**
   * Performs some initializations.
   * 
   * @return		null if successful, otherwise error message
   */
  public String setUp();
}

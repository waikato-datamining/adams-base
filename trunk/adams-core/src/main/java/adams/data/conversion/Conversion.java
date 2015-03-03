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
 * Conversion.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.io.Serializable;

import adams.core.CleanUpHandler;
import adams.core.QuickInfoSupporter;
import adams.core.Stoppable;

/**
 * Interface for conversion schemes.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface Conversion 
  extends Serializable, CleanUpHandler, QuickInfoSupporter, Stoppable {

  /**
   * Sets the owner of this conversion.
   *
   * @param value	the owner
   */
  public void setOwner(Object value);

  /**
   * Returns the owner of this conversion.
   *
   * @return		the owner, null if none set
   */
  public Object getOwner();

  /**
   * Sets the original data to convert.
   *
   * @param value	the data to convert
   * @see		StreamConversion
   */
  public void setInput(Object value);
  
  /**
   * The currently set input data to convert.
   *
   * @return		the data to convert, can be null if not yet set
   */
  public Object getInput();

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  public Class accepts();

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  public Class generates();

  /**
   * Performs the conversion.
   *
   * @return		null if everything worked otherwise the error message
   */
  public String convert();

  /**
   * Returns the generated output.
   *
   * @return		the output, null if none produced yet
   */
  public Object getOutput();
}

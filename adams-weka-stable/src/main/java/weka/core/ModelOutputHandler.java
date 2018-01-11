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
 * ModelOutputHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package weka.core;

/**
 * Interface for classes that allow user to decide whether to output a
 * model in string representation or not.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ModelOutputHandler {

  /**
   * Sets whether to output the model with the toString() method or not.
   *
   * @param value 	true if to suppress model output
   */
  public void setSuppressModelOutput(boolean value);

  /**
   * Returns whether to output the model with the toString() method or not.
   *
   * @return 		the label index
   */
  public boolean getSuppressModelOutput();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String suppressModelOutputTipText();
}

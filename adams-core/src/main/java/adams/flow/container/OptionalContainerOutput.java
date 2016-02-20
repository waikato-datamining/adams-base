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
 * OptionalContainerOutput.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.container;

/**
 * Interface for actors that have optional container output.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface OptionalContainerOutput {

  /**
   * Sets whether to output a container rather than just the data.
   *
   * @param value 	true if to output the container
   */
  public void setOutputContainer(boolean value);

  /**
   * Returns whether to output a container rather than just the data.
   *
   * @return 		true if to output the container
   */
  public boolean getOutputContainer();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputContainerTipText();
}

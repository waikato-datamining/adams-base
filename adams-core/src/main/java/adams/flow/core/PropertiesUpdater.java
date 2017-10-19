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
 * PropertiesUpdater.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

import adams.core.VariableName;
import adams.core.base.BaseString;

/**
 * Interface for actors that update one or more properties of a Java object
 * using variables.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface PropertiesUpdater
  extends Actor {

  /**
   * Sets the properties to update.
   *
   * @param value	the properties
   */
  public void setProperties(BaseString[] value);

  /**
   * Returns the properties to update.
   *
   * @return		the properties
   */
  public BaseString[] getProperties();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String propertiesTipText();

  /**
   * Sets the variables to use.
   *
   * @param value	the variables
   */
  public void setVariableNames(VariableName[] value);

  /**
   * Returns the variables to use.
   *
   * @return		the variables
   */
  public VariableName[] getVariableNames();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableNamesTipText();
}

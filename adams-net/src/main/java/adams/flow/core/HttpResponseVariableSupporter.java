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
 * HttpResponseVariableSupporter.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.VariableName;

/**
 * Interface for classes that support storing status code and/or body directly in variables.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface HttpResponseVariableSupporter {

  /**
   * Sets the (optional) variable name for storing the status code in.
   *
   * @param value	the variable name
   */
  public void setVariableStatusCode(VariableName value);

  /**
   * Returns the (optional) variable name for storing the status code in.
   *
   * @return		the variable name
   */
  public VariableName getVariableStatusCode();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableStatusCodeTipText();

  /**
   * Sets the (optional) variable name for storing the body in.
   *
   * @param value	the variable name
   */
  public void setVariableBody(VariableName value);

  /**
   * Returns the (optional) variable name for storing the body in.
   *
   * @return		the variable name
   */
  public VariableName getVariableBody();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String variableBodyTipText();
}

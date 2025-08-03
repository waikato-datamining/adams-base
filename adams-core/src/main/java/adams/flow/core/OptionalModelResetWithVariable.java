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
 * OptionalModelResetWithVariable.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.VariableName;

/**
 * Interface for flow components that allow (optionally)
 * a model to be reset via a monitored variable.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface OptionalModelResetWithVariable {

  /**
   * Sets whether to use a variable to monitor for changes in order
   * to reset the model.
   *
   * @param value	true if to use monitor variable
   */
  public void setUseModelResetVariable(boolean value);

  /**
   * Returns whether to use a variable to monitor for changes in order
   * to reset the model.
   *
   * @return		true if to use monitor variable
   */
  public boolean getUseModelResetVariable();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useModelResetVariableTipText();

  /**
   * Sets the variable to monitor for changes in order to reset the model.
   *
   * @param value	the variable
   */
  public void setModelResetVariable(VariableName value);

  /**
   * Returns the variable to monitor for changes in order to reset the model.
   *
   * @return		the variable
   */
  public VariableName getModelResetVariable();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String modelResetVariableTipText();
}

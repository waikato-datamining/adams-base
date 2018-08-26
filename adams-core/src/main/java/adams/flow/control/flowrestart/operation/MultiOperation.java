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
 * MultiOperation.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.control.flowrestart.operation;

import adams.flow.control.Flow;

/**
 * Executes multiple restart operations, one after the other.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MultiOperation
  extends AbstractRestartOperation {

  private static final long serialVersionUID = 5721670854550551855L;

  /** the operations to execute. */
  protected AbstractRestartOperation[] m_Operations;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes multiple restart operations, one after the other.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "operation", "operations",
      new AbstractRestartOperation[0]);
  }

  /**
   * Sets the operations to execute.
   *
   * @param value	the operations
   */
  public void setOperations(AbstractRestartOperation[] value) {
    m_Operations = value;
    reset();
  }

  /**
   * Returns the operations to execute.
   *
   * @return		the operations
   */
  public AbstractRestartOperation[] getOperations() {
    return m_Operations;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String operationsTipText() {
    return "The restart operations to execute sequentially.";
  }

  /**
   * Restarts the flow.
   *
   * @param flow	the flow to handle
   * @return		null if successfully restarted, otherwise the error message
   */
  @Override
  public String restart(Flow flow) {
    String	result;
    int		i;

    result = null;

    stopFlow(flow);

    for (i = 0; i < m_Operations.length; i++) {
      if (isLoggingEnabled())
        getLogger().info("Executing restart operation #" + (i+1) + ": " + m_Operations[i]);
      result = m_Operations[i].restart(flow);
      if (result != null) {
        getLogger().severe("Restart operation #" + (i+1) + " generated: " + result);
	break;
      }
    }

    return result;
  }
}

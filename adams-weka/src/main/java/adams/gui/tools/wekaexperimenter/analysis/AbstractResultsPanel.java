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
 * AbstractResultsPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekaexperimenter.analysis;

import adams.core.CleanUpHandler;
import adams.core.CloneHandler;
import adams.gui.core.BasePanel;
import weka.experiment.ResultMatrix;

/**
 * Ancestor for displaying the results of an analysis.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractResultsPanel
  extends BasePanel
  implements CloneHandler<AbstractResultsPanel>, CleanUpHandler {

  private static final long serialVersionUID = -2760167038696560756L;

  /** the underlying matrix. */
  protected ResultMatrix m_Matrix;

  /**
   * Returns the name to display in the GUI.
   *
   * @return		the name
   */
  public abstract String getResultsName();

  /**
   * Displays the results.
   */
  protected abstract void doDisplay();

  /**
   * Displays the results.
   *
   * @param matrix	the matrix with the results
   */
  public void display(ResultMatrix matrix) {
    m_Matrix = matrix;
    doDisplay();
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public abstract AbstractResultsPanel getClone();

  /**
   * Just returns the name of the panel.
   *
   * @return		the name
   */
  @Override
  public String toString() {
    return getResultsName();
  }

  /**
   * Cleans up data structures, frees up memory.
   * <br>
   * Default implementation does nothing.
   */
  public void cleanUp() {
  }
}

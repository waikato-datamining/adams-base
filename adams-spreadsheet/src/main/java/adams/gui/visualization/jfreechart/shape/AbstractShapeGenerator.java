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
 * AbstractShapeGenerator.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.shape;

import adams.core.option.AbstractOptionHandler;

import java.awt.Shape;

/**
 * Generates a shape to be used for marking data points (eg in a scatter plot).
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractShapeGenerator
  extends AbstractOptionHandler {

  private static final long serialVersionUID = -6405519989360228879L;

  /**
   * Hook method for checks.
   *
   * @return		null if checks passed successful, otherwise error message
   */
  protected String check() {
    return null;
  }

  /**
   * Generates the shape.
   *
   * @return		the shape, null if none generated
   */
  protected abstract Shape doGenerate();

  /**
   * Generates the shape.
   *
   * @return		the shape, null if none generated
   * @throws IllegalStateException	if checks fail
   * @see		#check()
   */
  public Shape generate() {
    String	msg;

    msg = check();
    if (msg != null)
      throw new IllegalStateException(msg);
    return doGenerate();
  }
}

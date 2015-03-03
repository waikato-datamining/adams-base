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
 * ComponentSupplier.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink;

import javax.swing.JComponent;

/**
 * Interface for sinks that supply a component. This can be used for saving
 * this component.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ComponentSupplier {

  /**
   * Supplies the component. May get called even before actor has been executed.
   *
   * @return		the component, null if none available
   */
  public JComponent supplyComponent();
}

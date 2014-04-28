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
 * ContainerWithComponent.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.container;

import java.awt.Component;

/**
 * Interface for Containers that also store a Swing component associated with
 * the payload.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <C> the type of component
 */
public interface ContainerWithComponent<C extends Component> {

  /**
   * Checks whether a component is stored.
   *
   * @return		true if a component is available
   */
  public boolean hasComponent();

  /**
   * Sets the component to associate.
   *
   * @param value	the component
   */
  public void setComponent(C value);

  /**
   * Returns the stored component.
   *
   * @return		the component, null if none available
   */
  public C getComponent();
}

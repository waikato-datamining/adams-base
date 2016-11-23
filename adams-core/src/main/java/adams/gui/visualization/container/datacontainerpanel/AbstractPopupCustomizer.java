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
 * AbstractPopupCustomizer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.container.datacontainerpanel;

import adams.data.container.DataContainer;
import adams.gui.visualization.container.AbstractContainer;
import adams.gui.visualization.container.AbstractContainerManager;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;

import java.io.Serializable;

/**
 * Ancestor for customizers for the data container panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractPopupCustomizer<T extends DataContainer, M extends AbstractContainerManager, C extends AbstractContainer>
  implements Serializable {

  private static final long serialVersionUID = 6847657392180231208L;

  /**
   * The name.
   *
   * @return		the name
   */
  public abstract String getName();

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  public abstract String getGroup();

  /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
  public abstract boolean handles(DataContainerPanelWithContainerList<T,M,C> panel);
}

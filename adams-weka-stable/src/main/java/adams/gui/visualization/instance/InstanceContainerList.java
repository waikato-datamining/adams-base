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
 * InstanceListPanel.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.instance;

import adams.gui.visualization.container.AbstractContainerList;

/**
 * A panel that lists Instances in a JTable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstanceContainerList
  extends AbstractContainerList<InstanceContainerManager,InstanceContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 3265940348362833587L;

  /**
   * Creates a new model.
   *
   * @param manager	the manager to use for the model
   * @return		the new model
   */
  protected InstanceContainerModel createModel(InstanceContainerManager manager) {
    return new InstanceContainerModel(manager);
  }
}

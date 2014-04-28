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
 * TimeseriesContainerList.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.gui.visualization.container.AbstractSearchableContainerList;
import adams.gui.visualization.container.ContainerTable;

/**
 * A class encapsulating containers tailored for timeseries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesContainerList<M extends TimeseriesContainerManager, C extends TimeseriesContainer>
  extends AbstractSearchableContainerList<M, C> {

  /** for serialization. */
  private static final long serialVersionUID = -1049275455980966385L;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle("Data");
  }

  /**
   * Creates a new table.
   *
   * @return		the new table
   */
  @Override
  protected ContainerTable<M, C> createTable() {
    return new TimeseriesContainerTable();
  }

  /**
   * Creates a new model.
   *
   * @param manager	the manager to use for the model
   * @return		the new model
   */
  @Override
  protected TimeseriesContainerModel createModel(M manager) {
    return new TimeseriesContainerModel(manager);
  }
}

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
 * TimeseriesContainerModel.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.timeseries;

import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.container.ContainerModel;

/**
 * A model for displaying the currently loaded timeseries.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesContainerModel<M extends TimeseriesContainerManager, C extends TimeseriesContainer>
  extends ContainerModel<M, C> {

  /** for serialization. */
  private static final long serialVersionUID = -7545555938539538659L;

  /**
   * Initializes the model.
   *
   * @param manager	the managing object to obtain the data from
   */
  public TimeseriesContainerModel(ContainerListManager<M> manager) {
    super((manager == null) ? null : manager.getContainerManager());
  }

  /**
   * Initializes the model.
   *
   * @param manager	the manager to obtain the data from
   */
  public TimeseriesContainerModel(M manager) {
    super(manager);
  }

  /**
   * Initializes members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Generator           = new TimeseriesContainerDisplayIDGenerator();
    m_ColumnNameGenerator = new TimeseriesContainerTableColumnNameGenerator();
  }
}
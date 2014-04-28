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
 * ReportContainerModel.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.report;

import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.container.ContainerModel;

/**
 * A model for displaying the currently loaded reports.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportContainerModel
  extends ContainerModel<ReportContainerManager, ReportContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 8164076143115071049L;

  /**
   * Initializes the model.
   *
   * @param manager	the managing object to obtain the data from
   */
  public ReportContainerModel(ContainerListManager<ReportContainerManager> manager) {
    super((manager == null) ? null : manager.getContainerManager());
  }

  /**
   * Initializes the model.
   *
   * @param manager	the manager to obtain the data from
   */
  public ReportContainerModel(ReportContainerManager manager) {
    super(manager);
  }

  /**
   * Initializes members.
   */
  protected void initialize() {
    super.initialize();

    m_Generator           = new ReportContainerDisplayIDGenerator();
    m_ColumnNameGenerator = new ReportContainerTableColumnNameGenerator();
  }

  /**
   * Returns whether a cell is editable or not.
   *
   * @param rowIndex		the row
   * @param columnIndex	the column
   * @return			true if editable
   */
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return false;
  }
}
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
 * SpreadSheetRowContainerModel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.spreadsheet;

import adams.gui.visualization.container.ContainerListManager;
import adams.gui.visualization.container.ContainerModel;

/**
 * A model for displaying the currently loaded SpreadSheetRow objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4584 $
 */
public class SpreadSheetRowContainerModel
  extends ContainerModel<SpreadSheetRowContainerManager, SpreadSheetRowContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -6259301933663814155L;

  /**
   * Initializes the model.
   *
   * @param manager	the managing object to obtain the data from
   */
  public SpreadSheetRowContainerModel(ContainerListManager<SpreadSheetRowContainerManager> manager) {
    super((manager == null) ? null : manager.getContainerManager());
  }

  /**
   * Initializes the model.
   *
   * @param manager	the manager to obtain the data from
   */
  public SpreadSheetRowContainerModel(SpreadSheetRowContainerManager manager) {
    super(manager);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_Generator           = new SpreadSheetRowContainerDisplayIDGenerator();
    m_ColumnNameGenerator = new SpreadSheetRowContainerTableColumnNameGenerator();
  }
}
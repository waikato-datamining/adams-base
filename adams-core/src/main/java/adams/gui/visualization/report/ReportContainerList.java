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
 * ReportContainerList.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.report;

import adams.gui.visualization.container.AbstractContainerList;

/**
 * A class encapsulating containers tailored for reports.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportContainerList
  extends AbstractContainerList<ReportContainerManager, ReportContainer> {

  /** for serialization. */
  private static final long serialVersionUID = -1049275455980966385L;

  /**
   * Creates a new model.
   *
   * @param manager	the manager to base the model on
   * @return		the model
   */
  protected ReportContainerModel createModel(ReportContainerManager manager) {
    return new ReportContainerModel(manager);
  }
}

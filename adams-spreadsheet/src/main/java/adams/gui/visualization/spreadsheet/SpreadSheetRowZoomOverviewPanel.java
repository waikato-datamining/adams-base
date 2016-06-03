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
 * SpreadSheetRowZoomOverviewPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.spreadsheet;

import adams.gui.visualization.container.AbstractDataContainerZoomOverviewPanel;

/**
 * Panel that shows the zoom in the TIC panel as overlay.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4914 $
 */
public class SpreadSheetRowZoomOverviewPanel
  extends AbstractDataContainerZoomOverviewPanel<SpreadSheetRowPanel, SpreadSheetRowLinePaintlet, SpreadSheetRowZoomOverviewPaintlet, SpreadSheetRow, SpreadSheetRowContainerManager> {

  /** for serialization. */
  private static final long serialVersionUID = -5141649373267221710L;

  /**
   * Creates a new zoom paintlet.
   * 
   * @return		the paintlet
   */
  protected SpreadSheetRowZoomOverviewPaintlet newZoomPaintlet() {
    return new SpreadSheetRowZoomOverviewPaintlet();
  }
}

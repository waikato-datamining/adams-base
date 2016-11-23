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
 * Reports.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.timeseries.containerlistpopup;

import adams.data.timeseries.Timeseries;
import adams.gui.visualization.container.ContainerTable;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.containerlistpopup.AbstractContainerListPopupCustomizer;
import adams.gui.visualization.timeseries.TimeseriesContainer;
import adams.gui.visualization.timeseries.TimeseriesContainerManager;
import adams.gui.visualization.timeseries.TimeseriesPanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.util.List;

/**
 * Views the reports of the timeseries containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Reports
  extends AbstractContainerListPopupCustomizer<Timeseries, TimeseriesContainerManager, TimeseriesContainer>{

  private static final long serialVersionUID = -7796583803269239174L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Reports";
  }

  /**
   * The group this customizer belongs to.
   *
   * @return		the group
   */
  @Override
  public String getGroup() {
    return "view";
  }

   /**
   * Checks whether this action can handle the panel.
   *
   * @param panel	the panel to check
   * @return		true if handled
   */
 @Override
  public boolean handles(DataContainerPanelWithContainerList<Timeseries, TimeseriesContainerManager, TimeseriesContainer> panel) {
    return (panel instanceof TimeseriesPanel);
  }

  /**
   * Returns a popup menu for the table of the container list.
   *
   * @param panel	the affected panel
   * @param table	the affected table
   * @param row		the row the mouse is currently over
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(DataContainerPanelWithContainerList<Timeseries, TimeseriesContainerManager, TimeseriesContainer> panel, ContainerTable<TimeseriesContainerManager, TimeseriesContainer> table, int row, JPopupMenu menu) {
    JMenuItem				item;
    final List<TimeseriesContainer>	visibleConts;

    visibleConts = panel.getTableModelContainers(true);
    item = new JMenuItem("Reports");
    item.addActionListener((ActionEvent e) -> ((TimeseriesPanel) panel).showReports(visibleConts));
  }
}

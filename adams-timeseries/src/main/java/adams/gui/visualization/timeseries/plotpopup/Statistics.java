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
 * Statistics.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.timeseries.plotpopup;

import adams.data.statistics.InformativeStatistic;
import adams.data.timeseries.Timeseries;
import adams.gui.core.GUIHelper;
import adams.gui.visualization.container.DataContainerPanelWithContainerList;
import adams.gui.visualization.container.datacontainerpanel.plotpopup.AbstractPlotPopupCustomizer;
import adams.gui.visualization.timeseries.TimeseriesContainer;
import adams.gui.visualization.timeseries.TimeseriesContainerManager;
import adams.gui.visualization.timeseries.TimeseriesPanel;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Generates statistics from the visible containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Statistics
  extends AbstractPlotPopupCustomizer<Timeseries, TimeseriesContainerManager, TimeseriesContainer> {

  private static final long serialVersionUID = 3295471324320509106L;

  /**
   * The name.
   *
   * @return		the name
   */
  @Override
  public String getName() {
    return "Statistics";
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
   * @param e		the mouse event
   * @param menu	the popup menu to customize
   */
  @Override
  public void customize(DataContainerPanelWithContainerList<Timeseries, TimeseriesContainerManager, TimeseriesContainer> panel, MouseEvent e, JPopupMenu menu) {
    JMenuItem				item;
    final List<TimeseriesContainer> 	visibleConts;

    visibleConts = panel.getTableModelContainers(true);
    item = new JMenuItem("Statistics", GUIHelper.getIcon("statistics.png"));
    item.addActionListener((ActionEvent ae) -> {
      List<InformativeStatistic> stats = new ArrayList<>();
      for (TimeseriesContainer cont: visibleConts)
	stats.add(cont.getData().toStatistic());
      ((TimeseriesPanel) panel).showStatistics(stats);
    });
    menu.add(item);
  }
}

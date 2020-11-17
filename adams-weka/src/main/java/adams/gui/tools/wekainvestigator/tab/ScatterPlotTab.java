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
 * ScatterPlotTab.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.Index;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.tools.wekainvestigator.InvestigatorPanel;
import adams.gui.tools.wekainvestigator.data.DataContainer;
import adams.gui.visualization.stats.paintlet.ScatterPaintletCross;
import adams.gui.visualization.stats.scatterplot.AbstractScatterPlotOverlay;
import adams.gui.visualization.stats.scatterplot.Coordinates;
import adams.gui.visualization.stats.scatterplot.ScatterPlot;
import adams.ml.data.InstancesView;

import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * For plotting attributes against each other.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ScatterPlotTab
  extends AbstractInvestigatorTabWithEditableDataTable
  implements ChangeListener {

  private static final long serialVersionUID = -94945456385486233L;

  /** the cache for the plots. */
  protected Map<DataContainer,ScatterPlot> m_PlotCache;

  /** the cache for the last update cache. */
  protected Map<DataContainer,Date> m_TimestampCache;

  /** the default max column width. */
  protected Integer m_MaxColWidth;

  /** the currently displayed panel. */
  protected ScatterPlot m_CurrentPanel;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PlotCache = new HashMap<>();
    m_TimestampCache = new HashMap<>();
    m_MaxColWidth    = null;
    m_CurrentPanel   = null;
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Scatter plot";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "scatterplot2.png";
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.SINGLE_SELECTION;
  }

  /**
   * Notifies the tab that the data changed.
   *
   * @param e		the event
   */
  public void dataChanged(WekaInvestigatorDataEvent e) {
    Set<DataContainer>	cached;
    Set<DataContainer>	current;
    DataContainer	con;

    super.dataChanged(e);

    // update cache
    // 1. remove containers no longer present
    cached  = new HashSet<>(m_PlotCache.keySet());
    current = new HashSet<>();
    for (DataContainer cont: getOwner().getData())
      current.add(cont);
    cached.removeAll(current);
    for (DataContainer cont: cached) {
      m_PlotCache.remove(cont);
      m_TimestampCache.remove(cont);
    }
    // 2. remove containers that were modified
    for (DataContainer cont: current) {
      if (m_TimestampCache.containsKey(cont)) {
	if (!cont.lastUpdated().equals(m_TimestampCache.get(cont))) {
	  m_PlotCache.remove(cont);
	  m_TimestampCache.remove(cont);
	}
      }
    }
    // other modified containers, e.g. UNDO
    if (e.getType() == WekaInvestigatorDataEvent.ROWS_MODIFIED) {
      for (int row: e.getRows()) {
	con = getData().get(row);
	m_PlotCache.remove(con);
	m_TimestampCache.remove(con);
      }
    }

    displayData();
  }

  /**
   * Gets called when the user changes the selection.
   */
  protected void dataTableSelectionChanged() {
    super.dataTableSelectionChanged();
    displayData();
  }

  /**
   * Displays the data.
   */
  protected void displayData() {
    DataContainer		cont;
    int				index;
    InstancesView		view;

    if (m_MaxColWidth == null)
      m_MaxColWidth = InvestigatorPanel.getProperties().getInteger("Data.MaxColWidth", 100);

    if ((m_Table.getRowCount() > 0) && (m_Table.getSelectedRow() > -1)) {
      index = m_Table.getSelectedRow();
      cont  = getData().get(index);
      // table
      if (m_PlotCache.containsKey(cont)) {
	m_CurrentPanel = m_PlotCache.get(cont);
      }
      else {
        view = new InstancesView(cont.getData());
	m_CurrentPanel = new ScatterPlot();
	m_CurrentPanel.setData(view);
	m_CurrentPanel.setXIndex(new Index("1"));
	m_CurrentPanel.setYIndex(new Index("2"));
	m_CurrentPanel.setPaintlet(new ScatterPaintletCross());
	m_CurrentPanel.setOverlays(new AbstractScatterPlotOverlay[]{
	  new Coordinates(),
	});
	m_CurrentPanel.reset();
	m_PlotCache.put(cont, m_CurrentPanel);
	m_TimestampCache.put(cont, new Date(cont.lastUpdated().getTime()));
      }
      m_PanelData.removeAll();
      m_PanelData.add(m_CurrentPanel, BorderLayout.CENTER);
      if (m_SplitPane.isBottomComponentHidden()) {
	m_SplitPane.setDividerLocation(m_DefaultDataTableHeight);
	m_SplitPane.setBottomComponentHidden(false);
      }
    }
    else {
      m_PanelData.removeAll();
      m_SplitPane.setBottomComponentHidden(true);
    }
    invalidate();
    revalidate();
    doLayout();
    repaint();
  }

  /**
   * Gets called when the data in the table changed.
   *
   * @param e		the event
   */
  @Override
  public void stateChanged(ChangeEvent e) {
    if (getSelectedRows().length > 0)
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_MODIFIED, getSelectedRows()[0]));
    else
      fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.TABLE_CHANGED));
  }
}

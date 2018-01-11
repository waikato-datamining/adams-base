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
 * BoxPlotTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.Range;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.visualization.stats.boxplot.BoxPlotManager;
import adams.ml.data.InstancesView;

import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

/**
 * Visualizes the selected dataset as box plot.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoxPlotTab
  extends AbstractInvestigatorTabWithDataTable {

  private static final long serialVersionUID = -4106630131554796889L;

  /** the number of plots side-by-side. */
  public final static int NUM_PLOTS_HORIZONTAL = 3;

  /** the last boxplot. */
  protected BoxPlotManager m_LastBoxPlot;

  /** the current boxplot. */
  protected BoxPlotManager m_CurrentBoxPlot;

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Box plot";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "boxplot.png";
  }

  /**
   * Returns whether a readonly table is used.
   *
   * @return		true if readonly
   */
  @Override
  protected boolean hasReadOnlyTable() {
    return true;
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  @Override
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.SINGLE_SELECTION;
  }

  @Override
  public void dataChanged(WekaInvestigatorDataEvent e) {
    super.dataChanged(e);
    dataTableSelectionChanged();
  }

  /**
   * Gets called when the user changes the selection.
   */
  @Override
  protected void dataTableSelectionChanged() {
    int			index;
    InstancesView	view;

    if ((m_Table.getRowCount() > 0) && (m_Table.getSelectedRow() > -1)) {
      m_LastBoxPlot    = m_CurrentBoxPlot;
      index            = m_Table.getSelectedRow();
      view             = new InstancesView(getData().get(index).getData());
      m_CurrentBoxPlot = new BoxPlotManager();
      // transfer settings
      if (m_LastBoxPlot != null) {
        m_CurrentBoxPlot.setBoxWidth(m_LastBoxPlot.getBoxWidth());
	m_CurrentBoxPlot.setBoxHeight(m_LastBoxPlot.getBoxHeight());
	m_CurrentBoxPlot.setAxisWidth(m_LastBoxPlot.getAxisWidth());
	m_CurrentBoxPlot.setNumHorizontal(m_LastBoxPlot.getNumHorizontal());
	m_CurrentBoxPlot.setSameAxis(m_LastBoxPlot.getSameAxis());
	m_CurrentBoxPlot.setRange(m_LastBoxPlot.getRange());
	m_CurrentBoxPlot.setFill(m_LastBoxPlot.getFill());
	m_CurrentBoxPlot.setColor(m_LastBoxPlot.getColor());
      }
      else {
        m_CurrentBoxPlot.setBoxWidth(200);
	m_CurrentBoxPlot.setBoxHeight(200);
	m_CurrentBoxPlot.setAxisWidth(50);
	m_CurrentBoxPlot.setNumHorizontal(NUM_PLOTS_HORIZONTAL);
	m_CurrentBoxPlot.setSameAxis(false);
	// to avoid deadlocks with many attributes
	if (view.getColumnCount() <= NUM_PLOTS_HORIZONTAL * NUM_PLOTS_HORIZONTAL)
	  m_CurrentBoxPlot.setRange(new Range(Range.ALL));
	else
	  m_CurrentBoxPlot.setRange(new Range("1-" + (NUM_PLOTS_HORIZONTAL*NUM_PLOTS_HORIZONTAL)));
	m_CurrentBoxPlot.setFill(true);
	m_CurrentBoxPlot.setColor(Color.RED);
      }
      m_CurrentBoxPlot.setData(new InstancesView(getData().get(index).getData()));
      m_CurrentBoxPlot.reset();
      m_PanelData.removeAll();
      m_PanelData.add(m_CurrentBoxPlot, BorderLayout.CENTER);
      m_PanelData.setPreferredSize(new Dimension(200, m_DefaultDataTableHeight));
      if (m_SplitPane.isBottomComponentHidden()) {
	m_SplitPane.setBottomComponentHidden(false);
	m_SplitPane.setDividerLocation(m_DefaultDataTableHeight);
      }
    }
    else {
      m_PanelData.removeAll();
      m_SplitPane.setBottomComponentHidden(true);
    }
    invalidate();
    revalidate();
    doLayout();
  }
}

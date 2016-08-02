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
 * MatrixTab.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import weka.gui.visualize.MatrixPanel;

import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;

/**
 * Visualizes the selected dataset as matrix plot.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MatrixTab
  extends AbstractInvestigatorTabWithDataTable {

  private static final long serialVersionUID = -4106630131554796889L;

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Matrix";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "matrixplot.png";
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

  /**
   * Gets called when the used changes the selection.
   */
  @Override
  protected void dataTableSelectionChanged() {
    int			index;
    MatrixPanel		panel;

    if (m_Table.getSelectedRow() > -1) {
      index = m_Table.getSelectedRow();
      panel = new MatrixPanel();
      panel.setInstances(getData().get(index).getData());
      m_PanelData.removeAll();
      m_PanelData.add(panel, BorderLayout.CENTER);
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

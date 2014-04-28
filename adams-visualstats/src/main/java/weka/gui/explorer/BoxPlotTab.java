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
 * BoxPlotTab.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.explorer;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import weka.core.Instances;
import weka.gui.explorer.Explorer.ExplorerPanel;
import adams.gui.visualization.stats.boxplot.BoxPlotManager;

/**
 * Class that calls the box plot manager from within the explorer.
 *
 * @author msf8
 * @version $Revision$
 */
public class BoxPlotTab
extends JPanel
implements ExplorerPanel{

  /** for serialization */
  private static final long serialVersionUID = -88655386498376666L;

  /** the parent frame */
  protected Explorer m_Explorer = null;

  /**  Instances object containing the data */
  protected Instances m_Instances;

  /** Box plot panel to display */
  protected BoxPlotManager m_boxPlots;

  /**
   * initializes the gui
   */
  private void initGUI()
  {
    setLayout(new BorderLayout());
    if(m_boxPlots != null) {
      remove(m_boxPlots);
    }
    m_boxPlots = new BoxPlotManager();
    m_boxPlots.setInstances(m_Instances);
    m_boxPlots.reset();
    add(m_boxPlots, BorderLayout.CENTER);
  }

  /**
   * sets the explorer to use as a parent frame(for sending
   * notifications about changes in the data
   * @param	parent		Parent explorer
   */
  public void setExplorer(Explorer parent) {
    m_Explorer = parent;
  }

  /**
   * returns the parent explorer frame
   * @return 			Parent explorer
   */
  public Explorer getExplorer() {
    return m_Explorer;
  }


  /**
   * Set the instances for the tab
   * @param			Instances to pass to plot
   */
  public void setInstances(Instances inst) {
    m_Instances = inst;
    initGUI();
  }

  /**
   * returns the title for the tab shown in the explorer
   * @return			String title of tab
   */
  public String getTabTitle() {
    return "Box plot";
  }

  /**
   * Tip text for the tab
   * @param			String describing the tab
   */
  public String getTabTitleToolTip() {
    return "Displays box plots of the data";
  }
}
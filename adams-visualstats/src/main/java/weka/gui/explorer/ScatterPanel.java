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
 * ScatterPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.explorer;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import adams.gui.visualization.stats.scatterplot.ScatterPlot;
import weka.core.Instances;
import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Class that creates a scatter plot and displays it from the explorer.
 *
 * @author msf8
 * @version $Revision$
 */
public class ScatterPanel
extends JPanel
implements ExplorerPanel{

  /** for serializing */
  private static final long serialVersionUID = 6428655870184166546L;

  /** parent frame */
  protected Explorer m_Explorer = null;

  /** instances to pass to plot */
  protected Instances m_Instances;

  /** scatter plot to display */
  protected ScatterPlot m_scatter;

  /**  initialises the gui */
  private void initGUI()
  {
    setLayout(new BorderLayout());
    if(m_scatter != null)
    {
      remove(m_scatter);
    }
    m_scatter = new ScatterPlot();
    m_scatter.setInstances(m_Instances);
    m_scatter.reset();

    add(m_scatter, BorderLayout.CENTER);
  }

  /**
   * sets the explorer to use as a parent frame
   * @param	Parent			parent explorer
   */
  public void setExplorer(Explorer parent) {
    m_Explorer = parent;
  }

  /**
   * returns the parent explorer frame
   * @param			parent explorer
   */
  public Explorer getExplorer() {
    return m_Explorer;
  }

  /**
   * sets instances to pass to the plot
   * @param	inst			Instances containing the data
   */
  public void setInstances(Instances inst) {
    m_Instances = inst;
    initGUI();
  }

  /**
   * returns the title for the tab shown in the explorer
   * @return			Title for tab
   */
  public String getTabTitle() {
    return "Scatter plot";
  }

  /**
   * tool tip for the tab shown in the explorer
   * @return		String describing the tab
   */
  public String getTabTitleToolTip() {
    return "Displays a scatter plot of the data";
  }
}
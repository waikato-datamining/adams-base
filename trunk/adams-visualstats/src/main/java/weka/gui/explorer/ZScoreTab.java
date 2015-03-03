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
 * ZScoreTab.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package weka.gui.explorer;

import java.awt.BorderLayout;
import javax.swing.JPanel;

import adams.gui.visualization.stats.zscore.ZScore;
import weka.core.Instances;
import weka.gui.explorer.Explorer.ExplorerPanel;

/**
 * Tab to display a z score plot in the explorer, not currently being used.
 *
 * @author msf8
 * @version $Revision$
 */
public class ZScoreTab
extends JPanel
implements ExplorerPanel{

  /** for serialization */
  private static final long serialVersionUID = 568159621130941329L;

  /**Parent frame */
  protected Explorer m_Explorer = null;

  /**instances to pass to the plot */
  protected Instances m_Instances;

  /**z score plot to display */
  protected ZScore m_ZScore;

  /**
   * initialises the gui
   **/
  private void initGUI() {
    setLayout(new BorderLayout());
    if(m_ZScore != null) {
      remove(m_ZScore);
    }
    m_ZScore = new ZScore();
    m_ZScore.setInstances(m_Instances);
    m_ZScore.reset();

    add(m_ZScore, BorderLayout.CENTER);
  }

  /**
   * Returns the parent explorer frame
   * @return			parent explorer
   */
  public Explorer getExplorer() {
    return m_Explorer;
  }

  /**
   * returns the title for the tab shown in the explorer
   * @return			String title of tab
   */
  public String getTabTitle() {
    return "Z-score";
  }

  /**
   * Tool tip for the tab shown in the explorer
   * @return			String describing the tab
   */
  public String getTabTitleToolTip() {
    return "Displays a z-score plot of the data";
  }

  /**
   * Set the explorer to use as a parent frame
   * @param	val			parent explorer
   */
  public void setExplorer(Explorer val) {
    m_Explorer = val;

  }

  /**
   * Set instances to pass to the plot
   * @param val			Instances containing the data
   */
  public void setInstances(Instances val) {
    m_Instances = val;
    initGUI();
  }
}
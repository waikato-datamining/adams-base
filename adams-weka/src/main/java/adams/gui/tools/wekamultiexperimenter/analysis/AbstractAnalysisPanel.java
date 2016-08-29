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
 * AbstractAnalysisPanel.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.wekamultiexperimenter.analysis;

import adams.core.CloneHandler;
import adams.gui.tools.wekamultiexperimenter.AbstractExperimenterPanel;
import weka.core.Instances;
import adams.core.ClassLister;

/**
 * Ancestor for panels that analysis experimental results.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAnalysisPanel
  extends AbstractExperimenterPanel
  implements CloneHandler<AbstractAnalysisPanel> {

  /** for serialization. */
  private static final long serialVersionUID = 5592519317307855580L;

  /** the raw results from the experiment. */
  protected Instances m_Results;
  
  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Results = null;
  }
  
  /**
   * finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }
  
  /**
   * Returns the name to display in the GUI.
   * 
   * @return		the name
   */
  public abstract String getAnalysisName();

  /**
   * Checks whether the results can be handled at all.
   * 
   * @param results	the results to check
   * @return		null if can handle, otherwise error message
   */
  public abstract String handlesResults(Instances results);
  
  /**
   * Sets the results to analyze.
   * 
   * @param value	the results
   */
  public void setResults(Instances value) {
    m_Results = value;
    update();
  }
  
  /**
   * Returns the currently set results.
   * 
   * @return		the results, null if none set
   */
  public Instances getResults() {
    return m_Results;
  }
  
  /**
   * Updates the GUI.
   */
  protected void update() {
  }

  /**
   * Returns a clone of the object.
   *
   * @return		the clone
   */
  public abstract AbstractAnalysisPanel getClone();

  /**
   * Returns the name of the panel.
   * 
   * @return		the name
   * @see		#getAnalysisName()
   */
  @Override
  public String toString() {
    return getAnalysisName();
  }
  
  /**
   * Returns a list with classnames of panels.
   *
   * @return		the panel classnames
   */
  public static String[] getPanels() {
    return ClassLister.getSingleton().getClassnames(AbstractAnalysisPanel.class);
  }
}

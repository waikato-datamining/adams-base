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
 * AbstractSetupPanel.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.gui.experiment.ext;

import weka.experiment.Experiment;
import adams.core.ClassLister;

/**
 * Ancestor for setup panels.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of experiment to handle
 */
public abstract class AbstractSetupPanel<T extends Experiment>
  extends AbstractExperimenterPanel {

  /** for serialization. */
  private static final long serialVersionUID = -7551590918482897687L;

  /** the handler for loading/saving experiments. */
  protected AbstractExperimentIO m_ExperimentIO;
  
  /** whether the setup has been modified. */
  protected boolean m_Modified;
  
  /** whether to ignored changes. */
  protected boolean m_IgnoreChanges;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ExperimentIO  = createExperimentIO();
    m_Modified      = false;
    m_IgnoreChanges = false;
  }
  
  /**
   * Returns the name for this setup panel.
   * 
   * @return		the name
   */
  public abstract String getSetupName();

  /**
   * Creates the handler for the IO, i.e., loading/saving of experiments.
   * 
   * @return		the handler
   */
  protected abstract AbstractExperimentIO createExperimentIO();
  
  /**
   * Returns the handler for the IO, i.e., loading/saving of experiments.
   * 
   * @return		the handler
   */
  public AbstractExperimentIO getExperimentIO() {
    return m_ExperimentIO;
  }
  
  /**
   * Returns the current experiment.
   * 
   * @return		the experiment
   */
  public abstract T getExperiment();
  
  /**
   * Sets the experiment to use.
   * 
   * @param value	the experiment
   */
  public abstract void setExperiment(T value);
  
  /**
   * Checks whether the experiment can be handled.
   * 
   * @param exp		the experiment to check
   * @return		true if can be handled
   */
  public abstract boolean handlesExperiment(T exp);

  /**
   * Sets the modified state.
   * 
   * @param value	the modified state
   * @see		#m_IgnoreChanges
   */
  public void setModified(boolean value) {
    if (m_IgnoreChanges)
      return;
    m_Modified = value;
    getOwner().update();
  }
  
  /**
   * Returns whether the setup has been modified.
   * 
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }
  
  /**
   * Returns a list with classnames of panels.
   *
   * @return		the panel classnames
   */
  public static String[] getPanels() {
    return ClassLister.getSingleton().getClassnames(AbstractSetupPanel.class);
  }
}

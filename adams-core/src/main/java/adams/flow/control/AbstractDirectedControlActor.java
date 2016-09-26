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
 * AbstractControlActor.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

/**
 * Ancestor for all actors that control sub-actors in some way.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDirectedControlActor
  extends AbstractControlActor 
  implements AtomicExecution {

  /** for serialization. */
  private static final long serialVersionUID = -7471817724012995179L;

  /** the director used for executing. */
  protected AbstractDirector m_Director;
  
  /** whether to finish execution first before stopping. */
  protected boolean m_FinishBeforeStopping;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "finish-before-stopping", "finishBeforeStopping",
	    false);
  }

  /**
   * Sets whether to finish processing before stopping execution.
   * 
   * @param value	if true then actor finishes processing first 
   */
  public void setFinishBeforeStopping(boolean value) {
    m_FinishBeforeStopping = value;
    reset();
  }
  
  /**
   * Returns whether to finish processing before stopping execution.
   * 
   * @return		true if actor finishes processing first
   */
  public boolean getFinishBeforeStopping() {
    return m_FinishBeforeStopping;
  }
  
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String finishBeforeStoppingTipText() {
    return "If enabled, actor first finishes processing all data before stopping.";
  }

  /**
   * Returns an instance of a director.
   *
   * @return		the director
   */
  protected AbstractDirector newDirector() {
    return new SequentialDirector();
  }
  
  /**
   * Returns the current director in use.
   * 
   * @return		the director, null if none in use
   */
  public AbstractDirector getDirector() {
    return m_Director;
  }

  /**
   * Gets called when the actor needs to be re-setUp when a variable changes.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String updateVariables() {
    if (m_Director != null) {
      m_Director.stopExecution();
      m_Director.cleanUp();
      m_Director = null;
    }

    return super.updateVariables();
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    if (m_FinishBeforeStopping)
      return "atomic execution";
    else
      return null;
  }

  /**
   * Initializes the sub-actors for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String		result;

    result = super.setUp();

    if (result == null) {
      if ((m_PauseStateManager != null) && (m_Director != null))
	m_PauseStateManager.removeListener(m_Director);
      m_Director = newDirector();
      m_Director.setControlActor(this);
      m_Director.setLoggingLevel(getLoggingLevel());
      m_Director.updatePrefix();
      if (m_PauseStateManager != null)
	m_PauseStateManager.addListener(m_Director);
    }

    return result;
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;

    result = null;

    try {
      result = m_Director.execute();
    }
    catch (Exception e) {
      result = handleException("Failed to execute director", e);
    }

    return result;
  }
  
  /**
   * Stops the processing of tokens without stopping the flow.
   */
  public void flushExecution() {
    if (m_Director != null)
      m_Director.flushExecution();

    super.flushExecution();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_Director != null) {
      if (m_FinishBeforeStopping) {
	while (isExecuting()) {
	  synchronized(this)  {
	    try {
	      wait(100);
	    }
	    catch (Exception e) {
	    }
	  }
	}
      }
      
      m_Director.stopExecution();
    }

    super.stopExecution();
  }

  /**
   * Finishes up the execution.
   */
  @Override
  public void wrapUp() {
    // wait for director to finish up
    if (m_Director != null) {
      while (!m_Director.isFinished()) {
	synchronized(this) {
	  try {
	    wait(100);
	  }
	  catch (Exception e) {
	    // ignored
	  }
	}
      }
    }

    super.wrapUp();
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    if (m_Director != null) {
      if (m_PauseStateManager != null)
	m_PauseStateManager.removeListener(m_Director);
      m_Director.cleanUp();
      m_Director.setControlActor(null);
      m_Director = null;
    }

    super.cleanUp();
  }
}

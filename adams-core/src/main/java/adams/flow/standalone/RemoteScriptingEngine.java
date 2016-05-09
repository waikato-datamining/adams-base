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
 * RemoteScriptingEngine.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.flow.control.Flow;
import adams.scripting.engine.DefaultScriptingEngine;

/**
 <!-- globalinfo-start -->
 * Starts&#47;stops a scripting engine for remote commands.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RemoteScriptingEngine
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-engine &lt;adams.scripting.engine.RemoteScriptingEngine&gt; (property: engine)
 * &nbsp;&nbsp;&nbsp;The remote scripting engine to run.
 * &nbsp;&nbsp;&nbsp;default: adams.scripting.engine.DefaultScriptingEngine -permission-handler adams.scripting.permissionhandler.AllowAll -request-handler adams.scripting.requesthandler.LoggingHandler -response-handler adams.scripting.responsehandler.LoggingHandler
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteScriptingEngine
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = -295054877801672294L;

  /** the engine to run. */
  protected adams.scripting.engine.RemoteScriptingEngine m_Engine;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Starts/stops a scripting engine for remote commands.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "engine", "engine",
	    new DefaultScriptingEngine());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "engine", m_Engine);
  }

  /**
   * Sets the engine to use.
   *
   * @param value	the engine
   */
  public void setEngine(adams.scripting.engine.RemoteScriptingEngine value) {
    m_Engine = value;
    reset();
  }

  /**
   * Returns the engine in use.
   *
   * @return 		the engine
   */
  public adams.scripting.engine.RemoteScriptingEngine getEngine() {
    return m_Engine;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String engineTipText() {
    return "The remote scripting engine to run.";
  }

  /**
   * Executes the actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    new Thread(() -> {
      if (getRoot() instanceof Flow)
	m_Engine.setApplicationContext(((Flow) getRoot()).getApplicationFrame());
      String msg = m_Engine.execute();
      if (msg != null)
        stopExecution(msg);
    }).start();
    return null;
  }
  
  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Engine != null)
      m_Engine.stopExecution();
    super.stopExecution();
  }
}

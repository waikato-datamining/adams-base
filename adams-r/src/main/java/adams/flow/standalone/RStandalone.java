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
 * RStandalone.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import org.rosuda.REngine.Rserve.RConnection;

import adams.core.Placeholders;
import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.scripting.RScript;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Carries out an R function on the input script.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: RStandalone
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 * 
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 * <pre>-script &lt;adams.core.scripting.RScript&gt; (property: script)
 * &nbsp;&nbsp;&nbsp;Script to pass into R.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author rsmith
 * @version $Revision$
 */
public class RStandalone
  extends AbstractStandalone {

  /** for serialization. */
  private static final long serialVersionUID = 7114485978382420994L;

  /** The R script. */
  protected RScript m_Script;

  /** Connection to Rserve */
  protected RConnection m_RConn;
  
  /** the Rserve actor. */
  protected Rserve m_Rserve;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"script", "script", 
	new RScript());
  }

  /**
   * Ressets the members.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_RConn = null;
  }

  /**
   * Returns the script.
   * 
   * @return the script
   */
  public RScript getScript() {
    return m_Script;
  }

  /**
   * Sets the script to be fed into R.
   * 
   * @param script
   *          the script to be fed to R
   */
  public void setScript(RScript script) {
    m_Script = script;
    reset();
  }

  /**
   * Description of the script.
   * 
   * @return string version of the description
   */
  public String scriptTipText() {
    return "Script to pass into R.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "script", Utils.shorten((m_Script.stringValue().length() == 0 ? "-none-" : m_Script.stringValue()), 40), "script: ");
  }

  /**
   * Sets up the connection to Rserve.
   */
  @Override
  public String setUp() {
    String 	result;
    
    result = super.setUp();

    if (result == null) {
      m_Rserve = (Rserve) ActorUtils.findClosestType(this, Rserve.class, true);
      if (m_Rserve == null)
	result = "Failed to find " + Rserve.class.getName() + " standalone with Rserve configuration!";
    }

    return result;
  }

  /**
   * Connects to Rserve and feeds it the script.
   */
  @Override
  protected String doExecute() {
    if (m_RConn == null) {
      m_RConn = m_Rserve.newConnection();
      if (m_RConn == null)
	return "Could not connect to Rserve!";
    }

    String expr = getVariables().expand(m_Script.getValue());
    expr = Placeholders.expandStr(expr);

    String[] lines = expr.split("\r?\n");
    for (String line: lines) {
      try {
	m_RConn.eval(line);
      }
      catch (Exception ex) {
	return handleException("Error occurred evaluating: " + line, ex);
      }
    }

    return null;
  }

  /**
   * Overall description of this flow.
   */
  @Override
  public String globalInfo() {
    return "Carries out an R function on the input script.";
  }

  /**
   * Closes the Rserve connection as the panel closes.
   */
  @Override
  public void wrapUp() {
    if (m_Rserve != null) {
      m_Rserve.closeConnection(m_RConn);
      m_RConn  = null;
      m_Rserve = null;
    }

    super.wrapUp();
  }
}

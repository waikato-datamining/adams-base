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
 * SetReportFromSource.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.flow.core.AbstractActor;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.Compatibility;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Obtains a report from a callable source and replaces the current one in the token passing through.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.MutableReportHandler<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: SetReportFromSource
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
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
 * <pre>-report &lt;adams.flow.core.CallableActorReference&gt; (property: report)
 * &nbsp;&nbsp;&nbsp;The callable source to obtain the report from.
 * &nbsp;&nbsp;&nbsp;default: unknown
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetReportFromSource
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -8951982264797087668L;

  /** the callable source to obtain the report from. */
  protected CallableActorReference m_Report;

  /** the helper class. */
  protected CallableActorHelper m_Helper;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Obtains a report from a callable source and replaces the current one in the token "
	+ "passing through.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "report", "report",
	    new CallableActorReference("unknown"));
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Sets the callable source to obtain the report from.
   *
   * @param value	the reference
   */
  public void setReport(CallableActorReference value) {
    m_Report = value;
    reset();
  }

  /**
   * Returns the callable source to obtain the report from.
   *
   * @return		the reference
   */
  public CallableActorReference getReport() {
    return m_Report;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reportTipText() {
    return "The callable source to obtain the report from.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "report", m_Report);
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted input
   */
  public Class[] accepts() {
    return new Class[]{MutableReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated output
   */
  public Class[] generates() {
    return new Class[]{MutableReportHandler.class};
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    String	variable;

    result = super.setUp();

    if (result == null) {
      variable = getOptionManager().getVariableForProperty("report");
      if (variable == null) {
	if (m_Report.isEmpty())
	  result = "No report source specified!";
      }
    }

    return result;
  }

  /**
   * Tries to find the callable actor referenced by its callable name.
   *
   * @return		the callable actor or null if not found
   */
  protected AbstractActor findCallableActor() {
    return m_Helper.findCallableActorRecursive(this, getReport());
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    MutableReportHandler	handler;
    AbstractActor		source;
    Compatibility		comp;
    Token			token;

    result  = null;

    handler = (MutableReportHandler) m_InputToken.getPayload();
    source  = findCallableActor();
    if (source instanceof OutputProducer) {
      comp = new Compatibility();
      if (!comp.isCompatible(new Class[]{Report.class}, ((OutputProducer) source).generates()))
	result = "Callable actor '" + m_Report + "' does not produce output that is compatible with '" + Report.class.getName() + "'!";
    }
    else {
      result = "Callable actor '" + m_Report + "' does not produce any output!";
    }

    token = null;
    if (result == null) {
      result = source.execute();
      if (result != null) {
	result = "Callable actor '" + m_Report + "' execution failed:\n" + result;
      }
      else {
	if (((OutputProducer) source).hasPendingOutput())
	  token = ((OutputProducer) source).output();
	else
	  result = "Callable actor '" + m_Report + "' did not generate any output!";
      }
    }
    
    if (result == null) {
      handler.setReport((Report) token.getPayload());
      m_OutputToken = new Token(handler);
    }
    
    return result;
  }
}

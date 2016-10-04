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
 * ChangeReportFieldPrefixes.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseString;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Replaces the specified old prefixes with the new one.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: ChangeReportFieldPrefixes
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-old-prefix &lt;adams.core.base.BaseString&gt; [-old-prefix ...] (property: oldPrefixes)
 * &nbsp;&nbsp;&nbsp;The old prefixes to replace.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-new-prefix &lt;java.lang.String&gt; (property: newPrefix)
 * &nbsp;&nbsp;&nbsp;The replacement prefix to use.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ChangeReportFieldPrefixes
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -2833759108269704357L;

  /** the prefixes to match to match. */
  protected BaseString[] m_OldPrefixes;

  /** the new prefix. */
  protected String m_NewPrefix;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Replaces the specified old prefixes with the new one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "old-prefix", "oldPrefixes",
	    new BaseString[0]);

    m_OptionManager.add(
	    "new-prefix", "newPrefix",
	    "");
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "oldPrefixes", m_OldPrefixes, "old: ");
    result += QuickInfoHelper.toString(this, "newPrefix", (m_NewPrefix.isEmpty() ? "-empty-" : m_NewPrefix), ", new: ");

    return result;
  }

  /**
   * Sets the old prefixes to replace.
   *
   * @param value	the prefixes
   */
  public void setOldPrefixes(BaseString[] value) {
    m_OldPrefixes = value;
    reset();
  }

  /**
   * Returns the old prefixes to replace.
   *
   * @return 		the prefixes
   */
  public BaseString[] getOldPrefixes() {
    return m_OldPrefixes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String oldPrefixesTipText() {
    return "The old prefixes to replace.";
  }

  /**
   * Sets the replacement prefix to use.
   *
   * @param value	the replacement
   */
  public void setNewPrefix(String value) {
    m_NewPrefix = value;
    reset();
  }

  /**
   * Returns the replacement prefix in use.
   *
   * @return 		the replacement
   */
  public String getNewPrefix() {
    return m_NewPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String newPrefixTipText() {
    return "The replacement prefix to use.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.report.Report.class, adams.data.report.ReportHandler.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.report.Report.class, adams.data.report.ReportHandler.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Report		report;
    List<AbstractField>	fields;
    AbstractField	newField;
    Object		value;

    try {
      if (m_InputToken.getPayload() instanceof Report)
	report = (Report) m_InputToken.getPayload();
      else
	report = ((ReportHandler) m_InputToken.getPayload()).getReport();

      // updated fields
      if (report != null) {
	fields = report.getFields();
	for (AbstractField oldField: fields) {
	  for (BaseString prefix: m_OldPrefixes) {
	    if (oldField.getName().startsWith(prefix.getValue())) {
	      newField = oldField.newField(oldField.getName().replaceFirst(prefix.getValue(), m_NewPrefix), oldField.getDataType());
	      value    = report.getValue(oldField);
	      report.removeValue(oldField);
	      report.addField(newField);
	      report.setValue(newField, value);
	      if (isLoggingEnabled())
		getLogger().info("Replaced '" + oldField + "' with '" + newField + "'");
	      break;
	    }
	  }
	}
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("No report available: " + m_InputToken);
      }

      result = null;
    }
    catch (Exception e) {
      result = handleException("Failed to get replace prefixes!", e);
    }

    if (result == null)
      m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}

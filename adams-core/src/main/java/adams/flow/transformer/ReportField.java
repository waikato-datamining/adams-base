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
 * ReportField.java
 * Copyright (C) 2010-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

import java.util.List;

/**
 <!-- globalinfo-start -->
 * Returns all the fields in a report or the report from a report handling object that match the provided regular expression.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
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
 * &nbsp;&nbsp;&nbsp;default: ReportField
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
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;If enabled, outputs the field names as array rather than one-by-one
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to match the field names against.
 * &nbsp;&nbsp;&nbsp;default: .*
 * &nbsp;&nbsp;&nbsp;more: https:&#47;&#47;docs.oracle.com&#47;javase&#47;tutorial&#47;essential&#47;regex&#47;
 * &nbsp;&nbsp;&nbsp;https:&#47;&#47;docs.oracle.com&#47;javase&#47;8&#47;docs&#47;api&#47;java&#47;util&#47;regex&#47;Pattern.html
 * </pre>
 *
 * <pre>-output-name-only &lt;boolean&gt; (property: outputNameOnly)
 * &nbsp;&nbsp;&nbsp;If enabled, only the field name is output and not the data type as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportField
    extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -2833759108269704357L;

  /** for backing up the fields. */
  public final static String BACKUP_FIELDS = "fields";

  /** the regular expression for the fields to match. */
  protected BaseRegExp m_RegExp;

  /** whether to output only the name (not the type). */
  protected boolean m_OutputNameOnly;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Returns all the fields in a report or the report from a report "
            + "handling object that match the provided regular expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
        "regexp", "regExp",
        new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
        "output-name-only", "outputNameOnly",
        false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "regExp", m_RegExp, "regexp: ");
    result += QuickInfoHelper.toString(this, "outputNameOnly", (m_OutputNameOnly ? "name only" : "name + type"), ", output: ");
    result += QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"), ", ");

    return result;
  }

  /**
   * Returns the base class of the items.
   *
   * @return the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, outputs the field names as array rather than one-by-one";
  }

  /**
   * Sets the regular expressions to use.
   *
   * @param value	the regular expressions
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expressions in use.
   *
   * @return 		the regular expressions
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to match the field names against.";
  }

  /**
   * Sets whether to output only the name and not the type as well.
   *
   * @param value	if true only the name is output
   */
  public void setOutputNameOnly(boolean value) {
    m_OutputNameOnly = value;
    reset();
  }

  /**
   * Returns whether to output only the name and not the type as well.
   *
   * @return 		true if only the name is output
   */
  public boolean getOutputNameOnly() {
    return m_OutputNameOnly;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String outputNameOnlyTipText() {
    return "If enabled, only the field name is output and not the data type as well.";
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    Report		report;
    List<AbstractField>	fields;

    try {
      m_Queue.clear();

      if (m_InputToken.getPayload() instanceof Report)
        report = (Report) m_InputToken.getPayload();
      else
        report = ((ReportHandler) m_InputToken.getPayload()).getReport();

      // match fields
      if (report != null) {
        fields = report.getFields();
        for (AbstractField field: fields) {
          if (m_RegExp.isMatch(field.getName())) {
            if (m_OutputNameOnly)
              m_Queue.add(field.toString());
            else
              m_Queue.add(field.toParseableString());
          }
        }
        if (isLoggingEnabled())
          getLogger().info("Matching fields: " + m_Queue);
      }
      else {
        if (isLoggingEnabled())
          getLogger().info("No report available: " + m_InputToken);
      }

      result = null;
    }
    catch (Exception e) {
      result = handleException("Failed to get report field(s): " + m_RegExp, e);
    }

    return result;
  }
}

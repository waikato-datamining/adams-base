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
 * ReportValueMatches.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Returns 'true' if the field in the report is present and has the specified value.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The field that must exist.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The required value for the field.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportValueMatches
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = 3535479178344450842L;

  /** the field in the report to check. */
  protected Field m_Field;

  /** the required value of the field. */
  protected String m_Value;

  /** whether the values have been parsed. */
  protected transient boolean m_ValueParsed;

  /** the numeric value. */
  protected transient Double m_ValueNumeric;

  /** the boolean value. */
  protected transient Boolean m_ValueBoolean;

  /**
   * Default constructor.
   */
  public ReportValueMatches() {
    super();
  }

  /**
   * Initializes with the specified field.
   *
   * @param field   	the field to use
   * @param value 	the required value
   */
  public ReportValueMatches(Field field, String value) {
    this();
    setField(field);
    setValue(value);
  }

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns 'true' if the field in the report is present and has the specified value.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "field",
      new Field());

    m_OptionManager.add(
      "value", "value",
      "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_ValueNumeric = null;
    m_ValueBoolean = null;
    m_ValueParsed  = false;
  }

  /**
   * Sets the field to look for in the report.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to look for in the report.
   *
   * @return		the field
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The field that must exist.";
  }

  /**
   * Sets the required value for the field.
   *
   * @param value	the value
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the required value for the field.
   *
   * @return		the value
   */
  public String getValue() {
    return m_Value;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueTipText() {
    return "The required value for the field.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "field", m_Field, "field: ");
    result += QuickInfoHelper.toString(this, "value", (m_Value.isEmpty() ? "-empty-" : m_Value), ", value: ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		ReportHandler, Report
   */
  @Override
  public Class[] accepts() {
    return new Class[]{ReportHandler.class, Report.class};
  }

  /**
   * Configures the condition.
   *
   * @param owner	the actor this condition belongs to
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp(Actor owner) {
    String	result;

    result = super.setUp(owner);

    if (result == null) {
      if (m_Field == null)
	result = "No report field provided!";
    }

    if (result == null) {
      m_ValueParsed = false;
      switch (m_Field.getDataType()) {
	case NUMERIC:
	  if (!Utils.isDouble(m_Value))
	    result = "Value must be a number, supplied: " + m_Value;
	  break;
	case BOOLEAN:
	  if (!Utils.isBoolean(m_Value))
	    result = "Value must be a boolean, supplied: " + m_Value;
	  break;
      }
    }

    return result;
  }

  /**
   * Performs the actual evaluation.
   *
   * @param owner	the owning actor
   * @param token	the current token passing through
   * @return		the result of the evaluation
   */
  @Override
  protected boolean doEvaluate(Actor owner, Token token) {
    boolean	result;
    Report	report;

    result = false;

    if (!m_ValueParsed) {
      switch (m_Field.getDataType()) {
	case NUMERIC:
	  m_ValueNumeric = Utils.toDouble(m_Value);
	  break;
	case BOOLEAN:
	  m_ValueBoolean = Boolean.parseBoolean(m_Value);
	  break;
      }
      m_ValueParsed = true;
    }

    if ((token != null) && (token.getPayload() != null)) {
      if (token.getPayload() instanceof ReportHandler)
	report = ((ReportHandler) token.getPayload()).getReport();
      else
	report = (Report) token.getPayload();

      if (report != null) {
	result = report.hasValue(m_Field);
	if (result) {
	  switch (m_Field.getDataType()) {
	    case NUMERIC:
	      result = (report.getDoubleValue(m_Field).equals(m_ValueNumeric));
	      break;
	    case BOOLEAN:
	      result = (report.getBooleanValue(m_Field).equals(m_ValueBoolean));
	      break;
	    default:
	      result = (report.getStringValue(m_Field).equals(m_Value));
	  }
	}
      }
    }

    return result;
  }
}

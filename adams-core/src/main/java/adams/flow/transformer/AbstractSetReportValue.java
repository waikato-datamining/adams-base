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
 * AbstractSetReportValue.java
 * Copyright (C) 2010-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.report.AbstractField;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;

/**
 * Ancestor for transformers that update the value of field in a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSetReportValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -5937471470417243026L;

  /** the field to get from the report. */
  protected AbstractField m_Field;

  /** the value to set. */
  protected String m_Value;

  /** whether to auto-detect the type of the string value. */
  protected boolean m_AutoDetectDataType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "field", "field",
      getDefaultField());

    m_OptionManager.add(
      "value", "value",
      "");

    m_OptionManager.add(
      "auto-detect-data-type", "autoDetectDataType",
      false);
  }

  /**
   * Returns the default field for the option.
   *
   * @return		the default field
   */
  protected abstract AbstractField getDefaultField();

  /**
   * Sets the value to set in the report.
   *
   * @param value	the value to set
   */
  public void setValue(String value) {
    m_Value = value;
    reset();
  }

  /**
   * Returns the value to set in the report.
   *
   * @return		the value to set
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
  public abstract String valueTipText();

  /**
   * Sets whether to use auto-detection for the data type.
   *
   * @param value	true if to use auto-detect
   */
  public void setAutoDetectDataType(boolean value) {
    m_AutoDetectDataType = value;
    reset();
  }

  /**
   * Returns whether to use auto-detection for the data type.
   *
   * @return		true if to auto-detect
   */
  public boolean getAutoDetectDataType() {
    return m_AutoDetectDataType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String autoDetectDataTypeTipText() {
    return "If enabled, tries to determine the data type of the value string automatically.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "field", m_Field.toParseableString());
    result += QuickInfoHelper.toString(this, "value", "'" + m_Value + "'", " -> ");

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  public abstract Class[] accepts();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  public abstract Class[] generates();

  /**
   * Creates a new, empty report if the {@link MutableReportHandler} is missing one.
   * 
   * @return		the report
   */
  protected abstract Report newReport();
  
  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Report	report;
    Field	field;
    String	value;

    result = null;

    if (m_InputToken.getPayload() instanceof ReportHandler)
      report = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      report = (Report) m_InputToken.getPayload();
    value = getVariables().expand(m_Value);

    try {
      if (report == null) {
	if (m_InputToken.getPayload() instanceof MutableReportHandler) {
	  report = newReport();
	  ((MutableReportHandler) m_InputToken.getPayload()).setReport(report);
	}
      }
      
      if (report != null) {
	field = new Field(m_Field);
	report.addField(field);
	if (m_AutoDetectDataType)
	  report.addParameter(m_Field.getName(), value);
	else
	  report.setValue(m_Field, value);
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("No report available: " + m_InputToken);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to set report value: " + m_Field + "/" + value, e);
    }

    // broadcast data
    m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}

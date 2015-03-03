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
 * BooleanReportValue.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.condition.bool;

import adams.core.QuickInfoHelper;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Actor;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Returns 'true' if the boolean field in the report is present and is 'true'.
 * <p/>
 <!-- globalinfo-end -->
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
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The field that determines the result of the evaluation.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BooleanReportValue
  extends AbstractBooleanCondition {

  /** for serialization. */
  private static final long serialVersionUID = -9169161144858552052L;

  /** the field in the report to check. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Returns 'true' if the boolean field in the report is present and is 'true'.";
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
    return "The field that determines the result of the evaluation.";
  }

  /**
   * Returns the quick info string to be displayed in the flow editor.
   *
   * @return		the info or null if no info to be displayed
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "field", m_Field, "boolean field: ");
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

    if ((token != null) && (token.getPayload() != null)) {
      if (token.getPayload() instanceof ReportHandler)
	report = ((ReportHandler) token.getPayload()).getReport();
      else
	report = (Report) token.getPayload();

      if (report != null) {
	if (report.hasValue(m_Field))
	  result = report.getBooleanValue(m_Field);
      }
    }

    return result;
  }
}

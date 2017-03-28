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
 * AbstractDeleteReportValueByExpression.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;
import adams.parser.BooleanExpression;
import adams.parser.BooleanExpressionText;

/**
 * Ancestor for transformers that delete field/value from a report if the
 * boolean expression evaluates to true.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDeleteReportValueByExpression
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6237324482439105653L;

  /** the field to delete from the report if the expression evaluates to true. */
  protected AbstractField m_Field;

  /** the expression to evaluate. */
  protected BooleanExpressionText m_Expression;

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
	    "expression", "expression",
	    new BooleanExpressionText("false"));
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "field", m_Field, "field: ");
    result += QuickInfoHelper.toString(this, "expression", m_Expression, ", expression: ");

    return result;
  }

  /**
   * Returns the default field for the option.
   *
   * @return		the default field
   */
  protected abstract AbstractField getDefaultField();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String fieldTipText();

  /**
   * Sets the boolean expression that decides whether to remove the field.
   *
   * @param value	the expression
   */
  public void setExpression(BooleanExpressionText value) {
    m_Expression = value;
    reset();
  }

  /**
   * Returns the boolean expression that decides whether to remove the field.
   *
   * @return		the expression
   */
  public BooleanExpressionText getExpression() {
    return m_Expression;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String expressionTipText() {
    return "The boolean expression that decides whether to remove the field or not.";
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
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Report	report;
    String	expr;

    result = null;

    if (m_InputToken.getPayload() instanceof ReportHandler)
      report = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      report = (Report) m_InputToken.getPayload();

    try {
      if (report != null) {
	expr = m_Expression.getValue();
	if (BooleanExpression.evaluate(expr, report)) {
	  if (isLoggingEnabled())
	    getLogger().info("Deleting field: " + m_Field);
	  report.removeValue(m_Field);
	}
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("No report available: " + m_InputToken);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to delete report field!", e);
    }

    m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}

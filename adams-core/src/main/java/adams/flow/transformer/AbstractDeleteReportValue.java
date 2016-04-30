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
 * AbstractDeleteReportValue.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static adams.flow.transformer.AbstractDeleteReportValue.MatchType.FIELDS;

/**
 * Ancestor for transformers that delete values from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDeleteReportValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6237324482439105653L;

  /**
   * How to select fields.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum MatchType {
    REGEXP,
    FIELDS
  }

  /** how to match fields. */
  protected MatchType m_Type;

  /** the regular expression for matching field names. */
  protected BaseRegExp m_RegExp;

  /** the field(s) to delete from the report. */
  protected AbstractField[] m_Fields;

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
	    "type", "type",
	    FIELDS);

    m_OptionManager.add(
	    "field", "fields",
	    getDefaultFields());

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "type", m_Type, "type: ");
    result += QuickInfoHelper.toString(this, "fields", m_Fields, ", fields: ");
    result += QuickInfoHelper.toString(this, "regExp", m_RegExp, ", regexp: ");

    return result;
  }

  /**
   * Sets the match type.
   *
   * @param value	the type
   */
  public void setType(MatchType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the match type.
   *
   * @return		the type
   */
  public MatchType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "How to match the fields.";
  }

  /**
   * Returns the default fields for the option.
   *
   * @return		the default fields
   */
  protected abstract AbstractField[] getDefaultFields();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String fieldsTipText();

  /**
   * Sets the regular expression to use for matching the field names.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to use for matching the field names.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression for matching the field names.";
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
    String			result;
    Report			report;
    List<AbstractField>		fields;

    result = null;

    if (m_InputToken.getPayload() instanceof ReportHandler)
      report = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      report = (Report) m_InputToken.getPayload();

    try {
      if (report != null) {
	switch (m_Type) {
	  case REGEXP:
	    fields = new ArrayList<>();
	    for (AbstractField field: report.getFields()) {
	      if (m_RegExp.isMatch(field.getName()))
		fields.add(field);
	    }
	    break;
	  case FIELDS:
	    fields = new ArrayList<>(Arrays.asList(m_Fields));
	    break;
	  default:
	    throw new IllegalStateException("Unhandled match type: " + m_Type);
	}

	// remove fields
	for (AbstractField field: fields) {
	  if (isLoggingEnabled())
	    getLogger().info("Deleting field: " + field);
	  report.removeValue(field);
	}
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("No report available: " + m_InputToken);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to delete report values!", e);
    }

    m_OutputToken = new Token(m_InputToken.getPayload());

    return result;
  }
}

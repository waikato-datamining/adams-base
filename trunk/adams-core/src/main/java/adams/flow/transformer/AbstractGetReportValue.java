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
 * AbstractGetReportValue.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.data.report.AbstractField;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Token;

/**
 * Ancestor for transformers that retrieve a value from a report.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of field to handle
 */
public abstract class AbstractGetReportValue
  extends AbstractTransformer {

  /** for serialization. */
  private static final long serialVersionUID = -6237324482439105653L;

  /** the field to get from the report. */
  protected AbstractField m_Field;

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
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "field", m_Field.toParseableString());
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
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  public abstract Class[] accepts();

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->java.lang.Double.class<!-- flow-generates-end -->
   */
  public Class[] generates() {
    switch (m_Field.getDataType()) {
      case NUMERIC:
	return new Class[]{Double.class};
      case BOOLEAN:
	return new Class[]{Boolean.class};
      case STRING:
	return new Class[]{String.class};
      default:
	return new Class[]{String.class};
    }
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Object	obj;
    Report	report;

    result = null;
    obj    = null;

    if (m_InputToken.getPayload() instanceof ReportHandler)
      report = ((ReportHandler) m_InputToken.getPayload()).getReport();
    else
      report = (Report) m_InputToken.getPayload();

    try {
      if (report != null) {
	if (report.hasValue(m_Field)) {
	  obj = report.getValue(m_Field);
	}
	else {
	  if (isLoggingEnabled())
	    getLogger().info("Field '" + m_Field + "' not available from report!");
	}
      }
      else {
	if (isLoggingEnabled())
	  getLogger().info("No report available: " + m_InputToken);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to get report value: " + m_Field, e);
    }

    // broadcast data
    if (obj != null) {
      switch (m_Field.getDataType()) {
	case NUMERIC:
	  m_OutputToken = new Token(new Double(obj.toString()));
	  break;
	case BOOLEAN:
	  m_OutputToken = new Token(new Boolean(obj.toString()));
	  break;
	case STRING:
	  m_OutputToken = new Token(obj.toString());
	  break;
	default:
	  m_OutputToken = new Token(obj.toString());
      }
    }

    return result;
  }
}

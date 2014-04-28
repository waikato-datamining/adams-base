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
 * StringValueRequired.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import java.util.ArrayList;
import java.util.List;

import adams.core.base.BaseRegExp;
import adams.data.container.DataContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 <!-- globalinfo-start -->
 * Checks whether the specified string field is available in the report and the value matches the specified regular expression.
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
 * &nbsp;&nbsp;&nbsp;The field to use (ie, the class).
 * &nbsp;&nbsp;&nbsp;default: blah
 * </pre>
 *
 * <pre>-regexp &lt;java.lang.String&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression used for matching the strings.
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 *
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringMatcher
  extends AbstractOutlierDetector {

  /** for serialization. */
  private static final long serialVersionUID = -4774492907534443823L;

  /** the name of the reference compound. */
  protected Field m_Field;

  /** the regular expression that the value must match. */
  protected BaseRegExp m_RegExp;

  /** whether to invert the matching sense. */
  protected boolean m_Invert;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Checks whether the specified string field is available in the report "
      + "and the value matches the specified regular expression.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "field",
	    new Field("blah", DataType.UNKNOWN));

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert", "invert",
	    false);
  }

  /**
   * Sets the reference, i.e., the class.
   *
   * @param value	the reference
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the current reference (i.e., class).
   *
   * @return		the reference
   */
  public Field getField() {
    return m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String fieldTipText() {
    return "The field to use (ie, the class).";
  }

  /**
   * Sets the regular expression to match the strings against.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to match the strings against.
   *
   * @return		the regular expression
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
    return "The regular expression used for matching the strings.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Invert = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Invert;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Performs the actual detection.
   *
   * @param data	the data to process
   * @return		the detections
   */
  @Override
  protected List<String> processData(DataContainer data) {
    List<String>	result;
    ReportHandler	handler;
    Report		report;
    String		s;
    boolean		pass;

    result = new ArrayList<String>();

    if (data instanceof ReportHandler) {
      handler = (ReportHandler) data;
      if (handler.getReport() != null) {
	report = handler.getReport();
	if (!report.hasValue(m_Field)) {
	  result.add("Field '" + m_Field + "' not in report!");
	}
	else {
	  if (!(report.getValue(m_Field) instanceof String)) {
	    result.add("Field '" + m_Field + "' not of type String!");
	  }
	  else {
	    s = report.getStringValue(m_Field);
	    if (m_Invert)
	      pass = !m_RegExp.isMatch(s);
	    else
	      pass = m_RegExp.isMatch(s);
	    if (!pass)
	      result.add("'" + s + "' doesn't match '" + m_RegExp + "' for field " + m_Field);
	  }
	}
      }
      else {
	result.add("No report available!");
      }
    }
    else {
      result.add("Data container does not handle reports!");
    }

    return result;
  }
}

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
 * RemoveByName.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

import adams.core.base.BaseRegExp;
import adams.data.container.DataContainer;

/**
 <!-- globalinfo-start -->
 * Removes all fields from the report which names match the specified regular expression. Matching can be inverted as well.
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
 * <pre>-reg-exp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the field names must match to get removed (or 
 * &nbsp;&nbsp;&nbsp;being kept, if matching is inverted).
 * &nbsp;&nbsp;&nbsp;default: .*
 * </pre>
 * 
 * <pre>-invert-matching (property: invertMatching)
 * &nbsp;&nbsp;&nbsp;If enabled the fields that don't match are retained rather than deleted.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveByName
  extends AbstractReportFilter {

  /** for serialization. */
  private static final long serialVersionUID = 1215043320920480970L;

  /** the regular expression to match against the names. */
  protected BaseRegExp m_RegExp;
  
  /** whether to invert the matching, ie keeping the fields that don't match. */
  protected boolean m_InvertMatching;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Removes all fields from the report which names match the "
	+ "specified regular expression. Matching can be inverted as well.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "invert-matching", "invertMatching",
	    false);
  }

  /**
   * Sets the regular expression to use.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression in use.
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
   * 			displaying in the gui
   */
  public String regExpTipText() {
    return 
	"The regular expression that the field names must match to get " 
	+ "removed (or being kept, if matching is inverted).";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if to invert
   */
  public void setInvertMatching(boolean value) {
    m_InvertMatching = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if inverted
   */
  public boolean getInvertMatching() {
    return m_InvertMatching;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String invertMatchingTipText() {
    return "If enabled the fields that don't match are retained rather than deleted.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected DataContainer processData(DataContainer data) {
    Report	oldReport;
    Report	newReport;
    
    if (!(data instanceof MutableReportHandler))
      return data;
    
    oldReport = ((MutableReportHandler) data).getReport();
    newReport = Report.newInstance(oldReport);
    
    for (AbstractField field: oldReport.getFields()) {
      if (m_InvertMatching) {
	if (m_RegExp.isMatch(field.getName())) {
	  newReport.addField(field);
	  newReport.setValue(field, oldReport.getValue(field));
	}
      }
      else {
	if (!m_RegExp.isMatch(field.getName())) {
	  newReport.addField(field);
	  newReport.setValue(field, oldReport.getValue(field));
	}
      }
    }
    
    ((MutableReportHandler) data).setReport(newReport);
    
    return data;
  }
}

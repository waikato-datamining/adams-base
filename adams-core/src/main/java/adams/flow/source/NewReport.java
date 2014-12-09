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
 * NewReport.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.data.report.Report;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Generates a new instance of the specified report class.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - generates:<br/>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: NewReport
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
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-report-class &lt;java.lang.String&gt; (property: reportClass)
 * &nbsp;&nbsp;&nbsp;The class to use for the report.
 * &nbsp;&nbsp;&nbsp;default: adams.data.report.Report
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7828 $
 */
public class NewReport
  extends AbstractSimpleSource {

  /** for serialization. */
  private static final long serialVersionUID = 7272049518765623563L;

  /** the class of the report. */
  protected String m_ReportClass;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a new instance of the specified report class.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "report-class", "reportClass",
	    getDefaultReportClass());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "reportClass", (m_ReportClass.length() != 0) ? m_ReportClass : "-from 1st element-", "Class: ");
  }

  /**
   * Returns the default report class to use.
   * 
   * @return		the class
   */
  protected String getDefaultReportClass() {
    return Report.class.getName();
  }
  
  /**
   * Sets the class for the report.
   *
   * @param value	the classname
   */
  public void setReportClass(String value) {
    m_ReportClass = value;
    reset();
  }

  /**
   * Returns the class for the report.
   *
   * @return		the classname
   */
  public String getReportClass() {
    return m_ReportClass;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String reportClassTipText() {
    return "The class to use for the report.";
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  @Override
  public Class[] generates() {
    Class[]	result;

    if (m_ReportClass.length() > 0) {
      try {
	result = new Class[]{Class.forName(m_ReportClass)};
      }
      catch (Exception e) {
	// ignored
	result = new Class[0];
      }
    }
    else {
      result = new Class[]{Unknown.class};
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String	result;
    Report	report;
    
    result = null;
    
    try {
      report        = (Report) Class.forName(m_ReportClass).newInstance();
      m_OutputToken = new Token(report);
    }
    catch (Exception e) {
      result = handleException("Failed to generate report!", e);
    }
    
    return result;
  }
}

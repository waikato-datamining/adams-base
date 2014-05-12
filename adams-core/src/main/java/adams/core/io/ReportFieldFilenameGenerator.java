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
 * ReportFieldFilenameGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 <!-- globalinfo-start -->
 * Obtains the filename from a field in the attached report.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The report field to obtain the filename from.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportFieldFilenameGenerator
  extends AbstractFilenameGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -6774985545949876372L;
  
  /** the field to obtain the filename from. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Obtains the filename from a field in the attached report.";
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
   * Sets the field to use.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field in use.
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
    return "The report field to obtain the filename from.";
  }

  /**
   * Performs the actual generation of the filename.
   *
   * @param obj		the object to generate the filename for
   * @return		the generated filename
   */
  @Override
  protected String doGenerate(Object obj) {
    String	result;
    Report	report;
    
    result = null;
    
    if (obj instanceof ReportHandler) {
      report = ((ReportHandler) obj).getReport();
      if ((report != null) && (report.hasValue(m_Field))) {
	result = "" + report.getValue(m_Field);
      }
      else {
	getLogger().severe("No report or report field ('" + m_Field + "') present: " + report);
      }
    }
    else {
      getLogger().severe("Object is not a report handler: " + obj.getClass().getName());
    }
    
    return result;
  }
}

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
 * DeleteReportValue.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.data.report.AbstractField;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Deletes values from a report. Can either use a regular expression to match against the names or specific fields.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br>
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: DeleteReportValue
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
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to remove from the report.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DeleteReportValue
  extends AbstractDeleteReportValue {

  private static final long serialVersionUID = -2551776226766445092L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Deletes values from a report. Can either use a regular expression "
	+ "to match against the names or specific fields.";
  }

  /**
   * Returns the default fields for the option.
   *
   * @return		the default fields
   */
  @Override
  protected AbstractField[] getDefaultFields() {
    return new Field[0];
  }

  /**
   * Sets the fields to delete from the report.
   *
   * @param value	the fields
   */
  public void setFields(Field[] value) {
    m_Fields = value;
    reset();
  }

  /**
   * Returns the fields to delete from the report.
   *
   * @return		the fields
   */
  public Field[] getFields() {
    return (Field[]) m_Fields;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String fieldsTipText() {
    return "The fields to remove from the report.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the accepted classes
   */
  @Override
  public Class[] accepts() {
    return new Class[]{Report.class, ReportHandler.class};
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the generated classes
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }
}

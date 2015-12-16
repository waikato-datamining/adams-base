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
 * SetReportValue.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.MutableReportHandler;
import adams.data.report.Report;
import adams.data.report.ReportHandler;
import adams.flow.core.Unknown;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Sets a value in a report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - accepts:<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.ReportHandler<br>
 * &nbsp;&nbsp;&nbsp;adams.data.report.Report<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;Unknown<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: SetReportValue
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The value of this field will be retrieved from the report.
 * &nbsp;&nbsp;&nbsp;default: blah[N]
 * </pre>
 *
 * <pre>-value &lt;java.lang.String&gt; (property: value)
 * &nbsp;&nbsp;&nbsp;The value to set in the report.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SetReportValue
  extends AbstractSetReportValue {

  /** for serialization. */
  private static final long serialVersionUID = -5937471470417243026L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets a value in a report.";
  }

  /**
   * Returns the default field for the option.
   *
   * @return		the default field
   */
  @Override
  protected Field getDefaultField() {
    return new Field("blah", DataType.NUMERIC);
  }

  /**
   * Sets the field to retrieve from the report.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_Field = value;
    reset();
  }

  /**
   * Returns the field to retrieve from the report.
   *
   * @return		the field
   */
  public Field getField() {
    return (Field) m_Field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "The value of this field will be retrieved from the report.";
  }
  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String valueTipText() {
    return "The value to set in the report.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.data.report.ReportHandler.class, adams.data.report.Report.class<!-- flow-accepts-end -->
   */
  @Override
  public Class[] accepts() {
    List<Class>	result;

    result = new ArrayList<Class>();

    result.add(ReportHandler.class);
    result.add(Report.class);

    return result.toArray(new Class[result.size()]);
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		<!-- flow-generates-start -->adams.data.report.ReportHandler.class, adams.data.report.Report.class<!-- flow-generates-end -->
   */
  @Override
  public Class[] generates() {
    return new Class[]{Unknown.class};
  }

  /**
   * Creates a new, empty report if the {@link MutableReportHandler} is missing one.
   * 
   * @return		the report
   */
  @Override
  protected Report newReport() {
    return new Report();
  }
}

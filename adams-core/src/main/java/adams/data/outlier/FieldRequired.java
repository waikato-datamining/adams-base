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
 * FieldRequired.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import java.util.ArrayList;
import java.util.List;

import adams.data.container.DataContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.ReportHandler;

/**
 <!-- globalinfo-start -->
 * Checks whether the specified field is present in the report.
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
 * &nbsp;&nbsp;&nbsp;The field to look for.
 * &nbsp;&nbsp;&nbsp;default: blah[N]
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FieldRequired
  extends AbstractOutlierDetector {

  /** for serialization. */
  private static final long serialVersionUID = -6838687006986727555L;

  /** the name of the field that needs to be present. */
  protected Field m_Field;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the specified field is present in the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "field",
	    new Field("blah", DataType.NUMERIC));
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
    return "The field to look for.";
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
    Object		t;
    ReportHandler	handler;

    result = new ArrayList<String>();

    if (data instanceof ReportHandler) {
      handler = (ReportHandler) data;
      if (handler.getReport() != null) {
	t = handler.getReport().getValue(m_Field);
	if (t == null)
	  result.add("Field '" + m_Field + "' not in report!");
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

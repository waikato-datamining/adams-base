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
 * MinMax.java
 * Copyright (C) 2008-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.data.outlier;

import java.util.ArrayList;
import java.util.List;

import adams.data.container.DataContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;
import adams.data.report.ReportHandler;

/**
 <!-- globalinfo-start -->
 * Detects data containers where a report value is too high/low.
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
 * <pre>-min &lt;double&gt; (property: min)
 * &nbsp;&nbsp;&nbsp;Min value of field in report.
 * &nbsp;&nbsp;&nbsp;default: 25.0
 * </pre>
 *
 * <pre>-max &lt;double&gt; (property: max)
 * &nbsp;&nbsp;&nbsp;Max value of field in report.
 * &nbsp;&nbsp;&nbsp;default: 40.0
 * </pre>
 *
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;Field in report.
 * &nbsp;&nbsp;&nbsp;default: Toluene-d8\\tConc
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  Dale (dale at cs dot waikato dot ac dot nz)
 * @version $Revision$
 */
public class MinMax
  extends AbstractOutlierDetector<DataContainer> {

  /** for serialization. */
  private static final long serialVersionUID = 8061387654170301948L;

  /** the quant field.*/
  protected Field m_field;

  /** min. */
  protected double m_min;

  /** max. */
  protected double m_max;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Detects data containers where a report value is too high/low.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min", "min",
	    25.0);

    m_OptionManager.add(
	    "max", "max",
	    40.0);

    m_OptionManager.add(
	    "field", "field",
	    new Field("Toluene-d8\tConc", DataType.NUMERIC));
  }

  /**
   * Sets the minimum.
   *
   * @param value	min
   */
  public void setMin(double value) {
    m_min = value;
    reset();
  }

  /**
   * Returns the currently set minimum.
   *
   * @return 		the minimum
   */
  public double getMin() {
    return m_min;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String minTipText() {
    return "Min value of field in report.";
  }

  /**
   * Sets the max.
   *
   * @param value	min
   */
  public void setMax(double value) {
    m_max = value;
    reset();
  }

  /**
   * Returns the currently set max.
   *
   * @return 		the max
   */
  public double getMax() {
    return m_max;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String maxTipText() {
    return "Max value of field in report.";
  }

  /**
   * Sets the field.
   *
   * @param value	the field
   */
  public void setField(Field value) {
    m_field = value;
    reset();
  }

  /**
   * Returns the field.
   *
   * @return 		the field
   */
  public Field getField() {
    return m_field;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return         tip text for this property suitable for
   *             displaying in the GUI or for listing the options.
   */
  public String fieldTipText() {
    return "Field in report.";
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
    String		msg;
    Report 		report;
    Double 		value;

    result = new ArrayList<String>();
    msg    = "";
    report = null;
    if (data instanceof ReportHandler)
      report = ((ReportHandler) data).getReport();

    if (report == null) {
      msg = "No report available";
      result.add(msg);
    }
    else {
      value = report.getDoubleValue(m_field);
      if (value == null) {
	msg = "Field '" + m_field + "' not found";
	result.add(msg);
      }
      else {
	if (value < m_min ) {
	  msg = m_field + " too small (< " + m_min + ") : " + value;
	  result.add(msg);
	}
	else if (value > m_max) {
	  msg = m_field + " too big (> " + m_max + "): " + value;
	  result.add(msg);
	}
      }
    }
    if (isLoggingEnabled())
      getLogger().info(data + " - " + getClass().getName() + ": " + msg);

    return result;
  }
}

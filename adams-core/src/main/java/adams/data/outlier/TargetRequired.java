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
 * TargetRequired.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
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
 * Checks whether the specified target field is available in the report.
 * <br><br>
 <!-- globalinfo-end -->
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
 * <pre>-field &lt;adams.data.report.Field&gt; (property: field)
 * &nbsp;&nbsp;&nbsp;The field to use (ie, the class).
 * &nbsp;&nbsp;&nbsp;default: benzene\\tConc
 * </pre>
 *
 * <pre>-conc &lt;double&gt; (property: minConcentration)
 * &nbsp;&nbsp;&nbsp;The minimum concentration that the target value must have.
 * &nbsp;&nbsp;&nbsp;default: 0.0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TargetRequired
  extends AbstractOutlierDetector {

  /** for serialization. */
  private static final long serialVersionUID = -6838687006986727555L;

  /** the name of the reference compound. */
  protected Field m_Field;

  /** the minimum concentration. */
  protected double m_MinConcentration;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Checks whether the specified target field is available in the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "field", "field",
	    new Field("benzene\tConc", DataType.NUMERIC));

    m_OptionManager.add(
	    "conc", "minConcentration",
	    0.0);
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
   * Set minimum concentration.
   *
   * @param value	the minimum concentration
   */
  public void setMinConcentration(double value) {
    m_MinConcentration = value;
    reset();
  }

  /**
   * Get minimum concentration.
   *
   * @return		the minimum concentration
   */
  public double getMinConcentration() {
    return m_MinConcentration;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String minConcentrationTipText() {
    return "The minimum concentration that the target value must have.";
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
	if (t == null) {
	  result.add("Target value '" + m_Field + "' not in report!");
	}
	else {
	  if (!(t instanceof Double)) {
	    result.add("Target value '" + m_Field + "' not of type Double!");
	  }
	  else {
	    if ((Double) t < m_MinConcentration)
	      result.add("Target value '" + m_Field + "' does not meet minimum concentration of " + m_MinConcentration + ", " + t + " instead!");
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

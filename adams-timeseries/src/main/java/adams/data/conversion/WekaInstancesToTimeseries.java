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
 * WekaInstancesToTimeseries.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.Date;

import weka.core.Instance;
import weka.core.Instances;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;
import adams.data.weka.WekaAttributeIndex;

/**
 <!-- globalinfo-start -->
 * Turns a WEKA Instances object into a Timeseries.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-date-attribute &lt;adams.data.weka.WekaAttributeIndex&gt; (property: dateAttribute)
 * &nbsp;&nbsp;&nbsp;The index of the date attribute in the dataset to use a timestamp for the 
 * &nbsp;&nbsp;&nbsp;timeseries data points.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-value-attribute &lt;adams.data.weka.WekaAttributeIndex&gt; (property: valueAttribute)
 * &nbsp;&nbsp;&nbsp;The index of the attribute with the timeseries values in the dataset.
 * &nbsp;&nbsp;&nbsp;default: 2
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from attribute names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class WekaInstancesToTimeseries
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3934411483801077460L;
  
  /** the date attribute to use. */
  protected WekaAttributeIndex m_DateAttribute;
  
  /** the value attribute to use. */
  protected WekaAttributeIndex m_ValueAttribute;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a WEKA Instances object into a Timeseries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "date-attribute", "dateAttribute",
	    new WekaAttributeIndex("1"));

    m_OptionManager.add(
	    "value-attribute", "valueAttribute",
	    new WekaAttributeIndex("2"));
  }

  /**
   * Sets the index of the date attribute to use as timestamp for timeseries.
   *
   * @param value	the index
   */
  public void setDateAttribute(WekaAttributeIndex value) {
    m_DateAttribute = value;
    reset();
  }

  /**
   * Returns the index of the date attribute to use as timestamp for timeseries.
   *
   * @return		the index
   */
  public WekaAttributeIndex getDateAttribute() {
    return m_DateAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dateAttributeTipText() {
    return "The index of the date attribute in the dataset to use a timestamp for the timeseries data points.";
  }

  /**
   * Sets the index of the attribute with the timeseries values.
   *
   * @param value	the index
   */
  public void setValueAttribute(WekaAttributeIndex value) {
    m_ValueAttribute = value;
    reset();
  }

  /**
   * Returns the index of the attribute with the timeseries values.
   *
   * @return		the index
   */
  public WekaAttributeIndex getValueAttribute() {
    return m_ValueAttribute;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String valueAttributeTipText() {
    return "The index of the attribute with the timeseries values in the dataset.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Instances.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Timeseries.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Timeseries			result;
    Instances			input;
    Instance			inst;
    int				indexDate;
    int				indexValue;
    TimeseriesPoint		point;
    int				i;
    Date			timestamp;
    double			value;
    
    input = (Instances) m_Input;

    // determine attribute indices
    m_DateAttribute.setData(input);
    indexDate = m_DateAttribute.getIntIndex();
    if (indexDate == -1)
      throw new IllegalStateException("Failed to located date attribute: " + m_DateAttribute.getIndex());
    m_ValueAttribute.setData(input);
    indexValue = m_ValueAttribute.getIntIndex();
    if (indexValue == -1)
      throw new IllegalStateException("Failed to located value attribute: " + m_ValueAttribute.getIndex());
    
    result = new Timeseries(input.relationName() + "-" + input.attribute(indexValue).name());
    for (i = 0; i < input.numInstances(); i++) {
      inst      = input.instance(i);
      if (!inst.isMissing(indexDate) && !inst.isMissing(indexValue)) {
	timestamp = new Date((long) inst.value(indexDate));
	value     = inst.value(indexValue);
	point     = new TimeseriesPoint(timestamp, value);
	result.add(point);
      }
    }
    
    return result;
  }
}

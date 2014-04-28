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
 * TimeseriesToWekaInstances.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import java.util.ArrayList;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import adams.core.Constants;
import adams.data.DateFormatString;
import adams.data.timeseries.Timeseries;
import adams.data.timeseries.TimeseriesPoint;

/**
 <!-- globalinfo-start -->
 * Turns a timeseries into a WEKA Instances object.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-format &lt;adams.data.DateFormatString&gt; (property: format)
 * &nbsp;&nbsp;&nbsp;The date&#47;time format to use in the Instances object.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd\'T\'HH:mm:ss
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TimeseriesToWekaInstances
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 3934411483801077460L;
  
  /** the date format to use in the Instances object. */
  protected DateFormatString m_Format;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a timeseries into a WEKA Instances object.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "format", "format",
	    new DateFormatString(Constants.TIMESTAMP_FORMAT_ISO8601));
  }

  /**
   * Sets the format to use in the Instances object.
   *
   * @param value	the format
   */
  public void setFormat(DateFormatString value) {
    m_Format = value;
    reset();
  }

  /**
   * Returns the format in use in the Instances object.
   *
   * @return		the format
   */
  public DateFormatString getFormat() {
    return m_Format;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String formatTipText() {
    return "The date/time format to use in the Instances object.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Timeseries.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Instances.class;
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    Instances			result;
    ArrayList<Attribute>	atts;
    Instance			inst;
    Timeseries			series;
    TimeseriesPoint		point;
    double[]			value;
    
    series = (Timeseries) m_Input;
    
    atts = new ArrayList<Attribute>();
    atts.add(new Attribute("Timestamp", m_Format.getValue()));
    atts.add(new Attribute("Value"));
    
    result = new Instances(series.getID(), atts, series.size());
    for (Object obj: series.toList()) {
      point    = (TimeseriesPoint) obj;
      value    = new double[2];
      value[0] = point.getTimestamp().getTime();
      value[1] = point.getValue();
      inst     = new DenseInstance(1.0, value);
      result.add(inst);
    }
    
    return result;
  }
}

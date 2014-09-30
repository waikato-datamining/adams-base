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
 * Values.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import java.util.ArrayList;
import java.util.List;

import adams.core.Constants;
import adams.core.DateFormat;
import adams.data.DateFormatString;
import adams.data.featureconverter.HeaderDefinition;
import adams.data.report.DataType;

/**
 <!-- globalinfo-start -->
 * Simple feature generator that just outputs all the values of a timeseries.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-converter &lt;adams.data.featureconverter.AbstractFeatureConverter&gt; (property: converter)
 * &nbsp;&nbsp;&nbsp;The feature converter to use to produce the output data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.featureconverter.SpreadSheetFeatureConverter -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-add-database-id &lt;boolean&gt; (property: addDatabaseID)
 * &nbsp;&nbsp;&nbsp;If enabled, the database ID of the container gets added to the data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-add-id &lt;boolean&gt; (property: addID)
 * &nbsp;&nbsp;&nbsp;If enabled, the ID of the container gets added to the data.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-field &lt;adams.data.report.Field&gt; [-field ...] (property: fields)
 * &nbsp;&nbsp;&nbsp;The fields to add to the output.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-notes &lt;adams.core.base.BaseString&gt; [-notes ...] (property: notes)
 * &nbsp;&nbsp;&nbsp;The notes to add as attributes to the generated data, eg 'PROCESS INFORMATION'
 * &nbsp;&nbsp;&nbsp;.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-add-timestamp &lt;boolean&gt; (property: addTimestamp)
 * &nbsp;&nbsp;&nbsp;If enabled, the timestamp gets added as well, preceding the value.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-timestamp-format &lt;adams.data.DateFormatString&gt; (property: timestampFormat)
 * &nbsp;&nbsp;&nbsp;The format to use for the timestamp strings.
 * &nbsp;&nbsp;&nbsp;default: yyyy-MM-dd HH:mm:ss
 * &nbsp;&nbsp;&nbsp;more: http:&#47;&#47;docs.oracle.com&#47;javase&#47;6&#47;docs&#47;api&#47;java&#47;text&#47;SimpleDateFormat.html
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Values
  extends AbstractTimeseriesFeatureGenerator<Timeseries> {

  /** for serialization. */
  private static final long serialVersionUID = 9084280445189495060L;

  /** whether to include the timestamp. */
  protected boolean m_AddTimestamp;
  
  /** the timestamp format. */
  protected DateFormatString m_TimestampFormat;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple feature generator that just outputs all the values of a timeseries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "add-timestamp", "addTimestamp",
	    false);

    m_OptionManager.add(
	    "timestamp-format", "timestampFormat",
	    new DateFormatString(Constants.TIMESTAMP_FORMAT));
  }
  
  /**
   * Sets whether to add the timestamp as well (preceding the value).
   *
   * @param value	true if to add timestamp
   */
  public void setAddTimestamp(boolean value) {
    m_AddTimestamp = value;
    reset();
  }

  /**
   * Returns whether to add the timestamp as well (preceding the value).
   *
   * @return		true if to add timestamp
   */
  public boolean getAddTimestamp() {
    return m_AddTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addTimestampTipText() {
    return "If enabled, the timestamp gets added as well, preceding the value.";
  }
  
  /**
   * Sets the format string to use for the timestamp strings.
   *
   * @param value	the format
   */
  public void setTimestampFormat(DateFormatString value) {
    m_TimestampFormat = value;
    reset();
  }

  /**
   * Returns the format string to use for the timestamp strings.
   *
   * @return		the format
   */
  public DateFormatString getTimestampFormat() {
    return m_TimestampFormat;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timestampFormatTipText() {
    return "The format to use for the timestamp strings.";
  }

  /**
   * Creates the header from a template timeseries.
   *
   * @param timeseries	the timeseries to act as a template
   * @return		the generated header
   */
  @Override
  public HeaderDefinition createHeader(Timeseries timeseries) {
    HeaderDefinition	result;
    int			i;
    
    result = new HeaderDefinition();
    for (i = 0; i < timeseries.size(); i++) {
      if (m_AddTimestamp)
	result.add("Timestamp-" + (i+1), DataType.STRING);
      result.add("Value-" + (i+1), DataType.NUMERIC);
    }
    
    return result;
  }

  /**
   * Performs the actual feature genration.
   *
   * @param timeseries	the timeseries to process
   * @return		the generated features
   */
  @Override
  public List<Object>[] generateRows(Timeseries timeseries) {
    List<Object>[]	result;
    int			i;
    TimeseriesPoint	point;
    DateFormat		tsformat;
    
    result    = new ArrayList[1];
    result[0] = new ArrayList();
    tsformat  = m_TimestampFormat.toDateFormat();
    for (i = 0; i < timeseries.size(); i++) {
      point = (TimeseriesPoint) timeseries.toList().get(i);
      if (m_AddTimestamp)
	result[0].add(tsformat.format(point.getTimestamp()));
      result[0].add(point.getValue());
    }
    
    return result;
  }
}

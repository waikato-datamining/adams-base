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
 * RemoveByDataType.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.report;

import java.util.Arrays;
import java.util.HashSet;

import adams.data.container.DataContainer;

/**
 <!-- globalinfo-start -->
 * Removes all fields from the report that match the defined data types. If matching is inverted, the filter keeps all defined data types instead and removes all others.
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
 * <pre>-data-type &lt;S|N|B|U&gt; [-data-type ...] (property: dataTypes)
 * &nbsp;&nbsp;&nbsp;The data types to remove (or keep, if matching is inverted).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-invert-matching (property: invertMatching)
 * &nbsp;&nbsp;&nbsp;If enabled the data types are retained rather than deleted, all that aren't 
 * &nbsp;&nbsp;&nbsp;listed are then removed instead.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveByDataType
  extends AbstractReportFilter {

  /** for serialization. */
  private static final long serialVersionUID = 2459960819587891448L;

  /** the data types to remove. */
  protected DataType[] m_DataTypes;
  
  /** whether to invert the matching, ie keeping the types and remove all others. */
  protected boolean m_InvertMatching;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Removes all fields from the report that match the defined "
	+ "data types. If matching is inverted, the filter keeps all defined "
	+ "data types instead and removes all others.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "data-type", "dataTypes",
	    new DataType[0]);

    m_OptionManager.add(
	    "invert-matching", "invertMatching",
	    false);
  }

  /**
   * Sets the data types to remove.
   *
   * @param value	the data types
   */
  public void setDataTypes(DataType[] value) {
    m_DataTypes = value;
    reset();
  }

  /**
   * Returns the data types to remove.
   *
   * @return		the data types
   */
  public DataType[] getDataTypes() {
    return m_DataTypes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String dataTypesTipText() {
    return "The data types to remove (or keep, if matching is inverted).";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if to invert
   */
  public void setInvertMatching(boolean value) {
    m_InvertMatching = value;
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if inverted
   */
  public boolean getInvertMatching() {
    return m_InvertMatching;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String invertMatchingTipText() {
    return 
	"If enabled the data types are retained rather than deleted, all "
	+ "that aren't listed are then removed instead.";
  }

  /**
   * Performs the actual filtering.
   *
   * @param data	the data to filter
   * @return		the filtered data
   */
  @Override
  protected DataContainer processData(DataContainer data) {
    Report		oldReport;
    Report		newReport;
    HashSet<DataType>	types;
    
    if (!(data instanceof MutableReportHandler))
      return data;
    
    oldReport = ((MutableReportHandler) data).getReport();
    newReport = Report.newInstance(oldReport);
    types     = new HashSet<DataType>(Arrays.asList(DataType.values()));
    
    for (AbstractField field: oldReport.getFields()) {
      if (m_InvertMatching) {
	if (types.contains(field.getDataType())) {
	  newReport.addField(field);
	  newReport.setValue(field, oldReport.getValue(field));
	}
      }
      else {
	if (!types.contains(field.getDataType())) {
	  newReport.addField(field);
	  newReport.setValue(field, oldReport.getValue(field));
	}
      }
    }
    
    ((MutableReportHandler) data).setReport(newReport);
    
    return data;
  }
}

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
 * AddMetaData.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.timeseries;

import java.util.List;

import adams.data.featureconverter.HeaderDefinition;
import adams.data.report.DataType;

/**
 <!-- globalinfo-start -->
 * Meta-generator that can add database ID and container ID.
 * <br><br>
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
 * <pre>-generator &lt;adams.data.timeseries.AbstractTimeseriesFeatureGenerator&gt; (property: generator)
 * &nbsp;&nbsp;&nbsp;The base generator to use.
 * &nbsp;&nbsp;&nbsp;default: adams.data.timeseries.Values -converter \"adams.data.featureconverter.SpreadSheetFeatureConverter -data-row-type adams.data.spreadsheet.DenseDataRow -spreadsheet-type adams.data.spreadsheet.SpreadSheet\"
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AddMetaData
  extends AbstractMetaTimeseriesFeatureGenerator {
  
  /** for serialization. */
  private static final long serialVersionUID = 8819500364007702561L;

  /** whether to add the database ID. */
  protected boolean m_AddDatabaseID;
  
  /** whether to add the container ID. */
  protected boolean m_AddID;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Meta-generator that can add database ID and container ID.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "add-database-id", "addDatabaseID",
	    false);

    m_OptionManager.add(
	    "add-id", "addID",
	    false);
  }

  /**
   * Returns the default generator to use.
   * 
   * @return		the generator
   */
  @Override
  protected AbstractTimeseriesFeatureGenerator getDefaultGenerator() {
    return new Values();
  }

  /**
   * Sets whether to add the database ID.
   *
   * @param value	true if to add database ID
   */
  public void setAddDatabaseID(boolean value) {
    m_AddDatabaseID = value;
    reset();
  }

  /**
   * Returns whether to add the database ID.
   *
   * @return		true if to add database ID
   */
  public boolean getAddDatabaseID() {
    return m_AddDatabaseID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addDatabaseIDTipText() {
    return "If enabled, the database ID of the container gets added to the data.";
  }

  /**
   * Sets whether to add the ID.
   *
   * @param value	true if to add ID
   */
  public void setAddID(boolean value) {
    m_AddID = value;
    reset();
  }

  /**
   * Returns whether to add the ID.
   *
   * @return		true if to add ID
   */
  public boolean getAddID() {
    return m_AddID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addIDTipText() {
    return "If enabled, the ID of the container gets added to the data.";
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
    
    result = m_Generator.createHeader(timeseries);
    
    // ID
    if (m_AddID)
      result.add(0, "ID", DataType.STRING);
    
    // database ID
    if (m_AddDatabaseID)
      result.add(0, "DatabaseID", DataType.NUMERIC);

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
    
    result = m_Generator.generateRows(timeseries);

    for (List<Object> row: result) {
      // ID
      if (m_AddID)
	row.add(0, timeseries.getID());

      // database ID
      if (m_AddDatabaseID)
	row.add(0, timeseries.getDatabaseID());
    }

    return result;
  }
}

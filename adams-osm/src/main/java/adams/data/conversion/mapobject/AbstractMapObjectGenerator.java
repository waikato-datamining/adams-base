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
 * AbstractMapObjectGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.mapobject.MetaDataSupporter;
import adams.data.mapobject.TimestampSupporter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 * Ancestor for generators of {@link MapObject}s.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of {@link MapObject} to generate
 */
public abstract class AbstractMapObjectGenerator<T extends MapObject>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -9173206961610198326L;
  
  /** the name of the layer. */
  protected String m_Layer;

  /** the index of the column with the timestamp information (optional). */
  protected SpreadSheetColumnIndex m_Timestamp;

  /** the actual index of the timestamp column. */
  protected int m_TimestampIndex;

  /** the additional attributes to store in the mapobject. */
  protected SpreadSheetColumnRange m_AdditionalAttributes;

  /** the actual indices of the additional attributes to store in the mapobject. */
  protected int[] m_AdditionalAttributesIndices;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "layer", "layer",
	    getDefaultLayer());
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Timestamp            = new SpreadSheetColumnIndex();
    m_AdditionalAttributes = new SpreadSheetColumnRange();
  }
  
  /**
   * Returns the default name for the feature type.
   * 
   * @return		the name of the feature type to generate
   */
  protected String getDefaultLayer() {
    return "Default";
  }

  /**
   * Sets the name of the layer.
   *
   * @param value	the name
   */
  public void setLayer(String value) {
    m_Layer = value;
    reset();
  }

  /**
   * Returns the name of the layer.
   *
   * @return		the name
   */
  public String getLayer() {
    return m_Layer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String layerTipText() {
    return "The name of the layer.";
  }

  /**
   * Sets the column containing the timestamp for the mapobject.
   *
   * @param value	the column
   */
  public void setTimestamp(SpreadSheetColumnIndex value) {
    m_Timestamp = value;
    reset();
  }

  /**
   * Returns the column containing the timestamp for the mapobject.
   *
   * @return		the column
   */
  public SpreadSheetColumnIndex getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timestampTipText() {
    return "The column to obtain the timestamp from for the map object (optional).";
  }

  /**
   * Sets the range of columns of additional attributes to add to the map object.
   *
   * @param value	the additional columns
   */
  public void setAdditionalAttributes(SpreadSheetColumnRange value) {
    m_AdditionalAttributes = value;
    reset();
  }

  /**
   * Returns the range of columns of additional attributes to add to the map object.
   *
   * @return		the additional columns
   */
  public SpreadSheetColumnRange getAdditionalAttributes() {
    return m_AdditionalAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalAttributesTipText() {
    return "The range of column to add to the map object as well.";
  }

  /**
   * Returns the type of data the generator creates.
   * 
   * @return		the data type
   */
  public abstract Class generates();
  
  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "layer", m_Layer, "Layer: ");
  }

  /**
   * Checks the spreadsheet and throws an exception if it fails.
   * <p/>
   * Default implementation only ensures that data is present.
   * 
   * @param sheet	the spreadsheet to check
   */
  protected void check(SpreadSheet sheet) {
    if (sheet == null)
      throw new IllegalArgumentException("No spreadsheet supplied!");
  }

  /**
   * Initializes the internal state with the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to initialize with
   */
  protected void init(SpreadSheet sheet) {
    m_Timestamp.setData(sheet);
    m_TimestampIndex = m_Timestamp.getIntIndex();
    m_AdditionalAttributes.setData(sheet);
    m_AdditionalAttributesIndices = m_AdditionalAttributes.getIntIndices();
  }
  
  /**
   * Transfers the timestamp from the row to the map-object.
   * 
   * @param row		the row to get the timestamp from
   * @param mapobject	the object to update
   */
  protected void addTimestamp(Row row, TimestampSupporter mapobject) {
    if (!row.hasCell(m_TimestampIndex) || row.getCell(m_TimestampIndex).isMissing())
      return;
    mapobject.setTimestamp(row.getCell(m_TimestampIndex).toAnyDateType());
  }
  
  /**
   * Transfers the meta-data from the row to the map-object.
   * 
   * @param row		the row to get the meta-data from
   * @param mapobject	the object to update
   */
  protected void addMetaData(Row row, MetaDataSupporter mapobject) {
    for (int col: m_AdditionalAttributesIndices) {
      if (!row.hasCell(col) || row.getCell(col).isMissing())
	continue;

      mapobject.addMetaData(
	  row.getOwner().getColumnName(col), 
	  row.getCell(col).getNative());
    }
  }
  
  /**
   * Post-processes the mapobject.
   * 
   * @param row		the row to get the data from
   * @param mapobject	the object to post-process
   */
  protected void postProcess(Row row, MapObject mapobject) {
    if (mapobject instanceof MetaDataSupporter)
      addMetaData(row, (MetaDataSupporter) mapobject); 
    if ((m_TimestampIndex != -1) && (mapobject instanceof TimestampSupporter))
      addTimestamp(row, (TimestampSupporter) mapobject);
  }
  
  /**
   * Performs the actual generation of the objects.
   * 
   * @param sheet	the spreadsheet to use
   * @return		the generated objects
   */
  protected abstract T[] doGenerate(SpreadSheet sheet);
  
  /**
   * Generates map objects from the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to process
   * @return		the generated map objects
   */
  public T[] generate(SpreadSheet sheet) {
    T[]		result;
    
    check(sheet);
    init(sheet);
    result = doGenerate(sheet);
    
    return result;
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @return		the shallow copy
   */
  public AbstractMapObjectGenerator shallowCopy() {
    return shallowCopy(false);
  }

  /**
   * Returns a shallow copy of itself, i.e., based on the commandline options.
   *
   * @param expand	whether to expand variables to their current values
   * @return		the shallow copy
   */
  public AbstractMapObjectGenerator shallowCopy(boolean expand) {
    return (AbstractMapObjectGenerator) OptionUtils.shallowCopy(this, expand);
  }
}

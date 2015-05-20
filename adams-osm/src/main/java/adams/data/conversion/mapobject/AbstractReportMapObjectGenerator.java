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
 * AbstractReportMapObjectGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapObject;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.core.option.OptionUtils;
import adams.data.mapobject.MetaDataSupporter;
import adams.data.mapobject.TimestampSupporter;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 * Ancestor for generators of {@link MapObject}s.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of {@link MapObject} to generate
 */
public abstract class AbstractReportMapObjectGenerator<T extends MapObject>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -9173206961610198326L;
  
  /** the name of the layer. */
  protected String m_Layer;

  /** the field with the timestamp information (optional). */
  protected Field m_Timestamp;

  /** the additional fields to store in the mapobject. */
  protected Field[] m_AdditionalAttributes;
  
  /** for parsing dates. */
  protected DateFormat m_DateFormat;
  
  /** for parsing date/times. */
  protected DateFormat m_DateTimeFormat;

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
    
    m_Timestamp            = new Field("timestamp", DataType.STRING);
    m_AdditionalAttributes = new Field[0];
    m_DateFormat           = DateUtils.getDateFormatter();
    m_DateTimeFormat       = DateUtils.getTimestampFormatter();
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
   * Sets the field the timestamp for the mapobject.
   *
   * @param value	the field
   */
  public void setTimestamp(Field value) {
    m_Timestamp = value;
    reset();
  }

  /**
   * Returns the field the timestamp for the mapobject.
   *
   * @return		the field
   */
  public Field getTimestamp() {
    return m_Timestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timestampTipText() {
    return "The field to obtain the timestamp from for the map object (optional).";
  }

  /**
   * Sets the additional fields to add to the map object.
   *
   * @param value	the additional fields
   */
  public void setAdditionalAttributes(Field[] value) {
    m_AdditionalAttributes = value;
    reset();
  }

  /**
   * Returns the additional fields to add to the map object.
   *
   * @return		the additional fields
   */
  public Field[] getAdditionalAttributes() {
    return m_AdditionalAttributes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String additionalAttributesTipText() {
    return "The fields to add to the map object as well.";
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
   * Checks the report and throws an exception if it fails.
   * <br><br>
   * Default implementation only ensures that data is present.
   * 
   * @param sheet	the spreadsheet to check
   */
  protected void check(Report report) {
    if (report == null)
      throw new IllegalArgumentException("No report supplied!");
  }
  
  /**
   * Returns the given value from the report. Attempts to convert string
   * values if encountered.
   * 
   * @param report	the report to obtain the value from
   * @param field	the field to obtain
   * @return		the numeric value, NaN if not available or failed to convert
   */
  protected double getNumericValue(Report report, Field field) {
    double	result;
    Object	obj;
    
    result = Double.NaN;
    
    if (report.hasValue(field)) {
      obj = report.getValue(field);
      if (obj instanceof String) {
	try {
	  result = Double.parseDouble((String) obj);
	}
	catch (Exception e) {
	  result = Double.NaN;
	}
      }
      else if (obj instanceof Number) {
	result = ((Number) obj).doubleValue();
      }
    }
    
    return result;
  }
  
  /**
   * Transfers the timestamp from the report to the map-object.
   * 
   * @param report	the report to use
   * @param mapobject	the object to update
   */
  protected void addTimestamp(Report report, TimestampSupporter mapobject) {
    if (!report.hasField(m_Timestamp))
      return;
    mapobject.setTimestamp(m_DateFormat.parse(report.getStringValue(m_Timestamp)));
  }
  
  /**
   * Transfers the meta-data from the report to the map-object.
   * 
   * @param report	the report to use
   * @param mapobject	the object to update
   */
  protected void addMetaData(Report report, MetaDataSupporter mapobject) {
    for (Field field: m_AdditionalAttributes) {
      if (!report.hasField(field))
	return;
      mapobject.addMetaData(
	  field.getName(), 
	  report.getValue(field));
    }
  }
  
  /**
   * Post-processes the mapobject.
   * 
   * @param report	the report to use
   * @param mapobject	the object to post-process
   */
  protected void postProcess(Report report, MapObject mapobject) {
    if (mapobject instanceof MetaDataSupporter)
      addMetaData(report, (MetaDataSupporter) mapobject); 
    if (mapobject instanceof TimestampSupporter)
      addTimestamp(report, (TimestampSupporter) mapobject);
  }
  
  /**
   * Performs the actual generation of the objects.
   * 
   * @param report	the report to use
   * @return		the generated objects
   */
  protected abstract T doGenerate(Report report);
  
  /**
   * Generates map objects from the given report.
   * 
   * @param report	the report to use
   * @return		the generated map objects
   */
  public T generate(Report report) {
    T		result;
    
    check(report);
    result = doGenerate(report);
    
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

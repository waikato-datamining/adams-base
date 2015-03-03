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
 * AbstractReportMapMarkerGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import adams.core.QuickInfoHelper;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 * Ancestor for generators that generate {@link MapMarker} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractReportMapMarkerGenerator
  extends AbstractReportMapObjectGenerator<MapMarker> {

  /** for serialization. */
  private static final long serialVersionUID = -8754565176631384914L;

  /** the field in the report to use as latitude. */
  protected Field m_Latitude;

  /** the field in the report to use as longitude. */
  protected Field m_Longitude;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "latitude", "latitude",
	    new Field("lat", DataType.NUMERIC));

    m_OptionManager.add(
	    "longitude", "longitude",
	    new Field("lon", DataType.NUMERIC));
  }
  
  /**
   * Sets the field to use for latitude.
   *
   * @param value	the field
   */
  public void setLatitude(Field value) {
    m_Latitude = value;
    reset();
  }

  /**
   * Returns the field in use for latitude.
   *
   * @return		the field
   */
  public Field getLatitude() {
    return m_Latitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String latitudeTipText() {
    return "The field in the report that contains the latitude.";
  }
  
  /**
   * Sets the field to use for longitude.
   *
   * @param value	the field
   */
  public void setLongitude(Field value) {
    m_Longitude = value;
    reset();
  }

  /**
   * Returns the field in use for longitude.
   *
   * @return		the field
   */
  public Field getLongitude() {
    return m_Longitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String longitudeTipText() {
    return "The field in the report that contains the longitude.";
  }

  /**
   * Returns the type of data the generator creates.
   * 
   * @return		the data type(s)
   */
  @Override
  public Class generates() {
    return MapMarker.class;
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "latitude", m_Latitude, ", lat: ");
    result += QuickInfoHelper.toString(this, "longitude", m_Longitude, ", lon: ");

    return result;
  }
  
  /**
   * Checks the report and throws an exception if it fails.
   * 
   * @param report	the report to check
   */
  @Override
  protected void check(Report report) {
    super.check(report);
    
    if (!report.hasValue(m_Latitude))
      throw new IllegalStateException("Failed to locate latitude field: " + m_Latitude);
    if (!report.hasValue(m_Longitude))
      throw new IllegalStateException("Failed to locate longitude field: " + m_Longitude);
  }
}

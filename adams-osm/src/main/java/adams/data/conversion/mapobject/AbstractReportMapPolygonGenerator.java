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
 * AbstractReportMapPolygonGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import adams.core.QuickInfoHelper;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 * Ancestor for generators that generate {@link MapPolygon} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractReportMapPolygonGenerator
  extends AbstractReportMapObjectGenerator<MapPolygon> {

  /** for serialization. */
  private static final long serialVersionUID = -8754565176631384914L;

  /** the fields for the polygon latitudes. */
  protected Field[] m_Latitudes;

  /** the fields for the polygon longitudes. */
  protected Field[] m_Longitudes;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "latitude", "latitudes",
	    new Field[0]);

    m_OptionManager.add(
	    "longitude", "longitudes",
	    new Field[0]);
  }

  /**
   * Sets the fields containing the latitudes for the polygons.
   *
   * @param value	the fields
   */
  public void setLatitudes(Field[] value) {
    m_Latitudes = value;
    reset();
  }

  /**
   * Returns the fields containing the latitudes for the polygons.
   *
   * @return		the fields
   */
  public Field[] getLatitudes() {
    return m_Latitudes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String latitudesTipText() {
    return "The fields containing the latitudes for the polygons.";
  }

  /**
   * Sets the fields containing the longitudes for the polygons.
   *
   * @param value	the fields
   */
  public void setLongitudes(Field[] value) {
    m_Longitudes = value;
    reset();
  }

  /**
   * Returns the fields containing the longitudes for the polygons.
   *
   * @return		the fields
   */
  public Field[] getLongitudes() {
    return m_Longitudes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String longitudesTipText() {
    return "The fields containing the longitudes for the polygons.";
  }

  /**
   * Returns the type of data the generator creates.
   * 
   * @return		the data type(s)
   */
  @Override
  public Class generates() {
    return MapPolygon.class;
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
    result += QuickInfoHelper.toString(this, "latitudes", m_Latitudes, ", lat: ");
    result += QuickInfoHelper.toString(this, "longitudes", m_Longitudes, ", lon: ");

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

    if (m_Latitudes.length != m_Longitudes.length)
      throw new IllegalStateException("Number of fields for lat and lon differ: " + m_Latitudes.length + " != " + m_Longitudes.length);

    if (m_Latitudes.length < 3)
      throw new IllegalStateException("At least three corners of a polygon are required, provided: " + m_Latitudes.length);
  }
}

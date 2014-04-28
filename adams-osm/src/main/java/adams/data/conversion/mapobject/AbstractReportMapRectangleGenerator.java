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
 * AbstractReportMapRectangleGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

import adams.core.QuickInfoHelper;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 * Ancestor for generators that generate {@link MapRectangle} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractReportMapRectangleGenerator
  extends AbstractReportMapObjectGenerator<MapRectangle> {

  /** for serialization. */
  private static final long serialVersionUID = -8754565176631384914L;

  /** the field of the topleft latitude. */
  protected Field m_TopLeftLatitude;

  /** the field of the topleft longitude. */
  protected Field m_TopLeftLongitude;

  /** the field of the bottomright latitude. */
  protected Field m_BottomRightLatitude;

  /** the field of the bottomright longitude. */
  protected Field m_BottomRightLongitude;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "top-left-latitude", "topLeftLatitude",
	    new Field("top-left-lat", DataType.NUMERIC));

    m_OptionManager.add(
	    "top-left-longitude", "topLeftLongitude",
	    new Field("top-left-lon", DataType.NUMERIC));

    m_OptionManager.add(
	    "bottom-right-latitude", "bottomRightLatitude",
	    new Field("bottom-right-lat", DataType.NUMERIC));

    m_OptionManager.add(
	    "bottom-right-longitude", "bottomRightLongitude",
	    new Field("bottom-right-lat", DataType.NUMERIC));
  }

  /**
   * Sets the field containing the latitude of the top-left corner.
   *
   * @param value	the field
   */
  public void setTopLeftLatitude(Field value) {
    m_TopLeftLatitude = value;
    reset();
  }

  /**
   * Returns the field containing the latitude of the top-left corner.
   *
   * @return		the field
   */
  public Field getTopLeftLatitude() {
    return m_TopLeftLatitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String topLeftLatitudeTipText() {
    return "The field containing the latitude of the top-left corner.";
  }

  /**
   * Sets the field containing the longitude of the top-left corner.
   *
   * @param value	the field
   */
  public void setTopLeftLongitude(Field value) {
    m_TopLeftLongitude = value;
    reset();
  }

  /**
   * Returns the field containing the longitude of the top-left corner.
   *
   * @return		the field
   */
  public Field getTopLeftLongitude() {
    return m_TopLeftLongitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String topLeftLongitudeTipText() {
    return "The field containing the longitude of the top-left corner.";
  }

  /**
   * Sets the field containing the latitude of the bottom-right corner.
   *
   * @param value	the field
   */
  public void setBottomRightLatitude(Field value) {
    m_BottomRightLatitude = value;
    reset();
  }

  /**
   * Returns the field containing the latitude of the bottom-right corner.
   *
   * @return		the field
   */
  public Field getBottomRightLatitude() {
    return m_BottomRightLatitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bottomRightLatitudeTipText() {
    return "The field containing the latitude of the bottom-right corner.";
  }

  /**
   * Sets the field containing the longitude of the bottom-right corner.
   *
   * @param value	the field
   */
  public void setBottomRightLongitude(Field value) {
    m_BottomRightLongitude = value;
    reset();
  }

  /**
   * Returns the field containing the longitude of the bottom-right corner.
   *
   * @return		the field
   */
  public Field getBottomRightLongitude() {
    return m_BottomRightLongitude;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bottomRightLongitudeTipText() {
    return "The field containing the longitude of the bottom-right corner.";
  }

  /**
   * Returns the type of data the generator creates.
   * 
   * @return		the data type(s)
   */
  @Override
  public Class generates() {
    return MapRectangle.class;
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
    result += QuickInfoHelper.toString(this, "topLeftLatitude", m_TopLeftLatitude, ", top-left-lat: ");
    result += QuickInfoHelper.toString(this, "topLeftLongitude", m_TopLeftLongitude, ", top-left-lon: ");
    result += QuickInfoHelper.toString(this, "bottomRightLatitude", m_BottomRightLatitude, ", bottom-right-lat: ");
    result += QuickInfoHelper.toString(this, "bottomRightLongitude", m_BottomRightLongitude, ", bottom-right-lon: ");

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
    
    if (!report.hasValue(m_TopLeftLatitude))
      throw new IllegalStateException("Failed to locate top-left latitude field: " + m_TopLeftLatitude);
    if (!report.hasValue(m_TopLeftLongitude))
      throw new IllegalStateException("Failed to locate top-left longitude field: " + m_TopLeftLongitude);

    if (!report.hasValue(m_BottomRightLatitude))
      throw new IllegalStateException("Failed to locate bottom-right latitude field: " + m_BottomRightLatitude);
    if (!report.hasValue(m_BottomRightLongitude))
      throw new IllegalStateException("Failed to locate bottom-right longitude field: " + m_BottomRightLongitude);
  }
}

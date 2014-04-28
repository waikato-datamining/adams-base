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
 * AbstractMapMarkerGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Ancestor for generators that generate {@link MapMarker} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMapMarkerGenerator
  extends AbstractMapObjectGenerator<MapMarker> {

  /** for serialization. */
  private static final long serialVersionUID = -8754565176631384914L;

  /** the index of the GPS coordinates column. */
  protected SpreadSheetColumnIndex m_GPS;

  /** the actual index of the GPS coordinates column. */
  protected int m_GPSIndex;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "gps", "GPS",
	    new SpreadSheetColumnIndex());
  }

  /**
   * Sets the index of the column containing the GPS objects.
   *
   * @param value	the column index
   */
  public void setGPS(SpreadSheetColumnIndex value) {
    m_GPS = value;
    reset();
  }

  /**
   * Returns the index of the column containing the GPS objects.
   *
   * @return		the column index
   */
  public SpreadSheetColumnIndex getGPS() {
    return m_GPS;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String GPSTipText() {
    return "The index of the column containing the GPS objects.";
  }

  /**
   * Returns the type of data the generator creates.
   * 
   * @return		the data type(s)
   */
  @Override
  public Class generates() {
    return MapMarker[].class;
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
    result += QuickInfoHelper.toString(this, "GPS", m_GPS, ", GPS: ");

    return result;
  }
  
  /**
   * Checks the spreadsheet and throws an exception if it fails.
   * 
   * @param sheet	the spreadsheet to check
   */
  @Override
  protected void check(SpreadSheet sheet) {
    super.check(sheet);
    
    m_GPS.setData(sheet);
    if (m_GPS.getIntIndex() == -1)
      throw new IllegalStateException("Failed to locate column: " + m_GPS.getIndex());
  }
  
  /**
   * Initializes the internal state with the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to initialize with
   */
  @Override
  protected void init(SpreadSheet sheet) {
    super.init(sheet);
    
    m_GPSIndex = m_GPS.getIntIndex();
  }
}

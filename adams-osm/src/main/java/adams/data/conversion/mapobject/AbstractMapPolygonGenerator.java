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
 * AbstractMapPolygonGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 * Ancestor for generators that generate {@link MapPolygon} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMapPolygonGenerator
  extends AbstractMapObjectGenerator<MapPolygon> {

  /** for serialization. */
  private static final long serialVersionUID = -8754565176631384914L;

  /** the columns for the polygon coordinates. */
  protected SpreadSheetColumnRange m_Coordinates;
  
  /** the actual indices of the columns. */
  protected int[] m_CoordinatesIndices;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "coordinates", "coordinates",
	    new SpreadSheetColumnRange());
  }

  /**
   * Sets the range of columns containing the GPS objects for the polygons.
   *
   * @param value	the column range
   */
  public void setCoordinates(SpreadSheetColumnRange value) {
    m_Coordinates = value;
    reset();
  }

  /**
   * Returns the range of columns containing the GPS objects for the polygons.
   *
   * @return		the column range
   */
  public SpreadSheetColumnRange getCoordinates() {
    return m_Coordinates;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String coordinatesTipText() {
    return "The range of columns containing the GPS objects for the polygons.";
  }

  /**
   * Returns the type of data the generator creates.
   * 
   * @return		the data type(s)
   */
  @Override
  public Class generates() {
    return MapPolygon[].class;
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
    result += QuickInfoHelper.toString(this, "coordinates", m_Coordinates, ", coordinates: ");

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
    
    m_Coordinates.setData(sheet);
    if (m_Coordinates.getIntIndices().length == 0)
      throw new IllegalStateException("Failed to locate any coordinate columns: " + m_Coordinates.getRange());
  }
  
  /**
   * Initializes the internal state with the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to initialize with
   */
  @Override
  protected void init(SpreadSheet sheet) {
    super.init(sheet);
    
    m_CoordinatesIndices = m_Coordinates.getIntIndices();
  }
}

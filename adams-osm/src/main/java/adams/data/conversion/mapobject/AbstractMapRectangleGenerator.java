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
 * AbstractMapRectangleGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

import adams.core.QuickInfoHelper;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;

/**
 * Ancestor for generators that generate {@link MapRectangle} objects.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMapRectangleGenerator
  extends AbstractMapObjectGenerator<MapRectangle> {

  /** for serialization. */
  private static final long serialVersionUID = -8754565176631384914L;

  /** the index of the topleft coordinates column. */
  protected SpreadSheetColumnIndex m_TopLeft;

  /** the actual index of the topleft coordinates column. */
  protected int m_TopLeftIndex;

  /** the index of the bottomright coordinates column. */
  protected SpreadSheetColumnIndex m_BottomRight;

  /** the actual index of the bottomright coordinates column. */
  protected int m_BottomRightIndex;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "top-left", "topLeft",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "bottom-right", "bottomRight",
	    new SpreadSheetColumnIndex());
  }

  /**
   * Sets the index of the column containing the GPS objects of the top-left corner.
   *
   * @param value	the column index
   */
  public void setTopLeft(SpreadSheetColumnIndex value) {
    m_TopLeft = value;
    reset();
  }

  /**
   * Returns the index of the column containing the GPS objects of the top-left corner.
   *
   * @return		the column index
   */
  public SpreadSheetColumnIndex getTopLeft() {
    return m_TopLeft;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String topLeftTipText() {
    return "The index of the column containing the GPS objects of the top-left corner.";
  }

  /**
   * Sets the index of the column containing the GPS objects of the bottom-right corner.
   *
   * @param value	the column index
   */
  public void setBottomRight(SpreadSheetColumnIndex value) {
    m_BottomRight = value;
    reset();
  }

  /**
   * Returns the index of the column containing the GPS objects of the bottom-right corner.
   *
   * @return		the column index
   */
  public SpreadSheetColumnIndex getBottomRight() {
    return m_BottomRight;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String bottomRightTipText() {
    return "The index of the column containing the GPS objects of the bottom-right corner.";
  }

  /**
   * Returns the type of data the generator creates.
   * 
   * @return		the data type(s)
   */
  @Override
  public Class generates() {
    return MapRectangle[].class;
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
    result += QuickInfoHelper.toString(this, "topLeft", m_TopLeft, ", top-left: ");
    result += QuickInfoHelper.toString(this, "bottomRight", m_BottomRight, ", bottom-right: ");

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
    
    m_TopLeft.setData(sheet);
    if (m_TopLeft.getIntIndex() == -1)
      throw new IllegalStateException("Failed to locate top-left column: " + m_TopLeft.getIndex());
    
    m_BottomRight.setData(sheet);
    if (m_BottomRight.getIntIndex() == -1)
      throw new IllegalStateException("Failed to locate bottom-right column: " + m_BottomRight.getIndex());
  }
  
  /**
   * Initializes the internal state with the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to initialize with
   */
  @Override
  protected void init(SpreadSheet sheet) {
    super.init(sheet);
    
    m_TopLeftIndex     = m_TopLeft.getIntIndex();
    m_BottomRightIndex = m_BottomRight.getIntIndex();
  }
}

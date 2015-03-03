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
 * SimplePolygonGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import adams.core.QuickInfoHelper;
import adams.data.gps.AbstractGPS;
import adams.data.mapobject.SimpleMapPolygon;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetColumnIndex;
import adams.data.spreadsheet.SpreadSheetColumnRange;

/**
 <!-- globalinfo-start -->
 * Generates polygons.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-layer &lt;java.lang.String&gt; (property: layer)
 * &nbsp;&nbsp;&nbsp;The name of the layer.
 * &nbsp;&nbsp;&nbsp;default: SimplePolygonGenerator
 * </pre>
 * 
 * <pre>-coordinates &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: coordinates)
 * &nbsp;&nbsp;&nbsp;The range of columns containing the GPS objects for the polygons.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-name &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The index of the column containing the name (optional).
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-timestamp &lt;adams.data.spreadsheet.SpreadSheetColumnIndex&gt; (property: timestamp)
 * &nbsp;&nbsp;&nbsp;The column to obtain the timestamp from for the map object (optional).
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: An index is a number starting with 1; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-additional-attributes &lt;adams.data.spreadsheet.SpreadSheetColumnRange&gt; (property: additionalAttributes)
 * &nbsp;&nbsp;&nbsp;The range of column to add to the map object as well.
 * &nbsp;&nbsp;&nbsp;default: 
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; apart from column names (case-sensitive), the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-border-color &lt;java.awt.Color&gt; (property: borderColor)
 * &nbsp;&nbsp;&nbsp;The border color for the polygon.
 * &nbsp;&nbsp;&nbsp;default: #0000ff
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;The fill color for the polygon.
 * &nbsp;&nbsp;&nbsp;default: #32646464
 * </pre>
 * 
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for the text.
 * &nbsp;&nbsp;&nbsp;default: helvetica-PLAIN-12
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimplePolygonGenerator
  extends AbstractMapPolygonGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8981130970653219268L;

  /** the index of the column with the name information (optional). */
  protected SpreadSheetColumnIndex m_Name;

  /** the actual index of the name column. */
  protected int m_NameIndex;

  /** the color of the border. */
  protected Color m_BorderColor;

  /** the fill color of the circle. */
  protected Color m_FillColor;
  
  /** the font to use. */
  protected Font m_Font;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates polygons.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "name", "name",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "timestamp", "timestamp",
	    new SpreadSheetColumnIndex());

    m_OptionManager.add(
	    "additional-attributes", "additionalAttributes",
	    new SpreadSheetColumnRange());

    m_OptionManager.add(
	    "border-color", "borderColor",
	    Color.BLUE);

    m_OptionManager.add(
	    "fill-color", "fillColor",
	    new Color(100,100,100,50));

    m_OptionManager.add(
	    "font", "font",
	    new Font("helvetica", Font.PLAIN, 12));
  }

  /**
   * Sets the index of the column containing the name.
   *
   * @param value	the column index
   */
  public void setName(SpreadSheetColumnIndex value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the index of the column containing the name.
   *
   * @return		the column index
   */
  public SpreadSheetColumnIndex getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameTipText() {
    return "The index of the column containing the name (optional).";
  }

  /**
   * Sets the circle color for the collection.
   *
   * @param value	the circle color
   */
  public void setBorderColor(Color value) {
    m_BorderColor = value;
    reset();
  }

  /**
   * Returns the circle color for the collection.
   *
   * @return		the circle color
   */
  public Color getBorderColor() {
    return m_BorderColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String borderColorTipText() {
    return "The border color for the polygon.";
  }

  /**
   * Sets the fill color for the polygon.
   *
   * @param value	the fill color
   */
  public void setFillColor(Color value) {
    m_FillColor = value;
    reset();
  }

  /**
   * Returns the fill color for the polygon.
   *
   * @return		the fill color
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fillColorTipText() {
    return "The fill color for the polygon.";
  }

  /**
   * Sets the font for the text.
   *
   * @param value	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font for the text.
   *
   * @return		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font to use for the text.";
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
    result += QuickInfoHelper.toString(this, "name", m_Name, ", name: ");

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

    m_Name.setData(sheet);
  }

  /**
   * Initializes the internal state with the given spreadsheet.
   * 
   * @param sheet	the spreadsheet to initialize with
   */
  @Override
  protected void init(SpreadSheet sheet) {
    super.init(sheet);
    
    m_NameIndex = m_Name.getIntIndex();
  }
  
  /**
   * Performs the actual generation of the layer.
   * 
   * @param sheet	the spreadsheet to use
   * @return		the generated layer
   */
  @Override
  protected MapPolygon[] doGenerate(SpreadSheet sheet) {
    List<MapPolygon>	result;
    SimpleMapPolygon	mapobject;
    double		lat;
    double		lon;
    String		name;
    List<Coordinate>	coords;
    
    result = new ArrayList<MapPolygon>();

    for (Row row: sheet.rows()) {
      coords = new ArrayList<Coordinate>();
      for (int col: m_CoordinatesIndices) {
	if (!row.hasCell(col) || row.getCell(col).isMissing())
	  continue;
	lat = ((AbstractGPS) row.getCell(col).getObject()).getLatitude().toDecimal();
	lon = ((AbstractGPS) row.getCell(col).getObject()).getLongitude().toDecimal();
	coords.add(new Coordinate(lat, lon));
      }
      mapobject = new SimpleMapPolygon(new Layer(m_Layer), coords);
      if (m_NameIndex > -1) {
	if (row.hasCell(m_NameIndex) && !row.getCell(m_NameIndex).isMissing()) {
	  name = row.getCell(m_NameIndex).getContent();
	  mapobject.setName(name);
	}
      }
      mapobject.setBackColor(m_FillColor);
      mapobject.setColor(m_BorderColor);
      mapobject.setFont(m_Font);
      postProcess(row, mapobject);
      result.add(mapobject);
    }
    
    return result.toArray(new MapPolygon[result.size()]);
  }
}

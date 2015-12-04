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
 * SimpleReportPolygonGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import adams.gui.core.Fonts;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapPolygon;

import adams.core.QuickInfoHelper;
import adams.data.mapobject.SimpleMapPolygon;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Generates polygons.
 * <br><br>
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
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-latitude &lt;adams.data.report.Field&gt; [-latitude ...] (property: latitudes)
 * &nbsp;&nbsp;&nbsp;The fields containing the latitudes for the polygons.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-longitude &lt;adams.data.report.Field&gt; [-longitude ...] (property: longitudes)
 * &nbsp;&nbsp;&nbsp;The fields containing the longitudes for the polygons.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-name &lt;adams.data.report.Field&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The field containing the name (optional).
 * &nbsp;&nbsp;&nbsp;default: name[S]
 * </pre>
 * 
 * <pre>-timestamp &lt;adams.data.report.Field&gt; (property: timestamp)
 * &nbsp;&nbsp;&nbsp;The field to obtain the timestamp from for the map object (optional).
 * &nbsp;&nbsp;&nbsp;default: timestamp[S]
 * </pre>
 * 
 * <pre>-additional-attributes &lt;adams.data.report.Field&gt; [-additional-attributes ...] (property: additionalAttributes)
 * &nbsp;&nbsp;&nbsp;The fields to add to the map object as well.
 * &nbsp;&nbsp;&nbsp;default: 
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
public class SimpleReportPolygonGenerator
  extends AbstractReportMapPolygonGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8981130970653219268L;

  /** the field with the name information (optional). */
  protected Field m_Name;

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
	    new Field("name", DataType.STRING));

    m_OptionManager.add(
	    "timestamp", "timestamp",
	    new Field("timestamp", DataType.STRING));

    m_OptionManager.add(
	    "additional-attributes", "additionalAttributes",
	    new Field[0]);

    m_OptionManager.add(
	    "border-color", "borderColor",
	    Color.BLUE);

    m_OptionManager.add(
	    "fill-color", "fillColor",
	    new Color(100,100,100,50));

    m_OptionManager.add(
	    "font", "font",
	    Fonts.getSansFont());
  }

  /**
   * Sets the field containing the name.
   *
   * @param value	the field
   */
  public void setName(Field value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the field containing the name.
   *
   * @return		the field
   */
  public Field getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String nameTipText() {
    return "The field containing the name (optional).";
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
   * Performs the actual generation of the layer.
   * 
   * @param sheet	the spreadsheet to use
   * @return		the generated layer
   */
  @Override
  protected MapPolygon doGenerate(Report report) {
    SimpleMapPolygon	result;
    double		lat;
    double		lon;
    String		name;
    List<Coordinate>	coords;
    int			i;

      coords = new ArrayList<Coordinate>();
      for (i = 0; i < m_Latitudes.length; i++) {
	lat = getNumericValue(report, m_Latitudes[i]);
	lon = getNumericValue(report, m_Longitudes[i]);
	coords.add(new Coordinate(lat, lon));
      }
      result = new SimpleMapPolygon(new Layer(m_Layer), coords);
      if (report.hasValue(m_Name)) {
	name = report.getStringValue(m_Name);
	result.setName(name);
      }
      result.setBackColor(m_FillColor);
      result.setColor(m_BorderColor);
      result.setFont(m_Font);
      postProcess(report, result);
    
    return result;
  }
}

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
 * SimpleReportRectangleGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import java.awt.Color;
import java.awt.Font;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapRectangle;

import adams.core.QuickInfoHelper;
import adams.data.mapobject.SimpleMapRectangle;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Generates rectangles.
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
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-top-left-latitude &lt;adams.data.report.Field&gt; (property: topLeftLatitude)
 * &nbsp;&nbsp;&nbsp;The field containing the latitude of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: top-left-lat[N]
 * </pre>
 * 
 * <pre>-top-left-longitude &lt;adams.data.report.Field&gt; (property: topLeftLongitude)
 * &nbsp;&nbsp;&nbsp;The field containing the longitude of the top-left corner.
 * &nbsp;&nbsp;&nbsp;default: top-left-lon[N]
 * </pre>
 * 
 * <pre>-bottom-right-latitude &lt;adams.data.report.Field&gt; (property: bottomRightLatitude)
 * &nbsp;&nbsp;&nbsp;The field containing the latitude of the bottom-right corner.
 * &nbsp;&nbsp;&nbsp;default: bottom-right-lat[N]
 * </pre>
 * 
 * <pre>-bottom-right-longitude &lt;adams.data.report.Field&gt; (property: bottomRightLongitude)
 * &nbsp;&nbsp;&nbsp;The field containing the longitude of the bottom-right corner.
 * &nbsp;&nbsp;&nbsp;default: bottom-right-lat[N]
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
 * <pre>-rectangle-color &lt;java.awt.Color&gt; (property: rectangleColor)
 * &nbsp;&nbsp;&nbsp;The rectangle color.
 * &nbsp;&nbsp;&nbsp;default: #0000ff
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;The fill color for the rectangle.
 * &nbsp;&nbsp;&nbsp;default: #c8c8c8c8
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
public class SimpleReportRectangleGenerator
  extends AbstractReportMapRectangleGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8981130970653219268L;

  /** the field with the name information (optional). */
  protected Field m_Name;

  /** the color of the rectangle. */
  protected Color m_RectangleColor;

  /** the fill color of the rectangle. */
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
    return "Generates rectangles.";
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
	    "rectangle-color", "rectangleColor",
	    Color.BLUE);

    m_OptionManager.add(
	    "fill-color", "fillColor",
	    new Color(200,200,200,200));

    m_OptionManager.add(
	    "font", "font",
	    new Font("helvetica", Font.PLAIN, 12));
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
   * Sets the rectangle color for the collection.
   *
   * @param value	the rectangle color
   */
  public void setRectangleColor(Color value) {
    m_RectangleColor = value;
    reset();
  }

  /**
   * Returns the rectangle color for the collection.
   *
   * @return		the rectangle color
   */
  public Color getRectangleColor() {
    return m_RectangleColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String rectangleColorTipText() {
    return "The rectangle color.";
  }

  /**
   * Sets the fill color for the collection.
   *
   * @param value	the fill color
   */
  public void setFillColor(Color value) {
    m_FillColor = value;
    reset();
  }

  /**
   * Returns the fill color for the collection.
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
    return "The fill color for the rectangle.";
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
  protected MapRectangle doGenerate(Report report) {
    SimpleMapRectangle	result;
    double		latTL;
    double		lonTL;
    double		latBR;
    double		lonBR;
    String		name;

    latTL   = getNumericValue(report, m_TopLeftLatitude);
    lonTL   = getNumericValue(report, m_TopLeftLongitude);
    latBR   = getNumericValue(report, m_BottomRightLatitude);
    lonBR   = getNumericValue(report, m_BottomRightLongitude);
    result  = new SimpleMapRectangle(new Layer(m_Layer), new Coordinate(latTL, lonTL), new Coordinate(latBR, lonBR));
    if (report.hasValue(m_Name)) {
      name = report.getStringValue(m_Name);
      result.setName(name);
    }
    result.setBackColor(m_FillColor);
    result.setColor(m_RectangleColor);
    result.setFont(m_Font);
    postProcess(report, result);
    
    return result;
  }
}

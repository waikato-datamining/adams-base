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
 * SimpleReportCircleMarkerGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion.mapobject;

import java.awt.Color;
import java.awt.Font;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.Layer;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

import adams.core.QuickInfoHelper;
import adams.data.mapobject.SimpleMapMarkerCircle;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.data.report.Report;

/**
 <!-- globalinfo-start -->
 * Generates circle markers.
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
 * <pre>-latitude &lt;adams.data.report.Field&gt; (property: latitude)
 * &nbsp;&nbsp;&nbsp;The field in the report that contains the latitude.
 * &nbsp;&nbsp;&nbsp;default: lat[N]
 * </pre>
 * 
 * <pre>-longitude &lt;adams.data.report.Field&gt; (property: longitude)
 * &nbsp;&nbsp;&nbsp;The field in the report that contains the longitude.
 * &nbsp;&nbsp;&nbsp;default: lon[N]
 * </pre>
 * 
 * <pre>-radius &lt;adams.data.report.Field&gt; (property: radius)
 * &nbsp;&nbsp;&nbsp;The field containing the radius.
 * &nbsp;&nbsp;&nbsp;default: radius[N]
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
 * <pre>-circle-color &lt;java.awt.Color&gt; (property: circleColor)
 * &nbsp;&nbsp;&nbsp;The circle color for the point.
 * &nbsp;&nbsp;&nbsp;default: #ffc800
 * </pre>
 * 
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;The fill color for the point.
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
public class SimpleReportCircleMarkerGenerator
  extends AbstractReportMapMarkerGenerator {

  /** for serialization. */
  private static final long serialVersionUID = -8981130970653219268L;

  /** the field with the radius information. */
  protected Field m_Radius;

  /** the field with the name information (optional). */
  protected Field m_Name;

  /** the color of the circle. */
  protected Color m_CircleColor;

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
    return "Generates circle markers.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "radius", "radius",
	    new Field("radius", DataType.NUMERIC));

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
	    "circle-color", "circleColor",
	    Color.ORANGE);

    m_OptionManager.add(
	    "fill-color", "fillColor",
	    new Color(100,100,100,50));

    m_OptionManager.add(
	    "font", "font",
	    new Font("helvetica", Font.PLAIN, 12));
  }

  /**
   * Sets the field containing the radius.
   *
   * @param value	the field
   */
  public void setRadius(Field value) {
    m_Radius = value;
    reset();
  }

  /**
   * Returns the field containing the radius.
   *
   * @return		the field
   */
  public Field getRadius() {
    return m_Radius;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String radiusTipText() {
    return "The field containing the radius.";
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
   * Sets the circle color for the circle.
   *
   * @param value	the circle color
   */
  public void setCircleColor(Color value) {
    m_CircleColor = value;
    reset();
  }

  /**
   * Returns the circle color for the circle.
   *
   * @return		the circle color
   */
  public Color getCircleColor() {
    return m_CircleColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String circleColorTipText() {
    return "The circle color for the point.";
  }

  /**
   * Sets the fill color for the circle.
   *
   * @param value	the fill color
   */
  public void setFillColor(Color value) {
    m_FillColor = value;
    reset();
  }

  /**
   * Returns the fill color for the circle.
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
    return "The fill color for the point.";
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
    result += QuickInfoHelper.toString(this, "radius", m_Radius, ", radius: ");
    result += QuickInfoHelper.toString(this, "name", m_Name, ", name: ");

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
    
    if (!report.hasValue(m_Radius))
      throw new IllegalStateException("Failed to locate radius field: " + m_Radius);
  }
  
  /**
   * Performs the actual generation of the layer.
   * 
   * @param sheet	the spreadsheet to use
   * @return		the generated layer
   */
  @Override
  protected MapMarker doGenerate(Report report) {
    SimpleMapMarkerCircle	result;
    double			lat;
    double			lon;
    double			radius;
    String			name;

    lat    = getNumericValue(report, m_Latitude);
    lon    = getNumericValue(report, m_Longitude);
    radius = getNumericValue(report, m_Radius);
    result = new SimpleMapMarkerCircle(new Layer(m_Layer), new Coordinate(lat, lon), radius);
    if (report.hasValue(m_Name)) {
      name = report.getStringValue(m_Name);
      result.setName(name);
    }
    result.setBackColor(m_FillColor);
    result.setColor(m_CircleColor);
    result.setFont(m_Font);
    postProcess(report, result);
    
    return result;
  }
}

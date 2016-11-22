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
 * ReportColorInstancePaintlet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.instance;

import adams.data.report.DataType;
import adams.data.report.Field;
import adams.gui.core.ColorHelper;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Paintlet for generating a line plot using the color stored in the report.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 * 
 * <pre>-markers-extent &lt;int&gt; (property: markerExtent)
 * &nbsp;&nbsp;&nbsp;The size of the markers in pixels.
 * &nbsp;&nbsp;&nbsp;default: 7
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-markers-disabled &lt;boolean&gt; (property: markersDisabled)
 * &nbsp;&nbsp;&nbsp;If set to true, the markers are disabled.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-always-show-markers &lt;boolean&gt; (property: alwaysShowMarkers)
 * &nbsp;&nbsp;&nbsp;If set to true, the markers are always displayed, not just when zoomed in.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing lines.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-color-field &lt;adams.data.report.Field&gt; (property: colorField)
 * &nbsp;&nbsp;&nbsp;The report field that contains the color information.
 * &nbsp;&nbsp;&nbsp;default: Color[S]
 * </pre>
 * 
 * <pre>-default-color &lt;java.awt.Color&gt; (property: defaultColor)
 * &nbsp;&nbsp;&nbsp;The default color to use if no color information in the report.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-update-container-color &lt;boolean&gt; (property: updateContainerColor)
 * &nbsp;&nbsp;&nbsp;If enabled, the color of the container gets updated with the color determined 
 * &nbsp;&nbsp;&nbsp;by this paintlet.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ReportColorInstancePaintlet
  extends InstanceLinePaintlet {

  private static final long serialVersionUID = -4837316110207980301L;

  /** the report field to get the color from. */
  protected Field m_ColorField;

  /** the default color to use if no color found in report. */
  protected Color m_DefaultColor;

  /** whether to update the color of the container. */
  protected boolean m_UpdateContainerColor;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for generating a line plot using the color stored in the report.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color-field", "colorField",
      new Field("Color", DataType.STRING));

    m_OptionManager.add(
      "default-color", "defaultColor",
      Color.BLACK);

    m_OptionManager.add(
      "update-container-color", "updateContainerColor",
      false);
  }

  /**
   * Sets the report field that contains the color.
   *
   * @param value	the field
   */
  public void setColorField(Field value) {
    m_ColorField = value;
    memberChanged();
  }

  /**
   * Returns the report field that contains the color.
   *
   * @return		the field
   */
  public Field getColorField() {
    return m_ColorField;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorFieldTipText() {
    return "The report field that contains the color information.";
  }

  /**
   * Sets the default color to use when no color information in the report.
   *
   * @param value	the color
   */
  public void setDefaultColor(Color value) {
    m_DefaultColor = value;
    memberChanged();
  }

  /**
   * Returns the default color to use when no color information in the report.
   *
   * @return		the color
   */
  public Color getDefaultColor() {
    return m_DefaultColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String defaultColorTipText() {
    return "The default color to use if no color information in the report.";
  }

  /**
   * Sets whether to update the container's color with the color determined
   * by this paintlet.
   *
   * @param value	true if to update
   */
  public void setUpdateContainerColor(boolean value) {
    m_UpdateContainerColor = value;
    memberChanged();
  }

  /**
   * Returns whether to update the container's color with the color determined
   * by this paintlet.
   *
   * @return		true if to update
   */
  public boolean getUpdateContainerColor() {
    return m_UpdateContainerColor;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String updateContainerColorTipText() {
    return "If enabled, the color of the container gets updated with the color determined by this paintlet.";
  }

  /**
   * Returns the color for the data with the given index.
   *
   * @param index	the index of the spectrum
   * @return		the color for the spectrum
   */
  public Color getColor(int index) {
    Color		result;
    InstanceContainer 	cont;

    result = m_DefaultColor;
    cont   = (InstanceContainer) getDataContainerPanel().getContainerManager().get(index);
    if (cont.getData().getReport().hasValue(m_ColorField)) {
      try {
	result = ColorHelper.valueOf(cont.getData().getReport().getStringValue(m_ColorField));
      }
      catch (Exception e) {
	getLogger().warning("Unparseable color: " + cont.getData().getReport().getValue(m_ColorField));
	result = m_DefaultColor;
      }
    }

    // update container?
    if (m_UpdateContainerColor) {
      if (!cont.getColor().equals(result))
	cont.setColor(result);
    }

    return result;
  }
}

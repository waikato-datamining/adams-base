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

/*
 * AbstractZScorePaintlet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Abstract class for creating z score paintlets.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractZScorePaintlet
  extends AbstractColorPaintlet
  implements AntiAliasingSupporter {

  /** for serialization */
  private static final long serialVersionUID = 6918445466346742103L;

  /**index of the attribute being plotted */
  protected int m_Index;

  /**data from the attribute */
  protected double[] m_Values;

  /** y axis of plot */
  protected AxisPanel m_AxisLeft;

  /** x axis of plot */
  protected AxisPanel m_AxisBottom;

  /** the line color. */
  protected Color m_LineColor;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"line-color", "lineColor",
	Color.LIGHT_GRAY);

    m_OptionManager.add(
	"anti-aliasing-enabled", "antiAliasingEnabled",
	GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  @Override
  protected void initialize() {
    super.initialize();
    m_Index = 0;
  }

  /**
   * Set the color to draw the lines.
   *
   * @param val		color for lines
   */
  public void setLineColor(Color val) {
    m_LineColor = val;
  }

  /**
   * Get the color for drawing the lines
   *
   * @return		color for lines
   */
  public Color getLineColor() {
    return m_LineColor;
  }

  /**
   * Tip text for the line color property.
   *
   * @return		String to describe the property
   */
  public String lineColorTipText() {
    return "Color for drawing the lines.";
  }

  /**
   * Sets whether to use anti-aliasing.
   *
   * @param value	if true then anti-aliasing is used
   */
  public void setAntiAliasingEnabled(boolean value) {
    m_AntiAliasingEnabled = value;
    memberChanged();
  }

  /**
   * Returns whether anti-aliasing is used.
   *
   * @return		true if anti-aliasing is used
   */
  public boolean isAntiAliasingEnabled() {
    return m_AntiAliasingEnabled;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String antiAliasingEnabledTipText() {
    return "If enabled, uses anti-aliasing for drawing lines.";
  }

  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    if(m_Data != null)
      drawData(g);
  }

  /**set the index of the attribute */
  public void setIndex(int ind) {
    m_Index = ind;
    memberChanged();
  }

  /**
   * pass the required parameters for the paintlet
   * @param data			Instances to be plotted
   * @param ind			index of the attribute
   */
  public void parameters(SpreadSheet data, int ind) {
    m_Data = data;
    m_Index = ind;
  }

  protected void drawData(Graphics g) {
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    if (m_Data != null) {
      g.setColor(Color.BLACK);

      m_Values = SpreadSheetUtils.getNumericColumn(m_Data, m_Index);
      m_AxisBottom = getPanel().getPlot().getAxis(Axis.BOTTOM);
      m_AxisLeft = getPanel().getPlot().getAxis(Axis.LEFT);
    }
  }
}
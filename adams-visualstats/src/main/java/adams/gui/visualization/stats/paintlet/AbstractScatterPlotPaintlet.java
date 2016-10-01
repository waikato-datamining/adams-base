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
 * AbstractScatterPlotPaintlet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.core.option.OptionUtils;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractColorProvider;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.DefaultColorProvider;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.core.plot.HitDetectorSupporter;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract class for creating scatterplot paintlets.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractScatterPlotPaintlet
  extends AbstractColorPaintlet
  implements AntiAliasingSupporter, HitDetectorSupporter<AbstractScatterPlotHitDetector> {

  /** for serialization */
  private static final long serialVersionUID = 7191423312364530577L;

  /**index of attribute for x axis */
  protected int m_XIndex;

  /**Index of attribute for y axis */
  protected int m_YIndex;

  /** the index of the categorical attriute for the coloring. */
  protected int m_ColorIndex;

  /** size of the plot points */
  protected int m_Size;

  /**Data to display on the x axis */
  protected double[] m_XData;

  /**Data to display on the y axis */
  protected double[] m_YData;

  /** the values of the coloring axis. */
  protected String[] m_ColorData;

  /** the value - color association. */
  protected Map<String,Color> m_ColorMapping;

  /** the color provider for the coloring. */
  protected AbstractColorProvider m_ColorProvider;

  /** y axis of plot */
  protected AxisPanel m_AxisLeft;

  /** x axis of plot */
  protected AxisPanel m_AxisBottom;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /** the hit detector to use. */
  protected AbstractScatterPlotHitDetector m_HitDetector;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));

    m_OptionManager.add(
      "color-provider", "colorProvider",
      new DefaultColorProvider());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_XIndex      = 0;
    m_YIndex      = 0;
    m_ColorIndex  = -1;
    m_HitDetector = newHitDetector();
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_XData        = null;
    m_YData        = null;
    m_ColorData    = null;
    m_ColorMapping = null;
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

  /**
   * Sets the color provider to use when using a column for the plot colors.
   *
   * @param value	the provider
   */
  public void setColorProvider(AbstractColorProvider value) {
    m_ColorProvider = value;
    memberChanged();
  }

  /**
   * Returns the color provider to use when using a column for the plot colors.
   *
   * @return		the provider
   */
  public AbstractColorProvider getColorProvider() {
    return m_ColorProvider;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorProviderTipText() {
    return "The color provider to use when using a column for the plot colors.";
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

  /**
   * Returns info on scatterpaintlet object
   */
  @Override
  public String toString() {
    return OptionUtils.getCommandLine(this);
  }

  /**
   * Returns the actual color to use. Uses the provided default color if
   * no color mapping active.
   *
   * @param index	the row index
   * @param defColor	the default color
   * @return
   */
  protected Color getActualColor(int index, Color defColor) {
    if (m_ColorMapping == null)
      return defColor;
    else
      return m_ColorMapping.get(m_ColorData[index]);
  }

  /**
   * draws the data on the graphics object
   * @param g		Graphics object to draw on
   */
  protected void drawData(Graphics g) {
    String[]	unique;

    if(m_Data != null) {
      g.setColor(m_Color);

      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

      //arrays of data to be plotted
      if (m_XData == null) {
	m_XData = SpreadSheetUtils.getNumericColumn(m_Data, m_XIndex);
	m_YData = SpreadSheetUtils.getNumericColumn(m_Data, m_YIndex);
	if (m_ColorIndex > -1) {
	  m_ColorData    = SpreadSheetUtils.getColumn(m_Data, m_ColorIndex, false, false);
	  unique         = SpreadSheetUtils.getColumn(m_Data, m_ColorIndex, true, true);
	  m_ColorMapping = new HashMap<>();
	  m_ColorProvider.resetColors();
	  for (String value: unique)
	    m_ColorMapping.put(value, m_ColorProvider.next());
	}
      }

      m_AxisBottom = getPanel().getPlot().getAxis(Axis.BOTTOM);
      m_AxisLeft = getPanel().getPlot().getAxis(Axis.LEFT);
    }
  }

  /**
   * get index of chosen attribute for x axis
   * @return		chosen index
   */
  public int getXIndex() {
    return m_XIndex;
  }

  /**
   * Set the index of attribute for x axis
   * @param val		Index to set
   */
  public void setXIndex(int val) {
    m_XIndex = val;
    memberChanged();
  }

  /**
   * Get index of chosen attribute for y axis
   * @return		chosen index
   */
  public int getYIndex() {
    return m_YIndex;
  }

  /**
   * Set the index of attribute for y axis
   * @param val		Index to set
   */
  public void setYIndex(int val) {
    m_YIndex = val;
    memberChanged();
  }

  /**
   * Get index of chosen attribute for color.
   *
   * @return		chosen index
   */
  public int getColorIndex() {
    return m_ColorIndex;
  }

  /**
   * Set the index of attribute for color.
   *
   * @param val		Index to set
   */
  public void setColorIndex(int val) {
    m_ColorIndex = val;
    memberChanged();
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  public AbstractScatterPlotHitDetector newHitDetector() {
    return null;
  }

  /**
   * Returns the hit detector to use for this paintlet.
   *
   * @return		the detector
   */
  public AbstractScatterPlotHitDetector getHitDetector() {
    return m_HitDetector;
  }
}
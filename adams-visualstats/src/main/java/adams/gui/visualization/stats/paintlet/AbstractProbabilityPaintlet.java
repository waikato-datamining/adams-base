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
 * AbstractProbabilityPaintlet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;

import java.awt.Color;

/**
 * Abstract class for paintlets that plot the regression in the
 * probability plot.
 *
 * @author msf8
 * @version $Revision$
 */
public abstract class AbstractProbabilityPaintlet
  extends AbstractColorPaintlet
  implements AntiAliasingSupporter {

  /** for serialization */
  private static final long serialVersionUID = 6017537002376582174L;

  /** Index of the attribute in the instances */
  protected int m_Index;

  /**Whether to draw a regression line */
  protected boolean m_RegressionLine;

  /** Size of the points */
  protected int m_Size;

  /**Whether to fill the data points */
  protected boolean m_Fill;

  /**x axis of the plot */
  protected AxisPanel m_AxisLeft;

  /**y axis of the plot */
  protected AxisPanel m_AxisBottom;

  /** the sorted data. */
  protected double[] m_Sorted;

  /**Transformed data for the y axis */
  protected double[] m_TransformedY;

  /** Color to fill data points */
  protected Color m_FillColor;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"size", "size", 5);

    m_OptionManager.add(
	"fill-point", "fillPoint", true);

    m_OptionManager.add(
	"fill-color", "fillColor", Color.RED);

    m_OptionManager.add(
	"anti-aliasing-enabled", "antiAliasingEnabled",
	GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_TransformedY = new double[0];
    m_Sorted = new double[0];
  }
  
  /**
   * Set the color for filling the data points
   * @param val		Color for filling points
   */
  public void setFillColor(Color val) {
    m_FillColor = val;
    memberChanged();
  }

  /**
   * get the color for filling the data points
   * @return			Color for filling the data points
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Tip text for the fill color property
   * @return			String describing the property
   */
  public String fillColorTipText() {
    return "Color for filling data point";
  }

  /**
   * Set whether the data points are filled with color
   * @param val			True if data points filled
   */
  public void setFillPoint(boolean val) {
    m_Fill = val;
    memberChanged();
  }

  /**
   * Get whether the data points should be filled with color
   * @return			True if filled
   */
  public boolean getFillPoint() {
    return m_Fill;
  }

  /**
   * Tip text for the fill point property
   * @return			String describing the property
   */
  public String fillPointTipText() {
    return "Whether to fill the data point with solid color";
  }

  /**
   * Set the size of the data points
   * @param val		Size of the data points in pixels
   */
  public void setSize(int val){
    m_Size = val;
    memberChanged();
  }

  /**
   * Get the size of the data points
   * @return			Size of the points in pixels
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Tip text for the data point size property
   * @return
   */
  public String sizeTipText() {
    return "Size of the data points";
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
   * Sets the axes to the correct type for the distribution.
   */
  public abstract void configureAxes();

  /**
   * For calculating the dimensions of the plot area.
   */
  public abstract void calculateDimensions();

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.PAINT;
  }

  /**
   * Set the index of the attribute used
   * @param val			Attribute index
   */
  public void setIndex(int val) {
    m_Index = val;
  }

  /**
   * Set whether a regression line is drawn
   * @param val			True if regression line is to be drawn
   */
  public void setLine(boolean val) {
    m_RegressionLine = val;
  }

  /**
   * Whether a regression line has been implemented
   * for this regresion paintlet
   * @return			True if regression line implemented
   */
  public abstract boolean hasFitLine();
}
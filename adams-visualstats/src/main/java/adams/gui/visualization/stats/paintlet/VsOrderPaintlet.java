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
 * VsOrderPaintlet.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

/**
 <!-- globalinfo-start -->
 * Paints the data for the versus order graph
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-stroke-thickness &lt;float&gt; (property: strokeThickness)
 * &nbsp;&nbsp;&nbsp;The thickness of the stroke.
 * &nbsp;&nbsp;&nbsp;default: 1.0
 * &nbsp;&nbsp;&nbsp;minimum: 0.01
 * </pre>
 *
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;Stroke color for the paintlet
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 *
 * <pre>-size &lt;int&gt; (property: size)
 * &nbsp;&nbsp;&nbsp;Size of the data points
 * &nbsp;&nbsp;&nbsp;default: 5
 * </pre>
 *
 * <pre>-fill-point (property: fillPoint)
 * &nbsp;&nbsp;&nbsp;Whether to fill the data point with solid color
 * </pre>
 *
 * <pre>-fill-color &lt;java.awt.Color&gt; (property: fillColor)
 * &nbsp;&nbsp;&nbsp;color for filling the data points
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class VsOrderPaintlet
  extends AbstractColorPaintlet
  implements AntiAliasingSupporter {

  /** for serialization */
  private static final long serialVersionUID = -5195847858691868288L;

  /** Index of residuals attribute in the instances */
  protected int m_Index;

  /** Color to fill the data points */
  protected Color m_FillColor;

  /** Whether to fill the data points */
  protected boolean m_Fill;

  /** Size of the data points */
  protected int m_Size;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  @Override
  public String globalInfo() {
    return "Paints the data for the versus order graph";
  }

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
   * Set whether the data points should be filled
   * @param val			True if points filled
   */
  public void setFillPoint(boolean val) {
    m_Fill = val;
    memberChanged();
  }

  /**
   * get whether the data points should be filled
   * @return			true if data points filled
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
   * Set the color for filling the points
   * @param val			Fill color
   */
  public void setFillColor(Color val) {
    m_FillColor = val;
    memberChanged();
  }

  /**
   * Get the color to fill the points
   * @return			Color to fill
   */
  public Color getFillColor() {
    return m_FillColor;
  }

  /**
   * Tip text for the color property
   * @return			String describing the property
   */
  public String fillColorTipText() {
    return "color for filling the data points";
  }

  /**
   * Set the size for the data points
   * @param val			Size in pixels
   */
  public void setSize(int val){
    m_Size = val;
    memberChanged();
  }

  /**
   * Get the size of the data points
   * @return			Size in pixels
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Tip text for the size property
   * @return			String describing the property
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
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
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
    if(m_Instances != null) {
      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

      AxisPanel axisBottom = getPanel().getPlot().getAxis(Axis.BOTTOM);
      AxisPanel axisLeft = getPanel().getPlot().getAxis(Axis.LEFT);

      double[] residuals = m_Instances.attributeToDoubleArray(m_Index);

      g.setColor(Color.BLACK);
      g.drawLine(0, axisLeft.valueToPos(0), axisBottom.valueToPos(axisBottom.getMaximum()), axisLeft.valueToPos(0));
      for(int i = 0; i< residuals.length; i++) {
	Graphics2D g2d = (Graphics2D)g;
	g2d.setStroke(new BasicStroke(m_StrokeThickness));
	//if filling points
	if(m_Fill) {
	  g2d.setColor(m_FillColor);
	  g2d.setStroke(new BasicStroke(0));
	  g2d.fillOval(axisBottom.valueToPos(i)-m_Size/2, axisLeft.valueToPos(residuals[i])-m_Size/2, m_Size, m_Size);
	}
	//outline of points
	g2d.setStroke(new BasicStroke(m_StrokeThickness));
	g2d.setColor(m_Color);
	g2d.drawOval(axisBottom.valueToPos(i)-m_Size/2, axisLeft.valueToPos(residuals[i])-m_Size/2, m_Size, m_Size);
	//connecting line between points
	if(i< residuals.length-1) {
	  g.setColor(Color.BLUE);
	  g.drawLine(axisBottom.valueToPos(i), axisLeft.valueToPos(residuals[i]),axisBottom.valueToPos(i+1), axisLeft.valueToPos(residuals[i+1]));
	}
      }
    }
  }

  /**
   * Set the index of the residuals attribute in the instances
   * @param val			Index of residuals
   */
  public void setIndex(int val) {
    m_Index = val;
  }
}
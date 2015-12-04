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
 * LinearRegressionOverlayPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.sequence;

import adams.core.Utils;
import adams.data.sequence.XYSequencePoint;
import adams.data.statistics.StatUtils;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractStrokePaintlet;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import gnu.trove.list.array.TDoubleArrayList;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

/**
 <!-- globalinfo-start -->
 * Draws a straight line, using slope and intercept determine by linear regression using all the data points in the plot(s).
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
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color for the line.
 * &nbsp;&nbsp;&nbsp;default: #000000
 * </pre>
 * 
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing lines.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 * <pre>-output-slope-intercept &lt;boolean&gt; (property: outputSlopeIntercept)
 * &nbsp;&nbsp;&nbsp;If enabled, slope and intercept are output on the plot as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: X)
 * &nbsp;&nbsp;&nbsp;The X position of the top-left corner of the text (1-based).
 * &nbsp;&nbsp;&nbsp;default: 5
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: Y)
 * &nbsp;&nbsp;&nbsp;The Y position of the top-left corner of the text (1-based).
 * &nbsp;&nbsp;&nbsp;default: 16
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font to use for the text.
 * &nbsp;&nbsp;&nbsp;default: monospaced-PLAIN-12
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LinearRegressionOverlayPaintlet
  extends AbstractStrokePaintlet
  implements XYSequencePaintlet, AntiAliasingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 6292059403058224856L;

  /** the color for the overlay. */
  protected Color m_Color;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /** whether to output slope/intercept. */
  protected boolean m_OutputSlopeIntercept;

  /** the X position of the text (1-based). */
  protected int m_X;

  /** the Y position of the text (1-based). */
  protected int m_Y;

  /** the font to use. */
  protected Font m_Font;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Draws a straight line, using slope and intercept determine by linear "
        + "regression using all the data points in the plot(s).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "color", "color",
      Color.BLACK);

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));

    m_OptionManager.add(
      "output-slope-intercept", "outputSlopeIntercept",
      false);

    m_OptionManager.add(
      "x", "X",
      5, 1, null);

    m_OptionManager.add(
      "y", "Y",
      16, 1, null);

    m_OptionManager.add(
      "font", "font",
      Fonts.getMonospacedFont());
  }

  /**
   * Set the stroke color for the paintlet.
   * 
   * @param value	color of the stroke
   */
  public void setColor(Color value) {
    m_Color = value;
    memberChanged();
  }

  /**
   * Get the stroke color for the paintlet.
   * 
   * @return		color of the stroke
   */
  public Color getColor() {
    return m_Color;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorTipText() {
    return "The color for the line.";
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
   * Sets whether to output slope/intercept.
   *
   * @param value	if true then slope/intercept are output
   */
  public void setOutputSlopeIntercept(boolean value) {
    m_OutputSlopeIntercept = value;
    memberChanged();
  }

  /**
   * Returns whether slope/intercept are output.
   *
   * @return		true if slope/intercept are output
   */
  public boolean getOutputSlopeIntercept() {
    return m_OutputSlopeIntercept;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String outputSlopeInterceptTipText() {
    return "If enabled, slope and intercept are output on the plot as well.";
  }

  /**
   * Sets the X position of the text (top-left corner).
   *
   * @param value	the position, 1-based
   */
  public void setX(int value) {
    if (value > 0) {
      m_X = value;
      reset();
    }
    else {
      getLogger().severe("X must be >0, provided: " + value);
    }
  }

  /**
   * Returns the X position of the text (top-left corner).
   *
   * @return		the position, 1-based
   */
  public int getX() {
    return m_X;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String XTipText() {
    return "The X position of the top-left corner of the text (1-based).";
  }

  /**
   * Sets the Y position of the text (top-left corner).
   *
   * @param value	the position, 1-based
   */
  public void setY(int value) {
    if (value > 0) {
      m_Y = value;
      reset();
    }
    else {
      getLogger().severe("Y must be >0, provided: " + value);
    }
  }

  /**
   * Returns the Y position of the text (top-left corner).
   *
   * @return		the position, 1-based
   */
  public int getY() {
    return m_Y;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String YTipText() {
    return "The Y position of the top-left corner of the text (1-based).";
  }

  /**
   * Sets the font to use.
   *
   * @param value	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the font in use.
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
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    return PaintMoment.GRID;
  }

  /**
   * Returns the XY sequence panel currently in use.
   *
   * @return		the panel in use
   */
  @Override
  public XYSequencePanel getSequencePanel() {
    return (XYSequencePanel) getPanel();
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		always null
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return null;
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    AxisPanel		xAxis;
    AxisPanel		yAxis;
    double		xMin;
    double		xMax;
    double 		yMin;
    double		yMax;
    TDoubleArrayList	x;
    TDoubleArrayList	y;
    int			i;
    XYSequenceContainer	cont;
    double[]		lr;

    x = new TDoubleArrayList();
    y = new TDoubleArrayList();
    for (i = 0; i < getSequencePanel().getContainerManager().countVisible(); i++) {
      cont = getSequencePanel().getContainerManager().getVisible(i);
      for (XYSequencePoint p: cont.getData()) {
	x.add(p.getX());
	y.add(p.getY());
      }
    }
    lr = StatUtils.linearRegression(x.toArray(), y.toArray());

    xAxis = getPlot().getAxis(Axis.BOTTOM);
    yAxis = getPlot().getAxis(Axis.LEFT);
    xMin  = xAxis.getActualMinimum();
    yMin  = xMin * lr[1] + lr[0];
    xMax  = xAxis.getActualMaximum();
    yMax  = xMax * lr[1] + lr[0];

    g.setColor(m_Color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);
    if (m_OutputSlopeIntercept) {
      g.setFont(getFont());
      g.drawString(
	"I: " + Utils.doubleToString(lr[0], 3)
	  + ", S: " + Utils.doubleToString(lr[1], 3),
	m_X - 1,
	m_Y - 1);
    }
    g.drawLine(xAxis.valueToPos(xMin), yAxis.valueToPos(yMin), xAxis.valueToPos(xMax), yAxis.valueToPos(yMax));
  }
}

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

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.sequence.XYSequencePoint;
import adams.data.utils.LOWESS;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AbstractStrokePaintlet;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Overlay that applies LOWESS smoothing over all the data points in the plot(s).<br>
 * <br>
 * For more information see:<br>
 * WikiPedia. Local Regression. URL http:&#47;&#47;en.wikipedia.org&#47;wiki&#47;Lowess.
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
 * <pre>-window &lt;int&gt; (property: window)
 * &nbsp;&nbsp;&nbsp;The window size to use.
 * &nbsp;&nbsp;&nbsp;default: 20
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing lines.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LOWESSOverlayPaintlet
  extends AbstractStrokePaintlet
  implements XYSequencePaintlet, AntiAliasingSupporter, TechnicalInformationHandler {

  /** for serialization. */
  private static final long serialVersionUID = 6292059403058224856L;

  /** the color for the overlay. */
  protected Color m_Color;

  /** the window size. */
  protected int m_Window;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Overlay that applies LOWESS smoothing over all the data points in the plot(s).\n\n"
      + "For more information see:\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    return LOWESS.getTechnicalInformation();
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
	"window", "window",
	20, 1, null);

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
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
   * Set the size of the window.
   *
   * @param value	the size of the window
   */
  public void setWindow(int value) {
    if (value >= 1) {
      m_Window = value;
      memberChanged();
    }
    else {
      getLogger().warning("Window size must >= 1, provided: " + value);
    }
  }

  /**
   * Get the size of the window.
   *
   * @return		the size of the window
   */
  public int getWindow() {
    return m_Window;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowTipText() {
    return "The window size to use.";
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
    Point2D		prior;
    Point2D		current;
    List<Point2D>	points;
    List<Point2D>	smoothed;
    int			i;
    XYSequenceContainer	cont;

    points = new ArrayList<>();
    for (i = 0; i < getSequencePanel().getContainerManager().countVisible(); i++) {
      cont = getSequencePanel().getContainerManager().getVisible(i);
      for (XYSequencePoint p: cont.getData())
      points.add(new Point2D.Double(p.getX(), p.getY()));
    }

    smoothed = LOWESS.calculate(points, m_Window);

    g.setColor(m_Color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    xAxis = getPlot().getAxis(Axis.BOTTOM);
    yAxis = getPlot().getAxis(Axis.LEFT);
    for (i = 1; i < smoothed.size(); i++) {
      prior   = smoothed.get(i - 1);
      current = smoothed.get(i);
      g.drawLine(
	xAxis.valueToPos(prior.getX()),
	yAxis.valueToPos(prior.getY()),
	xAxis.valueToPos(current.getX()),
	yAxis.valueToPos(current.getY()));
    }
  }
}

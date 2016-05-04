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
 * LowessPaintlet.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.paintlet;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;
import adams.data.spreadsheet.SpreadSheetUtils;
import adams.data.utils.LOWESS;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Paintlet for drawing the lowess overlay.
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
 * <pre>-window-size &lt;int&gt; (property: windowSize)
 * &nbsp;&nbsp;&nbsp;The window size for smoothing.
 * &nbsp;&nbsp;&nbsp;default: 100
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author msf8
 * @version $Revision$
 */
public class LowessPaintlet
  extends AbstractOverlayPaintlet
  implements AntiAliasingSupporter, TechnicalInformationHandler {

  /** for serializing */
  private static final long serialVersionUID = 1643339689654875242L;

  /**Size of window size for calculating lowess */
  protected int m_WindowSize;

  /**Points to plot for the lowess curve */
  protected List<Point2D> m_ToPlot;

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
      "Paintlet for drawing the lowess overlay.\n\n"
      + "For more information see:\n"
      + getTechnicalInformation().toString();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"window-size", "windowSize",
	100, LOWESS.MIN_WINDOW_SIZE, null);

    m_OptionManager.add(
	    "anti-aliasing-enabled", "antiAliasingEnabled",
	    GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));
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

  @Override
  public void setWindowSize(int val) {
    m_WindowSize = val;
    memberChanged();
  }

  /**
   * Get the Window size for calculating the lowess loverlay
   * @return		Number of data points in window
   */
  public int getWindowSize() {
    return m_WindowSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String windowSizeTipText() {
    return "The window size for smoothing.";
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
  public void calculate() {
    super.calculate();
    double[] x_data = SpreadSheetUtils.getNumericColumn(m_Data, m_XInd);
    double[] y_data = SpreadSheetUtils.getNumericColumn(m_Data, m_YInd);
    //create an arraylist of points from the instance data
    ArrayList<Point2D> points = new ArrayList<Point2D>();
    for(int i = 0; i< x_data.length; i++) {
      Point2D p = new Point2D.Double(x_data[i], y_data[i]);
      points.add(p);
    }
    m_ToPlot = LOWESS.calculate(points, m_WindowSize);
    m_Calculated = true;
  }

  @Override
  protected void drawData(Graphics g) {
    if(m_Calculated) {
      GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);
      g.setColor(m_Color);
      Graphics2D g2d = (Graphics2D)g;
      g2d.setStroke(new BasicStroke(m_StrokeThickness));
      //plot all the points
      for(int i = 0; i< m_ToPlot.size() -1; i++) {
	g2d.drawLine(m_AxisBottom.valueToPos(m_ToPlot.get(i).getX()), m_AxisLeft.valueToPos(m_ToPlot.get(i).getY()), m_AxisBottom.valueToPos(m_ToPlot.get(i+1).getX()), m_AxisLeft.valueToPos(m_ToPlot.get(i+1).getY()));
      }
    }
  }
}
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
 * OutlierPaintlet.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.flow.sink.sequenceplotter;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.flow.control.RemoveOutliers;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.AbstractXYSequencePaintlet;
import adams.gui.visualization.sequence.AbstractXYSequencePointHitDetector;
import adams.gui.visualization.sequence.CrossHitDetector;
import adams.gui.visualization.sequence.DiameterBasedPaintlet;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

/**
 * Paintlet for drawing crosses at the X-Y positions of the data points.
 * Uses a different color if the data point has the boolean {@link RemoveOutliers#KEY_OUTLIER}
 * meta-data value set.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class OutlierPaintlet
  extends AbstractXYSequencePaintlet
  implements AntiAliasingSupporter, DiameterBasedPaintlet {

  private static final long serialVersionUID = 6585282004697591762L;

  /** the diameter of the circle. */
  protected int m_Diameter;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /** the color for the regular data points. */
  protected Color m_ColorNormal;

  /** the color for the outliers. */
  protected Color m_ColorOutlier;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Paintlet for painting crosses at the specified X-Y position, uses "
	+ "separate color for outliers if the meta-data key "
	+ RemoveOutliers.KEY_OUTLIER + " is set.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "diameter", "diameter",
      7, 1, null);

    m_OptionManager.add(
      "anti-aliasing-enabled", "antiAliasingEnabled",
      GUIHelper.getBoolean(getClass(), "antiAliasingEnabled", true));

    m_OptionManager.add(
      "color-normal", "colorNormal",
      Color.BLUE);

    m_OptionManager.add(
      "color-outlier", "colorOutlier",
      Color.RED);
  }

  /**
   * Sets the cross diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    m_Diameter = value;
    memberChanged();
  }

  /**
   * Returns the diameter of the cross.
   *
   * @return		the diameter
   */
  public int getDiameter() {
    return m_Diameter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String diameterTipText() {
    return "The diameter of the cross in pixels.";
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
    return "If enabled, uses anti-aliasing for drawing the crosses.";
  }

  /**
   * Sets the color for normal data points.
   *
   * @param value	the color
   */
  public void setColorNormal(Color value) {
    m_ColorNormal = value;
    memberChanged();
  }

  /**
   * Returns the color for normal data points.
   *
   * @return		the color
   */
  public Color getColorNormal() {
    return m_ColorNormal;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorNormalTipText() {
    return "The color for normal data points.";
  }

  /**
   * Sets the color for outliers.
   *
   * @param value	the color
   */
  public void setColorOutlier(Color value) {
    m_ColorOutlier = value;
    memberChanged();
  }

  /**
   * Returns the color for outliers.
   *
   * @return		the color
   */
  public Color getColorOutlier() {
    return m_ColorOutlier;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String colorOutlierTipText() {
    return "The color for outliers.";
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return new CrossHitDetector(this);
  }

  /**
   * Draws the custom data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   */
  public void drawData(Graphics g, PaintMoment moment, XYSequence data) {
    List<XYSequencePoint> 	points;
    SequencePlotPoint		curr;
    int				currX;
    int				currY;
    AxisPanel axisX;
    AxisPanel			axisY;
    int				i;
    int 			radius;
    Color			lastColor;
    Color			currColor;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);

    // paint all points
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    radius    = m_Diameter / 2;
    lastColor = m_ColorNormal;
    g.setColor(lastColor);

    for (i = 0; i < data.size(); i++) {
      curr = (SequencePlotPoint) points.get(i);

      // determine coordinates
      currX = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));
      currY = axisY.valueToPos(XYSequencePoint.toDouble(curr.getY()));

      currColor = m_ColorNormal;
      if (curr.hasMetaData()) {
	if (curr.getMetaData().containsKey(RemoveOutliers.KEY_OUTLIER) && ((Boolean) curr.getMetaData().get(RemoveOutliers.KEY_OUTLIER)))
	  currColor = m_ColorOutlier;
      }
      if (currColor != lastColor) {
	g.setColor(currColor);
	lastColor = currColor;
      }

      // draw cross
      g.drawLine(currX - radius, currY - radius, currX + radius, currY + radius);
      g.drawLine(currX + radius, currY - radius, currX - radius, currY + radius);
    }
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    int			i;
    XYSequence		data;

    // paint all points
    synchronized(getActualContainerManager()) {
      for (i = 0; i < getActualContainerManager().count(); i++) {
	if (!getActualContainerManager().isVisible(i))
	  continue;
	data = getActualContainerManager().get(i).getData();
	if (data.size() == 0)
	  continue;
	synchronized(data) {
	  drawData(g, moment, data);
	}
      }
    }
  }
}

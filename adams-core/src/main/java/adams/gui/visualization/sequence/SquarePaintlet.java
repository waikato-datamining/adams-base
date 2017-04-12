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
 * SquarePaintlet.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.sequence;

import adams.data.sequence.XYSequence;
import adams.data.sequence.XYSequencePoint;
import adams.gui.core.AntiAliasingSupporter;
import adams.gui.core.GUIHelper;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.AxisPanel;
import adams.gui.visualization.core.plot.Axis;
import adams.gui.visualization.sequence.metadatacolor.AbstractMetaDataColor;
import adams.gui.visualization.sequence.metadatacolor.Dummy;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SquarePaintlet
  extends AbstractXYSequenceMetaDataColorPaintlet
  implements AntiAliasingSupporter, PaintletWithCustomDataSupport, DiameterBasedPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -8772546156227148237L;

  /** the diameter of the circle. */
  protected int m_Diameter;

  /** whether anti-aliasing is enabled. */
  protected boolean m_AntiAliasingEnabled;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Paintlet for simply painting squares at the specified X-Y position.";
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
  }

  /**
   * Sets the square diameter.
   *
   * @param value	the diameter
   */
  public void setDiameter(int value) {
    m_Diameter = value;
    memberChanged();
  }

  /**
   * Returns the diameter of the square.
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
    return "The diameter of the square in pixels.";
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
    return "If enabled, uses anti-aliasing for drawing squares.";
  }

  /**
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return new CircleHitDetector(this);
  }

  /**
   * Draws the custom data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  public void drawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    int				currX;
    int				currY;
    AxisPanel			axisX;
    AxisPanel			axisY;
    int				i;
    AbstractMetaDataColor 	metaColor;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);
    if (m_MetaDataColor instanceof Dummy)
      metaColor = null;
    else
      metaColor = m_MetaDataColor;

    // paint all points
    g.setColor(color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    for (i = 0; i < data.size(); i++) {
      curr = points.get(i);

      if (metaColor != null)
	g.setColor(metaColor.getColor(curr, color));

      // determine coordinates
      currX = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));
      currY = axisY.valueToPos(XYSequencePoint.toDouble(curr.getY()));

      currX -= ((m_Diameter - 1) / 2);
      currY -= ((m_Diameter - 1) / 2);

      // draw square
      g.drawRect(currX, currY, m_Diameter, m_Diameter);
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
	if (getActualContainerManager().isFiltered() && !getActualContainerManager().isFiltered(i))
	  continue;
	data = getActualContainerManager().get(i).getData();
	if (data.size() == 0)
	  continue;
	synchronized(data) {
	  drawCustomData(g, moment, data, getColor(i));
	}
      }
    }
  }
}

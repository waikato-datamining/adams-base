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
 * CrossPaintlet.java
 * Copyright (C) 2014-2024 University of Waikato, Hamilton, New Zealand
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
 * Paintlet for simply painting crosses at the specified X-Y position.
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
 * <pre>-meta-data-color &lt;adams.gui.visualization.sequence.metadatacolor.AbstractMetaDataColor&gt; (property: metaDataColor)
 * &nbsp;&nbsp;&nbsp;The scheme to use for extracting the color from the meta-data; ignored if 
 * &nbsp;&nbsp;&nbsp;adams.gui.visualization.sequence.metadatacolor.Dummy.
 * &nbsp;&nbsp;&nbsp;default: adams.gui.visualization.sequence.metadatacolor.Dummy
 * </pre>
 * 
 * <pre>-diameter &lt;int&gt; (property: diameter)
 * &nbsp;&nbsp;&nbsp;The diameter of the cross in pixels.
 * &nbsp;&nbsp;&nbsp;default: 7
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-anti-aliasing-enabled &lt;boolean&gt; (property: antiAliasingEnabled)
 * &nbsp;&nbsp;&nbsp;If enabled, uses anti-aliasing for drawing the crosses.
 * &nbsp;&nbsp;&nbsp;default: true
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 8902 $
 */
public class CrossPaintlet
  extends AbstractXYSequenceMetaDataColorPaintlet
  implements AntiAliasingSupporter, PaintletWithCustomDataSupport, DiameterBasedPaintlet {

  /** for serialization. */
  private static final long serialVersionUID = -8772546156227148237L;

  /** the diameter of the cross. */
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
    return "Paintlet for simply painting crosses at the specified X-Y position.";
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
   * Returns a new instance of the hit detector to use.
   *
   * @return		the hit detector
   */
  @Override
  public AbstractXYSequencePointHitDetector newHitDetector() {
    return new CrossHitDetector(this);
  }

  /**
   * Returns whether point preprocessing is actually supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsPointPreprocessor() {
    return true;
  }

  /**
   * Draws the custom data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  protected void doDrawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    List<XYSequencePoint>	points;
    XYSequencePoint		curr;
    int				currX;
    int				currY;
    AxisPanel			axisX;
    AxisPanel			axisY;
    int				i;
    int 			radius;
    AbstractMetaDataColor	metaColor;

    points = data.toList();
    axisX  = getPanel().getPlot().getAxis(Axis.BOTTOM);
    axisY  = getPanel().getPlot().getAxis(Axis.LEFT);
    if (m_MetaDataColor instanceof Dummy)
      metaColor = null;
    else
      metaColor = m_MetaDataColor;

    if (metaColor != null)
      metaColor.initialize(points);

    // paint all points
    g.setColor(color);
    GUIHelper.configureAntiAliasing(g, m_AntiAliasingEnabled);

    radius = m_Diameter / 2;

    m_PointPreprocessor.resetPreprocessor();

    for (i = 0; i < data.size(); i++) {
      curr = m_PointPreprocessor.preprocess(points.get(i), axisX, axisY);

      if (metaColor != null)
	g.setColor(metaColor.getColor(curr, color));

      // determine coordinates
      currX = axisX.valueToPos(XYSequencePoint.toDouble(curr.getX()));
      currY = axisY.valueToPos(XYSequencePoint.toDouble(curr.getY()));

      // draw cross
      g.drawLine(currX - radius, currY - radius, currX + radius, currY + radius);
      g.drawLine(currX + radius, currY - radius, currX - radius, currY + radius);
    }
  }

  /**
   * Draws the data with the given color.
   *
   * @param g		the graphics context
   * @param moment	the paint moment
   * @param data	the data to draw
   * @param color	the color to draw in
   */
  public void drawCustomData(Graphics g, PaintMoment moment, XYSequence data, Color color) {
    float	width;

    width = getStrokeWidth(g, 1.0f);
    applyStroke(g, m_StrokeThickness);
    doDrawCustomData(g, moment, data, color);
    applyStroke(g, width);
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  protected void doPerformPaint(Graphics g, PaintMoment moment) {
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
	if (data.isEmpty())
	  continue;
	synchronized(data) {
	  drawCustomData(g, moment, data, getColor(i));
	}
      }
    }
  }
}

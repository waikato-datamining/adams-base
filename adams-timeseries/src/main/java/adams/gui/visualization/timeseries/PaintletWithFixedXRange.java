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
 * PaintletWithFixedXRange.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.core.base.BaseDateTime;
import adams.gui.event.PaintEvent.PaintMoment;
import adams.gui.visualization.core.PaintablePanel;

import java.awt.Graphics;

/**
 * A wrapper for XY-sequence paintlets, in order to use fixed a X range.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PaintletWithFixedXRange
  extends AbstractTimeseriesPaintlet
  implements adams.gui.visualization.core.PaintletWithFixedXRange {

  /** for serialization. */
  private static final long serialVersionUID = 3270329510617886683L;

  /** the minimum of X. */
  protected BaseDateTime m_MinX;

  /** the maximum of X. */
  protected BaseDateTime m_MaxX;

  /** the actual paintlet to use. */
  protected AbstractTimeseriesPaintlet m_Paintlet;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Meta-paintlet that uses a fixed X range (for faster drawing) and a base-paintlet to draw the actual data.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min-x", "minX",
	    new BaseDateTime(BaseDateTime.INF_PAST));

    m_OptionManager.add(
	    "max-x", "maxX",
	    new BaseDateTime(BaseDateTime.INF_FUTURE));

    m_OptionManager.add(
	    "paintlet", "paintlet",
	    getDefaultPaintlet());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    setPaintlet(getDefaultPaintlet());

    super.initialize();
  }

  /**
   * Returns the default paintlet to use.
   *
   * @return		the default paintlet
   */
  protected AbstractTimeseriesPaintlet getDefaultPaintlet() {
    return new TimeseriesPaintlet();
  }

  /**
   * Sets the spectrum panel to use, null to disable painting.
   *
   * @param value	the panel to paint on
   */
  @Override
  public void setPanel(PaintablePanel value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(value);

    super.setPanel(value);
  }

  /**
   * Returns when this paintlet is to be executed.
   *
   * @return		when this paintlet is to be executed
   */
  @Override
  public PaintMoment getPaintMoment() {
    if (m_Paintlet != null)
      return m_Paintlet.getPaintMoment();
    else
      return PaintMoment.PAINT;
  }

  /**
   * Sets the minimum of the X range.
   *
   * @param value	the minimum
   */
  public void setMinX(BaseDateTime value) {
    m_MinX = value;
    memberChanged(true);
  }

  /**
   * Returns the minimum of the X range.
   *
   * @return		the minimum
   */
  public BaseDateTime getMinX() {
    return m_MinX;
  }

  /**
   * Returns the minimum of the X range.
   *
   * @return		the minimum
   */
  public double getMinimumX() {
    return m_MinX.dateValue().getTime();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minXTipText() {
    return "The minimum value for the X range.";
  }

  /**
   * Sets the maximum of the X range.
   *
   * @param value	the maximum
   */
  public void setMaxX(BaseDateTime value) {
    m_MaxX = value;
    memberChanged(true);
  }

  /**
   * Returns the maximum of the X range.
   *
   * @return		the maximum
   */
  public BaseDateTime getMaxX() {
    return m_MaxX;
  }

  /**
   * Returns the maximum of the X range.
   *
   * @return		the maximum
   */
  public double getMaximumX() {
    return m_MaxX.dateValue().getTime();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxXTipText() {
    return "The maximum value for the X range.";
  }

  /**
   * Sets the actual paintlet to use.
   *
   * @param value	the paintlet
   */
  public void setPaintlet(AbstractTimeseriesPaintlet value) {
    if (m_Paintlet != null)
      m_Paintlet.setPanel(null);

    m_Paintlet = value;
    m_Paintlet.setPanel(getPanel());

    memberChanged();
  }

  /**
   * Returns the painlet in use.
   *
   * @return		the paintlet
   */
  public AbstractTimeseriesPaintlet getPaintlet() {
    return m_Paintlet;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String paintletTipText() {
    return "The actual paintlet to use for drawing the data.";
  }

  /**
   * The paint routine of the paintlet.
   *
   * @param g		the graphics context to use for painting
   * @param moment	what {@link PaintMoment} is currently being painted
   */
  @Override
  public void performPaint(Graphics g, PaintMoment moment) {
    m_Paintlet.performPaint(g, moment);
  }
}

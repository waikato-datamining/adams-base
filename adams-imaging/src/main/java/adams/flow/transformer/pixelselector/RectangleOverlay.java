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
 * RectangleOverlay.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import java.awt.Graphics;
import java.awt.Point;

import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

/**
 <!-- globalinfo-start -->
 * Simply highlights the selected pixel<br>
 * <br>
 * Some actions that generate data for this overlay:<br>
 * adams.flow.transformer.pixelselector.TopLeftCorner<br>
 * adams.flow.transformer.pixelselector.BottomRightCorner
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
 * <pre>-color &lt;java.awt.Color&gt; (property: color)
 * &nbsp;&nbsp;&nbsp;The color to use for the overlay.
 * &nbsp;&nbsp;&nbsp;default: #ff0000
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RectangleOverlay
  extends AbstractSingleColorPixelSelectorOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -5646722242616870109L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  protected String getGlobalInfo() {
    return "Simply highlights the selected pixel";
  }

  /**
   * Returns some actions that generate data for this overlay.
   * 
   * @return		the actions
   */
  @Override
  public Class[] getSuggestedActions() {
    return new Class[]{TopLeftCorner.class, BottomRightCorner.class};
  }

  /**
   * Returns the top-left corner to paint.
   * 
   * @return		the location, null if none found
   */
  protected Point getTopLeft() {
    Point	result;
    Report	report;
    
    result = null;
    
    if ((m_Image != null) && (m_Image.hasReport())) {
      report = m_Image.getReport();
      if (report.hasValue(TopLeftCorner.PIXEL_TOP) && report.hasValue(TopLeftCorner.PIXEL_LEFT)) {
	result = new Point(
	    report.getDoubleValue(TopLeftCorner.PIXEL_LEFT).intValue(),
	    report.getDoubleValue(TopLeftCorner.PIXEL_TOP).intValue());
      }
    }
    
    return result;
  }

  /**
   * Returns the bottom-right corner to paint.
   * 
   * @return		the location, null if none found
   */
  protected Point getBottomRight() {
    Point	result;
    Report	report;
    
    result = null;
    
    if ((m_Image != null) && (m_Image.hasReport())) {
      report = m_Image.getReport();
      if (report.hasValue(BottomRightCorner.PIXEL_BOTTOM) && report.hasValue(BottomRightCorner.PIXEL_RIGHT)) {
	result = new Point(
	    report.getDoubleValue(BottomRightCorner.PIXEL_RIGHT).intValue(),
	    report.getDoubleValue(BottomRightCorner.PIXEL_BOTTOM).intValue());
      }
    }
    
    return result;
  }

  /**
   * Notifies the overlay that the image has changed.
   *
   * @param panel	the panel this overlay belongs to
   */
  public void imageChanged(PaintPanel panel) {
  }

  /**
   * Paints the actual overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  @Override
  protected void doPaintOverlay(PaintPanel panel, Graphics g) {
    Point	topLeft;
    Point	bottomRight;
    
    topLeft     = getTopLeft();
    bottomRight = getBottomRight();
    if ((topLeft == null) || (bottomRight == null))
      return;
    
    g.setColor(m_Color);
    g.drawRect(
	(int) topLeft.getX() - 1, 
	(int) topLeft.getY() - 1, 
	(int) (bottomRight.getX() - topLeft.getX() + 2), 
	(int) (bottomRight.getY() - topLeft.getY() + 2));
  }
}

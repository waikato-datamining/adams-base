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
 * SimpleOverlay.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.pixelselector;

import java.awt.Graphics;
import java.awt.Point;

import adams.data.report.Report;
import adams.gui.visualization.image.ImagePanel.PaintPanel;

/**
 <!-- globalinfo-start -->
 * Simply highlights the selected pixel<br/>
 * <br/>
 * Some actions that generate data for this overlay:<br/>
 * adams.flow.transformer.pixelselector.SimpleSelect
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
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
public class SimpleOverlay
  extends AbstractSingleColorPixelSelectorOverlay {

  /** for serialization. */
  private static final long serialVersionUID = -3424961839663573502L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  protected String getGlobalInfo() {
    return "Simply highlights the selected pixel";
  }

  /**
   * Returns some actions that generate data for this overlay.
   * 
   * @return		the actions
   */
  public Class[] getSuggestedActions() {
    return new Class[]{SimpleSelect.class};
  }

  /**
   * Returns the pixel location to paint.
   * 
   * @return		the location, null if none found
   */
  protected Point getPixelLocation() {
    Point	result;
    Report	report;
    
    result = null;
    
    if ((m_Image != null) && (m_Image.hasReport())) {
      report = m_Image.getReport();
      if (report.hasValue(SimpleSelect.PIXEL_X) && report.hasValue(SimpleSelect.PIXEL_Y)) {
	result = new Point(
	    report.getDoubleValue(SimpleSelect.PIXEL_X).intValue(),
	    report.getDoubleValue(SimpleSelect.PIXEL_Y).intValue());
      }
    }
    
    return result;
  }
  
  /**
   * Paints the actual overlay over the image.
   *
   * @param panel	the panel this overlay is for
   * @param g		the graphics context
   */
  protected void doPaintOverlay(PaintPanel panel, Graphics g) {
    Point	loc;
    
    loc = getPixelLocation();
    if (loc == null)
      return;
    
    g.setColor(m_Color);
    g.drawRect((int) loc.getX() - 1, (int) loc.getY() - 1, 3, 3);
  }
}

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
 * TrackMousePositionInConsole.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.event.MouseEvent;

/**
 <!-- globalinfo-start -->
 * Simply outputs the mouse position in the console.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TrackMousePositionInConsole
  extends AbstractMouseMovementTracker {

  /** for serialization. */
  private static final long serialVersionUID = -2604668648887704474L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply outputs the mouse position in the console.";
  }

  /**
   * Gets triggered when the mouse moved.
   * 
   * @param e		the mouse event that triggered the event
   */
  @Override
  public void mouseMovementTracked(MouseEvent e) {
    String	msg;
    
    msg = "X: " + e.getPoint().x + ", Y: " + e.getPoint().y;
    System.out.println(msg);
    getLogger().info(msg);
  }
}

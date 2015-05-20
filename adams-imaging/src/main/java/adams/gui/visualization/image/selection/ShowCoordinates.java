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
 * ShowCoordinates.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import java.awt.Point;

import adams.gui.core.GUIHelper;
import adams.gui.visualization.image.ImagePanel;

/**
 <!-- globalinfo-start -->
 * Shows the coordination of the selection.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ShowCoordinates
  extends AbstractSelectionProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -657789971297807743L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Shows the coordination of the selection.";
  }

  /**
   * Process the selection that occurred in the image panel.
   * 
   * @param panel	the origin
   * @param topLeft	the top-left position of the selection
   * @param bottomRight	the bottom-right position of the selection
   * @param modifiersEx	the associated modifiers
   */
  @Override
  protected void doProcessSelection(ImagePanel panel, Point topLeft, Point bottomRight, int modifiersEx) {
    GUIHelper.showInformationMessage(
	panel, 
	  "Top: " + panel.mouseToPixelLocation(topLeft).y + "\n"
	+ "Left: " + panel.mouseToPixelLocation(topLeft).x + "\n"
	+ "Bottom: " + panel.mouseToPixelLocation(bottomRight).y + "\n"
	+ "Right: " + panel.mouseToPixelLocation(bottomRight).x);
  }
}

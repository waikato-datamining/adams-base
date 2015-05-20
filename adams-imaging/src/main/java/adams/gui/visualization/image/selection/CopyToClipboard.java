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
 * CopyToClipboard.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.image.selection;

import java.awt.Point;
import java.awt.image.BufferedImage;

import adams.gui.core.GUIHelper;
import adams.gui.visualization.image.ImagePanel;

/**
 <!-- globalinfo-start -->
 * Copies the selection to the system's clipboard.
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
 * <pre>-silent (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, user is not prompted with dialog that the selection has been 
 * &nbsp;&nbsp;&nbsp;copied.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CopyToClipboard
  extends AbstractSelectionProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -657789971297807743L;

  /** whether to suppress the "copied to clipboard" dialog. */
  protected boolean m_Silent;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Copies the selection to the system's clipboard.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "silent", "silent",
	    false);
  }

  /**
   * Sets whether to suppress the "copied" dialog.
   *
   * @param value	true if to suppress dialog
   */
  public void setSilent(boolean value) {
    m_Silent = value;
    reset();
  }

  /**
   * Returns whether to suppress the "copied" dialog.
   *
   * @return		true if to suppress dialog
   */
  public boolean getSilent() {
    return m_Silent;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String silentTipText() {
    return "If enabled, user is not prompted with dialog that the selection has been copied.";
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
    BufferedImage	selection;
    
    selection = panel.getCurrentImage().getSubimage(
	panel.mouseToPixelLocation(topLeft).x,
	panel.mouseToPixelLocation(topLeft).y, 
	panel.mouseToPixelLocation(bottomRight).x - panel.mouseToPixelLocation(topLeft).x + 1,
	panel.mouseToPixelLocation(bottomRight).y - panel.mouseToPixelLocation(topLeft).y + 1);
    
    GUIHelper.copyToClipboard(selection);
    
    GUIHelper.showInformationMessage(panel, "Selection copied to clipboard!");
  }
}

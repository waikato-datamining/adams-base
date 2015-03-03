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
 * ShowPositionMapClickListener.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.sink.openstreetmapviewer;

import java.awt.Dialog;
import java.awt.Dialog.ModalityType;
import java.awt.event.MouseEvent;

import org.openstreetmap.gui.jmapviewer.JMapViewer;

import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextDialog;

/**
 <!-- globalinfo-start -->
 * Simply shows the position on the map that the user clicked on.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-title &lt;java.lang.String&gt; (property: title)
 * &nbsp;&nbsp;&nbsp;The title of the dialog.
 * &nbsp;&nbsp;&nbsp;default: Hits
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -2
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ShowPositionMapClickListener
  extends AbstractMapClickListenerWithDialog {

  /** for serialization. */
  private static final long serialVersionUID = 4354941485212807035L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply shows the position on the map that the user clicked on.";
  }

  /**
   * Returns whether a database connection is required.
   * 
   * @return		true if connection required
   */
  @Override
  public boolean requiresDatabaseConnection() {
    return false;
  }

  /**
   * Performs the actual processing of the click, returns the generated dialog.
   * 
   * @param viewer	the associated viewer
   * @param e		the associated event
   * @return		the generated dialog, null if none created
   */
  @Override
  protected Dialog doProcessClick(JMapViewer viewer, MouseEvent e) {
    TextDialog	result;
    
    if (GUIHelper.getParentDialog(viewer) != null)
      result = new TextDialog(GUIHelper.getParentDialog(viewer), ModalityType.MODELESS);
    else
      result = new TextDialog(GUIHelper.getParentFrame(viewer), false);
    result.setDialogTitle(m_Title);
    result.setEditable(false);
    result.setContent(viewer.getPosition(e.getPoint()).toString());

    return result;
  }
}

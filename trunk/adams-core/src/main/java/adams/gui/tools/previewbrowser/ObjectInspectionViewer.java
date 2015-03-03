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
 * ObjectInspectionViewer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.awt.BorderLayout;

import adams.gui.visualization.debug.InspectionPanel;

/**
 * Allows to inspect objects like in the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ObjectInspectionViewer
  extends AbstractSerializedObjectViewer {

  /** for serialization. */
  private static final long serialVersionUID = -3721237481983140889L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows to inspect an object.";
  }

  /**
   * Returns whether viewer handles this object.
   * 
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  public boolean handles(Object obj) {
    return true;
  }

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the preview for
   * @return		the preview, null if failed to generate preview
   */
  @Override
  protected PreviewPanel createPreview(Object obj) {
    PreviewPanel	result;
    InspectionPanel	panel;
    
    panel = new InspectionPanel();
    panel.setCurrent(obj);
    result = new PreviewPanel(panel);
    result.add(panel, BorderLayout.CENTER);

    return result;
  }
}

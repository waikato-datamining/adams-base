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
 * GraphSource.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import adams.gui.core.TextEditorPanel;
import weka.core.Drawable;

import java.util.logging.Level;

/**
 * Displays the source of a weka.core.Drawable graph.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class GraphSource
  extends AbstractSerializedObjectViewer {

  /** for serialization. */
  private static final long serialVersionUID = -262735238228366027L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the graph source from " + Drawable.class.getName() + " objects.";
  }

  /**
   * Returns whether viewer handles this object.
   * 
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof Drawable);
  }

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the preview for
   * @return		the preview, null if failed to generate preview
   */
  @Override
  protected PreviewPanel createPreview(Object obj) {
    TextEditorPanel	panel;
    String		graph;
    
    try {
      graph = ((Drawable) obj).graph();
      panel = new TextEditorPanel();
      panel.setContent(graph);
      panel.setEditable(false);
      panel.setLineWrap(false);
      return new PreviewPanel(panel, panel.getTextArea());
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to obtain graph source:", e);
      return new NoPreviewAvailablePanel();
    }
  }
}

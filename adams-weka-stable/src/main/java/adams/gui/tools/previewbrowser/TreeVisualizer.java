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
 * TreeVisualizer.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.util.logging.Level;

import weka.core.Drawable;
import weka.gui.treevisualizer.PlaceNode2;

/**
 * Displays trees in dot notation.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TreeVisualizer
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
    return "Display graphs from " + Drawable.class.getName() + " classifiers that output trees in DOT notation.";
  }

  /**
   * Returns whether viewer handles this object.
   * 
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof Drawable) && (((Drawable) obj).graphType() == Drawable.TREE);
  }

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the preview for
   * @return		the preview, null if failed to generate preview
   */
  @Override
  protected PreviewPanel createPreview(Object obj) {
    weka.gui.treevisualizer.TreeVisualizer	panel;
    String					dot;
    
    try {
      dot = ((Drawable) obj).graph();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to obtain DOT notation:", e);
      dot = null;
    }
    if (dot == null)
      return null;
    
    panel = new weka.gui.treevisualizer.TreeVisualizer(null, dot, new PlaceNode2());
    
    return new PreviewPanel(panel);
  }
}

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
 * GraphVisualizer.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.previewbrowser;

import java.util.logging.Level;

import weka.classifiers.bayes.BayesNet;
import weka.core.Drawable;

/**
 * Displays {@link BayesNet} graphs.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GraphVisualizer
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
    return "Display graphs from " + BayesNet.class.getName() + " models.";
  }

  /**
   * Returns whether viewer handles this object.
   * 
   * @param obj		the object to check
   * @return		true if the object can be handled
   */
  @Override
  public boolean handles(Object obj) {
    return (obj instanceof Drawable) && (((Drawable) obj).graphType() == Drawable.BayesNet);
  }

  /**
   * Creates the actual preview.
   *
   * @param obj		the object to create the preview for
   * @return		the preview, null if failed to generate preview
   */
  @Override
  protected PreviewPanel createPreview(Object obj) {
    weka.gui.graphvisualizer.GraphVisualizer	panel;
    String					bif;
    
    try {
      bif = ((Drawable) obj).graph();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to obtain BIF graph:", e);
      bif = null;
    }
    if (bif == null)
      return null;
    
    panel = new weka.gui.graphvisualizer.GraphVisualizer();
    try {
      panel.readBIF(bif);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to parse BIF graph:", e);
      panel = null;
    }
    if (panel == null)
      return null;
    
    return new PreviewPanel(panel);
  }
}

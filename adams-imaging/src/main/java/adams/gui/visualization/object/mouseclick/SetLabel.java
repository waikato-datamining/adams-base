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
 * SetLabel.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.mouseclick;

import adams.flow.transformer.locateobjects.LocatedObject;
import adams.flow.transformer.locateobjects.LocatedObjects;
import adams.gui.visualization.object.ObjectAnnotationPanel;
import adams.gui.visualization.object.annotator.LabelSuffixHandler;

import java.awt.event.MouseEvent;

/**
 * Sets/unsets the current label.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SetLabel
  extends AbstractMouseClickProcessor {

  private static final long serialVersionUID = 8422134104160247274L;

  public static final String DEFAULT_SUFFIX = "type";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Sets the current label.\n"
      + "If the current label string is empty or null, then it will unset the label.";
  }

  /**
   * Processes the mouse event.
   *
   * @param panel 	the owning panel
   * @param e		the event
   */
  @Override
  protected void doProcess(ObjectAnnotationPanel panel, MouseEvent e) {
    LocatedObjects 	objects;
    LocatedObjects 	hits;
    boolean		unset;
    String		suffix;

    objects = new LocatedObjects(panel.getObjects());
    hits    = determineHits(panel, e);
    if (hits.size() == 0)
      return;

    unset = (panel.getCurrentLabel() == null) || panel.getCurrentLabel().isEmpty();
    if (panel.getAnnotator() instanceof LabelSuffixHandler)
      suffix = ((LabelSuffixHandler) panel.getAnnotator()).getLabelSuffix();
    else
      suffix = DEFAULT_SUFFIX;
    if (suffix.startsWith("."))
      suffix = suffix.substring(1);

    panel.addUndoPoint(unset ? "Removing label" : "Setting label '" + panel.getCurrentLabel() + "'");
    for (LocatedObject hit: hits) {
      objects.remove(hit);
      if (unset)
        hit.getMetaData().remove(suffix);
      else
        hit.getMetaData().put(suffix, panel.getCurrentLabel());
      objects.add(hit);
    }
    panel.setObjects(objects);
    panel.annotationsChanged(this);
  }
}

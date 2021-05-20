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
 * LabelSelector.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.labelselector;

import adams.gui.visualization.object.ObjectAnnotationPanel;

/**
 * Interface for panels that allow selection of a label.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LabelSelector {

  /**
   * Sets the owning panel.
   *
   * @param value	the panel
   */
  public void setOwner(ObjectAnnotationPanel value);

  /**
   * Returns the owning panel.
   *
   * @return		the panel, null if none set
   */
  public ObjectAnnotationPanel getOwner();

  /**
   * Returns the labels to choose from.
   *
   * @return		the labels
   */
  public String[] getLabels();

  /**
   * Pre-selects the label.
   *
   * @param label	the label to use
   */
  public void preselectCurrentLabel(String label);

  /**
   * Sets the current label to use.
   *
   * @param value	the label, null to unset
   */
  public void setCurrentLabel(String value);

  /**
   * Shows or hides the "Unset" button.
   *
   * @param value	true if to show, false to hide
   */
  public void setUnsetButtonVisible(boolean value);

  /**
   * Returns whether the unset button is visible.
   *
   * @return		true if visible
   */
  public boolean isUnsetButtonVisible();

  /**
   * Moves to the next label.
   */
  public void selectNextLabel();
}

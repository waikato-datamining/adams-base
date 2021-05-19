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
 * AbstractLabelSelectorPanel.java
 * Copyright (C) 2020-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.object.labelselector;

import adams.gui.core.BasePanel;
import adams.gui.visualization.object.ObjectAnnotationPanel;

/**
 * Ancestor for panels that .
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLabelSelectorPanel
  extends BasePanel {

  private static final long serialVersionUID = -4366283634060701920L;

  /** the owner. */
  protected ObjectAnnotationPanel m_Owner;

  /** whether to ignore changes to the label. */
  protected boolean m_IgnoreChanges;

  /**
   * Initializes the panel.
   *
   * @param owner	the owning panel
   */
  protected AbstractLabelSelectorPanel(ObjectAnnotationPanel owner) {
    super();

    setOwner(owner);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_IgnoreChanges = false;
  }

  /**
   * Sets the owning panel.
   *
   * @param value	the panel
   */
  public void setOwner(ObjectAnnotationPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owning panel.
   *
   * @return		the panel, null if none set
   */
  public ObjectAnnotationPanel getOwner() {
    return m_Owner;
  }

  /**
   * Pre-selects the label.
   *
   * @param label	the label to use
   */
  protected abstract void doPreselectCurrentLabel(String label);

  /**
   * Pre-selects the label.
   *
   * @param label	the label to use
   */
  public void preselectCurrentLabel(String label) {
    m_IgnoreChanges = true;
    doPreselectCurrentLabel(label);
    m_IgnoreChanges = false;
  }

  /**
   * Sets the current label to use.
   *
   * @param value	the label, null to unset
   */
  public void setCurrentLabel(String value) {
    if (getOwner() != null) {
      getOwner().setCurrentLabel(value);
      if (!m_IgnoreChanges)
	getOwner().labelChanged(this);
    }
    else {
      throw new IllegalStateException("No owning panel set!");
    }
  }

  /**
   * Shows or hides the "Unset" button.
   *
   * @param value	true if to show, false to hide
   */
  public abstract void setUnsetButtonVisible(boolean value);

  /**
   * Returns whether the unset button is visible.
   *
   * @return		true if visible
   */
  public abstract boolean isUnsetButtonVisible();
}

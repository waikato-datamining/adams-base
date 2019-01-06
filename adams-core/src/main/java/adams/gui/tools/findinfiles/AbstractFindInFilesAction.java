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
 * AbstractFindInFilesAction.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.findinfiles;

import adams.gui.action.AbstractBaseAction;
import adams.gui.tools.FindInFilesPanel;

/**
 * Ancestor for actions in the Find in files panel.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractFindInFilesAction
  extends AbstractBaseAction
  implements Comparable<AbstractFindInFilesAction> {

  private static final long serialVersionUID = -3674130494680943522L;

  /** the owner. */
  protected FindInFilesPanel m_Owner;

  /**
   * Initializes the action.
   */
  public AbstractFindInFilesAction() {
    super();
    setName(getMenuItemText());
  }

  /**
   * Sets the owning panel.
   *
   * @param value	the owner
   */
  public void setOwner(FindInFilesPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owning panel.
   *
   * @return		the owner
   */
  public FindInFilesPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the text
   */
  protected abstract String getMenuItemText();

  /**
   * Updates the action with the current state of the owner.
   */
  protected abstract void doUpdate();

  /**
   * Updates the action with the current state of the owner.
   */
  public void update() {
    if (getOwner() == null)
      setEnabled(false);
    else
      doUpdate();
  }

  /**
   * Compares with the other action using the menu item text.
   *
   * @param o		the other action
   * @return		less than zero, zero, or greater than zero if
   * 			the menu item text is smaller, equal to, or greater
   * 			than the other one
   */
  public int compareTo(AbstractFindInFilesAction o) {
    return getMenuItemText().compareTo(o.getMenuItemText());
  }

  /**
   * Checks whether the two actions are the same (using the menu item text).
   *
   * @param obj		the action to check
   * @return		true if the same menu item text
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof AbstractFindInFilesAction)
      && (compareTo((AbstractFindInFilesAction) obj) == 0);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();
    m_Owner = null;
  }
}

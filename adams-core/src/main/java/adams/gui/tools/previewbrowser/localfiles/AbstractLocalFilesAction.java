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
 * AbstractLocalFilesAction.java
 * Copyright (C) 2020 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.previewbrowser.localfiles;

import adams.gui.action.AbstractBaseAction;
import adams.gui.core.GUIHelper;
import adams.gui.tools.PreviewBrowserPanel;

/**
 * Ancestor for actions that get applied to local files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractLocalFilesAction
  extends AbstractBaseAction {

  private static final long serialVersionUID = 8392474373718743027L;

  /** the owner. */
  protected PreviewBrowserPanel m_Owner;

  /**
   * Sets the owner.
   *
   * @param value	the owner
   */
  public void setOwner(PreviewBrowserPanel value) {
    m_Owner = value;
  }

  /**
   * Returns the owner.
   *
   * @return		the owner
   */
  public PreviewBrowserPanel getOwner() {
    return m_Owner;
  }

  /**
   * Displays the specified error.
   *
   * @param msg		the error message
   */
  protected void displayError(String msg) {
    GUIHelper.showErrorMessage(m_Owner, msg);
  }

  /**
   * Displays the specified error message and exception.
   *
   * @param msg		the error message
   * @param e 		the exception
   */
  protected void displayError(String msg, Throwable e) {
    GUIHelper.showErrorMessage(m_Owner, msg, e);
  }

  /**
   * Updates the action.
   */
  protected abstract void doUpdate();

  /**
   * Updates the action.
   */
  public void update() {
    if (m_Owner != null)
      doUpdate();
  }
}

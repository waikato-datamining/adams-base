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
 * AbstractTabHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.flow.tabhandler;

import adams.core.CleanUpHandler;
import adams.core.logging.CustomLoggingLevelObject;
import adams.gui.flow.FlowEditorPanel;
import adams.gui.flow.FlowMultiPagePane;
import adams.gui.flow.FlowPanel;

/**
 * Ancestor for tab handlers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTabHandler
  extends CustomLoggingLevelObject
  implements CleanUpHandler {

  private static final long serialVersionUID = 5686865821382789408L;

  /** the owning flow panel. */
  protected FlowPanel m_Owner;

  /**
   * Initializes the tab handler
   *
   * @param owner	the owning panel
   */
  public AbstractTabHandler(FlowPanel owner) {
    super();
    m_Owner = owner;
    initialize();
  }

  /**
   * Method for initializing member variables.
   */
  protected void initialize() {
  }

  /**
   * Returns the owner.
   *
   * @return		the flow panel
   */
  public FlowPanel getOwner() {
    return m_Owner;
  }

  /**
   * Returns the multi-page pane this panel belongs to.
   *
   * @return		the pane, null if none set
   */
  public FlowMultiPagePane getMultiPage() {
    if (m_Owner != null)
      return m_Owner.getOwner();
    else
      return null;
  }

  /**
   * Returns the editor this panel belongs to.
   *
   * @return		the editor, null if none set
   */
  public FlowEditorPanel getEditor() {
    if (m_Owner != null)
      return m_Owner.getOwner().getOwner();
    else
      return null;
  }

  /**
   * Gets called when the page changes.
   */
  public abstract void display();

  /**
   * Cleans up data structures, frees up memory.
   */
  public abstract void cleanUp();
}

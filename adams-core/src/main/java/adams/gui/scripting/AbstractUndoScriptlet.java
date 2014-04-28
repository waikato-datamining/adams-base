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
 * AbstractDatabaseScriptlet.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

import adams.gui.core.Undo;
import adams.gui.core.UndoHandler;

/**
 * Ancestor for scriptlets that allow undo (if available).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractUndoScriptlet
  extends AbstractScriptlet
  implements UndoHandler {

  /** for serialization. */
  private static final long serialVersionUID = 7823149146820368462L;

  /**
   * Returns the class(es) of an object that must be present for this action
   * to be executed.
   *
   * @return		the class(es) of which an instance must be present for
   * 			execution, null if none necessary
   */
  public Class[] getRequirements() {
    return new Class[]{UndoHandler.class};
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value) {
    if (isUndoSupported())
      ((UndoHandler) getOwner().getBasePanel()).setUndo(value);
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  public Undo getUndo() {
    if (isUndoSupported())
      return ((UndoHandler) getOwner().getBasePanel()).getUndo();
    else
      return null;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported() {
    return (    hasOwner()
	     && (getOwner().getBasePanel() instanceof UndoHandler)
	     && ((UndoHandler) getOwner().getBasePanel()).isUndoSupported());
  }

  /**
   * Adds an undo point, if possible.
   *
   * @param statusMsg	the status message to display while adding the undo point
   * @param undoComment	the comment for the undo point
   */
  protected void addUndoPoint(String statusMsg, String undoComment) {
    getOwner().addUndoPoint(statusMsg, undoComment);
  }
}

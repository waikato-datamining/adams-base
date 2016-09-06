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
 * UndoHandlerWithQuickAccess.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.core;

/**
 * Extended {@link UndoHandler} with quick access methods for performing
 * undo/redo.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface UndoHandlerWithQuickAccess
  extends UndoHandler {

  /**
   * Performs an undo if possible.
   */
  public void undo();

  /**
   * Performs a redo if possible.
   */
  public void redo();

  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  public void addUndoPoint(String comment);
}

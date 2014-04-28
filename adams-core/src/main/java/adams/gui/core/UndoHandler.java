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
 * UndoHandler.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;


/**
 * Interface for classes that support an optional undo-mechanism.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface UndoHandler {
  
  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   * 
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value);
  
  /**
   * Returns the current undo manager, can be null.
   * 
   * @return		the undo manager, if any
   */
  public Undo getUndo();

  /**
   * Returns whether an Undo manager is currently available.
   * 
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported();
}

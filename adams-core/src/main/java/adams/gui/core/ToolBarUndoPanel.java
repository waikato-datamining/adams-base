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
 * ToolBarUndoPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.gui.event.UndoListener;

/**
 * A toolbar panel with the ability for undo.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class ToolBarUndoPanel
  extends ToolBarPanel
  implements UndoListener, UndoHandler {

  /** for serialization. */
  private static final long serialVersionUID = -8319815232257188840L;

  /** the undo manager. */
  protected Undo m_Undo;

  /**
   * Initializes the panel, the undo mechanism holds the undo list in memory.
   */
  public ToolBarUndoPanel() {
    this(Object.class, false);
  }

  /**
   * Initializes the panel, the undo mechanism holds the undo list either
   * in memory or stores the objects on disk.
   *
   * @param undoClass		the class of objects the undo list is for
   * @param onDisk	if true then the objects are stored on disk
   */
  public ToolBarUndoPanel(Class undoClass, boolean onDisk) {
    super();

    m_Undo = new Undo(undoClass, onDisk);
    m_Undo.addUndoListener(this);
  }

  /**
   * For initializing members.
   */
  protected void initialize() {
    super.initialize();

    // dummy variable will be replaced by correct instance in constructor
    m_Undo = new Undo();
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value) {
    m_Undo = value;
  }

  /**
   * Returns the current undo manager, can be null.
   *
   * @return		the undo manager, if any
   */
  public Undo getUndo() {
    return m_Undo;
  }

  /**
   * Returns whether an Undo manager is currently available.
   *
   * @return		true if an undo manager is set
   */
  public boolean isUndoSupported() {
    return (m_Undo != null);
  }
}

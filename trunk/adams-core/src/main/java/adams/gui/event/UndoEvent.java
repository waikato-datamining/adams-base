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
 * UndoEvent.java
 * Copyright (C) 2008 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.event;


import java.util.EventObject;

import adams.gui.core.Undo;
import adams.gui.core.Undo.UndoPoint;

/**
 * An event that gets sent in case of an Undo event (add, undo).
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UndoEvent
  extends EventObject {

  /** for serialization. */
  private static final long serialVersionUID = 1054597438625837914L;

  /**
   * The enum of event types.
   * 
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum UndoType {
    /** an undo point was added. */
    ADD_UNDO,
    /** a redo point was added. */
    ADD_REDO,
    /** performed an "undo". */
    UNDO,
    /** performed a "redo". */
    REDO,
    /** the undo list was cleared. */
    CLEAR
  }
  
  /** the type of event. */
  protected UndoType m_Type;
  
  /** the undo point that got added, removed, etc. */
  protected UndoPoint m_UndoPoint;
  
  /** whether the action was successful. */
  protected boolean m_Success;
  
  /**
   * Initializes the event.
   * 
   * @param source	the Undo manager that sent the event
   * @param undoPoint	the undo point that got added or removed from the Undo list
   * @param type	the type of event: add, remove, etc.
   * @param success	whether the action was successful
   */
  public UndoEvent(Object source, UndoPoint undoPoint, UndoType type, boolean success) {
    super(source);
    
    m_UndoPoint = undoPoint;
    m_Type      = type;
    m_Success   = success;
  }
  
  /**
   * Returns the undo manager responsible for this event.
   * 
   * @return		the undo manager
   */
  public Undo getUndo() {
    return (Undo) getSource();
  }
  
  /**
   * Returns the undo point that was added to, removed from, etc. the undo list.
   * Can be null in case of a CLEAR event.
   * 
   * @return		the object
   */
  public UndoPoint getUndoPoint() {
    return m_UndoPoint;
  }
  
  /**
   * Returns the type of event.
   * 
   * @return		the type
   */
  public UndoType getType() {
    return m_Type;
  }
  
  /**
   * Returns whether the action was successful.
   * 
   * @return		true if the action was successful
   */
  public boolean getSuccess() {
    return m_Success;
  }
}

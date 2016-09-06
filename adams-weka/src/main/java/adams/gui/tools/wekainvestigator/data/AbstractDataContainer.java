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
 * AbstractDataContainer.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.data;

import adams.core.logging.LoggingObject;
import adams.gui.core.Undo;
import adams.gui.core.Undo.UndoPoint;
import adams.gui.event.UndoEvent;
import adams.gui.event.UndoEvent.UndoType;
import adams.gui.event.UndoListener;
import weka.core.Instances;

import java.io.Serializable;

/**
 * Ancestor for data containers.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractDataContainer
  extends LoggingObject
  implements DataContainer, UndoListener {

  private static final long serialVersionUID = 6267905940957451551L;

  /** the ID counter. */
  protected static int m_IDCounter;

  /** the ID of the container. */
  protected int m_ID;

  /** the underlying data. */
  protected Instances m_Data;

  /** whether the data has been modified. */
  protected boolean m_Modified;

  /** the undo manager. */
  protected Undo m_Undo;

  /**
   * Initializes the container with no data.
   */
  public AbstractDataContainer() {
    m_ID       = nextID();
    m_Data     = null;
    m_Modified = false;
    m_Undo     = new Undo(Serializable[].class, true);
    m_Undo.addUndoListener(this);
  }

  /**
   * Initializes the container with just the data.
   *
   * @param data	the data to use
   */
  public AbstractDataContainer(Instances data) {
    m_Data     = data;
    m_Modified = false;
  }

  /**
   * Sets the data.
   *
   * @param value	the data to use
   */
  public void setData(Instances value) {
    if (m_Data != null) {
      addUndoPoint("updated data");
      setModified(true);
    }
    m_Data = value;
  }

  /**
   * Returns the actual underlying data.
   *
   * @return		the data
   */
  @Override
  public Instances getData() {
    return m_Data;
  }

  /**
   * Returns the container ID.
   *
   * @return		the ID
   */
  public int getID() {
    return m_ID;
  }

  /**
   * Checks whether the data has been modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Sets whether the data has been modified.
   *
   * @param value	true if modified
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  protected abstract boolean doReload();

  /**
   * Reloads the data.
   *
   * @return		true if successfully reloaded
   */
  @Override
  public boolean reload() {
    boolean	result;

    result = false;

    if (canReload()) {
      result = doReload();
      if (result) {
	setModified(false);
	if (isUndoSupported())
	  getUndo().clear();
      }
    }

    return result;
  }

  /**
   * Sets the undo manager to use, can be null if no undo-support wanted.
   *
   * @param value	the undo manager to use
   */
  public void setUndo(Undo value) {
    if (m_Undo != null)
      m_Undo.removeUndoListener(this);

    m_Undo = value;

    if (m_Undo != null)
      m_Undo.addUndoListener(this);
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
    return (m_Undo != null) && m_Undo.isEnabled();
  }

  /**
   * An undo event, like add or remove, has occurred.
   *
   * @param e		the trigger event
   */
  public void undoOccurred(UndoEvent e) {
    if (e.getType() == UndoType.UNDO)
      applyUndoData((Serializable[]) e.getUndoPoint().getData());
  }

  /**
   * Performs an undo if possible.
   */
  public void undo() {
    UndoPoint 	point;

    if (!isUndoSupported() || !getUndo().canUndo())
      return;

    m_Undo.addRedo(getUndoData(), m_Undo.peekUndoComment());
    point = m_Undo.undo();
    applyUndoData((Serializable[]) point.getData());
  }

  /**
   * Performs a redo if possible.
   */
  public void redo() {
    UndoPoint 	point;

    if (!isUndoSupported() || !getUndo().canUndo())
      return;

    m_Undo.addUndo(getUndoData(), m_Undo.peekRedoComment(), true);

    point = m_Undo.redo();
    applyUndoData((Serializable[]) point.getData());
  }

  /**
   * Adds an undo point with the given comment.
   *
   * @param comment	the comment for the undo point
   */
  public void addUndoPoint(String comment) {
    if (isUndoSupported() && m_Undo.isEnabled())
      getUndo().addUndo(getUndoData(), comment);
  }

  /**
   * Returns the data to store in the undo.
   *
   * @return		the undo point
   */
  protected Serializable[] getUndoData() {
    return new Serializable[]{
      m_Data,
      m_Modified
    };
  }

  /**
   * Restores the data from the undo point.
   *
   * @param data	the undo point
   */
  protected void applyUndoData(Serializable[] data) {
    m_Data     = (Instances) data[0];
    m_Modified = (Boolean) data[1];
  }

  /**
   * Compares this container with the specified one.
   *
   * @param o		the container to compare with
   * @return		less than, equal to or greater than 0 if the container's
   * 			{@link #getSource()} and {@link #getID()} is smaller, equal to or greater
   * 			then the provided one
   */
  public int compareTo(DataContainer o) {
    int		result;

    result = getSource().compareTo(o.getSource());
    if (result == 0)
      result = new Integer(getID()).compareTo(o.getID());

    return result;
  }

  /**
   * Checks whether the specified object is the same.
   *
   * @param obj		the object to check
   * @return		true if the same, i.e., the same {@link #getSource()}
   * @see		#getID()
   */
  @Override
  public boolean equals(Object obj) {
    return (obj instanceof DataContainer) && (getID() == ((DataContainer) obj).getID());
  }

  /**
   * Returns a short description of the container.
   *
   * @return		the description
   */
  @Override
  public String toString() {
    return getData().relationName() + " [" + getSource() + "]";
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Data = null;
    if (m_Undo != null)
      m_Undo.cleanUp();
  }

  /**
   * Returns the next container ID.
   *
   * @return		the next ID
   */
  protected static synchronized int nextID() {
    return m_IDCounter++;
  }
}

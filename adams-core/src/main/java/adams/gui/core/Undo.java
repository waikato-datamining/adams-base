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
 * Undo.java
 * Copyright (C) 2008-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.core.ClassLocator;
import adams.core.CloneHandler;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.gui.event.UndoEvent;
import adams.gui.event.UndoEvent.UndoType;
import adams.gui.event.UndoListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * A general Undo/Redo-mechanism: stores objects either in memory or on disk.
 * The objects have to be serializable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Undo {

  /**
   * Represents a single undo point, i.e., data and comment.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class UndoPoint
    implements Serializable, CloneHandler<UndoPoint> {

    /** for serialization. */
    private static final long serialVersionUID = -8911750482537649987L;

    /** the undo data. */
    protected Object m_Data;

    /** the associated comment. */
    protected String m_Comment;

    /**
     * Initializes the undo point.
     *
     * @param data	the data to store
     * @param comment	the associated comment
     */
    public UndoPoint(Object data, String comment) {
      super();

      m_Data    = data;
      m_Comment = comment;
    }

    /**
     * Returns a copy of itself.
     *
     * @return		the copy
     */
    public UndoPoint getClone() {
      return new UndoPoint(Utils.deepCopy(m_Data), new String(m_Comment));
    }

    /**
     * Returns the stored data.
     *
     * @return		the data
     */
    public Object getData() {
      return m_Data;
    }

    /**
     * Returns the comment associated with the data.
     *
     * @return		the comment
     */
    public String getComment() {
      return m_Comment;
    }

    /**
     * Returns a string representation of the undo point.
     *
     * @return		a string representation
     */
    @Override
    public String toString() {
      return m_Data.getClass().getName() + " - " + getComment();
    }
  }

  /** the maximum length for a comment. */
  public final static int COMMENT_MAX_LENGTH = 40;

  /** the default for number of undo steps. */
  public final static int DEFAULT_MAX_UNDO = 100;

  /** the class for which the undo is. */
  protected Class m_UndoClass;

  /** whether to store the objects on disk. */
  protected boolean m_OnDisk;

  /** the vector for the undo-points objects. */
  protected List<UndoPoint> m_UndoList;

  /** the vector for the redo-points objects. */
  protected List<UndoPoint> m_RedoList;

  /** the list of listeners. */
  protected HashSet<UndoListener> m_Listeners;

  /** whether undo is currently enabled or not. */
  protected boolean m_Enabled;

  /** whether the object is currently working, e.g., performing an undo/redo. */
  protected boolean m_Working;

  /** the maximum number of undo points. */
  protected int m_MaxUndo;

  /**
   * Initializes the undo mechanism. Maximum of DEFAULT_MAX_UNDO undo steps.
   *
   * @see		#setMaxUndo(int)
   * @see		#DEFAULT_MAX_UNDO
   */
  public Undo() {
    this(Object.class);
  }

  /**
   * Initializes the undo mechanism for the specified class (in memory).
   * Maximum of DEFAULT_MAX_UNDO undo steps.
   *
   * @param undoClass	the class for which the undo is setup
   * @see		#setMaxUndo(int)
   * @see		#DEFAULT_MAX_UNDO
   */
  public Undo(Class undoClass) {
    this(undoClass, false);
  }

  /**
   * Initializes the undo mechanism for the specified class.
   * Maximum of DEFAULT_MAX_UNDO undo steps.
   *
   * @param undoClass	the class for which the undo is setup
   * @param onDisk	whether to store the objects on disk (when they're serializable)
   * @see		#setMaxUndo(int)
   * @see		#DEFAULT_MAX_UNDO
   */
  public Undo(Class undoClass, boolean onDisk) {
    super();

    m_UndoClass   = undoClass;
    m_OnDisk      = onDisk && ClassLocator.hasInterface(Serializable.class, undoClass);
    m_UndoList    = new ArrayList<UndoPoint>();
    m_RedoList    = new ArrayList<UndoPoint>();
    m_Listeners   = new HashSet<UndoListener>();
    m_Enabled     = true;
    m_Working     = false;
    m_MaxUndo     = DEFAULT_MAX_UNDO;
  }

  /**
   * Returns the class this undo mechanism is for.
   *
   * @return		the class this undo is for
   */
  public Class getUndoClass() {
    return m_UndoClass;
  }

  /**
   * Returns whether the objects are stored on disk (only possible for
   * serializable ones) or held in memory.
   *
   * @return		true if the objects are stored on disk
   */
  public boolean getOnDisk() {
    return m_OnDisk;
  }

  /**
   * Returns whether undo is currently enabled or not.
   *
   * @return		true if undo is enabled
   */
  public boolean isEnabled() {
    return m_Enabled;
  }

  /**
   * Sets the enabled state, i.e., either enables or disables the undo.
   *
   * @param value	if true then the undo will be enabled.
   */
  public void setEnabled(boolean value) {
    m_Enabled = value;
  }

  /**
   * Sets the maximum number of undo steps to allow. Can only be set before the
   * first undo/redo happens.
   *
   * @param value	the number of undo steps, use -1 for unlimited
   */
  public synchronized void setMaxUndo(int value) {
    if ((value == -1) || (value > 0)) {
      if ((m_UndoList.size() == 0) && (m_RedoList.size() == 0))
	m_MaxUndo = value;
      else
	System.err.println(
	    "Cannot change undo step limit after undo/redo steps already occurred - ignored!");
    }
    else {
      System.err.println(
	  "Maximum number of undo steps must be >0 or -1 for unlimited, "
	  + "provided: " + value);
    }
  }

  /**
   * Returns the maximum number of undo steps to allow.
   *
   * @return		the number of undo steps, -1 if unlimited
   */
  public int getMaxUndo() {
    return m_MaxUndo;
  }

  /**
   * Returns whether undo/redo is currently performed.
   *
   * @return		true if an undo/redo is currently performed
   */
  public boolean isWorking() {
    return m_Working;
  }

  /**
   * Adds the object to the undo list, throws the Redo list away.
   *
   * @param o		the object to add to the undo list
   * @param comment	the comment to associate with the undo point
   * @return		true if the object was added successfully
   */
  public boolean addUndo(Object o, String comment) {
    return addUndo(o, comment, false);
  }

  /**
   * Adds the object to the undo list.
   *
   * @param o		the object to add to the undo list
   * @param comment	the comment to associate with the undo point
   * @param keepRedo	whether to keep the redo list
   * @return		true if the object was added successfully
   */
  public boolean addUndo(Object o, String comment, boolean keepRedo) {
    boolean		result;
    File		file;
    ObjectOutputStream 	oos;
    UndoPoint		point;

    if (!isEnabled())
      return false;

    result    = true;
    m_Working = true;

    if (m_OnDisk) {
      try {
	// create tmp file
	file = FileUtils.createTempFile("undo", null);
	file.deleteOnExit();

	// write to disk
	oos  = new ObjectOutputStream(new FileOutputStream(file));
	oos.writeObject(o);
	oos.flush();
	oos.close();

	// create undo point
	point = new UndoPoint(file, comment);
      }
      catch (Exception e) {
	e.printStackTrace();
	point  = null;
	result = false;
      }
    }
    else {
      // create undo point
      point = new UndoPoint(o, comment);
    }

    // add to history
    m_UndoList.add(point);

    // trim history, if necessary
    if (m_MaxUndo > 0) {
      while ((m_UndoList.size() > 1) && (m_UndoList.size() + m_RedoList.size() > m_MaxUndo))
	m_UndoList.remove(0);
    }

    // clear redo history
    if (!keepRedo)
      cleanUp(m_RedoList);

    m_Working = false;

    // notify listeners
    notifyListeners(new UndoEvent(this, point, UndoType.ADD_UNDO, result));

    return result;
  }

  /**
   * Adds the object to the redo list.
   *
   * @param o		the object to add to the redo list
   * @param comment	the comment to associate with the redo point
   * @return		true if the object was added successfully
   */
  public boolean addRedo(Object o, String comment) {
    boolean		result;
    File		file;
    ObjectOutputStream 	oos;
    UndoPoint		point;

    if (!isEnabled())
      return false;

    result    = true;
    m_Working = true;

    if (m_OnDisk) {
      try {
	// create tmp file
	file = FileUtils.createTempFile("redo", null);
	file.deleteOnExit();

	// write to disk
	oos  = new ObjectOutputStream(new FileOutputStream(file));
	oos.writeObject(o);
	oos.flush();
	oos.close();

	// create undo point
	point = new UndoPoint(file, comment);
      }
      catch (Exception e) {
	e.printStackTrace();
	point  = null;
	result = false;
      }
    }
    else {
      // create undo point
      point = new UndoPoint(o, comment);
    }

    // add to history
    m_RedoList.add(point);

    m_Working = false;

    // notify listeners
    notifyListeners(new UndoEvent(this, point, UndoType.ADD_REDO, result));

    return result;
  }

  /**
   * Returns whether any undo-steps are left.
   *
   * @return		true if at least one undo-step is left
   */
  public boolean canUndo() {
    return isEnabled() && (m_UndoList.size() > 0) && !isWorking();
  }

  /**
   * Returns the object from the next undo step and removes it from its
   * internal list.
   *
   * @return		the next undo object
   */
  public UndoPoint undo() {
    return undo(true);
  }

  /**
   * "Peeks" at the last undo item.
   * 
   * @return		the last undo item
   */
  public UndoPoint peekUndo() {
    return m_UndoList.get(m_UndoList.size() - 1);
  }
  
  /**
   * Returns the comment of the last undo point.
   *
   * @return		the comment
   */
  public String peekUndoComment() {
    return peekUndoComment(false);
  }

  /**
   * Returns the comment of the last undo point.
   *
   * @param shorten	whether to shorten the comment if too long
   * @return		the comment
   */
  public String peekUndoComment(boolean shorten) {
    if (shorten)
      return shortenComment(m_UndoList.get(m_UndoList.size() - 1).getComment());
    else
      return m_UndoList.get(m_UndoList.size() - 1).getComment();
  }

  /**
   * Returns the object from the next undo step and removes it from its
   * internal list.
   *
   * @param notify	whether a notify event should be sent
   * @return		the next undo object, or null if not successful
   */
  protected UndoPoint undo(boolean notify) {
    UndoPoint		result;
    File		file;
    boolean		success;
    ObjectInputStream 	ois;
    UndoPoint		point;

    success   = true;
    m_Working = true;

    if (m_OnDisk) {
      try {
	// remove file from history
	point = m_UndoList.remove(m_UndoList.size() - 1);
	file  = (File) point.getData();

	// load object
	ois    = new ObjectInputStream(new FileInputStream(file));
	result = new UndoPoint(ois.readObject(), new String(point.getComment()));
	ois.close();
      }
      catch (Exception e) {
	e.printStackTrace();
	result  = null;
	point   = null;
	success = false;
      }
    }
    else {
      // remove from history
      result = m_UndoList.remove(m_UndoList.size() - 1);
    }

    m_Working = false;

    // notify listeners
    if (notify)
      notifyListeners(new UndoEvent(this, result, UndoType.UNDO, success));

    return result;
  }

  /**
   * Returns whether any redo-steps are left.
   *
   * @return		true if at least one redo-step is left
   */
  public boolean canRedo() {
    return isEnabled() && (m_RedoList.size() > 0) && !isWorking();
  }

  /**
   * Returns the object from the next redo step and removes it from its
   * internal list.
   *
   * @return		the next undo object
   */
  public UndoPoint redo() {
    return redo(true);
  }

  /**
   * "Peeks" at the last redo item.
   * 
   * @return		the last redo item
   */
  public UndoPoint peekRedo() {
    return m_RedoList.get(m_RedoList.size() - 1);
  }

  /**
   * Returns the comment of the last redo point.
   *
   * @return		the comment
   */
  public String peekRedoComment() {
    return peekRedoComment(false);
  }

  /**
   * Returns the comment of the last redo point.
   *
   * @param shorten	whether to shorten the comment, if too long
   * @return		the comment
   */
  public String peekRedoComment(boolean shorten) {
    if (shorten)
      return shortenComment(m_RedoList.get(m_RedoList.size() - 1).getComment());
    else
      return m_RedoList.get(m_RedoList.size() - 1).getComment();
  }

  /**
   * Returns the object from the next redo step and removes it from its
   * internal list.
   *
   * @param notify	whether a notify event should be sent
   * @return		the next redo object, or null if not successful
   */
  protected UndoPoint redo(boolean notify) {
    UndoPoint		result;
    File		file;
    boolean		success;
    ObjectInputStream 	ois;
    UndoPoint		point;

    success   = true;
    m_Working = true;

    if (m_OnDisk) {
      try {
	// remove file from history
	point = m_RedoList.remove(m_RedoList.size() - 1);
	file  = (File) point.getData();

	// load object
	ois    = new ObjectInputStream(new FileInputStream(file));
	result = new UndoPoint(ois.readObject(), new String(point.getComment()));
	ois.close();
      }
      catch (Exception e) {
	e.printStackTrace();
	result  = null;
	point   = null;
	success = false;
      }
    }
    else {
      // remove from history
      result = m_RedoList.remove(m_RedoList.size() - 1);
    }

    m_Working = false;

    // notify listeners
    if (notify)
      notifyListeners(new UndoEvent(this, result, UndoType.REDO, success));

    return result;
  }

  /**
   * If "onDisk" is active, removes all files from disk, otherwise it only
   * empties the list.
   *
   * @param list	the undo points to clean up
   */
  protected void cleanUp(List<UndoPoint> list) {
    int		i;
    UndoPoint	point;
    File	file;

    // remove files from disk
    if (m_OnDisk) {
      for (i = 0; i < list.size(); i++) {
	point = list.get(i);
	file  = (File) point.getData();
	file.delete();
      }
    }

    list.clear();
  }

  /**
   * Clears the undo and redo list.
   */
  public void clear() {
    m_Working = true;

    cleanUp(m_UndoList);
    cleanUp(m_RedoList);

    m_Working = false;

    // notify listeners
    notifyListeners(new UndoEvent(this, null, UndoType.CLEAR, true));
  }

  /**
   * Adds the listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addUndoListener(UndoListener l) {
    m_Listeners.add(l);
  }

  /**
   * Removes the listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeUndoListener(UndoListener l) {
    m_Listeners.remove(l);
  }

  /**
   * notifies all listeners with the specified event.
   *
   * @param e		the event to send
   */
  protected void notifyListeners(UndoEvent e) {
    Iterator<UndoListener>	iter;

    iter = m_Listeners.iterator();
    while (iter.hasNext())
      iter.next().undoOccurred(e);
  }

  /**
   * Shortens the comment.
   *
   * @param s		the comment to process
   * @return		the (potentially) shortened comment
   */
  protected String shortenComment(String s) {
    String	result;

    result = s;

    if (result.length() > COMMENT_MAX_LENGTH)
      result = result.substring(0, COMMENT_MAX_LENGTH) + "...";

    return result;
  }
}

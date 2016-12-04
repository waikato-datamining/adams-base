/**
 * AbstractPersistentHistory.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import adams.core.io.PlaceholderFile;

/**
 * Ancestor of classes for maintaining a history of objects that are stored
 * on disk.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of objects to handler
 */
public abstract class AbstractPersistentHistory<T>
  extends AbstractHistory<T> {

  /** for serialization. */
  private static final long serialVersionUID = 6749583793409177117L;

  /** the file to save the history to. */
  protected PlaceholderFile m_HistoryFile;

  /**
   * Initializes members.
   */
  protected void initialize() {
    super.initialize();
    
    m_HistoryFile = new PlaceholderFile(".");
  }

  /**
   * Clears the history.
   */
  @Override
  public synchronized void clear() {
    super.clear();
    save();
  }

  /**
   * Sets the file to load from/save to.
   * 
   * @param value	the history file
   */
  public synchronized void setHistoryFile(PlaceholderFile value) {
    m_HistoryFile = value;
    load();
  }
  
  /**
   * Returns the file to load from/save to.
   * 
   * @return		the history file
   */
  public PlaceholderFile getHistoryFile() {
    return m_HistoryFile;
  }
  
  /**
   * Adds the object to the history.
   *
   * @param obj		the object to add
   */
  public synchronized void add(T obj) {
    super.add(obj);
    save();
  }
  
  /**
   * Saves the history to disk.
   * 
   * @return		true if successfully saved
   */
  protected abstract boolean save();
  
  /**
   * Loads the history from disk.
   * 
   * @return		true if successfully loaded
   */
  protected abstract boolean load();
}

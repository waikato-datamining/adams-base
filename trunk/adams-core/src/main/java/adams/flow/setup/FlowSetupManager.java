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
 * FlowSetupManager.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.setup;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import adams.core.CloneHandler;
import adams.core.Properties;
import adams.core.StatusMessageHandler;
import adams.core.option.OptionUtils;
import adams.event.FlowSetupStateEvent;
import adams.event.FlowSetupStateEvent.Type;
import adams.event.FlowSetupStateListener;

/**
 * Manages several setups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowSetupManager
  extends Vector<FlowSetup>
  implements FlowSetupStateListener, CloneHandler<FlowSetupManager> {

  /** for serialization. */
  private static final long serialVersionUID = -571220451906124470L;

  /** the key for the number of stored setups. */
  public final static String COUNT = "count";

  /** the key prefix for a setup. */
  public final static String SETUP_PREFIX = "setup.";

  /** the key prefix for a placeholder. */
  public final static String PLACEHOLDER_PREFIX = "placeholder.";

  /** whether manager got modified. */
  protected boolean m_Modified;

  /** the change listeners. */
  protected HashSet<ChangeListener> m_ChangeListeners;

  /** the handler to use for displaying messages. */
  protected StatusMessageHandler m_StatusMessageHandler;

  /**
   * Initializes the setup manager.
   */
  public FlowSetupManager() {
    super();

    m_Modified             = false;
    m_ChangeListeners      = new HashSet<ChangeListener>();
    m_StatusMessageHandler = null;
  }

  /**
   * Initializes the setup manager.
   *
   * @param collection	the collection to initialize with
   */
  public FlowSetupManager(Collection<FlowSetup> collection) {
    super(collection);

    Iterator<FlowSetup> iter = collection.iterator();
    while (iter.hasNext())
      iter.next().addFlowSetupStateChangeListener(this);

    m_Modified = false;
  }

  /**
   * Sets the handler for status messages.
   *
   * @param value	the handler
   */
  public void setStatusMessageHandler(StatusMessageHandler value) {
    m_StatusMessageHandler = value;
  }

  /**
   * Returns the current handler for status messages.
   *
   * @return		the handler, null if none set
   */
  public StatusMessageHandler getStatusMessageHandler() {
    return m_StatusMessageHandler;
  }

  /**
   * Returns whether the manager was modified.
   *
   * @return		true if modified
   */
  public boolean isModified() {
    return m_Modified;
  }

  /**
   * Sets the modified state of the manager.
   *
   * @param value	the modified state
   */
  public void setModified(boolean value) {
    m_Modified = value;
  }

  /**
   * Sets the modified state to TRUE and notifies all change listeners.
   */
  protected void modified() {
    modified(true);
  }

  /**
   * Sets the modified state and notifies all change listeners.
   *
   * @param modified	the modified state
   */
  protected void modified(boolean modified) {
    m_Modified = modified;
    notifyChangeListeners(new ChangeEvent(this));
  }

  /**
   * Adds a setup.
   *
   * @param e		the setup
   * @return		true if the manager was modified
   */
  @Override
  public synchronized boolean add(FlowSetup e) {
    boolean	result;

    result = super.add(e);
    e.setOwner(this);
    e.addFlowSetupStateChangeListener(this);

    if (result)
      modified();

    return result;
  }

  /**
   * Adds all the setups from the collection.
   *
   * @param c		the collection to add
   * @return		true if the manager was modified
   */
  @Override
  public synchronized boolean addAll(Collection<? extends FlowSetup> c) {
    boolean		result;
    Iterator<FlowSetup> iter;
    FlowSetup		setup;

    result = super.addAll(c);

    iter = (Iterator<FlowSetup>) c.iterator();
    while (iter.hasNext()) {
      setup = iter.next();
      setup.setOwner(this);
      setup.addFlowSetupStateChangeListener(this);
    }

    if (result)
      modified();

    return result;
  }

  /**
   * Adds the specified setup.
   *
   * @param obj		the setup to add
   */
  @Override
  public synchronized void addElement(FlowSetup obj) {
    boolean	modified;

    modified = !contains(obj);
      modified();

    super.addElement(obj);
    obj.setOwner(this);
    obj.addFlowSetupStateChangeListener(this);

    if (!modified)
      modified();
  }

  /**
   * Adds the setup at the specified location.
   *
   * @param index	the index to add the setup
   * @param element	the setup to add
   */
  @Override
  public void add(int index, FlowSetup element) {
    super.add(index, element);
    element.setOwner(this);
    element.addFlowSetupStateChangeListener(this);
    modified();
  }

  /**
   * Adds all elements of the collection at the specified location.
   *
   * @param index	the index at which to add the setups
   * @param c		the collection with the setups to add
   * @return		true if collection got modified
   */
  @Override
  public synchronized boolean addAll(int index, Collection<? extends FlowSetup> c) {
    boolean		result;
    Iterator<FlowSetup>	iter;
    FlowSetup		setup;

    result = super.addAll(index, c);

    iter = (Iterator<FlowSetup>) c.iterator();
    while (iter.hasNext()) {
      setup = iter.next();
      setup.setOwner(this);
      setup.addFlowSetupStateChangeListener(this);
    }

    if (result)
      modified();

    return result;
  }

  /**
   * Removes all setups.
   */
  @Override
  public void clear() {
    int		i;
    boolean	modified;

    modified = (size() > 0);

    for (i = 0; i < size(); i++) {
      get(i).setOwner(null);
      get(i).removeFlowSetupStateChangeListener(this);
    }

    super.clear();

    if (modified)
      modified();
  }

  /**
   * Returns a clone of itself, with the setups being shallow copies.
   *
   * @return		the clone
   */
  public synchronized FlowSetupManager getClone() {
    FlowSetupManager	result;
    int			i;

    result = new FlowSetupManager();
    for (i = 0; i < size(); i++)
      result.add(get(i).shallowCopy());
    result.setModified(m_Modified);

    return result;
  }

  /**
   * Inserts the setup at the specified location.
   *
   * @param obj		the setup to insert
   * @param index	the position
   */
  @Override
  public synchronized void insertElementAt(FlowSetup obj, int index) {
    super.insertElementAt(obj, index);
    obj.setOwner(this);
    obj.addFlowSetupStateChangeListener(this);
    modified();
  }

  /**
   * Removes the specified setup.
   *
   * @param index	the position of the setup to remove
   * @return		the element that was removed
   */
  @Override
  public synchronized FlowSetup remove(int index) {
    FlowSetup	result;

    get(index).setOwner(null);
    get(index).removeFlowSetupStateChangeListener(this);
    result = super.remove(index);

    modified();

    return result;
  }

  /**
   * Removes all setups of the specified collection.
   *
   * @param c		the collection to use for removing setups
   * @return		true if the collection changed
   */
  @Override
  public synchronized boolean removeAll(Collection<?> c) {
    boolean		result;
    Iterator<FlowSetup>	iter;
    FlowSetup		setup;

    result = super.removeAll(c);

    iter = (Iterator<FlowSetup>) c.iterator();
    while (iter.hasNext()) {
      setup = iter.next();
      setup.setOwner(null);
      setup.removeFlowSetupStateChangeListener(this);
    }

    if (result)
      modified();

    return result;
  }

  /**
   * Removes the specified object.
   *
   * @param o		the object to remove
   * @return		true if object was removed
   */
  @Override
  public boolean remove(Object o) {
    boolean	result;

    result = super.remove(o);

    ((FlowSetup) o).setOwner(null);
    ((FlowSetup) o).removeFlowSetupStateChangeListener(this);

    if (result)
      modified();

    return result;
  }

  /**
   * Removes all setups.
   */
  @Override
  public synchronized void removeAllElements() {
    int		i;
    boolean	modified;

    modified =(size() > 0);

    for (i = 0; i < size(); i++) {
      get(i).setOwner(null);
      get(i).removeFlowSetupStateChangeListener(this);
    }

    super.removeAllElements();

    if (modified)
      modified();
  }

  /**
   * Removes the specified setup.
   *
   * @param obj		the setup to remove
   * @return		true if removed successfully
   */
  @Override
  public synchronized boolean removeElement(Object obj) {
    boolean	result;

    result = super.removeElement(obj);

    ((FlowSetup) obj).setOwner(null);
    ((FlowSetup) obj).removeFlowSetupStateChangeListener(this);

    if (result)
      modified();

    return result;
  }

  /**
   * Removes the setup at the specified location.
   *
   * @param index	the position of the setup to remove
   */
  @Override
  public synchronized void removeElementAt(int index) {
    get(index).setOwner(null);
    get(index).removeFlowSetupStateChangeListener(this);
    super.removeElementAt(index);
    modified();
  }

  /**
   * Removes a range of setups.
   *
   * @param fromIndex	starting index (incl)
   * @param toIndex	ending index (excl)
   */
  @Override
  protected synchronized void removeRange(int fromIndex, int toIndex) {
    int		i;

    for (i = fromIndex; i < toIndex; i++) {
      get(i).setOwner(null);
      get(i).removeFlowSetupStateChangeListener(this);
    }

    super.removeRange(fromIndex, toIndex);

    modified();
  }

  /**
   * Retains only setups that are in the specified collection.
   *
   * @param c		the collection of setups to retain
   * @return		true if collection changed
   */
  @Override
  public synchronized boolean retainAll(Collection<?> c) {
    boolean		result;
    int			i;
    FlowSetup		setup;

    for (i = 0; i < size(); i++) {
      setup = get(i);
      if (!c.contains(setup)) {
	setup.setOwner(null);
	setup.removeFlowSetupStateChangeListener(this);
      }
    }

    result = super.retainAll(c);

    if (result)
      modified();

    return result;
  }

  /**
   * Sets the setup at the specified location.
   *
   * @param index	the position to set the setup
   * @param element	the setup to set
   * @return		the element previously at this position
   */
  @Override
  public synchronized FlowSetup set(int index, FlowSetup element) {
    FlowSetup	result;

    get(index).setOwner(null);
    get(index).removeFlowSetupStateChangeListener(this);

    element.setOwner(this);
    element.addFlowSetupStateChangeListener(this);

    result = super.set(index, element);
    modified();

    return result;
  }

  /**
   * Sets the setup at the specified location.
   *
   * @param obj		the setup to set
   * @param index	the position to place the setup
   */
  @Override
  public synchronized void setElementAt(FlowSetup obj, int index) {
    get(index).setOwner(null);
    get(index).removeFlowSetupStateChangeListener(this);

    obj.setOwner(this);
    obj.addFlowSetupStateChangeListener(this);

    super.setElementAt(obj, index);
    modified();
  }

  /**
   * Resizes the collection: if new size is larger, then null objects are added,
   * otherwise the setups at the end discarded.
   *
   * @param newSize	the new size for the collection
   */
  @Override
  public synchronized void setSize(int newSize) {
    int		i;
    boolean	modified;

    modified = (size() != newSize);

    for (i = size(); i < newSize; i++) {
      get(i).setOwner(null);
      get(i).removeFlowSetupStateChangeListener(this);
    }

    super.setSize(newSize);

    if (modified)
      modified();
  }

  /**
   * Writes the setups to the given file.
   *
   * @param filename	the file to write to
   * @return		true if successfully written
   */
  public boolean write(String filename) {
    boolean		result;
    Properties		props;
    int			i;

    props = new Properties();

    // setups
    props.setInteger(COUNT, size());
    for (i = 0; i < size(); i++)
      props.setProperty(SETUP_PREFIX + i, get(i).toCommandLine());

    result = props.save(filename);

    if (result)
      modified(false);

    return result;
  }

  /**
   * Reads and returns the setups from the given file.
   *
   * @param filename	the file to read from
   * @return		true if successfully read
   */
  public boolean read(String filename) {
    boolean		result;
    Vector<FlowSetup>	setups;
    FlowSetup		setup;
    Properties		props;
    int			count;
    int			i;

    result = false;

    clear();

    props = new Properties();
    props.load(filename);

    // setups
    if (props.hasKey(COUNT)) {
      count  = props.getInteger(COUNT);
      setups = new Vector<FlowSetup>();
      for (i = 0; i < count; i++) {
	try {
	  setup = (FlowSetup) OptionUtils.forAnyCommandLine(FlowSetup.class, props.getProperty(SETUP_PREFIX + i));
	  setups.add(setup);
	}
	catch (Exception e) {
	  System.err.println("Error loading setup #" + i + " (0-based index) from '" + filename + "':");
	  e.printStackTrace();
	  setups = null;
	  break;
	}
      }
      if (setups != null) {
	result = true;
	clear();
	addAll(setups);
	m_Modified = false;
      }
    }

    return result;
  }

  /**
   * Returns the index of the flowsetup with the given name.
   *
   * @param name	the name to look for
   * @return		the index, -1 if not found
   */
  public synchronized int indexOf(String name) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < size(); i++) {
      if (get(i).getName().equals(name)) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Gets called when the state of the flow setup changed.
   *
   * @param e		the event
   */
  public void flowSetupStateChanged(FlowSetupStateEvent e) {
    FlowSetup	setup;
    FlowSetup	setupNext;
    int		index;

    // notify listeners about change
    notifyChangeListeners(new ChangeEvent(this));

    setup = e.getFlowSetup();
    index = -1;
    if ((e.getType() == Type.ERROR) && setup.hasLastError() && setup.hasOnError())
      index = indexOf(setup.getOnError());  // TODO: pass on error message?
    else if ((e.getType() == Type.FINISHED) && setup.hasOnFinish())
      index = indexOf(setup.getOnFinish());

    if (index > -1) {
      setup.cleanUp();
      setupNext = get(index);
      setupNext.execute();
    }
  }

  /**
   * Adds the listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addChangeListener(ChangeListener l) {
    m_ChangeListeners.add(l);
  }

  /**
   * Removes the listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeChangeListener(ChangeListener l) {
    m_ChangeListeners.remove(l);
  }

  /**
   * Sends the event to all change listeners.
   *
   * @param e		the event to send
   */
  protected void notifyChangeListeners(ChangeEvent e) {
    Iterator<ChangeListener>	iter;

    iter = m_ChangeListeners.iterator();
    while (iter.hasNext())
      iter.next().stateChanged(e);
  }
}

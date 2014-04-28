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
 * BaseList.java
 * Copyright (C) 2009-2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Vector;

import javax.swing.JList;
import javax.swing.ListModel;

import adams.gui.event.RemoveItemsEvent;
import adams.gui.event.RemoveItemsListener;

/**
 * Enhanced javax.swing.JList. Incorporates functionality from the
 * JListHelper class.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see JListHelper
 */
public class BaseList
  extends JList {

  /** for serialization. */
  private static final long serialVersionUID = -3859605644790923061L;

  /** the listeners for items to be removed. */
  protected HashSet<RemoveItemsListener> m_RemoveItemsListeners;

  /**
   * Constructs a <code>BaseList</code> with an empty, read-only, model.
   */
  public BaseList() {
    super();
    initGUI();
  }

  /**
   * Constructs a <code>BaseList</code> that displays the elements in
   * the specified array. This constructor creates a read-only model
   * for the given array, and then delegates to the constructor that
   * takes a {@code ListModel}.
   * <p>
   * Attempts to pass a {@code null} value to this method results in
   * undefined behavior and, most likely, exceptions. The created model
   * references the given array directly. Attempts to modify the array
   * after constructing the list results in undefined behavior.
   *
   * @param  listData  the array of Objects to be loaded into the data model,
   *                   {@code non-null}
   */
  public BaseList(final Object[] listData) {
    super(listData);
    initGUI();
  }

  /**
   * Constructs a <code>BaseList</code> that displays the elements in
   * the specified <code>Vector</code>. This constructor creates a read-only
   * model for the given {@code Vector}, and then delegates to the constructor
   * that takes a {@code ListModel}.
   * <p>
   * Attempts to pass a {@code null} value to this method results in
   * undefined behavior and, most likely, exceptions. The created model
   * references the given {@code Vector} directly. Attempts to modify the
   * {@code Vector} after constructing the list results in undefined behavior.
   *
   * @param  listData  the <code>Vector</code> to be loaded into the
   *		         data model, {@code non-null}
   */
  public BaseList(final Vector<?> listData) {
    super(listData);
    initGUI();
  }

  /**
   * Constructs a {@code BaseList} that displays elements from the specified,
   * {@code non-null}, model. All {@code BaseList} constructors delegate to
   * this one.
   * <p>
   * This constructor registers the list with the {@code ToolTipManager},
   * allowing for tooltips to be provided by the cell renderers.
   *
   * @param dataModel the model for the list
   * @throws IllegalArgumentException if the model is {@code null}
   */
  public BaseList(ListModel dataModel) {
    super(dataModel);
    setModel(getModel());  // super class bypasses setModel(ListModel) method!
    initGUI();
  }

  /**
   * Initializes the members.
   */
  protected void initGUI() {
    m_RemoveItemsListeners = new HashSet<RemoveItemsListener>();

    addKeyListener(new KeyListener() {
      public void keyTyped(KeyEvent e) {
	// ignored
      }
      public void keyReleased(KeyEvent e) {
	// ignored
      }
      public void keyPressed(KeyEvent e) {
	if (m_RemoveItemsListeners.size() > 0) {
	  if ((e.getKeyCode() == KeyEvent.VK_DELETE) && (e.getModifiers() == 0)) {
	    e.consume();
	    notifyRemoveItemsListeners(getSelectedIndices());
	  }
	}
      }
    });
  }

  /**
   * moves the selected items up by 1.
   */
  public void moveUp() {
    JListHelper.moveUp(this);
  }

  /**
   * moves the selected item down by 1.
   */
  public void moveDown() {
    JListHelper.moveDown(this);
  }

  /**
   * moves the selected items to the top.
   */
  public void moveTop() {
    JListHelper.moveTop(this);
  }

  /**
   * moves the selected items to the end.
   */
  public void moveBottom() {
    JListHelper.moveBottom(this);
  }

  /**
   * checks whether the selected items can be moved up.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveUp() {
    return JListHelper.canMoveUp(this);
  }

  /**
   * checks whether the selected items can be moved down.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveDown() {
    return JListHelper.canMoveDown(this);
  }

  /**
   * Adds the remove items listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addRemoveItemsListener(RemoveItemsListener l) {
    m_RemoveItemsListeners.add(l);
  }

  /**
   * Removes the remove items listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeRemoveItemsListener(RemoveItemsListener l) {
    m_RemoveItemsListeners.remove(l);
  }

  /**
   * Notifies the remove items listeners about the indices that are to be
   * removed.
   *
   * @param indices	the indices that should get removed
   */
  protected void notifyRemoveItemsListeners(int[] indices) {
    RemoveItemsEvent	event;

    event = new RemoveItemsEvent(this, indices);
    for (RemoveItemsListener l: m_RemoveItemsListeners)
      l.removeItems(event);
  }
}

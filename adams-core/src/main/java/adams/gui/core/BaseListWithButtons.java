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
 * BaseListWithButtons.java
 * Copyright (C) 2009-2017 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.core;

import adams.gui.event.RemoveItemsListener;

import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.MouseEvent;

/**
 * Graphical component that consists of a BaseTable with buttons on the
 * right-hand side.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseListWithButtons
  extends AbstractDoubleClickableComponentWithButtons<BaseList> {

  /** for serialization. */
  private static final long serialVersionUID = 1935542795448084154L;

  /** the model listener for updating the counts. */
  protected ListDataListener m_CountModelListener;

  /**
   * The default constructor.
   */
  public BaseListWithButtons() {
    super();
  }

  /**
   * Initializes the list with the specified model.
   *
   * @param model	the model to use
   */
  public BaseListWithButtons(ListModel model) {
    super();

    m_Component.setModel(model);
    updateCountsModelListener(model);
  }

  /**
   * Returns whether the component requires a JScrollPane around it.
   *
   * @return		true if the component requires a JScrollPane
   */
  public boolean requiresScrollPane() {
    return true;
  }

  /**
   * Creates the component to use in the panel. If a
   *
   * @return		the component
   */
  protected BaseList createComponent() {
    BaseList	result;

    result = new BaseList();
    result.addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateCounts();
      }
    });

    return result;
  }

  /**
   * Checks whether the double click is valid for this component.
   *
   * @param e		the mouse event of the double click
   * @return		true if valid double click
   */
  protected boolean isValidDoubleClick(MouseEvent e) {
    // unfortunately, locationToIndex only returns the nearest entry
    // and not the exact one, i.e. if there's one item in the list and
    // one doublelclicks somewhere in the list, this index will be
    // returned
    int index = m_Component.locationToIndex(e.getPoint());
    return (index > -1);
  }

  /**
   * Sets the model that represents the contents or "value" of the
   * list, notifies property change listeners, and then clears the
   * list's selection.
   * <p>
   * This is a JavaBeans bound property.
   *
   * @param model  the <code>ListModel</code> that provides the
   *						list of items for display
   */
  public void setModel(ListModel model) {
    updateCountsModelListener(model);
    m_Component.setModel(model);
    updateCounts();
  }

  /**
   * Returns the underlying list model.
   *
   * @return		the underlying list model
   */
  public ListModel getModel() {
    return m_Component.getModel();
  }

  /**
   * Adds a listener to the list, to be notified each time a change to the
   * selection occurs; the preferred way of listening for selection state
   * changes. {@code JList} takes care of listening for selection state
   * changes in the selection model, and notifies the given listener of
   * each change. {@code ListSelectionEvent}s sent to the listener have a
   * {@code source} property set to this list.
   *
   * @param listener the {@code ListSelectionListener} to add
   */
  public void addListSelectionListener(ListSelectionListener listener) {
    m_Component.addListSelectionListener(listener);
  }

  /**
   * Removes a selection listener from the list.
   *
   * @param listener the {@code ListSelectionListener} to remove
   */
  public void removeListSelectionListener(ListSelectionListener listener) {
    m_Component.removeListSelectionListener(listener);
  }

  /**
   * Returns an array of all of the selected indices, in increasing
   * order.
   *
   * @return all of the selected indices, in increasing order,
   *         or an empty array if nothing is selected
   * @see #removeSelectionInterval
   * @see #addListSelectionListener
   */
  public int[] getSelectedIndices() {
    return m_Component.getSelectedIndices();
  }

  /**
   * Selects a single cell. Does nothing if the given index is greater
   * than or equal to the model size. This is a convenience method that uses
   * {@code setSelectionInterval} on the selection model. Refer to the
   * documentation for the selection model class being used for details on
   * how values less than {@code 0} are handled.
   *
   * @param index the index of the cell to select
   * @see ListSelectionModel#setSelectionInterval
   * @see #isSelectedIndex
   * @see #addListSelectionListener
   * @beaninfo
   * description: The index of the selected cell.
   */
  public void setSelectedIndex(int index) {
    m_Component.setSelectedIndex(index);
  }

  /**
   * Changes the selection to be the set of indices specified by the given
   * array. Indices greater than or equal to the model size are ignored.
   * This is a convenience method that clears the selection and then uses
   * {@code addSelectionInterval} on the selection model to add the indices.
   * Refer to the documentation of the selection model class being used for
   * details on how values less than {@code 0} are handled.
   *
   * @param indices an array of the indices of the cells to select,
   *                {@code non-null}
   * @see ListSelectionModel#addSelectionInterval
   * @see #isSelectedIndex
   * @see #addListSelectionListener
   * @throws NullPointerException if the given array is {@code null}
   */
  public void setSelectedIndices(int[] indices) {
    m_Component.setSelectedIndices(indices);
  }

  /**
   * Returns an array of all the selected values, in increasing order based
   * on their indices in the list.
   *
   * @return the selected values, or an empty array if nothing is selected
   * @see #isSelectedIndex
   * @see #getModel
   * @see #addListSelectionListener
   */
  public Object[] getSelectedValues() {
    return m_Component.getSelectedValues();
  }

  /**
   * Returns the smallest selected cell index; <i>the selection</i> when only
   * a single item is selected in the list. When multiple items are selected,
   * it is simply the smallest selected index. Returns {@code -1} if there is
   * no selection.
   * <p>
   * This method is a cover that delegates to {@code getMinSelectionIndex}.
   *
   * @return the smallest selected cell index
   */
  public int getSelectedIndex() {
    return m_Component.getSelectedIndex();
  }

  /**
   * Returns the value for the smallest selected cell index;
   * <i>the selected value</i> when only a single item is selected in the
   * list. When multiple items are selected, it is simply the value for the
   * smallest selected index. Returns {@code null} if there is no selection.
   * <p>
   * This is a convenience method that simply returns the model value for
   * {@code getMinSelectionIndex}.
   *
   * @return the first selected value
   */
  public Object getSelectedValue() {
    return m_Component.getSelectedValue();
  }

  /**
   * Selects the specified object from the list.
   *
   * @param anObject      the object to select
   * @param shouldScroll  {@code true} if the list should scroll to display
   *                      the selected object, if one exists; otherwise {@code false}
   */
  public void setSelectedValue(Object anObject, boolean shouldScroll) {
    m_Component.setSelectedValue(anObject, shouldScroll);
  }

  /**
   * moves the selected items up by 1.
   */
  public void moveUp() {
    m_Component.moveUp();
  }

  /**
   * moves the selected item down by 1.
   */
  public void moveDown() {
    m_Component.moveDown();
  }

  /**
   * moves the selected items to the top.
   */
  public void moveTop() {
    m_Component.moveTop();
  }

  /**
   * moves the selected items to the end.
   */
  public void moveBottom() {
    m_Component.moveBottom();
  }

  /**
   * checks whether the selected items can be moved up.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveUp() {
    return m_Component.canMoveUp();
  }

  /**
   * checks whether the selected items can be moved down.
   *
   * @return		true if the selected items can be moved
   */
  public boolean canMoveDown() {
    return m_Component.canMoveDown();
  }

  /**
   * Sets the selection mode for the list. This is a cover method that sets
   * the selection mode directly on the selection model.
   * <p>
   * The following list describes the accepted selection modes:
   * <ul>
   * <li>{@code ListSelectionModel.SINGLE_SELECTION} -
   *   Only one list index can be selected at a time. In this mode,
   *   {@code setSelectionInterval} and {@code addSelectionInterval} are
   *   equivalent, both replacing the current selection with the index
   *   represented by the second argument (the "lead").
   * <li>{@code ListSelectionModel.SINGLE_INTERVAL_SELECTION} -
   *   Only one contiguous interval can be selected at a time.
   *   In this mode, {@code addSelectionInterval} behaves like
   *   {@code setSelectionInterval} (replacing the current selection},
   *   unless the given interval is immediately adjacent to or overlaps
   *   the existing selection, and can be used to grow the selection.
   * <li>{@code ListSelectionModel.MULTIPLE_INTERVAL_SELECTION} -
   *   In this mode, there's no restriction on what can be selected.
   *   This mode is the default.
   * </ul>
   *
   * @param selectionMode the selection mode
   * @see #getSelectionMode
   */
  public void setSelectionMode(int selectionMode) {
    m_Component.setSelectionMode(selectionMode);
  }

  /**
   * Returns the current selection mode for the list. This is a cover
   * method that delegates to the method of the same name on the
   * list's selection model.
   *
   * @return the current selection mode
   * @see #setSelectionMode
   */
  public int getSelectionMode() {
    return m_Component.getSelectionMode();
  }

  /**
   * Returns the current selection model. The selection model maintains the
   * selection state of the list. See the class level documentation for more
   * details.
   *
   * @return the <code>ListSelectionModel</code> that maintains the
   *         list's selections
   *
   * @see #setSelectionModel
   * @see ListSelectionModel
   */
  public ListSelectionModel getSelectionModel() {
    return m_Component.getSelectionModel();
  }

  /**
   * Sets the <code>selectionModel</code> for the list to a
   * non-<code>null</code> <code>ListSelectionModel</code>
   * implementation. The selection model handles the task of making single
   * selections, selections of contiguous ranges, and non-contiguous
   * selections.
   * <p>
   * This is a JavaBeans bound property.
   *
   * @param selectionModel  the <code>ListSelectionModel</code> that
   *				implements the selections
   */
  public void setSelectionModel(ListSelectionModel selectionModel) {
    m_Component.setSelectionModel(selectionModel);
  }

  /**
   * Adds the remove items listener to its internal list.
   *
   * @param l		the listener to add
   */
  public void addRemoveItemsListener(RemoveItemsListener l) {
    m_Component.addRemoveItemsListener(l);
  }

  /**
   * Removes the remove items listener from its internal list.
   *
   * @param l		the listener to remove
   */
  public void removeRemoveItemsListener(RemoveItemsListener l) {
    m_Component.removeRemoveItemsListener(l);
  }

  /**
   * Whether to display the information JLabel or not.
   *
   * @param value	if true then the information is being displayed
   */
  public void setInfoVisible(boolean value) {
    super.setInfoVisible(value);
    if (value)
      updateCounts();
  }

  /**
   * Updates the table model's listener for updating the counts.
   *
   * @param dataModel	the model to update
   */
  protected void updateCountsModelListener(ListModel dataModel) {
    if (m_CountModelListener != null)
      getModel().removeListDataListener(m_CountModelListener);

    m_CountModelListener = new ListDataListener() {
      public void intervalRemoved(ListDataEvent e) {
	updateCounts();
      }
      public void intervalAdded(ListDataEvent e) {
	updateCounts();
      }
      public void contentsChanged(ListDataEvent e) {
	updateCounts();
      }
    };
    dataModel.addListDataListener(m_CountModelListener);
  }

  /**
   * Updates the information about the counts.
   */
  protected void updateCounts() {
    updateInfo(
	"Total: " + m_Component.getModel().getSize()
	+ ", Selected: " + m_Component.getSelectedIndices().length);
  }
}

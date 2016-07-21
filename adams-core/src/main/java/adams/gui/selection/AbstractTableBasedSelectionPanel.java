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
 * AbstractTableBasedSelectionPanel.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.selection;

import adams.core.Utils;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTable;
import adams.gui.core.CustomSearchTableModel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MouseUtils;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.event.DoubleClickEvent;
import adams.gui.event.DoubleClickListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract ancestor for selection panels that use a JTable for displaying
 * the options.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of items to display
 */
public abstract class AbstractTableBasedSelectionPanel<T>
  extends AbstractSelectionPanel {

  /** for serialization. */
  private static final long serialVersionUID = -2196072150751857296L;

  /**
   * A simple table model for displaying the data.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   * @param <T> the type of items to display
   */
  public static abstract class AbstractSelectionTableModel<T>
    extends AbstractBaseTableModel
    implements CustomSearchTableModel {

    /** for serialization. */
    private static final long serialVersionUID = -6034857465096265433L;

    /**
     * Returns the item at the specified position.
     *
     * @param row	the (actual, not visible) position of the item
     * @return		the item at the position, null if not valid index
     */
    public abstract T getItemAt(int row);

    /**
     * Returns the index of the given (visible) item, -1 if not found.
     *
     * @param item	the item to look for
     * @return		the index, -1 if not found
     */
    public abstract int indexOf(T item);
  }

  /**
   * Interface for classes that supply a popup menu for the selection table.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static interface SelectionPopupMenuSupplier {

    /**
     * Returns a popup menu for the table of the selection table.
     *
     * @param table	the affected table
     * @param row	the row the mouse is currently over
     * @return		the popup menu
     */
    public BasePopupMenu getSelectionPopupMenu(SortableAndSearchableTable table, int row);
  }

  /** the panel itself. */
  protected AbstractTableBasedSelectionPanel<T> m_Self;

  /** the table for displaying the items. */
  protected SortableAndSearchableTable m_TableData;

  /** the underlying table model. */
  protected AbstractSelectionTableModel<T> m_TableDataModel;

  /** the panel that encompasses all widgets. */
  protected JPanel m_PanelAll;

  /** the search panel. */
  protected SearchPanel m_SearchPanel;

  /** the panel for additional components. */
  protected JPanel m_PanelAdditional;

  /** the panel for displaying total and selected count. */
  protected JPanel m_PanelCounts;

  /** the label for displaying the total and selected counts. */
  protected JLabel m_LabelCounts;

  /** the selected items. */
  protected T[] m_Current;

  /** the double-click listeners. */
  protected HashSet<DoubleClickListener> m_DoubleClickListeners;

  /** the popup menu supplier. */
  protected SelectionPopupMenuSupplier m_PopupMenuSupplier;

  /**
   * For initializing members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Self                 = this;
    m_Current              = (T[]) Array.newInstance(getItemClass(), 0);
    m_DoubleClickListeners = new HashSet<DoubleClickListener>();
    m_PopupMenuSupplier    = null;
  }

  /**
   * initializes the GUI elements.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panel2;
    JPanel	panel3;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelAll = new JPanel(new BorderLayout());
    add(m_PanelAll, BorderLayout.CENTER);

    m_PanelCounts = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelCounts.setVisible(false);
    m_LabelCounts = new JLabel(" ");
    m_PanelCounts.add(m_LabelCounts);

    panel = new JPanel(new BorderLayout());
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_PanelAll.add(panel, BorderLayout.CENTER);

    m_TableDataModel = newTableModel();
    m_TableData      = new SortableAndSearchableTable(m_TableDataModel);
    m_TableDataModel.addTableModelListener(m_TableData);
    m_TableData.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_TableData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    m_TableData.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateCounts();
      }
    });
    m_TableData.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (m_TableData.getSelectedRow() != -1) {
	  if (MouseUtils.isDoubleClick(e)) {
	    e.consume();
	    notifyDoubleClickListeners(new DoubleClickEvent(m_Self));
	  }
	  else if (MouseUtils.isRightClick(e)) {
	    e.consume();
	    int row = m_TableData.rowAtPoint(new Point(e.getX(), e.getY()));
	    BasePopupMenu menu = m_PopupMenuSupplier.getSelectionPopupMenu(m_TableData, row);
	    if (menu != null)
	      menu.showAbsolute(m_TableData, e);
	  }
	}
	if (!e.isConsumed())
	  super.mouseClicked(e);
      }
    });
    panel.add(new BaseScrollPane(m_TableData), BorderLayout.CENTER);

    m_SearchPanel = newSearchPanel();
    if (m_SearchPanel.getLayoutType() == LayoutType.VERTICAL) {
      panel2 = new JPanel(new BorderLayout());
      panel.add(panel2, BorderLayout.EAST);
      panel3 = new JPanel(new BorderLayout());
      panel2.add(panel3, BorderLayout.NORTH);
      panel3.add(m_SearchPanel, BorderLayout.WEST);

      panel3 = new JPanel(new BorderLayout());
      panel2.add(panel3, BorderLayout.CENTER);
      m_PanelAdditional = new JPanel(new GridLayout(0, 1));
      panel3.add(m_PanelAdditional, BorderLayout.NORTH);
    }
    else {
      panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
      panel.add(panel2, BorderLayout.NORTH);
      panel2.add(m_SearchPanel);
      m_PanelAdditional = new JPanel(new GridLayout(1, 0));
      panel2.add(m_PanelAdditional);
    }
  }

  /**
   * Creates a new search panel widget.
   *
   * @return		the search panel
   */
  protected SearchPanel newSearchPanel() {
    SearchPanel	result;

    result = new SearchPanel(LayoutType.VERTICAL, true);
    result.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	search();
      }
    });

    return result;
  }

  /**
   * Returns an empty table model.
   *
   * @return		the model
   */
  protected abstract AbstractSelectionTableModel<T> newTableModel();

  /**
   * Returns the class of the items displayed, same as "T".
   *
   * @return		the class of the items
   */
  protected abstract Class getItemClass();

  /**
   * Sets whether multiple or single selection is used.
   *
   * @param value	if true multiple items can be selected
   */
  public void setMultipleSelection(boolean value) {
    if (value)
      m_TableData.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    else
      m_TableData.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
  }

  /**
   * Returns whether multiple or single selection is active.
   *
   * @return		true if multiple selection is active
   */
  public boolean isMultipleSelection() {
    return (m_TableData.getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
  }

  /**
   * Returns the number of selected rows.
   *
   * @return		the number of selected rows
   */
  public int getSelectedRowCount() {
    return m_TableData.getSelectedRowCount();
  }

  /**
   * Performs a search.
   */
  protected void search() {
    Runnable	run;

    run = new Runnable() {
      public void run() {
	m_SearchPanel.setEnabled(false);
	m_TableData.search(
	    m_SearchPanel.getSearchText(), m_SearchPanel.isRegularExpression());
	m_SearchPanel.setEnabled(true);
	m_SearchPanel.grabFocus();
      }
    };
    SwingUtilities.invokeLater(run);
  }

  /**
   * Adds the given listener to the table's list of ListSelectionListeners.
   *
   * @param l		the listener to add
   */
  public void addListSelectionListener(ListSelectionListener l) {
    m_TableData.getSelectionModel().addListSelectionListener(l);
  }

  /**
   * Removes the given listener from the table's list of ListSelectionListeners.
   *
   * @param l		the listener to remove
   */
  public void removeListSelectionListener(ListSelectionListener l) {
    m_TableData.getSelectionModel().removeListSelectionListener(l);
  }

  /**
   * Adds the given listener to the list of double-click listeners.
   *
   * @param l		the listener to add
   */
  public void addDoubleClickListener(DoubleClickListener l) {
    m_DoubleClickListeners.add(l);
  }

  /**
   * Removes the given listener from the list of double-click listeners.
   *
   * @param l		the listener to remove
   */
  public void removeDoubleClickListener(DoubleClickListener l) {
    m_DoubleClickListeners.remove(l);
  }

  /**
   * Sends the event to all double-click listeners.
   *
   * @param e		the event to send
   */
  protected void notifyDoubleClickListeners(DoubleClickEvent e) {
    Iterator<DoubleClickListener>	iter;

    iter = m_DoubleClickListeners.iterator();
    while (iter.hasNext())
      iter.next().doubleClickOccurred(e);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_DoubleClickListeners.clear();
  }

  /**
   * Sets the enabled state of the panel.
   *
   * @param value	if true then the components will be enabled
   */
  @Override
  public void setEnabled(boolean value) {
    super.setEnabled(value);

    m_SearchPanel.setEnabled(value);
    m_TableData.setEnabled(value);
  }

  /**
   * The text field for the search tries to grab the focus.
   */
  @Override
  public void grabFocus() {
    m_SearchPanel.grabFocus();
  }

  /**
   * Returns the count of currently displayed items.
   *
   * @return		the total count
   */
  public int getItemCount() {
    return m_TableData.getRowCount();
  }

  /**
   * Returns the count of currently selected items.
   *
   * @return		the selection count
   */
  public int getSelectedItemCount() {
    return m_TableData.getSelectedRowCount();
  }

  /**
   * Returns the currently selected items, null if none chosen or dialog
   * canceled.
   *
   * @return		the selected items
   */
  protected T[] getCurrentItems() {
    m_Current = (T[]) Array.newInstance(getItemClass(), m_TableData.getSelectedRowCount());
    int[] rows = m_TableData.getSelectedRows();
    for (int i = 0; i < rows.length; i++)
      m_Current[i] = m_TableDataModel.getItemAt(
	  m_TableData.getActualRow(rows[i]));

    return m_Current.clone();
  }

  /**
   * Returns the item, null if none chosen or dialog canceled.
   *
   * @return		the selected item
   */
  public T getItem() {
    if (getItems().length == 0)
      return null;
    else
      return getItems()[0];
  }

  /**
   * Returns the selected items, null if none chosen or dialog
   * canceled.
   *
   * @return		the selected items
   */
  public T[] getItems() {
    return getCurrentItems();
  }

  /**
   * Sets the initially selected item.
   *
   * @param value	the item to select
   */
  public void setItem(T value) {
    T[]		items;

    if (value != null) {
      items = (T[]) Array.newInstance(getItemClass(), 1);
      Array.set(items, 0, value);
    }
    else {
      items = (T[]) Array.newInstance(getItemClass(), 0);
    }

    setItems(items);
  }

  /**
   * Hook method for processing items that were not found when trying to
   * select them initially.
   * <br><br>
   * The default implementation merely outputs the items.
   *
   * @param missing	the missing items
   */
  protected void processMissing(List<T> missing) {
    System.err.println("Missing items: " + Utils.arrayToString(missing.toArray()));
  }

  /**
   * Checks whether the item is valid.
   * <br><br>
   * Default implementation only checks for null.
   *
   * @param item	the item to check
   * @return		true if valid
   */
  protected boolean isValidItem(T item) {
    return (item != null);
  }

  /**
   * Returns whether to add the item really to the missing list.
   * <br><br>
   * Default implementation returns always true.
   * 
   * @param item	the item to check
   * @return		true if to add the item to the missing list, false otherwise
   */
  protected boolean addToMissing(T item) {
    return true;
  }
  
  /**
   * Selects the items.
   *
   * @param value	the items to select
   * @return		the actually selected items
   */
  protected List<T> select(T[] value) {
    List<T>	result;
    int		index;
    int		i;
    int		row;
    List<T>	missing;

    result  = new ArrayList<T>();
    missing = new ArrayList<T>();

    m_TableData.getSelectionModel().clearSelection();

    if (value != null) {
      row = m_TableData.getRowCount();
      for (i = 0; i < value.length; i++) {
	if (!isValidItem(value[i]))
	  continue;
	index = m_TableDataModel.indexOf(value[i]);
	if (index == -1) {
	  if (addToMissing(value[i]))
	    missing.add(value[i]);
	}
	else {
	  result.add(m_TableDataModel.getItemAt(index));
	  m_TableData.getSelectionModel().addSelectionInterval(
	      m_TableData.getDisplayRow(index),
	      m_TableData.getDisplayRow(index));
	  if (m_TableData.getDisplayRow(index) < row)
	    row = m_TableData.getDisplayRow(index);
	}
      }
    }

    if (missing.size() > 0)
      processMissing(missing);

    return result;
  }

  /**
   * Sets the initially selected items.
   *
   * @param value	the items to select
   */
  public void setItems(T[] value) {
    List<T>	items;
    int		i;

    // clear search
    m_SearchPanel.setSearchText("");
    m_SearchPanel.search();

    items = select(value);

    m_Current = (T[]) Array.newInstance(getItemClass(), items.size());
    for (i = 0; i < items.size(); i++)
      m_Current[i] = items.get(i);
  }

  /**
   * Makes sure that the first selected row is visible.
   */
  public void scrollIntoView() {
    int		row;

    row = m_TableData.getSelectedRow();
    if ((row != -1) && (row < m_TableData.getRowCount()))
      m_TableData.scrollRowToVisible(row);
  }

  /**
   * Hook method just before the dialog is made visible.
   */
  @Override
  protected void beforeShow() {
    super.beforeShow();
    m_SearchPanel.setEnabled(m_TableDataModel.getRowCount() > 0);
    updateCounts();
  }

  /**
   * Sets the popup menu supplier, if the user right-clicks on the table cells.
   *
   * @param value	the supplier, can be null to turn off menu
   */
  public void setPopupMenuSupplier(SelectionPopupMenuSupplier value) {
    m_PopupMenuSupplier = value;
  }

  /**
   * Returns the current popup menu supplier.
   *
   * @return		the supplier, can be null if turned off
   */
  public SelectionPopupMenuSupplier getPopupMenuSupplier() {
    return m_PopupMenuSupplier;
  }

  /**
   * Sets a default popup menu supplier. Simply allows to copy all the column
   * values to the clipboard.
   */
  public void setDefaultPopupMenuSupplier() {
    setPopupMenuSupplier(new SelectionPopupMenuSupplier() {
      public BasePopupMenu getSelectionPopupMenu(SortableAndSearchableTable table, int row) {
	BasePopupMenu result = new BasePopupMenu();
	for (int i = 0; i < m_TableDataModel.getColumnCount(); i++) {
	  JMenuItem menuitem = new JMenuItem("Copy '" + m_TableDataModel.getColumnName(i) + "'");
	  final Object obj = m_TableData.getValueAt(row, i);
	  menuitem.setEnabled(obj != null);
	  menuitem.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	      if (obj != null)
		ClipboardHelper.copyToClipboard(obj.toString());
	    }
	  });
	  result.add(menuitem);
	}

	return result;
      }
    });
  }

  /**
   * Sets whether the total/selected counts are displayed.
   *
   * @param value	if true then the total/selected counts are displayed
   */
  public void setCountsVisible(boolean value) {
    m_PanelCounts.setVisible(value);
    if (value) {
      m_PanelAll.add(m_PanelCounts, BorderLayout.SOUTH);
      updateCounts();
    }
    else {
      m_PanelAll.remove(m_PanelCounts);
    }
  }

  /**
   * Returns whether the total/selected counts are displayed.
   *
   * @return		true if the total/selected counts are displayed
   */
  public boolean isCountsVisible() {
    return m_PanelCounts.isVisible();
  }

  /**
   * Updates the total/selected counts.
   */
  protected void updateCounts() {
    m_LabelCounts.setText(
	"Total: " + m_TableData.getRowCount()
	+ ", Selected: " + m_TableData.getSelectedRowCount());
  }
}

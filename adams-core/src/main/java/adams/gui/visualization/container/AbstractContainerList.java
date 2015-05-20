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
 * AbstractContainerList.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import adams.core.CleanUpHandler;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.MouseUtils;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.event.DataChangeEvent;
import adams.gui.event.DataChangeListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

/**
 * An abstract panel that lists containers in a JTable.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <M> the type of container manager to use
 * @param <C> the type of container to use
 */
public class AbstractContainerList<M extends AbstractContainerManager, C extends AbstractContainer>
  extends BasePanel
  implements CleanUpHandler, TableModelListener, ListSelectionListener, DataChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = -3486748595304948144L;

  /** the table for displaying the containers. */
  protected ContainerTable<M,C> m_Table;

  /** the popup menu supplier. */
  protected ContainerListPopupMenuSupplier<M,C> m_PopupMenuSupplier;

  /** the title label. */
  protected JLabel m_LabelTitle;
  
  /** the title string. */
  protected String m_Title;

  /** the table model listeners to manage. */
  protected HashSet<TableModelListener> m_TableModelListeners;

  /** the list selection listeners to manage. */
  protected HashSet<ListSelectionListener> m_ListSelectionListeners;

  /** the panel for searching the entry names. */
  protected SearchPanel m_PanelSearch;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_PopupMenuSupplier      = null;
    m_TableModelListeners    = new HashSet<TableModelListener>();
    m_ListSelectionListeners = new HashSet<ListSelectionListener>();
  }

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_LabelTitle = new JLabel();

    m_Table = createTable();
    m_Table.getModel().addTableModelListener(this);
    m_Table.addMouseListener(new MouseAdapter() {
      @Override
      public void mouseClicked(MouseEvent e) {
	if (m_PopupMenuSupplier != null) {
	  if (MouseUtils.isRightClick(e)) {
	    e.consume();
	    int row = m_Table.rowAtPoint(new Point(e.getX(), e.getY()));
	    JPopupMenu menu = m_PopupMenuSupplier.getContainerListPopupMenu(m_Table, row);
	    if (menu != null)
	      menu.show(m_Table, e.getX(), e.getY());
	  }
	}

	if (!e.isConsumed())
	  super.mouseClicked(e);
      }
    });
    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
    
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false, null, true, "");
    m_PanelSearch.setVisible(getManager() instanceof SearchableContainerManager);
    m_PanelSearch.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	if (!getAllowSearch())
	  return;
	SearchableContainerManager smanager = (SearchableContainerManager) getManager();
	if (e.getParameters().getSearchString() == null)
	  smanager.clearSearch();
	else
	  smanager.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Creates a new table.
   *
   * @return		the new table
   */
  protected ContainerTable<M,C> createTable() {
    return new ContainerTable<M,C>();
  }

  /**
   * Creates a new model.
   *
   * @param manager	the manager to use for the model
   * @return		the new model
   */
  protected ContainerModel<M,C> createModel(M manager) {
    return new ContainerModel<M,C>(manager);
  }

  /**
   * Configures the model.
   *
   * @param model	the model to configure
   */
  protected void configureModel(ContainerModel<M,C> model) {
    model.setDisplayDatabaseID(getDisplayDatabaseID());
    model.setDisplayVisibility(getDisplayVisibility());
  }

  /**
   * Creates a new model.
   *
   * @param manager	the manager to base the model on
   * @return		the model
   */
  protected ContainerModel<M,C> newModel(M manager) {
    ContainerModel<M,C>	result;

    result = createModel(manager);
    configureModel(result);

    return result;
  }

  /**
   * Returns the underlying container model.
   *
   * @return		the container model, null if of different type
   */
  protected ContainerModel<M,C> getContainerModel() {
    if (m_Table.getModel() instanceof ContainerModel)
      return (ContainerModel<M,C>) m_Table.getModel();
    else
      return null;
  }

  /**
   * Sets the manager with the containers to use for display.
   *
   * @param value	the manager
   */
  public void setManager(M value) {
    // detach listeners
    getContainerModel().removeTableModelListener(this);
    m_Table.getSelectionModel().removeListSelectionListener(this);
    if (getManager() != null)
      getManager().removeDataChangeListener(this);

    // change over model
    getContainerModel().cleanUp();
    m_Table.setModel(newModel(value));

    // re-attach listeners
    getContainerModel().addTableModelListener(this);
    m_Table.getSelectionModel().addListSelectionListener(this);
    getManager().addDataChangeListener(this);
  }

  /**
   * Returns the current manager, can be null.
   *
   * @return		the manager or null if not set
   */
  public M getManager() {
    return getContainerModel().getManager();
  }

  /**
   * Returns the table.
   *
   * @return		the table
   */
  public ContainerTable<M,C> getTable() {
    return m_Table;
  }

  /**
   * Sets the selection mode of the table.
   *
   * @param value	the mode
   * @see 		ListSelectionModel#setSelectionMode(int)
   */
  public void setSelectionMode(int value) {
    getTable().setSelectionMode(value);
  }

  /**
   * Returns the selection mode.
   *
   * @return		the mode
   * @see		ListSelectionModel#getSelectionMode()
   */
  public int getSelectionMode() {
    return getTable().getSelectionModel().getSelectionMode();
  }

  /**
   * Sets the display string generator.
   *
   * @param value	the new generator
   */
  public void setDisplayStringGenerator(AbstractContainerDisplayStringGenerator value) {
    getContainerModel().setDisplayStringGenerator(value);
  }

  /**
   * Returns the current display string generator.
   *
   * @return		the generator
   */
  public AbstractContainerDisplayStringGenerator getDisplayStringGenerator() {
    return getContainerModel().getDisplayStringGenerator();
  }

  /**
   * Sets the generator for the column names.
   *
   * @param value	the new generator
   */
  public void setColumnNameGenerator(AbstractContainerTableColumnNameGenerator value) {
    getContainerModel().setColumnNameGenerator(value);
  }

  /**
   * Returns the current generator for the column names.
   *
   * @return		the generator
   */
  public AbstractContainerTableColumnNameGenerator getColumnNameGenerator() {
    return getContainerModel().getColumnNameGenerator();
  }

  /**
   * Sets the popup menu supplier, if the user right-clicks on the table cells.
   *
   * @param value	the supplier, can be null to turn off menu
   */
  public void setPopupMenuSupplier(ContainerListPopupMenuSupplier<M,C> value) {
    m_PopupMenuSupplier = value;
  }

  /**
   * Returns the current popup menu supplier.
   *
   * @return		the supplier, can be null if turned off
   */
  public ContainerListPopupMenuSupplier<M,C> getPopupMenuSupplier() {
    return m_PopupMenuSupplier;
  }

  /**
   * Whether to display the visibility column (if possible).
   *
   * @param value	if true then the column will be displayed where possible
   */
  public void setDisplayVisibility(boolean value) {
    getContainerModel().setDisplayVisibility(value);
    getTable().invalidateColumnWidths();
  }

  /**
   * Returns whether the visibility column will be displayed where possible.
   *
   * @return		true if the column will be displayed where possible
   */
  public boolean getDisplayVisibility() {
    return getContainerModel().getDisplayVisibility();
  }

  /**
   * Whether to display the database ID column (if possible).
   *
   * @param value	if true then the column will be displayed where possible
   */
  public void setDisplayDatabaseID(boolean value) {
    getContainerModel().setDisplayDatabaseID(value);
    getTable().invalidateColumnWidths();
  }

  /**
   * Returns whether the database ID column will be displayed where possible.
   *
   * @return		true if the column will be displayed where possible
   */
  public boolean getDisplayDatabaseID() {
    return getContainerModel().getDisplayDatabaseID();
  }

  /**
   * Returns whether a title has been set.
   *
   * @return		true if a title was set
   */
  public boolean hasTitle() {
    return (m_Title.length() > 0);
  }

  /**
   * Sets the title of the container list.
   *
   * @param value	the title, use empty string or null to remove
   */
  public void setTitle(String value) {
    if (value == null)
      value = "";

    m_Title = value;
    if (m_Title.length() == 0)
      remove(m_LabelTitle);
    else
      add(m_LabelTitle, BorderLayout.NORTH);

    updateTitle();
  }

  /**
   * Updates the title, including the number of containers in the list.
   */
  protected void updateTitle() {
    String	title;
    M		manager;
    
    if (m_LabelTitle == null)
      return;
    
    manager = getManager();
    title   = m_Title;
    if (manager != null) {
      title += " (";
      if (manager instanceof VisibilityContainerManager)
	title += ((VisibilityContainerManager) manager).countVisible() + "/";
      title += manager.count();
      title += ")";
    }

    m_LabelTitle.setText(title);
  }
  
  /**
   * Returns the title.
   *
   * @return		the title, null if none set
   */
  public String getTitle() {
    String	result;

    result = null;
    if (hasTitle())
      result = m_Title;

    return result;
  }

  /**
   * Gets called when the underlying data model changes.
   * <br><br>
   * Default implementation notifies all the TableModelListener with the
   * even that this method receives.
   *
   * @param e		the event
   * @see		#m_TableModelListeners
   */
  public void tableChanged(TableModelEvent e) {
    for (TableModelListener l: m_TableModelListeners)
      l.tableChanged(e);
  }

  /**
   * Adds the given listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addTableModelListener(TableModelListener l) {
    m_TableModelListeners.add(l);
  }

  /**
   * Removes the given listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeTableModelListener(TableModelListener l) {
    m_TableModelListeners.remove(l);
  }

  /**
   * Called whenever the value of the selection changes.
   * <br><br>
   * Default implementation forwards the event to all managed list selection
   * listeners.
   *
   * @param e 		the event that characterizes the change.
   * @see		#m_ListSelectionListeners
   */
  public void valueChanged(ListSelectionEvent e) {
    for (ListSelectionListener l: m_ListSelectionListeners)
      l.valueChanged(e);
  }

  /**
   * Adds the given listener to the internal list.
   *
   * @param l		the listener to add
   */
  public void addListSelectionListener(ListSelectionListener l) {
    m_ListSelectionListeners.add(l);
  }

  /**
   * Removes the given listener from the internal list.
   *
   * @param l		the listener to remove
   */
  public void removeListSelectionListener(ListSelectionListener l) {
    m_ListSelectionListeners.remove(l);
  }

  /**
   * Gets called if the data of the spectrum panel has changed.
   * 
   * @param e		the event that the spectrum panel sent
   */
  public void dataChanged(DataChangeEvent e) {
    updateTitle();
  }
  
  /**
   * Sets whether the entry list is searchable. Container manager must implement
   * {@link SearchableContainerManager} interface to allow enabling of search
   * 
   * @param value	true if to make the list searchable
   * @see		SearchableContainerManager
   */
  public void setAllowSearch(boolean value) {
    if (getManager() instanceof SearchableContainerManager)
      m_PanelSearch.setVisible(value);
    else
      m_PanelSearch.setVisible(false);
  }
  
  /**
   * Returns whether the entry list is searchable.
   * 
   * @return		true if list is searchable
   */
  public boolean getAllowSearch() {
    return m_PanelSearch.isVisible();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    getContainerModel().removeTableModelListener(this);
    m_Table.getSelectionModel().removeListSelectionListener(this);
    getContainerModel().cleanUp();
    m_TableModelListeners.clear();
    m_ListSelectionListeners.clear();
  }
}

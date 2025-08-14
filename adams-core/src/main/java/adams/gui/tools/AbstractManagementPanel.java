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
 * AbstractManagementPanel.java
 * Copyright (C) 2012-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.CleanUpHandler;
import adams.core.logging.Logger;
import adams.core.logging.LoggingHelper;
import adams.core.logging.LoggingSupporter;
import adams.data.id.IDHandler;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.BaseTextPaneWithWordWrap;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.ParameterPanelWithButtons;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.SearchEvent;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.text.Document;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * A panel for managing objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of the objects in the table model
 */
public abstract class AbstractManagementPanel<T extends Comparable>
  extends BasePanel
  implements CleanUpHandler, MenuBarProvider, LoggingSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3181901882660335578L;

  /** the button for clearing the input. */
  protected BaseButton m_ButtonClear;

  /** the button for adding an object. */
  protected BaseButton m_ButtonAdd;

  /** the button for updating an object. */
  protected BaseButton m_ButtonUpdate;

  /** the table for displaying the objects. */
  protected SortableAndSearchableTableWithButtons m_TableValues;

  /** the table model for the objects. */
  protected AbstractManagementTableModel<T> m_ModelValues;

  /** the button for refreshing the table. */
  protected BaseButton m_ButtonRefresh;

  /** the button for deleting selected objects. */
  protected BaseButton m_ButtonRemove;

  /** the button for loading the selected object. */
  protected BaseButton m_ButtonLoad;

  /** the panel with the values for adding/updating objects. */
  protected ParameterPanelWithButtons m_PanelValues;

  /** the panel with the currently available objects. */
  protected JPanel m_PanelTable;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the file chooser for saving the spreadsheet. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the menu bar. */
  protected JMenuBar m_MenuBar;

  /** the menu item for refreshing. */
  protected JMenuItem m_MenuItemFileRefresh;

  /** the menu item for clearing. */
  protected JMenuItem m_MenuItemEditClear;

  /** the menu item for adding. */
  protected JMenuItem m_MenuItemEditAdd;

  /** the menu item for updating. */
  protected JMenuItem m_MenuItemEditUpdate;

  /** the menu item for removing. */
  protected JMenuItem m_MenuItemEditRemove;

  /** the logger instance to use. */
  protected Logger m_Logger;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    // values
    m_PanelValues = new ParameterPanelWithButtons();
    m_PanelValues.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    addFields(m_PanelValues);
    add(m_PanelValues, BorderLayout.NORTH);

    // buttons

    // buttons/clear
    m_ButtonClear = new BaseButton("Clear");
    m_ButtonClear.setMnemonic('C');
    m_ButtonClear.addActionListener((ActionEvent e) -> clear());
    m_PanelValues.addToButtonsPanel(m_ButtonClear);

    // buttons/add
    if (!isReadOnly()) {
      m_ButtonAdd = new BaseButton("Add");
      m_ButtonAdd.setMnemonic('A');
      m_ButtonAdd.addActionListener((ActionEvent e) -> addObject());
      m_PanelValues.addToButtonsPanel(m_ButtonAdd);
    }

    // buttons/update
    if (!isReadOnly()) {
      m_ButtonUpdate = new BaseButton("Update");
      m_ButtonUpdate.setMnemonic('U');
      m_ButtonUpdate.addActionListener((ActionEvent e) -> updateObject());
      m_PanelValues.addToButtonsPanel(m_ButtonUpdate);
    }

    // table
    m_PanelTable = new JPanel(new BorderLayout());
    m_PanelTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(m_PanelTable, BorderLayout.CENTER);

    m_ModelValues = newTableModel();
    m_TableValues = new SortableAndSearchableTableWithButtons(m_ModelValues);
    m_TableValues.setShowSimpleCellPopupMenu(true);
    m_TableValues.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    m_TableValues.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> update());
    m_TableValues.setShowSimpleCellPopupMenu(true);
    m_PanelTable.add(m_TableValues, BorderLayout.CENTER);

    m_ButtonRefresh = new BaseButton("Refresh");
    m_ButtonRefresh.setMnemonic('R');
    m_ButtonRefresh.addActionListener((ActionEvent e) -> refresh());
    m_TableValues.addToButtonsPanel(m_ButtonRefresh);

    m_ButtonLoad = new BaseButton("Load");
    m_ButtonLoad.setMnemonic('L');
    m_ButtonLoad.addActionListener((ActionEvent e) -> loadValue());
    m_TableValues.addToButtonsPanel(m_ButtonLoad);
    m_TableValues.setDoubleClickButton(m_ButtonLoad);

    if (canRemoveObjects()) {
      m_TableValues.addToButtonsPanel(new JLabel(" "));  // separator

      m_ButtonRemove = new BaseButton("Remove");
      m_ButtonRemove.setMnemonic('m');
      m_ButtonRemove.addActionListener((ActionEvent e) -> removeObjects());
      m_TableValues.addToButtonsPanel(m_ButtonRemove);
    }

    // search panel
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
    m_PanelSearch.addSearchListener((SearchEvent e) -> m_TableValues.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    add(m_PanelSearch, BorderLayout.SOUTH);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    clear();
    refresh();
  }

  /**
   * Returns the logger in use.
   *
   * @return		the logger
   */
  public Logger getLogger() {
    if (m_Logger == null)
      m_Logger = LoggingHelper.getLogger(getClass(), Level.INFO);
    return m_Logger;
  }

  /**
   * Returns whether logging is enabled.
   *
   * @return		true if at least {@link Level#INFO}
   */
  public boolean isLoggingEnabled() {
    return LoggingHelper.isAtLeast(getLogger(), Level.INFO);
  }

  /**
   * Hook method for adding items to the "File" menu.
   *
   * @param menu	the menu to update
   * @return 		true if an item was added
   */
  protected boolean addToFileMenu(JMenu menu) {
    return false;
  }

  /**
   * Hook method for adding items to the "Edit" menu.
   *
   * @param menu	the menu to update
   * @return 		true if an item was added
   */
  protected boolean addToEditMenu(JMenu menu) {
    return false;
  }

  /**
   * For adding additional menus.
   *
   * @param menubar	the menubar to extend
   */
  protected void addOtherMenus(JMenuBar menubar) {
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // File/Refresh
      menuitem = new JMenuItem("Refresh");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("F5"));
      menuitem.setIcon(ImageManager.getIcon("refresh.gif"));
      menuitem.addActionListener((ActionEvent e) -> refresh());
      m_MenuItemFileRefresh = menuitem;

      addToFileMenu(menu);

      menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(ImageManager.getIcon("exit.png"));
      menuitem.addActionListener((ActionEvent e) -> closeParent());

      // Edit
      menu = new JMenu("Edit");
      result.add(menu);
      menu.setMnemonic('E');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // Edit/Clear
      menuitem = new JMenuItem("Clear");
      menu.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(ImageManager.getIcon("new.gif"));
      menuitem.addActionListener((ActionEvent e) -> clear());
      m_MenuItemEditClear = menuitem;

      if (!isReadOnly()) {
	// Edit/Add
	menuitem = new JMenuItem("Add");
	menu.add(menuitem);
	menuitem.setMnemonic('A');
	menuitem.setIcon(ImageManager.getIcon("add.gif"));
	menuitem.addActionListener((ActionEvent e) -> addObject());
	m_MenuItemEditAdd = menuitem;

	// Edit/Update
	menuitem = new JMenuItem("Update");
	menu.add(menuitem);
	menuitem.setMnemonic('U');
	menuitem.setIcon(ImageManager.getIcon("save.gif"));
	menuitem.addActionListener((ActionEvent e) -> updateObject());
	m_MenuItemEditUpdate = menuitem;
      }

      if (canRemoveObjects()) {
	// Edit/Remove
	menuitem = new JMenuItem("Remove");
	menu.add(menuitem);
	menuitem.setMnemonic('R');
	menuitem.setIcon(ImageManager.getIcon("delete.gif"));
	menuitem.addActionListener((ActionEvent e) -> removeObjects());
	m_MenuItemEditRemove = menuitem;
      }

      addToEditMenu(menu);

      // additional menus
      addOtherMenus(result);

      m_MenuBar = result;
    }

    return m_MenuBar;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;

    m_MenuItemEditClear.setEnabled(canClearFields());
    if (!isReadOnly()) {
      m_MenuItemEditAdd.setEnabled(canAddObject());
      m_MenuItemEditUpdate.setEnabled(canAddObject());
    }
    if (canRemoveObjects()) {
      m_MenuItemEditRemove.setEnabled(m_TableValues.getSelectedRowCount() > 0);
    }
  }

  /**
   * Returns the filechooser for saving the table as spreadsheet.
   *
   * @return		the filechooser
   */
  protected synchronized SpreadSheetFileChooser getFileChooser() {
    if (m_FileChooser == null) {
      m_FileChooser = new SpreadSheetFileChooser();
      m_FileChooser.setMultiSelectionEnabled(false);
    }

    return m_FileChooser;
  }

  /**
   * Adds the appropriate document listener to the document.
   *
   * @param doc		the document to add the listener to
   * @see		#addListener(Component)
   */
  protected void addDocumentListener(Document doc) {
    doc.addDocumentListener(new DocumentListener() {
      public void changedUpdate(DocumentEvent e) {
	update();
      }
      public void insertUpdate(DocumentEvent e) {
	update();
      }
      public void removeUpdate(DocumentEvent e) {
	update();
      }
    });
  }

  /**
   * Adds an action listener to the combobox.
   *
   * @param combo	the combobox to add the listener to
   * @see		#addListener(Component)
   */
  protected void addActionListener(JComboBox combo) {
    combo.addActionListener((ActionEvent e) -> update());
  }

  /**
   * Adds a change listener to the chooser.
   *
   * @param chooser	the chooser to add the listener to
   * @see		#addListener(Component)
   */
  protected void addChangeListener(AbstractChooserPanel chooser) {
    chooser.addChangeListener((ChangeEvent e) -> update());
  }

  /**
   * Adds the appropriate listener to the component.
   *
   * @param comp	the component to add a listener to
   */
  protected void addListener(Component comp) {
    // document listeners
    if (comp instanceof JTextField)
      addDocumentListener(((JTextField) comp).getDocument());
    else if (comp instanceof JTextArea)
      addDocumentListener(((JTextArea) comp).getDocument());
    else if (comp instanceof BaseTextAreaWithButtons)
      addDocumentListener(((BaseTextAreaWithButtons) comp).getDocument());
    else if (comp instanceof JTextPane)
      addDocumentListener(((JTextPane) comp).getDocument());
    else if (comp instanceof BaseTextPaneWithWordWrap)
      addDocumentListener(((BaseTextPaneWithWordWrap) comp).getDocument());
      // action listeners
    else if (comp instanceof JComboBox)
      addActionListener((JComboBox) comp);
      // change listeners
    else if (comp instanceof AbstractChooserPanel)
      addChangeListener((AbstractChooserPanel) comp);
  }

  /**
   * For adding all the fields.
   *
   * @param panel	the panel to add the fields to
   */
  protected abstract void addFields(ParameterPanelWithButtons panel);

  /**
   * Returns an instance of a new table model.
   *
   * @return		the table model
   */
  protected abstract AbstractManagementTableModel<T> newTableModel();

  /**
   * Returns the class that is being managed.
   *
   * @return		the class being managed
   */
  protected abstract Class getManagedClass();

  /**
   * Returns a human-readable string describing the managed objects.
   *
   * @param multiple	true if to return the plural
   * @return		the name of the objects
   */
  protected String getItemName(boolean multiple) {
    String	result;

    result = getManagedClass().getSimpleName();
    if (multiple)
      result += "s";

    return result;
  }

  /**
   * Resets the input fields.
   */
  protected abstract void clear();

  /**
   * Turns the fields into an object.
   *
   * @return		the generated object
   */
  protected abstract T fieldsToObject();

  /**
   * Updates the field with the specified object.
   *
   * @param value	the object to display
   */
  protected abstract void objectToFields(T value);

  /**
   * Loads all the objects.
   *
   * @return		all available Objects
   */
  protected abstract List<T> loadAll();

  /**
   * Checks whether the object already exists.
   *
   * @param value	the value to look for
   * @return		true if already available
   */
  protected abstract boolean exists(T value);

  /**
   * Stores the object.
   *
   * @param value	the value to store
   * @return		true if successfully stored
   */
  protected abstract boolean store(T value);

  /**
   * Removes the object.
   *
   * @param value	the value to remove
   * @return		true if successfully removed
   */
  protected abstract boolean remove(T value);

  /**
   * Returns the ID from the object.
   * <br><br>
   * Default implementation only returns and ID if the object implements 
   * {@link IDHandler}.
   *
   * @param value	the object to get the ID from
   * @return		the ID, null if it could not be retrieved
   */
  protected String getID(T value) {
    if (value instanceof IDHandler)
      return ((IDHandler) value).getID();
    else
      return null;
  }

  /**
   * Adds the object and refreshes the table.
   */
  protected void addObject() {
    T	value;

    value = fieldsToObject();

    if (exists(value)) {
      GUIHelper.showErrorMessage(
	this, getItemName(false) + " with ID '" + getID(value) + "' already exists!");
      return;
    }

    if (!store(value))
      GUIHelper.showErrorMessage(
	this, "Couldn't add " + getItemName(false) + " with '" + getID(value) + "' - check console!");

    refresh();
  }

  /**
   * Updates the object and refreshes the table.
   */
  protected void updateObject() {
    T	value;

    value = fieldsToObject();

    if (!exists(value)) {
      GUIHelper.showErrorMessage(
	this, getItemName(false) + " with ID '" + getID(value) + "' doesn't exists - cannot update, use 'Add'!");
      return;
    }

    if (!store(value))
      GUIHelper.showErrorMessage(
	this, "Couldn't update " + getItemName(false) + " with ID '" + getID(value) + "' - check console!");

    refresh();
  }

  /**
   * Removes the selected objects and refreshes the table.
   */
  protected void removeObjects() {
    int[]		indices;
    StringBuilder	ids;
    int			i;
    int			retVal;
    final T[]		values;
    SwingWorker		worker;

    indices = m_TableValues.getSelectedRows();
    values  = (T[]) Array.newInstance(getManagedClass(), indices.length);
    ids     = new StringBuilder();
    for (i = 0; i < indices.length; i++) {
      values[i] = m_ModelValues.getItemAt(m_TableValues.getActualRow(indices[i]));
      if (i > 0)
	ids.append(", ");
      ids.append(getID(values[i]));
    }

    retVal = GUIHelper.showConfirmMessage(
      this,
      "Do you really want to remove the following " + getItemName(indices.length != 1) + "?",
      ids.toString(),
      "Confirm removal");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	int count = 0;
	for (Object value: values) {
	  count++;
	  remove((T) value);
	  if (count % 100 == 0)
	    getLogger().info("Removed " + count + "/" + values.length + "...");
	}
	getLogger().info("Removed " + count + "/" + values.length + "!");
	return null;
      }

      @Override
      protected void done() {
	super.done();
	setCursor(Cursor.getDefaultCursor());
	refresh();
      }
    };
    worker.execute();
  }

  /**
   * Loads the Object from the table and displays it in the GUI.
   */
  protected void loadValue() {
    int		row;
    T		value;

    row = m_TableValues.getSelectedRow();
    if (row == -1)
      return;

    value = (T) m_ModelValues.getItemAt(m_TableValues.getActualRow(row));

    objectToFields(value);

    update();
  }

  /**
   * Returns the selected values.
   *
   * @return		the selected values
   */
  protected List<T> getSelectedValues() {
    List<T>	result;
    int[]	rows;

    result = new ArrayList<>();
    rows = m_TableValues.getSelectedRows();
    for (int row: rows)
      result.add(m_ModelValues.getItemAt(m_TableValues.getActualRow(row)));

    return result;
  }

  /**
   * Refreshes the table.
   */
  protected void refresh() {
    SwingWorker		worker;

    m_ModelValues.clear();
    worker = new SwingWorker() {
      @Override
      protected Object doInBackground() throws Exception {
	setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	m_ModelValues.addAll(loadAll());
	return null;
      }

      @Override
      protected void done() {
	super.done();
	m_TableValues.setOptimalColumnWidthBounded(getMaximumColumnWidth());
	setCursor(Cursor.getDefaultCursor());
	update();
      }
    };
    worker.execute();
  }

  /**
   * Returns the maximum column width to use when determining the optimal
   * column width.
   *
   * @return		the maximum
   */
  protected int getMaximumColumnWidth() {
    return 150;
  }

  /**
   * Returns whether the fields can be cleared, i.e., if there is any input.
   *
   * @return		true if input can be cleared
   */
  protected abstract boolean canClearFields();

  /**
   * Returns whether modified data cannot be stored.
   *
   * @return		true if storing is not available
   */
  protected abstract boolean isReadOnly();

  /**
   * Returns whether objects can be deleted.
   * Default implementation returns inverse of {@link #isReadOnly()}.
   *
   * @return		true if delete supported
   */
  protected boolean canRemoveObjects() {
    return !isReadOnly();
  }

  /**
   * Returns whether all the required fields are set to add the object.
   *
   * @return		true if required fields are filled in
   */
  protected abstract boolean canAddObject();

  /**
   * Updates the enabled state of the widgets.
   */
  protected void update() {
    updateMenu();
    updateButtons();
  }

  /**
   * Updates the enabled state of the widgets.
   */
  protected void updateButtons() {
    m_ButtonClear.setEnabled(canClearFields());
    if (!isReadOnly()) {
      m_ButtonAdd.setEnabled(canAddObject());
      m_ButtonUpdate.setEnabled(m_ButtonAdd.isEnabled());
    }
    if (canRemoveObjects()) {
      m_ButtonRemove.setEnabled(m_TableValues.getSelectedRowCount() > 0);
    }
    m_ButtonLoad.setEnabled(m_TableValues.getSelectedRowCount() == 1);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
  }
}

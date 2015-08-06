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
 * Copyright (C) 2012-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.CleanUpHandler;
import adams.data.id.IDHandler;
import adams.data.io.output.SpreadSheetWriter;
import adams.gui.chooser.AbstractChooserPanel;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseTextAreaWithButtons;
import adams.gui.core.BaseTextPaneWithWordWrap;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanelWithButtons;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTable;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.event.PopupMenuListener;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.lang.reflect.Array;
import java.util.List;

/**
 * A panel for managing objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type 
 */
public abstract class AbstractManagementPanel<T extends Comparable>
  extends BasePanel
  implements CleanUpHandler {

  /** for serialization. */
  private static final long serialVersionUID = 3181901882660335578L;

  /** the button for clearing the input. */
  protected JButton m_ButtonClear;

  /** the button for adding an object. */
  protected JButton m_ButtonAdd;

  /** the button for updating an object. */
  protected JButton m_ButtonUpdate;

  /** the table for displaying the objects. */
  protected SortableAndSearchableTableWithButtons m_TableValues;

  /** the table model for the objects. */
  protected AbstractManagementTableModel<T> m_ModelValues;

  /** the button for refreshing the table. */
  protected JButton m_ButtonRefresh;

  /** the button for deleting selected objects. */
  protected JButton m_ButtonRemove;

  /** the button for loading the selected object. */
  protected JButton m_ButtonLoad;

  /** the panel with the values for adding/updating objects. */
  protected ParameterPanelWithButtons m_PanelValues;

  /** the panel with the currently available objects. */
  protected JPanel m_PanelTable;

  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the file chooser for saving the spreadsheet. */
  protected SpreadSheetFileChooser m_FileChooser;
  
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
    m_ButtonClear = new JButton("Clear");
    m_ButtonClear.setMnemonic('C');
    m_ButtonClear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	clear();
      }
    });
    m_PanelValues.addToButtonsPanel(m_ButtonClear);

    // buttons/add
    if (!isReadOnly()) {
      m_ButtonAdd = new JButton("Add");
      m_ButtonAdd.setMnemonic('A');
      m_ButtonAdd.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  addObject();
	}
      });
      m_PanelValues.addToButtonsPanel(m_ButtonAdd);
    }

    // buttons/update
    if (!isReadOnly()) {
      m_ButtonUpdate = new JButton("Update");
      m_ButtonUpdate.setMnemonic('U');
      m_ButtonUpdate.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  updateObject();
	}
      });
      m_PanelValues.addToButtonsPanel(m_ButtonUpdate);
    }

    // table
    m_PanelTable = new JPanel(new BorderLayout());
    m_PanelTable.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(m_PanelTable, BorderLayout.CENTER);

    m_ModelValues = newTableModel();
    m_TableValues = new SortableAndSearchableTableWithButtons(m_ModelValues);
    m_TableValues.setAutoResizeMode(SortableAndSearchableTable.AUTO_RESIZE_OFF);
    m_TableValues.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        update();
      }
    });
    m_TableValues.addCellPopupMenuListener(new PopupMenuListener() {
      public void showPopupMenu(MouseEvent e) {
	BasePopupMenu menu = createPopupMenu(e);
	if (menu != null)
	  menu.showAbsolute(m_TableValues.getComponent(), e);
      }
    });
    m_PanelTable.add(m_TableValues, BorderLayout.CENTER);

    m_ButtonRefresh = new JButton("Refresh");
    m_ButtonRefresh.setMnemonic('R');
    m_ButtonRefresh.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        refresh();
      }
    });
    m_TableValues.addToButtonsPanel(m_ButtonRefresh);

    m_ButtonLoad = new JButton("Load");
    m_ButtonLoad.setMnemonic('L');
    m_ButtonLoad.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        loadValue();
      }
    });
    m_TableValues.addToButtonsPanel(m_ButtonLoad);
    m_TableValues.setDoubleClickButton(m_ButtonLoad);

    m_TableValues.addToButtonsPanel(new JLabel(" "));  // separator

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.setMnemonic('m');
    m_ButtonRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeObjects();
      }
    });
    m_TableValues.addToButtonsPanel(m_ButtonRemove);

    // search panel
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.addSearchListener(new SearchListener() {
      public void searchInitiated(SearchEvent e) {
	m_TableValues.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
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
   * Creates the popup menu for the table.
   * 
   * @param e		the mouse event that triggered the popup
   * @return		the popup menu, null if to suppress menu
   */
  protected BasePopupMenu createPopupMenu(MouseEvent e) {
    BasePopupMenu	result;
    JMenuItem		menuitem;
    
    result = new BasePopupMenu();

    menuitem = new JMenuItem("Copy");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(m_TableValues.getSelectedRowCount() > 0);
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	m_TableValues.getComponent().copyToClipboard();
      }
    });
    result.add(menuitem);

    menuitem = new JMenuItem("Save as...");
    menuitem.setIcon(GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int ret = getFileChooser().showSaveDialog(AbstractManagementPanel.this);
	if (ret != SpreadSheetFileChooser.APPROVE_OPTION)
	  return;
	File file = getFileChooser().getSelectedFile();
	SpreadSheetWriter writer = getFileChooser().getWriter();
	if (!writer.write(m_ModelValues.toSpreadSheet(), file)) {
	  GUIHelper.showErrorMessage(
	      AbstractManagementPanel.this,
	      "Failed to save spreadsheet to the following file:\n" + file);
	}
      }
    });
    result.add(menuitem);
    
    return result;
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
    combo.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	update();
      }
    });
  }
  
  /**
   * Adds a change listener to the chooser.
   * 
   * @param chooser	the chooser to add the listener to
   * @see		#addListener(Component)
   */
  protected void addChangeListener(AbstractChooserPanel chooser) {
    chooser.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	update();
      }
    });
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
    int[]	indices;
    String	ids;
    int		i;
    int		retVal;
    T[]		values;

    indices = m_TableValues.getSelectedRows();
    values  = (T[]) Array.newInstance(getManagedClass(), indices.length);
    ids     = "";
    for (i = 0; i < indices.length; i++) {
      values[i] = m_ModelValues.getItemAt(m_TableValues.getActualRow(indices[i]));
      if (i > 0)
	ids += ", ";
      ids += getID(values[i]);
    }

    retVal = GUIHelper.showConfirmMessage(
	  this,
	  "Do you really want to remove the following " + getItemName(indices.length != 1) + "?", 
	  ids,
	  "Confirm removal");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    for (Object value: values)
      remove((T) value);

    refresh();
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
   * Refreshes the table.
   */
  protected void refresh() {
    m_ModelValues.clear();
    m_ModelValues.addAll(loadAll());
    m_TableValues.setOptimalColumnWidth();
    update();
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
   * Returns whether all the required fields are set to add the object.
   * 
   * @return		true if required fields are filled in
   */
  protected abstract boolean canAddObject();
  
  /**
   * Updates the enabled state of the widgets.
   */
  protected void update() {
    m_ButtonClear.setEnabled(canClearFields());
    if (!isReadOnly()) {
      m_ButtonAdd.setEnabled(canAddObject());
      m_ButtonUpdate.setEnabled(m_ButtonAdd.isEnabled());
    }
    m_ButtonLoad.setEnabled(m_TableValues.getSelectedRowCount() == 1);
    m_ButtonRemove.setEnabled(m_TableValues.getSelectedRowCount() > 0);
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
  }
}

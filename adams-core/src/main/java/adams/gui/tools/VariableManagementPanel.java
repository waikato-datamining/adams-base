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
 * VariableManagementPanel.java
 * Copyright (C) 2009-2020 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.CleanUpHandler;
import adams.core.Variables;
import adams.event.VariableChangeEvent;
import adams.event.VariableChangeEvent.Type;
import adams.event.VariableChangeListener;
import adams.flow.core.ActorUtils;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseCheckBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.dialog.TextDialog;
import adams.gui.event.PopupMenuListener;
import adams.gui.event.SearchEvent;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Panel for managing the variables (at runtime).
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class VariableManagementPanel
  extends BasePanel
  implements CleanUpHandler, VariableChangeListener {

  /** for serialization. */
  private static final long serialVersionUID = 8289824326163269560L;

  /**
   * Specialized table model for the variables.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class VariableTableModel
    extends AbstractBaseTableModel
    implements VariableChangeListener {

    /** for serialization. */
    private static final long serialVersionUID = 1842691685087532235L;

    /** programmatic variables. */
    protected Set<String> m_ProgrammaticVars;

    /** the name - actual value relation. */
    protected Map<String,String> m_Values;

    /** the sorted list of var names. */
    protected List<String> m_Names;

    /** the sorted list of user-only var names. */
    protected List<String> m_NamesUserOnly;

    /** the underlying variables instance. */
    protected Variables m_Variables;

    /** whether to display user-only vars. */
    protected boolean m_DisplayUserOnly;

    /**
     * Initializes the model with the global variables.
     *
     * @param variables		the Variables instance to use
     */
    public VariableTableModel(Variables variables) {
      super();

      m_Variables = variables;

      initialize();
    }

    /**
     * Initializes the model.
     */
    protected void initialize() {
      Enumeration<String> 	enm;
      String 			name;

      m_DisplayUserOnly  = false;
      m_ProgrammaticVars = new HashSet<>(Arrays.asList(ActorUtils.PROGRAMMATIC_VARIABLES));
      m_Names            = new ArrayList<>();
      m_NamesUserOnly    = new ArrayList<>();
      m_Values           = new HashMap<>();
      enm                = m_Variables.names();
      while (enm.hasMoreElements()) {
	name = enm.nextElement();
	m_Names.add(name);
	if (!m_ProgrammaticVars.contains(name))
	  m_NamesUserOnly.add(name);
	m_Values.put(name, m_Variables.get(name));
      }
      Collections.sort(m_Names);
      Collections.sort(m_NamesUserOnly);
    }

    /**
     * Returns the underlying Variables instance.
     *
     * @return		the variables
     */
    public Variables getVariables() {
      return m_Variables;
    }

    /**
     * Sets whether only user variables are displayed without the programmatic ones.
     *
     * @param value	true if user-only
     */
    public void setDisplayUserOnly(boolean value) {
      m_DisplayUserOnly = value;
      fireTableDataChanged();
    }

    /**
     * Returns whether only user variables are displayed without the programmatic ones.
     *
     * @return		true if user-only
     */
    public boolean getDisplayUserOnly() {
      return m_DisplayUserOnly;
    }

    /**
     * Returns the number of columns in the model.
     *
     * @return		always 2
     */
    public int getColumnCount() {
      return 2;
    }

    /**
     * Returns the number of rows in the model.
     *
     * @return		the number of variables
     */
    public int getRowCount() {
      if (m_DisplayUserOnly)
        return m_NamesUserOnly.size();
      else
	return m_Names.size();
    }

    /**
     * Returns the class of the column.
     *
     * @param columnIndex	the index of the column
     * @return			the class of the column
     */
    @Override
    public Class<?> getColumnClass(int columnIndex) {
      if (columnIndex == 0)
	return String.class;
      else if (columnIndex == 1)
	return String.class;
      else
	throw new IllegalArgumentException("Illegal column: " + columnIndex);
    }

    /**
     * Returns the name of the column.
     *
     * @param column	the index of the column
     * @return		the name
     */
    @Override
    public String getColumnName(int column) {
      if (column == 0)
	return "Name";
      else if (column == 1)
	return "Value";
      else
	throw new IllegalArgumentException("Illegal column: " + column);
    }

    /**
     * Returns the name at the specified row.
     *
     * @param rowIndex	the row to get the variable name for
     * @return		the name
     */
    protected String getVariableNameAt(int rowIndex) {
      List<String>	names;

      if (m_DisplayUserOnly)
        names = m_NamesUserOnly;
      else
        names = m_Names;

      return names.get(rowIndex);
    }

    /**
     * Returns the cell value.
     *
     * @param rowIndex		the row of the cell
     * @param columnIndex	the column of the cell
     * @return			the value
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (columnIndex == 0)
	return getVariableNameAt(rowIndex);
      else if (columnIndex == 1)
	return m_Values.get(getVariableNameAt(rowIndex));
      else
	throw new IllegalArgumentException("Illegal column: " + columnIndex);
    }

    /**
     * Checks whether the cell is editable.
     *
     * @param rowIndex		the row of the cell
     * @param columnIndex	the column of the cell
     * @return			always true
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
      return true;
    }

    /**
     * Sets the value at the specified position.
     *
     * @param value		the value to set
     * @param rowIndex		the row of the cell
     * @param columnIndex	the column of the cell
     */
    @Override
    public void setValueAt(Object value, int rowIndex, int columnIndex) {
      String	newName;
      String	oldName;
      String	oldValue;
      String	name;
      int	index;

      if (columnIndex == 0) {
	newName = ((String) value);
	if (!Variables.isValidName(newName)) {
	  GUIHelper.showErrorMessage(
	      null,
	      "Not a valid variable name: " + newName + "\n"
	      + "Allowed characters:\n" + Variables.CHARS);
	  return;
	}
	oldName  = getVariableNameAt(rowIndex);
	oldValue = m_Values.get(oldName);
	index    = m_Names.indexOf(oldName);
	m_Names.set(index, newName);
	index    = m_NamesUserOnly.indexOf(oldName);
	if (index != -1)
	  m_NamesUserOnly.set(index, newName);
	m_Values.remove(oldName);
	m_Values.put(newName, oldValue);
	m_Variables.remove(oldName);
	m_Variables.set(newName, oldValue);
      }
      else if (columnIndex == 1) {
        name = getVariableNameAt(rowIndex);
	m_Values.put(name, (String) value);
	m_Variables.set(name, (String) value);
      }
      else {
	throw new IllegalArgumentException("Illegal column: " + columnIndex);
      }
    }

    /**
     * Removes the variable at the position.
     *
     * @param rowIndex	the row to remove
     */
    public void remove(int rowIndex) {
      m_Variables.remove(getVariableNameAt(rowIndex));  // triggers event
    }

    /**
     * Adds the variable (name-value pair) to the model.
     *
     * @param name	the name of the variable
     * @param value	the corresponding value
     */
    public void add(String name, String value) {
      if (!Variables.isValidName(name)) {
	GUIHelper.showErrorMessage(
	    null,
	    "Not a valid variable name: " + name + "\n"
	    + "Allowed characters:\n" + Variables.CHARS);
	return;
      }
      if (m_Names.contains(name))
	return;

      m_Variables.set(name, value);  // triggers event
    }

    /**
     * Gets triggered when a variable changed (added, modified, removed).
     *
     * @param e		the event
     */
    public void variableChanged(VariableChangeEvent e) {
      int	rowIndex;

      if (e.getType() == Type.ADDED) {
	m_Names.add(e.getName());
	m_NamesUserOnly.add(e.getName());
	Collections.sort(m_Names);
	Collections.sort(m_NamesUserOnly);
	m_Values.put(e.getName(), m_Variables.get(e.getName()));
	// notify
	rowIndex = m_Names.indexOf(e.getName());
	fireTableRowsInserted(rowIndex, rowIndex);
      }
      else if (e.getType() == Type.REMOVED) {
	rowIndex = m_Names.indexOf(e.getName());
	m_Names.remove(e.getName());
	m_NamesUserOnly.remove(e.getName());
	m_Values.remove(e.getName());
	// notify
	fireTableRowsDeleted(rowIndex, rowIndex);
      }
      else if (e.getType() == Type.MODIFIED) {
	m_Values.put(e.getName(), m_Variables.get(e.getName()));
	rowIndex = m_Names.indexOf(e.getName());
	fireTableCellUpdated(rowIndex, 1);
      }
    }
  }

  /** the underlying table model. */
  protected VariableTableModel m_Model;

  /** the table holding the variables. */
  protected SortableAndSearchableTableWithButtons m_Table;

  /** the filechooser for choosing directories. */
  protected BaseFileChooser m_FileChooser;

  /** for searching the variables. */
  protected SearchPanel m_PanelSearch;

  /** whether to show only user variables. */
  protected BaseCheckBox m_CheckBoxUserOnlyVars;

  /** the button for copying the variable name. */
  protected BaseButton m_ButtonCopyName;

  /** the button for copying the variable value. */
  protected BaseButton m_ButtonCopyValue;

  /** the button for showing the variable value. */
  protected BaseButton m_ButtonShowValue;
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new BaseFileChooser();
    m_FileChooser.setFileSelectionMode(BaseFileChooser.DIRECTORIES_ONLY);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    // table
    m_Model = new VariableTableModel(new Variables());
    m_Table = new SortableAndSearchableTableWithButtons(m_Model);
    m_Table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> update());
    m_Table.addCellPopupMenuListener(new PopupMenuListener() {
      public void showPopupMenu(MouseEvent e) {
	BasePopupMenu menu = new BasePopupMenu();
	JMenuItem menuitem;
	final int row = m_Table.rowAtPoint(e.getPoint());
	boolean enabled = (m_Table.getSelectedRowCount() == 1);
	// variable name
	menuitem = new JMenuItem("Copy name");
	menuitem.setEnabled(enabled);
	menuitem.addActionListener((ActionEvent ae) -> copyName(row));
	menu.add(menuitem);
	// variable value
	menuitem = new JMenuItem("Copy value");
	menuitem.setEnabled(enabled);
	menuitem.addActionListener((ActionEvent ae) -> copyValue(row));
	menu.add(menuitem);
	// show value
	menuitem = new JMenuItem("Show value");
	menuitem.setEnabled(enabled);
	menuitem.addActionListener((ActionEvent ae) -> showValue(row));
	menu.addSeparator();
	menu.add(menuitem);
	
	menu.showAbsolute(m_Table.getComponent(), e);
      }
    });
    add(m_Table, BorderLayout.CENTER);

    m_ButtonCopyName = new BaseButton("Copy name");
    m_ButtonCopyName.addActionListener((ActionEvent e) -> copyName(m_Table.getSelectedRow()));
    m_Table.addToButtonsPanel(m_ButtonCopyName);

    m_ButtonCopyValue = new BaseButton("Copy value");
    m_ButtonCopyValue.addActionListener((ActionEvent e) -> copyValue(m_Table.getSelectedRow()));
    m_Table.addToButtonsPanel(m_ButtonCopyValue);

    m_ButtonShowValue = new BaseButton("Show value");
    m_ButtonShowValue.addActionListener((ActionEvent e) -> showValue(m_Table.getSelectedRow()));
    m_Table.addToButtonsPanel(m_ButtonShowValue);
    
    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_PanelSearch.addSearchListener((SearchEvent e) ->
      m_Table.getComponent().search(
	e.getParameters().getSearchString(),
	e.getParameters().isRegExp()));
    m_PanelSearch.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

    m_CheckBoxUserOnlyVars = new BaseCheckBox("Show user-only vars");
    m_CheckBoxUserOnlyVars.setSelected(false);
    m_CheckBoxUserOnlyVars.addActionListener((ActionEvent e) -> updateUserOnlyVariables());

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.SOUTH);
    panel.add(m_PanelSearch, BorderLayout.CENTER);
    panel.add(m_CheckBoxUserOnlyVars, BorderLayout.EAST);

    update();
  }

  /**
   * Finalizes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();

    m_Model.getVariables().addVariableChangeListener(this);
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    String	title;

    title = "Variable management";

    setParentTitle(title);
  }

  /**
   * Updates menu, buttons and title.
   */
  protected void update() {
    updateTitle();
    
    m_ButtonCopyName.setEnabled(m_Table.getSelectedRowCount()  == 1);
    m_ButtonCopyValue.setEnabled(m_Table.getSelectedRowCount() == 1);
    m_ButtonShowValue.setEnabled(m_Table.getSelectedRowCount() == 1);
  }

  /**
   * Copies the name of the variable in the specified row to the clipboard.
   * 
   * @param row		the row in the table
   */
  protected void copyName(int row) {
    ClipboardHelper.copyToClipboard("" + m_Table.getValueAt(row, 0));
  }
  
  /**
   * Copies the value of the variable in the specified row to the clipboard.
   * 
   * @param row		the row in the table
   */
  protected void copyValue(int row) {
    ClipboardHelper.copyToClipboard("" + m_Table.getValueAt(row, 1));
  }

  /**
   * Shows the value of the variable in the specified row in a separate dialog.
   * 
   * @param row		the row in the table
   */
  protected void showValue(int row) {
    TextDialog 	dlg;
    
    dlg = new TextDialog();
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setUpdateParentTitle(false);
    dlg.setTitle("" + m_Table.getValueAt(row, 0));
    dlg.setContent("" + m_Table.getValueAt(row, 1));
    dlg.setSize(GUIHelper.getDefaultTinyDialogDimension());
    dlg.setLineWrap(true);
    dlg.setLocationRelativeTo(VariableManagementPanel.this);
    dlg.setVisible(true);
  }

  /**
   * Updates whether programmatic variables are displayed or not.
   */
  protected void updateUserOnlyVariables() {
    m_Model.setDisplayUserOnly(m_CheckBoxUserOnlyVars.isSelected());
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }

  /**
   * Sets the Variables instance to use.
   *
   * @param value	the instance to use
   */
  public void setVariables(Variables value) {
    boolean	userOnly;

    userOnly = m_Model.getDisplayUserOnly();
    m_Model.getVariables().removeVariableChangeListener(this);
    m_Model = new VariableTableModel(value);
    m_Model.setDisplayUserOnly(userOnly);
    m_Model.getVariables().addVariableChangeListener(this);
    m_Table.setModel(m_Model);
    m_Table.setOptimalColumnWidth();
  }

  /**
   * Returns the underlying Variables instance in use.
   *
   * @return		the instance in use
   */
  public Variables getVariables() {
    return m_Model.getVariables();
  }

  /**
   * Gets triggered when a variable changed (added, modified, removed).
   *
   * @param e		the event
   */
  public void variableChanged(VariableChangeEvent e) {
    m_Model.variableChanged(e);
    if (m_PanelSearch.getSearchText().length() > 0)
      m_PanelSearch.search();
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Model.getVariables().removeVariableChangeListener(this);
  }
}

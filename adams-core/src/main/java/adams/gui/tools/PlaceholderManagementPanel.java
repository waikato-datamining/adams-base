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
 * PlaceholderManagementPanel.java
 * Copyright (C) 2009-2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.tools;

import adams.core.Placeholders;
import adams.core.Properties;
import adams.core.base.BaseString;
import adams.env.Environment;
import adams.env.PlaceholdersDefinition;
import adams.gui.chooser.DirectoryChooserFactory;
import adams.gui.chooser.FileChooser;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTabbedPane;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.BaseTextField;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.MouseUtils;

import javax.swing.DefaultCellEditor;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for managing the placeholders.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PlaceholderManagementPanel
  extends BasePanel
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = -7798984060662041747L;

  /**
   * Specialized table model for the placeholders.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class PlaceholderTableModel
    extends AbstractBaseTableModel {

    /** for serialization. */
    private static final long serialVersionUID = -7703129343528534771L;

    /** whether the model is readonly. */
    protected boolean m_ReadOnly;

    /** whether the values got modified. */
    protected boolean m_Modified;

    /** the key - actual value relation. */
    protected Map<String,String> m_Values;

    /** the sorted list of keys. */
    protected List<String> m_Keys;

    /**
     * Initializes the model with the global placeholders.
     *
     * @param system	whether to display system ones or user-defined ones
     */
    public PlaceholderTableModel(boolean system) {
      this(Placeholders.getSingleton().toProperties(), system);
    }

    /**
     * Initializes the model with the specified properties.
     *
     * @param props	the properties to display
     * @param system	whether to display system ones or user-defined ones
     */
    public PlaceholderTableModel(Properties props, boolean system) {
      super();

      m_Modified = false;
      m_ReadOnly = system;
      m_Keys     = new ArrayList<>();
      m_Values   = new HashMap<>();
      Enumeration<String> enm = (Enumeration<String>) props.propertyNames();
      while (enm.hasMoreElements()) {
	String key = enm.nextElement();
	boolean add;
	if (system)
	  add = (    key.equals(Placeholders.CWD)
	          || key.equals(Placeholders.PROJECT)
   	          || key.equals(Placeholders.TMP)
	          || key.equals(Placeholders.HOME) );
	else
	  add = !(    key.equals(Placeholders.CWD)
	           || key.equals(Placeholders.PROJECT)
	           || key.equals(Placeholders.TMP)
	           || key.equals(Placeholders.HOME) );
	if (add) {
	  m_Keys.add(key);
	  m_Values.put(key, props.getProperty(key));
	}
      }
      Collections.sort(m_Keys);
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
     * @return		the number of placeholders
     */
    public int getRowCount() {
      return m_Keys.size();
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
	return "Key";
      else if (column == 1)
	return "Value";
      else
	throw new IllegalArgumentException("Illegal column: " + column);
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
	return m_Keys.get(rowIndex);
      else if (columnIndex == 1)
	return m_Values.get(m_Keys.get(rowIndex));
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
      return !m_ReadOnly;
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
      String	newKey;
      String	oldKey;
      String	oldValue;

      if (columnIndex == 0) {
	newKey   = ((String) value).toUpperCase();
	oldKey   = m_Keys.get(rowIndex);
	oldValue = m_Values.get(oldKey);
	m_Values.remove(oldKey);
	m_Keys.set(rowIndex, newKey);
	m_Values.put(newKey, oldValue);
	m_Modified = true;
	fireTableCellUpdated(rowIndex, columnIndex);
      }
      else if (columnIndex == 1) {
	m_Values.put(m_Keys.get(rowIndex), (String) value);
	m_Modified = true;
	fireTableCellUpdated(rowIndex, columnIndex);
      }
      else {
	throw new IllegalArgumentException("Illegal column: " + columnIndex);
      }
    }

    /**
     * Sets the modified state.
     *
     * @param value	the modified state
     */
    public void setModified(boolean value) {
      if (m_ReadOnly)
	return;

      m_Modified = value;
    }

    /**
     * Returns modified state.
     *
     * @return		the modified state
     */
    public boolean isModified() {
      return m_Modified;
    }

    /**
     * Removes the placeholder at the position.
     *
     * @param rowIndex	the row to remove
     */
    public void remove(int rowIndex) {
      if (m_ReadOnly)
	return;

      m_Values.remove(m_Keys.get(rowIndex));
      m_Keys.remove(rowIndex);

      m_Modified = true;

      fireTableDataChanged();
    }

    /**
     * Adds the placeholder (key-value pair) to the model.
     *
     * @param key	the key of the placeholder
     * @param value	the corresponding value
     */
    public void add(String key, String value) {
      if (m_ReadOnly)
	return;

      key = key.toUpperCase();
      if (m_Keys.contains(key))
	return;

      m_Keys.add(key);
      Collections.sort(m_Keys);
      m_Values.put(key, value);

      m_Modified = true;

      fireTableDataChanged();
    }

    /**
     * Returns the data as props structure.
     *
     * @return		the generated properties
     */
    public Properties getProperties() {
      Properties	result;

      result = new Properties();

      for (String key: m_Values.keySet())
	result.setProperty(key, m_Values.get(key));

      return result;
    }

    /**
     * Returns the data as BaseString array.
     *
     * @return		the generated array
     */
    public BaseString[] getArray() {
      BaseString[]	result;
      int		i;

      result = new BaseString[m_Values.size()];

      i = 0;
      for (String key: m_Values.keySet()) {
	result[i] = new BaseString(key + Placeholders.SEPARATOR + m_Values.get(key));
	i++;
      }

      return result;
    }
  }

  /**
   * Custom cell renderer for the placeholders.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class PlaceholderTableCellEditor
    extends DefaultCellEditor {

    /** for serialization. */
    private static final long serialVersionUID = 8824466216235970313L;

    /** the table. */
    protected BaseTable m_Table;

    /** the table model to use. */
    protected PlaceholderTableModel m_TableModel;

    /** the BaseTextField used for displaying the path. */
    protected BaseTextField m_TextPath;

    /** the underlying directory chooser. */
    protected FileChooser m_DirChooser;

    /**
     * Initializes the cell editor.
     *
     * @param table		the table in use
     * @param model		the model to use
     * @param dirChooser	the directory chooser to use
     */
    public PlaceholderTableCellEditor(BaseTable table, PlaceholderTableModel model, FileChooser dirChooser) {
      super(new BaseTextField());

      m_TextPath = (BaseTextField) getComponent();
      m_TextPath.setToolTipText("Double-click to bring up a dialog to select a directory");
      m_TextPath.addMouseListener(new MouseAdapter() {
	@Override
	public void mouseClicked(MouseEvent e) {
	  if (MouseUtils.isDoubleClick(e)) {
	    e.consume();
	    m_DirChooser.setSelectedFile(new File(m_TextPath.getText()));
	    int retVal = m_DirChooser.showOpenDialog(m_Table);
	    if (retVal != DirectoryChooserFactory.APPROVE_OPTION)
	      return;
	    m_TextPath.setText(m_DirChooser.getSelectedFile().getAbsolutePath());
	  }
	  else {
	    super.mouseClicked(e);
	  }
	}
      });

      m_Table      = table;
      m_TableModel = model;
      m_DirChooser = dirChooser;
    }
  }

  /** the tabbed pane. */
  protected BaseTabbedPane m_TabbedPane;

  /** the underlying table model. */
  protected PlaceholderTableModel m_Model;

  /** the table holding the placeholders. */
  protected BaseTableWithButtons m_Table;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "save" menu item. */
  protected JMenuItem m_MenuItemSave;

  /** the "revert" menu item. */
  protected JMenuItem m_MenuItemRevert;

  /** the "close" menu item. */
  protected JMenuItem m_MenuItemClose;

  /** the button for adding a placeholder. */
  protected BaseButton m_ButtonAdd;

  /** the button for removing the selected placeholders. */
  protected BaseButton m_ButtonRemove;

  /** the directory chooser for choosing directories. */
  protected FileChooser m_DirChooser;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DirChooser = DirectoryChooserFactory.createChooser();
    m_DirChooser.setDialogTitle("Select directory");
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    PlaceholderTableModel	modelSystem;
    BaseTableWithButtons	tableSystem;

    super.initGUI();

    setLayout(new BorderLayout());

    m_TabbedPane = new BaseTabbedPane();
    add(m_TabbedPane, BorderLayout.CENTER);

    // user
    m_Model = new PlaceholderTableModel(false);
    m_Model.addTableModelListener((TableModelEvent e) -> update());
    m_Table = new BaseTableWithButtons(m_Model);
    m_Table.getColumnModel().getColumn(1).setCellEditor(new PlaceholderTableCellEditor(m_Table.getComponent(), m_Model, m_DirChooser));
    m_Table.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    m_Table.setInfoVisible(true);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setOptimalColumnWidth();
    m_Table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> updateButtons());
    m_Table.getModel().addTableModelListener((TableModelEvent e) -> m_Table.setOptimalColumnWidth());
    m_Table.setShowSimpleCellPopupMenu(true);
    m_TabbedPane.addTab("User", m_Table);

    // buttons
    m_ButtonAdd = new BaseButton("Add...");
    m_ButtonAdd.setMnemonic('A');
    m_ButtonAdd.addActionListener((ActionEvent e) -> {
      String key = GUIHelper.showInputDialog(PlaceholderManagementPanel.this, "Please add new placeholder");
      if (key == null)
	return;

      int retVal = m_DirChooser.showOpenDialog(PlaceholderManagementPanel.this);
      if (retVal != DirectoryChooserFactory.APPROVE_OPTION)
	return;

      m_Model.add(key, m_DirChooser.getSelectedFile().getAbsolutePath());
    });
    m_Table.addToButtonsPanel(m_ButtonAdd);

    m_ButtonRemove = new BaseButton("Remove");
    m_ButtonRemove.setMnemonic('R');
    m_ButtonRemove.addActionListener((ActionEvent e) -> {
      int[] indices = m_Table.getSelectedRows();
      for (int i = indices.length - 1; i >= 0; i--)
	m_Model.remove(indices[i]);
    });
    m_Table.addToButtonsPanel(m_ButtonRemove);

    // system
    modelSystem = new PlaceholderTableModel(true);
    tableSystem = new BaseTableWithButtons(modelSystem);
    tableSystem.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    tableSystem.setInfoVisible(true);
    tableSystem.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    tableSystem.setOptimalColumnWidth();
    tableSystem.setShowSimpleCellPopupMenu(true);
    m_TabbedPane.addTab("System", tableSystem);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    update();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar		result;
    JMenu		menu;
    JMenuItem		menuitem;

    if (m_MenuBar == null) {
      // register window listener since we're part of a dialog or frame
      if (getParentFrame() != null) {
	final JFrame frame = (JFrame) getParentFrame();
	frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	frame.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }
      else if (getParentDialog() != null) {
	final JDialog dialog = (JDialog) getParentDialog();
	dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	dialog.addWindowListener(new WindowAdapter() {
	  @Override
	  public void windowClosing(WindowEvent e) {
	    close();
	  }
	});
      }

      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      // File/Save
      menuitem = new JMenuItem("Save");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(ImageManager.getIcon("save.gif"));
      menuitem.addActionListener((ActionEvent e) -> save());
      m_MenuItemSave = menuitem;

      // File/Revert
      menuitem = new JMenuItem("Revert");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setIcon(ImageManager.getIcon("revert.png"));
      menuitem.addActionListener((ActionEvent e) -> revert());
      m_MenuItemRevert = menuitem;

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(ImageManager.getIcon("exit.png"));
      menuitem.addActionListener((ActionEvent e) -> close());
      m_MenuItemClose = menuitem;

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    String	title;

    title = "Placeholder management";
    if (m_Model.isModified())
      title = "*" + title;

    setParentTitle(title);
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    updateTitle();

    if (m_MenuBar == null)
      return;

    // File
    m_MenuItemSave.setEnabled(m_Model.isModified());
    m_MenuItemRevert.setEnabled(m_Model.isModified());
    m_MenuItemClose.setEnabled(true);
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonAdd.setEnabled(true);
    m_ButtonRemove.setEnabled(m_Table.getSelectedRowCount() > 0);
  }

  /**
   * Updates menu, buttons and title.
   */
  protected void update() {
    updateMenu();
    updateTitle();
    updateButtons();
  }

  /**
   * Returns whether we can proceed with the operation or not, depending on
   * whether the user saved the placeholders or discarded the changes.
   * In case of custom placeholdersm the use will never get asked.
   *
   * @return		true if safe to proceed
   */
  protected boolean checkForModified() {
    boolean 	result;
    int		retVal;

    result = !m_Model.isModified();

    if (!result) {
      retVal = GUIHelper.showConfirmMessage(
	  this,
	  "Placeholders are modified - save?",
	  "Placeholders modified");

      switch (retVal) {
	case GUIHelper.APPROVE_OPTION:
	  save();
	  result = !m_Model.isModified();
	  break;
	case GUIHelper.DISCARD_OPTION:
	  result = true;
	  break;
	case GUIHelper.CANCEL_OPTION:
	  result = false;
	  break;
      }
    }

    return result;
  }

  /**
   * Saves the current placeholders.
   * Ignored if custom placeholders.
   */
  protected void save() {
    String	filename;

    filename = Environment.getInstance().getCustomPropertiesFilename(PlaceholdersDefinition.KEY);
    if (!Environment.getInstance().write(PlaceholdersDefinition.KEY, m_Model.getProperties())) {
      GUIHelper.showErrorMessage(
	  this,
	  "Error saving placeholders to '" + filename + "'!",
	  "Error saving placeholders");
    }
    else {
      GUIHelper.showInformationMessage(
	  this,
	  "Placeholders have been successfully saved to '" + filename + "'!\n"
	  + "Please restart the application now.",
	  "Placeholders saved");
      m_Model.setModified(false);
    }

    update();
  }

  /**
   * Reverts the changes.
   * Ignored if custom placeholders.
   */
  protected void revert() {
    m_Model = new PlaceholderTableModel(false);
    m_Table.setModel(m_Model);

    update();
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    if (!checkForModified()) {
      if (getParentDialog() != null)
	getParentDialog().setVisible(true);
      else if (getParentFrame() != null)
	getParentFrame().setVisible(true);
      return;
    }

    if (getParentFrame() != null)
      ((JFrame) getParentFrame()).setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    closeParent();
  }
}

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
 * NamedSetupManagement.java
 * Copyright (C) 2010-2012 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools;

import adams.core.ClassLister;
import adams.core.NamedSetup;
import adams.core.NamedSetups;
import adams.core.Properties;
import adams.core.option.OptionUtils;
import adams.env.Environment;
import adams.env.NamedSetupsDefinition;
import adams.gui.core.AbstractBaseTableModel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseTable;
import adams.gui.core.BaseTableWithButtons;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.goe.GenericObjectEditorDialog;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

/**
 * Management panel for the system-wide named setups.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedSetupManagementPanel
  extends BasePanel
  implements MenuBarProvider {

  /** for serialization. */
  private static final long serialVersionUID = -1320023079138581818L;

  /**
   * The table model for the setups.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class Model
    extends AbstractBaseTableModel {

    /** for serialization. */
    private static final long serialVersionUID = -172967388530743539L;

    /** the (sorted) names. */
    protected Vector<String> m_Names;

    /** the names &lt;-&gt; setup relation. */
    protected Hashtable<String,String> m_Setups;

    /** whether the model was modified. */
    protected boolean m_Modified;

    /**
     * Initializes the model with the global settings.
     */
    public Model() {
      super();

      m_Modified = false;
      m_Names    = new Vector<String>();
      m_Setups   = new Hashtable<String,String>();

      Enumeration<String> enm = NamedSetups.getSingleton().names();
      while (enm.hasMoreElements()) {
	String name = enm.nextElement();
	String cmdline = null;
	try {
	  cmdline = OptionUtils.getCommandLine(NamedSetups.getSingleton().get(name));
	  m_Names.add(name);
	  m_Setups.put(name, cmdline);
	}
	catch (Exception e) {
	  System.err.println("Failed to obtain command-line for named setup '" + name + "':");
	  e.printStackTrace();
	}
      }

      Collections.sort(m_Names);
    }

    /**
     * Returns the number of setups.
     *
     * @return		the number of setups
     */
    public int getRowCount() {
      return m_Names.size();
    }

    /**
     * Returns the number of columns.
     *
     * @return		always 2
     */
    public int getColumnCount() {
      return 2;
    }

    /**
     * Returns the name of the columns.
     *
     * @param column	the column to get the name for
     * @return		the name
     */
    @Override
    public String getColumnName(int column) {
      if (column == 0)
	return "Name";
      else if (column == 1)
	return "Setup";
      else
	throw new IllegalArgumentException("Illegal column: " + column);
    }

    /**
     * Returns the value to display.
     *
     * @param rowIndex		the row of the cell
     * @param columnIndex	the column of the cell
     * @return			the cell value
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
      if (columnIndex == 0)
	return m_Names.get(rowIndex);
      else if (columnIndex == 1)
	return m_Setups.get(m_Names.get(rowIndex));
      else
	throw new IllegalArgumentException("Illegal column: " + columnIndex);
    }

    /**
     * Adds the setup to the model.
     *
     * @param name	the name of the setup
     * @param obj	the setup itself
     */
    public void add(String name, Object obj) {
      m_Names.add(name);
      m_Setups.put(name, OptionUtils.getCommandLine(obj));

      Collections.sort(m_Names);

      m_Modified = true;

      fireTableDataChanged();
    }

    /**
     * Removes the specified setup.
     *
     * @param index	the index of the setup to remove
     */
    public void remove(int index) {
      remove(m_Names.get(index));
    }

    /**
     * Removes the specified setup.
     *
     * @param name	the name of the setup to remove
     */
    public void remove(String name) {
      m_Names.remove(name);
      m_Setups.remove(name);

      m_Modified = true;

      fireTableDataChanged();
    }

    /**
     * Returns all the setups in a properties file.
     *
     * @return		the current setups in props format
     */
    public Properties getProperties() {
      Properties	result;
      int		i;

      result = new Properties();

      for (i = 0; i < m_Names.size(); i++)
	result.setProperty(m_Names.get(i), m_Setups.get(m_Names.get(i)));

      return result;
    }

    /**
     * Returns whether the model has been modified.
     *
     * @return		true if the model was modified
     */
    public boolean isModified() {
      return m_Modified;
    }

    /**
     * Sets the modified state of the model.
     *
     * @param value	if true then the model will be flagged as modified
     */
    public void setModified(boolean value) {
      m_Modified = value;
    }

    /**
     * Returns the index of the setup.
     *
     * @param setup	the setup to get the index for
     * @return		the index of -1 if not found
     */
    public int indexOf(NamedSetup setup) {
      int	result;
      int	i;

      result = -1;

      for (i = 0; i < m_Names.size(); i++) {
	if (m_Names.get(i).equals(setup.getName())) {
	  result = i;
	  break;
	}
      }

      return result;
    }
  }

  /** the table for displaying the setups. */
  protected BaseTableWithButtons m_Table;

  /** the underlying table model. */
  protected Model m_Model;

  /** the button for adding a setup. */
  protected JButton m_ButtonAdd;

  /** the button for removing a setup. */
  protected JButton m_ButtonRemove;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the save menu item. */
  protected JMenuItem m_MenuItemSave;

  /** the close menu item. */
  protected JMenuItem m_MenuItemClose;

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_Model = new Model();
    m_Table = new BaseTableWithButtons(m_Model);
    m_Table.setInfoVisible(true);
    m_Table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    m_Table.setOptimalColumnWidth();
    m_Table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
	updateButtons();
      }
    });
    m_Table.getModel().addTableModelListener(new TableModelListener() {
      @Override
      public void tableChanged(TableModelEvent e) {
	m_Table.setOptimalColumnWidth();
      }
    });
    add(m_Table, BorderLayout.CENTER);

    // buttons
    m_ButtonAdd = new JButton("Add...");
    m_ButtonAdd.setMnemonic('A');
    m_ButtonAdd.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	add();
      }
    });
    m_Table.addToButtonsPanel(m_ButtonAdd);

    m_ButtonRemove = new JButton("Remove");
    m_ButtonRemove.setMnemonic('R');
    m_ButtonRemove.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	remove();
	int[] indices = m_Table.getSelectedRows();
	for (int i = indices.length - 1; i >= 0; i--)
	  m_Model.remove(indices[i]);
      }
    });
    m_Table.addToButtonsPanel(m_ButtonRemove);

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
      menu.addChangeListener(new ChangeListener() {
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Save
      menuitem = new JMenuItem("Save");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
      menuitem.setIcon(GUIHelper.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  save();
	}
      });
      m_MenuItemSave = menuitem;

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.addSeparator();
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(GUIHelper.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });
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

    title = "Named setup management";
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
   * Adds a named setup.
   */
  protected void add() {
    List<String> 		superclasses;
    String[]			classes;
    String			selected;
    GenericObjectEditorDialog	dialog;
    Class			cls;
    Object			defValue;
    String			name;

    // first: select superclass
    superclasses = new ArrayList<>(Arrays.asList(ClassLister.getSingleton().getSuperclasses()));
    Collections.sort(superclasses);

    selected = GUIHelper.showInputDialog(
	this,
	"Please select type of setup",
	null,
	superclasses.toArray(new String[superclasses.size()]),
	"Type");
    if (selected == null)
      return;

    // second: show GOE for setup
    try {
      cls = Class.forName(selected);
    }
    catch (Exception e) {
      System.err.println("Failed to obtain class for: " + selected);
      e.printStackTrace();
      return;
    }
    classes = ClassLister.getSingleton().getClassnames(cls);
    if (classes.length == 0) {
      GUIHelper.showErrorMessage(
	  this, "No concrete classes available for superclass '" + cls.getName() + "'!");
      return;
    }

    try {
      defValue = Class.forName(classes[0]).newInstance();
    }
    catch (Exception e) {
      System.err.println("Failed to instantiate object for class  '" + classes[0] + "'!");
      e.printStackTrace();
      return;
    }

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog());
    else
      dialog = new GenericObjectEditorDialog(getParentFrame());
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(cls);
    dialog.getGOEEditor().setValue(defValue);
    dialog.setTitle("Configure the setup");
    dialog.setLocationRelativeTo(this);
    dialog.setModalityType(ModalityType.DOCUMENT_MODAL);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    // third: nickname for setup
    name = GUIHelper.showInputDialog(
	GUIHelper.getParentComponent(this),
	"Please enter 'nickname' for the setup:",
	"name_for_setup");
    if (name == null)
      return;

    // add setup
    m_Model.add(name, dialog.getCurrent());

    update();
  }

  /**
   * Removes the currently selected setups.
   */
  protected void remove() {
    int[] 	indices;
    int 	i;

    indices = m_Table.getSelectedRows();
    for (i = indices.length - 1; i >= 0; i--)
      m_Model.remove(indices[i]);

    update();
  }

  /**
   * Saves the current named setups.
   */
  protected void save() {
    String	filename;

    filename = Environment.getInstance().getCustomPropertiesFilename(NamedSetupsDefinition.KEY);
    if (!Environment.getInstance().write(NamedSetupsDefinition.KEY, m_Model.getProperties())) {
      GUIHelper.showErrorMessage(
	  this,
	  "Error saving named setups to '" + filename + "'!",
	  "Error saving named setups");
    }
    else {
      GUIHelper.showInformationMessage(
	  this,
	  "Named setups have been successfully saved to '" + filename + "'!",
	  "Named setups saved");

      // update model
      NamedSetups.getSingleton().reload();
      m_Model = new Model();
      m_Table.setModel(m_Model);
    }

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

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
 * SpreadSheetPanel.java
 * Copyright (C) 2013-2022 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.core.io.PlaceholderFile;
import adams.data.io.output.CsvSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.RowComparator;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.chooser.SpreadSheetFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.JTableSupporter;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetColumnComboBox;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.core.spreadsheettable.CellRenderingCustomizer;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;
import adams.gui.visualization.core.PopupMenuCustomizer;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Displays a spreadsheet in a table.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetPanel
  extends BasePanel 
  implements MenuBarProvider, SendToActionSupporter, JTableSupporter<SpreadSheetTable> {

  /** for serialization. */
  private static final long serialVersionUID = 8089541494489119743L;

  /** the table for displaying the spreadsheet. */
  protected SpreadSheetTable m_Table;
  
  /** the table model. */
  protected SpreadSheetTableModel m_TableModel;

  /** the filedialog for saving the spreadsheet. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileExit;

  /** the "displayed decimals" menu item. */
  protected JMenuItem m_MenuItemViewDisplayedDecimals;

  /** the "negative background" menu item. */
  protected JMenuItem m_MenuItemViewRendering;

  /** the "positive background" menu item. */
  protected JMenuItem m_MenuItemViewPositiveBackground;

  /** the "show formulas" menu item. */
  protected JMenuItem m_MenuItemViewShowFormulas;
  
  /** the search panel. */
  protected SearchPanel m_PanelSearch;

  /** the panel for the column combobox. */
  protected JPanel m_PanelColumnComboBox;

  /** the combobox for jumping to columns. */
  protected SpreadSheetColumnComboBox m_ColumnComboBox;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new SpreadSheetFileChooser();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {

    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_TableModel = new SpreadSheetTableModel();
    m_Table      = new SpreadSheetTable(m_TableModel);
    add(new BaseScrollPane(m_Table), BorderLayout.CENTER);
    
    m_PanelColumnComboBox = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_ColumnComboBox      = new SpreadSheetColumnComboBox(m_Table);
    m_PanelColumnComboBox.add(m_ColumnComboBox);
    add(m_PanelColumnComboBox, BorderLayout.NORTH);

    m_PanelSearch = new SearchPanel(LayoutType.HORIZONTAL, true);
    m_PanelSearch.setVisible(false);
    m_PanelSearch.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	m_Table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    add(m_PanelSearch, BorderLayout.SOUTH);
  }
  
  /**
   * Sets the spreadsheet to display.
   * 
   * @param value	the spreadsheet to display
   */
  public void setSpreadSheet(SpreadSheet value) {
    m_TableModel = m_TableModel.newModel(value);
    m_Table.setModel(m_TableModel);
  }
  
  /**
   * Returns the spreadsheet currently displayed.
   * 
   * @return		the spreadsheet on display
   */
  public SpreadSheet getSpreadSheet() {
    return m_TableModel.toSpreadSheet();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
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
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // File/Save as
      menuitem = new JMenuItem("Save as...");
      menu.add(menuitem);
      menuitem.setMnemonic('S');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl shift pressed S"));
      menuitem.setIcon(ImageManager.getIcon("save.gif"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  saveAs();
	}
      });
      m_MenuItemFileSaveAs = menuitem;

      // File/Send to
      menu.addSeparator();
      if (SendToActionUtils.addSendToSubmenu(this, menu))
	menu.addSeparator();

      // File/Close
      menuitem = new JMenuItem("Close");
      menu.add(menuitem);
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.setIcon(ImageManager.getIcon("exit.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  close();
	}
      });
      m_MenuItemFileExit = menuitem;

      // View
      menu = new JMenu("View");
      result.add(menu);
      menu.setMnemonic('V');
      menu.addChangeListener(new ChangeListener() {
	@Override
	public void stateChanged(ChangeEvent e) {
	  updateMenu();
	}
      });

      // View/Displayed decimals
      menuitem = new JMenuItem("Decimals...");
      menu.add(menuitem);
      menuitem.setMnemonic('d');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed D"));
      menuitem.setIcon(ImageManager.getIcon("decimal-place.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  enterNumDecimals();
	}
      });
      m_MenuItemViewDisplayedDecimals = menuitem;

      // View/Negative background
      menuitem = new JMenuItem("Rendering...");
      menu.add(menuitem);
      menuitem.setMnemonic('R');
      menuitem.setIcon(ImageManager.getEmptyIcon());
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  selectRendering();
	}
      });
      m_MenuItemViewRendering = menuitem;

      // View/Show formulas
      menuitem = new JCheckBoxMenuItem("Show formulas");
      menu.add(menuitem);
      menuitem.setMnemonic('f');
      menuitem.setIcon(ImageManager.getIcon("formula.png"));
      menuitem.addActionListener(new ActionListener() {
	@Override
	public void actionPerformed(ActionEvent e) {
	  m_Table.setShowFormulas(m_MenuItemViewShowFormulas.isSelected());
	}
      });
      m_MenuItemViewShowFormulas = menuitem;

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
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    if (m_MenuBar == null)
      return;
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    closeParent();
  }

  /**
   * Saves the current sheet.
   */
  protected void saveAs() {
    int			retVal;
    PlaceholderFile	file;

    retVal = m_FileChooser.showSaveDialog(this);
    if (retVal != SpreadSheetFileChooser.APPROVE_OPTION)
      return;

    file = m_FileChooser.getSelectedPlaceholderFile();
    write(m_FileChooser.getWriter(), file);
  }

  /**
   * Saves the specified file.
   *
   * @param writer	the writer to use for saving the file
   * @param file	the file to save
   */
  public void write(SpreadSheetWriter writer, File file) {
    if (!writer.write(m_Table.toSpreadSheet(), file))
      GUIHelper.showErrorMessage(this, "Failed to write spreadsheet to '" + file + "'!");
  }

  /**
   * Alows the user to enter the number of decimals to display.
   */
  protected void enterNumDecimals() {
    String 	valueStr;
    int 	decimals;

    decimals = m_Table.getNumDecimals();
    valueStr = GUIHelper.showInputDialog(
	this, "Please enter the number of decimals to display (-1 to display all):", "" + decimals);
    if (valueStr == null)
      return;

    decimals = Integer.parseInt(valueStr);
    m_Table.setNumDecimals(decimals);
  }

  /**
   * Allows the user to select a different cell rendering customizer.
   */
  protected void selectRendering() {
    CellRenderingCustomizer 	renderer;
    GenericObjectEditorDialog	dialog;

    renderer = m_Table.getCellRenderingCustomizer();

    if (getParentDialog() != null)
      dialog = new GenericObjectEditorDialog(getParentDialog(), ModalityType.DOCUMENT_MODAL);
    else
      dialog = new GenericObjectEditorDialog(getParentFrame(), true);
    dialog.setTitle("Cell rendering customizer");
    dialog.setUISettingsPrefix(CellRenderingCustomizer.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    dialog.getGOEEditor().setClassType(CellRenderingCustomizer.class);
    dialog.setCurrent(renderer);
    dialog.setLocationRelativeTo(dialog.getParent());
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION)
      return;

    renderer = (CellRenderingCustomizer) dialog.getCurrent();
    m_Table.setCellRenderingCustomizer(renderer);
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  @Override
  public Class[] getSendToClasses() {
    return new Class[]{PlaceholderFile.class, JTable.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  @Override
  public boolean hasSendToItem(Class[] cls) {
    return (SendToActionUtils.isAvailable(new Class[]{PlaceholderFile.class, JTable.class}, cls));
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  @Override
  public Object getSendToItem(Class[] cls) {
    Object			result;
    CsvSpreadSheetWriter	writer;
    SpreadSheet			sheet;

    result = null;

    if (SendToActionUtils.isAvailable(PlaceholderFile.class, cls)) {
      sheet  = m_Table.toSpreadSheet();
      result = SendToActionUtils.nextTmpFile("spreadsheetpanel", "csv");
      writer = new CsvSpreadSheetWriter();
      if (!writer.write(sheet, (PlaceholderFile) result))
	result = null;
    }
    else if (SendToActionUtils.isAvailable(JTable.class, cls)) {
      result = m_Table;
    }

    return result;
  }

  /**
   * Sets the popup menu customizer to use for the header.
   *
   * @param value	the customizer, null to remove it
   */
  public void setHeaderPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_Table.setHeaderPopupMenuCustomizer(value);
  }

  /**
   * Returns the current popup menu customizer for the header.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getHeadePopupMenuCustomizer() {
    return m_Table.getHeaderPopupMenuCustomizer();
  }

  /**
   * Sets the popup menu customizer to use for the cells.
   *
   * @param value	the customizer, null to remove it
   */
  public void setCellPopupMenuCustomizer(PopupMenuCustomizer value) {
    m_Table.setCellPopupMenuCustomizer(value);
  }

  /**
   * Returns the current popup menu customizer for the cells.
   *
   * @return		the customizer, null if none set
   */
  public PopupMenuCustomizer getCellPopupMenuCustomizer() {
    return m_Table.getCellPopupMenuCustomizer();
  }

  /**
   * Sets the renderer.
   *
   * @param value	the renderer
   */
  public void setCellRenderingCustomizer(CellRenderingCustomizer value) {
    m_Table.setCellRenderingCustomizer(value);
  }

  /**
   * Returns the renderer.
   *
   * @return		the renderer
   */
  public CellRenderingCustomizer getCellRenderingCustomizer() {
    return m_Table.getCellRenderingCustomizer();
  }

  /**
   * Sets whether to display the formulas or their calculated values.
   *
   * @param value	true if to display the formulas rather than the calculated values
   */
  public void setShowFormulas(boolean value) {
    m_Table.setShowFormulas(value);
  }

  /**
   * Returns whether to display the formulas or their calculated values.
   *
   * @return		true if to display the formulas rather than the calculated values
   */
  public boolean getShowFormulas() {
    return m_Table.getShowFormulas();
  }

  /**
   * Sorts the spreadsheet with the given comparator.
   *
   * @param comparator	the row comparator to use
   */
  public void sort(RowComparator comparator) {
    m_Table.sort(comparator);
  }

  /**
   * Sets the number of decimals to display. Use -1 to display all.
   *
   * @param value	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_Table.setNumDecimals(value);
  }

  /**
   * Returns the currently set number of decimals. -1 if displaying all.
   *
   * @return		the number of decimals
   */
  public int getNumDecimals() {
    return m_Table.getNumDecimals();
  }
  
  /**
   * Sets whether the search is visible.
   * 
   * @param value	true if to show search
   */
  public void setShowSearch(boolean value) {
    m_PanelSearch.setVisible(value);
  }
  
  /**
   * Returns whether the search is visible.
   * 
   * @return 		true if search is shown
   */
  public boolean getShowSearch() {
    return m_PanelSearch.isVisible();
  }

  /**
   * Sets whether the column combobox is visible.
   *
   * @param value	true if to show column combobox
   */
  public void setShowColumnComboBox(boolean value) {
    m_PanelColumnComboBox.setVisible(value);
  }

  /**
   * Returns whether the column combobox is visible.
   *
   * @return 		true if column combobox is shown
   */
  public boolean getShowColumnComboBox() {
    return m_PanelColumnComboBox.isVisible();
  }

  /**
   * Returns the underlying table.
   *
   * @return		the table
   */
  public SpreadSheetTable getTable() {
    return m_Table;
  }
}

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
 * ActorUsage.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractBasicMenuItemDefinition;
import adams.gui.application.UserMode;
import adams.gui.chooser.BaseDirectoryChooser;
import adams.gui.core.BaseTable;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SortableAndSearchableTableWithButtons;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.event.SearchEvent;
import adams.gui.event.SearchListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Scans a user-selected directory for flows and analyzes/displays the actor
 * usage. It also allows the user to edit selected flows.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ActorUsage
  extends AbstractBasicMenuItemDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -6548349613973153076L;

  /** the flow editor for displaying flows. */
  protected adams.gui.flow.FlowEditorPanel m_FlowEditor;

  /**
   * Initializes the menu item.
   */
  public ActorUsage() {
    this(null);
  }

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public ActorUsage(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_FlowEditor = null;
  }
  
  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Actor usage";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "adams.flow.transformer.SetVariable.gif";
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.BASIC;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_HELP;
  }

  /**
   * Lets the user choose the directory with the flows.
   * 
   * @return		the directory, null if dialog canceled
   */
  protected PlaceholderDirectory chooseDir() {
    PlaceholderDirectory	result;
    BaseDirectoryChooser	chooser;
    int				retVal;
    
    result  = null;
    chooser = new BaseDirectoryChooser(new File("."));
    chooser.setDialogTitle("Choose directory with flows");
    retVal  = chooser.showOpenDialog(null);
    if (retVal == BaseDirectoryChooser.APPROVE_OPTION)
      result = chooser.getSelectedDirectory();
    
    return result;
  }

  /**
   * Generates the spreadsheet with the actor usage.
   * 
   * @param dir		the directory to inspect
   * @return		the spreadsheet, null if failed to generate
   */
  protected SpreadSheet determineUsage(PlaceholderDirectory dir) {
    SpreadSheet			result;
    adams.flow.core.ActorUsage	usage;
    
    usage = new adams.flow.core.ActorUsage();
    usage.setRecursive(true);
    usage.setDirectories(new PlaceholderDirectory[]{dir});
    result = usage.execute();
    
    return result;
  }
  
  /**
   * Displays the actor usage.
   * 
   * @param sheet	the sheet with the usage
   */
  protected void displayUsage(SpreadSheet sheet) {
    final SortableAndSearchableTableWithButtons	table;
    SpreadSheetTableModel			model;
    SearchPanel					search;
    final JButton				editFlow;
    
    model = new SpreadSheetTableModel(sheet);
    model.setUseSimpleHeader(true);
    model.setShowRowColumn(false);
    table = new SortableAndSearchableTableWithButtons(model);
    table.setAutoResizeMode(BaseTable.AUTO_RESIZE_OFF);
    table.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    search = new SearchPanel(LayoutType.HORIZONTAL, true);
    search.addSearchListener(new SearchListener() {
      @Override
      public void searchInitiated(SearchEvent e) {
	table.search(e.getParameters().getSearchString(), e.getParameters().isRegExp());
      }
    });
    table.add(search, BorderLayout.SOUTH);
    editFlow = new JButton("Edit");
    editFlow.setMnemonic('E');
    editFlow.setToolTipText("Opens the selected flows with the Flow editor");
    editFlow.setEnabled(false);
    editFlow.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	int[] indices = table.getSelectedRows();
	List<String> flows = new ArrayList<String>();
	for (int i = 0; i < indices.length; i++) {
	  String flow = (String) table.getModel().getValueAt(indices[i], 1);
	  if (flow.length() > 1)
	    flows.add(flow);
	}
	if (m_FlowEditor == null) {
	  FlowEditor editor = new FlowEditor();
	  editor.setOwner(getOwner());
	  editor.setAdditionalParameters(flows.toArray(new String[flows.size()]));
	  editor.launch();
	  m_FlowEditor = editor.getLastWidget();
	}
	else {
	  for (String f: flows)
	    m_FlowEditor.loadUnsafe(new PlaceholderFile(f));
	  GUIHelper.toFront(m_FlowEditor);
	}
      }
    });
    table.addToButtonsPanel(editFlow);
    table.setDoubleClickButton(editFlow);
    table.getComponent().getSelectionModel().addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
	editFlow.setEnabled(table.getSelectedRowCount() > 0);
      }
    });
    createChildFrame(table, GUIHelper.getDefaultDialogDimension());
    table.setOptimalColumnWidth();
  }
  
  /**
   * Launches the functionality of the menu item.
   */
  @Override
  public void launch() {
    PlaceholderDirectory	dir;
    SpreadSheet			sheet;
    
    dir = chooseDir();
    if (dir == null) {
      GUIHelper.showErrorMessage(null, "Canceled selection of flow directory!");
      return;
    }
    
    sheet = determineUsage(dir);
    if (sheet == null) {
      GUIHelper.showErrorMessage(null, "Failed to generate overview!");
      return;
    }
    
    displayUsage(sheet);
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return false;
  }
}
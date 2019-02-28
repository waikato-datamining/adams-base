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
 * DataQueryTab.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.core.MessageCollection;
import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.db.SQLStatement;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanelWithButtons;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentSQLStatementsHandler;
import adams.gui.core.SpreadSheetQueryEditorPanel;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.help.HelpFrame;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.core.Instances;

import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Allows the execution of an SQL-like query to manipulate datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataQueryTab
  extends AbstractInvestigatorTabWithDataTable {

  private static final long serialVersionUID = -4106630131554796889L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "WekaInvestigatorDataQueries.props";

  /** the key for the query. */
  public final static String KEY_QUERY = "query";

  /** the query panel. */
  protected SpreadSheetQueryEditorPanel m_PanelQuery;

  /** the execute button. */
  protected BaseButton m_ButtonExecute;

  /** the clear button. */
  protected BaseButton m_ButtonClear;

  /** the button for the history. */
  protected BaseButton m_ButtonHistory;

  /** the help button. */
  protected BaseButton m_ButtonHelp;

  /** the save button. */
  protected BaseButton m_ButtonSave;

  /** the generated spreadsheet. */
  protected SpreadSheetTable m_TableResult;

  /** whether data was generated. */
  protected boolean m_DataGenerated;

  /** the popup menu for the recent items. */
  protected JPopupMenu m_PopupMenu;

  /** the recent files handler. */
  protected RecentSQLStatementsHandler<JPopupMenu> m_RecentStatementsHandler;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_DataGenerated = false;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    BasePanelWithButtons	panelTop;
    BaseSplitPane		splitPane;

    super.initGUI();

    m_PanelData.setLayout(new BorderLayout());

    splitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    splitPane.setDividerLocation(150);
    splitPane.setUISettingsParameters(getClass(), "divider");
    m_PanelData.add(splitPane, BorderLayout.CENTER);

    panelTop = new BasePanelWithButtons();

    m_PanelQuery = new SpreadSheetQueryEditorPanel();
    m_PanelQuery.setText("SELECT *");
    m_PanelQuery.addChangeListener((ChangeEvent e) -> updateButtons());
    panelTop.add(m_PanelQuery, BorderLayout.CENTER);

    m_ButtonExecute = new BaseButton(GUIHelper.getIcon("run.gif"));
    m_ButtonExecute.setToolTipText("Executes the query on the selected dataset");
    m_ButtonExecute.addActionListener((ActionEvent e) -> executeQuery());
    panelTop.addToButtonsPanel(m_ButtonExecute);

    m_ButtonHistory = new BaseButton(GUIHelper.getIcon("history.png"));
    m_ButtonHistory.setToolTipText("Recent queries");
    m_ButtonHistory.addActionListener((ActionEvent e) -> m_PopupMenu.show(m_ButtonHistory, 0, m_ButtonHistory.getHeight()));
    panelTop.addToButtonsPanel(m_ButtonHistory);

    m_ButtonClear = new BaseButton(GUIHelper.getIcon("new.gif"));
    m_ButtonClear.setToolTipText("Removes the previously generated result");
    m_ButtonClear.addActionListener((ActionEvent e) -> clear());
    panelTop.addToButtonsPanel(m_ButtonClear);

    m_ButtonSave = new BaseButton(GUIHelper.getIcon("save.gif"));
    m_ButtonSave.setToolTipText("Stores the result as a new dataset");
    m_ButtonSave.addActionListener((ActionEvent e) -> saveDataset());
    panelTop.addToButtonsPanel(m_ButtonSave);

    m_ButtonHelp = new BaseButton(GUIHelper.getIcon("help.gif"));
    m_ButtonHelp.setToolTipText("Help screen for the query language");
    m_ButtonHelp.addActionListener((ActionEvent e) -> showHelp());
    panelTop.addToButtonsPanel(m_ButtonHelp);

    splitPane.setTopComponent(panelTop);

    m_TableResult = new SpreadSheetTable(new SpreadSheetTableModel());
    m_TableResult.setShowSimplePopupMenus(true);
    splitPane.setBottomComponent(new BaseScrollPane(m_TableResult));

    m_SplitPane.setBottomComponentHidden(false);

    m_PopupMenu = new JPopupMenu();
    m_RecentStatementsHandler = new RecentSQLStatementsHandler<>(SESSION_FILE, 10, m_PopupMenu);
    m_RecentStatementsHandler.addRecentItemListener(new RecentItemListener<JPopupMenu,SQLStatement>() {
      public void recentItemAdded(RecentItemEvent<JPopupMenu,SQLStatement> e) {
	// ignored
      }
      public void recentItemSelected(RecentItemEvent<JPopupMenu,SQLStatement> e) {
	setStatement(e.getItem());
      }
    });
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    clear();
    updateButtons();
  }

  /**
   * Returns the title of this table.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Data query";
  }

  /**
   * Returns the icon name for the tab icon.
   *
   * @return		the icon name, null if not available
   */
  public String getTabIcon() {
    return "dataquery.png";
  }

  /**
   * Returns whether a readonly table is used.
   *
   * @return		true if readonly
   */
  @Override
  protected boolean hasReadOnlyTable() {
    return true;
  }

  /**
   * Returns the list selection mode to use.
   *
   * @return		the mode
   * @see                ListSelectionModel
   */
  @Override
  protected int getDataTableListSelectionMode() {
    return ListSelectionModel.SINGLE_SELECTION;
  }

  @Override
  public void dataChanged(WekaInvestigatorDataEvent e) {
    super.dataChanged(e);
    dataTableSelectionChanged();
  }

  /**
   * Gets called when the user changes the selection.
   */
  @Override
  protected void dataTableSelectionChanged() {
    updateButtons();
  }

  /**
   * Applies the query to the dataset.
   */
  protected void executeQuery() {
    String			query;
    WekaInstancesToSpreadSheet	conv;
    String			msg;
    SpreadSheet			current;
    SpreadSheet 		generated;

    if (getData().size() != 1)
      return;

    conv = new WekaInstancesToSpreadSheet();
    conv.setInput(getData().get(0).getData());
    msg = conv.convert();
    if (msg != null) {
      logError(msg, "Failed to turn dataset into spreadsheet!");
      return;
    }
    current = (SpreadSheet) conv.getOutput();
    conv.cleanUp();

    query = m_PanelQuery.getQuery().getValue();
    try {
      generated = adams.parser.SpreadSheetQuery.evaluate(query, new HashMap(), current);
      m_RecentStatementsHandler.addRecentItem(new SQLStatement(m_PanelQuery.getContent()));
      m_TableResult.setModel(new SpreadSheetTableModel(generated));
      m_DataGenerated = true;
    }
    catch (Exception e) {
      clear();
      logError("Failed to execute query:\n" + query, e, "Data query failed");
    }

    updateButtons();
  }

  /**
   * Saves the result as a new dataset.
   */
  protected void saveDataset() {
    SpreadSheetToWekaInstances	conv;
    String			msg;
    String			name;
    Instances			newData;
    MemoryContainer		newCont;

    if (!m_DataGenerated)
      return;

    conv = new SpreadSheetToWekaInstances();
    conv.setInput(m_TableResult.toSpreadSheet());
    msg = conv.convert();
    if (msg != null) {
      logError(msg, "Conversion failed!");
      conv.cleanUp();
      return;
    }

    name = m_PanelQuery.getQuery().getValue().replace("\n", " ");
    name = GUIHelper.showInputDialog(this, "Please enter relation name", name);
    if (name == null)
      return;

    newData = (Instances) conv.getOutput();
    newData.setRelationName(name);
    conv.cleanUp();

    newCont = new MemoryContainer(newData);
    getData().add(newCont);
    logMessage("Added query result as " + newCont.getID() + "!");
    fireDataChange(new WekaInvestigatorDataEvent(getOwner(), WekaInvestigatorDataEvent.ROWS_ADDED, getData().size() - 1));
  }

  /**
   * Saves the result as a new dataset.
   */
  protected void clear() {
    m_TableResult.setModel(new SpreadSheetTableModel());
    m_DataGenerated = false;
  }

  /**
   * Displays the help for the queries.
   */
  protected void showHelp() {
    HelpFrame.showHelp(
      adams.parser.SpreadSheetQuery.class.getName(),
      new adams.parser.SpreadSheetQuery().getGrammar(),
      false);
  }

  /**
   * Sets the SQL statement.
   *
   * @param value	the statement to use
   */
  public void setStatement(SQLStatement value) {
    m_PanelQuery.setContent(value.getValue());
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonExecute.setEnabled((getSelectedRows().length == 1) && !m_PanelQuery.getQuery().isEmpty());
    m_ButtonClear.setEnabled(m_DataGenerated);
    m_ButtonSave.setEnabled(m_ButtonExecute.isEnabled() && m_DataGenerated);
  }

  /**
   * Returns the objects for serialization.
   *
   * @param options 	what to serialize
   * @return		the mapping of the objects to serialize
   */
  @Override
  protected Map<String,Object> doSerialize(Set<SerializationOption> options) {
    Map<String,Object>	result;

    result = super.doSerialize(options);
    if (options.contains(SerializationOption.PARAMETERS))
      result.put(KEY_QUERY, m_PanelQuery.getText());

    return result;
  }

  /**
   * Restores the objects.
   *
   * @param data	the data to restore
   * @param errors	for storing errors
   */
  @Override
  protected void doDeserialize(Map<String,Object> data, MessageCollection errors) {
    super.doDeserialize(data, errors);
    if (data.containsKey(KEY_QUERY))
      m_PanelQuery.setText((String) data.get(KEY_QUERY));
  }
}

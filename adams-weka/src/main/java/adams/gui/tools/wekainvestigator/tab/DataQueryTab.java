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
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.wekainvestigator.tab;

import adams.data.conversion.SpreadSheetToWekaInstances;
import adams.data.conversion.WekaInstancesToSpreadSheet;
import adams.data.spreadsheet.SpreadSheet;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanelWithButtons;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetQueryEditorPanel;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.event.WekaInvestigatorDataEvent;
import adams.gui.help.HelpFrame;
import adams.gui.tools.wekainvestigator.data.MemoryContainer;
import weka.core.Instances;

import javax.swing.JLabel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;

/**
 * Allows the execution of an SQL-like query to manipulate datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DataQueryTab
  extends AbstractInvestigatorTabWithDataTable {

  private static final long serialVersionUID = -4106630131554796889L;

  /** the query panel. */
  protected SpreadSheetQueryEditorPanel m_PanelQuery;

  /** the execute button. */
  protected BaseButton m_ButtonExecute;

  /** the clear button. */
  protected BaseButton m_ButtonClear;

  /** the help button. */
  protected BaseButton m_ButtonHelp;

  /** the save button. */
  protected BaseButton m_ButtonSave;

  /** the generated spreadsheet. */
  protected SpreadSheetTable m_TableResult;

  /** whether data was generated. */
  protected boolean m_DataGenerated;

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

    m_ButtonExecute = new BaseButton("Execute");
    m_ButtonExecute.setToolTipText("Executes the query on the selected dataset");
    m_ButtonExecute.addActionListener((ActionEvent e) -> executeQuery());
    panelTop.addToButtonsPanel(m_ButtonExecute);

    m_ButtonClear = new BaseButton("Clear");
    m_ButtonClear.setToolTipText("Removes the previously generated result");
    m_ButtonClear.addActionListener((ActionEvent e) -> clear());
    panelTop.addToButtonsPanel(m_ButtonClear);

    m_ButtonSave = new BaseButton("Save...");
    m_ButtonSave.setToolTipText("Stores the result as a new dataset");
    m_ButtonSave.addActionListener((ActionEvent e) -> saveDataset());
    panelTop.addToButtonsPanel(m_ButtonSave);

    panelTop.addToButtonsPanel(new JLabel(""));

    m_ButtonHelp = new BaseButton("Help");
    m_ButtonHelp.setToolTipText("Help screen for the query language");
    m_ButtonHelp.addActionListener((ActionEvent e) -> showHelp());
    panelTop.addToButtonsPanel(m_ButtonHelp);

    splitPane.setTopComponent(panelTop);

    m_TableResult = new SpreadSheetTable(new SpreadSheetTableModel());
    m_TableResult.setShowSimpleCellPopupMenu(true);
    splitPane.setBottomComponent(new BaseScrollPane(m_TableResult));

    m_SplitPane.setBottomComponentHidden(false);
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
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    m_ButtonExecute.setEnabled((getSelectedRows().length == 1) && !m_PanelQuery.getQuery().isEmpty());
    m_ButtonClear.setEnabled(m_DataGenerated);
    m_ButtonSave.setEnabled(m_ButtonExecute.isEnabled() && m_DataGenerated);
  }
}

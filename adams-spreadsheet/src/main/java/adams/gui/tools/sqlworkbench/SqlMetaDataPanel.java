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
 * SqlMetaDataPanel.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.sqlworkbench;

import adams.core.MessageCollection;
import adams.core.Utils;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.AbstractTypeMapper;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionProvider;
import adams.db.MetaDataType;
import adams.db.MetaDataUtils;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseComboBox;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.BaseTextField;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ParameterPanel;
import adams.gui.core.SQLConnectionPanel;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.event.SearchEvent;
import adams.gui.goe.GenericObjectEditorPanel;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * For querying meta-data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SqlMetaDataPanel
  extends BasePanel
  implements DatabaseConnectionProvider {

  private static final long serialVersionUID = -7292928176878371096L;

  /** the connection panel. */
  protected SQLConnectionPanel m_PanelConnection;

  /** the panel with the table. */
  protected JPanel m_PanelTable;

  /** the select result. */
  protected SpreadSheetTable m_TableResults;

  /** the panel for searching the result. */
  protected SearchPanel m_PanelTableSearch;

  /** the panel with the text. */
  protected JPanel m_PanelText;

  /** for other results. */
  protected BaseTextArea m_TextResults;

  /** the no result panel. */
  protected JPanel m_PanelNoResult;

  /** the panel at the top. */
  protected JPanel m_PanelTop;

  /** the panel at the bottom. */
  protected JPanel m_PanelCenter;

  /** the panel with the parameters. */
  protected ParameterPanel m_PanelParameters;

  /** the combobox with the metadata types. */
  protected BaseComboBox<MetaDataType> m_ComboBoxMetaDataType;

  /** the GOE with the type mapper. */
  protected GenericObjectEditorPanel m_PanelMapper;

  /** the text field with the (optional) table. */
  protected BaseTextField m_TextTable;

  /** the button for executing the query. */
  protected BaseButton m_ButtonExecute;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panelButtons;
    JLabel	label;

    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelTop = new JPanel(new BorderLayout());
    add(m_PanelTop, BorderLayout.NORTH);

    m_PanelCenter = new JPanel(new BorderLayout());
    add(m_PanelCenter, BorderLayout.CENTER);

    m_PanelConnection = new SQLConnectionPanel();
    m_PanelConnection.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_PanelTop.add(m_PanelConnection, BorderLayout.NORTH);

    m_PanelParameters = new ParameterPanel();
    m_PanelTop.add(m_PanelParameters, BorderLayout.CENTER);

    m_ComboBoxMetaDataType = new BaseComboBox<>(MetaDataType.values());
    m_PanelParameters.addParameter("Meta-data", m_ComboBoxMetaDataType);

    m_PanelMapper = new GenericObjectEditorPanel(AbstractTypeMapper.class, new DefaultTypeMapper());
    m_PanelParameters.addParameter("Type mapper", m_PanelMapper);

    m_TextTable = new BaseTextField(20);
    m_PanelParameters.addParameter("Table", m_TextTable);

    panelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelTop.add(panelButtons, BorderLayout.SOUTH);

    m_ButtonExecute = new BaseButton(GUIHelper.getIcon("run.gif"));
    m_ButtonExecute.addActionListener((ActionEvent e) -> execute());
    m_ButtonExecute.setToolTipText("Executes the query (Alt+X)");
    panelButtons.add(m_ButtonExecute);

    m_PanelNoResult = new JPanel();
    m_PanelCenter.add(m_PanelNoResult, BorderLayout.CENTER);

    m_TableResults = new SpreadSheetTable(new SpreadSheetTableModel());
    m_TableResults.setShowSimpleCellPopupMenu(true);
    m_TableResults.setShowSimpleHeaderPopupMenu(true);
    m_TableResults.setUseOptimalColumnWidths(false);
    m_PanelTable = new JPanel(new BorderLayout());
    m_PanelTable.add(new BaseScrollPane(m_TableResults), BorderLayout.CENTER);

    m_PanelTableSearch = new SearchPanel(LayoutType.HORIZONTAL, false);
    m_PanelTableSearch.addSearchListener((SearchEvent e) -> m_TableResults.search(e.getParameters().getSearchString(), e.getParameters().isRegExp()));
    m_PanelTable.add(m_PanelTableSearch, BorderLayout.SOUTH);

    m_TextResults = new BaseTextArea(5, 40);
    m_TextResults.setTextFont(Fonts.getMonospacedFont());
    m_PanelText = new JPanel(new BorderLayout());
    m_PanelText.add(new BaseScrollPane(m_TextResults), BorderLayout.CENTER);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    updateButtons();
  }

  /**
   * Updates the buttons.
   */
  protected void updateButtons() {
    m_ButtonExecute.setEnabled(m_ComboBoxMetaDataType.getSelectedIndex() > -1);
  }

  /**
   * Executes the current query.
   */
  public void execute() {
    final String	query;
    SwingWorker		worker;

    worker = new SwingWorker() {
      protected SpreadSheet m_Sheet = null;
      protected String m_Error = null;
      protected String m_Result = null;
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonExecute.setEnabled(false);
        m_PanelParameters.setEnabled(false);
        m_PanelConnection.setEnabled(false);
        MessageCollection errors = new MessageCollection();
	try {
	  m_Sheet = MetaDataUtils.getMetaData(m_PanelConnection.getDatabaseConnection(), (AbstractTypeMapper) m_PanelMapper.getCurrent(), m_ComboBoxMetaDataType.getSelectedItem(), m_TextTable.getText(), errors);
	  if (!errors.isEmpty())
	    m_Error = errors.toString();
	  else
	    m_Result = "Meta-data query succeeded!";
	}
	catch (Exception e) {
	  m_Error = "Failed to execute meta-data query for:\n\n"
	    + m_ComboBoxMetaDataType.getSelectedItem()
	    + "\n\nException:\n\n"
	    + Utils.throwableToString(e);
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
	m_PanelParameters.setEnabled(true);
	m_ButtonExecute.setEnabled(true);
        m_PanelConnection.setEnabled(true);
	m_PanelCenter.removeAll();
	if (m_Error != null) {
	  m_TextResults.setText(m_Error);
	  m_PanelCenter.add(m_PanelText, BorderLayout.CENTER);
	}
	else {
	  if (m_Sheet != null) {
	    m_TableResults.setModel(new SpreadSheetTableModel(m_Sheet));
	    m_TableResults.setOptimalColumnWidthBounded(150);
	    m_PanelCenter.add(m_PanelTable, BorderLayout.CENTER);
	  }
	  else {
	    m_TextResults.setText(m_Result);
	    m_PanelCenter.add(m_PanelText, BorderLayout.CENTER);
	  }
	}
	m_PanelCenter.invalidate();
	m_PanelCenter.revalidate();
	m_PanelCenter.repaint();
      }
    };
    worker.execute();
  }

  /**
   * Returns the currently used database connection object, can be null.
   *
   * @return		the current object
   */
  public AbstractDatabaseConnection getDatabaseConnection() {
    return m_PanelConnection.getDatabaseConnection();
  }
}

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
 * SqlPanel.java
 * Copyright (C) 2018-2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools.sqlworkbench;

import adams.core.Utils;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.sql.DefaultTypeMapper;
import adams.data.spreadsheet.sql.Reader;
import adams.db.AbstractDatabaseConnection;
import adams.db.DatabaseConnectionProvider;
import adams.db.SQLF;
import adams.db.SQLIntf;
import adams.db.SQLStatement;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.SearchPanel;
import adams.gui.core.SearchPanel.LayoutType;
import adams.gui.core.SpreadSheetTable;
import adams.gui.core.SpreadSheetTableModel;
import adams.gui.dialog.SQLStatementPanel;
import adams.gui.event.SearchEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;

/**
 * For executing a query.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SqlQueryPanel
  extends BasePanel
  implements DatabaseConnectionProvider {

  private static final long serialVersionUID = -7292928176878371096L;

  /** the connection panel. */
  protected SqlConnectionPanel m_PanelConnection;

  /** the split panel. */
  protected BaseSplitPane m_SplitPane;

  /** the query panel. */
  protected SQLStatementPanel m_PanelQuery;

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

  /** the button for executing the query. */
  protected BaseButton m_ButtonExecute;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_PanelConnection = new SqlConnectionPanel();
    m_PanelConnection.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    add(m_PanelConnection, BorderLayout.NORTH);

    m_SplitPane = new BaseSplitPane(BaseSplitPane.VERTICAL_SPLIT);
    m_SplitPane.setDividerLocation(250);
    m_SplitPane.setUISettingsParameters(getClass(), "Divider");
    add(m_SplitPane, BorderLayout.CENTER);

    m_PanelQuery = new SQLStatementPanel();
    m_PanelQuery.addQueryChangeListener((ChangeEvent e) -> updateButtons());
    m_SplitPane.setTopComponent(m_PanelQuery);
    m_PanelQuery.getQueryPanel().getTextPane().addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.VK_X) && (e.getModifiersEx() == KeyEvent.ALT_DOWN_MASK)) {
          e.consume();
          execute();
	}
	if (!e.isConsumed())
	  super.keyPressed(e);
      }
    });

    m_ButtonExecute = new BaseButton(GUIHelper.getIcon("run.gif"));
    m_ButtonExecute.addActionListener((ActionEvent e) -> execute());
    m_ButtonExecute.setToolTipText("Executes the query (Alt+X)");
    m_PanelQuery.getButtonsLeft().add(m_ButtonExecute);

    m_PanelNoResult = new JPanel();
    m_SplitPane.setBottomComponent(m_PanelNoResult);

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
    m_ButtonExecute.setEnabled(!m_PanelQuery.getStatement().isEmpty());
  }

  /**
   * Sets the query.
   *
   * @param query	the query
   */
  public void setQuery(String query) {
    m_PanelQuery.setStatement(new SQLStatement(query));
  }

  /**
   * Returns the query.
   *
   * @return		the query
   */
  public String getQuery() {
    return m_PanelQuery.getStatement().getValue();
  }

  /**
   * Executes the current query.
   */
  public void execute() {
    final String	query;
    SwingWorker		worker;

    query = getQuery().trim();
    if (query.isEmpty())
      return;

    worker = new SwingWorker() {
      protected SpreadSheet m_Sheet = null;
      protected String m_Error = null;
      protected String m_Result= null;
      @Override
      protected Object doInBackground() throws Exception {
        m_ButtonExecute.setEnabled(false);
        m_PanelQuery.setEnabled(false);
        m_PanelConnection.setEnabled(false);
	try {
	  SQLIntf sql = SQLF.getSingleton(m_PanelConnection.getDatabaseConnection());
	  if (query.toLowerCase().startsWith("select ")) {
	    Reader reader = new Reader(new DefaultTypeMapper(), DenseDataRow.class);
	    ResultSet rs = sql.getResultSet(query);
	    m_Sheet = reader.read(rs, 0);
	  }
	  else {
	    if (sql.execute(query))
	      m_Error = "Query generated results unexpectedly!";
	    else
	      m_Result = "Query succeeded!";
	  }
	}
	catch (Exception e) {
	  m_Error = "Failed to execute query:\n\n"
	    + query
	    + "\n\nException:\n\n"
	    + Utils.throwableToString(e);
	}
	return null;
      }

      @Override
      protected void done() {
	super.done();
	m_PanelQuery.setEnabled(true);
	m_ButtonExecute.setEnabled(true);
        m_PanelConnection.setEnabled(true);
	int location = m_SplitPane.getDividerLocation();
	if (m_Error != null) {
	  m_TextResults.setText(m_Error);
	  m_SplitPane.setBottomComponent(m_PanelText);
	}
	else {
	  m_PanelQuery.addStatementToHistory();
	  if (m_Sheet != null) {
	    m_TableResults.setModel(new SpreadSheetTableModel(m_Sheet));
	    m_TableResults.setOptimalColumnWidthBounded(150);
	    m_SplitPane.setBottomComponent(m_PanelTable);
	  }
	  else {
	    m_TextResults.setText(m_Result);
	    m_SplitPane.setBottomComponent(m_PanelText);
	  }
	}
	m_SplitPane.setDividerLocation(location);
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

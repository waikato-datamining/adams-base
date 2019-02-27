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
 * Query.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.tab;

import adams.data.spreadsheet.SpreadSheet;
import adams.db.SQLStatement;
import adams.flow.core.Token;
import adams.flow.transformer.SpreadSheetQuery;
import adams.gui.core.BaseButton;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentSQLStatementsHandler;
import adams.gui.core.SpreadSheetQueryEditorPanel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.help.HelpFrame;
import adams.gui.tools.spreadsheetviewer.MultiPagePane;

import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;

/**
 * Allows the user to run a query on a spreadsheet and create a new transformed
 * sheet using the {@link SpreadSheetQuery} transformer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @see SpreadSheetQuery
 */
public class Query
  extends AbstractViewerTab {

  /** for serialization. */
  private static final long serialVersionUID = -4215008790991120558L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "SpreadsSheetViewerQueries.props";

  /** the table with the information. */
  protected SpreadSheetQueryEditorPanel m_PanelQuery;
  
  /** the help button. */
  protected BaseButton m_ButtonHelp;

  /** the button for the history. */
  protected BaseButton m_ButtonHistory;

  /** the execute button. */
  protected BaseButton m_ButtonExecute;

  /** the popup menu for the recent items. */
  protected JPopupMenu m_PopupMenu;

  /** the recent files handler. */
  protected RecentSQLStatementsHandler<JPopupMenu> m_RecentStatementsHandler;

  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panelButtons;
    JPanel	panelButtonsLeft;
    JPanel	panelButtonsRight;

    super.initGUI();
    
    setLayout(new BorderLayout());
    
    m_PanelQuery = new SpreadSheetQueryEditorPanel();
    m_PanelQuery.setWordWrap(true);
    m_PanelQuery.getTextPane().getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void removeUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void insertUpdate(DocumentEvent e) {
	update();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	update();
      }
      protected void update() {
	m_ButtonExecute.setEnabled(
	       (getCurrentPanel() != null) 
	    && !m_PanelQuery.getQuery().isEmpty());
      }
    });
    add(m_PanelQuery, BorderLayout.CENTER);
    
    panelButtons = new JPanel(new BorderLayout());
    add(panelButtons, BorderLayout.SOUTH);

    panelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelButtons.add(panelButtonsLeft, BorderLayout.WEST);

    panelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panelButtonsRight, BorderLayout.EAST);

    m_ButtonHelp = new BaseButton("Help");
    m_ButtonHelp.setMnemonic('H');
    m_ButtonHelp.addActionListener((ActionEvent e) -> showHelp());
    panelButtonsLeft.add(m_ButtonHelp);

    m_ButtonHistory = new BaseButton(GUIHelper.getIcon("history.png"));
    m_ButtonHistory.setToolTipText("Recent queries");
    m_ButtonHistory.addActionListener((ActionEvent e) -> m_PopupMenu.show(m_ButtonHistory, 0, m_ButtonHistory.getHeight()));
    panelButtonsRight.add(m_ButtonHistory);

    m_ButtonExecute = new BaseButton("Execute");
    m_ButtonExecute.setMnemonic('E');
    m_ButtonExecute.setEnabled(false);
    m_ButtonExecute.addActionListener((ActionEvent e) -> performQuery());
    panelButtonsRight.add(m_ButtonExecute);

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
   * Returns the title of the tab.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Query";
  }

  /**
   * Runs the query on the current sheet.
   */
  protected void performQuery() {
    SpreadSheet		sheet;
    SpreadSheetQuery 	query;
    String		msg;
    Token		output;
    MultiPagePane tabbedPane;
    
    sheet = getCurrentPanel().getSheet();
    query = new SpreadSheetQuery();
    query.setQuery(m_PanelQuery.getQuery());
    msg = query.setUp();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, msg);
      query.cleanUp();
      return;
    }
    query.input(new Token(sheet));
    msg = query.execute();
    if (msg != null) {
      GUIHelper.showErrorMessage(this, msg);
      query.cleanUp();
      return;
    }
    m_RecentStatementsHandler.addRecentItem(new SQLStatement(m_PanelQuery.getContent()));
    output = null;
    if (query.hasPendingOutput())
      output = query.output();
    if (output != null) {
      sheet      = (SpreadSheet) output.getPayload();
      tabbedPane = getOwner().getOwner().getMultiPagePane();
      tabbedPane.addPage(tabbedPane.newTitle(), sheet);
    }
    query.cleanUp();
  }
  
  /**
   * Displays the grammar.
   */
  protected void showHelp() {
    HelpFrame.showHelp(adams.parser.SpreadSheetQuery.class);
  }

  /**
   * Sets the SQL statement.
   *
   * @param value	the statement to use
   */
  public void setStatement(SQLStatement value) {
    m_PanelQuery.setContent(value.getValue());
  }
}

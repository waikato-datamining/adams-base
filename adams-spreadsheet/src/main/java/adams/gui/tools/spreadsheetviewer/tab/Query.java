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
 * Query.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.tools.spreadsheetviewer.tab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import adams.data.spreadsheet.SpreadSheet;
import adams.flow.core.Token;
import adams.flow.transformer.SpreadSheetQuery;
import adams.gui.core.GUIHelper;
import adams.gui.core.SpreadSheetQueryEditorPanel;
import adams.gui.dialog.TextDialog;
import adams.gui.tools.spreadsheetviewer.TabbedPane;

/**
 * Allows the user to run a query on a spreadsheet and create a new transformed
 * sheet using the {@link SpreadSheetQuery} transformer.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see SpreadSheetQuery
 */
public class Query
  extends AbstractViewerTab {

  /** for serialization. */
  private static final long serialVersionUID = -4215008790991120558L;
  
  /** the table with the information. */
  protected SpreadSheetQueryEditorPanel m_PanelQuery;
  
  /** the help button. */
  protected JButton m_ButtonHelp;
  
  /** the execute button. */
  protected JButton m_ButtonExecute;
  
  /**
   * For initializing the GUI.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panelButtons;
    
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
    
    panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelButtons.add(panel, BorderLayout.EAST);

    m_ButtonHelp = new JButton("Help");
    m_ButtonHelp.setMnemonic('H');
    m_ButtonHelp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	showHelp();
      }
    });
    panel.add(m_ButtonHelp);

    m_ButtonExecute = new JButton("Execute");
    m_ButtonExecute.setMnemonic('E');
    m_ButtonExecute.setEnabled(false);
    m_ButtonExecute.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
	performQuery();
      }
    });
    panel.add(m_ButtonExecute);
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
    TabbedPane		tabbedPane;
    
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
    output = null;
    if (query.hasPendingOutput())
      output = query.output();
    if (output != null) {
      sheet      = (SpreadSheet) output.getPayload();
      tabbedPane = getOwner().getOwner().getTabbedPane();
      tabbedPane.addTab(tabbedPane.newTitle(), sheet);
    }
    query.cleanUp();
  }
  
  /**
   * Displays the grammar.
   */
  protected void showHelp() {
    String 	help;
    TextDialog 	dlg;
    
    help = m_PanelQuery.getAdditionalInformation();
    dlg = new TextDialog();
    dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
    dlg.setDialogTitle("Help");
    dlg.setContent(help);
    dlg.setLineWrap(true);
    dlg.setEditable(false);
    dlg.setVisible(true);
  }
}

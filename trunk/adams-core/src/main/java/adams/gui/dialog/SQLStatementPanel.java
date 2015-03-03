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
 * SQLStatementPanel.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import adams.core.AdditionalInformationHandler;
import adams.db.SQLStatement;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentSQLStatementsHandler;
import adams.gui.core.SQLSyntaxEditorPanel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;

/**
 * Panel with SQL statement editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SQLStatementPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -4419661519749458767L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "SQLStatements.props";

  /** the panel with the statement. */
  protected SQLSyntaxEditorPanel m_PanelStatement;

  /** the button for the options. */
  protected JButton m_ButtonOptions;

  /** the button for the history. */
  protected JButton m_ButtonHistory;

  /** the button for displaying the help. */
  protected JButton m_ButtonHelp;

  /** the panel for the buttons at the bottom. */
  protected JPanel m_PanelBottom;

  /** the panel for the buttons on the right. */
  protected JPanel m_PanelButtonsRight;

  /** the panel for the buttons on the left. */
  protected JPanel m_PanelButtonsLeft;
  
  /** the popup menu for the recent items. */
  protected JPopupMenu m_PopupMenu;

  /** the recent files handler. */
  protected RecentSQLStatementsHandler<JPopupMenu> m_RecentStatementsHandler;
  
  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    m_PanelStatement = new SQLSyntaxEditorPanel();
    m_PanelStatement.setWordWrap(true);
    add(m_PanelStatement, BorderLayout.CENTER);

    m_PanelBottom = new JPanel(new BorderLayout());
    add(m_PanelBottom, BorderLayout.SOUTH);
    
    m_PanelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelBottom.add(m_PanelButtonsLeft, BorderLayout.WEST);
    
    m_ButtonOptions = new JButton("...");
    m_ButtonOptions.setToolTipText("Options menu");
    m_ButtonOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	JPopupMenu menu = new JPopupMenu();
	JMenuItem menuitem;
	// cut
	menuitem = new JMenuItem("Cut");
	menuitem.setIcon(GUIHelper.getIcon("cut.gif"));
	menuitem.setEnabled(m_PanelStatement.canCut());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelStatement.cut();
	  }
	});
	menu.add(menuitem);
	// copy
	menuitem = new JMenuItem("Copy");
	menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
	menuitem.setEnabled(m_PanelStatement.canCopy());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelStatement.copy();
	  }
	});
	menu.add(menuitem);
	// paste
	menuitem = new JMenuItem("Paste");
	menuitem.setIcon(GUIHelper.getIcon("paste.gif"));
	menuitem.setEnabled(m_PanelStatement.canPaste());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelStatement.paste();
	  }
	});
	menu.add(menuitem);
	// line wrap
	menuitem = new JCheckBoxMenuItem("Line wrap");
	menuitem.setIcon(GUIHelper.getEmptyIcon());
	menuitem.setSelected(m_PanelStatement.getWordWrap());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelStatement.setWordWrap(((JMenuItem) e.getSource()).isSelected());
	  }
	});
	menu.addSeparator();
	menu.add(menuitem);
	
	menu.show(m_ButtonOptions, 0, m_ButtonOptions.getHeight());
      }
    });
    m_PanelButtonsLeft.add(m_ButtonOptions);
    
    m_ButtonHistory = new JButton(GUIHelper.getIcon("history.png"));
    m_ButtonHistory.setToolTipText("Recent queries");
    m_ButtonHistory.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	m_PopupMenu.show(m_ButtonHistory, 0, m_ButtonHistory.getHeight());
      }
    });
    m_PanelButtonsLeft.add(m_ButtonHistory);

    m_PanelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelBottom.add(m_PanelButtonsRight, BorderLayout.EAST);

    if (m_PanelStatement instanceof AdditionalInformationHandler) {
      m_ButtonHelp = new JButton("Help");
      m_ButtonHelp.setMnemonic('H');
      m_ButtonHelp.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  String help = ((AdditionalInformationHandler) m_PanelStatement).getAdditionalInformation();
	  TextDialog dlg = new TextDialog();
	  dlg.setDefaultCloseOperation(TextDialog.DISPOSE_ON_CLOSE);
	  dlg.setDialogTitle("Help");
	  dlg.setContent(help);
	  dlg.setLineWrap(true);
	  dlg.setEditable(false);
	  dlg.setVisible(true);
	}
      });
      m_PanelButtonsRight.add(m_ButtonHelp);
    }
    
    m_PopupMenu = new JPopupMenu();
    m_RecentStatementsHandler = new RecentSQLStatementsHandler<JPopupMenu>(SESSION_FILE, 5, m_PopupMenu);
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
   * Sets the SQL statement.
   * 
   * @param value	the statement to use
   */
  public void setStatement(SQLStatement value) {
    m_PanelStatement.setContent(value.getValue());
  }
  
  /**
   * Returns the current SQL statement.
   * 
   * @return		the current statement
   */
  public SQLStatement getStatement() {
    return new SQLStatement(m_PanelStatement.getContent());
  }
  
  /**
   * Adds the current statement to the list of recent statements.
   */
  public void addStatementToHistory() {
    m_RecentStatementsHandler.addRecentItem(getStatement());
  }
}

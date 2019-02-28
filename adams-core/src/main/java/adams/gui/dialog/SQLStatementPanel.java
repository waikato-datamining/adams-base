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
 * SQLStatementPanel.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.core.AdditionalInformationHandler;
import adams.db.SQLStatement;
import adams.gui.core.BaseButton;
import adams.gui.core.BaseButtonWithDropDownMenu;
import adams.gui.core.BasePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.RecentSQLStatementsHandler;
import adams.gui.core.SQLSyntaxEditorPanel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.help.HelpFrame;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

/**
 * Panel with SQL statement editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
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
  protected BaseButtonWithDropDownMenu m_ButtonOptions;

  /** the button for the history. */
  protected BaseButton m_ButtonHistory;

  /** the button for displaying the help. */
  protected BaseButton m_ButtonHelp;

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

  /** the list of query change listeners. */
  protected Set<ChangeListener> m_QueryChangeListeners;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_QueryChangeListeners = new HashSet<>();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JMenuItem 	menuitem;

    m_PanelStatement = new SQLSyntaxEditorPanel();
    m_PanelStatement.setWordWrap(true);
    m_PanelStatement.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	notifyQueryChangeListeners();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	notifyQueryChangeListeners();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	notifyQueryChangeListeners();
      }
    });
    add(m_PanelStatement, BorderLayout.CENTER);

    m_PanelBottom = new JPanel(new BorderLayout());
    add(m_PanelBottom, BorderLayout.SOUTH);
    
    m_PanelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelBottom.add(m_PanelButtonsLeft, BorderLayout.WEST);
    
    m_ButtonOptions = new BaseButtonWithDropDownMenu();
    m_ButtonOptions.setToolTipText("Options menu");
    // cut
    menuitem = new JMenuItem("Cut");
    menuitem.setIcon(GUIHelper.getIcon("cut.gif"));
    menuitem.setEnabled(m_PanelStatement.canCut());
    menuitem.addActionListener((ActionEvent e) -> m_PanelStatement.cut());
    m_ButtonOptions.addToMenu(menuitem);
    // copy
    menuitem = new JMenuItem("Copy");
    menuitem.setIcon(GUIHelper.getIcon("copy.gif"));
    menuitem.setEnabled(m_PanelStatement.canCopy());
    menuitem.addActionListener((ActionEvent e) -> m_PanelStatement.copy());
    m_ButtonOptions.addToMenu(menuitem);
    // paste
    menuitem = new JMenuItem("Paste");
    menuitem.setIcon(GUIHelper.getIcon("paste.gif"));
    menuitem.setEnabled(m_PanelStatement.canPaste());
    menuitem.addActionListener((ActionEvent e) -> m_PanelStatement.paste());
    m_ButtonOptions.addToMenu(menuitem);
    // line wrap
    menuitem = new JCheckBoxMenuItem("Line wrap");
    menuitem.setIcon(GUIHelper.getEmptyIcon());
    menuitem.setSelected(m_PanelStatement.getWordWrap());
    menuitem.addActionListener((ActionEvent e) -> m_PanelStatement.setWordWrap(((JMenuItem) e.getSource()).isSelected()));
    m_ButtonOptions.addSeparatorToMenu();
    m_ButtonOptions.addToMenu(menuitem);
    m_PanelButtonsLeft.add(m_ButtonOptions);
    
    m_ButtonHistory = new BaseButton(GUIHelper.getIcon("history.png"));
    m_ButtonHistory.setToolTipText("Recent queries");
    m_ButtonHistory.addActionListener((ActionEvent e) -> m_PopupMenu.show(m_ButtonHistory, 0, m_ButtonHistory.getHeight()));
    m_PanelButtonsLeft.add(m_ButtonHistory);

    m_PanelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelBottom.add(m_PanelButtonsRight, BorderLayout.EAST);

    if (m_PanelStatement instanceof AdditionalInformationHandler) {
      m_ButtonHelp = new BaseButton(GUIHelper.getIcon("help.gif"));
      m_ButtonHelp.addActionListener((ActionEvent e) -> {
	String help = ((AdditionalInformationHandler) m_PanelStatement).getAdditionalInformation();
	HelpFrame.showHelp(SQLStatement.class, help, false);
      });
      m_PanelButtonsRight.add(m_ButtonHelp);
    }
    
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

  /**
   * Returns the panel for the buttons on the left.
   *
   * @return		the panel
   */
  public JPanel getButtonsLeft() {
    return m_PanelButtonsLeft;
  }

  /**
   * Returns the panel for the buttons on the right.
   *
   * @return		the panel
   */
  public JPanel getButtonsRight() {
    return m_PanelButtonsRight;
  }

  /**
   * Returns the query panel.
   *
   * @return		the panel
   */
  public SQLSyntaxEditorPanel getQueryPanel() {
    return m_PanelStatement;
  }

  /**
   * Sets the enabled state.
   *
   * @param value	true if to be enabled
   */
  public void setEnabled(boolean value) {
    super.setEnabled(value);
    m_ButtonOptions.setEnabled(value);
    m_ButtonHistory.setEnabled(value);
    if (m_ButtonHelp != null)
      m_ButtonHelp.setEnabled(value);
    m_PanelStatement.setEnabled(value);
  }

  /**
   * Adds the listener for changes to the query.
   *
   * @param l		the listener to add
   */
  public void addQueryChangeListener(ChangeListener l) {
    m_QueryChangeListeners.add(l);
  }

  /**
   * Removes the listener for changes to the query.
   *
   * @param l		the listener to remove
   */
  public void removeQueryChangeListener(ChangeListener l) {
    m_QueryChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners that the query has changed.
   */
  protected void notifyQueryChangeListeners() {
    ChangeEvent		e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_QueryChangeListeners)
      l.stateChanged(e);
  }
}

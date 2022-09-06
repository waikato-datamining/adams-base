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
 * SpreadSheetQueryPanel.java
 * Copyright (C) 2013-2019 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.dialog;

import adams.core.AdditionalInformationHandler;
import adams.gui.core.BaseButton;
import adams.gui.core.BasePanel;
import adams.gui.core.ImageManager;
import adams.gui.core.RecentSpreadSheetQueriesHandler;
import adams.gui.core.SpreadSheetQueryEditorPanel;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import adams.gui.help.HelpFrame;
import adams.parser.SpreadSheetQuery;
import adams.parser.SpreadSheetQueryText;

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
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;

/**
 * Panel with spreadsheet query editor.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SpreadSheetQueryPanel
  extends BasePanel {

  /** for serialization. */
  private static final long serialVersionUID = -4419661519749458767L;

  /** the file to store the recent queries in. */
  public final static String SESSION_FILE = "SpreadSheetQueries.props";

  /** the panel with the query. */
  protected SpreadSheetQueryEditorPanel m_PanelQuery;

  /** the button for the options. */
  protected BaseButton m_ButtonOptions;

  /** the button for displaying the help. */
  protected BaseButton m_ButtonHelp;

  /** the panel for the buttons at the bottom. */
  protected JPanel m_PanelBottom;

  /** the panel for the buttons on the right. */
  protected JPanel m_PanelButtonsRight;

  /** the panel for the buttons on the left. */
  protected JPanel m_PanelButtonsLeft;

  /** the button for the history. */
  protected BaseButton m_ButtonHistory;

  /** the popup menu for the recent items. */
  protected JPopupMenu m_PopupMenu;

  /** the recent files handler. */
  protected RecentSpreadSheetQueriesHandler<JPopupMenu> m_RecentStatementsHandler;

  /** the change listeners. */
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
    m_PanelQuery = new SpreadSheetQueryEditorPanel();
    m_PanelQuery.setWordWrap(true);
    m_PanelQuery.getDocument().addDocumentListener(new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
	notfyQueryChangeListeners();
      }
      @Override
      public void removeUpdate(DocumentEvent e) {
	notfyQueryChangeListeners();
      }
      @Override
      public void changedUpdate(DocumentEvent e) {
	notfyQueryChangeListeners();
      }
    });
    add(m_PanelQuery, BorderLayout.CENTER);

    m_PanelBottom = new JPanel(new BorderLayout());
    add(m_PanelBottom, BorderLayout.SOUTH);
    
    m_PanelButtonsLeft = new JPanel(new FlowLayout(FlowLayout.LEFT));
    m_PanelBottom.add(m_PanelButtonsLeft, BorderLayout.WEST);
    
    m_ButtonOptions = new BaseButton(ImageManager.getIcon("arrow-head-down.png"));
    m_ButtonOptions.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	JPopupMenu menu = new JPopupMenu();
	JMenuItem menuitem;
	// cut
	menuitem = new JMenuItem("Cut");
	menuitem.setIcon(ImageManager.getIcon("cut.gif"));
	menuitem.setEnabled(m_PanelQuery.canCut());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.cut();
	  }
	});
	menu.add(menuitem);
	// copy
	menuitem = new JMenuItem("Copy");
	menuitem.setIcon(ImageManager.getIcon("copy.gif"));
	menuitem.setEnabled(m_PanelQuery.canCopy());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.copy();
	  }
	});
	menu.add(menuitem);
	// paste
	menuitem = new JMenuItem("Paste");
	menuitem.setIcon(ImageManager.getIcon("paste.gif"));
	menuitem.setEnabled(m_PanelQuery.canPaste());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.paste();
	  }
	});
	menu.add(menuitem);
	// line wrap
	menuitem = new JCheckBoxMenuItem("Line wrap");
	menuitem.setIcon(ImageManager.getEmptyIcon());
	menuitem.setSelected(m_PanelQuery.getWordWrap());
	menuitem.addActionListener(new ActionListener() {
	  @Override
	  public void actionPerformed(ActionEvent e) {
	    m_PanelQuery.setWordWrap(((JMenuItem) e.getSource()).isSelected());
	  }
	});
	menu.addSeparator();
	menu.add(menuitem);
	
	menu.show(m_ButtonOptions, 0, m_ButtonOptions.getHeight());
      }
    });
    m_PanelButtonsLeft.add(m_ButtonOptions);

    m_ButtonHistory = new BaseButton(ImageManager.getIcon("history.png"));
    m_ButtonHistory.setToolTipText("Recent queries");
    m_ButtonHistory.setVisible(false);
    m_ButtonHistory.addActionListener((ActionEvent e) -> m_PopupMenu.show(m_ButtonHistory, 0, m_ButtonHistory.getHeight()));
    m_PanelButtonsLeft.add(m_ButtonHistory);

    m_PopupMenu = new JPopupMenu();
    m_RecentStatementsHandler = new RecentSpreadSheetQueriesHandler<>(SESSION_FILE, 10, m_PopupMenu);
    m_RecentStatementsHandler.addRecentItemListener(new RecentItemListener<JPopupMenu,SpreadSheetQueryText>() {
      public void recentItemAdded(RecentItemEvent<JPopupMenu,SpreadSheetQueryText> e) {
	// ignored
      }
      public void recentItemSelected(RecentItemEvent<JPopupMenu,SpreadSheetQueryText> e) {
	setQuery(e.getItem());
      }
    });

    m_PanelButtonsRight = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    m_PanelBottom.add(m_PanelButtonsRight, BorderLayout.EAST);

    if (m_PanelQuery instanceof AdditionalInformationHandler) {
      m_ButtonHelp = new BaseButton(ImageManager.getIcon("help.gif"));
      m_ButtonHelp.addActionListener((ActionEvent e) -> {
	String help = m_PanelQuery.getAdditionalInformation();
	HelpFrame.showHelp(SpreadSheetQuery.class, help, false);
      });
      m_PanelButtonsRight.add(m_ButtonHelp);
    }
  }
  
  /**
   * Sets the query.
   * 
   * @param value	the query to use
   */
  public void setQuery(SpreadSheetQueryText value) {
    m_PanelQuery.setContent(value.getValue());
  }
  
  /**
   * Returns the current query.
   * 
   * @return		the current query
   */
  public SpreadSheetQueryText getQuery() {
    return new SpreadSheetQueryText(m_PanelQuery.getContent());
  }

  /**
   * Adds the listener for changes in the query.
   *
   * @param l		the listener to add
   */
  public void addQueryChangeListener(ChangeListener l) {
    m_QueryChangeListeners.add(l);
  }

  /**
   * Removes the listener for changes in the query.
   *
   * @param l		the listener to remove
   */
  public void removeQueryChangeListener(ChangeListener l) {
    m_QueryChangeListeners.remove(l);
  }

  /**
   * Notifies all listeners that query has changed.
   */
  protected void notfyQueryChangeListeners() {
    ChangeEvent	e;

    e = new ChangeEvent(this);
    for (ChangeListener l: m_QueryChangeListeners)
      l.stateChanged(e);
  }

  /**
   * Returns the query panel.
   *
   * @return		the panel
   */
  public SpreadSheetQueryEditorPanel getQueryPanel() {
    return m_PanelQuery;
  }

  /**
   * Returns the panel with the left buttons.
   *
   * @return		the panel
   */
  public JPanel getButtonsLeft() {
    return m_PanelButtonsLeft;
  }

  /**
   * Returns the panel with the right buttons.
   *
   * @return		the panel
   */
  public JPanel getButtonsRight() {
    return m_PanelButtonsRight;
  }

  /**
   * Sets whether the history button is visible.
   *
   * @param value	true if visible
   */
  public void setHistoryVisible(boolean value) {
    m_ButtonHistory.setVisible(value);
  }

  /**
   * Returns whether the history button is visible.
   *
   * @return		true if visible
   */
  public boolean isHistoryVisible() {
    return m_ButtonHistory.isVisible();
  }

  /**
   * Adds the current query to the history.
   */
  public void addToHistory() {
    m_RecentStatementsHandler.addRecentItem(getQuery());
  }
}

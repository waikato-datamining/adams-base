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
 * HelpPanel.java
 * Copyright (C) 2017-2021 University of Waikato, Hamilton, NZ
 */

package adams.gui.help;

import adams.core.classmanager.ClassManager;
import adams.core.net.HtmlUtils;
import adams.gui.core.BaseFrame;
import adams.gui.core.BaseHtmlEditorPane;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseSplitPane;
import adams.gui.core.BrowserHelper;
import adams.gui.core.ConsolePanel;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * For displaying help as plain text or html.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class HelpFrame
  extends BaseFrame {

  private static final long serialVersionUID = -2182546218856998120L;

  /**
   * Default handler for hyperlinks. Opens URL in default browser.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   */
  public static class HelpHyperlinkListener
    implements HyperlinkListener {

    /**
     * Called when a hypertext link is updated.
     *
     * @param e the event responsible for the update
     */
    public void hyperlinkUpdate(HyperlinkEvent e) {
      String	classname;
      String	url;

      url       = null;
      classname = null;
      if (e.getURL() != null) {
        url = "" + e.getURL();
        if (HtmlUtils.isClassCrossRefURL(url))
          classname = HtmlUtils.extractClassFromCrossRefURL(url);
      }

      if (e instanceof HTMLFrameHyperlinkEvent) {
	if (e.getSource() instanceof JEditorPane) {
	  JEditorPane editor = (JEditorPane) e.getSource();
	  HTMLDocument doc = (HTMLDocument) editor.getDocument();
	  doc.processHTMLFrameHyperlinkEvent((HTMLFrameHyperlinkEvent) e);
	}
      }
      else if (e.getEventType() == EventType.ACTIVATED) {
        if (classname != null) {
          try {
            showHelp(ClassManager.getSingleton().forName(classname));
	  }
	  catch (Exception ex) {
            ConsolePanel.getSingleton().append("Failed to generate help for class: " + classname, ex);
	  }
	}
	else {
	  try {
	    BrowserHelper.openURL(url);
	  }
	  catch (Exception ex) {
            ConsolePanel.getSingleton().append("Failed to open URL: " + url, ex);
	  }
	}
      }
    }
  }

  /** the singleton. */
  protected static HelpFrame m_Singleton;

  /** for displaying the help. */
  protected HelpHistoryPanel m_PanelHistory;

  /** the split pane. */
  protected BaseSplitPane m_SplitPane;

  /** the editor pane. */
  protected BaseHtmlEditorPane m_Text;

  /** the clear menu item. */
  protected JMenuItem m_MenuItemClear;

  /** the save as menu item. */
  protected JMenuItem m_MenuItemSaveAs;

  /** the close menu item. */
  protected JMenuItem m_MenuItemClose;

  /**
   * Initializes the frame.
   */
  protected HelpFrame() {
    super();
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setTitle("Help");
    setDefaultCloseOperation(BaseFrame.HIDE_ON_CLOSE);
    setLayout(new BorderLayout());

    m_SplitPane = new BaseSplitPane(BaseSplitPane.HORIZONTAL_SPLIT);
    m_SplitPane.setDividerLocation(200);
    add(m_SplitPane, BorderLayout.CENTER);

    m_Text = new BaseHtmlEditorPane();
    m_Text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    m_Text.setFont(Fonts.getMonospacedFont());
    m_Text.addHyperlinkListener(new HelpHyperlinkListener());
    m_Text.addKeyListener(getKeyListener());
    m_Text.setFont(Fonts.getMonospacedFont());
    m_SplitPane.setRightComponent(new BaseScrollPane(m_Text));

    m_PanelHistory = new HelpHistoryPanel();
    m_PanelHistory.setText(m_Text);
    m_PanelHistory.setCaretAtStart(true);
    m_PanelHistory.setAllowSearch(true);
    m_SplitPane.setLeftComponent(m_PanelHistory);

    // menu
    setJMenuBar(createMenu());
  }

  /**
   * Generates the menu.
   */
  protected JMenuBar createMenu() {
    JMenuBar 	result;
    JMenu	menu;
    JMenuItem	item;
    
    result = new JMenuBar();
    
    menu = new JMenu("File");
    menu.setMnemonic('F');
    menu.addChangeListener((ChangeEvent e) -> updateMenu());
    result.add(menu);
    
    item = new JMenuItem("Clear", ImageManager.getIcon("new.gif"));
    item.setMnemonic('l');
    item.addActionListener((ActionEvent e) -> m_PanelHistory.clear());
    menu.add(item);
    m_MenuItemClear = item;
    
    item = new JMenuItem("Save as...", ImageManager.getIcon("save.gif"));
    item.setMnemonic('S');
    item.addActionListener((ActionEvent e) -> {
      if (m_PanelHistory.getSelectedEntry() == null)
        return;
      m_PanelHistory.saveEntry(m_PanelHistory.getSelectedEntry());
    });
    menu.add(item);
    m_MenuItemSaveAs = item;
    
    menu.addSeparator();
    
    item = new JMenuItem("Close", ImageManager.getIcon("exit.png"));
    item.setMnemonic('C');
    item.addActionListener((ActionEvent e) -> setVisible(false));
    menu.add(item);
    m_MenuItemClose = item;
    
    return result;
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    m_MenuItemClear.setEnabled(m_PanelHistory.count() > 0);
    m_MenuItemSaveAs.setEnabled(m_PanelHistory.getSelectedEntry() != null);
  }

  /**
   * Returns the {@link KeyListener} to use for text and button.
   *
   * @return		the listener
   */
  protected KeyListener getKeyListener() {
    return new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	if ((e.getKeyCode() == KeyEvent.VK_W) && e.isControlDown() && !e.isAltDown() && !e.isShiftDown()) {
	  e.consume();
	  setVisible(false);
	}
	else {
	  super.keyPressed(e);
	}
      }
    };
  }

  /**
   * Shows the help.
   *
   * @param key 	the key for the help
   * @param cont	the help
   */
  public void showHelp(String key, HelpContainer cont) {
    m_PanelHistory.addEntry(key, cont);
    m_PanelHistory.setSelectedEntry(key);
  }

  /**
   * Gives access to the singleton.
   *
   * @return		the singleton
   */
  public static synchronized HelpFrame getSingleton() {
    if (m_Singleton == null) {
      m_Singleton = new HelpFrame();
      m_Singleton.setSize(GUIHelper.makeWider(GUIHelper.getDefaultDialogDimension()));
      m_Singleton.setLocationRelativeTo(null);
    }
    return m_Singleton;
  }

  /**
   * Shows the help in the frame.
   *
   * @param cls		the class to generate the help for
   */
  public static void showHelp(Class cls) {
    HelpContainer	cont;

    cont = AbstractHelpGenerator.generateHelp(cls);
    showHelp(cls.getName(), cont.getHelp(), cont.isHtml());
  }

  /**
   * Shows the help in the frame.
   *
   * @param obj		the object to generate the help for
   */
  public static void showHelp(Object obj) {
    HelpContainer	cont;

    cont = AbstractHelpGenerator.generateHelp(obj);
    showHelp(obj.getClass().getName(), cont.getHelp(), cont.isHtml());
  }

  /**
   * Shows the help in the frame.
   *
   * @param cls		the class identifying the help screen
   * @param help	the help to display
   * @param html	true if to display as HTML
   */
  public static void showHelp(Class cls, String help, boolean html) {
    showHelp(cls.getName(), help, html);
  }

  /**
   * Shows the help in the frame.
   *
   * @param key		the key identifying the help screen
   * @param help	the help to display
   * @param html	true if to display as HTML
   */
  public static void showHelp(String key, String help, boolean html) {
    HelpContainer	cont;
    HelpFrame		frame;

    cont = new HelpContainer(help, html);
    frame = getSingleton();
    frame.showHelp(key, cont);
    frame.setVisible(true);
  }

}

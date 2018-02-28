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
 * FileMonitorPanel.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.CleanUpHandler;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.core.RecentFilesHandler;
import adams.gui.core.TitleGenerator;
import adams.gui.event.RecentItemEvent;
import adams.gui.event.RecentItemListener;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;

/**
 * Allows the user to monitor a file for new data being appended, like log files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class FileMonitorPanel
  extends BasePanel
  implements MenuBarProvider, CleanUpHandler {

  private static final long serialVersionUID = -3090959157666111892L;

  /** the file to store the recent files in. */
  public final static String SESSION_FILE = "FileMonitorSession.props";

  public static class Listener
    extends TailerListenerAdapter {

    /** the owner. */
    protected FileMonitorPanel m_Owner;

    /** the monitored file. */
    protected String m_File;

    /**
     * Initializes the owner.
     *
     * @param owner	the owning actor
     */
    public Listener(FileMonitorPanel owner, String file) {
      m_Owner = owner;
      m_File  = file;
    }

    /**
     * Handles a line from a Tailer.
     * @param line the line.
     */
    @Override
    public void handle(String line) {
      m_Owner.addLine(line);
    }

    /**
     * Handles an Exception .
     * @param ex the exception.
     */
    @Override
    public void handle(Exception ex) {
      ConsolePanel.getSingleton().append(Level.SEVERE, "Error tailing file: " + m_File, ex);
    }
  }

  /** for generating the title. */
  protected TitleGenerator m_TitleGenerator;

  /** the filename of the current file. */
  protected File m_CurrentFile;

  /** the filedialog for loading files. */
  protected BaseFileChooser m_FileChooser;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "open" submenu. */
  protected JMenuItem m_MenuItemOpen;

  /** the "open recent" submenu. */
  protected JMenu m_MenuItemOpenRecent;

  /** the "close" submenu. */
  protected JMenuItem m_MenuItemClose;

  /** the recent files handler. */
  protected RecentFilesHandler<JMenu> m_RecentFilesHandler;

  /** for displaying the content. */
  protected BaseTextArea m_TextMonitor;

  /** the tailer instance. */
  protected transient Tailer m_Tailer;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_CurrentFile         = null;
    m_RecentFilesHandler  = null;
    m_TitleGenerator      = new TitleGenerator("Flow runner", true);
    m_FileChooser         = new BaseFileChooser();
    m_FileChooser.addChoosableFileFilter(ExtensionFileFilter.getLogFileFilter());
    m_FileChooser.addChoosableFileFilter(ExtensionFileFilter.getTextFileFilter());
    m_FileChooser.setAcceptAllFileFilterUsed(true);
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    super.initGUI();

    setLayout(new BorderLayout());

    m_TextMonitor = new BaseTextArea(20, 40);
    m_TextMonitor.setTextFont(Fonts.getMonospacedFont());
    add(new BaseScrollPane(m_TextMonitor), BorderLayout.CENTER);
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  @Override
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenu	submenu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // File
      menu = new JMenu("File");
      result.add(menu);
      menu.setMnemonic('F');
      menu.addChangeListener((ChangeEvent e) -> updateMenu());

      menuitem = new JMenuItem("Open...", GUIHelper.getIcon("open.gif"));
      menuitem.setMnemonic('O');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed O"));
      menuitem.addActionListener((ActionEvent e) -> open());
      menu.add(menuitem);
      m_MenuItemOpen = menuitem;

      // File/Recent files
      submenu = new JMenu("Open recent");
      menu.add(submenu);
      m_RecentFilesHandler = new RecentFilesHandler<>(SESSION_FILE, 5, submenu);
      m_RecentFilesHandler.addRecentItemListener(new RecentItemListener<JMenu,File>() {
	public void recentItemAdded(RecentItemEvent<JMenu,File> e) {
	  // ignored
	}
	public void recentItemSelected(RecentItemEvent<JMenu,File> e) {
	  open(e.getItem());
	}
      });
      m_MenuItemOpenRecent = submenu;

      menu.addSeparator();

      menuitem = new JMenuItem("Close", GUIHelper.getIcon("exit.png"));
      menuitem.setMnemonic('C');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
      menuitem.addActionListener((ActionEvent e) -> close());
      menu.add(menuitem);
      m_MenuItemClose = menuitem;

      // update menu
      m_MenuBar = result;
      updateMenu();
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Prompts the user to select a file to monitor.
   */
  protected void open() {
    int		retVal;

    retVal = m_FileChooser.showOpenDialog(this);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    open(m_FileChooser.getSelectedFile());
  }

  /**
   * Monitors the specified file.
   *
   * @param file 	the file to monitor
   */
  protected void open(File file) {
    Listener listener;

    stopListening();
    listener = new Listener(this, file.getAbsolutePath());
    m_Tailer = Tailer.create(file.getAbsoluteFile(), listener, 100, true);
    m_RecentFilesHandler.addRecentItem(file);
    setCurrentFile(file);
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    cleanUp();
    closeParent();
  }

  /**
   * Updates the state of the menu items.
   */
  protected void updateMenu() {
    updateTitle();

    if (m_MenuBar == null)
      return;

    // File
    m_MenuItemOpen.setEnabled(true);
    m_MenuItemOpenRecent.setEnabled(m_RecentFilesHandler.size() > 0);
    m_MenuItemClose.setEnabled(true);
  }

  /**
   * Appends the line from the file.
   *
   * @param line	the line to append
   */
  public void addLine(String line) {
    boolean	end;

    end = (m_TextMonitor.getCaretPosition() == m_TextMonitor.getText().length());
    m_TextMonitor.append(line + "\n");
    if (end)
      m_TextMonitor.setCaretPositionLast();
  }

  /**
   * Returns the title generator in use.
   *
   * @return		the generator
   */
  public TitleGenerator getTitleGenerator() {
    return m_TitleGenerator;
  }

  /**
   * Updates the title of the dialog.
   */
  protected void updateTitle() {
    if (!m_TitleGenerator.isEnabled())
      return;
    setParentTitle(m_TitleGenerator.generate(m_CurrentFile));
  }

  /**
   * Sets the current file.
   *
   * @param value	the file
   */
  protected void setCurrentFile(File value) {
    m_CurrentFile = value;
  }

  /**
   * Returns the current file in use.
   *
   * @return		the current file, can be null
   */
  public File getCurrentFile() {
    return m_CurrentFile;
  }

  /**
   * Stops the file tailer.
   */
  protected void stopListening() {
    if (m_Tailer != null) {
      m_Tailer.stop();
      m_Tailer = null;
    }
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    stopListening();
  }
}

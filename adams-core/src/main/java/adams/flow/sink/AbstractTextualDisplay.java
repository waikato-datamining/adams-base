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
 * AbstractTextualDisplay.java
 * Copyright (C) 2010-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.core.DateUtils;
import adams.core.QuickInfoHelper;
import adams.core.io.ConsoleHelper;
import adams.core.io.FileUtils;
import adams.core.option.UserMode;
import adams.core.option.parsing.FontParsing;
import adams.data.io.output.AbstractTextWriter;
import adams.data.io.output.NullWriter;
import adams.flow.control.Flow;
import adams.flow.core.ClearableDisplay;
import adams.flow.core.FlowControlSubMenuSupporter;
import adams.flow.core.StopHelper;
import adams.flow.core.StopMode;
import adams.flow.core.Token;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.Fonts;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.MenuBarProvider;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Date;

/**
 * Ancestor for actors that display textual stuff.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractTextualDisplay
  extends AbstractDisplay
  implements MenuBarProvider, TextSupplier, SendToActionSupporter, ClearableDisplay,
	       FlowControlSubMenuSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 3852563073987265768L;

  /** the text writer to use. */
  protected AbstractTextWriter m_Writer;

  /** whether to show flow control sub-menu. */
  protected boolean m_ShowFlowControlSubMenu;

  /** the font to use. */
  protected Font m_Font;

  /** whether to always clear the display before a token is processed. */
  protected boolean m_AlwaysClear;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "clear" menu item. */
  protected JMenuItem m_MenuItemFileClear;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the "pause/resume" menu item. */
  protected JMenuItem m_MenuItemFlowPauseResume;

  /** the "stop" menu item. */
  protected JMenuItem m_MenuItemFlowStop;

  /** the filedialog for loading/saving flows. */
  protected transient TextFileChooser m_FileChooser;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "font", "font",
      getDefaultFont());

    if (supportsClear()) {
      m_OptionManager.add(
	"always-clear", "alwaysClear",
	false);
    }

    m_OptionManager.add(
      "show-flow-control-submenu", "showFlowControlSubMenu",
      false).setMinUserMode(UserMode.EXPERT);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Writer = new NullWriter();
  }

  /**
   * Returns (and initializes if necessary) the file chooser.
   *
   * @return 		the file chooser
   */
  protected TextFileChooser getFileChooser() {
    TextFileChooser	fileChooser;
    ExtensionFileFilter	filter;

    if (m_FileChooser == null) {
      fileChooser = new TextFileChooser();
      filter      = null;
      if (this instanceof TextSupplier)
	filter = ((TextSupplier) this).getCustomTextFileFilter();
      if (filter != null) {
	fileChooser.resetChoosableFileFilters();
	fileChooser.addChoosableFileFilter(filter);
	fileChooser.setFileFilter(filter);
	fileChooser.setDefaultExtension(filter.getExtensions()[0]);
      }
      m_FileChooser = fileChooser;
    }

    return m_FileChooser;
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return super.getQuickInfo() + QuickInfoHelper.toString(this, "font", FontParsing.toString(null, m_Font), ", font: ");
  }

  /**
   * Sets the writer.
   *
   * @param value 	the writer
   */
  public void setWriter(AbstractTextWriter value) {
    m_Writer = value;
    reset();
  }

  /**
   * Returns the writer.
   *
   * @return 		the writer
   */
  public AbstractTextWriter getWriter() {
    return m_Writer;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String writerTipText() {
    return "The writer to use for storing the textual output.";
  }

  /**
   * Sets whether to show a flow control sub-menu in the menubar.
   *
   * @param value 	true if to show
   */
  @Override
  public void setShowFlowControlSubMenu(boolean value) {
    m_ShowFlowControlSubMenu = value;
    reset();
  }

  /**
   * Returns whether to show a flow control sub-menu in the menubar.
   *
   * @return 		true if to show
   */
  @Override
  public boolean getShowFlowControlSubMenu() {
    return m_ShowFlowControlSubMenu;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String showFlowControlSubMenuTipText() {
    return "If enabled, adds a flow control sub-menu to the menubar.";
  }

  /**
   * Returns the default font for the dialog.
   *
   * @return		the default font
   */
  protected Font getDefaultFont() {
    return Fonts.getMonospacedFont();
  }

  /**
   * Sets the font of the dialog.
   *
   * @param value 	the font
   */
  public void setFont(Font value) {
    m_Font = value;
    reset();
  }

  /**
   * Returns the currently set font of the dialog.
   *
   * @return 		the font
   */
  public Font getFont() {
    return m_Font;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String fontTipText() {
    return "The font of the dialog.";
  }

  /**
   * Sets whether to always clear the display before processing a token.
   * Only available if supportsClear() returns true.
   *
   * @param value 	if true the display is always cleared
   * @see		#supportsClear()
   */
  public void setAlwaysClear(boolean value) {
    m_AlwaysClear = value;
    reset();
  }

  /**
   * Returns whether the display is always cleared before processing a token.
   * Only available if supportsClear() returns true.
   *
   * @return 		true if the display is always cleared
   */
  public boolean getAlwaysClear() {
    return m_AlwaysClear;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alwaysClearTipText() {
    return "If enabled, the display is always cleared before processing a token.";
  }

  /**
   * Creates the "File" menu.
   *
   * @return		the generated menu
   */
  protected JMenu createFileMenu() {
    JMenu		result;
    JMenuItem		menuitem;
    JCheckBoxMenuItem 	checkMenuitem;

    // File
    result = new JMenu("File");
    result.setMnemonic('F');
    result.addChangeListener((ChangeEvent e) -> updateMenu());

    // File/Clear
    if (supportsClear()) {
      menuitem = new JMenuItem("Clear");
      result.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(ImageManager.getIcon("new.gif"));
      menuitem.addActionListener((ActionEvent e) -> clear());
      m_MenuItemFileClear = menuitem;
    }

    // File/Save As
    menuitem = new JMenuItem("Save as...");
    result.add(menuitem);
    menuitem.setMnemonic('a');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
    menuitem.setIcon(ImageManager.getIcon("save.gif"));
    menuitem.addActionListener((ActionEvent e) -> saveAs());
    m_MenuItemFileSaveAs = menuitem;

    // File/Send to
    result.addSeparator();
    if (SendToActionUtils.addSendToSubmenu(this, result))
      result.addSeparator();

    // File/Keep open
    checkMenuitem = new JCheckBoxMenuItem("Keep open");
    result.add(checkMenuitem);
    checkMenuitem.setMnemonic('K');
    checkMenuitem.addActionListener((ActionEvent e) -> setKeepOpen(!getKeepOpen()));

    // File/Close
    menuitem = new JMenuItem("Close");
    result.add(menuitem);
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
    menuitem.setIcon(ImageManager.getIcon("exit.png"));
    menuitem.addActionListener((ActionEvent e) -> close());
    m_MenuItemFileClose = menuitem;

    return result;
  }

  /**
   * Creates the flow control sub-menu.
   *
   * @return		the menu
   */
  protected JMenu createFlowMenu() {
    JMenu 	result;
    JMenuItem	menuitem;

    // Flow
    result = new JMenu("Flow");
    result.setMnemonic('w');
    result.addChangeListener((ChangeEvent e) -> updateMenu());

    // Flow/PauseResume
    menuitem = new JMenuItem("Pause");
    result.add(menuitem);
    menuitem.setMnemonic('u');
    menuitem.setIcon(ImageManager.getIcon("pause.gif"));
    menuitem.addActionListener((ActionEvent e) -> pauseResumeFlow());
    m_MenuItemFlowPauseResume = menuitem;

    // Flow/Stop
    menuitem = new JMenuItem("Stop");
    result.add(menuitem);
    menuitem.setMnemonic('p');
    menuitem.setIcon(ImageManager.getIcon("stop_blue.gif"));
    menuitem.addActionListener((ActionEvent e) -> stopFlow());
    m_MenuItemFlowStop = menuitem;

    return result;
  }

  /**
   * Determines the index of the menu item in the specified menu.
   *
   * @param menu	the menu to search in
   * @param menuitem	the menu item to get the index for
   * @return		the index, -1 if not found
   */
  protected int indexOfMenuItem(JMenu menu, JMenuItem menuitem) {
    int		result;
    int		i;

    result = -1;

    for (i = 0; i < menu.getItemCount(); i++) {
      if (menu.getItem(i) == menuitem) {
	result = i;
	break;
      }
    }

    return result;
  }

  /**
   * Assembles the menu bar.
   *
   * @return		the menu bar
   */
  protected JMenuBar createMenuBar() {
    JMenuBar	result;

    result = new JMenuBar();
    result.add(createFileMenu());
    if (m_ShowFlowControlSubMenu)
      result.add(createFlowMenu());

    return result;
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    if (m_MenuBar == null) {
      m_MenuBar = createMenuBar();
      updateMenu();
    }

    return m_MenuBar;
  }

  /**
   * updates the enabled state of the menu items.
   */
  protected void updateMenu() {
    int		len;
    String	text;

    if (m_MenuBar == null)
      return;

    text = supplyText();
    if (text == null)
      len = 0;
    else
      len = text.length();

    if (supportsClear())
      m_MenuItemFileClear.setEnabled(len > 0);
    m_MenuItemFileSaveAs.setEnabled(len > 0);

    if (m_MenuItemFlowPauseResume != null) {
      m_MenuItemFlowPauseResume.setEnabled(canPauseOrResume());
      if (m_MenuItemFlowPauseResume.isEnabled()) {
        if (isPaused()) {
          m_MenuItemFlowPauseResume.setText("Resume");
          m_MenuItemFlowPauseResume.setIcon(ImageManager.getIcon("resume.gif"));
        }
        else {
          m_MenuItemFlowPauseResume.setText("Pause");
          m_MenuItemFlowPauseResume.setIcon(ImageManager.getIcon("pause.gif"));
        }
      }
    }
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   * <br><br>
   * Default implementation returns "false".
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsClear() {
    return false;
  }

  /**
   * Clears the display.
   * <br><br>
   * Default implementation does nothing.
   */
  @Override
  public void clear() {
  }

  /**
   * Saves the setups.
   */
  protected void saveAs() {
    int		retVal;
    String	msg;

    retVal = getFileChooser().showSaveDialog(m_Panel);
    if (retVal != BaseFileChooser.APPROVE_OPTION)
      return;

    msg = FileUtils.writeToFileMsg(
      getFileChooser().getSelectedFile().getAbsolutePath(),
      supplyText(),
      false,
      getFileChooser().getEncoding());

    if (msg != null)
      getLogger().severe("Error saving text to '" + getFileChooser().getSelectedFile() + "':\n" + msg);
  }

  /**
   * Closes the dialog or frame.
   */
  protected void close() {
    // explicitly requested close, need to disable flag
    if (m_KeepOpen)
      m_KeepOpen = false;
    m_Panel.closeParent();
  }

  /**
   * Returns whether the flow can be paused/resumed.
   *
   * @return		true if pause/resume available
   */
  protected boolean canPauseOrResume() {
    return (getRoot() instanceof Flow);
  }

  /**
   * Returns whether the flow is currently paused.
   *
   * @return		true if currently paused
   */
  protected boolean isPaused() {
    boolean	result;
    Flow	root;

    result = false;

    if (getRoot() instanceof Flow) {
      root   = (Flow) getRoot();
      result = root.isPaused();
    }

    return result;
  }

  /**
   * Pauses or resumes the flow.
   */
  protected void pauseResumeFlow() {
    Flow	root;

    if (getRoot() instanceof Flow) {
      root = (Flow) getRoot();
      if (root.isPaused())
	root.resumeExecution();
      else
	root.pauseExecution();
    }
  }

  /**
   * Stops the flow.
   */
  protected void stopFlow() {
    getLogger().warning("Flow stopped by user (" + getFullName() + ")");
    StopHelper.stop(this, StopMode.GLOBAL, null);
  }

  /**
   * Returns the text for the menu item.
   *
   * @return		the menu item text, null for default
   */
  public String getCustomSupplyTextMenuItemCaption() {
    return null;
  }

  /**
   * Returns a custom file filter for the file chooser.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		the file filter, null if to use default one
   */
  public ExtensionFileFilter getCustomTextFileFilter() {
    return null;
  }

  /**
   * Returns the text to save.
   *
   * @return		the text, null if no text available
   */
  public abstract String supplyText();

  /**
   * Before the token is displayed.
   *
   * @param token	the token to display
   */
  @Override
  protected void preDisplay(Token token) {
    if (supportsClear() && getAlwaysClear())
      clear();
  }

  /**
   * Returns whether headless execution is supported.
   *
   * @return		true if supported
   */
  @Override
  public boolean supportsHeadlessExecution() {
    return true;
  }

  /**
   * Executes the flow item in headless mode.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecuteHeadless() {
    ConsoleHelper.printlnOut("\n--> " + DateUtils.getTimestampFormatterMsecs().format(new Date()) + "\n");
    ConsoleHelper.printlnOut("" + m_InputToken.getPayload());

    return null;
  }

  /**
   * Removes all graphical components.
   */
  @Override
  protected void cleanUpGUI() {
    super.cleanUpGUI();

    m_MenuBar            = null;
    m_MenuItemFileClear  = null;
    m_MenuItemFileSaveAs = null;
    m_MenuItemFileClose  = null;
  }

  /**
   * Cleans up after the execution has finished. Also removes graphical
   * components.
   */
  @Override
  public void cleanUp() {
    super.cleanUp();

    m_FileChooser = null;
  }

  /**
   * Returns the classes that the supporter generates.
   *
   * @return		the classes
   */
  public Class[] getSendToClasses() {
    return new Class[]{String.class};
  }

  /**
   * Checks whether something to send is available.
   *
   * @param cls		the classes to retrieve the item for
   * @return		true if an object is available for sending
   */
  public boolean hasSendToItem(Class[] cls) {
    return (SendToActionUtils.isAvailable(String.class, cls));
  }

  /**
   * Returns the object to send.
   *
   * @param cls		the classes to retrieve the item for
   * @return		the item to send
   */
  public Object getSendToItem(Class[] cls) {
    String	result;

    result = null;

    if (SendToActionUtils.isAvailable(String.class, cls)) {
      result = supplyText();
      if (result.length() == 0)
	result = null;
    }

    return result;
  }

  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    Runnable	run;

    super.wrapUp();

    if (!(m_Writer instanceof NullWriter) && (supplyText() != null)) {
      run = new Runnable() {
	@Override
	public void run() {
	  try {
	    m_Writer.write(supplyText(), null);
	  }
	  catch (Exception e) {
	    handleException("Failed to write textual output", e);
	  }
	}
      };
      SwingUtilities.invokeLater(run);
    }
  }
}

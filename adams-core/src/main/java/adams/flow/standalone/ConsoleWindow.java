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
 * ConsoleWindow.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.standalone;

import adams.core.QuickInfoHelper;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.flow.core.AbstractDisplay;
import adams.flow.sink.TextSupplier;
import adams.gui.chooser.BaseFileChooser;
import adams.gui.chooser.TextFileChooser;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseScrollPane;
import adams.gui.core.BaseTextArea;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ConsolePanel.OutputType;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.event.ConsolePanelEvent;
import adams.gui.event.ConsolePanelListener;
import adams.gui.sendto.SendToActionSupporter;
import adams.gui.sendto.SendToActionUtils;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;

/**
 <!-- globalinfo-start -->
 * Displays the messages that are output in the system's 'Console window'.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ConsoleWindow
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-short-title &lt;boolean&gt; (property: shortTitle)
 * &nbsp;&nbsp;&nbsp;If enabled uses just the name for the title instead of the actor's full 
 * &nbsp;&nbsp;&nbsp;name.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The width of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 800
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-height &lt;int&gt; (property: height)
 * &nbsp;&nbsp;&nbsp;The height of the dialog.
 * &nbsp;&nbsp;&nbsp;default: 600
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 * <pre>-x &lt;int&gt; (property: x)
 * &nbsp;&nbsp;&nbsp;The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-y &lt;int&gt; (property: y)
 * &nbsp;&nbsp;&nbsp;The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -3
 * </pre>
 * 
 * <pre>-type &lt;INFO|ERROR|DEBUG&gt; [-type ...] (property: types)
 * &nbsp;&nbsp;&nbsp;The types of messages to display.
 * &nbsp;&nbsp;&nbsp;default: INFO, DEBUG, ERROR
 * </pre>
 * 
 * <pre>-font &lt;java.awt.Font&gt; (property: font)
 * &nbsp;&nbsp;&nbsp;The font of the dialog.
 * &nbsp;&nbsp;&nbsp;default: Monospaced-PLAIN-12
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ConsoleWindow
  extends AbstractDisplay
  implements ConsolePanelListener ,MenuBarProvider, TextSupplier, SendToActionSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 4306748076674344994L;

  /** the key for storing the lookup in the backup. */
  public final static String BACKUP_INPUT = "lookup";

  /** the output types to display. */
  protected OutputType[] m_Types;
  
  /** the font to use. */
  protected Font m_Font;

  /** the text area for displaying the messages. */
  protected BaseTextArea m_TextArea;
  
  /** the lookup for the types. */
  protected HashSet<OutputType> m_LookUp;

  /** the menu bar, if used. */
  protected JMenuBar m_MenuBar;

  /** the "clear" menu item. */
  protected JMenuItem m_MenuItemFileClear;

  /** the "save as" menu item. */
  protected JMenuItem m_MenuItemFileSaveAs;

  /** the "exit" menu item. */
  protected JMenuItem m_MenuItemFileClose;

  /** the filedialog for loading/saving flows. */
  protected transient TextFileChooser m_FileChooser;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Displays the messages that are output in the system's 'Console window'.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "type", "types",
	    new OutputType[]{
		OutputType.INFO,
		OutputType.DEBUG,
		OutputType.ERROR,
	    });

    m_OptionManager.add(
	"font", "font",
	getDefaultFont());
  }

  /**
   * Initializes the actor.
   */
  @Override
  protected void reset() {
    super.reset();
    
    m_LookUp = null;
  }
  
  /**
   * Removes entries from the backup.
   */
  @Override
  protected void pruneBackup() {
    super.pruneBackup();
    pruneBackup(BACKUP_INPUT);
  }

  /**
   * Backs up the current state of the actor before update the variables.
   *
   * @return		the backup
   */
  @Override
  protected Hashtable<String,Object> backupState() {
    Hashtable<String,Object>	result;

    result = super.backupState();

    if (m_LookUp != null)
      result.put(BACKUP_INPUT, m_LookUp);

    return result;
  }

  /**
   * Restores the state of the actor before the variables got updated.
   *
   * @param state	the backup of the state to restore from
   */
  @Override
  protected void restoreState(Hashtable<String,Object> state) {
    if (state.containsKey(BACKUP_INPUT)) {
      m_LookUp = (HashSet<OutputType>) state.get(BACKUP_INPUT);
      state.remove(BACKUP_INPUT);
    }

    super.restoreState(state);
  }

  /**
   * Sets the {@link OutputType}s to display.
   *
   * @param value	the types
   */
  public void setTypes(OutputType[] value) {
    m_Types = value;
    reset();
  }

  /**
   * Returns the {@link OutputType}s to display .
   *
   * @return		the types
   */
  public OutputType[] getTypes() {
    return m_Types;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typesTipText() {
    return "The types of messages to display.";
  }

  /**
   * Returns the default font for the dialog.
   *
   * @return		the default font
   */
  protected Font getDefaultFont() {
    return new Font("Monospaced", Font.PLAIN, 12);
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "types", Utils.flatten(m_Types, ", "));
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
   * Clears the content of the panel.
   */
  @Override
  public void clearPanel() {
    if (m_TextArea != null)
      m_TextArea.setText("");
  }

  /**
   * Creates the "File" menu.
   *
   * @return		the generated menu
   */
  protected JMenu createFileMenu() {
    JMenu	result;
    JMenuItem	menuitem;

    // File
    result = new JMenu("File");
    result.setMnemonic('F');
    result.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
	updateMenu();
      }
    });

    // File/Clear
    if (supportsClear()) {
      menuitem = new JMenuItem("Clear");
      result.add(menuitem);
      menuitem.setMnemonic('l');
      menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed N"));
      menuitem.setIcon(GUIHelper.getIcon("new.gif"));
      menuitem.addActionListener(new ActionListener() {
	public void actionPerformed(ActionEvent e) {
	  clear();
	}
      });
      m_MenuItemFileClear = menuitem;
    }

    // File/Save As
    menuitem = new JMenuItem("Save as...");
    result.add(menuitem);
    menuitem.setMnemonic('a');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed S"));
    menuitem.setIcon(GUIHelper.getIcon("save.gif"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	saveAs();
      }
    });
    m_MenuItemFileSaveAs = menuitem;

    // File/Send to
    result.addSeparator();
    if (SendToActionUtils.addSendToSubmenu(this, result))
      result.addSeparator();

    // File/Close
    menuitem = new JMenuItem("Close");
    result.add(menuitem);
    menuitem.setMnemonic('C');
    menuitem.setAccelerator(GUIHelper.getKeyStroke("ctrl pressed Q"));
    menuitem.setIcon(GUIHelper.getIcon("exit.png"));
    menuitem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
	close();
      }
    });
    m_MenuItemFileClose = menuitem;

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
  }

  /**
   * Whether "clear" is supported and shows up in the menu.
   *
   * @return		true if supported
   */
  protected boolean supportsClear() {
    return true;
  }

  /**
   * Clears the display.
   */
  protected void clear() {
    clearPanel();
  }

  /**
   * Saves the setups.
   */
  protected void saveAs() {
    int		retVal;
    String 	msg;

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
    m_Panel.closeParent();
  }

  /**
   * Returns a custom file filter for the file chooser.
   * <p/>
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
  public String supplyText() {
    if (m_TextArea == null)
      return null;
    else
      return m_TextArea.getText();
  }

  /**
   * Creates the panel to display in the dialog.
   *
   * @return		the panel
   */
  @Override
  protected BasePanel newPanel() {
    BasePanel	result;
    
    result     = new BasePanel(new BorderLayout());
    m_TextArea = new BaseTextArea();
    m_TextArea.setFont(m_Font);
    result.add(new BaseScrollPane(m_TextArea), BorderLayout.CENTER);
    
    return result;
  }

  /**
   * Returns a runnable that displays frame, etc.
   * Must call notifyAll() on the m_Self object and set m_Updating to false.
   *
   * @return		the runnable
   */
  @Override
  protected Runnable newDisplayRunnable() {
    Runnable	result;

    result = new Runnable() {
      public void run() {
	if (m_CreateFrame && !m_Frame.isVisible())
	  m_Frame.setVisible(true);
	synchronized(m_Self) {
	  m_Self.notifyAll();
	}
	m_Updating = false;
      }
    };

    return result;
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;
    
    result = super.setUp();
    
    if (result == null) {
      if (!m_Headless) {
	ConsolePanel.getSingleton().addListener(this);
	m_LookUp = new HashSet<OutputType>(Arrays.asList(m_Types));
      }
    }
    
    return result;
  }
  
  /**
   * Gets called when the {@link ConsolePanel} receives a message.
   * 
   * @param e		the generated event
   */
  public void consolePanelMessageReceived(ConsolePanelEvent e) {
    if (m_LookUp.contains(e.getOutputType())) {
      if (m_TextArea != null)
	m_TextArea.append(e.getMessage());
    }
  }
  
  /**
   * Cleans up after the execution has finished.
   */
  @Override
  public void wrapUp() {
    if (!m_Headless)
      ConsolePanel.getSingleton().removeListener(this);
    if (m_LookUp != null)
      m_LookUp = null;
    
    super.wrapUp();
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
}

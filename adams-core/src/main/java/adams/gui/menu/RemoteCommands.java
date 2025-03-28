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
 * RemoteCommands.java
 * Copyright (C) 2016-2017 University of Waikato, Hamilton, New Zealand
 *
 */

package adams.gui.menu;

import adams.core.Properties;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import adams.core.option.UserMode;
import adams.gui.application.AbstractApplicationFrame;
import adams.gui.application.AbstractSubMenuDefinition;
import adams.gui.core.GUIHelper;
import adams.gui.core.ImageManager;
import adams.gui.core.PropertiesParameterPanel;
import adams.gui.core.PropertiesParameterPanel.PropertyType;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.goe.GenericObjectEditorDialog;
import adams.gui.goe.GenericObjectEditorPanel;
import adams.gui.tools.remotecontrolcenter.RemoteControlCenterManagerPanel;
import adams.scripting.ScriptingHelper;
import adams.scripting.command.RemoteCommand;
import adams.scripting.command.basic.SystemInfo;
import adams.scripting.connection.Connection;
import adams.scripting.engine.RemoteScriptingEngine;
import adams.scripting.processor.RemoteCommandProcessor;

import javax.swing.JMenuItem;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

/**
 * Provides submenu for starting/stopping of remote command scripting.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoteCommands
  extends AbstractSubMenuDefinition {

  /** for serialization. */
  private static final long serialVersionUID = -6548349613973153076L;

  public static final String KEY_COMMAND = "command";

  public static final String KEY_CONNECTION = "connection";

  public static final String KEY_COMMANDPROCESSOR = "command processor";

  /** the control center menu item. */
  protected JMenuItem m_MenuItemCC;

  /** the start listening menu item. */
  protected JMenuItem m_MenuItemStart;

  /** the stop listening menu item. */
  protected JMenuItem m_MenuItemStop;

  /** the send menu item. */
  protected JMenuItem m_MenuItemSend;

  /** the last command sent. */
  protected RemoteCommand m_LastCommand;

  /** the last connection used. */
  protected Connection m_LastConnection;

  /** the last command processor used. */
  protected RemoteCommandProcessor m_LastCommandProcessor;

  /**
   * Initializes the menu item.
   *
   * @param owner	the owning application
   */
  public RemoteCommands(AbstractApplicationFrame owner) {
    super(owner);
  }

  /**
   * Initializes members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_LastCommand          = null;
    m_LastConnection       = null;
    m_LastCommandProcessor = null;
  }

  /**
   * Returns the title of the window (and text of menuitem).
   *
   * @return 		the title
   */
  @Override
  public String getTitle() {
    return "Remote commands...";
  }

  /**
   * Returns the file name of the icon.
   *
   * @return		the filename or null if no icon available
   */
  @Override
  public String getIconName() {
    return "remote_command.png";
  }

  /**
   * Returns the user mode, which determines visibility as well.
   *
   * @return		the user mode
   */
  @Override
  public UserMode getUserMode() {
    return UserMode.EXPERT;
  }

  /**
   * Returns the category of the menu item in which it should appear, i.e.,
   * the name of the menu.
   *
   * @return		the category/menu name
   */
  @Override
  public String getCategory() {
    return CATEGORY_PROGRAM;
  }

  /**
   * Whether the panel can only be displayed once.
   *
   * @return		true if the panel can only be displayed once
   */
  @Override
  public boolean isSingleton() {
    return true;
  }

  /**
   * Allows the user to select and configure a scripting engine before
   * starting it.
   */
  protected void startScripting() {
    GenericObjectEditorDialog	dialog;

    dialog = new GenericObjectEditorDialog(getOwner(), true);
    dialog.setTitle("Scripting engine");
    dialog.getGOEEditor().setClassType(RemoteScriptingEngine.class);
    dialog.getGOEEditor().setCanChangeClassInDialog(true);
    if (getOwner().getRemoteScriptingEngine() == null)
      dialog.setCurrent(ScriptingHelper.getSingleton().getDefaultEngine());
    else
      dialog.setCurrent(OptionUtils.shallowCopy(getOwner().getRemoteScriptingEngine()));
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getResult() != GenericObjectEditorDialog.APPROVE_OPTION) {
      dialog.dispose();
      return;
    }
    getOwner().setRemoteScriptingEngine((RemoteScriptingEngine) dialog.getCurrent());
    dialog.dispose();
  }

  /**
   * Allows the sending of a remote command.
   */
  protected void sendCommand() {
    ApprovalDialog		dialog;
    PropertiesParameterPanel	params;
    Properties			props;
    String			msg;

    if (m_LastCommand == null)
      m_LastCommand = new SystemInfo();
    if (m_LastConnection == null)
      m_LastConnection = ScriptingHelper.getSingleton().getDefaultConnection();
    if (m_LastCommandProcessor == null)
      m_LastCommandProcessor = ScriptingHelper.getSingleton().getDefaultProcessor();

    params = new PropertiesParameterPanel();
    params.addPropertyType(KEY_COMMAND, PropertyType.OBJECT_EDITOR);
    params.setChooser(KEY_COMMAND, new GenericObjectEditorPanel(RemoteCommand.class, m_LastCommand, true));
    params.setLabel(KEY_COMMAND, "Command to send");
    params.addPropertyType(KEY_CONNECTION, PropertyType.OBJECT_EDITOR);
    params.setChooser(KEY_CONNECTION, new GenericObjectEditorPanel(Connection.class, m_LastConnection, true));
    params.setLabel(KEY_CONNECTION, "Connection");
    params.addPropertyType(KEY_COMMANDPROCESSOR, PropertyType.OBJECT_EDITOR);
    params.setChooser(KEY_COMMANDPROCESSOR, new GenericObjectEditorPanel(RemoteCommandProcessor.class, m_LastCommandProcessor, true));
    params.setLabel(KEY_COMMANDPROCESSOR, "Processor");

    props = new Properties();
    props.setProperty(KEY_COMMAND, OptionUtils.getCommandLine(m_LastCommand));
    props.setProperty(KEY_CONNECTION, OptionUtils.getCommandLine(m_LastConnection));
    props.setProperty(KEY_COMMANDPROCESSOR, OptionUtils.getCommandLine(m_LastCommandProcessor));
    params.setProperties(props);

    dialog = new ApprovalDialog(getOwner(), true);
    dialog.setTitle("Remote command");
    dialog.getContentPane().add(params, BorderLayout.CENTER);
    dialog.setSize(
      GUIHelper.getInteger("DefaultSmallDialog.Width", 600),
      GUIHelper.getInteger("DefaultSmallDialog.Height", 400) / 2);
    dialog.setLocationRelativeTo(null);
    dialog.setVisible(true);
    if (dialog.getOption() != ApprovalDialog.APPROVE_OPTION) {
      dialog.dispose();
      return;
    }

    // retrieve parameters
    props = params.getProperties();
    dialog.dispose();
    try {
      m_LastCommand = (RemoteCommand) OptionUtils.forCommandLine(
	RemoteCommand.class, props.getProperty(KEY_COMMAND));
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	null,
	"Failed to instantiate command: '" + props.getProperty(KEY_COMMAND) + "'\n"
	  + LoggingHelper.throwableToString(e));
      return;
    }
    try {
      m_LastConnection = (Connection) OptionUtils.forCommandLine(
	Connection.class, props.getProperty(KEY_CONNECTION));
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	null,
	"Failed to instantiate connection: '" + props.getProperty(KEY_CONNECTION) + "'\n"
	  + LoggingHelper.throwableToString(e));
      return;
    }
    try {
      m_LastCommandProcessor = (RemoteCommandProcessor) OptionUtils.forCommandLine(
	RemoteCommandProcessor.class, props.getProperty(KEY_COMMANDPROCESSOR));
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(
	null,
	"Failed to instantiate command processor: '" + props.getProperty(KEY_COMMANDPROCESSOR) + "'\n"
	  + LoggingHelper.throwableToString(e));
      return;
    }

    // send command
    m_LastCommand.setRemoteScriptingEngineHandler(getOwner());
    msg = m_LastConnection.sendRequest(m_LastCommand, m_LastCommandProcessor);
    if (msg != null)
      GUIHelper.showErrorMessage(null, "Failed to send command:\n" + msg);

    // clean up
    m_LastConnection.cleanUp();
  }

  /**
   * Updates the menu items.
   */
  protected void updateMenu() {
    m_MenuItemStop.setEnabled(getOwner().getRemoteScriptingEngine() != null);
  }

  /**
   * Returns the menu items to add to the submenu.
   *
   * @return		the menu items
   */
  @Override
  protected JMenuItem[] getSubMenuItems() {
    JMenuItem[]		result;

    m_MenuItemCC = new JMenuItem("Control center", ImageManager.getIcon("remote_command.png"));
    m_MenuItemCC.addActionListener((ActionEvent e) -> {
      RemoteControlCenterManagerPanel center = new RemoteControlCenterManagerPanel();
      center.setOwner(getOwner());
      createChildFrame(center, GUIHelper.makeWider(GUIHelper.getDefaultLargeDialogDimension()));
    });

    m_MenuItemStart = new JMenuItem("Start listening...", ImageManager.getIcon("run.gif"));
    m_MenuItemStart.addActionListener((ActionEvent e) -> {
      startScripting();
      updateMenu();
    });

    m_MenuItemStop = new JMenuItem("Stop listening", ImageManager.getIcon("stop_blue.gif"));
    m_MenuItemStop.addActionListener((ActionEvent e) -> {
      getOwner().setRemoteScriptingEngine(null);
      updateMenu();
    });

    m_MenuItemSend = new JMenuItem("Send...", ImageManager.getIcon("remote_command_execute.png"));
    m_MenuItemSend.addActionListener((ActionEvent e) -> {
      sendCommand();
      updateMenu();
    });

    updateMenu();

    result = new JMenuItem[]{
      m_MenuItemCC,
      m_MenuItemStart,
      m_MenuItemStop,
      m_MenuItemSend,
    };

    return result;
  }
}
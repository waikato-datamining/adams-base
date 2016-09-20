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
 * FileCommanderPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.MessageCollection;
import adams.core.StatusMessageHandlerExt;
import adams.core.StoppableWithFeedback;
import adams.core.io.FileObject;
import adams.core.io.fileoperations.FileOperations;
import adams.core.io.fileoperations.LocalFileOperations;
import adams.core.io.fileoperations.Operation;
import adams.core.io.fileoperations.RemoteDirection;
import adams.core.io.fileoperations.RemoteFileOperations;
import adams.core.io.fileoperations.RemoteToRemoteFileOperations;
import adams.core.logging.LoggingLevel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.tools.filecommander.AbstractFileCommanderAction;
import adams.gui.tools.filecommander.Actions;
import adams.gui.tools.filecommander.FileCommanderDirectoryPanel;
import com.jidesoft.swing.JideButton;
import com.jidesoft.swing.JideSplitButton;

import javax.swing.BorderFactory;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * File manager with two-pane interface, similar to the Midnight Commander.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileCommanderPanel
  extends BasePanel
  implements MenuBarProvider, StatusMessageHandlerExt, StoppableWithFeedback {

  private static final long serialVersionUID = 3894304347424478383L;

  /** the left panel. */
  protected FileCommanderDirectoryPanel m_PanelLeft;

  /** the right panel. */
  protected FileCommanderDirectoryPanel m_PanelRight;

  /** the active panel. */
  protected FileCommanderDirectoryPanel m_FilesActive;

  /** the inactive panel. */
  protected FileCommanderDirectoryPanel m_FilesInactive;

  /** the panel with the buttons. */
  protected JPanel m_PanelButtons;

  /** the button for reloading the files. */
  protected JideButton m_ButtonReload;

  /** the button for renaming. */
  protected JideButton m_ButtonRename;

  /** the button for viewing the file. */
  protected JideButton m_ButtonView;

  /** the button for copying the file. */
  protected JideButton m_ButtonCopy;

  /** the button for moving. */
  protected JideButton m_ButtonMove;

  /** the button for creating a directory. */
  protected JideButton m_ButtonMkDir;

  /** the button for deleting. */
  protected JideButton m_ButtonDelete;

  /** the action button. */
  protected JideSplitButton m_ButtonAction;

  /** the button for stopping an operation. */
  protected JideButton m_ButtonStop;

  /** the button for quitting. */
  protected JideButton m_ButtonQuit;

  /** the statusbar. */
  protected BaseStatusBar m_StatusBar;

  /** the menubar. */
  protected JMenuBar m_MenuBar;

  /** whether left panel shows hidden files. */
  protected JMenuItem m_MenuItemLeftShowHidden;

  /** whether right panel shows hidden files. */
  protected JMenuItem m_MenuItemRightShowHidden;

  /** the worker thread. */
  protected SwingWorker m_Worker;

  /** the available actions. */
  protected List<AbstractFileCommanderAction> m_Actions;

  /** the current file operations. */
  protected FileOperations m_FileOperations;

  /** whether the operation was stopped by the user. */
  protected boolean m_Stopped;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    Class[]			classes;
    AbstractFileCommanderAction action;

    super.initialize();

    m_FilesActive    = null;
    m_FilesInactive  = null;
    m_Worker         = null;
    m_Stopped        = false;
    m_FileOperations = new LocalFileOperations();
    m_Actions        = new ArrayList<>();
    classes          = AbstractFileCommanderAction.getActions();
    for (Class cls: classes) {
      try {
	action = (AbstractFileCommanderAction) cls.newInstance();
	action.setOwner(this);
	m_Actions.add(action);
      }
      catch (Exception e) {
	ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, "Failed to instantiate action: " + cls.getName(), e);
      }
    }
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;
    JPanel	panelAll;

    super.initGUI();

    setLayout(new BorderLayout());

    panelAll = new JPanel(new BorderLayout());
    add(panelAll, BorderLayout.CENTER);

    panel = new JPanel(new GridLayout(1, 2, 5, 5));
    panelAll.add(panel, BorderLayout.CENTER);

    // left
    m_PanelLeft = new FileCommanderDirectoryPanel();
    m_PanelLeft.setOwner(this);
    m_PanelLeft.addChooserChangeListeners((ChangeEvent e) ->
      chooserChanged((FileCommanderDirectoryPanel) e.getSource()));
    panel.add(m_PanelLeft);

    // right
    m_PanelRight = new FileCommanderDirectoryPanel();
    m_PanelRight.setOwner(this);
    m_PanelRight.addChooserChangeListeners((ChangeEvent e) ->
      chooserChanged((FileCommanderDirectoryPanel) e.getSource()));
    panel.add(m_PanelRight);

    // buttons
    m_PanelButtons = new JPanel(new FlowLayout());
    panelAll.add(m_PanelButtons, BorderLayout.SOUTH);

    m_ButtonReload = new JideButton("Reload");
    m_ButtonReload.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonReload.addActionListener((ActionEvent) -> reload());
    m_PanelButtons.add(m_ButtonReload);

    m_ButtonRename = new JideButton("Rename");
    m_ButtonRename.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonRename.addActionListener((ActionEvent) -> rename());
    m_PanelButtons.add(m_ButtonRename);

    m_ButtonView = new JideButton("View");
    m_ButtonView.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonView.addActionListener((ActionEvent) -> view());
    m_PanelButtons.add(m_ButtonView);

    m_ButtonCopy = new JideButton("Copy");
    m_ButtonCopy.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonCopy.addActionListener((ActionEvent) -> copy());
    m_PanelButtons.add(m_ButtonCopy);

    m_ButtonMove = new JideButton("Move");
    m_ButtonMove.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonMove.addActionListener((ActionEvent) -> move());
    m_PanelButtons.add(m_ButtonMove);

    m_ButtonMkDir = new JideButton("MkDir");
    m_ButtonMkDir.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonMkDir.addActionListener((ActionEvent) -> mkdir());
    m_PanelButtons.add(m_ButtonMkDir);

    m_ButtonDelete = new JideButton("Delete");
    m_ButtonDelete.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonDelete.addActionListener((ActionEvent) -> delete());
    m_PanelButtons.add(m_ButtonDelete);

    // only show actions button if actual actions present
    // discounting the dummy "Actions" action
    if (m_Actions.size() > 1) {
      m_ButtonAction = new JideSplitButton();
      m_ButtonAction.setAlwaysDropdown(false);
      m_ButtonAction.setButtonEnabled(true);
      m_ButtonAction.setButtonStyle(JideSplitButton.TOOLBOX_STYLE);
      for (AbstractFileCommanderAction action: m_Actions) {
	if (action.getClass() == Actions.class)
	  m_ButtonAction.setAction(action);
	else
	  m_ButtonAction.add(action);
      }
      m_PanelButtons.add(m_ButtonAction);
    }

    m_ButtonStop = new JideButton("Stop");
    m_ButtonStop.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonStop.addActionListener((ActionEvent) -> stopExecution());
    m_PanelButtons.add(m_ButtonStop);

    m_ButtonQuit = new JideButton("Quit");
    m_ButtonQuit.setButtonStyle(JideButton.TOOLBOX_STYLE);
    m_ButtonQuit.addActionListener((ActionEvent) -> quit());
    m_PanelButtons.add(m_ButtonQuit);

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);
  }

  /**
   * Finishes the initialization.
   */
  @Override
  protected void finishInit() {
    super.finishInit();
    setActive(m_PanelLeft);
    updateButtons();
  }

  /**
   * Updates the active state of the panel.
   *
   * @param active	the new active panel
   */
  public void setActive(FileCommanderDirectoryPanel active) {
    m_FilesActive = active;
    if (m_PanelLeft == active)
      m_FilesInactive = m_PanelRight;
    else
      m_FilesInactive = m_PanelLeft;

    m_FilesInactive.getFilePanel().clearSelection();
    m_FilesActive.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
    m_FilesInactive.setBorder(BorderFactory.createLineBorder(m_FilesActive.getBackground(), 3));

    updateFileOperations();
    updateButtons();
  }

  /**
   * Updates the state of the buttons.
   */
  protected void updateButtons() {
    boolean 	hasActive;
    boolean	busy;
    File[]	activeFiles;

    hasActive   = (m_FilesActive != null);
    busy        = isBusy();
    if (hasActive)
      activeFiles = m_FilesActive.getFilePanel().getSelectedFiles();
    else
      activeFiles = new File[0];

    m_ButtonRename.setEnabled(!busy && hasActive && (activeFiles.length == 1) && m_FileOperations.isSupported(Operation.RENAME));
    m_ButtonView.setEnabled(!busy && hasActive && (activeFiles.length == 1));
    m_ButtonCopy.setEnabled(!busy && hasActive && (activeFiles.length > 0) && m_FileOperations.isSupported(Operation.COPY));
    m_ButtonMove.setEnabled(!busy && hasActive && (activeFiles.length > 0) && m_FileOperations.isSupported(Operation.MOVE));
    m_ButtonMkDir.setEnabled(!busy && hasActive && m_FileOperations.isSupported(Operation.MKDIR));
    m_ButtonDelete.setEnabled(!busy && hasActive && (activeFiles.length > 0) && m_FileOperations.isSupported(Operation.DELETE));
    m_ButtonStop.setEnabled(busy);
    m_ButtonQuit.setEnabled(!busy);
    for (AbstractFileCommanderAction action: m_Actions)
      action.update();
  }

  /**
   * Creates a menu bar (singleton per panel object). Can be used in frames.
   *
   * @return		the menu bar
   */
  public JMenuBar getMenuBar() {
    JMenuBar	result;
    JMenu	menu;
    JMenuItem	menuitem;

    if (m_MenuBar == null) {
      result = new JMenuBar();

      // left
      menu = new JMenu("Left");
      menu.setMnemonic('L');
      result.add(menu);

      menuitem = new JMenuItem("Filter...");
      menuitem.addActionListener((ActionEvent e) -> setFilter(true));
      menu.add(menuitem);

      menuitem = new JCheckBoxMenuItem("Show hidden");
      menuitem.addActionListener((ActionEvent e) -> m_PanelLeft.getFilePanel().setShowHidden(m_MenuItemLeftShowHidden.isSelected()));
      menu.add(menuitem);
      m_MenuItemLeftShowHidden = menuitem;

      // right
      menu = new JMenu("Right");
      menu.setMnemonic('R');
      result.add(menu);

      menuitem = new JMenuItem("Filter...");
      menuitem.addActionListener((ActionEvent e) -> setFilter(false));
      menu.add(menuitem);

      menuitem = new JCheckBoxMenuItem("Show hidden");
      menuitem.addActionListener((ActionEvent e) -> m_PanelRight.getFilePanel().setShowHidden(m_MenuItemRightShowHidden.isSelected()));
      menu.add(menuitem);
      m_MenuItemRightShowHidden = menuitem;

      m_MenuBar = result;
    }
    else {
      result = m_MenuBar;
    }

    return result;
  }

  /**
   * Returns whether we're currently busy.
   *
   * @return		true if busy
   */
  public boolean isBusy() {
    return (m_Worker != null);
  }

  /**
   * Sets the current directory.
   *
   * @param dir		the directory to use
   * @param left	if true the left panel gets updated
   */
  public void setDirectory(String dir, boolean left) {
    if (left)
      m_PanelLeft.setDirectory(dir);
    else
      m_PanelRight.setDirectory(dir);
  }

  /**
   * Returns the current directory.
   *
   * @param left	if true the directory of the left panel is retrieved
   * @return		the current directory
   */
  public String getDirectory(boolean left) {
    if (left)
      return m_PanelLeft.getDirectory();
    else
      return m_PanelRight.getDirectory();
  }

  /**
   * Reloads the files.
   */
  protected void reload() {
    m_PanelLeft.reload();
    m_PanelRight.reload();
  }

  /**
   * Views the selected file.
   */
  protected void view() {
    if (m_FilesActive == null)
      return;
    m_FilesActive.view();
  }

  /**
   * Copies the selected files to the other directory.
   */
  public void copy() {
    FileObject[]	files;
    String		input;
    String		target;
    int			retVal;

    m_Stopped = false;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getFilePanel().getSelectedFileObjects();
    if (files.length == 0)
      return;

    if (files.length == 1)
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to copy\n" + files[0]
	  + "\nto\n" + m_FilesInactive.getFilePanel().getCurrentDir() + "?");
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to copy " + files.length + " file" + (files.length > 1 ? "s" : "")
	  + "\nfrom\n" + m_FilesActive.getFilePanel().getCurrentDir()
	  + "\nto\n" + m_FilesInactive.getFilePanel().getCurrentDir() + "?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    if (files.length > 1) {
      if (m_FilesActive.getFilePanel().getCurrentDir().equals(m_FilesInactive.getFilePanel().getCurrentDir())) {
	GUIHelper.showErrorMessage(this, "Source and target directory are the same, cannot copy files!");
	return;
      }
      m_Worker = new SwingWorker() {
	protected MessageCollection errors = new MessageCollection();
	@Override
	protected Object doInBackground() throws Exception {
	  String target = m_FilesInactive.getFilePanel().getCurrentDir();
	  for (int i = 0; i < files.length; i++) {
	    if (m_Stopped)
	      break;
	    FileObject file = files[i];
	    String targetFile = target + "/" + file.getName();
	    m_StatusBar.showStatus("Copying " + (i+1) + "/" + files.length + ": " + file);
	    try {
	      String msg = m_FileOperations.copy(file.toString(), targetFile);
	      if (msg != null)
		errors.add("Failed to copy " + file + " to " + targetFile + ":\n" + msg);
	    }
	    catch (Exception e) {
	      errors.add("Failed to copy " + files[0] + " to " + targetFile + "!", e);
	    }
	  }
	  return null;
	}
	@Override
	protected void done() {
	  super.done();
	  m_Worker = null;
	  if (m_Stopped)
	    m_StatusBar.showStatus("User stopped copying files!");
	  else
	    m_StatusBar.showStatus("");
	  updateButtons();
	  if (!errors.isEmpty())
	    GUIHelper.showErrorMessage(FileCommanderPanel.this, errors.toString());
	  reload();
	}
      };
      m_Worker.execute();
    }
    else {
      input = GUIHelper.showInputDialog(this, "Please enter new file name", files[0].getName());
      if (input == null)
	return;
      if (m_FilesActive.getFilePanel().getCurrentDir().equals(m_FilesInactive.getFilePanel().getCurrentDir()) && input.equals(files[0].getName())) {
	showStatus("Cannot copy file unto itself!");
	return;
      }
      target = m_FilesInactive.getFilePanel().getCurrentDir();
      String targetFile = target + "/" + input;
      try {
	String msg = m_FileOperations.copy(files[0].toString(), targetFile);
	if (msg != null)
	  GUIHelper.showErrorMessage(this, "Failed to copy " + files[0] + " to " + targetFile + ":\n" + msg);
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(this, "Failed to copy " + files[0] + " to " + targetFile + "!", e);
      }
    }

    reload();
  }

  /**
   * Renames a file.
   */
  public void rename() {
    FileObject[]	files;
    String		input;
    String		target;
    int			retVal;
    String		msg;

    m_Stopped = false;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getFilePanel().getSelectedFileObjects();
    if (files.length != 1)
      return;

    retVal = GUIHelper.showConfirmMessage(
      this, "Do you want to rename the following file?\n" + files[0]);
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    input = GUIHelper.showInputDialog(this, "Please enter new name", files[0].getName());
    if ((input == null) || input.equals(files[0].getName()))
      return;
    target = m_FilesActive.getDirectory() + File.separator + input;
    try {
      msg = m_FileOperations.rename(files[0].toString(), target);
      if (msg != null)
	GUIHelper.showErrorMessage(this, "Failed to rename " + files[0] + " to " + target + ":\n" + msg);
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(this, "Failed to rename " + files[0] + " to " + target + "!", e);
    }

    reload();
  }

  /**
   * Moves multiple selected files to the other directory.
   */
  public void move() {
    FileObject[]	files;
    int			retVal;

    m_Stopped = false;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getFilePanel().getSelectedFileObjects();
    if (files.length == 0)
      return;
    if (m_FilesActive.getFilePanel().getCurrentDir().equals(m_FilesInactive.getFilePanel().getCurrentDir())) {
      GUIHelper.showErrorMessage(this, "Source and target directory are the same, cannot move files!");
      return;
    }

    if (files.length == 1)
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to move the following file?\n" + files[0]);
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to move " + files.length + " file" + (files.length > 1 ? "s" : "")
	  + "\nfrom\n" + m_FilesActive.getFilePanel().getCurrentDir()
	  + "\nto\n" + m_FilesInactive.getFilePanel().getCurrentDir() + "?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    m_Worker = new SwingWorker() {
      protected MessageCollection errors = new MessageCollection();
      @Override
      protected Object doInBackground() throws Exception {
	String target = m_FilesInactive.getFilePanel().getCurrentDir();
	for (int i = 0; i < files.length; i++) {
	  if (m_Stopped)
	    break;
	  FileObject file = files[i];
	  m_StatusBar.showStatus("Moving " + (i+1) + "/" + files.length + ": " + file);
	  try {
	    String msg = m_FileOperations.move(file.toString(), target);
	    if (msg != null)
	      errors.add("Failed to move " + file + " to " + target + ":\n" + msg);
	  }
	  catch (Exception e) {
	    errors.add("Failed to move " + file + " to " + target + "!", e);
	  }
	}
	return null;
      }
      @Override
      protected void done() {
	super.done();
	m_Worker = null;
	if (m_Stopped)
	  m_StatusBar.showStatus("User stopped moving files!");
	else
	  m_StatusBar.showStatus("");
	updateButtons();
	if (!errors.isEmpty())
	  GUIHelper.showErrorMessage(FileCommanderPanel.this, errors.toString());
	reload();
      }
    };
    m_Worker.execute();

    reload();
  }

  /**
   * Creates a new directory.
   */
  public void mkdir() {
    String	input;
    String	dir;
    FileObject	dirNew;
    String	msg;

    m_Stopped = false;

    if (m_FilesActive == null)
      return;

    dir   = m_FilesActive.getFilePanel().getCurrentDir();
    input = GUIHelper.showInputDialog(this, "Please enter name for new directory");
    if (input == null)
      return;

    dirNew = m_FilesActive.getDirectoryLister().newDirectory(dir + File.separator + input);
    msg    = m_FileOperations.mkdir(dirNew.toString());
    if (msg != null)
      GUIHelper.showErrorMessage(this, "Failed to create directory: " + dirNew + "\n" + msg);
    else
      reload();
  }

  /**
   * Deletes the selected files.
   */
  public void delete() {
    FileObject[]	files;
    int			retVal;

    m_Stopped = false;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getFilePanel().getSelectedFileObjects();
    if (files.length == 0)
      return;

    if (files.length == 1)
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to delete the following file?\n" + files[0]);
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to delete " + files.length + " file" + (files.length > 1 ? "s" : "")
	  + " from\n" + m_FilesActive.getFilePanel().getCurrentDir() + "?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    m_Worker = new SwingWorker() {
      protected MessageCollection errors = new MessageCollection();
      @Override
      protected Object doInBackground() throws Exception {
	updateButtons();
	for (int i = 0; i < files.length; i++) {
	  if (m_Stopped)
	    break;
	  FileObject file = files[i];
	  m_StatusBar.showStatus("Deleting " + (i+1) + "/" + files.length + ": " + file);
	  try {
	    String msg = m_FileOperations.delete(file.toString());
	    if (msg != null)
	      errors.add("Failed to delete: " + file + "\n" + msg);
	  }
	  catch (Exception e) {
	    errors.add("Failed to delete: " + file, e);
	  }
	}
	return null;
      }
      @Override
      protected void done() {
	super.done();
	m_Worker = null;
	if (m_Stopped)
	  m_StatusBar.showStatus("User stopped deleting files!");
	else
	  m_StatusBar.showStatus("");
	updateButtons();
	if (!errors.isEmpty())
	  GUIHelper.showErrorMessage(FileCommanderPanel.this, "Failed to delete file(s)!\n" + errors);
	reload();
      }
    };
    m_Worker.execute();
  }

  /**
   * Closes the commander.
   */
  public void quit() {
    closeParent();
  }

  /**
   * Prompts the user to enter a regular expression as filter.
   *
   * @param left	whether to apply it to the left or right panel
   */
  protected void setFilter(boolean left) {
    if (left)
      m_PanelLeft.setFilter();
    else
      m_PanelRight.setFilter();
  }

  /**
   * Returns the currently active panel.
   *
   * @return		the active panel
   */
  public FileCommanderDirectoryPanel getActive() {
    return m_FilesActive;
  }

  /**
   * Returns the currently inactive panel.
   *
   * @return		the inactive panel
   */
  public FileCommanderDirectoryPanel getInactive() {
    return m_FilesInactive;
  }

  /**
   * Displays a message.
   *
   * @param msg		the message to display
   */
  public void showStatus(String msg) {
    m_StatusBar.showStatus(msg);
  }

  /**
   * Displays a message.
   *
   * @param left	whether to show the message on the left or right
   * @param msg		the message to display
   */
  public void showStatus(boolean left, String msg) {
    m_StatusBar.showStatus(left, msg);
  }

  /**
   * Updates the file operations object.
   */
  protected void updateFileOperations() {
    FileOperations			active;
    FileOperations			inactive;
    RemoteToRemoteFileOperations r2r;

    active   = m_FilesActive.getFileOperations();
    inactive = m_FilesInactive.getFileOperations();

    if ((active instanceof RemoteFileOperations) && (inactive instanceof RemoteFileOperations)) {
      r2r = new RemoteToRemoteFileOperations();
      r2r.setSource((RemoteFileOperations) active);
      r2r.setTarget((RemoteFileOperations) inactive);
    }
    else if (active instanceof RemoteFileOperations) {
      m_FileOperations = active;
      ((RemoteFileOperations) m_FileOperations).setDirection(RemoteDirection.REMOTE_TO_LOCAL);
    }
    else if (inactive instanceof RemoteFileOperations) {
      m_FileOperations = inactive;
      ((RemoteFileOperations) m_FileOperations).setDirection(RemoteDirection.LOCAL_TO_REMOTE);
    }
    else {
      m_FileOperations = new LocalFileOperations();
    }
  }

  /**
   * Gets called when a panel's chooser changes.
   *
   * @param source	the panel that triggered the event
   */
  protected void chooserChanged(FileCommanderDirectoryPanel source) {
    updateFileOperations();
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Stopped = true;
  }

  /**
   * Whether the execution has been stopped.
   *
   * @return		true if stopped
   */
  @Override
  public boolean isStopped() {
    return m_Stopped;
  }
}

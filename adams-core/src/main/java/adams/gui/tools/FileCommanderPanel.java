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
 * FileCommanderPanel.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.tools;

import adams.core.MessageCollection;
import adams.core.StatusMessageHandlerExt;
import adams.core.StoppableWithFeedback;
import adams.core.io.FileObject;
import adams.core.io.FileUtils;
import adams.core.io.fileoperations.FileOperations;
import adams.core.io.fileoperations.LocalFileOperations;
import adams.core.io.fileoperations.Operation;
import adams.core.io.fileoperations.RemoteDirection;
import adams.core.io.fileoperations.RemoteFileOperations;
import adams.core.io.fileoperations.RemoteToRemoteFileOperations;
import adams.core.logging.LoggingLevel;
import adams.gui.core.BaseFlatButton;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseSplitButton;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.GUIHelper;
import adams.gui.core.KeyUtils;
import adams.gui.core.MenuBarProvider;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.tools.filecommander.AbstractFileCommanderAction;
import adams.gui.tools.filecommander.Actions;
import adams.gui.tools.filecommander.FileCommanderDirectoryPanel;

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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
  protected BaseFlatButton m_ButtonReload;

  /** the button for renaming. */
  protected BaseFlatButton m_ButtonRename;

  /** the button for viewing the file. */
  protected BaseFlatButton m_ButtonView;

  /** the button for copying. */
  protected BaseFlatButton m_ButtonCopy;

  /** the button for duplicating. */
  protected BaseFlatButton m_ButtonDuplicate;

  /** the button for moving. */
  protected BaseFlatButton m_ButtonMove;

  /** the button for creating a directory. */
  protected BaseFlatButton m_ButtonMkDir;

  /** the button for deleting. */
  protected BaseFlatButton m_ButtonDelete;

  /** the action button. */
  protected BaseSplitButton m_ButtonAction;

  /** the button for stopping an operation. */
  protected BaseFlatButton m_ButtonStop;

  /** the button for quitting. */
  protected BaseFlatButton m_ButtonQuit;

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

  /** the key listener for actions on the active table. */
  protected KeyAdapter m_ActivePanelKeyListener;

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
	action = (AbstractFileCommanderAction) cls.getDeclaredConstructor().newInstance();
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
    setPreferredSize(GUIHelper.makeWider(GUIHelper.getDefaultDialogDimension()));

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
    m_PanelButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelAll.add(m_PanelButtons, BorderLayout.SOUTH);

    m_ButtonReload = new BaseFlatButton("Reload");
    m_ButtonReload.addActionListener((ActionEvent) -> reload());
    m_ButtonReload.setToolTipText("Ctrl+R");
    m_PanelButtons.add(m_ButtonReload);

    m_ButtonRename = new BaseFlatButton("Rename");
    m_ButtonRename.addActionListener((ActionEvent) -> rename());
    m_ButtonRename.setToolTipText("F2");
    m_PanelButtons.add(m_ButtonRename);

    m_ButtonView = new BaseFlatButton("View");
    m_ButtonView.addActionListener((ActionEvent) -> view());
    m_ButtonView.setToolTipText("F3");
    m_PanelButtons.add(m_ButtonView);

    m_ButtonCopy = new BaseFlatButton("Copy");
    m_ButtonCopy.addActionListener((ActionEvent) -> copy());
    m_ButtonCopy.setToolTipText("F5");
    m_PanelButtons.add(m_ButtonCopy);

    m_ButtonDuplicate = new BaseFlatButton("Duplicate");
    m_ButtonDuplicate.addActionListener((ActionEvent) -> duplicate());
    m_ButtonDuplicate.setToolTipText("Ctrl+C");
    m_PanelButtons.add(m_ButtonDuplicate);

    m_ButtonMove = new BaseFlatButton("Move");
    m_ButtonMove.addActionListener((ActionEvent) -> move());
    m_ButtonMove.setToolTipText("F6");
    m_PanelButtons.add(m_ButtonMove);

    m_ButtonMkDir = new BaseFlatButton("MkDir");
    m_ButtonMkDir.addActionListener((ActionEvent) -> mkdir());
    m_ButtonMkDir.setToolTipText("F7");
    m_PanelButtons.add(m_ButtonMkDir);

    m_ButtonDelete = new BaseFlatButton("Delete");
    m_ButtonDelete.addActionListener((ActionEvent) -> delete());
    m_ButtonDelete.setToolTipText("F8");
    m_PanelButtons.add(m_ButtonDelete);

    // only show actions button if actual actions present
    // discounting the dummy "Actions" action
    if (m_Actions.size() > 1) {
      m_ButtonAction = new BaseSplitButton();
      m_ButtonAction.setButtonEnabled(true);
      for (AbstractFileCommanderAction action: m_Actions) {
	if (action.getClass() == Actions.class)
	  m_ButtonAction.setAction(action);
	else
	  m_ButtonAction.add(action);
      }
      m_PanelButtons.add(m_ButtonAction);
    }

    m_ButtonStop = new BaseFlatButton("Stop");
    m_ButtonStop.addActionListener((ActionEvent) -> stopExecution());
    m_ButtonStop.setToolTipText("F9");
    m_PanelButtons.add(m_ButtonStop);

    m_ButtonQuit = new BaseFlatButton("Quit");
    m_ButtonQuit.addActionListener((ActionEvent) -> quit());
    m_ButtonQuit.setToolTipText("F10");
    m_PanelButtons.add(m_ButtonQuit);

    m_StatusBar = new BaseStatusBar();
    add(m_StatusBar, BorderLayout.SOUTH);

    m_ActivePanelKeyListener = new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
	switch (e.getKeyCode()) {
	  case KeyEvent.VK_R:
	    if (KeyUtils.isOnlyCtrlDown(e.getModifiersEx())) {
	      if (m_ButtonReload.isEnabled()) {
		e.consume();
		m_FilesActive.reload();
	      }
	    }
	    break;
	  case KeyEvent.VK_F2:
	    if (m_ButtonRename.isEnabled()) {
	      e.consume();
	      rename();
	    }
	    break;
	  case KeyEvent.VK_F3:
	    if (m_ButtonView.isEnabled()) {
	      e.consume();
	      view();
	    }
	    break;
	  case KeyEvent.VK_F5:
	    if (m_ButtonCopy.isEnabled()) {
	      e.consume();
	      copy();
	    }
	    break;
	  case KeyEvent.VK_C:
	    if (KeyUtils.isOnlyCtrlDown(e.getModifiersEx())) {
	      if (m_ButtonDuplicate.isEnabled()) {
		e.consume();
		duplicate();
	      }
	    }
	    break;
	  case KeyEvent.VK_F6:
	    if (m_ButtonMove.isEnabled()) {
	      e.consume();
	      move();
	    }
	    break;
	  case KeyEvent.VK_F7:
	    if (m_ButtonMkDir.isEnabled()) {
	      e.consume();
	      mkdir();
	    }
	    break;
	  case KeyEvent.VK_F8:
	    if (m_ButtonDelete.isEnabled()) {
	      e.consume();
	      delete();
	    }
	    break;
	  case KeyEvent.VK_F9:
	    if (m_ButtonStop.isEnabled()) {
	      e.consume();
	      stopExecution();
	    }
	    break;
	  case KeyEvent.VK_F10:
	    if (m_ButtonQuit.isEnabled()) {
	      e.consume();
	      quit();
	    }
	    break;
	}
      }
    };
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
    m_FilesInactive.getFilePanel().removeTableKeyListener(m_ActivePanelKeyListener);
    m_FilesActive.getFilePanel().removeTableKeyListener(m_ActivePanelKeyListener);
    m_FilesActive.getFilePanel().addTableKeyListener(m_ActivePanelKeyListener);

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
    m_ButtonView.setEnabled(!busy && hasActive && (activeFiles.length == 1) && activeFiles[0].isFile());
    m_ButtonCopy.setEnabled(!busy && hasActive && (activeFiles.length > 0) && m_FileOperations.isSupported(Operation.COPY));
    m_ButtonDuplicate.setEnabled(!busy && hasActive && (activeFiles.length == 1) && m_FileOperations.isSupported(Operation.DUPLICATE));
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
   * Copies the selected files/dirs to the other directory.
   */
  public void copy() {
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
	this, "Do you want to copy\n" + files[0]
	  + "\nto\n" + m_FilesInactive.getFilePanel().getCurrentDirAsFile().getAbsolutePath() + "?",
	"Copy");
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to copy " + files.length + " objects?"
	  + "\nfrom\n" + m_FilesActive.getFilePanel().getCurrentDirAsFile().getAbsolutePath()
	  + "\nto\n" + m_FilesInactive.getFilePanel().getCurrentDirAsFile().getAbsolutePath() + "?",
	"Copy");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    m_Worker = new SwingWorker() {
      private MessageCollection errors = new MessageCollection();
      @Override
      protected Object doInBackground() throws Exception {
	String target = m_FilesInactive.getFilePanel().getCurrentDirAsFile().getAbsolutePath();
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
	  m_StatusBar.showStatus("User stopped copying objects!");
	else
	  m_StatusBar.showStatus("");
	updateButtons();
	if (!errors.isEmpty())
	  GUIHelper.showErrorMessage(FileCommanderPanel.this, errors.toString());
	// update the panel that received the files
	m_FilesInactive.reload();
      }
    };
    m_Worker.execute();
  }

  /**
   * Duplicates the selected file/dir.
   * Only for local files/dirs.
   */
  public void duplicate() {
    FileObject[]	files;
    String		input;
    String 		base;
    String		ext;
    int			count;
    String		newName;
    final String	sourceObj;
    final String 	targetObj;

    m_Stopped = false;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getFilePanel().getSelectedFileObjects();
    if (files.length != 1)
      return;

    // determine new name
    base = FileUtils.replaceExtension(files[0].getName(), "");
    ext  = FileUtils.getExtension(files[0].getName());
    if (ext == null)
      ext = "";
    if (base.matches(".\\([0-9]+\\)$"))
      base = base.replaceAll(".\\([0-9]+\\)$", "");
    count = 1;
    while (true) {
      count++;
      newName = base + " (" + count + ")." + ext;
      if (!FileUtils.fileExists(files[0].getFile().getParent() + "/" + newName))
	break;
    }

    // prompt user for new name
    input = GUIHelper.showInputDialog(this, "Please enter new name", newName, "Duplicate");
    if (input == null)
      return;
    if (input.equals(files[0].getName())) {
      showStatus("Cannot copy file/dir unto itself!");
      return;
    }

    // duplicate file/dir
    sourceObj = files[0].getFile().getAbsolutePath();
    targetObj = files[0].getFile().getParent() + "/" + input;
    m_Worker = new SwingWorker() {
      private MessageCollection errors = new MessageCollection();
      @Override
      protected Object doInBackground() throws Exception {
	m_StatusBar.showStatus("Duplicating " + sourceObj + "...");
	String msg = m_FileOperations.copy(sourceObj, targetObj);
	if (msg != null)
	  errors.add(msg);
	return null;
      }
      @Override
      protected void done() {
	super.done();
	m_Worker = null;
	if (m_Stopped)
	  m_StatusBar.showStatus("User stopped duplicating operation!");
	else
	  m_StatusBar.showStatus("");
	updateButtons();
	if (!errors.isEmpty())
	  GUIHelper.showErrorMessage(FileCommanderPanel.this, errors.toString(), "Duplicate");
	// update the affected panel
	m_FilesActive.reload();
      }
    };
    m_Worker.execute();
  }

  /**
   * Renames a file/dir.
   */
  public void rename() {
    FileObject[]	files;
    String		input;
    String		target;
    String		msg;

    m_Stopped = false;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getFilePanel().getSelectedFileObjects();
    if (files.length != 1)
      return;

    input = GUIHelper.showInputDialog(this, "Please enter new name", files[0].getName(), "Rename");
    if ((input == null) || input.equals(files[0].getName()))
      return;
    target = m_FilesActive.getDirectory() + File.separator + input;
    try {
      msg = m_FileOperations.rename(files[0].toString(), target);
      if (msg != null)
	GUIHelper.showErrorMessage(this, "Failed to rename " + files[0] + " to " + target + ":\n" + msg, "Rename");
    }
    catch (Exception e) {
      GUIHelper.showErrorMessage(this, "Failed to rename " + files[0] + " to " + target + "!", e, "Rename");
    }

    // update the affected panel
    m_FilesActive.reload(() -> m_FilesActive.getFilePanel().setSelectedName(input));
  }

  /**
   * Moves multiple selected files/dirs to the other directory.
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
      GUIHelper.showErrorMessage(this, "Source and target directory are the same, cannot move objects!", "Move");
      return;
    }

    if (files.length == 1)
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to move the following object?\n" + files[0],
	"Move");
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to move " + files.length + " objects"
	  + "\nfrom\n" + m_FilesActive.getFilePanel().getCurrentDirAsFile().getAbsolutePath()
	  + "\nto\n" + m_FilesInactive.getFilePanel().getCurrentDirAsFile().getAbsolutePath() + "?",
	"Move");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    m_Worker = new SwingWorker() {
      private MessageCollection errors = new MessageCollection();
      @Override
      protected Object doInBackground() throws Exception {
	String target = m_FilesInactive.getFilePanel().getCurrentDirAsFile().getAbsolutePath();
	for (int i = 0; i < files.length; i++) {
	  if (m_Stopped)
	    break;
	  FileObject file = files[i];
	  m_StatusBar.showStatus("Moving " + (i+1) + "/" + files.length + ": " + file);
	  try {
	    String msg = m_FileOperations.move(file.toString(), target + "/" + file.getName());
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
	  m_StatusBar.showStatus("User stopped moving objects!");
	else
	  m_StatusBar.showStatus("");
	updateButtons();
	if (!errors.isEmpty())
	  GUIHelper.showErrorMessage(FileCommanderPanel.this, errors.toString(), "Move");
	// update both panels
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

    dir   = m_FilesActive.getFilePanel().getCurrentDirAsFile().getAbsolutePath();
    input = GUIHelper.showInputDialog(this, "Please enter name for new directory", "", "Make dir");
    if (input == null)
      return;

    dirNew = m_FilesActive.getDirectoryLister().newDirectory(dir, input);
    msg    = m_FileOperations.mkdir(dirNew.toString());
    if (msg != null)
      GUIHelper.showErrorMessage(this, "Failed to create directory: " + dirNew + "\n" + msg, "Make dir");
    else
      m_FilesActive.reload(() -> m_FilesActive.getFilePanel().setSelectedFile(dirNew));
  }

  /**
   * Deletes the selected files/dirs.
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
	this, "Do you want to delete the following object?\n" + files[0],
	"Delete");
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to delete " + files.length + " objects"
	  + " from\n" + m_FilesActive.getFilePanel().getCurrentDirAsFile().getAbsolutePath() + "?",
	"Delete");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    m_Worker = new SwingWorker() {
      private MessageCollection errors = new MessageCollection();
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
	  m_StatusBar.showStatus("User stopped deleting objects!");
	else
	  m_StatusBar.showStatus("");
	updateButtons();
	if (!errors.isEmpty())
	  GUIHelper.showErrorMessage(FileCommanderPanel.this, "Failed to delete object(s)!\n" + errors, "Delete");
	// update the affected panel
	m_FilesActive.reload();
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

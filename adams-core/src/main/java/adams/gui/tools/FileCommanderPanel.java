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
import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.BaseStatusBar;
import adams.gui.core.ConsolePanel;
import adams.gui.core.FilePanel;
import adams.gui.core.FilePanel.FileDoubleClickEvent;
import adams.gui.core.GUIHelper;
import adams.gui.core.MenuBarProvider;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.SimplePreviewBrowserDialog;
import adams.gui.tools.filecommander.AbstractFileCommanderAction;
import adams.gui.tools.filecommander.Actions;
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
import java.awt.Dialog.ModalityType;
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
  implements MenuBarProvider, StatusMessageHandlerExt {

  private static final long serialVersionUID = 3894304347424478383L;

  /** the left panel. */
  protected JPanel m_PanelLeft;

  /** the left side. */
  protected FilePanel m_FilesLeft;

  /** the left dir panel. */
  protected DirectoryChooserPanel m_DirLeft;

  /** the right panel. */
  protected JPanel m_PanelRight;

  /** the right side. */
  protected FilePanel m_FilesRight;

  /** the right dir panel. */
  protected DirectoryChooserPanel m_DirRight;

  /** the active panel. */
  protected FilePanel m_FilesActive;

  /** the inactive panel. */
  protected FilePanel m_FilesInactive;

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

  /** whether to ignore changes. */
  protected boolean m_IgnoreChanges;

  /** the worker thread. */
  protected SwingWorker m_Worker;

  /** the available actions. */
  protected List<AbstractFileCommanderAction> m_Actions;

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
    m_IgnoreChanges  = false;
    m_Worker         = null;
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

    m_FilesLeft = new FilePanel(true);
    m_FilesLeft.startUpdate();
    m_FilesLeft.setSearchVisible(true);
    m_FilesLeft.setListDirs(true);
    m_FilesLeft.setMultiSelection(true);
    m_FilesLeft.finishUpdate();
    m_FilesLeft.addSelectionChangeListener((ChangeEvent e) -> setActive(m_FilesLeft));
    m_FilesLeft.addDirectoryChangeListener((ChangeEvent e) -> {
      m_IgnoreChanges = true;
      m_DirLeft.setCurrent(new PlaceholderFile(m_FilesLeft.getCurrentDir()));
      m_IgnoreChanges = false;
    });
    m_FilesLeft.addFileDoubleClickListener((FileDoubleClickEvent e) -> view(e.getFile()));
    m_DirLeft = new DirectoryChooserPanel();
    m_DirLeft.addChangeListener((ChangeEvent e) -> {
      if (m_IgnoreChanges)
	return;
      setActive(m_FilesLeft);
      m_FilesLeft.setCurrentDir(m_DirLeft.getCurrent().getAbsolutePath());
    });
    m_PanelLeft = new JPanel(new BorderLayout(5, 5));
    m_PanelLeft.add(m_FilesLeft, BorderLayout.CENTER);
    m_PanelLeft.add(m_DirLeft, BorderLayout.NORTH);
    panel.add(m_PanelLeft);

    m_FilesRight = new FilePanel(true);
    m_FilesRight.startUpdate();
    m_FilesRight.setSearchVisible(true);
    m_FilesRight.setListDirs(true);
    m_FilesRight.setMultiSelection(true);
    m_FilesRight.finishUpdate();
    m_FilesRight.addSelectionChangeListener((ChangeEvent e) -> setActive(m_FilesRight));
    m_FilesRight.addDirectoryChangeListener((ChangeEvent e) -> {
      m_IgnoreChanges = true;
      m_DirRight.setCurrent(new PlaceholderFile(m_FilesRight.getCurrentDir()));
      m_IgnoreChanges = false;
    });
    m_FilesRight.addFileDoubleClickListener((FileDoubleClickEvent e) -> view(e.getFile()));
    m_DirRight = new DirectoryChooserPanel();
    m_DirRight.addChangeListener((ChangeEvent e) -> {
      if (m_IgnoreChanges)
	return;
      setActive(m_FilesRight);
      m_FilesRight.setCurrentDir(m_DirRight.getCurrent().getAbsolutePath());
    });
    m_PanelRight = new JPanel(new BorderLayout(5, 5));
    m_PanelRight.add(m_FilesRight, BorderLayout.CENTER);
    m_PanelRight.add(m_DirRight, BorderLayout.NORTH);
    panel.add(m_PanelRight);

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
    setActive(m_FilesLeft);
    updateButtons();
  }

  /**
   * Updates the active state of the panel.
   *
   * @param active	the new active panel
   */
  protected void setActive(FilePanel active) {
    JPanel	panelActive;
    JPanel	panelInactive;

    m_FilesActive = active;
    if (m_FilesLeft == active) {
      m_FilesInactive = m_FilesRight;
      panelInactive   = m_PanelRight;
      panelActive     = m_PanelLeft;
    }
    else {
      m_FilesInactive = m_FilesLeft;
      panelInactive   = m_PanelLeft;
      panelActive     = m_PanelRight;
    }
    m_FilesInactive.clearSelection();
    panelActive.setBorder(BorderFactory.createLineBorder(Color.GRAY, 3));
    panelInactive.setBorder(BorderFactory.createLineBorder(m_FilesActive.getBackground(), 3));
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
    activeFiles = m_FilesActive.getSelectedFiles();

    m_ButtonRename.setEnabled(!busy && hasActive && (activeFiles.length == 1));
    m_ButtonView.setEnabled(!busy && hasActive && (activeFiles.length == 1));
    m_ButtonCopy.setEnabled(!busy && hasActive && (activeFiles.length > 0));
    m_ButtonMove.setEnabled(!busy && hasActive && (activeFiles.length > 0));
    m_ButtonMkDir.setEnabled(!busy && hasActive);
    m_ButtonDelete.setEnabled(!busy && hasActive && (activeFiles.length > 0));
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
      menuitem.addActionListener((ActionEvent e) -> m_FilesLeft.setShowHidden(m_MenuItemLeftShowHidden.isSelected()));
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
      menuitem.addActionListener((ActionEvent e) -> m_FilesRight.setShowHidden(m_MenuItemRightShowHidden.isSelected()));
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
  public void setDirectory(File dir, boolean left) {
    if (left) {
      m_FilesLeft.setCurrentDir(dir.getAbsolutePath());
      m_DirLeft.setCurrent(dir);
    }
    else {
      m_FilesRight.setCurrentDir(dir.getAbsolutePath());
      m_DirRight.setCurrent(dir);
    }
  }

  /**
   * Returns the current directory.
   *
   * @param left	if true the directory of the left panel is retrieved
   * @return		the current directory
   */
  public File getDirectory(boolean left) {
    if (left)
      return new File(m_FilesLeft.getCurrentDir());
    else
      return new File(m_FilesRight.getCurrentDir());
  }

  /**
   * Reloads the files.
   */
  protected void reload() {
    m_FilesLeft.reload();
    m_FilesRight.reload();
  }

  /**
   * Views the selected file.
   */
  protected void view() {
    if (m_FilesActive == null)
      return;
    view(m_FilesActive.getSelectedFile());
  }

  /**
   * Views the file.
   *
   * @param file	the file to view, ignored if null
   */
  protected void view(File file) {
    SimplePreviewBrowserDialog	dialog;

    if (file == null)
      return;

    if (getParentDialog() != null)
      dialog = new SimplePreviewBrowserDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new SimplePreviewBrowserDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(SimplePreviewBrowserDialog.DISPOSE_ON_CLOSE);
    dialog.open(new PlaceholderFile(m_FilesActive.getSelectedFile()));
    dialog.setSize(GUIHelper.getDefaultDialogDimension());
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
  }

  /**
   * Copies the selected files to the other directory.
   */
  public void copy() {
    File[]		files;
    String		input;
    File		target;
    int			retVal;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getSelectedFiles();
    if (files.length == 0)
      return;

    if (files.length == 1)
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to copy\n" + files[0]
	  + "\nto\n" + m_FilesInactive.getCurrentDir() + "?");
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to copy " + files.length + " file" + (files.length > 1 ? "s" : "")
	  + "\nfrom\n" + m_FilesActive.getCurrentDir()
	  + "\nto\n" + m_FilesInactive.getCurrentDir() + "?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    if (files.length > 1) {
      if (m_FilesActive.getCurrentDir().equals(m_FilesInactive.getCurrentDir())) {
	GUIHelper.showErrorMessage(this, "Source and target directory are the same, cannot copy files!");
	return;
      }
      m_Worker = new SwingWorker() {
	protected MessageCollection errors = new MessageCollection();
	@Override
	protected Object doInBackground() throws Exception {
	  File target = new PlaceholderFile(m_FilesInactive.getCurrentDir());
	  for (int i = 0; i < files.length; i++) {
	    File file = files[i];
	    m_StatusBar.showStatus("Copying " + (i+1) + "/" + files.length + ": " + file);
	    try {
	      if (!FileUtils.copy(file, target))
		errors.add("Failed to copy " + file + " to " + target + "!");
	    }
	    catch (Exception e) {
	      errors.add("Failed to copy " + files[0] + " to " + target + "!", e);
	    }
	  }
	  return null;
	}
	@Override
	protected void done() {
	  super.done();
	  m_Worker = null;
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
      if (m_FilesActive.getCurrentDir().equals(m_FilesInactive.getCurrentDir()) && input.equals(files[0].getName()))
	return;
      target = new PlaceholderFile(files[0].getParentFile().getAbsolutePath() + File.separator + input);
      try {
	if (!FileUtils.copy(files[0], target))
	  GUIHelper.showErrorMessage(this, "Failed to copy " + files[0] + " to " + target + "!");
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(this, "Failed to copy " + files[0] + " to " + target + "!", e);
      }
    }

    reload();
  }

  /**
   * Renames a file.
   */
  public void rename() {
    File[]		files;
    String		input;
    File		target;
    int			retVal;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getSelectedFiles();
    if (files.length != 1)
      return;

    retVal = GUIHelper.showConfirmMessage(
      this, "Do you want to rename the following file?\n" + files[0]);
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    input = GUIHelper.showInputDialog(this, "Please enter new name", files[0].getName());
    if ((input == null) || input.equals(files[0].getName()))
      return;
    target = new PlaceholderFile(files[0].getParentFile().getAbsolutePath() + File.separator + input);
    try {
      if (!FileUtils.move(files[0], target))
	GUIHelper.showErrorMessage(this, "Failed to rename " + files[0] + " to " + target + "!");
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
    File[]		files;
    int			retVal;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getSelectedFiles();
    if (files.length == 0)
      return;
    if (m_FilesActive.getCurrentDir().equals(m_FilesInactive.getCurrentDir())) {
      GUIHelper.showErrorMessage(this, "Source and target directory are the same, cannot move files!");
      return;
    }

    if (files.length == 1)
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to move the following file?\n" + files[0]);
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to move " + files.length + " file" + (files.length > 1 ? "s" : "")
	  + "\nfrom\n" + m_FilesActive.getCurrentDir()
	  + "\nto\n" + m_FilesInactive.getCurrentDir() + "?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    m_Worker = new SwingWorker() {
      protected MessageCollection errors = new MessageCollection();
      @Override
      protected Object doInBackground() throws Exception {
	File target = new PlaceholderFile(m_FilesInactive.getCurrentDir());
	for (int i = 0; i < files.length; i++) {
	  File file = files[i];
	  m_StatusBar.showStatus("Moving " + (i+1) + "/" + files.length + ": " + file);
	  try {
	    if (!FileUtils.move(file, target))
	      errors.add("Failed to move " + file + " to " + target + "!");
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
    File	dirNew;

    if (m_FilesActive == null)
      return;

    dir   = m_FilesActive.getCurrentDir();
    input = GUIHelper.showInputDialog(this, "Please enter name for new directory");
    if (input == null)
      return;

    dirNew = new PlaceholderDirectory(dir + File.separator + input);
    if (!dirNew.mkdir())
      GUIHelper.showErrorMessage(this, "Failed to create directory:\n" + dirNew);
    else
      reload();
  }

  /**
   * Deletes the selected files.
   */
  public void delete() {
    File[]		files;
    int			retVal;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getSelectedFiles();
    if (files.length == 0)
      return;

    if (files.length == 1)
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to delete the following file?\n" + files[0]);
    else
      retVal = GUIHelper.showConfirmMessage(
	this, "Do you want to delete " + files.length + " file" + (files.length > 1 ? "s" : "")
	  + " from\n" + m_FilesActive.getCurrentDir() + "?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    m_Worker = new SwingWorker() {
      protected MessageCollection errors = new MessageCollection();
      @Override
      protected Object doInBackground() throws Exception {
	updateButtons();
	for (int i = 0; i < files.length; i++) {
	  File file = files[i];
	  m_StatusBar.showStatus("Deleting " + (i+1) + "/" + files.length + ": " + file);
	  try {
	    if (!FileUtils.delete(file))
	      errors.add("Failed to delete: " + file);
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
    BaseRegExp	regExp;
    String	input;

    if (left)
      regExp = m_FilesLeft.getFilter();
    else
      regExp = m_FilesRight.getFilter();

    input = GUIHelper.showInputDialog(this, "Please enter the filter (regular expression)", regExp.getValue());
    if (input == null)
      return;
    if (!regExp.isValid(input)) {
      GUIHelper.showErrorMessage(this, "Invalid regular expression:\n" + input);
      return;
    }

    regExp.setValue(input);
    if (left)
      m_FilesLeft.setFilter(regExp);
    else
      m_FilesRight.setFilter(regExp);
  }

  /**
   * Returns the currently active panel.
   *
   * @return		the active panel
   */
  public FilePanel getActive() {
    return m_FilesActive;
  }

  /**
   * Returns the currently inactive panel.
   *
   * @return		the inactive panel
   */
  public FilePanel getInactive() {
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
}

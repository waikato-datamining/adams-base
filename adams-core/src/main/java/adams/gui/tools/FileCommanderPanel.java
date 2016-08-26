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
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.DirectoryChooserPanel;
import adams.gui.core.BasePanel;
import adams.gui.core.FilePanel;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.ApprovalDialog;
import adams.gui.dialog.PreviewBrowserDialog;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;

/**
 * File manager with two-pane interface, similar to the Midnight Commander.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileCommanderPanel
  extends BasePanel {

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
  protected JButton m_ButtonReload;

  /** the button for viewing the file. */
  protected JButton m_ButtonView;

  /** the button for copying the file. */
  protected JButton m_ButtonCopy;

  /** the button for renaming/moving. */
  protected JButton m_ButtonRenameMove;

  /** the button for creating a directory. */
  protected JButton m_ButtonMkDir;

  /** the button for deleting. */
  protected JButton m_ButtonDelete;

  /** the button for quitting. */
  protected JButton m_ButtonQuit;

  /** whether to ignore changes. */
  protected boolean m_IgnoreChanges;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FilesActive   = null;
    m_FilesInactive = null;
    m_IgnoreChanges = false;
  }

  /**
   * Initializes the widgets.
   */
  @Override
  protected void initGUI() {
    JPanel	panel;

    super.initGUI();

    setLayout(new BorderLayout());

    panel = new JPanel(new GridLayout(1, 2, 5, 5));
    add(panel, BorderLayout.CENTER);

    m_FilesLeft = new FilePanel(true);
    m_FilesLeft.startUpdate();
    m_FilesLeft.setListDirs(true);
    m_FilesLeft.setMultiSelection(true);
    m_FilesLeft.finishUpdate();
    m_FilesLeft.addSelectionChangeListener((ChangeEvent e) -> setActive(m_FilesLeft));
    m_FilesLeft.addDirectoryChangeListener((ChangeEvent e) -> {
      m_IgnoreChanges = true;
      m_DirLeft.setCurrent(m_FilesLeft.getCurrentDir());
      m_IgnoreChanges = false;
    });
    m_DirLeft = new DirectoryChooserPanel();
    m_DirLeft.addChangeListener((ChangeEvent e) -> {
      if (m_IgnoreChanges)
	return;
      setActive(m_FilesLeft);
      m_FilesLeft.setCurrentDir(new PlaceholderDirectory(m_DirLeft.getCurrent()));
    });
    m_PanelLeft = new JPanel(new BorderLayout(5, 5));
    m_PanelLeft.add(m_FilesLeft, BorderLayout.CENTER);
    m_PanelLeft.add(m_DirLeft, BorderLayout.SOUTH);
    panel.add(m_PanelLeft);

    m_FilesRight = new FilePanel(true);
    m_FilesRight.startUpdate();
    m_FilesRight.setListDirs(true);
    m_FilesRight.setMultiSelection(true);
    m_FilesRight.finishUpdate();
    m_FilesRight.addSelectionChangeListener((ChangeEvent e) -> setActive(m_FilesRight));
    m_FilesRight.addDirectoryChangeListener((ChangeEvent e) -> {
      m_IgnoreChanges = true;
      m_DirRight.setCurrent(m_FilesRight.getCurrentDir());
      m_IgnoreChanges = false;
    });
    m_DirRight = new DirectoryChooserPanel();
    m_DirRight.addChangeListener((ChangeEvent e) -> {
      if (m_IgnoreChanges)
	return;
      setActive(m_FilesRight);
      m_FilesRight.setCurrentDir(new PlaceholderDirectory(m_DirRight.getCurrent()));
    });
    m_PanelRight = new JPanel(new BorderLayout(5, 5));
    m_PanelRight.add(m_FilesRight, BorderLayout.CENTER);
    m_PanelRight.add(m_DirRight, BorderLayout.SOUTH);
    panel.add(m_PanelRight);

    m_PanelButtons = new JPanel(new FlowLayout());
    add(m_PanelButtons, BorderLayout.SOUTH);

    m_ButtonReload = new JButton("Reload");
    m_ButtonReload.addActionListener((ActionEvent) -> reload());
    m_PanelButtons.add(m_ButtonReload);

    m_ButtonView = new JButton("View");
    m_ButtonView.addActionListener((ActionEvent) -> view());
    m_PanelButtons.add(m_ButtonView);

    m_ButtonCopy = new JButton("Copy");
    m_ButtonCopy.addActionListener((ActionEvent) -> copy());
    m_PanelButtons.add(m_ButtonCopy);

    m_ButtonRenameMove = new JButton("RenMov");
    m_ButtonRenameMove.addActionListener((ActionEvent) -> renameMove());
    m_PanelButtons.add(m_ButtonRenameMove);

    m_ButtonMkDir = new JButton("MkDir");
    m_ButtonMkDir.addActionListener((ActionEvent) -> mkdir());
    m_PanelButtons.add(m_ButtonMkDir);

    m_ButtonDelete = new JButton("Delete");
    m_ButtonDelete.addActionListener((ActionEvent) -> delete());
    m_PanelButtons.add(m_ButtonDelete);

    m_ButtonQuit = new JButton("Quit");
    m_ButtonQuit.addActionListener((ActionEvent) -> quit());
    m_PanelButtons.add(m_ButtonQuit);
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
    boolean	active;

    active = (m_FilesActive != null);

    m_ButtonView.setEnabled(active && (m_FilesActive.getSelectedFiles().length == 1));
    m_ButtonCopy.setEnabled(active && (m_FilesActive.getSelectedFiles().length > 0));
    m_ButtonRenameMove.setEnabled(active && (m_FilesActive.getSelectedFiles().length > 0));
    m_ButtonMkDir.setEnabled(active);
    m_ButtonDelete.setEnabled(active && (m_FilesActive.getSelectedFiles().length > 0));
  }

  /**
   * Sets the current directory.
   *
   * @param dir		the directory to use
   * @param left	if true the left panel gets updated
   */
  public void setDirectory(PlaceholderDirectory dir, boolean left) {
    if (left) {
      m_FilesLeft.setCurrentDir(dir);
      m_DirLeft.setCurrent(dir);
    }
    else {
      m_FilesRight.setCurrentDir(dir);
      m_DirRight.setCurrent(dir);
    }
  }

  /**
   * Returns the current directory.
   *
   * @param left	if true the directory of the left panel is retrieved
   * @return		the current directory
   */
  public PlaceholderDirectory getDirectory(boolean left) {
    if (left)
      return m_FilesLeft.getCurrentDir();
    else
      return m_FilesRight.getCurrentDir();
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
    PreviewBrowserDialog 	dialog;

    if (m_FilesActive == null)
      return;
    if (m_FilesActive.getSelectedFile() == null)
      return;

    if (getParentDialog() != null)
      dialog = new PreviewBrowserDialog(getParentDialog(), ModalityType.MODELESS);
    else
      dialog = new PreviewBrowserDialog(getParentFrame(), false);
    dialog.setDefaultCloseOperation(PreviewBrowserDialog.DISPOSE_ON_CLOSE);
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
    MessageCollection	errors;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getSelectedFiles();
    if (files.length == 0)
      return;

    if (files.length > 1) {
      if (m_FilesActive.getCurrentDir().equals(m_FilesInactive.getCurrentDir())) {
	GUIHelper.showErrorMessage(this, "Source and target directory are the same, cannot copy files!");
	return;
      }
      target = m_FilesInactive.getCurrentDir();
      errors = new MessageCollection();
      for (File file: files) {
	try {
	  if (!FileUtils.copy(files[0], target))
	    errors.add("Failed to copy " + file + " to " + target + "!");
	}
	catch (Exception e) {
	  errors.add("Failed to copy " + files[0] + " to " + target + "!", e);
	}
      }
      if (!errors.isEmpty())
	GUIHelper.showErrorMessage(this, errors.toString());
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
   * Renames a single file or moves multiple selected files to the other
   * directory.
   */
  public void renameMove() {
    File[]		files;
    String		input;
    File		target;
    MessageCollection	errors;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getSelectedFiles();
    if (files.length == 0)
      return;

    if (files.length > 1) {
      if (m_FilesActive.getCurrentDir().equals(m_FilesInactive.getCurrentDir())) {
	GUIHelper.showErrorMessage(this, "Source and target directory are the same, cannot move files!");
	return;
      }
      target = m_FilesInactive.getCurrentDir();
      errors = new MessageCollection();
      for (File file: files) {
	try {
	  if (!FileUtils.move(file, target))
	    errors.add("Failed to move " + file + " to " + target + "!");
	}
	catch (Exception e) {
	  errors.add("Failed to move " + file + " to " + target + "!", e);
	}
      }
      if (!errors.isEmpty())
	GUIHelper.showErrorMessage(this, errors.toString());
    }
    else {
      input = GUIHelper.showInputDialog(this, "Please enter new name", files[0].getName());
      if ((input == null) || input.equals(files[0].getName()))
	return;
      target = new PlaceholderFile(files[0].getParentFile().getAbsolutePath() + File.separator + input);
      try {
	if (!FileUtils.move(files[0], target))
	  GUIHelper.showErrorMessage(this, "Failed to move " + files[0] + " to " + target + "!");
      }
      catch (Exception e) {
	GUIHelper.showErrorMessage(this, "Failed to move " + files[0] + " to " + target + "!", e);
      }
    }

    reload();
  }

  /**
   * Creates a new directory.
   */
  public void mkdir() {
    String			input;
    PlaceholderDirectory	dir;
    PlaceholderDirectory	dirNew;

    if (m_FilesActive == null)
      return;

    dir   = m_FilesActive.getCurrentDir();
    input = GUIHelper.showInputDialog(this, "Please enter name for new directory");
    if (input == null)
      return;

    dirNew = new PlaceholderDirectory(dir.getAbsolutePath() + File.separator + input);
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
    MessageCollection	errors;

    if (m_FilesActive == null)
      return;
    files = m_FilesActive.getSelectedFiles();
    if (files.length == 0)
      return;

    retVal = GUIHelper.showConfirmMessage(this, "Do you want to delete " + files.length + " file" + (files.length > 0 ? "s" : "") + "?");
    if (retVal != ApprovalDialog.APPROVE_OPTION)
      return;

    errors = new MessageCollection();
    for (File file: files) {
      try {
	if (!file.delete())
	  errors.add("Failed to delete: " + file);
      }
      catch (Exception e) {
	errors.add("Failed to delete: " + file, e);
      }
    }
    if (!errors.isEmpty())
      GUIHelper.showErrorMessage(this, "Failed to delete file(s)!\n" + errors);

    reload();
  }

  /**
   * Closes the commander.
   */
  public void quit() {
    closeParent();
  }
}

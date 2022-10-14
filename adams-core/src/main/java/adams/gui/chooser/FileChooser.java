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
 * FileChooser.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.UIDefaults;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;
import javax.swing.plaf.FileChooserUI;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Interface for file chooser components.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface FileChooser {

  /**
   * Sets the <code>dragEnabled</code> property,
   * which must be <code>true</code> to enable
   * automatic drag handling (the first part of drag and drop)
   * on this component.
   * The <code>transferHandler</code> property needs to be set
   * to a non-<code>null</code> value for the drag to do
   * anything.  The default value of the <code>dragEnabled</code>
   * property
   * is <code>false</code>.
   *
   * <p>
   *
   * When automatic drag handling is enabled,
   * most look and feels begin a drag-and-drop operation
   * whenever the user presses the mouse button over an item
   * and then moves the mouse a few pixels.
   * Setting this property to <code>true</code>
   * can therefore have a subtle effect on
   * how selections behave.
   *
   * <p>
   *
   * Some look and feels might not support automatic drag and drop;
   * they will ignore this property.  You can work around such
   * look and feels by modifying the component
   * to directly call the <code>exportAsDrag</code> method of a
   * <code>TransferHandler</code>.
   *
   * @param b the value to set the <code>dragEnabled</code> property to
   * @exception HeadlessException if
   *            <code>b</code> is <code>true</code> and
   *            <code>GraphicsEnvironment.isHeadless()</code>
   *            returns <code>true</code>
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see #getDragEnabled
   * @see #setTransferHandler
   * @see TransferHandler
   * @since 1.4
   */
  public void setDragEnabled(boolean b);

  /**
   * Gets the value of the <code>dragEnabled</code> property.
   *
   * @return  the value of the <code>dragEnabled</code> property
   * @see #setDragEnabled
   * @since 1.4
   */
  public boolean getDragEnabled();

  // *****************************
  // ****** File Operations ******
  // *****************************

  /**
   * Returns the selected file. This can be set either by the
   * programmer via <code>setSelectedFile</code> or by a user action, such as
   * either typing the filename into the UI or selecting the
   * file from a list in the UI.
   *
   * @see #setSelectedFile
   * @return the selected file
   */
  public File getSelectedFile();

  /**
   * Sets the selected file. If the file's parent directory is
   * not the current directory, changes the current directory
   * to be the file's parent directory.
   *
   * @see #getSelectedFile
   *
   * @param file the selected file
   */
  public void setSelectedFile(File file);

  /**
   * Returns a list of selected files if the file chooser is
   * set to allow multiple selection.
   *
   * @return an array of selected {@code File}s
   */
  public File[] getSelectedFiles();

  /**
   * Sets the list of selected files if the file chooser is
   * set to allow multiple selection.
   *
   * @param selectedFiles an array {@code File}s to be selected
   */
  public void setSelectedFiles(File[] selectedFiles);

  /**
   * Returns the current directory.
   *
   * @return the current directory
   * @see #setCurrentDirectory
   */
  public File getCurrentDirectory();

  /**
   * Sets the current directory. Passing in <code>null</code> sets the
   * file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * If the file passed in as <code>currentDirectory</code> is not a
   * directory, the parent of the file will be used as the currentDirectory.
   * If the parent is not traversable, then it will walk up the parent tree
   * until it finds a traversable directory, or hits the root of the
   * file system.
   *
   * @param dir the current directory to point to
   * @see #getCurrentDirectory
   */
  public void setCurrentDirectory(File dir);

  /**
   * Changes the directory to be set to the parent of the
   * current directory.
   *
   * @see #getCurrentDirectory
   */
  public void changeToParentDirectory();

  /**
   * Tells the UI to rescan its files list from the current directory.
   */
  public void rescanCurrentDirectory();

  /**
   * Makes sure that the specified file is viewable, and
   * not hidden.
   *
   * @param f  a File object
   */
  public void ensureFileIsVisible(File f);

  // **************************************
  // ***** JFileChooser Dialog methods *****
  // **************************************

  /**
   * Pops up an "Open File" file chooser dialog. Note that the
   * text that appears in the approve button is determined by
   * the L&amp;F.
   *
   * @param    parent  the parent component of the dialog,
   *                  can be <code>null</code>;
   *                  see <code>showDialog</code> for details
   * @return   the return state of the file chooser on popdown:
   * <ul>
   * <li>JFileChooser.CANCEL_OPTION
   * <li>JFileChooser.APPROVE_OPTION
   * <li>JFileChooser.ERROR_OPTION if an error occurs or the
   *                  dialog is dismissed
   * </ul>
   * @exception HeadlessException if GraphicsEnvironment.isHeadless()
   * returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see #showDialog
   */
  public int showOpenDialog(Component parent) throws HeadlessException;

  /**
   * Pops up a "Save File" file chooser dialog. Note that the
   * text that appears in the approve button is determined by
   * the L&amp;F.
   *
   * @param    parent  the parent component of the dialog,
   *                  can be <code>null</code>;
   *                  see <code>showDialog</code> for details
   * @return   the return state of the file chooser on popdown:
   * <ul>
   * <li>JFileChooser.CANCEL_OPTION
   * <li>JFileChooser.APPROVE_OPTION
   * <li>JFileChooser.ERROR_OPTION if an error occurs or the
   *                  dialog is dismissed
   * </ul>
   * @exception HeadlessException if GraphicsEnvironment.isHeadless()
   * returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see #showDialog
   */
  public int showSaveDialog(Component parent) throws HeadlessException;

  /**
   * Pops a custom file chooser dialog with a custom approve button.
   * For example, the following code
   * pops up a file chooser with a "Run Application" button
   * (instead of the normal "Save" or "Open" button):
   * <pre>
   * filechooser.showDialog(parentFrame, "Run Application");
   * </pre>
   *
   * Alternatively, the following code does the same thing:
   * <pre>
   *    JFileChooser chooser = new JFileChooser(null);
   *    chooser.setApproveButtonText("Run Application");
   *    chooser.showDialog(parentFrame, null);
   * </pre>
   *
   * <!--PENDING(jeff) - the following method should be added to the api:
   *      showDialog(Component parent);-->
   * <!--PENDING(kwalrath) - should specify modality and what
   *      "depends" means.-->
   *
   * <p>
   *
   * The <code>parent</code> argument determines two things:
   * the frame on which the open dialog depends and
   * the component whose position the look and feel
   * should consider when placing the dialog.  If the parent
   * is a <code>Frame</code> object (such as a <code>JFrame</code>)
   * then the dialog depends on the frame and
   * the look and feel positions the dialog
   * relative to the frame (for example, centered over the frame).
   * If the parent is a component, then the dialog
   * depends on the frame containing the component,
   * and is positioned relative to the component
   * (for example, centered over the component).
   * If the parent is <code>null</code>, then the dialog depends on
   * no visible window, and it's placed in a
   * look-and-feel-dependent position
   * such as the center of the screen.
   *
   * @param   parent  the parent component of the dialog;
   *                  can be <code>null</code>
   * @param   approveButtonText the text of the <code>ApproveButton</code>
   * @return  the return state of the file chooser on popdown:
   * <ul>
   * <li>JFileChooser.CANCEL_OPTION
   * <li>JFileChooser.APPROVE_OPTION
   * <li>JFileChooser.ERROR_OPTION if an error occurs or the
   *                  dialog is dismissed
   * </ul>
   * @exception HeadlessException if GraphicsEnvironment.isHeadless()
   * returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  @SuppressWarnings("deprecation")
  public int showDialog(Component parent, String approveButtonText) throws HeadlessException;

  // **************************
  // ***** Dialog Options *****
  // **************************

  /**
   * Returns the value of the <code>controlButtonsAreShown</code>
   * property.
   *
   * @return   the value of the <code>controlButtonsAreShown</code>
   *     property
   *
   * @see #setControlButtonsAreShown
   * @since 1.3
   */
  public boolean getControlButtonsAreShown();


  /**
   * Sets the property
   * that indicates whether the <i>approve</i> and <i>cancel</i>
   * buttons are shown in the file chooser.  This property
   * is <code>true</code> by default.  Look and feels
   * that always show these buttons will ignore the value
   * of this property.
   * This method fires a property-changed event,
   * using the string value of
   * <code>CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY</code>
   * as the name of the property.
   *
   * @param b <code>false</code> if control buttons should not be
   *    shown; otherwise, <code>true</code>
   *
   * @see #getControlButtonsAreShown
   * @see #CONTROL_BUTTONS_ARE_SHOWN_CHANGED_PROPERTY
   * @since 1.3
   */
  public void setControlButtonsAreShown(boolean b);

  /**
   * Returns the type of this dialog.  The default is
   * <code>JFileChooser.OPEN_DIALOG</code>.
   *
   * @return   the type of dialog to be displayed:
   * <ul>
   * <li>JFileChooser.OPEN_DIALOG
   * <li>JFileChooser.SAVE_DIALOG
   * <li>JFileChooser.CUSTOM_DIALOG
   * </ul>
   *
   * @see #setDialogType
   */
  public int getDialogType();

  /**
   * Sets the type of this dialog. Use <code>OPEN_DIALOG</code> when you
   * want to bring up a file chooser that the user can use to open a file.
   * Likewise, use <code>SAVE_DIALOG</code> for letting the user choose
   * a file for saving.
   * Use <code>CUSTOM_DIALOG</code> when you want to use the file
   * chooser in a context other than "Open" or "Save".
   * For instance, you might want to bring up a file chooser that allows
   * the user to choose a file to execute. Note that you normally would not
   * need to set the <code>JFileChooser</code> to use
   * <code>CUSTOM_DIALOG</code>
   * since a call to <code>setApproveButtonText</code> does this for you.
   * The default dialog type is <code>JFileChooser.OPEN_DIALOG</code>.
   *
   * @param dialogType the type of dialog to be displayed:
   * <ul>
   * <li>JFileChooser.OPEN_DIALOG
   * <li>JFileChooser.SAVE_DIALOG
   * <li>JFileChooser.CUSTOM_DIALOG
   * </ul>
   *
   * @exception IllegalArgumentException if <code>dialogType</code> is
   *                          not legal
   *
   * @see #getDialogType
   * @see #setApproveButtonText
   */
  // PENDING(jeff) - fire button text change property
  public void setDialogType(int dialogType);

  /**
   * Sets the string that goes in the <code>JFileChooser</code> window's
   * title bar.
   *
   * @param dialogTitle the new <code>String</code> for the title bar
   *
   * @see #getDialogTitle
   *
   */
  public void setDialogTitle(String dialogTitle);

  /**
   * Gets the string that goes in the <code>JFileChooser</code>'s titlebar.
   *
   * @return the string from the {@code JFileChooser} window's title bar
   * @see #setDialogTitle
   */
  public String getDialogTitle();

  // ************************************
  // ***** JFileChooser View Options *****
  // ************************************



  /**
   * Sets the tooltip text used in the <code>ApproveButton</code>.
   * If <code>null</code>, the UI object will determine the button's text.
   *
   * @param toolTipText the tooltip text for the approve button
   * @see #setApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  public void setApproveButtonToolTipText(String toolTipText);


  /**
   * Returns the tooltip text used in the <code>ApproveButton</code>.
   * If <code>null</code>, the UI object will determine the button's text.
   *
   * @return the tooltip text used for the approve button
   *
   * @see #setApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  public String getApproveButtonToolTipText();

  /**
   * Returns the approve button's mnemonic.
   * @return an integer value for the mnemonic key
   *
   * @see #setApproveButtonMnemonic
   */
  public int getApproveButtonMnemonic();

  /**
   * Sets the approve button's mnemonic using a numeric keycode.
   *
   * @param mnemonic  an integer value for the mnemonic key
   *
   * @see #getApproveButtonMnemonic
   */
  public void setApproveButtonMnemonic(int mnemonic);

  /**
   * Sets the approve button's mnemonic using a character.
   * @param mnemonic  a character value for the mnemonic key
   *
   * @see #getApproveButtonMnemonic
   */
  public void setApproveButtonMnemonic(char mnemonic);


  /**
   * Sets the text used in the <code>ApproveButton</code> in the
   * <code>FileChooserUI</code>.
   *
   * @param approveButtonText the text used in the <code>ApproveButton</code>
   *
   * @see #getApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  // PENDING(jeff) - have ui set this on dialog type change
  public void setApproveButtonText(String approveButtonText);

  /**
   * Returns the text used in the <code>ApproveButton</code> in the
   * <code>FileChooserUI</code>.
   * If <code>null</code>, the UI object will determine the button's text.
   *
   * Typically, this would be "Open" or "Save".
   *
   * @return the text used in the <code>ApproveButton</code>
   *
   * @see #setApproveButtonText
   * @see #setDialogType
   * @see #showDialog
   */
  public String getApproveButtonText();

  /**
   * Gets the list of user choosable file filters.
   *
   * @return a <code>FileFilter</code> array containing all the choosable
   *         file filters
   *
   * @see #addChoosableFileFilter
   * @see #removeChoosableFileFilter
   * @see #resetChoosableFileFilters
   */
  public FileFilter[] getChoosableFileFilters();

  /**
   * Adds a filter to the list of user choosable file filters.
   * For information on setting the file selection mode, see
   * {@link #setFileSelectionMode setFileSelectionMode}.
   *
   * @param filter the <code>FileFilter</code> to add to the choosable file
   *               filter list
   *
   * @see #getChoosableFileFilters
   * @see #removeChoosableFileFilter
   * @see #resetChoosableFileFilters
   * @see #setFileSelectionMode
   */
  public void addChoosableFileFilter(FileFilter filter);

  /**
   * Removes a filter from the list of user choosable file filters. Returns
   * true if the file filter was removed.
   *
   * @param f the file filter to be removed
   * @return true if the file filter was removed, false otherwise
   * @see #addChoosableFileFilter
   * @see #getChoosableFileFilters
   * @see #resetChoosableFileFilters
   */
  public boolean removeChoosableFileFilter(FileFilter f);

  /**
   * Resets the choosable file filter list to its starting state. Normally,
   * this removes all added file filters while leaving the
   * <code>AcceptAll</code> file filter.
   *
   * @see #addChoosableFileFilter
   * @see #getChoosableFileFilters
   * @see #removeChoosableFileFilter
   */
  public void resetChoosableFileFilters();

  /**
   * Returns the <code>AcceptAll</code> file filter.
   * For example, on Microsoft Windows this would be All Files (*.*).
   *
   * @return the {@code AcceptAll} file filter
   */
  public FileFilter getAcceptAllFileFilter();

  /**
   * Returns whether the <code>AcceptAll FileFilter</code> is used.
   * @return true if the <code>AcceptAll FileFilter</code> is used
   * @see #setAcceptAllFileFilterUsed
   * @since 1.3
   */
  public boolean isAcceptAllFileFilterUsed();

  /**
   * Determines whether the <code>AcceptAll FileFilter</code> is used
   * as an available choice in the choosable filter list.
   * If false, the <code>AcceptAll</code> file filter is removed from
   * the list of available file filters.
   * If true, the <code>AcceptAll</code> file filter will become the
   * actively used file filter.
   *
   * @param b a {@code boolean} which determines whether the {@code AcceptAll}
   *          file filter is an available choice in the choosable filter list
   *
   * @see #isAcceptAllFileFilterUsed
   * @see #getAcceptAllFileFilter
   * @see #setFileFilter
   * @since 1.3
   */
  public void setAcceptAllFileFilterUsed(boolean b);

  /**
   * Returns the accessory component.
   *
   * @return this JFileChooser's accessory component, or null
   * @see #setAccessory
   */
  public JComponent getAccessory();

  /**
   * Sets the accessory component. An accessory is often used to show a
   * preview image of the selected file; however, it can be used for anything
   * that the programmer wishes, such as extra custom file chooser controls.
   *
   * <p>
   * Note: if there was a previous accessory, you should unregister
   * any listeners that the accessory might have registered with the
   * file chooser.
   *
   * @param newAccessory the accessory component to be set
   */
  public void setAccessory(JComponent newAccessory);

  /**
   * Sets the <code>JFileChooser</code> to allow the user to just
   * select files, just select
   * directories, or select both files and directories.  The default is
   * <code>JFilesChooser.FILES_ONLY</code>.
   *
   * @param mode the type of files to be displayed:
   * <ul>
   * <li>JFileChooser.FILES_ONLY
   * <li>JFileChooser.DIRECTORIES_ONLY
   * <li>JFileChooser.FILES_AND_DIRECTORIES
   * </ul>
   *
   * @exception IllegalArgumentException  if <code>mode</code> is an
   *                          illegal file selection mode
   *
   * @see #getFileSelectionMode
   */
  public void setFileSelectionMode(int mode);

  /**
   * Returns the current file-selection mode.  The default is
   * <code>JFilesChooser.FILES_ONLY</code>.
   *
   * @return the type of files to be displayed, one of the following:
   * <ul>
   * <li>JFileChooser.FILES_ONLY
   * <li>JFileChooser.DIRECTORIES_ONLY
   * <li>JFileChooser.FILES_AND_DIRECTORIES
   * </ul>
   * @see #setFileSelectionMode
   */
  public int getFileSelectionMode();

  /**
   * Convenience call that determines if files are selectable based on the
   * current file selection mode.
   *
   * @return true if files are selectable, false otherwise
   * @see #setFileSelectionMode
   * @see #getFileSelectionMode
   */
  public boolean isFileSelectionEnabled();

  /**
   * Convenience call that determines if directories are selectable based
   * on the current file selection mode.
   *
   * @return true if directories are selectable, false otherwise
   * @see #setFileSelectionMode
   * @see #getFileSelectionMode
   */
  public boolean isDirectorySelectionEnabled();

  /**
   * Sets the file chooser to allow multiple file selections.
   *
   * @param b true if multiple files may be selected
   *
   * @see #isMultiSelectionEnabled
   */
  public void setMultiSelectionEnabled(boolean b);

  /**
   * Returns true if multiple files can be selected.
   * @return true if multiple files can be selected
   * @see #setMultiSelectionEnabled
   */
  public boolean isMultiSelectionEnabled();


  /**
   * Returns true if hidden files are not shown in the file chooser;
   * otherwise, returns false.
   *
   * @return the status of the file hiding property
   * @see #setFileHidingEnabled
   */
  public boolean isFileHidingEnabled();

  /**
   * Sets file hiding on or off. If true, hidden files are not shown
   * in the file chooser. The job of determining which files are
   * shown is done by the <code>FileView</code>.
   *
   * @param b the boolean value that determines whether file hiding is
   *          turned on
   * @see #isFileHidingEnabled
   */
  public void setFileHidingEnabled(boolean b);

  /**
   * Sets the current file filter. The file filter is used by the
   * file chooser to filter out files from the user's view.
   *
   * @param filter the new current file filter to use
   * @see #getFileFilter
   */
  public void setFileFilter(FileFilter filter);


  /**
   * Returns the currently selected file filter.
   *
   * @return the current file filter
   * @see #setFileFilter
   * @see #addChoosableFileFilter
   */
  public FileFilter getFileFilter();

  /**
   * Sets the file view to be used to retrieve UI information, such as
   * the icon that represents a file or the type description of a file.
   *
   * @param fileView a {@code FileView} to be used to retrieve UI information
   *
   * @see #getFileView
   */
  public void setFileView(FileView fileView);

  /**
   * Returns the current file view.
   *
   * @return the current file view
   * @see #setFileView
   */
  public FileView getFileView();

  // ******************************
  // *****FileView delegation *****
  // ******************************

  // NOTE: all of the following methods attempt to delegate
  // first to the client set fileView, and if <code>null</code> is returned
  // (or there is now client defined fileView) then calls the
  // UI's default fileView.

  /**
   * Returns the filename.
   * @param f the <code>File</code>
   * @return the <code>String</code> containing the filename for
   *          <code>f</code>
   * @see FileView#getName
   */
  public String getName(File f);

  /**
   * Returns the file description.
   * @param f the <code>File</code>
   * @return the <code>String</code> containing the file description for
   *          <code>f</code>
   * @see FileView#getDescription
   */
  public String getDescription(File f);

  /**
   * Returns the file type.
   * @param f the <code>File</code>
   * @return the <code>String</code> containing the file type description for
   *          <code>f</code>
   * @see FileView#getTypeDescription
   */
  public String getTypeDescription(File f);

  /**
   * Returns the icon for this file or type of file, depending
   * on the system.
   * @param f the <code>File</code>
   * @return the <code>Icon</code> for this file, or type of file
   * @see FileView#getIcon
   */
  public Icon getIcon(File f);

  /**
   * Returns true if the file (directory) can be visited.
   * Returns false if the directory cannot be traversed.
   * @param f the <code>File</code>
   * @return true if the file/directory can be traversed, otherwise false
   * @see FileView#isTraversable
   */
  public boolean isTraversable(File f);

  /**
   * Returns true if the file should be displayed.
   * @param f the <code>File</code>
   * @return true if the file should be displayed, otherwise false
   * @see FileFilter#accept
   */
  public boolean accept(File f);

  /**
   * Sets the file system view that the <code>JFileChooser</code> uses for
   * accessing and creating file system resources, such as finding
   * the floppy drive and getting a list of root drives.
   * @param fsv  the new <code>FileSystemView</code>
   *
   * @see FileSystemView
   */
  public void setFileSystemView(FileSystemView fsv);

  /**
   * Returns the file system view.
   * @return the <code>FileSystemView</code> object
   * @see #setFileSystemView
   */
  public FileSystemView getFileSystemView();

  // **************************
  // ***** Event Handling *****
  // **************************

  /**
   * Called by the UI when the user hits the Approve button
   * (labeled "Open" or "Save", by default). This can also be
   * called by the programmer.
   * This method causes an action event to fire
   * with the command string equal to
   * <code>APPROVE_SELECTION</code>.
   *
   * @see #APPROVE_SELECTION
   */
  public void approveSelection();

  /**
   * Called by the UI when the user chooses the Cancel button.
   * This can also be called by the programmer.
   * This method causes an action event to fire
   * with the command string equal to
   * <code>CANCEL_SELECTION</code>.
   *
   * @see #CANCEL_SELECTION
   */
  public void cancelSelection();

  /**
   * Adds an <code>ActionListener</code> to the file chooser.
   *
   * @param l  the listener to be added
   *
   * @see #approveSelection
   * @see #cancelSelection
   */
  public void addActionListener(ActionListener l);

  /**
   * Removes an <code>ActionListener</code> from the file chooser.
   *
   * @param l  the listener to be removed
   *
   * @see #addActionListener
   */
  public void removeActionListener(ActionListener l);

  /**
   * Returns an array of all the action listeners
   * registered on this file chooser.
   *
   * @return all of this file chooser's <code>ActionListener</code>s
   *         or an empty
   *         array if no action listeners are currently registered
   *
   * @see #addActionListener
   * @see #removeActionListener
   *
   * @since 1.4
   */
  public ActionListener[] getActionListeners();

  // *********************************
  // ***** Pluggable L&F methods *****
  // *********************************

  /**
   * Resets the UI property to a value from the current look and feel.
   *
   * @see JComponent#updateUI
   */
  public void updateUI();

  /**
   * Returns a string that specifies the name of the L&amp;F class
   * that renders this component.
   *
   * @return the string "FileChooserUI"
   * @see JComponent#getUIClassID
   * @see UIDefaults#getUI
   */
  public String getUIClassID();

  /**
   * Gets the UI object which implements the L&amp;F for this component.
   *
   * @return the FileChooserUI object that implements the FileChooserUI L&amp;F
   */
  public FileChooserUI getUI();

  /**
   * Gets the AccessibleContext associated with this JFileChooser.
   * For file choosers, the AccessibleContext takes the form of an
   * AccessibleJFileChooser.
   * A new AccessibleJFileChooser instance is created if necessary.
   *
   * @return an AccessibleJFileChooser that serves as the
   *         AccessibleContext of this JFileChooser
   */
  public AccessibleContext getAccessibleContext();
}

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
 * BaseFileChooser.java
 * Copyright (C) 2009-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.Utils;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingLevel;
import adams.gui.core.ConsolePanel;
import adams.gui.core.ExtensionFileFilter;
import adams.gui.core.GUIHelper;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.io.File;

/**
 * A file chooser dialog based on the one developed by the 
 * <a href="http://vfsjfilechooser.sourceforge.net/" target="_blank">VFSJFileChooser project</a>.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BaseFileChooser
  extends JFileChooser {

  /** for serialization. */
  private static final long serialVersionUID = -5712455182900852653L;
  
  /** whether to ask to overwrite an existing file (using the save dialog). */
  protected boolean m_PromptOverwriteFile;

  /** whether to automatically append extension. */
  protected boolean m_AutoAppendExtension;

  /** whether glob filters (eg "*.txt") are allowed. */
  protected boolean m_AllowGlobFilters;

  /** the default extension. */
  protected String m_DefaultExtension;
  
  /** the bookmarks. */
  protected FileChooserBookmarksPanel m_PanelBookmarks;

  /**
   * Constructs a <code>BaseFileChooser</code> pointing to the user's
   * default directory. This default depends on the operating system.
   * It is typically the "My Documents" folder on Windows, and the
   * user's home directory on Unix.
   */
  public BaseFileChooser() {
    super();

    initialize();
  }

  /**
   * Constructs a <code>BaseFileChooser</code> using the given path.
   * Passing in a <code>null</code>
   * string causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectoryPath  a <code>String</code> giving the path
   *				to a file or directory
   */
  public BaseFileChooser(String currentDirectoryPath) {
    super(currentDirectoryPath);

    initialize();
  }

  /**
   * Constructs a <code>BaseFileChooser</code> using the given <code>File</code>
   * as the path. Passing in a <code>null</code> file
   * causes the file chooser to point to the user's default directory.
   * This default depends on the operating system. It is
   * typically the "My Documents" folder on Windows, and the user's
   * home directory on Unix.
   *
   * @param currentDirectory  a <code>File</code> object specifying
   *				the path to a file or directory
   */
  public BaseFileChooser(File currentDirectory) {
    super(currentDirectory);

    initialize();
  }

  /**
   * For initializing some stuff.
   */
  protected void initialize() {
    JComponent		accessory;
    int			width;
    int			height;
    
    m_PromptOverwriteFile = true;
    m_AutoAppendExtension = false;
    m_AllowGlobFilters    = true;
    m_DefaultExtension    = null;
    
    accessory = createAccessoryPanel();
    if (accessory != null)
      setAccessory(accessory);

    width  = GUIHelper.getInteger("BaseFileChooser.Width", 750);
    height = GUIHelper.getInteger("BaseFileChooser.Height", 500);
    if ((width != -1) && (height != -1))
      setPreferredSize(new Dimension(width, height));
  }

  /**
   * Returns the preferred dimension.
   * 
   * @return		the dimension, null if to use default
   */
  protected Dimension getDefaultAccessoryDimension() {
    int		height;
    int		width;
    
    width  = GUIHelper.getInteger("BaseFileChooser.Accessory.Width", -1);
    height = GUIHelper.getInteger("BaseFileChooser.Accessory.Height", -1);
    if ((width != -1) && (height != -1))
      return new Dimension(width, height);
    else
      return null;
  }
  
  /**
   * Creates an accessory panel displayed next to the files.
   * 
   * @return		the panel or null if none available
   */
  protected JComponent createAccessoryPanel() {
    Dimension	dim;
    
    m_PanelBookmarks = new FileChooserBookmarksPanel();
    m_PanelBookmarks.setOwner(this);
    m_PanelBookmarks.setBorder(BorderFactory.createEmptyBorder(2, 5, 0, 0));
    dim = getDefaultAccessoryDimension();
    if (dim != null) {
      m_PanelBookmarks.setSize(dim);
      m_PanelBookmarks.setMinimumSize(dim);
      m_PanelBookmarks.setPreferredSize(dim);
    }
    
    return m_PanelBookmarks;
  }
  
  /**
   * Sets whether the user gets prompted by the save dialog if the selected file
   * already exists.
   *
   * @param value	if true, then the user will get prompted if file
   * 			already exists
   */
  public void setPromptOverwriteFile(boolean value) {
    m_PromptOverwriteFile = value;
  }

  /**
   * Returns whether the user gets prompted by the save dialog if the selected
   * file already exists.
   *
   * @return		true if the user will get prompted
   */
  public boolean getPromptOverwriteFile() {
    return m_PromptOverwriteFile;
  }

  /**
   * Sets whether to automatically append the currently selected file extension
   * or the default one (if All-Filter is used).
   *
   * @param value	if true, then the file extension will be added
   * 			automatically
   */
  public void setAutoAppendExtension(boolean value) {
    m_AutoAppendExtension = value;
  }

  /**
   * Returns whether to automatically append the currently selected file extension
   * or the default one (if All-Filter is used).
   *
   * @return		true if the file extension will be added
   * 			automatically
   */
  public boolean getAutoAppendExtension() {
    return m_AutoAppendExtension;
  }

  /**
   * Sets the default extension. Is used if m_AutoAppendExtension is true
   * and the All-Filter is selected.
   *
   * @param value	the extension (without dot), use null to unset
   */
  public void setDefaultExtension(String value) {
    m_DefaultExtension = value;
  }

  /**
   * Returns the default extension. Is used if m_AutoAppendExtension is true
   * and the All-Filter is selected.
   *
   * @return		the extension, can be null
   */
  public String getDefaultExtension() {
    return m_DefaultExtension;
  }

  /**
   * Sets whether the user can enter glob filters like "*.txt".
   *
   * @param value	if true, then the user can enter glob filters
   */
  public void setAllowGlobFilters(boolean value) {
    m_AllowGlobFilters = value;
  }

  /**
   * Returns whether the user can enter glob filters like "*.txt".
   *
   * @return		true if the user can enter glob filters
   */
  public boolean getAllowGlobFilters() {
    return m_AllowGlobFilters;
  }

  /**
   * Returns whether the filter is a "glob" filter, e.g., when the user
   * enters "*.txt" manually.
   * 
   * @param filter	the filter to check
   * @return		true if a glob filter
   */
  protected boolean isGlobFilter(FileFilter filter) {
    if (filter == null)
      return false;
    
    // TODO: classname?
    return filter.getClass().getName().endsWith("$GlobFilter");
  }
  
  /**
   * Checks the filter whether it can be set.
   * 
   * @param filter	the filter to set
   * @return		true if check passed and valid
   */
  protected boolean checkFilter(FileFilter filter) {
    boolean	result;
    
    if (filter == null)
      return true;
    
    result =    (filter instanceof ExtensionFileFilter)
	 || (filter == getAcceptAllFileFilter())
	 || (isGlobFilter(filter) && getAllowGlobFilters());
    
    if (!result) {
      if (isGlobFilter(filter)) {
	System.err.println(
	    "Please select a filter from the drop-down list!");
      }
      else {
	System.err.println(
	    "Only instances of " + ExtensionFileFilter.class.getName() + " are accepted!\n"
		+ "Provided: " + filter.getClass().getName());
      }
    }

    return result;
  }
  
  @Override
  public void setFileFilter(FileFilter filter) {
    if (checkFilter(filter))
      super.setFileFilter(filter);
  }
  
  /**
   * Adds the file filter. Has to be a <code>ExtensionFileFilter</code>.
   *
   * @param filter	the filter to add
   * @see		ExtensionFileFilter
   */
  @Override
  public void addChoosableFileFilter(FileFilter filter) {
    if (checkFilter(filter))
      super.addChoosableFileFilter(filter);
  }

  /**
   * Sets the selected file. If the file's parent directory is
   * not the current directory, changes the current directory
   * to be the file's parent directory.
   *
   * @see #getSelectedFile
   * @param file the selected file
   */
  @Override
  public void setSelectedFile(File file) {
    File	selFile;

    selFile = null;

    if (file != null)
      selFile = new File(file.getAbsolutePath());
    if (selFile == null)
      return;

    try {
      // sometimes property change events result in exceptions
      super.setSelectedFile(selFile);
    }
    catch (Exception e) {
      super.setSelectedFile(selFile);
    }
  }

  /**
   * Returns the selected file as PlaceholderFile. This can be set either by the
   * programmer via <code>setFile</code> or by a user action, such as
   * either typing the filename into the UI or selecting the
   * file from a list in the UI.
   *
   * @return the selected file, converted to PlaceholderFile
   */
  public PlaceholderFile getSelectedPlaceholderFile() {
    File 	file;

    file = super.getSelectedFile();

    if (file != null)
      return new PlaceholderFile(file);
    else
      return null;
  }

  /**
   * Sets the list of selected files if the file chooser is
   * set to allow multiple selection.
   *
   * @param selectedFiles	the files to select initially
   */
  @Override
  public void setSelectedFiles(File[] selectedFiles) {
    File[]	files;
    int		i;

    files = null;
    if (selectedFiles != null) {
      files = new File[selectedFiles.length];
      for (i = 0; i < selectedFiles.length; i++)
	files[i] = selectedFiles[i].getAbsoluteFile();
    }

    try {
      // sometimes property change events result in exceptions
      super.setSelectedFiles(files);
    }
    catch (Exception e) {
      super.setSelectedFiles(files);
    }
  }

  /**
   * Returns a list of selected files if the file
   * chooser is set to allow multiple selection.
   * <br><br>
   * Fixes the problem with JFileChooser of not returning anything sometimes 
   * when only a single file is selected.
   *
   * @return the selected files
   */
  @Override
  public File[] getSelectedFiles() {
    File[]	result;
    
    result = super.getSelectedFiles();
    if ((result.length == 0) && (getSelectedFile() != null))
      result = new File[]{getSelectedFile()};
    
    return result;
  }
  
  /**
   * Returns a list of selected files as PlaecholderFile objects if the file
   * chooser is set to allow multiple selection.
   *
   * @return the selected files, converted to PlaceholderFile objects
   */
  public PlaceholderFile[] getSelectedPlaceholderFiles() {
    PlaceholderFile[]	result;
    File[]		files;
    int			i;

    files  = getSelectedFiles();
    result = new PlaceholderFile[files.length];
    for (i = 0; i < result.length; i++)
      result[i] = new PlaceholderFile(files[i]);

    return result;
  }

  /**
   * Checks the filename, whether it has an extension from the provided list.
   *
   * @param file	the file to check
   * @param extensions	the extensions to check against
   * @return		true if valid extension
   */
  protected boolean hasCorrectExtension(File file, String[] extensions) {
    boolean	result;
    String	filename;
    int		i;

    result = false;
    
    // open: file exists -> correct extension
    if (getDialogType() == OPEN_DIALOG) 
      result = file.exists();
    // save: all files -> correct extension
    if ((getDialogType() == SAVE_DIALOG) && (getFileFilter() == getAcceptAllFileFilter()))
      result = true;
    // save: glob filter -> correct extension
    if ((getDialogType() == SAVE_DIALOG) && (isGlobFilter(getFileFilter())))
      result = true;

    if (!result) {
      filename = file.getAbsolutePath().toLowerCase();
      for (i = 0; i < extensions.length; i++) {
	if (filename.endsWith("." + extensions[i].toLowerCase())) {
	  result = true;
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Dislpays the dialog.
   *
   * @param   parent  the parent component of the dialog;
   *			can be <code>null</code>
   * @param   approveButtonText the text of the <code>ApproveButton</code>
   * @return  the return state of the file chooser on popdown
   * @throws HeadlessException 	if GraphicsEnvironment.isHeadless()
   * 				returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  @Override
  public int showDialog(Component parent, String approveButtonText) {
    int			result;
    int			retVal;
    File[]		selfiles;
    File		selfile;
    int			i;
    String[]		extensions;
    
    m_PanelBookmarks.reload();
    
    parent = GUIHelper.getParentComponent(parent);
    result = super.showDialog(parent, approveButtonText);

    // fix extensions?
    if (m_AutoAppendExtension) {
      // determine extension to add
      if ((getFileFilter() != getAcceptAllFileFilter()) && !isGlobFilter(getFileFilter()))
	extensions = ((ExtensionFileFilter) getFileFilter()).getExtensions();
      else
	extensions = new String[]{getDefaultExtension()};

      // fix extensions if necessary
      if (extensions != null) {
	if (isMultiSelectionEnabled()) {
	  selfiles = getSelectedFiles();
	  for (i = 0; i < selfiles.length; i++) {
	    if (!hasCorrectExtension(selfiles[i], extensions))
	      selfiles[i] = new File(selfiles[i].getAbsolutePath() + "." + extensions[0]);
	  }
	  setSelectedFiles(selfiles);
	}
	else {
	  selfile = getSelectedFile();
	  if (selfile != null) {
	    if (!hasCorrectExtension(selfile, extensions))
	      selfile = new File(selfile.getAbsolutePath() + "." + extensions[0]);
	    setSelectedFile(selfile);
	  }
	}
      }
    }

    // do we have to prompt user to confirm overwriting of file?
    if (    (result == APPROVE_OPTION) && (getDialogType() == SAVE_DIALOG)
	 && ((getFileSelectionMode() == FILES_AND_DIRECTORIES) || (getFileSelectionMode() == FILES_ONLY)) ) {
      if (    getPromptOverwriteFile() && (getSelectedFile() != null)
	   && getSelectedFile().exists() && !getSelectedFile().isDirectory()) {
	retVal = GUIHelper.showConfirmMessage(
	    parent,
	    "File already exists - overwrite?\n" + getSelectedFile(),
	    "File exists already");

	switch (retVal) {
	  case JOptionPane.YES_OPTION:
	    break;

	  case JOptionPane.NO_OPTION:
	    result = showSaveDialog(parent);
	    break;

	  case JOptionPane.CANCEL_OPTION:
	    result = CANCEL_OPTION;
	    break;
	}
      }
    }

    return result;
  }

  /**
   * Returns the current directory.
   *
   * @return the current directory, as PlaceholderDirectory
   * @see #setCurrentDirectory
   */
  public PlaceholderDirectory getCurrentPlaceholderDirectory() {
    File	current;

    current = super.getCurrentDirectory();
    if (current == null)
      return null;
    else
      return new PlaceholderDirectory(current);
  }

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
  @Override
  public void setCurrentDirectory(File dir) {
    try {
      if (dir == null)
	super.setCurrentDirectory(null);
      else
	super.setCurrentDirectory(dir.getAbsoluteFile());
    }
    catch (Exception e) {
      // sometimes property change events result in exceptions
      if (dir == null)
	super.setCurrentDirectory(null);
      else
	super.setCurrentDirectory(dir.getAbsoluteFile());
    }
  }

  /**
   * Outputs the error in the console window.
   *
   * @param msg		the message
   * @param t 		the exception
   */
  protected static void handleException(String msg, Throwable t) {
    ConsolePanel.getSingleton().append(LoggingLevel.SEVERE, msg + "\n" + Utils.throwableToString(t));
    System.err.println(msg);
    t.printStackTrace();
  }
}

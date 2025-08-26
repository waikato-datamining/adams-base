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
 * FileChooserPanel.java
 * Copyright (C) 2008-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.PlaceholderFileHistory;
import adams.core.Placeholders;
import adams.core.io.AbsolutePathSupporter;
import adams.core.io.PlaceholderFile;
import adams.env.Environment;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.PopupMenuActions;
import adams.gui.event.HistorySelectionEvent;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.JFileChooser;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A panel that contains a text field with the current file/directory and a
 * button for bringing up a BaseFileChooser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FileChooserPanel
  extends AbstractChooserPanel<File>
  implements AbsolutePathSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /** the JFileChooser for selecting a file. */
  protected BaseFileChooser m_FileChooser;

  /** whether to use the open or save dialog. */
  protected boolean m_UseSaveDialog;

  /** the history of files. */
  protected static Map<Class,PlaceholderFileHistory> m_History;

  /** the title to use for the filechooser. */
  protected String m_FileChooserTitle;

  /** the default filechooser title. */
  protected String m_FileChooserTitleDefault;

  /** whether to use absolute path rather than placeholders. */
  protected boolean m_UseAbsolutePath;

  /**
   * Initializes the panel with no file.
   */
  public FileChooserPanel() {
    this("");
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public FileChooserPanel(String path) {
    this(new PlaceholderFile(path));
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public FileChooserPanel(File path) {
    super();

    setCurrent(path);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser             = new BaseFileChooser();
    m_FileChooserTitleDefault = m_FileChooser.getDialogTitle();
    m_FileChooserTitle        = "";
    m_UseAbsolutePath = false;
    m_UseSaveDialog           = false;

    if (m_History == null)
      m_History = new HashMap<>();
    if (!m_History.containsKey(getClass())) {
      m_History.put(getClass(), new PlaceholderFileHistory());
      m_History.get(getClass()).setHistoryFile(
	new PlaceholderFile(
	  Environment.getInstance().getHome() + File.separator + getClass().getName() + ".txt"));
    }
  }

  /**
   * Sets the title for the filechooser.
   *
   * @param value	the title, null or empty string for default
   */
  public void setFileChooserTitle(String value) {
    if (value == null)
      value = "";
    m_FileChooserTitle = value;
  }

  /**
   * Returns the tile for the filechooser.
   *
   * @return		the title, empty string for default
   */
  public String getFileChooserTitle() {
    return m_FileChooserTitle;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected File doChoose() {
    if (!m_FileChooserTitle.isEmpty())
      m_FileChooser.setDialogTitle(m_FileChooserTitle);
    else
      m_FileChooser.setDialogTitle(m_FileChooserTitleDefault);
    m_FileChooser.setSelectedFile(getCurrent());

    if (m_UseSaveDialog) {
      if (m_FileChooser.showSaveDialog(m_Self) == BaseFileChooser.APPROVE_OPTION) {
	m_History.get(getClass()).add(new PlaceholderFile(m_FileChooser.getSelectedFile()));
	return m_FileChooser.getSelectedFile();
      }
      else {
	return null;
      }
    }
    else {
      if (m_FileChooser.showOpenDialog(m_Self) == BaseFileChooser.APPROVE_OPTION) {
	m_History.get(getClass()).add(new PlaceholderFile(m_FileChooser.getSelectedFile()));
	return m_FileChooser.getSelectedFile();
      }
      else {
	return null;
      }
    }
  }

  /**
   * Copies the current file to the clipboard.
   */
  protected void copyToClipboard() {
    String  	allText;
    String  	selText;
    boolean	copyAll;

    selText = m_TextSelection.getSelectedText();
    allText = m_TextSelection.getText();
    copyAll = (selText == null) || (selText.equals(allText));
    if (copyAll)
      ClipboardHelper.copyToClipboard(fromString(allText).getAbsolutePath());
    else
      ClipboardHelper.copyToClipboard(selText);
  }

  /**
   * Hook method after pasting from clipboard.
   */
  @Override
  protected void afterPasteFromClipboard() {
    super.afterPasteFromClipboard();
    m_History.get(getClass()).add(new PlaceholderFile(m_FileChooser.getSelectedFile()));
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(File value) {
    if (m_UseAbsolutePath)
      return value.getAbsolutePath();
    else
      return Placeholders.collapseStr(value.getAbsolutePath());
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected File fromString(String value) {
    File	result;

    result = new PlaceholderFile(value.trim());

    if (m_UseAbsolutePath)
      return result.getAbsoluteFile();
    else
      return result;
  }

  /**
   * Checks whether the string value is valid and can be parsed.
   *
   * @param value	the value to check
   * @return		true if valid
   */
  @Override
  protected boolean isValid(String value) {
    return (value != null) && PlaceholderFile.isValid(value.trim());
  }

  /**
   * Sets whether to use absolute paths.
   *
   * @param value	if true if absolute paths
   */
  @Override
  public void setUseAbsolutePath(boolean value) {
    m_UseAbsolutePath = value;
  }

  /**
   * Returns whether to use absolute paths.
   *
   * @return		true if absolute paths
   */
  @Override
  public boolean getUseAbsolutePath() {
    return m_UseAbsolutePath;
  }

  /**
   * Sets the selection mode, whether only files or directories, or both can
   * be selected. FILES_ONLY is the default.
   *
   * @param value	the mode
   */
  public void setFileSelectionMode(int value) {
    m_FileChooser.setFileSelectionMode(value);
  }

  /**
   * Returns the selection mode, whether only files or directories, or both
   * can be selected. FILES_ONLY is the default.
   *
   * @return		the mode
   * @see		JFileChooser#FILES_ONLY
   * @see		JFileChooser#DIRECTORIES_ONLY
   * @see		JFileChooser#FILES_AND_DIRECTORIES
   */
  public int getFileSelectionMode() {
    return m_FileChooser.getFileSelectionMode();
  }

  /**
   * Adds the given file filter to the filechooser.
   *
   * @param value	the file filter to add
   */
  public void addChoosableFileFilter(FileFilter value) {
    FileFilter	current;

    current = m_FileChooser.getFileFilter();
    m_FileChooser.addChoosableFileFilter(value);
    m_FileChooser.setFileFilter(current);
  }

  /**
   * Removes the specified file filter from the filechooser.
   *
   * @param value	the file filter to remove
   */
  public void removeChoosableFileFilter(FileFilter value) {
    m_FileChooser.removeChoosableFileFilter(value);
  }

  /**
   * Removes all file filters from the filechooser.
   */
  public void removeChoosableFileFilters() {
    FileFilter[]	filters;

    filters = m_FileChooser.getChoosableFileFilters();
    for (FileFilter filter: filters)
      m_FileChooser.removeChoosableFileFilter(filter);
  }

  /**
   * Returns all choosable file filters.
   *
   * @return		the current file filters
   */
  public FileFilter[] getChoosableFileFilters() {
    return m_FileChooser.getChoosableFileFilters();
  }

  /**
   * Sets whether the "accept all files" filter is used.
   *
   * @param value	if true then the filter will be used
   */
  public void setAcceptAllFileFilterUsed(boolean value) {
    m_FileChooser.setAcceptAllFileFilterUsed(value);
  }

  /**
   * Returns whether the "accept all files" filter is used.
   *
   * @return		true if the filter is used
   */
  public boolean isAcceptAllFileFilterUsed() {
    return m_FileChooser.isAcceptAllFileFilterUsed();
  }

  /**
   * Sets the active file filter.
   *
   * @param value	the file filter to select
   */
  public void setFileFilter(FileFilter value) {
    m_FileChooser.setFileFilter(value);
  }

  /**
   * Returns the active file filter.
   *
   * @return		the current file filter
   */
  public FileFilter getFileFilter() {
    return m_FileChooser.getFileFilter();
  }

  /**
   * Sets whether the user gets prompted by the save dialog if the selected file
   * already exists.
   *
   * @param value	if true, then the user will get prompted if file
   * 			already exists
   */
  public void setPromptOverwriteFile(boolean value) {
    m_FileChooser.setPromptOverwriteFile(value);
  }

  /**
   * Returns whether the user gets prompted by the save dialog if the selected
   * file already exists.
   *
   * @return		true if the user will get prompted
   */
  public boolean getPromptOverwriteFile() {
    return m_FileChooser.getPromptOverwriteFile();
  }

  /**
   * Sets whether to automatically append the currently selected file extension
   * or the default one (if All-Filter is used).
   *
   * @param value	if true, then the file extension will be added
   * 			automatically
   */
  public void setAutoAppendExtension(boolean value) {
    m_FileChooser.setAutoAppendExtension(value);
  }

  /**
   * Returns whether to automatically append the currently selected file extension
   * or the default one (if All-Filter is used).
   *
   * @return		true if the file extension will be added
   * 			automatically
   */
  public boolean getAutoAppendExtension() {
    return m_FileChooser.getAutoAppendExtension();
  }

  /**
   * Sets the default extension. Is used if m_AutoAppendExtension is true
   * and the All-Filter is selected.
   *
   * @param value	the extension (without dot), use null to unset
   */
  public void setDefaultExtension(String value) {
    m_FileChooser.setDefaultExtension(value);
  }

  /**
   * Returns the default extension. Is used if m_AutoAppendExtension is true
   * and the All-Filter is selected.
   *
   * @return		the extension, can be null
   */
  public String getDefaultExtension() {
    return m_FileChooser.getDefaultExtension();
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(File value) {
    m_FileChooser.setCurrentDirectory(new PlaceholderFile(value).getAbsoluteFile());
  }

  /**
   * Returns the current directory in use by the file chooser.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_FileChooser.getCurrentDirectory();
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   * @return		true if successfully set
   */
  @Override
  public boolean setCurrent(File value) {
    boolean	result;

    result = super.setCurrent(new PlaceholderFile(value));
    m_FileChooser.setSelectedFile(getCurrent().getAbsoluteFile());

    return result;
  }

  /**
   * Sets whether to use the save or open dialog.
   *
   * @param value	if true the save dialog is used
   */
  public void setUseSaveDialog(boolean value) {
    m_UseSaveDialog = value;
  }

  /**
   * Returns whether the save or open dialog is used.
   *
   * @return	true if the save dialog is used
   */
  public boolean getUseSaveDialog() {
    return m_UseSaveDialog;
  }

  /**
   * Returns a popup menu when right-clicking on the edit field.
   *
   * @return		the menu, null if non available
   */
  @Override
  protected BasePopupMenu getPopupMenu() {
    BasePopupMenu	result;

    result = super.getPopupMenu();

    result.addSeparator();

    PopupMenuActions.openInPreviewBrowser(result, getCurrent());
    PopupMenuActions.openInFileBrowser(result, getCurrent());
    PopupMenuActions.openInTerminal(result, getCurrent());

    result.addSeparator();

    m_History.get(getClass()).customizePopupMenu(
      result,
      getCurrent(),
      (HistorySelectionEvent e) -> {
	setCurrent((File) e.getHistoryItem());
	notifyChangeListeners(new ChangeEvent(m_Self));
      });

    return result;
  }
}

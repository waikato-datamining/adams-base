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
 * MultipleFileChooserPanel.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.Placeholders;
import adams.core.io.AbsolutePathSupporter;
import adams.core.io.PlaceholderFile;
import adams.core.logging.LoggingHelper;
import adams.core.option.OptionUtils;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.filechooser.FileFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * A panel that contains a text field with the current files and a
 * button for bringing up a BaseFileChooser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultipleFileChooserPanel
  extends AbstractChooserPanel<File[]>
  implements AbsolutePathSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /** the JFileChooser for selecting files. */
  protected BaseFileChooser m_FileChooser;

  /** the title to use for the filechooser. */
  protected String m_FileChooserTitle;

  /** the default filechooser title. */
  protected String m_FileChooserTitleDefault;

  /** whether to use absolute path rather than placeholders. */
  protected boolean m_UseAbsolutePath;

  /**
   * Initializes the panel with no file.
   */
  public MultipleFileChooserPanel() {
    this("");
  }

  /**
   * Initializes the panel with the given filename.
   *
   * @param path	the filename to use
   */
  public MultipleFileChooserPanel(String path) {
    this(new PlaceholderFile(path));
  }

  /**
   * Initializes the panel with the given filenames.
   *
   * @param paths	the filenames to use
   */
  public MultipleFileChooserPanel(String[] paths) {
    this(toFiles(paths));
  }

  /**
   * Initializes the panel with the given filename.
   *
   * @param path	the filename to use
   */
  public MultipleFileChooserPanel(File path) {
    super();

    setCurrent(new File[]{path});
  }

  /**
   * Initializes the panel with the given filenames.
   *
   * @param paths	the filenames to use
   */
  public MultipleFileChooserPanel(File[] paths) {
    super();

    setCurrent(paths);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser             = new BaseFileChooser();
    m_FileChooser.setMultiSelectionEnabled(true);
    m_FileChooserTitleDefault = m_FileChooser.getDialogTitle();
    m_FileChooserTitle        = "";
    m_UseAbsolutePath         = false;
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
   * @return		the chosen objects or null if none chosen
   */
  @Override
  protected File[] doChoose() {
    if (!m_FileChooserTitle.isEmpty())
      m_FileChooser.setDialogTitle(m_FileChooserTitle);
    else
      m_FileChooser.setDialogTitle(m_FileChooserTitleDefault);
    m_FileChooser.setSelectedFiles(getCurrent());
    if (m_FileChooser.showOpenDialog(m_Self) == BaseFileChooser.APPROVE_OPTION) {
      return m_FileChooser.getSelectedFiles();
    }
    else {
      return null;
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
      ClipboardHelper.copyToClipboard(toString(fromString(allText), true));
    else
      ClipboardHelper.copyToClipboard(selText);
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value		the value to convert
   * @param useAbsolutePath 	whether to use the absolute path
   * @return			the generated string
   */
  public String toString(File[] value, boolean useAbsolutePath) {
    List<String> 	paths;
    String		path;

    paths = new ArrayList<>();
    for (File f: value) {
      if (useAbsolutePath)
	path = f.getAbsolutePath();
      else
	path = Placeholders.collapseStr(f.getAbsolutePath());
      paths.add(path);
    }

    return OptionUtils.joinOptions(paths.toArray(new String[0]));
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  @Override
  protected String toString(File[] value) {
    return toString(value, m_UseAbsolutePath);
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  @Override
  protected File[] fromString(String value) {
    File[]	result;
    String[]	paths;
    int		i;

    try {
      paths = OptionUtils.splitOptions(value);
    }
    catch (Exception e) {
      LoggingHelper.global().log(Level.SEVERE, "Failed to split into separate file names: " + value, e);
      paths = new String[]{value};
    }

    result = new File[paths.length];
    for (i = 0; i < paths.length; i++) {
      result[i] = new PlaceholderFile(paths[i].trim());
      if (m_UseAbsolutePath)
	result[i] = result[i].getAbsoluteFile();
    }

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
    String[]	paths;

    if (value == null)
      return false;

    try {
      paths = OptionUtils.splitOptions(value);
      for (String path: paths) {
	if (!PlaceholderFile.isValid(path.trim()))
	  return false;
      }
    }
    catch (Exception e) {
      return false;
    }

    return true;
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
  public boolean setCurrent(File[] value) {
    boolean		result;
    PlaceholderFile[]	files;
    int			i;

    files = new PlaceholderFile[value.length];
    for (i = 0; i < value.length; i++)
      files[i] = new PlaceholderFile(value[i]);

    result = super.setCurrent(files);
    m_FileChooser.setSelectedFiles(getCurrent());

    return result;
  }

  /**
   * Turns the string array into a Files one.
   *
   * @param paths	the strings to convert
   * @return		the generated files
   */
  protected static File[] toFiles(String[] paths) {
    File[]	result;
    int		i;

    result = new File[paths.length];
    for (i = 0; i < paths.length; i++)
      result[i] = new PlaceholderFile(paths[i]);

    return result;
  }
}

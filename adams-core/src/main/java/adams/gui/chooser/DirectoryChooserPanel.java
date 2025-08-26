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
 * DirectoryChooserPanel.java
 * Copyright (C) 2011-2025 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.PlaceholderDirectoryHistory;
import adams.core.Placeholders;
import adams.core.io.AbsolutePathSupporter;
import adams.core.io.PlaceholderDirectory;
import adams.core.io.PlaceholderFile;
import adams.core.io.fileoperations.FileOperations;
import adams.core.io.fileoperations.LocalFileOperations;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.LocalDirectoryLister;
import adams.env.Environment;
import adams.gui.core.BasePopupMenu;
import adams.gui.core.PopupMenuActions;
import adams.gui.event.HistorySelectionEvent;
import com.github.fracpete.jclipboardhelper.ClipboardHelper;

import javax.swing.event.ChangeEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * A panel that contains a text field with the current directory and a
 * button for bringing up a BaseDirectoryChooser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class DirectoryChooserPanel
  extends AbstractChooserPanelWithIOSupport<File>
  implements AbsolutePathSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 6235369491956122980L;

  /** the JFileChooser for selecting a file. */
  protected FileChooser m_DirectoryChooser;

  /** the history of dirs. */
  protected Map<Class,PlaceholderDirectoryHistory> m_History;

  /** the title to use for the dirchooser. */
  protected String m_DirectoryChooserTitle;

  /** the default dirchooser title. */
  protected String m_DirectoryChooserTitleDefault;

  /** whether to use absolute path rather than placeholders. */
  protected boolean m_UseAbsolutePath;

  /**
   * Initializes the panel with no file.
   */
  public DirectoryChooserPanel() {
    this("");
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public DirectoryChooserPanel(String path) {
    this(new PlaceholderFile(path));
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public DirectoryChooserPanel(File path) {
    super();

    setCurrent(path);
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_DirectoryChooser             = DirectoryChooserFactory.createChooser();
    m_DirectoryChooserTitleDefault = m_DirectoryChooser.getDialogTitle();
    m_DirectoryChooserTitle        = "";
    m_UseAbsolutePath = false;

    if (m_History == null)
      m_History = new HashMap<>();
    if (!m_History.containsKey(getClass())) {
      m_History.put(getClass(), new PlaceholderDirectoryHistory());
      m_History.get(getClass()).setHistoryFile(
	new PlaceholderFile(
	  Environment.getInstance().getHome() + File.separator + getClass().getName() + ".txt"));
    }
  }

  /**
   * Sets the title for the dirchooser.
   *
   * @param value	the title, null or empty string for default
   */
  public void setDirectoryChooserTitle(String value) {
    if (value == null)
      value = "";
    m_DirectoryChooserTitle = value;
  }

  /**
   * Returns the tile for the dirchooser.
   *
   * @return		the title, empty string for default
   */
  public String getDirectoryChooserTitle() {
    return m_DirectoryChooserTitle;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  protected File doChoose() {
    if (!m_DirectoryChooserTitle.isEmpty())
      m_DirectoryChooser.setDialogTitle(m_DirectoryChooserTitle);
    else
      m_DirectoryChooser.setDialogTitle(m_DirectoryChooserTitleDefault);
    m_DirectoryChooser.setSelectedFile(getCurrent());
    if (m_DirectoryChooser.showOpenDialog(m_Self) == DirectoryChooserFactory.APPROVE_OPTION) {
      m_History.get(getClass()).add(new PlaceholderDirectory(m_DirectoryChooser.getSelectedFile()));
      return m_DirectoryChooser.getSelectedFile();
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
    m_History.get(getClass()).add(new PlaceholderDirectory(m_DirectoryChooser.getSelectedFile()));
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
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
  protected File fromString(String value) {
    File 	result;

    try {
      result = new PlaceholderFile(value.trim()).getCanonicalFile();
    }
    catch (Exception e) {
      result = new PlaceholderFile(value);
    }

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
   * Sets the current directory.
   *
   * @param value	the current directory
   * @see		#setCurrent(File)
   */
  public void setCurrentDirectory(String value) {
    setCurrent(fromString(value));
  }

  /**
   * Returns the current directory.
   *
   * @return		the current directory
   * @see		#getCurrent()
   */
  public String getCurrentDirectory() {
    return getCurrent().getAbsolutePath();
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   * @return		true if successfully set
   */
  public boolean setCurrent(File value) {
    boolean	result;

    result = super.setCurrent(new PlaceholderFile(value));
    m_DirectoryChooser.setSelectedFile(getCurrent().getAbsoluteFile());

    return result;
  }

  /**
   * Returns the type of chooser (description).
   *
   * @return		the type
   */
  public String getChooserType() {
    return "Local";
  }

  /**
   * Returns the directory lister.
   *
   * @return		the lister
   */
  public DirectoryLister getDirectoryLister() {
    LocalDirectoryLister	result;

    result = new LocalDirectoryLister();
    result.setWatchDir(getCurrentDirectory());

    return result;
  }

  /**
   * Returns the file operations.
   *
   * @return		the operations
   */
  @Override
  public FileOperations getFileOperations() {
    return new LocalFileOperations();
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

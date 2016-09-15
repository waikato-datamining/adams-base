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
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.Placeholders;
import adams.core.io.PlaceholderFile;
import adams.core.io.lister.DirectoryLister;
import adams.core.io.lister.LocalDirectoryLister;

import java.io.File;

/**
 * A panel that contains a text field with the current directory and a
 * button for bringing up a BaseDirectoryChooser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DirectoryChooserPanel
  extends AbstractChooserPanelWithDirectoryListerSupport<File> {

  /** for serialization. */
  private static final long serialVersionUID = 6235369491956122980L;

  /** the JFileChooser for selecting a file. */
  protected BaseDirectoryChooser m_DirectoryChooser;

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

    m_DirectoryChooser = new BaseDirectoryChooser();
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  protected File doChoose() {
    m_DirectoryChooser.setSelectedFile(getCurrent());
    if (m_DirectoryChooser.showOpenDialog(m_Self) == BaseDirectoryChooser.APPROVE_OPTION)
      return m_DirectoryChooser.getSelectedFile();
    else
      return null;
  }

  /**
   * Converts the value into its string representation.
   *
   * @param value	the value to convert
   * @return		the generated string
   */
  protected String toString(File value) {
    return Placeholders.collapseStr(value.getAbsolutePath());
  }

  /**
   * Converts the string representation into its object representation.
   *
   * @param value	the string value to convert
   * @return		the generated object
   */
  protected File fromString(String value) {
    return new PlaceholderFile(value);
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(File value) {
    m_DirectoryChooser.setCurrentDirectory(new PlaceholderFile(value));
  }

  /**
   * Returns the current directory in use by the file chooser.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_DirectoryChooser.getCurrentDirectory();
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
    result.setWatchDir(getCurrentDirectory().getAbsolutePath());

    return result;
  }
}

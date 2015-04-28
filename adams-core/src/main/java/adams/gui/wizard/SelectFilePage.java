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
 * SelectFilePage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.FileChooserPanel;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Wizard page that allows the user to select a file. File filters can
 * be defined as well.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class SelectFilePage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** key in the properties that contains the file name. */
  public static final String KEY_FILE = "file";

  /** the panel for selecting the file. */
  protected FileChooserPanel m_PanelFile;

  /** whether to show the

  /**
   * Default constructor.
   */
  public SelectFilePage() {
    super();
  }

  /**
   * Initializes the page with the given page name.
   *
   * @param pageName	the page name to use
   */
  public SelectFilePage(String pageName) {
    this();
    setPageName(pageName);
  }

  /**
   * Initializes the widets.
   */
  @Override
  protected void initGUI() {
    JPanel panel;

    super.initGUI();

    panel = new JPanel(new BorderLayout());
    add(panel, BorderLayout.CENTER);
    m_PanelFile = new FileChooserPanel();
    m_PanelFile.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateButtons();
      }
    });
    panel.add(m_PanelFile, BorderLayout.NORTH);
  }

  /**
   * Sets whether to automatically append the currently selected file extension
   * or the default one (if All-Filter is used).
   *
   * @param value	if true, then the file extension will be added
   * 			automatically
   */
  public void setAutoAppendExtension(boolean value) {
    m_PanelFile.setAutoAppendExtension(value);
  }

  /**
   * Returns whether to automatically append the currently selected file extension
   * or the default one (if All-Filter is used).
   *
   * @return		true if the file extension will be added
   * 			automatically
   */
  public boolean getAutoAppendExtension() {
    return m_PanelFile.getAutoAppendExtension();
  }

  /**
   * Sets the default extension. Is used if m_AutoAppendExtension is true
   * and the All-Filter is selected.
   *
   * @param value	the extension (without dot), use null to unset
   */
  public void setDefaultExtension(String value) {
    m_PanelFile.setDefaultExtension(value);
  }

  /**
   * Returns the default extension. Is used if m_AutoAppendExtension is true
   * and the All-Filter is selected.
   *
   * @return		the extension, can be null
   */
  public String getDefaultExtension() {
    return m_PanelFile.getDefaultExtension();
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  public void setCurrentDirectory(File value) {
    m_PanelFile.setCurrentDirectory(new PlaceholderFile(value));
  }

  /**
   * Returns the current directory in use by the file chooser.
   *
   * @return		the current directory
   */
  public File getCurrentDirectory() {
    return m_PanelFile.getCurrentDirectory();
  }

  /**
   * Sets the current file.
   *
   * @param value	the value to use, can be null
   * @return		true if successfully set
   */
  public boolean setCurrent(File value) {
    return m_PanelFile.setCurrent(value);
  }

  /**
   * Returns the current value.
   *
   * @return		the current value
   */
  public File getCurrent() {
    return m_PanelFile.getCurrent();
  }

  /**
   * Adds the given file filter to the filechooser.
   *
   * @param value	the file filter to add
   */
  public void addChoosableFileFilter(FileFilter value) {
    m_PanelFile.addChoosableFileFilter(value);
  }

  /**
   * Sets the active file filter.
   *
   * @param value	the file filter to select
   */
  public void setFileFilter(FileFilter value) {
    m_PanelFile.setFileFilter(value);
  }

  /**
   * Returns the active file filter.
   *
   * @return		the current file filter
   */
  public FileFilter getFileFilter() {
    return m_PanelFile.getFileFilter();
  }

  /**
   * Sets whether to use the save or open dialog.
   *
   * @param value	if true the save dialog is used
   */
  public void setUseSaveDialog(boolean value) {
    m_PanelFile.setUseSaveDialog(value);
  }

  /**
   * Returns whether the save or open dialog is used.
   *
   * @return	true if the save dialog is used
   */
  public boolean getUseSaveDialog() {
    return m_PanelFile.getUseSaveDialog();
  }

  /**
   * Returns the content of the page (ie parameters) as properties.
   * 
   * @return		the parameters as properties
   */
  @Override
  public Properties getProperties() {
    Properties	result;
    
    result = new Properties();

    result.setProperty(KEY_FILE, m_PanelFile.getCurrent().getAbsolutePath());
    
    return result;
  }
}

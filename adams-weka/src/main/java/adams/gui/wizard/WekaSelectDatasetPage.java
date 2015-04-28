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
 * WekaSelectDatasetPage.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.DatasetFileChooserPanel;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Wizard page that allows the user to select a Weka dataset.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class WekaSelectDatasetPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** key in the properties that contains the file name. */
  public static final String KEY_FILE = "file";

  /** the panel for selecting the file. */
  protected DatasetFileChooserPanel m_PanelFile;

  /** whether to show the

  /**
   * Default constructor.
   */
  public WekaSelectDatasetPage() {
    super();
  }

  /**
   * Initializes the page with the given page name.
   *
   * @param pageName	the page name to use
   */
  public WekaSelectDatasetPage(String pageName) {
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
    m_PanelFile = new DatasetFileChooserPanel();
    m_PanelFile.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateButtons();
      }
    });
    panel.add(m_PanelFile, BorderLayout.NORTH);
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

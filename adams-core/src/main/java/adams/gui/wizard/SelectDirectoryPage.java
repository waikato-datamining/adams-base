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
 * SelectDirectoryPage.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.wizard;

import adams.core.Properties;
import adams.core.io.PlaceholderFile;
import adams.gui.chooser.DirectoryChooserPanel;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.BorderLayout;
import java.io.File;

/**
 * Wizard page that allows the user to select a directory.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 9915 $
 */
public class SelectDirectoryPage
  extends AbstractWizardPage {

  /** for serialization. */
  private static final long serialVersionUID = -7633802524155866313L;

  /** key in the properties that contains the directory. */
  public static final String KEY_DIRECTORY = "directory";

  /** the panel for selecting the file. */
  protected DirectoryChooserPanel m_PanelDir;

  /**
   * Default constructor.
   */
  public SelectDirectoryPage() {
    super();
  }

  /**
   * Initializes the page with the given page name.
   *
   * @param pageName	the page name to use
   */
  public SelectDirectoryPage(String pageName) {
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
    m_PanelDir = new DirectoryChooserPanel();
    m_PanelDir.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        updateButtons();
      }
    });
    panel.add(m_PanelDir, BorderLayout.NORTH);
  }

  /**
   * Sets the current directory.
   *
   * @param value	the directory to use
   * @return		true if successfully set
   */
  public boolean setCurrent(File value) {
    if (value.exists() && value.isDirectory())
      return m_PanelDir.setCurrent(value);
    else
      return false;
  }

  /**
   * Returns the current directory.
   *
   * @return		the current directory
   */
  public File getCurrent() {
    return m_PanelDir.getCurrent();
  }

  /**
   * Sets the content of the page (ie parameters) as properties.
   *
   * @param value	the parameters as properties
   */
  public void setProperties(Properties value) {
    if (value.hasKey(KEY_DIRECTORY))
      m_PanelDir.setCurrent(new PlaceholderFile(value.getProperty(KEY_DIRECTORY)));
    else
      m_PanelDir.setDefault();
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

    result.setProperty(KEY_DIRECTORY, m_PanelDir.getCurrent().getAbsolutePath());
    
    return result;
  }
}

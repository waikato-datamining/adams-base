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
 * DatasetFileChooserPanel.java
 * Copyright (C) 2008-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import java.io.File;

import javax.swing.JFileChooser;

import weka.core.converters.AbstractFileLoader;
import weka.core.converters.AbstractFileSaver;
import weka.core.converters.ConverterUtils;
import weka.gui.AdamsHelper;
import weka.gui.ConverterFileChooser;
import adams.core.io.PlaceholderFile;

/**
 * A panel that contains a text field with the current file and a
 * button for bringing up a ConverterFileChooser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see ConverterFileChooser
 */
public class DatasetFileChooserPanel
  extends FileChooserPanel {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /** the filechooser for selecting the dataset. */
  protected ConverterFileChooser m_FileChooser;

  /** the current loader. */
  protected AbstractFileLoader m_Loader;

  /** the current saver. */
  protected AbstractFileSaver m_Saver;

  /**
   * Initializes the panel with no file.
   */
  public DatasetFileChooserPanel() {
    this("");
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public DatasetFileChooserPanel(String path) {
    this(new PlaceholderFile(path));
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public DatasetFileChooserPanel(File path) {
    super(path);

    initializeConverters(path);
  }

  /**
   * Initializes the converters.
   *
   * @param path	the path/filename to use
   */
  protected void initializeConverters(File path) {
    if ((path.length() > 0) && path.isFile()) {
      try {
	m_Loader = ConverterUtils.getLoaderForFile(path.getAbsoluteFile());
	if (m_Loader != null)
	  m_Loader.setFile(path.getAbsoluteFile());
      }
      catch (Exception e) {
	e.printStackTrace();
      }
      m_Saver = ConverterUtils.getSaverForFile(path.getAbsoluteFile());
    }
    else {
      m_Loader = null;
      m_Saver  = null;
    }
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new ConverterFileChooser();
    AdamsHelper.updateFileChooserAccessory(m_FileChooser);
    m_Loader      = null;
    m_Saver       = null;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected File doChoose() {
    m_FileChooser.setSelectedFile(getCurrent().getAbsoluteFile());
    if (m_FileChooser.showOpenDialog(m_Self) == JFileChooser.APPROVE_OPTION) {
      m_Loader = m_FileChooser.getLoader();
      m_Saver  = m_FileChooser.getSaver();
      return new PlaceholderFile(m_FileChooser.getSelectedFile());
    }
    else {
      return null;
    }
  }

  /**
   * Returns the current loader. Only initialized after the user selected
   * a file with the filechooser.
   *
   * @return		the loader
   */
  public AbstractFileLoader getLoader() {
    if (m_Loader != null) {
      try {
	m_Loader.reset();
      }
      catch (Exception e) {
	e.printStackTrace();
      }
    }

    return m_Loader;
  }

  /**
   * Returns the current saver. Only initialized after the user selected
   * a file with the filechooser.
   *
   * @return		the saver
   */
  public AbstractFileSaver getSaver() {
    return m_Saver;
  }

  /**
   * Sets the current directory to use for the file chooser.
   *
   * @param value	the current directory
   */
  @Override
  public void setCurrentDirectory(File value) {
    m_FileChooser.setCurrentDirectory(value.getAbsoluteFile());
  }

  /**
   * Returns the current directory in use by the file chooser.
   *
   * @return		the current directory
   */
  @Override
  public File getCurrentDirectory() {
    return new PlaceholderFile(m_FileChooser.getCurrentDirectory());
  }

  /**
   * Sets the current value.
   *
   * @param value	the value to use, can be null
   */
  @Override
  public boolean setCurrent(File value) {
    boolean	result;

    result = super.setCurrent(value);
    initializeConverters(getCurrent());
    m_FileChooser.setSelectedFile(getCurrent().getAbsoluteFile());

    return result;
  }
}

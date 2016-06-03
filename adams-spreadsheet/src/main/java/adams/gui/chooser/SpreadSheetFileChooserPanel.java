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
 * SpreadSheetFileChooserPanel.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.chooser;

import adams.core.io.PlaceholderFile;
import adams.data.io.input.SpreadSheetReader;
import adams.data.io.output.SpreadSheetWriter;

import javax.swing.JFileChooser;
import java.io.File;

/**
 * A panel that contains a text field with the current file and a
 * button for bringing up a SpreadSheetFileChooser.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SpreadSheetFileChooserPanel
  extends FileChooserPanel {

  /** for serialization. */
  private static final long serialVersionUID = -8755020252465094120L;

  /** the filechooser for selecting the dataset. */
  protected SpreadSheetFileChooser m_FileChooser;

  /** the current reader. */
  protected SpreadSheetReader m_Reader;

  /** the current writer. */
  protected SpreadSheetWriter m_Writer;

  /**
   * Initializes the panel with no file.
   */
  public SpreadSheetFileChooserPanel() {
    this("");
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public SpreadSheetFileChooserPanel(String path) {
    this(new PlaceholderFile(path));
  }

  /**
   * Initializes the panel with the given filename/directory.
   *
   * @param path	the filename/directory to use
   */
  public SpreadSheetFileChooserPanel(File path) {
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
      m_Reader = m_FileChooser.getReaderForFile(path.getAbsoluteFile());
      m_Writer = m_FileChooser.getWriterForFile(path.getAbsoluteFile());
    }
    else {
      m_Reader = null;
      m_Writer = null;
    }
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_FileChooser = new SpreadSheetFileChooser();
    m_Reader = null;
    m_Writer = null;
  }

  /**
   * Performs the actual choosing of an object.
   *
   * @return		the chosen object or null if none chosen
   */
  @Override
  protected File doChoose() {
    m_FileChooser.setSelectedFile(getCurrent().getAbsoluteFile());
    if (!m_UseSaveDialog) {
      if (m_FileChooser.showOpenDialog(m_Self) == JFileChooser.APPROVE_OPTION) {
        m_Reader = m_FileChooser.getReader();
        m_Writer = null;
        return new PlaceholderFile(m_FileChooser.getSelectedFile());
      }
      else {
        return null;
      }
    }
    else {
      if (m_FileChooser.showSaveDialog(m_Self) == JFileChooser.APPROVE_OPTION) {
        m_Reader = null;
        m_Writer = m_FileChooser.getWriter();
        return new PlaceholderFile(m_FileChooser.getSelectedFile());
      }
      else {
        return null;
      }
    }
  }

  /**
   * Returns the current reader. Only initialized after the user selected
   * a file with the filechooser.
   *
   * @return		the reader
   */
  public SpreadSheetReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the current writer. Only initialized after the user selected
   * a file with the filechooser.
   *
   * @return		the writer
   */
  public SpreadSheetWriter getWriter() {
    return m_Writer;
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

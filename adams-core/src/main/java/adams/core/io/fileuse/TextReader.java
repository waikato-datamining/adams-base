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
 * TextReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.core.io.fileuse;

import adams.core.io.FileUtils;
import adams.data.io.input.AbstractTextReader;
import adams.data.io.input.LineArrayTextReader;

import java.io.File;
import java.io.FileInputStream;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Uses the specified text reader to load the file for checking the 'in use' state: if reading fails, then it is assumed the file is in use.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-reader &lt;adams.data.io.input.AbstractTextReader&gt; (property: reader)
 * &nbsp;&nbsp;&nbsp;The text reader to use for checking the 'in use' state.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.LineArrayTextReader
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TextReader
  extends AbstractFileUseCheck {

  private static final long serialVersionUID = -3766862011655514895L;

  /** the text reader to use. */
  protected AbstractTextReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Uses the specified text reader to load the file for checking the 'in use' "
	+ "state: if reading fails, then it is assumed the file is in use. ";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "reader", "reader",
      new LineArrayTextReader());
  }

  /**
   * Sets the text reader to use for checking the file use.
   *
   * @param value	the reader
   */
  public void setReader(AbstractTextReader value) {
    m_Reader = value;
    reset();
  }

  /**
   * Returns the text reader to use for checking the file use.
   *
   * @return		the reader
   */
  public AbstractTextReader getReader() {
    return m_Reader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String readerTipText() {
    return "The text reader to use for checking the 'in use' state.";
  }

  /**
   * Checks whether the file is in use.
   *
   * @param file	the file to check
   * @return		true if in use
   */
  @Override
  public boolean isInUse(File file) {
    boolean		result;
    FileInputStream	stream;

    stream = null;
    try {
      stream = new FileInputStream(file.getAbsolutePath());
      m_Reader.initialize(stream);
      while (m_Reader.hasNext())
	m_Reader.next();
      result = false;
    }
    catch (Exception e) {
      if (isLoggingEnabled())
	getLogger().log(Level.SEVERE, "Failed to load: " + file, e);
      result = true;
    }
    finally {
      FileUtils.closeQuietly(stream);
    }

    return result;
  }
}

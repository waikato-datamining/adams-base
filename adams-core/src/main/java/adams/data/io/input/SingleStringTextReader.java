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
 * SingleStringTextReader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads all text data into a single string and returns that
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SingleStringTextReader
  extends AbstractTextReader<String> {

  /** for serialization. */
  private static final long serialVersionUID = -6754529362096587742L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads all text data into a single string and returns that";
  }
  
  /**
   * Returns the class of the data that it returns.
   * 
   * @return		the generated data type
   */
  @Override
  public Class generates() {
    return String.class;
  }

  /**
   * Returns the next lot of data.
   * 
   * @return		the next amount of data, null if failed to read
   */
  @Override
  protected String doNext() {
    StringBuilder	result;
    String		line;
    
    result = new StringBuilder();
    try {
      while ((line = m_Reader.readLine()) != null) {
	result.append(line);
	result.append("\n");
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read from reader!", e);
      return null;
    }
    finally {
      m_Reader = null;
    }
    
    return result.toString();
  }
}

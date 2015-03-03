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
 * SplitByLineCount.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.splitfile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Splits the text file into chunks with the specified maximum of lines.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-prefix &lt;adams.core.io.PlaceholderFile&gt; (property: prefix)
 * &nbsp;&nbsp;&nbsp;The prefix for the generated files.
 * &nbsp;&nbsp;&nbsp;default: .&#47;split
 * </pre>
 * 
 * <pre>-extension &lt;java.lang.String&gt; (property: extension)
 * &nbsp;&nbsp;&nbsp;The file extension to use.
 * &nbsp;&nbsp;&nbsp;default: .bin
 * </pre>
 * 
 * <pre>-num-digits &lt;int&gt; (property: numDigits)
 * &nbsp;&nbsp;&nbsp;The number of digits to use for the index of the generated files.
 * &nbsp;&nbsp;&nbsp;default: 3
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-max-lines &lt;int&gt; (property: maxLines)
 * &nbsp;&nbsp;&nbsp;The maximum number of lines in the generated files.
 * &nbsp;&nbsp;&nbsp;default: 1024
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SplitByLineCount
  extends AbstractFileSplitter {

  /** for serialization. */
  private static final long serialVersionUID = -2726373275364150379L;
  
  /** the maximum number of lines. */
  protected int m_MaxLines;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the text file into chunks with the specified maximum of lines.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "max-lines", "maxLines",
	    1024, 1, null);
  }

  /**
   * Sets the maximum number of lines for the files.
   *
   * @param value	the maximum lines
   */
  public void setMaxLines(int value) {
    if (value > 0) {
      m_MaxLines = value;
      reset();
    }
    else {
      getLogger().warning("Maximum number of lines must be >0, provided: " + value);
    }
  }

  /**
   * Returns the maximum number of lines for the files.
   *
   * @return		the maximum lines
   */
  public int getMaxLines() {
    return m_MaxLines;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxLinesTipText() {
    return "The maximum number of lines in the generated files.";
  }

  /**
   * Performs the actual splitting of the file.
   * 
   * @param file	the file to split
   */
  @Override
  protected void doSplit(PlaceholderFile file) {
    BufferedReader	reader;
    BufferedWriter	writer;
    String		line;
    int			count;
    
    try {
      reader = new BufferedReader(new FileReader(file.getAbsoluteFile()));
      writer = null;
      count  = 0;
      while ((line = reader.readLine()) != null) {
	if (writer == null)
	  writer = new BufferedWriter(new FileWriter(nextFile().getAbsoluteFile()));
	
	writer.write(line);
	writer.newLine();
	
	count++;
	
	if (count >= m_MaxLines) {
	  writer.flush();
	  writer.close();
	  writer = null;
	}
      }
      reader.close();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to split file: " + file, e);
    }
  }
}

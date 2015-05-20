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
 * SplitBySize.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.splitfile;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.logging.Level;

import adams.core.io.PlaceholderFile;

/**
 <!-- globalinfo-start -->
 * Splits the file into chunks with the specified maximum size.
 * <br><br>
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
 * <pre>-max-size &lt;long&gt; (property: maxSize)
 * &nbsp;&nbsp;&nbsp;The maximum size of the generated files.
 * &nbsp;&nbsp;&nbsp;default: 1048576
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SplitBySize
  extends AbstractFileSplitter {

  /** for serialization. */
  private static final long serialVersionUID = 6675923081135115020L;
  
  /** the maximum size in bytes. */
  protected long m_MaxSize;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the file into chunks with the specified maximum size.";
  }
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "max-size", "maxSize",
	    1024*1024L, 1L, null);
  }

  /**
   * Sets the maximum size for the files.
   *
   * @param value	the maximum size
   */
  public void setMaxSize(long value) {
    if (value > 0) {
      m_MaxSize = value;
      reset();
    }
    else {
      getLogger().warning("Maximum size must be >0, provided: " + value);
    }
  }

  /**
   * Returns the maximum size for the files.
   *
   * @return		the maximum size
   */
  public long getMaxSize() {
    return m_MaxSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxSizeTipText() {
    return "The maximum size of the generated files.";
  }

  /**
   * Performs the actual splitting of the file.
   * 
   * @param file	the file to split
   */
  @Override
  protected void doSplit(PlaceholderFile file) {
    FileReader	reader;
    FileWriter	writer;
    char[]	buffer;
    int		read;
    long	count;
    int		partial;
    int		bufferSize;
    
    bufferSize = (int) Math.min(1024, m_MaxSize);
    buffer     = new char[bufferSize];
    try {
      reader = new FileReader(file.getAbsoluteFile());
      writer = null;
      count  = 0;
      while ((read = reader.read(buffer)) > -1) {
	count += read;

	if (writer == null)
	  writer = new FileWriter(nextFile().getAbsoluteFile());

	if (count >= m_MaxSize) {
	  partial = (int) (m_MaxSize - (count - bufferSize));
	  if (count > m_MaxSize)
	    writer.write(buffer, 0, partial);
	  else
	    writer.write(buffer, 0, read);

	  writer.flush();
	  writer.close();
	  writer = null;
	  count  = 0;
	  
	  if (partial < bufferSize) {
	    writer = new FileWriter(nextFile().getAbsoluteFile());
	    writer.write(buffer, partial, read - partial);
	    count += read - partial;
	  }
	}
	else {
	  writer.write(buffer, 0, read);
	}
      }
      if (writer != null) {
	writer.flush();
	writer.close();
      }
      reader.close();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to split file: " + file, e);
    }
  }
}

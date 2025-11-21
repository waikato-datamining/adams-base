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
 * SplitByNumber.java
 * Copyright (C) 2014-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.transformer.splitfile;

import adams.core.QuickInfoHelper;
import adams.core.io.PlaceholderFile;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Splits the file into a number of equally sized files.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
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
 * <pre>-file-type &lt;TEXT|BINARY&gt; (property: fileType)
 * &nbsp;&nbsp;&nbsp;Defines how to treat the file(s).
 * &nbsp;&nbsp;&nbsp;default: TEXT
 * </pre>
 *
 * <pre>-buffer-size &lt;int&gt; (property: bufferSize)
 * &nbsp;&nbsp;&nbsp;The size of byte-buffer used for reading the content.
 * &nbsp;&nbsp;&nbsp;default: 1024
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-num-files &lt;int&gt; (property: numFiles)
 * &nbsp;&nbsp;&nbsp;The number of files to generate.
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SplitByNumber
  extends AbstractFileSplitterWithBinarySupport {

  /** for serialization. */
  private static final long serialVersionUID = 6675923081135115020L;

  /** the number of files. */
  protected int m_NumFiles;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the file into a number of equally sized files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-files", "numFiles",
      1, 1, null);
  }

  /**
   * Sets the number the files.
   *
   * @param value	the number
   */
  public void setNumFiles(int value) {
    if (getOptionManager().isValid("numFiles", value)) {
      m_NumFiles = value;
      reset();
    }
  }

  /**
   * Returns the number of files.
   *
   * @return		the number
   */
  public int getNumFiles() {
    return m_NumFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numFilesTipText() {
    return "The number of files to generate.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String 	result;

    result = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "numFiles", m_NumFiles, ", #files: ");

    return result;
  }

  /**
   * Performs the actual splitting of the file.
   *
   * @param file	the file to split
   */
  @Override
  protected void doSplitText(PlaceholderFile file) {
    FileReader	reader;
    FileWriter	writer;
    long	maxSize;
    char[]	buffer;
    int		read;
    long	count;
    int		partial;
    int		bufferSize;

    try {
      maxSize    = (long) Math.ceil(file.length() / (double) m_NumFiles);
      bufferSize = (int) Math.min(m_BufferSize, maxSize);
      buffer     = new char[bufferSize];
      reader     = new FileReader(file.getAbsoluteFile());
      writer     = null;
      count      = 0;
      while (((read = reader.read(buffer)) > -1) && !m_Stopped) {
	count += read;

	if (writer == null)
	  writer = new FileWriter(nextFile().getAbsoluteFile());

	if (count >= maxSize) {
	  partial = (int) (maxSize - (count - bufferSize));
	  if (count > maxSize)
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

  /**
   * Performs the actual splitting of the file.
   *
   * @param file	the file to split
   */
  @Override
  protected void doSplitBinary(PlaceholderFile file) {
    InputStream 	in;
    OutputStream 	out;
    long		maxSize;
    byte[]		buffer;
    int			read;
    long		count;
    int			partial;
    int			bufferSize;

    try {
      maxSize    = (long) Math.ceil(file.length() / (double) m_NumFiles);
      bufferSize = (int) Math.min(m_BufferSize, maxSize);
      buffer     = new byte[bufferSize];
      in         = new FileInputStream(file.getAbsoluteFile());
      out        = null;
      count      = 0;
      while (((read = in.read(buffer)) > -1) && !m_Stopped) {
	count += read;

	if (out == null)
	  out = new FileOutputStream(nextFile().getAbsoluteFile());

	if (count >= maxSize) {
	  partial = (int) (maxSize - (count - bufferSize));
	  if (count > maxSize)
	    out.write(buffer, 0, partial);
	  else
	    out.write(buffer, 0, read);

	  out.flush();
	  out.close();
	  out = null;
	  count  = 0;

	  if (partial < bufferSize) {
	    out = new FileOutputStream(nextFile().getAbsoluteFile());
	    out.write(buffer, partial, read - partial);
	    count += read - partial;
	  }
	}
	else {
	  out.write(buffer, 0, read);
	}
      }
      if (out != null) {
	out.flush();
	out.close();
      }
      in.close();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to split file: " + file, e);
    }
  }
}

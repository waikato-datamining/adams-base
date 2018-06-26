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
 * AbstractNestedFlowReader.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Ancestor for flow readers that support the nested format as well.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractNestedFlowReader
  extends AbstractFlowReader
  implements NestedFlowReader {

  private static final long serialVersionUID = 8278421715981535356L;
  
  /**
   * Reads the flow in nested format from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the flow
   */
  public List readNested(File file) {
    return readNested(file.getAbsolutePath());
  }

  /**
   * Reads the flow in nested format from the given file.
   *
   * @param filename	the file to read from
   * @return		the flow or null in case of an error
   */
  public List readNested(String filename) {
    List		result;
    BufferedReader reader;
    InputStream input;

    check();
    
    reader = null;
    input  = null;
    try {
      if (!FileUtils.fileExists(filename))
        throw new IllegalArgumentException("File does not exist: " + filename);
      switch (getInputType()) {
	case FILE:
	  result = doReadNested(new PlaceholderFile(filename));
	  break;
	case STREAM:
	  input = new FileInputStream(filename);
	  result = doReadNested(input);
	  break;
	case READER:
	  reader = new BufferedReader(new FileReader(filename));
	  result = doReadNested(reader);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      addError("Failed to read node from: " + filename, e);
    }
    finally {
      if (!(this instanceof ChunkedSpreadSheetReader)) {
        FileUtils.closeQuietly(reader);
        FileUtils.closeQuietly(input);
      }
    }
    
    return result;
  }

  /**
   * Reads the flow in nested format from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the flow or null in case of an error
   */
  public List readNested(InputStream stream) {
    List	result;

    check();

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not input streams!");
	case STREAM:
	  result = doReadNested(stream);
	  break;
	case READER:
	  result = doReadNested(new BufferedReader(new InputStreamReader(stream)));
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      addError("Failed to read node from input stream!", e);
    }

    return result;
  }

  /**
   * Reads the flow from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   */
  public List readNested(Reader r) {
    List	result;

    check();

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not readers!");
	case STREAM:
	  result = doReadNested(new ReaderInputStream(r));
	  break;
	case READER:
	  result = doReadNested(r);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      addError("Failed to read node from reader!", e);
    }

    return result;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param file	the file to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected List doReadNested(File file) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param r		the reader to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected List doReadNested(Reader r) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param in		the input stream to read from
   * @return		the flow or null in case of an error
   * @see		#getInputType()
   */
  protected List doReadNested(InputStream in) {
    return null;
  }
}

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
 * TextFileSearchHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filesearch;

import adams.core.exception.ExceptionHandler;
import adams.core.io.FileUtils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Pattern;

/**
 * Searches text files, skips binary files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class TextFileSearchHandler
  extends AbstractFileSearchHandlerWithEncoding
  implements RegExpFileSearchHandler, StreamableFileSearchHandler {

  private static final long serialVersionUID = -9178519518724461853L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Searches text files, skips binary files.";
  }

  /**
   * Checks whether the handler can manage this file.
   *
   * @param file	the file to check
   * @return		true if handler can search this type of file
   */
  @Override
  public boolean handles(String file) {
    return !FileUtils.isBinary(file);
  }

  /**
   * Searches the specified file.
   *
   * @param file	the file to search
   * @param searchText	the search text
   * @param handler 	for handling exceptions, can be null
   * @return		true if the search text was found
   */
  @Override
  public boolean searchFile(String file, String searchText, boolean caseSensitive, ExceptionHandler handler) {
    FileInputStream 	fistream;
    InputStreamReader	isreader;
    BufferedReader 	breader;
    boolean		result;

    result   = false;
    fistream = null;
    isreader = null;
    breader  = null;

    try {
      fistream = new FileInputStream(file);
      isreader = new InputStreamReader(fistream, m_Encoding.charsetValue());
      breader  = new BufferedReader(isreader);
      result   = searchStream(breader, searchText, caseSensitive, handler);
    }
    catch (Exception e) {
      if (handler != null)
	handler.handleException("Failed to search: " + file, e);
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(isreader);
      FileUtils.closeQuietly(fistream);
    }

    return result;
  }

  /**
   * Searches the specified file using regular expressions.
   *
   * @param file	the file to search
   * @param searchText	the search text
   * @param handler 	for handling exceptions, can be null
   * @return		true if the search text was found
   */
  public boolean searchRegExp(String file, String searchText, boolean caseSensitive, ExceptionHandler handler) {
    FileInputStream 	fistream;
    InputStreamReader	isreader;
    BufferedReader 	breader;
    String		line;
    Pattern 		pattern;
    boolean		result;

    result   = false;
    fistream = null;
    isreader = null;
    breader  = null;
    pattern = Pattern.compile(searchText);

    try {
      fistream = new FileInputStream(file);
      isreader = new InputStreamReader(fistream, m_Encoding.charsetValue());
      breader  = new BufferedReader(isreader);
      while ((line = breader.readLine()) != null) {
        if (!caseSensitive)
          line = line.toLowerCase();
	result = pattern.matcher(line).matches();
        if (result)
          break;
        if (m_Stopped)
          break;
      }
    }
    catch (Exception e) {
      if (handler != null)
	handler.handleException("Failed to search: " + file, e);
    }
    finally {
      FileUtils.closeQuietly(breader);
      FileUtils.closeQuietly(isreader);
      FileUtils.closeQuietly(fistream);
    }

    return result;
  }

  /**
   * Searches the specified character stream.
   *
   * @param reader	the reader to search
   * @param searchText	the search text
   * @param handler 	for handling exceptions, can be null
   * @return		true if the search text was found
   */
  public boolean searchStream(Reader reader, String searchText, boolean caseSensitive, ExceptionHandler handler) {
    boolean		result;
    BufferedReader	breader;
    char[] 		buff;
    int			buffLen;
    int			numRead;
    String		lastStr;
    String		currStr;

    result    = false;
    m_Stopped = false;
    buffLen   = 1024;
    buff      = new char[buffLen];
    currStr   = null;
    if (!caseSensitive)
      searchText = searchText.toLowerCase();
    if (reader instanceof BufferedReader)
      breader = (BufferedReader) reader;
    else
      breader = new BufferedReader(reader);
    while (!result) {
      try {
	numRead = breader.read(buff);
	lastStr = currStr;
	if (lastStr != null)
	  lastStr = lastStr.substring(lastStr.length() - searchText.length(), lastStr.length());
	currStr = (lastStr == null ? "" : lastStr) + new String(buff);
	if (!caseSensitive)
	  currStr = currStr.toLowerCase();
	result = currStr.contains(searchText);
	if (numRead < buffLen)
	  break;
	if (m_Stopped)
	  break;
      }
      catch (Exception e) {
	if (handler != null)
	  handler.handleException("Failed to search stream!", e);
      }
    }

    return result;
  }
}

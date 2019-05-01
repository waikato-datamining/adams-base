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
 * LzfFileSearchHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filesearch;

import adams.core.exception.ExceptionHandler;
import adams.core.io.FileUtils;
import adams.core.io.LzfUtils;
import com.ning.compress.lzf.LZFInputStream;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Searches lzf-compressed (text) files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LzfFileSearchHandler
  extends AbstractFileSearchHandlerWithEncoding {

  private static final long serialVersionUID = 2030528214619565963L;

  /** the actual search. */
  protected TextFileSearchHandler m_TextSearch;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Searches lzf-compressed (text) files.";
  }

  /**
   * Checks whether the handler can manage this file.
   *
   * @param file	the file to check
   * @return		true if handler can search this type of file
   */
  @Override
  public boolean handles(String file) {
    return file.toLowerCase().endsWith(LzfUtils.EXTENSION);
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
  public boolean search(String file, String searchText, boolean caseSensitive, ExceptionHandler handler) {
    boolean		result;
    InputStream 	fis;
    InputStream 	cis;
    Reader 		isr;

    result       = false;
    fis          = null;
    cis          = null;
    isr          = null;
    m_TextSearch = new TextFileSearchHandler();
    m_Stopped    = false;

    try {
      fis    = new FileInputStream(file);
      cis    = new LZFInputStream(fis);
      isr    = new InputStreamReader(cis, m_Encoding.charsetValue());
      result = m_TextSearch.search(isr, searchText, caseSensitive, handler);
    }
    catch (Exception e) {
      if (handler != null)
        handler.handleException("Failed to search: " + file, e);
    }
    finally {
      FileUtils.closeQuietly(isr);
      FileUtils.closeQuietly(cis);
      FileUtils.closeQuietly(fis);
    }

    m_TextSearch = null;

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    if (m_TextSearch != null)
      m_TextSearch.stopExecution();
    super.stopExecution();
  }
}

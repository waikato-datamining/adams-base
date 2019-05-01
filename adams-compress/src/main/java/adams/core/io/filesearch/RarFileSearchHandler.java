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
 * RarFileSearchHandler.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filesearch;

import adams.core.exception.ExceptionHandler;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.io.RarUtils.DummyUnrarCallback;
import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Searches rar-compressed (text) files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class RarFileSearchHandler
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
    return "Searches rar-compressed (text) files.";
  }

  /**
   * Checks whether the handler can manage this file.
   *
   * @param file	the file to check
   * @return		true if handler can search this type of file
   */
  @Override
  public boolean handles(String file) {
    return file.toLowerCase().endsWith(".rar");
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
    Archive 		archive;
    InputStream 	in;
    Reader 		isr;

    result       = false;
    archive      = null;
    m_Stopped    = false;
    m_TextSearch = new TextFileSearchHandler();

    try {
      archive = new Archive(new PlaceholderFile(file).getAbsoluteFile(), new DummyUnrarCallback());
      if (archive.isEncrypted())
	throw new IllegalStateException("Cannot handle encrypted archives!");
      for (FileHeader entry : archive.getFileHeaders()) {
        if (m_Stopped)
          break;
	if (entry.isDirectory())
	  continue;

	in  = null;
	isr = null;
	try {
	  in     = new BufferedInputStream(archive.getInputStream(entry));
	  isr    = new InputStreamReader(in, m_Encoding.charsetValue());
	  result = m_TextSearch.search(isr, searchText, caseSensitive, handler);
	}
	catch (Exception e) {
	  if (handler != null)
	    handler.handleException("Failed to zip/entry: " + file + "/" + entry.getFileNameString(), e);
	}
	finally {
	  FileUtils.closeQuietly(isr);
	  FileUtils.closeQuietly(in);
	}
      }
    }
    catch (Exception e) {
      if (handler != null)
        handler.handleException("Failed to search: " + file, e);
    }
    finally {
      if (archive != null) {
	try {
	  archive.close();
	}
	catch (Exception e) {
	  // ignored
	}
      }
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

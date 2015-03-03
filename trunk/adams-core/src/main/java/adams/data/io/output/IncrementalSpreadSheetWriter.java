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
 * IncrementalSpreadSheetWriter.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import adams.data.spreadsheet.Row;

/**
 * Interface for writers that allow incremental writing.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface IncrementalSpreadSheetWriter
  extends SpreadSheetWriter {

  /**
   * Returns whether the writer can write data incrementally.
   * 
   * @return		true if data can be written incrementally
   */
  public boolean isIncremental();
  
  /**
   * Writes the given content to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(Row content, File file);

  /**
   * Writes the spreadsheet to the given file.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(Row content, String filename);

  /**
   * Writes the spreadsheet to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the spreadsheet to write
   * @param stream	the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(Row content, OutputStream stream);

  /**
   * Writes the spreadsheet to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(Row content, Writer writer);
}

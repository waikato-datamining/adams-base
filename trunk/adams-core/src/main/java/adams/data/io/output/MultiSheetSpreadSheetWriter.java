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
 * MultiSheetSpreadSheetWriter.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.File;
import java.io.OutputStream;
import java.io.Writer;

import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for spreadsheet writers that can write multiple sheets into 
 * a single document.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MultiSheetSpreadSheetWriter
  extends SpreadSheetWriter {

  /**
   * Returns whether the writer supports writing multiple sheets.
   * 
   * @return		true if it can write multiple sheets
   */
  public boolean canWriteMultiple();
  
  /**
   * Writes the spreadsheets to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  public boolean write(SpreadSheet[] content, File file);

  /**
   * Writes the spreadsheets to the given file.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(SpreadSheet[] content, String filename);

  /**
   * Writes the spreadsheets to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the spreadsheet to write
   * @param stream	the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(SpreadSheet[] content, OutputStream stream);

  /**
   * Writes the spreadsheets to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  public boolean write(SpreadSheet[] content, Writer writer);
}

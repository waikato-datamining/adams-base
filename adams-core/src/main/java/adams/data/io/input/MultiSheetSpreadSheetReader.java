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
 * MultiSheetSpreadSheetReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import adams.core.Range;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for spreadsheet readers that can read multiple sheets from the
 * same document.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface MultiSheetSpreadSheetReader
  extends SpreadSheetReader {

  /**
   * Sets the range of the sheets to load.
   *
   * @param value	the range (1-based)
   */
  public void setSheetRange(Range value);

  /**
   * Returns the range of the sheets to load.
   *
   * @return		the range (1-based)
   */
  public Range getSheetRange();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String sheetRangeTipText();

  /**
   * Reads the spreadsheet from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the spreadsheet
   */
  public List<SpreadSheet> readRange(File file);

  /**
   * Reads the spreadsheets from the given file.
   *
   * @param filename	the file to read from
   * @return		the spreadsheets or null in case of an error
   */
  public List<SpreadSheet> readRange(String filename);

  /**
   * Reads the spreadsheets from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the spreadsheets or null in case of an error
   */
  public List<SpreadSheet> readRange(InputStream stream);

  /**
   * Reads the spreadsheets from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the spreadsheets or null in case of an error
   */
  public List<SpreadSheet> readRange(Reader r);
}

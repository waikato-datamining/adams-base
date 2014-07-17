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
 * SpreadSheetReader.java
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

import adams.core.Stoppable;
import adams.core.io.FileFormatHandler;
import adams.core.option.OptionHandler;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.DataRowType;
import adams.data.spreadsheet.SpreadSheet;

/**
 * Interface for spreadsheet readers.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface SpreadSheetReader
  extends Stoppable, OptionHandler, FileFormatHandler {

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public abstract String getDefaultFormatExtension();

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public abstract SpreadSheetWriter getCorrespondingWriter();
  
  /**
   * Sets the type of data row to use.
   *
   * @param value	the type
   */
  public void setDataRowType(DataRowType value);

  /**
   * Returns the type of data row to use.
   *
   * @return		the type
   */
  public DataRowType getDataRowType();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataRowTypeTipText();

  /**
   * Sets the type of spreadsheet to use.
   *
   * @param value	the type
   */
  public void setSpreadSheetType(SpreadSheet value);

  /**
   * Returns the type of spreadsheet to use.
   *
   * @return		the type
   */
  public SpreadSheet getSpreadSheetType();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String spreadSheetTypeTipText();

  /**
   * Reads the spreadsheet from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the spreadsheet
   */
  public SpreadSheet read(File file);

  /**
   * Reads the spreadsheet from the given file.
   *
   * @param filename	the file to read from
   * @return		the spreadsheet or null in case of an error
   */
  public SpreadSheet read(String filename);

  /**
   * Reads the spreadsheet from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the spreadsheet or null in case of an error
   */
  public SpreadSheet read(InputStream stream);

  /**
   * Reads the spreadsheet from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   */
  public SpreadSheet read(Reader r);

  /**
   * Stops the reading (might not be immediate, depending on reader).
   */
  public void stopExecution();
  
  /**
   * Returns whether the reading was stopped.
   * 
   * @return		true if stopped
   */
  public boolean isStopped();

  /**
   * Returns whether an error was encountered during the last read.
   * 
   * @return		true if an error occurred
   */
  public boolean hasLastError();
  
  /**
   * Returns the error that occurred during the last read.
   * 
   * @return		the error string, null if none occurred
   */
  public String getLastError();
}

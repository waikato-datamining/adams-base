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
 * AbstractMultiSheetSpreadSheetWriter.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.data.spreadsheet.SpreadSheet;
import org.apache.commons.io.output.WriterOutputStream;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Ancestor for spreadsheet writers that can write multiple sheets into
 * a single document.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiSheetSpreadSheetWriter
  extends AbstractSpreadSheetWriter
  implements MultiSheetSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = -9004877579871173007L;
  
  /** The prefix for the sheet names. */
  protected String m_SheetPrefix;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sheet-prefix", "sheetPrefix",
	    getDefaultSheetPrefix());
  }

  /**
   * Returns the default missing value.
   *
   * @return		the default for missing values
   */
  protected String getDefaultSheetPrefix() {
    return "Sheet";
  }

  /**
   * Sets the prefix for sheet names.
   *
   * @param value	the prefix
   */
  public void setSheetPrefix(String value) {
    m_SheetPrefix = value;
    reset();
  }

  /**
   * Returns the prefix for sheet names
   *
   * @return		the prefix
   */
  public String getSheetPrefix() {
    return m_SheetPrefix;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String sheetPrefixTipText() {
    return "The prefix for sheet names.";
  }

  /**
   * Returns whether the writer supports writing multiple sheets.
   * 
   * @return		true if it can write multiple sheets
   */
  public boolean canWriteMultiple() {
    return true;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, Writer writer) {
    return doWrite(new SpreadSheet[]{content}, writer);
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   *
   * @param content	the spreadsheet to write
   * @param out		the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, OutputStream out) {
    return doWrite(new SpreadSheet[]{content}, out);
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   * <p/>
   * Default implementation returns always false.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet[] content, String filename) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   * <p/>
   * Default implementation returns always false.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet[] content, Writer writer) {
    return false;
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   * <p/>
   * Default implementation returns always false.
   *
   * @param content	the spreadsheet to write
   * @param out		the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  protected boolean doWrite(SpreadSheet[] content, OutputStream out) {
    return false;
  }

  /**
   * Writes the spreadsheets to the specified file.
   *
   * @param content	the content to write
   * @param file	the file to write to
   * @return		true if successfully written
   */
  @Override
  public boolean write(SpreadSheet[] content, File file) {
    return write(content, file.getAbsolutePath());
  }

  /**
   * Writes the spreadsheets to the given file.
   *
   * @param content	the spreadsheet to write
   * @param filename	the file to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  public boolean write(SpreadSheet[] content, String filename) {
    boolean			result;
    BufferedWriter		writer;
    OutputStream		output;

    result = true;

    try {
      switch (getOutputType()) {
        case FILE:
          result = doWrite(content, filename);
          break;
        case STREAM:
          output = new FileOutputStream(filename, false);
          result = doWrite(content, output);
          output.close();
          break;
        case WRITER:
          writer = new BufferedWriter(new FileWriter(filename, false));
          result = doWrite(content, writer);
          writer.close();
          break;
        default:
          throw new IllegalStateException("Unhandled output type: " + getOutputType());
      }
    }
    catch (Exception e) {
      result = false;
      e.printStackTrace();
    }

    return result;
  }

  /**
   * Writes the spreadsheets to the given output stream. The caller
   * must ensure that the stream gets closed.
   *
   * @param content	the spreadsheet to write
   * @param stream	the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  public boolean write(SpreadSheet[] content, OutputStream stream) {
    switch (getOutputType()) {
      case FILE:
        throw new IllegalStateException("Can only write to files!");
      case STREAM:
        return doWrite(content, stream);
      case WRITER:
        return doWrite(content, new OutputStreamWriter(stream));
      default:
        throw new IllegalStateException("Unhandled output type: " + getOutputType());
    }
  }

  /**
   * Writes the spreadsheets to the given writer. The caller
   * must ensure that the writer gets closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  public boolean write(SpreadSheet[] content, Writer writer) {
    switch (getOutputType()) {
      case FILE:
        throw new IllegalStateException("Can only write to files!");
      case STREAM:
        return doWrite(content, new WriterOutputStream(writer));
      case WRITER:
        return doWrite(content, writer);
      default:
        throw new IllegalStateException("Unhandled output type: " + getOutputType());
    }
  }
}

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
 * SimpleStreamSpreadSheetWriter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.output;

import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.data.io.input.SimpleStreamSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.io.BufferedWriter;
import java.io.Writer;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Simple stream format for spreadsheets:<br>
 * - one spreadsheet row per line in the output<br>
 * - cells are separated by TAB<br>
 * - cell format: '&lt;1-based index&gt;:&lt;content-type ID&gt;:&lt;content&gt;'<br>
 * NB: tabs, new lines etc in the content get backquoted.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when writing using a writer, use empty string 
 * &nbsp;&nbsp;&nbsp;for default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-omit-missing &lt;boolean&gt; (property: omitMissing)
 * &nbsp;&nbsp;&nbsp;If enabled, missing cells and ones with missing values are omitted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleStreamSpreadSheetWriter
  extends AbstractSpreadSheetWriter {

  private static final long serialVersionUID = -6342369466120096983L;

  /** whether to omit missing cells or ones with missing value. */
  protected boolean m_OmitMissing;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Simple stream format for spreadsheets:\n"
	+ "- one spreadsheet row per line in the output\n"
	+ "- cells are separated by TAB\n"
        + "- cell format: '<1-based index>:<content-type ID>:<content>'\n"
        + "NB: tabs, new lines etc in the content get backquoted.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "encoding", "encoding",
      new BaseCharset());

    m_OptionManager.add(
      "omit-missing", "omitMissing",
      false);
  }

  /**
   * Sets whether to omit missing cells and ones with missing value.
   *
   * @param value	true if to omit
   */
  public void setOmitMissing(boolean value) {
    m_OmitMissing = value;
    reset();
  }

  /**
   * Returns whether to omit missing cells and ones with missing value.
   *
   * @return		true if to omit
   */
  public boolean getOmitMissing() {
    return m_OmitMissing;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String omitMissingTipText() {
    return "If enabled, missing cells and ones with missing values are omitted.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Simple stream format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"ssf"};
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.WRITER;
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  @Override
  public SpreadSheetReader getCorrespondingReader() {
    return new SimpleStreamSpreadSheetReader();
  }

  /**
   * Writes a cell using the supplied writer.
   *
   * @param writer	the writer to use
   * @param index	the 0-based index of the cell
   * @param cell	the cell to write, null for missing cell
   */
  protected void writeCell(BufferedWriter writer, int index, Cell cell) throws Exception {
    // 1-based index
    writer.write("" + (index + 1));

    writer.write(":");

    // cell type
    if (cell == null)
      writer.write(ContentType.MISSING.getID());
    else
      writer.write(cell.getContentType().getID());

    writer.write(":");

    // cell value
    if ((cell == null) || (cell.isMissing()))
      writer.write(SpreadSheet.MISSING_VALUE);
    else
      writer.write(Utils.backQuoteChars(cell.getContent()));
  }

  /**
   * Writes a row using the supplied writer.
   *
   * @param writer	the writer to use
   * @param sheet	the sheet this row belongs to
   * @param row		the row to write
   * @return		true if successful written
   */
  protected boolean writeRow(BufferedWriter writer, SpreadSheet sheet, Row row) {
    boolean	result;
    int		i;
    boolean	first;
    Cell 	cell;

    result = true;

    try {
      first = true;
      for (i = 0; i < sheet.getColumnCount(); i++) {
	if (row.hasCell(i)) {
	  cell = row.getCell(i);
	  if (cell.isMissing()) {
	    if (!m_OmitMissing) {
	      if (!first)
		writer.write('\t');
	      writeCell(writer, i, cell);
	      first = false;
	    }
	  }
	  else {
	    if (!first)
	      writer.write('\t');
	    writeCell(writer, i, cell);
	    first = false;
	  }
	}
	else {
	  if (!m_OmitMissing) {
	    if (!first)
	      writer.write('\t');
	    writeCell(writer, i, null);
	    first = false;
	  }
	}
      }
      writer.newLine();
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to write row: " + row, e);
      result = false;
    }

    return result;
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
    boolean		result;
    BufferedWriter	bwriter;

    if (writer instanceof BufferedWriter)
      bwriter = (BufferedWriter) writer;
    else
      bwriter = new BufferedWriter(writer);

    // header
    result = writeRow(bwriter, content, content.getHeaderRow());

    // data
    if (result) {
      for (Row row : content.rows()) {
	result = writeRow(bwriter, content, row);
	if (!result)
	  getLogger().severe("Failed to write row to writer: " + row);
      }
    }

    return result;
  }
}

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
 * SimpleStreamSpreadSheetReader.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.data.io.output.SimpleStreamSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;

import java.io.BufferedReader;
import java.io.Reader;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads file in simple stream format:<br>
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
 * <pre>-data-row-type &lt;adams.data.spreadsheet.DataRow&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DenseDataRow
 * </pre>
 * 
 * <pre>-spreadsheet-type &lt;adams.data.spreadsheet.SpreadSheet&gt; (property: spreadSheetType)
 * &nbsp;&nbsp;&nbsp;The type of spreadsheet to use for the data.
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.DefaultSpreadSheet
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading using a reader, leave empty for 
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 * <pre>-no-header &lt;boolean&gt; (property: noHeader)
 * &nbsp;&nbsp;&nbsp;If enabled, all rows get added as data rows and a dummy header will get 
 * &nbsp;&nbsp;&nbsp;inserted.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-custom-column-headers &lt;java.lang.String&gt; (property: customColumnHeaders)
 * &nbsp;&nbsp;&nbsp;The custom headers to use for the columns instead (comma-separated list);
 * &nbsp;&nbsp;&nbsp; ignored if empty.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-chunk-size &lt;int&gt; (property: chunkSize)
 * &nbsp;&nbsp;&nbsp;The maximum number of rows per chunk; using -1 will read put all data into 
 * &nbsp;&nbsp;&nbsp;a single spreadsheet object.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleStreamSpreadSheetReader
  extends AbstractSpreadSheetReader
  implements NoHeaderSpreadSheetReader, ChunkedSpreadSheetReader {

  private static final long serialVersionUID = -6855023352925257381L;

  /** whether the file has a header or not. */
  protected boolean m_NoHeader;

  /** the comma-separated list of column header names. */
  protected String m_CustomColumnHeaders;

  /** the chunk size to use. */
  protected int m_ChunkSize;

  /** the reader in use. */
  protected BufferedReader m_Reader;

  /** the header in use. */
  protected SpreadSheet m_Header;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Reads file in simple stream format:\n"
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
      "no-header", "noHeader",
      false);

    m_OptionManager.add(
      "custom-column-headers", "customColumnHeaders",
      "");

    m_OptionManager.add(
      "chunk-size", "chunkSize",
      -1, -1, null);
  }

  /**
   * Sets whether the file contains a header row or not.
   *
   * @param value	true if no header row available
   */
  public void setNoHeader(boolean value) {
    m_NoHeader = value;
    reset();
  }

  /**
   * Returns whether the file contains a header row or not.
   *
   * @return		true if no header row available
   */
  public boolean getNoHeader() {
    return m_NoHeader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String noHeaderTipText() {
    return "If enabled, all rows get added as data rows and a dummy header will get inserted.";
  }

  /**
   * Sets the custom headers to use.
   *
   * @param value	the comma-separated list
   */
  public void setCustomColumnHeaders(String value) {
    m_CustomColumnHeaders = value;
    reset();
  }

  /**
   * Returns whether the file contains a header row or not.
   *
   * @return		the comma-separated list
   */
  public String getCustomColumnHeaders() {
    return m_CustomColumnHeaders;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String customColumnHeadersTipText() {
    return "The custom headers to use for the columns instead (comma-separated list); ignored if empty.";
  }

  /**
   * Sets the maximum chunk size.
   *
   * @param value	the size of the chunks, &lt; 1 denotes infinity
   */
  @Override
  public void setChunkSize(int value) {
    if (value < 1)
      value = -1;
    m_ChunkSize = value;
    reset();
  }

  /**
   * Returns the current chunk size.
   *
   * @return	the size of the chunks, &lt; 1 denotes infinity
   */
  @Override
  public int getChunkSize() {
    return m_ChunkSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  @Override
  public String chunkSizeTipText() {
    return "The maximum number of rows per chunk; using -1 will read put all data into a single spreadsheet object.";
  }

  /**
   * Returns, if available, the corresponding writer.
   *
   * @return		the writer, null if none available
   */
  @Override
  public SpreadSheetWriter getCorrespondingWriter() {
    return new SimpleStreamSpreadSheetWriter();
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new SimpleStreamSpreadSheetWriter().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new SimpleStreamSpreadSheetWriter().getFormatExtensions();
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.READER;
  }

  /**
   * Performs the actual reading.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected SpreadSheet doRead(Reader r) {
    m_Header = null;

    if (r instanceof BufferedReader)
      m_Reader = (BufferedReader) r;
    else
      m_Reader = new BufferedReader(r);

    return nextChunk();
  }

  /**
   * Checks whether there is more data to read.
   *
   * @return		true if there is more data available
   */
  @Override
  public boolean hasMoreChunks() {
    return (m_Reader != null);
  }

  /**
   * Reads the next line in the file.
   *
   * @return		the cells, null if no more data
   * @throws Exception	if reading fails
   */
  protected String[] nextLine() throws Exception {
    String[]	result;
    String	line;

    if (m_Reader == null)
      return null;

    line = m_Reader.readLine();
    if (line == null) {
      m_Reader = null;
      return null;
    }

    result = line.split("\t");

    return result;
  }

  /**
   * Splits the cell into its three parts: index, type, content.
   *
   * @param cell	the cell string to split
   * @return		the parts
   */
  protected String[] splitCell(String cell) {
    String[]	result;
    int		pos;
    int		pos2;

    result    = new String[3];
    pos       = cell.indexOf(":");
    result[0] = cell.substring(0, pos);
    pos2      = cell.indexOf(":", pos + 1);
    result[1] = cell.substring(pos + 1, pos2);
    result[2] = cell.substring(pos2 + 1);

    return result;
  }

  /**
   * Returns the next chunk.
   *
   * @return		the next chunk, null if no data available
   */
  @Override
  public SpreadSheet nextChunk() {
    SpreadSheet		result;
    int			count;
    String[]		cells;
    String[]		parts;
    List<String>	header;
    int			i;
    int			index;
    ContentType 	type;
    Row			row;
    boolean		finished;

    result = null;

    try {
      // header?
      cells = null;
      if (m_Header == null) {
	m_Header = m_SpreadSheetType.newInstance();
	m_Header.setDataRowClass(m_DataRowType.getClass());
	row = m_Header.getHeaderRow();
	if (!m_CustomColumnHeaders.isEmpty() || m_NoHeader) {
	  cells  = nextLine();
	  header = SpreadSheetUtils.createHeader(cells.length, m_CustomColumnHeaders);
	  for (i = 0; i < header.size(); i++)
	    row.addCell("" + i).setContentAsString(header.get(i));
	  if (!m_NoHeader)
	    cells = null;
	}
	else {
	  cells = nextLine();
	  for (i = 0; i < cells.length; i++) {
	    parts = splitCell(cells[i]);
	    row.addCell("" + i).setContentAsString(parts[2]);
	  }
	  cells = null;
	}
      }

      // data
      result   = m_Header.getHeader();
      finished = false;
      count    = 0;
      while (!finished) {
	if (cells == null)
	  cells = nextLine();
	if (cells == null)
	  break;
	count++;

	row = result.addRow();
	for (i = 0; i < cells.length; i++) {
	  parts = splitCell(cells[i]);
	  index = Integer.parseInt(parts[0]) - 1;
	  type  = ContentType.MISSING.parse(parts[1]);
	  if (type == null)
	    row.addCell("" + index).setContent(parts[2]);
	  else
	    row.addCell("" + index).setContentAs(parts[2], type);
	}

	if (m_ChunkSize > 0) {
	  if (count >= m_ChunkSize)
	    finished = true;
	}
	cells = null;
      }
    }
    catch (Exception e) {
      m_LastError = "Failed to read data!\n" + Utils.throwableToString(e);
    }

    return result;
  }
}

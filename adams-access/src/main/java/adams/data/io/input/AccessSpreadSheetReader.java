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
 * AccessSpreadSheetReader.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.io.input;

import adams.core.DateTime;
import adams.core.Utils;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Allows the reading of MS Access databases.
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: adams.data.spreadsheet.SpreadSheet
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 * 
 * <pre>-table &lt;java.lang.String&gt; (property: table)
 * &nbsp;&nbsp;&nbsp;The table to read from the database.
 * &nbsp;&nbsp;&nbsp;default: MyTable
 * </pre>
 * 
 * <pre>-first-row &lt;int&gt; (property: firstRow)
 * &nbsp;&nbsp;&nbsp;The index of the first row to retrieve (1-based).
 * &nbsp;&nbsp;&nbsp;default: 1
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 * 
 * <pre>-num-rows &lt;int&gt; (property: numRows)
 * &nbsp;&nbsp;&nbsp;The number of data rows to retrieve; use -1 for unlimited.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
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
public class AccessSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport
  implements ChunkedSpreadSheetReader, WindowedSpreadSheetReader {

  private static final long serialVersionUID = 1822931227110464391L;

  /**
   * Reads data from the table in chunks.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ChunkReader
    implements Serializable {

    /** the owning reader. */
    protected AccessSpreadSheetReader m_Owner;

    /** the table in use. */
    protected Table m_Table;

    /** the header. */
    protected SpreadSheet m_Header;

    /** the missing value. */
    protected String m_MissingValue;

    /** the chunk size. */
    protected int m_ChunkSize;

    /** the rows read so far. */
    protected int m_RowCount;

    /** the first row to retrieve (1-based). */
    protected int m_FirstRow;

    /** the number of rows to retrieve (less than 1 = unlimited). */
    protected int m_NumRows;

    /**
     * Initializes the low-level reader.
     *
     * @param owner	the owning reader
     */
    public ChunkReader(AccessSpreadSheetReader owner) {
      m_Owner = owner;
    }

    /**
     * Reads the next chunk.
     *
     * @return		the next chunk
     */
    public SpreadSheet next() {
      SpreadSheet	                    result;
      List<String>	                    cells;
      List<? extends Column>                cols;
      com.healthmarketscience.jackcess.Row  aRow;
      Row		                    row;
      Cell		                    cell;
      int		                    i;
      boolean		                    canAdd;
      boolean                               first;

      cols = m_Table.getColumns();
      if (m_Header == null) {
	result = m_Owner.getSpreadSheetType().newInstance();
	result.setDataRowClass(m_Owner.getDataRowType().getClass());
        row  = result.getHeaderRow();
        for (Column col: cols)
          row.addCell(col.getName()).setContent(col.getName());
      }
      else {
	result = m_Header.getHeader();
      }
      first = (m_Header == null);

      try {
	while (!m_Owner.isStopped()) {
          try {
            aRow = m_Table.getNextRow();
            if (aRow == null) {
              close();
              break;
            }
          }
          catch (Exception e) {
            close();
            break;
          }

	  // window not yet reached?
	  canAdd = true;
          m_RowCount++;
          if (m_RowCount < m_FirstRow)
            canAdd = false;

	  if (canAdd) {
            row = result.addRow();
            for (Column col: cols) {
              cell = row.addCell(col.getName());
              cell.isMissing();
              try {
                switch (col.getType()) {
                  case BOOLEAN:
                    cell.setContent(aRow.getBoolean(col.getName()));
                    break;
                  case BYTE:
                    cell.setContent(aRow.getByte(col.getName()));
                    break;
                  case DOUBLE:
                  case NUMERIC:
                    cell.setContent(aRow.getDouble(col.getName()));
                    break;
                  case FLOAT:
                    cell.setContent(aRow.getFloat(col.getName()));
                    break;
                  case INT:
                    cell.setContent(aRow.getShort(col.getName()));
                    break;
                  case LONG:
                    cell.setContent(aRow.getInt(col.getName()));
                    break;
                  case MEMO:
                  case TEXT:
                    cell.setContent(aRow.getString(col.getName()));
                    break;
                  case SHORT_DATE_TIME:
                    if (aRow.getDate(col.getName()) != null)
                      cell.setContent(new DateTime(aRow.getDate(col.getName())));
                    break;
                  default:
                    if (first)
                      m_Owner.getLogger().warning("Unsupported data type: " + col.getType() + " ('" + m_Owner.getTable() + "'/'" + col.getName() + "')");
                    break;
                }
              }
              catch (Exception e) {
                m_Owner.getLogger().log(Level.SEVERE, "Failed to process table/row/column: '" + m_Owner.getTable() + "'/" + result.getRowCount() + "/'" + col.getName(), e);
              }
            }
            first = false;
          }

	  // keep as reference
	  if (m_Header == null)
	    m_Header = result.getHeader();

	  // end of window reached?
	  if (m_NumRows > -1) {
	    if (m_RowCount >= m_FirstRow + m_NumRows - 1) {
	      close();
	      break;
	    }
	  }

	  // chunk limit reached?
	  if ((m_ChunkSize > 0) && (result.getRowCount() == m_ChunkSize))
	    break;
	}
      }
      catch (Exception e) {
        result = null;
	m_Owner.getLogger().log(Level.SEVERE, "Failed to read data!", e);
	m_Owner.setLastError("Failed to read data!\n" + Utils.throwableToString(e));
      }

      return result;
    }

    /**
     * Closes the reader.
     */
    protected void close() {
      try {
        m_Table.getDatabase().close();
	m_Table = null;
      }
      catch (Exception e) {
	m_Owner.getLogger().log(Level.SEVERE, "Failed to read data!", e);
	m_Owner.setLastError("Failed to read data!\n" + Utils.throwableToString(e));
      }
    }

    /**
     * Returns whether there is more data to be read.
     *
     * @return		true if more data available
     */
    public boolean hasNext() {
      return (m_Table != null);
    }

    /**
     * Reads the spreadsheet content from the specified table.
     *
     * @param table	the table to read from
     * @return		the spreadsheet or null in case of an error
     */
    public SpreadSheet read(Table table) {
      m_Table = table;

      m_Header          = null;
      m_ChunkSize       = m_Owner.getChunkSize();
      m_MissingValue    = m_Owner.getMissingValue();
      m_FirstRow        = m_Owner.getFirstRow();
      m_NumRows         = m_Owner.getNumRows();
      m_RowCount        = 0;

      return next();
    }
  }

  /** the table to read. */
  protected String m_Table;

  /** the first row to retrieve (1-based). */
  protected int m_FirstRow;

  /** the number of rows to retrieve (less than 1 = unlimited). */
  protected int m_NumRows;

  /** the chunk size to use. */
  protected int m_ChunkSize;

  /** the reader. */
  protected ChunkReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Allows the reading of MS Access databases.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "table", "table",
      "MyTable");

    m_OptionManager.add(
      "first-row", "firstRow",
      1, 1, null);

    m_OptionManager.add(
      "num-rows", "numRows",
      -1, -1, null);

    m_OptionManager.add(
      "chunk-size", "chunkSize",
      -1, -1, null);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Reader = null;
  }

  /**
   * Sets the table to read from.
   *
   * @param value	the table
   */
  public void setTable(String value) {
    m_Table = value;
    reset();
  }

  /**
   * Returns the table to read from.
   *
   * @return		the table
   */
  public String getTable() {
    return m_Table;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String tableTipText() {
    return "The table to read from the database.";
  }

  /**
   * Sets the first row to return.
   *
   * @param value	the first row (1-based), greater than 0
   */
  public void setFirstRow(int value) {
    if (value > 0) {
      m_FirstRow = value;
      reset();
    }
    else {
      getLogger().warning("First row must be > 0, provided: " + value);
    }
  }

  /**
   * Returns the first row to return.
   *
   * @return		the first row (1-based), greater than 0
   */
  public int getFirstRow() {
    return m_FirstRow;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String firstRowTipText() {
    return "The index of the first row to retrieve (1-based).";
  }

  /**
   * Sets the number of data rows to return.
   *
   * @param value	the number of rows, -1 for unlimited
   */
  public void setNumRows(int value) {
    if (value < 0)
      m_NumRows = -1;
    else
      m_NumRows = value;
    reset();
  }

  /**
   * Returns the number of data rows to return.
   *
   * @return		the number of rows, -1 for unlimited
   */
  public int getNumRows() {
    return m_NumRows;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numRowsTipText() {
    return "The number of data rows to retrieve; use -1 for unlimited.";
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
    return null;
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "MS Access database";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{".mdb", ".accdb"};
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.FILE;
  }

  /**
   * Performs the actual reading.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   */
  @Override
  protected SpreadSheet doRead(File file) {
    SpreadSheet             result;
    Database                db;

    try {
      db       = DatabaseBuilder.open(file.getAbsoluteFile());
      m_Reader = new ChunkReader(this);
      result   = m_Reader.read(db.getTable(m_Table));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to open database: " + file, e);
      result = null;
    }

    return result;
  }

  /**
   * Checks whether there is more data to read.
   *
   * @return		true if there is more data available
   */
  @Override
  public boolean hasMoreChunks() {
    return (m_Reader != null) && m_Reader.hasNext();
  }

  /**
   * Returns the next chunk.
   *
   * @return		the next chunk, null if no data available
   */
  @Override
  public SpreadSheet nextChunk() {
    if ((m_Reader == null) || !m_Reader.hasNext())
      return null;
    else
      return m_Reader.next();
  }
}

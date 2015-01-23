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
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

import java.io.File;
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
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AccessSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport {

  /** the table to read. */
  protected String m_Table;

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
   * Performs the actual reading. Must handle compression itself, if
   * {@link #supportsCompressedInput()} returns true.
   * <p/>
   * Default implementation returns null.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   * @see		#supportsCompressedInput()
   */
  @Override
  protected SpreadSheet doRead(File file) {
    SpreadSheet             result;
    Row                     row;
    Cell                    cell;
    Database                db;
    Table                   table;
    List<? extends Column>  cols;
    boolean                 first;

    db = null;
    try {
      db     = DatabaseBuilder.open(file.getAbsoluteFile());
      table  = db.getTable(m_Table);
      result = new SpreadSheet();
      result.setDataRowClass(getDataRowType().getClass());
      result.setName(m_Table);

      // header
      cols = table.getColumns();
      row  = result.getHeaderRow();
      for (Column col: cols)
        row.addCell(col.getName()).setContent(col.getName());

      // data
      first = true;
      for (com.healthmarketscience.jackcess.Row r: table) {
        row = result.addRow();
        for (Column col: cols) {
          cell = row.addCell(col.getName());
          cell.isMissing();
          try {
            switch (col.getType()) {
              case BOOLEAN:
                cell.setContent(r.getBoolean(col.getName()));
                break;
              case BYTE:
                cell.setContent(r.getByte(col.getName()));
                break;
              case DOUBLE:
              case NUMERIC:
                cell.setContent(r.getDouble(col.getName()));
                break;
              case FLOAT:
                cell.setContent(r.getFloat(col.getName()));
                break;
              case INT:
                cell.setContent(r.getShort(col.getName()));
                break;
              case LONG:
                cell.setContent(r.getInt(col.getName()));
                break;
              case MEMO:
              case TEXT:
                cell.setContent(r.getString(col.getName()));
                break;
              case SHORT_DATE_TIME:
                if (r.getDate(col.getName()) != null)
                  cell.setContent(new DateTime(r.getDate(col.getName())));
                break;
              default:
                if (first)
                  getLogger().warning("Unsupported data type: " + col.getType() + " ('" + m_Table + "'/'" + col.getName() + "')");
                break;
            }
          }
          catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to process table/row/column: '" + m_Table + "'/" + result.getRowCount() + "/'" + col.getName(), e);
          }
        }
        first = false;
      }
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to open database: " + file, e);
      result = null;
    }
    finally {
      if (db != null) {
        try {
          db.close();
        }
        catch (Exception e) {
          // ignored
        }
      }
    }

    return result;
  }
}

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
 * GnuplotSpreadSheetReader.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.base.BaseRegExp;
import adams.core.io.FileUtils;
import adams.data.io.output.GnuplotSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import java.io.File;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Reads data in Gnuplot format.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-data-row-type &lt;DENSE|SPARSE&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: DENSE
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: -999
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnuplotSpreadSheetReader
  extends AbstractSpreadSheetReaderWithMissingValueSupport {

  /** for serialization. */
  private static final long serialVersionUID = 2126554583457852066L;

  /** the line comment start. */
  public final static String COMMENT = "#";

  /** the default for missing values. */
  public final static String MISSING_VALUE = "-999";

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads data in Gnuplot format.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Gnuplot data";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"dat", "data"};
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new GnuplotSpreadSheetWriter();
  }

  /**
   * Returns the default missing value to use.
   * 
   * @return		the default
   */
  @Override
  protected BaseRegExp getDefaultMissingValue() {
    return new BaseRegExp("-999");
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
    SpreadSheet		result;
    List<String>	lines;
    String[]		cells;
    boolean		first;
    Row			row;
    int			i;
    Cell		cell;
    
    lines = FileUtils.loadFromFile(file, getEncoding().stringValue());
    if (lines == null)
      return null;
    
    result = m_SpreadSheetType.newInstance();

    first = true;
    for (String line: lines) {
      if (m_Stopped)
	break;
      if (line.startsWith(COMMENT)) {
	result.addComment(line.substring(COMMENT.length()));
      }
      else {
	cells = line.split("\\s+");
	// need header?
	if (first) {
	  row = result.getHeaderRow();
	  for (i = 0; i < cells.length; i++)
	    row.addCell("" + i).setContent("Col" + (i+1));
	  first = false;
	}
	// data row
	row = result.addRow();
	for (i = 0; (i < cells.length) && (i < result.getColumnCount()); i++) {
	  cell = row.addCell("" + i);
	  if (m_MissingValue.isMatch(cells[i]) || (cells[i].isEmpty() && m_MissingValue.isEmpty()))
	    cell.setMissing();
	  else
	    cell.setContent(cells[i]);
	}
      }
    }
    
    return result;
  }
}

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

/*
 * FastExcelSpreadSheetWriter.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.Utils;
import adams.data.io.input.FastExcelSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;
import adams.env.Modules;
import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;

import java.io.OutputStream;
import java.util.HashSet;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Writes MS Excel files (using fast-excel).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-sheet-prefix &lt;java.lang.String&gt; (property: sheetPrefix)
 * &nbsp;&nbsp;&nbsp;The prefix for sheet names.
 * &nbsp;&nbsp;&nbsp;default: Sheet
 * </pre>
 *
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-output-as-displayed &lt;boolean&gt; (property: outputAsDisplayed)
 * &nbsp;&nbsp;&nbsp;If enabled, cells are output as displayed, ie, results of formulas instead
 * &nbsp;&nbsp;&nbsp;of the formulas.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FastExcelSpreadSheetWriter
  extends AbstractMultiSheetSpreadSheetWriterWithMissingValueSupport
  implements SpreadSheetWriterWithFormulaSupport {

  /** for serialization. */
  private static final long serialVersionUID = -3549185519778801930L;

  /** whether to output the cells as displayed (disable to output formulas). */
  protected boolean m_OutputAsDisplayed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes MS Excel files (using fast-excel).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "output-as-displayed", "outputAsDisplayed",
      false);
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "MS Excel spreadsheets (fast-excel)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"xlsx", "xlsm"};
  }

  /**
   * Returns, if available, the corresponding reader.
   *
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new FastExcelSpreadSheetReader();
  }

  /**
   * Returns how the data is written.
   *
   * @return		the type
   */
  @Override
  protected OutputType getOutputType() {
    return OutputType.STREAM;
  }

  /**
   * Sets whether to output the cell content as displayed, ie, no formulas
   * but the result of formulas.
   *
   * @param value	true if to output as displayed
   */
  @Override
  public void setOutputAsDisplayed(boolean value) {
    m_OutputAsDisplayed = value;
    reset();
  }

  /**
   * Returns whether to output the cell content as displayed, ie, no formulas
   * but the result of formulas.
   *
   * @return		true if to output as displayed
   */
  @Override
  public boolean getOutputAsDisplayed() {
    return m_OutputAsDisplayed;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  @Override
  public String outputAsDisplayedTipText() {
    return "If enabled, cells are output as displayed, ie, results of formulas instead of the formulas.";
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param out		the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet[] content, OutputStream out) {
    boolean		result;
    Workbook 		workbook;
    Worksheet 		sheet;
    Row 		spRow;
    Cell 		spCell;
    int 		i;
    int 		n;
    int			count;
    HashSet<String>	names;
    String		name;

    result = true;

    try {
      workbook = new Workbook(out, Environment.getInstance().getProject(), Modules.getSingleton().getModule("adams-excel").getVersion());

      count = 0;
      names = new HashSet<>();
      for (SpreadSheet cont: content) {
	if (m_Stopped)
	  return false;

	if (cont.getName() != null) {
	  name = cont.getName().replace("'", "");
	  if (names.contains(name))
	    name += (count + 1);
	}
	else {
	  name = m_SheetPrefix + (count + 1);
	}
	sheet = workbook.newWorksheet(name);
	names.add(name);

	// header
	for (i = 0; i < cont.getColumnCount(); i++)
	  sheet.value(0, i, cont.getHeaderRow().getCell(i).getContent());

	// data
	for (n = 0; n < cont.getRowCount(); n++) {
	  if (m_Stopped)
	    return false;
	  spRow = cont.getRow(n);
	  for (i = 0; i < cont.getColumnCount(); i++) {
	    spCell = spRow.getCell(i);
	    if ((spCell == null) || spCell.isMissing()) {
	      if (!m_MissingValue.isEmpty())
		sheet.value(n+1, i, m_MissingValue);
	      else
		sheet.value(n+1, i, "");
	      continue;
	    }

	    if (spCell.isFormula() && !m_OutputAsDisplayed) {
	      sheet.value(n+1, i, spCell.getFormula().substring(1));
	    }
	    else {
	      if (spCell.isAnyDateType()) {
		sheet.value(n+1, i, spCell.toDate());
	      }
	      else if (spCell.isNumeric()) {
		sheet.value(n+1, i, Utils.toDouble(spCell.getContent()));
	      }
	      else if (spCell.isBoolean()) {
		sheet.value(n+1, i, spCell.toBoolean());
	      }
	      else {
		sheet.value(n+1, i, spCell.getContent());
	      }
	    }
	  }
	}

	// next sheet
	count++;
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet data", e);
    }

    return result;
  }
}

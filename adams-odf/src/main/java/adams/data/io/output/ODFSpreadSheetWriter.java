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
 * ODFSpreadSheetWriter.java
 * Copyright (C) 2010-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import adams.core.DateFormat;
import adams.core.DateUtils;
import adams.data.io.input.ODFSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Writes ODF (Open Document Format) spreadsheet files.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
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
 * <pre>-output-as-displayed (property: outputAsDisplayed)
 * &nbsp;&nbsp;&nbsp;If enabled, cells are output as displayed, ie, results of formulas instead 
 * &nbsp;&nbsp;&nbsp;of the formulas.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ODFSpreadSheetWriter
  extends AbstractMultiSheetSpreadSheetWriterWithMissingValueSupport 
  implements SpreadSheetWriterWithFormulaSupport {

  /** for serialization. */
  private static final long serialVersionUID = -3549185519778801930L;

  /** the binary file extension. */
  public static String FILE_EXTENSION = ".ods";
  
  /** whether to output the cells as displayed (disable to output formulas). */
  protected boolean m_OutputAsDisplayed;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes ODF (Open Document Format) spreadsheet files.";
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
    return "OpenDocument format";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"ods"};
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new ODFSpreadSheetReader();
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
   * @param content	the spreadsheets to write
   * @param out		the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet[] content, OutputStream out) {
    boolean		result;
    org.jopendocument.dom.spreadsheet.SpreadSheet	spreadsheet;
    org.jopendocument.dom.spreadsheet.SpreadSheet	single;
    int			i;
    int			n;
    DefaultTableModel	model;
    TableModel 		singleModel;
    Row			row;
    Cell		cell;
    DateFormat		dformat;
    String[]		colNames;
    int			count;
    String		name;
    HashSet<String>	names;

    result = true;

    try {
      spreadsheet = org.jopendocument.dom.spreadsheet.SpreadSheet.createEmpty(new DefaultTableModel());
      dformat = DateUtils.getTimestampFormatter();
      count   = 0;
      names   = new HashSet<String>();
      for (SpreadSheet cont: content) {
	count++;
	// header
	colNames = new String[cont.getColumnCount()];
	row      = cont.getHeaderRow();
	for (i = 0; i < cont.getColumnCount(); i++) {
	  if ((row.getCell(i) == null) || row.getCell(i).isMissing())
	    colNames[i] = m_MissingValue;
	  else
	    colNames[i] = row.getCell(i).getContent().toString();
	}
	model = new DefaultTableModel(colNames, cont.getRowCount());
	// data
	for (n = 0; n < cont.getRowCount(); n++) {
	  row = cont.getRow(n);
	  for (i = 0; i < cont.getColumnCount(); i++) {
	    cell = row.getCell(i);
	    if ((cell == null) || cell.isMissing()) {
	      model.setValueAt(m_MissingValue, n, i);
	    }
	    else {
	      if (cell.isFormula() && !m_OutputAsDisplayed)
		model.setValueAt(cell.getFormula(), n, i);
	      else if (cell.isDate())
		model.setValueAt(dformat.format(cell.toDate()), n, i);
	      else if (cell.isTime())
		model.setValueAt(dformat.format(cell.toTime()), n, i);
	      else if (cell.isDateTime())
		model.setValueAt(dformat.format(cell.toDateTime()), n, i);
	      else
		model.setValueAt(cell.getContent().toString(), n, i);
	    }
	  }
	}
	single      = org.jopendocument.dom.spreadsheet.SpreadSheet.createEmpty(model);
	singleModel = single.getSheet(0).getTableModel(0, 0);
	if (cont.getName() != null) {
	  name = cont.getName().replace("'", "");
	  if (names.contains(name))
	    name += (count + 1);
	}
	else {
	  name = m_SheetPrefix + (count + 1);
	}
	names.add(name);
	if (count > 1)
	  spreadsheet.addSheet(name);
	else
	  spreadsheet.getSheet(0).setName(name);
        spreadsheet.getSheet(name).merge(singleModel, 0, 0);
      }
      spreadsheet.getPackage().save(out);
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet data", e);
    }

    return result;
  }
}

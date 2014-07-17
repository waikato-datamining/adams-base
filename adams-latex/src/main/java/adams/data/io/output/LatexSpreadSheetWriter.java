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
 * LatexSpreadSheetWriter.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.Writer;
import java.util.logging.Level;

import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Generates LaTeX tables from spreadsheets.
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
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-number-format &lt;java.lang.String&gt; (property: numberFormat)
 * &nbsp;&nbsp;&nbsp;The format for the numbers (see java.text.DecimalFormat), use empty string
 * &nbsp;&nbsp;&nbsp;for default 'double' output.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LatexSpreadSheetWriter
  extends AbstractMultiSheetFormattedSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = 8693302523602090616L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates LaTeX tables from spreadsheets.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "LaTeX table output";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"tex"};
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return null;
  }

  /**
   * Returns whether to write to an OutputStream rather than a Writer.
   *
   * @return		true if to write to an OutputStream
   */
  @Override
  protected boolean getUseOutputStream() {
    return false;
  }

  /**
   * Turns the string into LaTeX-compliant text.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String escape(String s) {
    StringBuilder	result;
    int			i;
    char		c;

    result = new StringBuilder();

    for (i = 0; i < s.length(); i++) {
      c = s.charAt(i);
      switch (c) {
	case '&':
	case '_':
	case '$':
	case '%':
	case '#':
	case '{':
	case '}':
	  result.append('\\');
	  result.append(c);
	  break;

	case '<':
	case '>':
	  result.append('$');
	  result.append(c);
	  result.append('$');
	  break;

	default:
	  result.append(c);
      }
    }

    return result.toString();
  }

  /**
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheets to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet[] content, Writer writer) {
    boolean			result;
    boolean			first;
    Cell			cell;
    int				i;
    String			newline;

    result = true;

    try {
      newline = System.getProperty("line.separator");

      for (SpreadSheet cont: content) {
	// comments?
	for (i = 0; i < cont.getComments().size(); i++)
	  writer.write("% " + cont.getComments().get(i) + newline);

	writer.write("\\begin{tabular}{|");
	for (i = 0; i < cont.getColumnCount(); i++) {
	  if (cont.isNumeric(i))
	    writer.write("r|");
	  else
	    writer.write("l|");
	}
	writer.write("}" + newline);

	// write header
	writer.write("  \\hline" + newline);
	first = true;
	for (String key: cont.getHeaderRow().cellKeys()) {
	  cell = cont.getHeaderRow().getCell(key);

	  if (!first)
	    writer.write(" & ");
	  else
	    writer.write("  ");
	  if (cell.isMissing())
	    writer.write(escape(m_MissingValue));
	  else
	    writer.write(escape(cell.getContent()));

	  first = false;
	}
	writer.write(" \\\\" + newline);

	// write data rows
	for (DataRow row: cont.rows()) {
	  writer.write("  \\hline" + newline);
	  first = true;
	  for (String key: cont.getHeaderRow().cellKeys()) {
	    cell = row.getCell(key);

	    if (!first)
	      writer.write(" & ");
	    else
	      writer.write("  ");
	    if ((cell != null) && (cell.getContent() != null) && !cell.isMissing()) {
	      if (cell.isNumeric())
		writer.write(escape(format(cell.toDouble())));
	      else
		writer.write(escape(cell.getContent()));
	    }
	    else {
	      writer.write(escape(m_MissingValue));
	    }

	    first = false;
	  }
	  writer.write(" \\\\" + newline);
	}

	writer.write("  \\hline" + newline);
	writer.write("\\end{tabular}" + newline);
	writer.write(newline);
      }
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet data", e);
    }

    return result;
  }
}

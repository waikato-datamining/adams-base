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
 * GnuplotSpreadSheetWriter.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.Writer;
import java.util.logging.Level;

import adams.core.Range;
import adams.core.Utils;
import adams.data.io.input.GnuplotSpreadSheetReader;
import adams.data.io.input.SpreadSheetReader;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.env.Environment;

/**
 <!-- globalinfo-start -->
 * Outputs all numeric columns of a spreadsheet in Gnuplot format.
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
 * &nbsp;&nbsp;&nbsp;default: -999
 * </pre>
 *
 * <pre>-columns &lt;adams.core.Range&gt; (property: columns)
 * &nbsp;&nbsp;&nbsp;The columns in the spreadsheet to output.
 * &nbsp;&nbsp;&nbsp;default: first-last
 * </pre>
 *
 * <pre>-invert (property: invert)
 * &nbsp;&nbsp;&nbsp;If set to true, then the matching sense is inverted.
 * </pre>
 *
 * <pre>-appending (property: appending)
 * &nbsp;&nbsp;&nbsp;If enabled, multiple spreadsheets with the same structure can be written
 * &nbsp;&nbsp;&nbsp;to the same file.
 * </pre>
 *
 * <pre>-keep-existing (property: keepExisting)
 * &nbsp;&nbsp;&nbsp;If enabled, any output file that exists when the writer is executed for
 * &nbsp;&nbsp;&nbsp;the first time won't get replaced with the current header; useful when outputting
 * &nbsp;&nbsp;&nbsp;data in multiple locations in the flow, but one needs to be cautious as
 * &nbsp;&nbsp;&nbsp;to not stored mixed content (eg varying number of columns, etc).
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnuplotSpreadSheetWriter
  extends AbstractSpreadSheetWriterWithMissingValueSupport
  implements AppendableSpreadSheetWriter {

  /** for serialization. */
  private static final long serialVersionUID = -1298185600402768643L;

  /** the range of columns to output in the data file. */
  protected Range m_Columns;

  /** whether to append spreadsheets. */
  protected boolean m_Appending;

  /** the header of the first spreadsheet written to file, if appending is active. */
  protected SpreadSheet m_Header;

  /** whether to keep existing files the first time the writer is called. */
  protected boolean m_KeepExisting;

  /** indicator whether a column is numeric or not. */
  protected boolean[] m_IsNumeric;

  /** whether the file already exists. */
  protected boolean m_FileExists;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs all numeric columns of a spreadsheet in Gnuplot format.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "columns", "columns",
	    new Range(Range.ALL));

    m_OptionManager.add(
	    "invert", "invert",
	    false);

    m_OptionManager.add(
	    "appending", "appending",
	    false);

    m_OptionManager.add(
	    "keep-existing", "keepExisting",
	    false);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Header     = null;
    m_FileExists = false;
  }

  /**
   * Resets the writer.
   */
  @Override
  public void reset() {
    super.reset();

    m_Header     = null;
    m_FileExists = false;
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return new GnuplotSpreadSheetReader().getFormatDescription();
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new GnuplotSpreadSheetReader().getFormatExtensions();
  }

  /**
   * Returns, if available, the corresponding reader.
   * 
   * @return		the reader, null if none available
   */
  public SpreadSheetReader getCorrespondingReader() {
    return new GnuplotSpreadSheetReader();
  }

  /**
   * Returns the default missing value.
   *
   * @return		the default for missing values
   */
  @Override
  protected String getDefaultMissingValue() {
    return GnuplotSpreadSheetReader.MISSING_VALUE;
  }

  /**
   * Sets the placeholder for missing values.
   *
   * @param value	the placeholder
   */
  public void setColumns(Range value) {
    m_Columns = value;
    reset();
  }

  /**
   * Returns the current placeholder for missing values.
   *
   * @return		the placeholder
   */
  public Range getColumns() {
    return m_Columns;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String columnsTipText() {
    return "The columns in the spreadsheet to output.";
  }

  /**
   * Sets whether to invert the matching sense.
   *
   * @param value	true if inverting matching sense
   */
  public void setInvert(boolean value) {
    m_Columns.setInverted(value);
    reset();
  }

  /**
   * Returns whether to invert the matching sense.
   *
   * @return		true if matching sense is inverted
   */
  public boolean getInvert() {
    return m_Columns.isInverted();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String invertTipText() {
    return "If set to true, then the matching sense is inverted.";
  }

  /**
   * Checks whether we can append the specified spreadsheet to the existing
   * file.
   *
   * @param sheet	the spreadsheet to append to the existing one
   * @return		true if appending is possible
   */
  @Override
  public boolean canAppend(SpreadSheet sheet) {
    if (m_Header == null)
      return m_KeepExisting;
    return (m_Header.equalsHeader(sheet) == null);
  }

  /**
   * Sets whether the next write call is to append the data to the existing
   * file.
   *
   * @param value	true if to append
   */
  @Override
  public void setAppending(boolean value) {
    m_Appending = value;
    reset();
  }

  /**
   * Returns whether the next spreadsheet will get appended.
   *
   * @return		true if append is active
   */
  @Override
  public boolean isAppending() {
    return m_Appending;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  @Override
  public String appendingTipText() {
    return "If enabled, multiple spreadsheets with the same structure can be written to the same file.";
  }

  /**
   * Sets whether to keep any existing file on first execution.
   *
   * @param value	if true then existing file is kept
   */
  @Override
  public void setKeepExisting(boolean value) {
    m_KeepExisting = value;
    reset();
  }

  /**
   * Returns whether any existing file is kept on first execution.
   *
   * @return		true if existing file is kept
   */
  @Override
  public boolean getKeepExisting() {
    return m_KeepExisting;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String keepExistingTipText() {
    return
        "If enabled, any output file that exists when the writer is executed "
      + "for the first time won't get replaced with the current header; "
      + "useful when outputting data in multiple locations in the flow, but "
      + "one needs to be cautious as to not stored mixed content (eg varying "
      + "number of columns, etc).";
  }

  /**
   * Sets whether the output file already exists.
   *
   * @param value	true if the output file already exists
   */
  @Override
  public void setFileExists(boolean value) {
    m_FileExists = value;
  }

  /**
   * Returns whether the output file already exists.
   *
   * @return		true if the output file already exists
   */
  @Override
  public boolean getFileExists() {
    return m_FileExists;
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
   * Performs the actual writing. The caller must ensure that the writer gets
   * closed.
   *
   * @param content	the spreadsheet to write
   * @param writer	the writer to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet content, Writer writer) {
    boolean			result;
    boolean			first;
    Row				header;
    Cell			cell;
    int				i;
    int[]			indices;
    String			newline;
    String			colName;

    result = true;

    try {
      newline = System.getProperty("line.separator");

      m_Columns.setMax(content.getColumnCount());
      indices = m_Columns.getIntIndices();

      if (m_Header == null) {
	// determine numeric columns
	m_IsNumeric = new boolean[indices.length];
	first     = true;
	for (i = 0; i < m_IsNumeric.length; i++) {
	  m_IsNumeric[i] = content.isNumeric(indices[i]);
	  if (m_IsNumeric[i])
	    first = false;
	}
	// no numeric column found!
	if (first)
	  return false;

	if (!m_FileExists || !m_KeepExisting) {
	  // generated by ADAMS
	  writer.write(GnuplotSpreadSheetReader.COMMENT + " Project: " + Environment.getInstance().getProject() + newline);
	  writer.write(GnuplotSpreadSheetReader.COMMENT + " User: " + System.getProperty("user.name") + newline);
	  writer.write(GnuplotSpreadSheetReader.COMMENT + newline);

	  // spreadsheet name?
	  if (content.hasName()) {
	    writer.write(GnuplotSpreadSheetReader.COMMENT + " " + content.getName() + newline);
	    writer.write(GnuplotSpreadSheetReader.COMMENT + newline);
	  }

	  // comments?
	  if (content.getComments().size() > 0) {
	    for (i = 0; i < content.getComments().size(); i++)
	      writer.write(GnuplotSpreadSheetReader.COMMENT + " " + content.getComments().get(i) + newline);
	    writer.write(GnuplotSpreadSheetReader.COMMENT + newline);
	  }

	  // write header
	  first = true;
	  for (i = 0; i < indices.length; i++) {
	    cell = content.getHeaderRow().getCell(indices[i]);

	    // can we output cell?
	    if (!m_IsNumeric[i])
	      continue;

	    if (first)
	      writer.write(GnuplotSpreadSheetReader.COMMENT + " ");
	    else
	      writer.write("\t");
	    if (cell.isMissing())
	      writer.write(Utils.doubleQuote(m_MissingValue));
	    else
	      writer.write(Utils.doubleQuote(cell.getContent()));

	    first = false;
	  }
	  writer.write(newline);
	}

	// keep header as reference
	if (m_Appending)
	  m_Header = content.getHeader();
      }

      // write data rows
      header = content.getHeaderRow();
      for (DataRow row: content.rows()) {
	first = true;
	for (i = 0; i < indices.length; i++) {
	  colName = header.getCellKey(indices[i]);
	  cell    = row.getCell(colName);

	  // can we output cell?
	  if (!m_IsNumeric[i])
	    continue;

	  if (!first)
	    writer.write("\t");
	  if ((cell != null) && (cell.getContent() != null) && !cell.isMissing())
	    writer.write(Utils.doubleQuote(cell.getContent()));
	  else
	    writer.write(Utils.doubleQuote(m_MissingValue));

	  first = false;
	}
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

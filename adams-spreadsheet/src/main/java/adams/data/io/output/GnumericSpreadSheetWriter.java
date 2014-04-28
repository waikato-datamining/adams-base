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
 * GnumericSpreadSheetWriter.java
 * Copyright (C) 2011-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.output;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import adams.core.DateFormat;
import adams.core.Utils;
import adams.core.base.BaseDateTime;
import adams.core.net.HtmlUtils;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Writes Gnumeric workbook files (GZIP compressed or uncompressed XML), version 1.10.13.
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
 * <pre>-creation-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: creationTimestamp)
 * &nbsp;&nbsp;&nbsp;The timestamp to use as creation date&#47;time info in the file.
 * &nbsp;&nbsp;&nbsp;default: NOW
 * </pre>
 * 
 * <pre>-uncompressed (property: uncompressedOutput)
 * &nbsp;&nbsp;&nbsp;If enabled, uncompressed XML instead of GZIP compressed output is generated.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnumericSpreadSheetWriter
  extends AbstractMultiSheetSpreadSheetWriterWithMissingValueSupport {

  /** for serialization. */
  private static final long serialVersionUID = -3549185519778801930L;

  /** the major version. */
  public final static String VERSION_MAJOR = "1";

  /** the minor version. */
  public final static String VERSION_MINOR = "10";

  /** the revision version. */
  public final static String VERSION_REVISION = "13";

  /** the complete version. */
  public final static String VERSION = VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_REVISION;

  /** numeric cells. */
  public final static String VALUETYPE_NUMERIC = "40";

  /** string cells. */
  public final static String VALUETYPE_STRING = "60";
  
  /** the creation timestamp. */
  protected BaseDateTime m_CreationTimestamp;

  /** whether to use uncompressed output. */
  protected boolean m_UncompressedOutput;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Writes Gnumeric workbook files (GZIP compressed or uncompressed XML), version " + VERSION + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "creation-timestamp", "creationTimestamp",
	    new BaseDateTime(BaseDateTime.NOW));

    m_OptionManager.add(
	    "uncompressed", "uncompressedOutput",
	    false);
  }

  /**
   * Sets the creation timestamp.
   *
   * @param value	the timestamp
   */
  public void setCreationTimestamp(BaseDateTime value) {
    m_CreationTimestamp = value;
    reset();
  }

  /**
   * Returns the creation timestamp.
   *
   * @return		the timestamp
   */
  public BaseDateTime getCreationTimestamp() {
    return m_CreationTimestamp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String creationTimestampTipText() {
    return "The timestamp to use as creation date/time info in the file.";
  }

  /**
   * Sets whether to output uncompressed XML.
   *
   * @param value	if true uncompressed XML is generated
   */
  public void setUncompressedOutput(boolean value) {
    m_UncompressedOutput = value;
    reset();
  }

  /**
   * Returns whether to output uncompressed XML.
   *
   * @return		true if uncompressed XML is generated
   */
  public boolean getUncompressedOutput() {
    return m_UncompressedOutput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String uncompressedOutputTipText() {
    return "If enabled, uncompressed XML instead of GZIP compressed output is generated.";
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "Gnumeric (XML files)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"gnumeric"};
  }

  /**
   * Returns whether to write to an OutputStream rather than a Writer.
   *
   * @return		true if to write to an OutputStream
   */
  @Override
  protected boolean getUseOutputStream() {
    return true;
  }

  /**
   * Returns the integer bound for number of columns/rows.
   *
   * @param content	the sheet to use as basis
   * @param cols	if true columns are checked, otherwise rows
   * @return		the bound (base 256)
   */
  protected long getBound(SpreadSheet content, boolean cols) {
    long	result;
    long	current;
    long	factor;

    factor = 256;
    result = factor;

    if (cols)
      current = content.getColumnCount();
    else
      current = content.getRowCount();

    while (result < current)
      result *= factor;

    return result;
  }

  /**
   * Performs the actual writing. The caller must ensure that the output stream
   * gets closed.
   *
   * @param content	the spreadsheets to write
   * @param out		the output stream to write the spreadsheet to
   * @return		true if successfully written
   */
  @Override
  protected boolean doWrite(SpreadSheet[] content, OutputStream out) {
    boolean		result;
    PrintStream		stream;
    Row			row;
    Cell		cell;
    int			i;
    int			n;
    DateFormat		dformat;
    String		name;
    String		type;
    GZIPOutputStream	gzip;
    boolean		numeric;
    String		value;
    int			count;

    result = true;

    try {
      if (m_UncompressedOutput) {
	gzip   = null;
	stream = new PrintStream(out);
      }
      else {
	gzip   = new GZIPOutputStream(out);
	stream = new PrintStream(gzip);
      }
      dformat = new DateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

      // XML header
      stream.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
      stream.println("<gnm:Workbook xmlns:gnm=\"http://www.gnumeric.org/v10.dtd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gnumeric.org/v9.xsd\">");
      stream.println("  <gnm:Version Epoch=\"" + VERSION_MAJOR + "\" Major=\"" + VERSION_MINOR + "\" Minor=\"" + VERSION_REVISION + "\" Full=\"" + VERSION + "\"/>");
      stream.println("  <office:document-meta xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:meta=\"urn:oasis:names:tc:opendocument:xmlns:meta:1.0\" xmlns:ooo=\"http://openoffice.org/2004/office\" office:version=\"1.1\">");
      stream.println("    <office:meta>");
      stream.println("      <meta:creation-date>" + dformat.format(m_CreationTimestamp.dateValue()) + "</meta:creation-date>");
      stream.println("    </office:meta>");
      stream.println("  </office:document-meta>");

      // sheet names
      stream.println("  <gnm:SheetNameIndex>");
      count = 0;
      for (SpreadSheet cont: content) {
	count++;
	name = m_SheetPrefix + count;
	stream.println("    <gnm:SheetName gnm:Cols=\"" + getBound(cont, true) + "\" gnm:Rows=\"" + getBound(cont, false) + "\">" + HtmlUtils.toHTML(name) + "</gnm:SheetName>");
      }
      stream.println("  </gnm:SheetNameIndex>");

      // sheets
      stream.println("  <gnm:Sheets>");
      count = 0;
      for (SpreadSheet cont: content) {
	count++;
	name = m_SheetPrefix + count;
	stream.println("    <gnm:Sheet>");
	stream.println("      <gnm:Name>" + HtmlUtils.toHTML(name) + "</gnm:Name>");
	stream.println("      <gnm:MaxCol>" + cont.getColumnCount() + "</gnm:MaxCol>");
	stream.println("      <gnm:MaxRow>" + cont.getRowCount() + "</gnm:MaxRow>");
	stream.println("      <gnm:Cells>");

	// write header
	for (i = 0; i < cont.getColumnCount(); i++) {
	  cell = cont.getHeaderRow().getCell(i);
	  stream.print("        <gnm:Cell Row=\"0\" Col=\"" + i + "\" ValueType=\"" + VALUETYPE_STRING + "\">");
	  if (cell.isMissing())
	    stream.print(Utils.doubleQuote(m_MissingValue));
	  else
	    stream.print(Utils.doubleQuote(cell.getContent()));
	  stream.println("</gnm:Cell>");
	}

	// write data rows
	for (n = 0; n < cont.getRowCount();  n++) {
	  row = cont.getRow(n);
	  for (i = 0; i < cont.getColumnCount(); i++) {
	    cell = row.getCell(i);
	    if ((cell == null) || (cell.getContent() == null) || (cell.getContent().length() == 0) || cell.isMissing()) {
	      if (m_MissingValue.length() == 0) {
		continue;
	      }
	      else {
		numeric = false;
		value   = HtmlUtils.toHTML(m_MissingValue);
	      }
	    }
	    else {
	      numeric = cell.isNumeric();
	      if (numeric)
		value = "" + cell.toDouble();
	      else
		value = HtmlUtils.toHTML(cell.getContent());
	    }
	    if (numeric)
	      type = VALUETYPE_NUMERIC;
	    else
	      type = VALUETYPE_STRING;
	    stream.print("        <gnm:Cell Row=\"" + (n+1) + "\" Col=\"" + i + "\" ValueType=\"" + type + "\">");
	    stream.print(value);
	    stream.println("</gnm:Cell>");
	  }
	}

	// footer
	stream.println("      </gnm:Cells>");
	stream.println("    </gnm:Sheet>");
      }
      stream.println("  </gnm:Sheets>");
      stream.println("</gnm:Workbook>");
      stream.flush();
      if (!m_UncompressedOutput)
	gzip.finish();
    }
    catch (Exception e) {
      result = false;
      getLogger().log(Level.SEVERE, "Failed writing spreadsheet data", e);
    }

    return result;
  }
}

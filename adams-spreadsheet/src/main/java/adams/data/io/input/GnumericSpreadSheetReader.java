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
 * GnumericSpreadSheetReader.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Utils;
import adams.data.io.output.GnumericSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

/**
 <!-- globalinfo-start -->
 * Reads Gnumeric workbook files (GZIP compressed or uncompressed XML), version 1.10.13.
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
 * <pre>-sheets &lt;adams.core.Range&gt; (property: sheetRange)
 * &nbsp;&nbsp;&nbsp;The range of sheets to load.
 * &nbsp;&nbsp;&nbsp;default: first
 * &nbsp;&nbsp;&nbsp;example: A range is a comma-separated list of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts the range '...'; the following placeholders can be used as well: first, second, third, last_2, last_1, last
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 * 
 * <pre>-uncompressed &lt;boolean&gt; (property: uncompressedInput)
 * &nbsp;&nbsp;&nbsp;If enabled, file is assumed to be uncompressed XML rather than GZIP compressed.
 * &nbsp;&nbsp;&nbsp;default: false
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnumericSpreadSheetReader
  extends AbstractMultiSheetSpreadSheetReaderWithMissingValueSupport
  implements NoHeaderSpreadSheetReader, WindowedSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = -2259236765510690348L;

  /** the version. */
  public final static String VERSION = GnumericSpreadSheetWriter.VERSION;

  /** whether to use uncompressed input. */
  protected boolean m_UncompressedInput;

  /** whether the file has a header or not. */
  protected boolean m_NoHeader;

  /** the comma-separated list of column header names. */
  protected String m_CustomColumnHeaders;

  /** the first row to retrieve (1-based). */
  protected int m_FirstRow;

  /** the number of rows to retrieve (less than 1 = unlimited). */
  protected int m_NumRows;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads Gnumeric workbook files (GZIP compressed or uncompressed XML), version " + VERSION + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "uncompressed", "uncompressedInput",
      false);

    m_OptionManager.add(
      "no-header", "noHeader",
      false);

    m_OptionManager.add(
      "custom-column-headers", "customColumnHeaders",
      "");

    m_OptionManager.add(
      "first-row", "firstRow",
      1, 1, null);

    m_OptionManager.add(
      "num-rows", "numRows",
      -1, -1, null);
  }

  /**
   * Sets whether input is uncompressed XML.
   *
   * @param value	true if uncompressed XML
   */
  public void setUncompressedInput(boolean value) {
    m_UncompressedInput = value;
    reset();
  }

  /**
   * Returns whether input is uncompressed XML.
   *
   * @return		true if uncompressed XML
   */
  public boolean getUncompressedInput() {
    return m_UncompressedInput;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String uncompressedInputTipText() {
    return "If enabled, file is assumed to be uncompressed XML rather than GZIP compressed.";
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
   * Sets the first row to return.
   *
   * @param value	the first row (1-based), greater than 0
   */
  public void setFirstRow(int value) {
    if (getOptionManager().isValid("firstRow", value)) {
      m_FirstRow = value;
      reset();
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
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new GnumericSpreadSheetWriter();
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.STREAM;
  }
  
  /**
   * Performs the actual reading.
   *
   * @param in		the input stream to read from
   * @return		the spreadsheets or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected List<SpreadSheet> doReadRange(InputStream in) {
    List<SpreadSheet>		result;
    SpreadSheet			sheet;
    DocumentBuilderFactory 	dbFactory;
    DocumentBuilder 		dBuilder;
    Document 			doc;
    NodeList 			sheetList;
    int[]			indices;
    NodeList			cellList;
    int				i;
    int				n;
    Node			sheetNode;
    Node			cellNode;
    Node			node;
    String			name;
    int				cols;
    int				rows;
    int				cellCol;
    int				cellRow;
    Row				row;
    String			type;
    String			nname;
    String			content;
    int				firstRow;
    int 			lastRow;
    boolean			isHeader;

    result = new ArrayList<>();
    
    try {
      if (!m_UncompressedInput)
	in = new GZIPInputStream(in);

      dbFactory = DocumentBuilderFactory.newInstance();
      dBuilder  = dbFactory.newDocumentBuilder();
      doc       = dBuilder.parse(in);    
      doc.getDocumentElement().normalize();
      
      // traverse sheets
      sheetList = doc.getElementsByTagName("gnm:Sheet");
      m_SheetRange.setMax(sheetList.getLength());
      indices = m_SheetRange.getIntIndices();
      for (i = 0; i < indices.length; i++) {
	sheet = m_SpreadSheetType.newInstance();
	sheet.setDataRowClass(m_DataRowType.getClass());
	result.add(sheet);
	
	sheetNode = sheetList.item(indices[i]);
	// sheet specs
	name = "Sheet" + (indices[i]+1);
	rows = -1;
	cols = -1;
	for (n = 0; n < sheetNode.getChildNodes().getLength(); n++) {
	  node  = sheetNode.getChildNodes().item(n);
	  nname = node.getNodeName();
	  if ("gnm:Name".equals(nname) || "Name".equals(nname))
	    name = node.getTextContent();
	  else if ("gnm:MaxRow".equals(nname) || "MaxRow".equals(nname))
	    rows = Integer.parseInt(node.getTextContent()) + 1;
	  else if ("gnm:MaxCol".equals(nname) || "MaxCol".equals(nname))
	    cols = Integer.parseInt(node.getTextContent()) + 1;
	}
	sheet.setName(name);
	if (isLoggingEnabled()) {
	  getLogger().info("Sheet: " + name);
	  getLogger().info("Rows: " + rows);
	  getLogger().info("Cols: " + cols);
	}
	
	// generate matrix
	if (cols != -1) {
	  row = sheet.getHeaderRow();
	  if (m_NoHeader) {
	    for (String col: SpreadSheetUtils.createHeader(cols, m_CustomColumnHeaders))
	      row.addCell("" + sheet.getColumnCount()).setContentAsString(col);
	  }
	  else {
	    // gets filled in later on
	    for (n = 0; n < cols; n++)
	      row.addCell("" + n).setContentAsString(SpreadSheetUtils.PREFIX_COL + (n + 1));
	  }
	}

	// cells
	firstRow = m_FirstRow - 1;
	if (m_NumRows > 0)
	  lastRow = firstRow + m_NumRows - 1;
	else
	  lastRow = -1;
	cellList = ((Element) sheetNode).getElementsByTagName("gnm:Cell");
	for (n = 0; n < cellList.getLength(); n++) {
	  cellNode = cellList.item(n);
	  type     = GnumericSpreadSheetWriter.VALUETYPE_STRING;
	  cellCol  = -1;
	  cellRow  = -1;
	  content  = cellNode.getTextContent();
	  // cell type
	  node = cellNode.getAttributes().getNamedItem("ValueType");
	  if (node == null)
	    node = cellNode.getAttributes().getNamedItem("gnm:ValueType");
	  if (node != null)
	    type = node.getTextContent();
	  // col
	  node = cellNode.getAttributes().getNamedItem("Col");
	  if (node == null)
	    node = cellNode.getAttributes().getNamedItem("gnm:Col");
	  if (node != null)
	    cellCol = Integer.parseInt(node.getTextContent());
	  // row
	  node = cellNode.getAttributes().getNamedItem("Row");
	  if (node == null)
	    node = cellNode.getAttributes().getNamedItem("gnm:Row");
	  if (node != null)
	    cellRow = Integer.parseInt(node.getTextContent());
	  // valid location?
	  if (cellRow < firstRow)
	    continue;
	  if ((lastRow > -1) && (cellRow > lastRow))
	    continue;
	  // add cell
	  if ((cellCol != -1) && (cellRow != -1)) {
	    // make sure cell can be placed
	    while (sheet.getColumnCount() <= cellCol)
	      sheet.getHeaderRow().addCell("" + sheet.getColumnCount()).setContent(SpreadSheetUtils.PREFIX_COL + sheet.getColumnCount());
	    if (m_NoHeader) {
	      while (sheet.getRowCount() <= cellRow - firstRow)
		sheet.addRow();
	    }
	    else {
	      while (sheet.getRowCount() < cellRow - firstRow)  // header is extra!
		sheet.addRow();
	    }
	    // add cell
	    isHeader = false;
	    if (m_NoHeader) {
	      row = sheet.getRow(cellRow - firstRow);
	    }
	    else {
	      if (cellRow - firstRow == 0) {
		isHeader = true;
		row      = sheet.getHeaderRow();
	      }
	      else {
		row = sheet.getRow(cellRow - firstRow - 1);
	      }
	    }
	    if (m_MissingValue.isMatch(content))
	      content = null;
	    if (content == null)
	      row.addCell(cellCol).setMissing();
	    else if (isHeader)
	      row.addCell(cellCol).setContentAsString(content);
	    else if (type.equals(GnumericSpreadSheetWriter.VALUETYPE_NUMERIC) && (content.length() > 0))
	      row.addCell(cellCol).setContent(Double.parseDouble(content));
	    else
	      row.addCell(cellCol).setContent(content);
	    if (isLoggingEnabled())
	      getLogger().info(SpreadSheetUtils.getCellPosition(cellRow, cellCol) + ": " + content);
	  }
	}
      }
    }
    catch (Exception e) {
      result = null;
      getLogger().log(Level.SEVERE, "Failed to read data!", e);
      m_LastError = "Failed to read range '" + m_SheetRange + "' from stream!\n" + Utils.throwableToString(e);
    }
    
    return result;
  }
}

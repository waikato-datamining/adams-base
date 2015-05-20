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
 * Copyright (C) 2013-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adams.core.Utils;
import adams.data.io.output.GnumericSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Reads Gnumeric workbook files (GZIP compressed or uncompressed XML), version 1.10.13.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-data-row-type &lt;DENSE|SPARSE&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: DENSE
 * </pre>
 * 
 * <pre>-sheets &lt;adams.core.Range&gt; (property: sheetRange)
 * &nbsp;&nbsp;&nbsp;The range of sheets to load; A range is a comma-separated list of single 
 * &nbsp;&nbsp;&nbsp;1-based indices or sub-ranges of indices ('start-end'); 'inv(...)' inverts 
 * &nbsp;&nbsp;&nbsp;the range '...'; the following placeholders can be used as well: first, 
 * &nbsp;&nbsp;&nbsp;second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: first
 * </pre>
 * 
 * <pre>-missing &lt;java.lang.String&gt; (property: missingValue)
 * &nbsp;&nbsp;&nbsp;The placeholder for missing values.
 * &nbsp;&nbsp;&nbsp;default: ?
 * </pre>
 * 
 * <pre>-uncompressed (property: uncompressedOutput)
 * &nbsp;&nbsp;&nbsp;If enabled, file is assumed to be uncompressed XML rather than GZIP compressed.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GnumericSpreadSheetReader
  extends AbstractMultiSheetSpreadSheetReaderWithMissingValueSupport {

  /** for serialization. */
  private static final long serialVersionUID = -2259236765510690348L;

  /** the version. */
  public final static String VERSION = GnumericSpreadSheetWriter.VERSION;

  /** whether to use uncompressed output. */
  protected boolean m_UncompressedOutput;

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
	    "uncompressed", "uncompressedOutput",
	    false);
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
    return "If enabled, file is assumed to be uncompressed XML rather than GZIP compressed.";
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
    
    result = new ArrayList<SpreadSheet>();
    
    try {
      if (!m_UncompressedOutput)
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
	    rows = Integer.parseInt(node.getTextContent());
	  else if ("gnm:MaxCol".equals(nname) || "MaxCol".equals(nname))
	    cols = Integer.parseInt(node.getTextContent());
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
	  for (n = 0; n < cols; n++)
	    row.addCell("" + n).setContent("Col" + (n+1));
	}
	if (rows != -1) {
	  for (n = 0; n < rows; n++)
	    sheet.addRow();
	}
	
	// cells
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
	  // add cell
	  if ((cellCol != -1) && (cellRow != -1)) {
	    // make sure cell can be placed
	    while (sheet.getColumnCount() <= cellCol)
	      sheet.getHeaderRow().addCell("" + sheet.getColumnCount()).setContent("Col" + sheet.getColumnCount());
	    while (sheet.getRowCount() < cellRow)  // header is extra!
	      sheet.addRow();
	    // add cell
	    if (cellRow == 0)
	      row = sheet.getHeaderRow();
	    else
	      row = sheet.getRow(cellRow - 1);
	    if (content.equals(m_MissingValue))
	      content = null;
	    if (content == null)
	      row.addCell(cellCol).setMissing();
	    else if (type.equals(GnumericSpreadSheetWriter.VALUETYPE_NUMERIC) && (content.length() > 0))
	      row.addCell(cellCol).setContent(Double.parseDouble(content));
	    else
	      row.addCell(cellCol).setContent(content);
	    if (isLoggingEnabled())
	      getLogger().info(SpreadSheet.getCellPosition(cellRow, cellCol) + ": " + content);
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

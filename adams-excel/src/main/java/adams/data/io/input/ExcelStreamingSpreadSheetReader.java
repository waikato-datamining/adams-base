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
 * ExcelStreamingSpreadSheetReader.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 * Copyright (C) Apache Foundation (example SAX handler)
 */
package adams.data.io.input;

import adams.core.DateTime;
import adams.core.DateTimeMsec;
import adams.core.ExcelHelper;
import adams.core.License;
import adams.core.Stoppable;
import adams.core.Time;
import adams.core.Utils;
import adams.core.annotation.MixedCopyright;
import adams.core.base.BaseString;
import adams.core.io.FileUtils;
import adams.core.logging.LoggingHelper;
import adams.data.io.output.ExcelStreamingSpreadSheetWriter;
import adams.data.io.output.SpreadSheetWriter;
import adams.data.spreadsheet.Cell;
import adams.data.spreadsheet.Cell.ContentType;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetUtils;
import gnu.trove.set.hash.TIntHashSet;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

/**
 <!-- globalinfo-start -->
 * Reads large MS Excel XML files (using streaming via SAX).<br>
 * Increasing the debug level to more than 1 results in outputting detailed information on cells.
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
 * <pre>-no-auto-extend-header (property: autoExtendHeader)
 * &nbsp;&nbsp;&nbsp;If enabled, the header gets automatically extended if rows have more cells 
 * &nbsp;&nbsp;&nbsp;than the header.
 * </pre>
 * 
 * <pre>-text-columns &lt;java.lang.String&gt; (property: textColumns)
 * &nbsp;&nbsp;&nbsp;The range of columns to treat as text; A range is a comma-separated list 
 * &nbsp;&nbsp;&nbsp;of single 1-based indices or sub-ranges of indices ('start-end'); 'inv(..
 * &nbsp;&nbsp;&nbsp;.)' inverts the range '...'; the following placeholders can be used as well:
 * &nbsp;&nbsp;&nbsp; first, second, third, last_2, last_1, last
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-cell-type-id &lt;adams.core.base.BaseString&gt; [-cell-type-id ...] (property: cellTypeID)
 * &nbsp;&nbsp;&nbsp;The IDs (= strings) for the cell types to parse.
 * &nbsp;&nbsp;&nbsp;default: b, s
 * </pre>
 * 
 * <pre>-cell-type-contenttype &lt;MISSING|STRING|BOOLEAN|LONG|DOUBLE|DATE|DATETIME|TIME|OBJECT&gt; [-cell-type-contenttype ...] (property: cellTypeContentType)
 * &nbsp;&nbsp;&nbsp;The corresponding content types for the cell types to parse.
 * &nbsp;&nbsp;&nbsp;default: BOOLEAN, STRING
 * </pre>
 * 
 * <pre>-cell-string-id &lt;adams.core.base.BaseString&gt; [-cell-string-id ...] (property: cellStringID)
 * &nbsp;&nbsp;&nbsp;The IDs (= strings) for the cell strings to parse.
 * &nbsp;&nbsp;&nbsp;default: 1, 2, 3, 4, 7, 8
 * </pre>
 * 
 * <pre>-cell-string-contenttype &lt;MISSING|STRING|BOOLEAN|LONG|DOUBLE|DATE|DATETIME|TIME|OBJECT&gt; [-cell-string-contenttype ...] (property: cellStringContentType)
 * &nbsp;&nbsp;&nbsp;The corresponding content types for the cell strings to parse.
 * &nbsp;&nbsp;&nbsp;default: DATE, TIME, DOUBLE, DATE, DATE, LONG
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@MixedCopyright(
    copyright = "Apache Foundation",
    license = License.APACHE2,
    url = "http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api",
    note = "Adapted example from Apache website"
)
public class ExcelStreamingSpreadSheetReader
  extends AbstractExcelSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = 4755872204697328246L;

  /**
   * Dummy exception to stop the parsing.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ParseStopException 
    extends SAXParseException {

    /** for serialization. */
    private static final long serialVersionUID = -5378507296511062333L;

    public ParseStopException(String message, Locator locator) {
      super(message, locator);
    }
  }
  
  /**
   * For reading a sheet from XML.
   *
   * @author  Apache Foundation (POI)
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  @MixedCopyright(
      copyright = "Apache Foundation",
      license = License.APACHE2,
      url = "http://poi.apache.org/spreadsheet/how-to.html#xssf_sax_api",
      note = "Adapted example from Apache website"
  )
  public static class SheetHandler 
    extends DefaultHandler 
    implements Stoppable {
    
    /** the reader this handler belongs to. */
    protected ExcelStreamingSpreadSheetReader m_Owner;
    
    /** the spreadsheet to add the content to. */
    protected SpreadSheet m_Sheet;
    
    /** the table for shared strings. */
    protected SharedStringsTable m_SST;
    
    /** the assembled cell content. */
    protected StringBuilder m_LastContents;
    
    /** what type the current cell is. */
    protected ContentType m_ContentType;
    
    /** the cell reference. */
    protected String m_Reference;

    /** whether the parsing was stopped. */
    protected boolean m_Stopped;
    
    /** the cell-types. */
    protected HashMap<String,ContentType> m_CellTypes;
    
    /** the cell-strings. */
    protected HashMap<String,ContentType> m_CellStrings;
    
    /** the unknown cell-type. */
    protected HashSet<String> m_UnknownCellTypes;
    
    /** the current unknown cell-types. */
    protected String m_UnknownCellType;
    
    /** examples of the unknown cell-types (type - example). */
    protected HashMap<String,String> m_UnknownCellTypesExamples;
    
    /** the unknown cell-strings. */
    protected HashSet<String> m_UnknownCellStrings;
    
    /** the current unknown cell-string. */
    protected String m_UnknownCellString;
    
    /** examples of the unknown cell-strings (string - example). */
    protected HashMap<String,String> m_UnknownCellStringsExamples;
    
    /** whether logging is at least fine. */
    protected boolean m_LoggingAtLeastFine;
    
    /** the text columns. */
    protected TIntHashSet m_TextColumns;

    /**
     * Initializes the SAX handler.
     * 
     * @param owner	the reader this handler belongs to
     * @param sheet	the spreadsheet to add the content to
     * @param sst	the table for shared strings
     */
    public SheetHandler(ExcelStreamingSpreadSheetReader owner, SpreadSheet sheet, SharedStringsTable sst) {
      int		i;
      
      m_Owner              = owner;
      m_Sheet              = sheet;
      m_SST                = sst;
      m_LastContents       = new StringBuilder();
      m_Reference          = "";
      m_ContentType        = ContentType.MISSING;
      m_Stopped            = false;
      m_LoggingAtLeastFine = LoggingHelper.isAtLeast(m_Owner.getLogger(), Level.FINE);
      m_TextColumns        = null;

      m_CellTypes                  = new HashMap<String,ContentType>();
      m_CellStrings                = new HashMap<String,ContentType>();
      m_UnknownCellTypes           = new HashSet<String>();
      m_UnknownCellTypesExamples   = new HashMap<String,String>();
      m_UnknownCellStrings         = new HashSet<String>();
      m_UnknownCellStringsExamples = new HashMap<String,String>();
      
      for (i = 0; i < m_Owner.getCellTypeID().length; i++)
	m_CellTypes.put(m_Owner.getCellTypeID()[i].stringValue(), m_Owner.getCellTypeContentType()[i]);
      for (i = 0; i < m_Owner.getCellStringID().length; i++)
	m_CellStrings.put(m_Owner.getCellStringID()[i].stringValue(), m_Owner.getCellStringContentType()[i]);
    }

    /**
     * Receive notification of the start of an element.
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param name The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @param attributes The attributes attached to the element.  If
     *        there are no attributes, it shall be an empty
     *        Attributes object.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {
      String 	cellType;
      String	cellStr;
      
      // c => cell
      if (name.equals("c")) {
	// Print the cell reference
	m_Reference = attributes.getValue("r");
	if (m_Owner.isLoggingEnabled())
	  m_Owner.getLogger().info("ref: " + m_Reference);
	// Figure out if the value is an index in the SST
	cellType = attributes.getValue("t");
	if (m_LoggingAtLeastFine)
	  m_Owner.getLogger().fine("  cellType: " + cellType);
	cellStr = attributes.getValue("s");
	if (m_LoggingAtLeastFine)
	  m_Owner.getLogger().fine("  cellStr: " + cellStr);
	m_ContentType       = ContentType.DOUBLE;
	m_UnknownCellType   = null;
	m_UnknownCellString = null;
	if ("e".equals(cellStr)) {
	  m_ContentType = null;
	}
	else if (cellType != null) {
	  if (m_CellTypes.containsKey(cellType))
	    m_ContentType = m_CellTypes.get(cellType);
	  else
	    m_UnknownCellType = cellType;
	}
	else if (cellStr != null) {
	  if (m_CellStrings.containsKey(cellStr))
	    m_ContentType = m_CellStrings.get(cellStr);
	  else
	    m_UnknownCellString = cellStr;
	}
	if (m_LoggingAtLeastFine)
	  m_Owner.getLogger().fine("  contentType: " + m_ContentType);
      }
      else {
	if (m_LoggingAtLeastFine) {
	  m_Owner.getLogger().fine(name + ":");
	  for (int i = 0; i < attributes.getLength(); i++)
	    m_Owner.getLogger().fine("  " + attributes.getQName(i) + ": " + attributes.getValue(i));
	}
      }
      
      // Clear contents cache
      if (m_LastContents.length() > 0)
	m_LastContents.delete(0, m_LastContents.length());
    }

    /**
     * Receive notification of the end of an element.
     *
     * @param uri The Namespace URI, or the empty string if the
     *        element has no Namespace URI or if Namespace
     *        processing is not being performed.
     * @param localName The local name (without prefix), or the
     *        empty string if Namespace processing is not being
     *        performed.
     * @param name The qualified name (with prefix), or the
     *        empty string if qualified names are not available.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
      int 	idx;
      int[]	loc;
      Row	row;
      Cell	cell;
      String	content;
      
      // Process the last contents as required.
      // Do now, as characters() may be called more than once
      if (m_ContentType == ContentType.STRING) {
	idx            = Integer.parseInt(m_LastContents.toString());
	m_LastContents = new StringBuilder(new XSSFRichTextString(m_SST.getEntryAt(idx)).toString());
      }

      // expand spreadsheet if necessary
      loc  = null;
      cell = null;
      if (!m_Reference.isEmpty()) {
	try {
	  loc = ExcelHelper.getCellLocation(m_Reference);
	  // fill in rows, if necessary
	  while (m_Sheet.getRowCount() < loc[0])
	    m_Sheet.addRow();
	  // fill in columns, if necessary
	  while (m_Sheet.getColumnCount() <= loc[1])
	    m_Sheet.insertColumn(m_Sheet.getColumnCount(), "col" + (m_Sheet.getColumnCount() + 1));
	  if (loc[0] == 0)
	    row = m_Sheet.getHeaderRow();
	  else
	    row = m_Sheet.getRow(loc[0] - 1);
	  if ((m_Owner.getTextColumns().getMax() < m_Sheet.getColumnCount()) || (m_TextColumns == null)) {
	    m_Owner.getTextColumns().setMax(m_Sheet.getColumnCount());
	    m_TextColumns = new TIntHashSet(m_Owner.getTextColumns().getIntIndices());
	  }
	  cell = row.addCell(loc[1]);
	}
	catch (Exception e) {
	  loc = null;
	  cell = null;
	  m_Owner.getLogger().log(Level.SEVERE,
	    "Failed to set cell content at " + m_Reference
	      + " (rows=" + m_Sheet.getRowCount() + ", cols=" + m_Sheet.getColumnCount() + "):", e);
	}
      }

      // v => contents of a cell
      // Output after we've seen the string contents
      if (name.equals("v") && (cell != null)) {
	try {
	  content = m_LastContents.toString();
	  if (m_UnknownCellType != null) {
	    m_UnknownCellTypes.add(m_UnknownCellType);
	    if (!m_UnknownCellTypesExamples.containsKey(m_UnknownCellType))
	      m_UnknownCellTypesExamples.put(m_UnknownCellType, content + " (" + m_Sheet.getName() + "/" + m_Reference + ")");
	  }
	  if (m_UnknownCellString != null) {
	    m_UnknownCellStrings.add(m_UnknownCellString);
	    if (!m_UnknownCellStringsExamples.containsKey(m_UnknownCellString))
	      m_UnknownCellStringsExamples.put(m_UnknownCellString, content + " (" + m_Sheet.getName() + "/" + m_Reference + ")");
	  }
	  if (m_Owner.isLoggingEnabled())
	    m_Owner.getLogger().info("  content: " + content);
	  if (content.equals(m_Owner.getMissingValue())) {
	    cell.setMissing();
	  }
	  else {
	    // enforce STRING cell type?
	    if (m_TextColumns.contains(loc[1]))
	      m_ContentType = ContentType.STRING;
	    
	    // eg formula
	    if (m_ContentType == null) {
	      cell.setContent(content);
	    }
	    else {
	      switch (m_ContentType) {
		case BOOLEAN:
		  cell.setContent(!content.equals("0"));
		  break;
		case DATE:
		  cell.setContent(DateUtil.getJavaDate(Double.parseDouble(content)));
		  break;
		case DATETIME:
		  cell.setContent(new DateTime(DateUtil.getJavaDate(Double.parseDouble(content))));
		  break;
		case DATETIMEMSEC:
		  cell.setContent(new DateTimeMsec(DateUtil.getJavaDate(Double.parseDouble(content))));
		  break;
		case TIME:
		  cell.setContent(new Time(DateUtil.getJavaDate(Double.parseDouble(content))));
		  break;
		case LONG:
		  cell.setContent(new Long(content));
		  break;
		case DOUBLE:
		  cell.setContent(new Double(content));
		  break;
		default:
		  cell.setContentAsString(content);
		  break;
	      }
	    }
	  }
	}
	catch (Exception e) {
	  m_Owner.getLogger().log(Level.SEVERE, 
	      "Failed to set cell content at " + m_Reference 
	      + " (rows=" + m_Sheet.getRowCount() + ", cols=" + m_Sheet.getColumnCount() + "):", e);
	}
      }

      m_ContentType = ContentType.MISSING;

      if (m_Stopped)
	throw new ParseStopException("", null);
    }

    /**
     * Receive notification of character data inside an element.
     *
     * <p>By default, do nothing.  Application writers may override this
     * method to take specific actions for each chunk of character data
     * (such as adding the data to a node or buffer, or printing it to
     * a file).</p>
     *
     * @param ch The characters.
     * @param start The start position in the character array.
     * @param length The number of characters to use from the
     *               character array.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     */
    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
      m_LastContents.append(ch, start, length);
    }

    /**
     * Stops the parsing.
     */
    public void stopExecution() {
      m_Stopped = true;
    }
    
    /**
     * Returns the unknown cell types that were encountered.
     * 
     * @return		the cell types
     */
    public HashSet<String> getUnknownCellTypes() {
      return m_UnknownCellTypes;
    }
    
    /**
     * Returns examples of the unknown cell types that were encountered.
     * 
     * @return		the examples
     */
    public HashMap<String,String> getUnknownCellTypesExamples() {
      return m_UnknownCellTypesExamples;
    }
    
    /**
     * Returns the unknown cell strings that were encountered.
     * 
     * @return		the cell strings
     */
    public HashSet<String> getUnknownCellStrings() {
      return m_UnknownCellStrings;
    }
    
    /**
     * Returns examples of the unknown cell strings that were encountered.
     * 
     * @return		the examples
     */
    public HashMap<String,String> getUnknownCellStringsExamples() {
      return m_UnknownCellStringsExamples;
    }
  }

  /** the currently used handler for parsing. */
  protected SheetHandler m_Handler;
  
  /** the extra cell types to manage. */
  protected BaseString[] m_CellTypeID;
  
  /** the corresponding types. */
  protected ContentType[] m_CellTypeContentType;
  
  /** the extra cell strings to manage. */
  protected BaseString[] m_CellStringID;
  
  /** the corresponding types. */
  protected ContentType[] m_CellStringContentType;
	
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Reads large MS Excel XML files (using streaming via SAX).\n"
	+ "Increasing the debug level to more than 1 results in outputting "
	+ "detailed information on cells.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "cell-type-id", "cellTypeID",
	    new BaseString[]{
		new BaseString("b"),
		new BaseString("s"),
	    });

    m_OptionManager.add(
	    "cell-type-contenttype", "cellTypeContentType",
	    new ContentType[]{
		ContentType.BOOLEAN,
		ContentType.STRING,
	    });

    m_OptionManager.add(
	    "cell-string-id", "cellStringID",
	    new BaseString[0]);

    m_OptionManager.add(
	    "cell-string-contenttype", "cellStringContentType",
	    new ContentType[0]);
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "MS Excel spreadsheets (large XML)";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"xlsx"};
  }

  /**
   * Returns, if available, the corresponding writer.
   * 
   * @return		the writer, null if none available
   */
  public SpreadSheetWriter getCorrespondingWriter() {
    return new ExcelStreamingSpreadSheetWriter();
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
   * Sets the array of cell type IDs.
   *
   * @param value	the IDs
   */
  public void setCellTypeID(BaseString[] value) {
    m_CellTypeID = value;
    reset();
  }

  /**
   * Returns the array of cell type IDs.
   *
   * @return		the IDs
   */
  public BaseString[] getCellTypeID() {
    return m_CellTypeID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String cellTypeIDTipText() {
    return "The IDs (= strings) for the cell types to parse.";
  }

  /**
   * Sets the array of cell type content types.
   *
   * @param value	the types
   */
  public void setCellTypeContentType(ContentType[] value) {
    m_CellTypeContentType = value;
    reset();
  }

  /**
   * Returns the array of cell type content types.
   *
   * @return		the types
   */
  public ContentType[] getCellTypeContentType() {
    return m_CellTypeContentType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String cellTypeContentTypeTipText() {
    return "The corresponding content types for the cell types to parse.";
  }

  /**
   * Sets the array of cell string IDs.
   *
   * @param value	the IDs
   */
  public void setCellStringID(BaseString[] value) {
    m_CellStringID = value;
    reset();
  }

  /**
   * Returns the array of cell string IDs.
   *
   * @return		the IDs
   */
  public BaseString[] getCellStringID() {
    return m_CellStringID;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String cellStringIDTipText() {
    return "The IDs (= strings) for the cell strings to parse.";
  }

  /**
   * Sets the array of cell string content types.
   *
   * @param value	the types
   */
  public void setCellStringContentType(ContentType[] value) {
    m_CellStringContentType = value;
    reset();
  }

  /**
   * Returns the array of cell string content types.
   *
   * @return		the types
   */
  public ContentType[] getCellStringContentType() {
    return m_CellStringContentType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  public String cellStringContentTypeTipText() {
    return "The corresponding content types for the cell strings to parse.";
  }

  /**
   * Hook method to perform some checks before performing the actual read.
   */
  @Override
  protected void check() {
    if (m_CellTypeID.length != m_CellTypeContentType.length)
      throw new IllegalStateException(
	  "Length of cellTypeID and cellTypeContentType differ: "
	  + m_CellTypeID.length + " != " + m_CellTypeContentType.length);
    if (m_CellStringID.length != m_CellStringContentType.length)
      throw new IllegalStateException(
	  "Length of cellStringID and cellStringContentType differ: "
	  + m_CellStringID.length + " != " + m_CellStringContentType.length);
  }

  /**
   * Determines the number of sheets in the file.
   * 
   * @param file	the file to inspec
   * @return		the number of sheets
   * @throws Exception	if reading of file fails
   */
  protected int getSheetCount(File file) throws Exception {
    int				result;
    OPCPackage 			pkg;
    XSSFReader 			reader;
    Iterator<InputStream> 	sheets;
    InputStream 		sheet;

    pkg       = OPCPackage.open(file.getAbsolutePath(), PackageAccess.READ);
    reader    = new XSSFReader(pkg);
    sheets    = reader.getSheetsData();
    result    = 0;
    while (sheets.hasNext()) {
      if (m_Stopped)
	break;
      sheet = sheets.next();
      sheet.close();
      result++;
    }
    pkg.close();

    return result;
  }
  
  /**
   * Reads the spreadsheet content from the specified file.
   *
   * @param file	the file to read from
   * @return		the spreadsheets or null in case of an error
   */
  @Override
  protected List<SpreadSheet> doReadRange(File file) {
    List<SpreadSheet>		result;
    int				count;
    OPCPackage 			pkg;
    XSSFReader 			reader;
    SharedStringsTable 		sst;
    XMLReader 			parser;
    XSSFReader.SheetIterator 	sheets;
    InputStream 		sheet;
    SpreadSheet			spsheet;
    InputSource 		sheetSource;
    HashSet<Integer>		indices;
    List<String>                header;
    Row                         rowOld;
    Row                         row;
    int                         i;

    result = new ArrayList<SpreadSheet>();

    try {
      m_SheetRange.setMax(getSheetCount(file));
      indices = new HashSet<Integer>(Utils.toList(m_SheetRange.getIntIndices()));
      pkg       = OPCPackage.open(file.getAbsolutePath(), PackageAccess.READ);
      reader    = new XSSFReader(pkg);
      sst       = reader.getSharedStringsTable();
      sheets    = (XSSFReader.SheetIterator) reader.getSheetsData();
      count     = 0;
      while (sheets.hasNext()) {
	if (m_Stopped)
	  break;
	sheet = null;
	try {
	  sheet = sheets.next();
	  if (indices.contains(count)) {
	    spsheet = m_SpreadSheetType.newInstance();
	    spsheet.setDataRowClass(m_DataRowType.getClass());
	    spsheet.setName(sheets.getSheetName());
	    parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
	    m_Handler = new SheetHandler(this, spsheet, sst);
	    parser.setContentHandler(m_Handler);
	    sheetSource = new InputSource(sheet);
	    parser.parse(sheetSource);
	    // fix header?
	    if (getNoHeader()) {
	      header = SpreadSheetUtils.createHeader(spsheet.getColumnCount(), getCustomColumnHeaders());
	      rowOld = spsheet.getHeaderRow();
	      row = spsheet.insertRow(0);
	      for (i = 0; i < spsheet.getColumnCount(); i++)
		row.getCell(i).assign(rowOld.getCell(i));
	      row = spsheet.getHeaderRow();
	      for (i = 0; i < header.size() && i < spsheet.getColumnCount(); i++)
		row.getCell(i).setContent(header.get(i));
	    }
	    else {
	      if (!getCustomColumnHeaders().trim().isEmpty()) {
		header = SpreadSheetUtils.createHeader(spsheet.getColumnCount(), getCustomColumnHeaders());
		row = spsheet.getHeaderRow();
		for (i = 0; i < header.size() && i < spsheet.getColumnCount(); i++)
		  row.getCell(i).setContent(header.get(i));
	      }
	    }
	    result.add(spsheet);
	    // missing types?
	    if (m_Handler.getUnknownCellTypes().size() > 0) {
	      getLogger().severe("Unknown cell types: " + m_Handler.getUnknownCellTypes());
	      for (String type : m_Handler.getUnknownCellTypes())
		getLogger().severe("- cell type '" + type + "': " + m_Handler.getUnknownCellTypesExamples().get(type));
	    }
	    if (m_Handler.getUnknownCellStrings().size() > 0) {
	      getLogger().severe("Unknown cell strings: " + m_Handler.getUnknownCellStrings());
	      for (String str : m_Handler.getUnknownCellStrings())
		getLogger().severe("- cell string '" + str + "': " + m_Handler.getUnknownCellStringsExamples().get(str));
	    }
	  }
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to process sheet:", e);
	}

	FileUtils.closeQuietly(sheet);

	count++;
      }
      pkg.close();
    }
    catch (ParseStopException e) {
      getLogger().severe("Parsing stopped!");
      result = null;
      m_LastError = "Failed to read range '" + m_SheetRange + "' from file '" + file + "'!\n" + Utils.throwableToString(e);
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to read spreadsheet:", e);
    }

    m_Handler = null;
    
    return result;
  }

  /**
   * Stops the reading (might not be immediate, depending on reader).
   */
  @Override
  public void stopExecution() {
    super.stopExecution();
    if (m_Handler != null)
      m_Handler.stopExecution();
  }
}

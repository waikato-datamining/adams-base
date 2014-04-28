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
 * SqlDumpSpreadSheetReader.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;

import adams.core.Utils;
import adams.data.spreadsheet.Row;
import adams.data.spreadsheet.SpreadSheet;

/**
 <!-- globalinfo-start -->
 * Reads in SQL dump files.
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
 * <pre>-data-row-type &lt;DENSE|SPARSE&gt; (property: dataRowType)
 * &nbsp;&nbsp;&nbsp;The type of row to use for the data.
 * &nbsp;&nbsp;&nbsp;default: DENSE
 * </pre>
 * 
 * <pre>-custom-column-headers &lt;java.lang.String&gt; (property: customColumnHeaders)
 * &nbsp;&nbsp;&nbsp;The custom headers to use for the columns (comma-separated list).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-time-zone &lt;java.util.TimeZone&gt; (property: timeZone)
 * &nbsp;&nbsp;&nbsp;The time zone to use for interpreting dates&#47;times; default is the system-wide 
 * &nbsp;&nbsp;&nbsp;defined one.
 * </pre>
 * 
 * <pre>-use-backslashes (property: useBackslashes)
 * &nbsp;&nbsp;&nbsp;If enabled, any output file that exists when the writer is executed for 
 * &nbsp;&nbsp;&nbsp;the first time won't get replaced with the current header; useful when outputting 
 * &nbsp;&nbsp;&nbsp;data in multiple locations in the flow, but one needs to be cautious as 
 * &nbsp;&nbsp;&nbsp;to not stored mixed content (eg varying number of columns, etc).
 * </pre>
 * 
 * <pre>-chunk-size &lt;int&gt; (property: chunkSize)
 * &nbsp;&nbsp;&nbsp;The maximum number of rows per chunk; using -1 will read put all data into 
 * &nbsp;&nbsp;&nbsp;a single spreadsheet object.
 * &nbsp;&nbsp;&nbsp;default: -1
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SqlDumpSpreadSheetReader
  extends AbstractSpreadSheetReader
  implements ChunkedSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = -1828187977878321234L;

  /**
   * Reads CSV files chunk by chunk.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class ChunkReader 
    implements Serializable {

    /** for serialization. */
    private static final long serialVersionUID = 26915431591885853L;
    
    /** the owning reader. */
    protected SqlDumpSpreadSheetReader m_Owner;

    /** the reader in use. */
    protected BufferedReader m_Reader;
    
    /** the header. */
    protected SpreadSheet m_Header;

    /** the chunk size. */
    protected int m_ChunkSize;
    
    /** whether to backslashes are used for escaping single quotes. */
    protected boolean m_UseBackslashes;

    /** the header cells to use. */
    protected List<String> m_HeaderCells;
    
    /**
     * Initializes the low-level reader.
     * 
     * @param owner	the owning reader
     */
    public ChunkReader(SqlDumpSpreadSheetReader owner) {
      m_Owner          = owner;
      m_UseBackslashes = owner.getUseBackslashes();
    }
    
    /**
     * Unquotes the given string.
     *
     * @param s		the string to unquote, if necessary
     * @return		the processed string
     */
    protected String unquote(String s) {
      String	result;

      if (m_UseBackslashes)
	result = Utils.unquote(s, "'");
      else
	result = Utils.unDoubleUpQuotes(s, '\'', new String[]{"\\t", "\\n"}, new char[]{'\t', '\n'});

      return result;
    }

    /**
     * Removes a trailing CR.
     * 
     * @param current	the current buffer
     */
    protected void removeTrailingCR(StringBuilder current) {
      if (current.length() > 0) {
        if (current.charAt(current.length() - 1) == '\r')
  	current.delete(current.length() - 1, current.length());
      }
    }
    
    /**
     * Reads a row and breaks it up into cells.
     *
     * @param reader		the reader to read from
     * @return			the cells, null if nothing could be read (EOF)
     * @throws IOException	if reading fails, e.g., due to IO error
     */
    protected List<String> readCells(Reader reader) throws IOException {
      ArrayList<String>	result;
      StringBuilder	current;
      boolean		escaped;
      char		escapeChr;
      char		chr;
      boolean		lineFinished;
      int		in;
      boolean		firstChr;

      result       = new ArrayList<String>();
      current      = new StringBuilder();
      escaped      = false;
      escapeChr    = '\0';
      lineFinished = false;
      firstChr     = true;

      while (!lineFinished && ((in = reader.read()) != -1)) {
	firstChr = false;
	chr      = (char) in;

	if (chr == ',') {
	  if (escaped) {
	    current.append(chr);
	  }
	  else {
	    if ((result.size() == 0) && current.toString().toLowerCase().startsWith("insert"))
	      current.delete(0, current.indexOf("(") + 1);
	    removeTrailingCR(current);
	    result.add(unquote(current.toString()));
	    if (current.length() > 0)
	      current.delete(0, current.length());
	    escapeChr = '\0';
	  }
	}
	else if ((chr == '\'') || (chr == '\"')) {
	  if (chr == escapeChr) {
	    escaped = !escaped;
	  }
	  else if (escapeChr == '\0') {
	    escaped   = true;
	    escapeChr = chr;
	  }
	  current.append(chr);
	}
	else if (chr == '\n') {
	  if (escaped) {
	    removeTrailingCR(current);
	    current.append(chr);
	  }
	  else {
	    lineFinished = true;
	  }
	}
	else {
	  current.append(chr);
	}
      }

      // add last cell
      if (!firstChr) {
        removeTrailingCR(current);
        if (current.lastIndexOf(")") > -1)
          current.delete(current.lastIndexOf(")"), current.length());
        result.add(unquote(current.toString()));
      }
      else {
        result = null;
      }

      return result;
    }

    /**
     * Reads the next chunk.
     * 
     * @return		the next chunk
     */
    public SpreadSheet next() {
      SpreadSheet	result;
      List<String>	cells;
      boolean		comments;
      Row		row;
      int		i;

      if (m_Header == null) {
	result = m_Owner.getSpreadSheetType().newInstance();
	result.setTimeZone(m_Owner.getTimeZone());
	result.setDataRowClass(m_Owner.getDataRowType().getRowClass());
	m_HeaderCells = new ArrayList<String>(Arrays.asList(m_Owner.getCustomColumnHeaders().split(",")));
	row           = result.getHeaderRow();
	for (String cell: m_HeaderCells)
	  row.addCell("" + result.getColumnCount()).setContentAsString(cell);
      }
      else {
	result = m_Header.getHeader();
      }

      try {
	comments = (m_Header == null);
	while (!m_Owner.isStopped()) {
	  cells = readCells(m_Reader);
	  if (cells == null) {
	    m_Reader.close();
	    m_Reader = null;
	    break;
	  }

	  // still in comments section?
	  if (comments) {
	    if ((cells.get(0).trim().length() == 0) || cells.get(0).startsWith("--") || cells.get(0).toLowerCase().startsWith("create")) {
	      if (cells.get(0).startsWith("--"))
		result.addComment(Utils.flatten(cells, ",").substring(2).trim());
	      else
		result.addComment(Utils.flatten(cells, ",").trim());
	      continue;
	    }
	  }

	  // actual data
	  comments = false;
	  if ((cells.size() == 1) && (cells.get(0).trim().length() == 0))
	    continue;
	  row = result.addRow();
	  for (i = 0; (i < m_HeaderCells.size()) && (i < cells.size()); i++) {
	    if (!cells.get(i).equals("NULL"))
	      row.addCell("" + i).setContent(cells.get(i));
	  }

	  // chunk limit reached?
	  if ((m_ChunkSize > 0) && (result.getRowCount() == m_ChunkSize))
	    break;
	}
      }
      catch (Exception e) {
        result = null;
	m_Owner.getLogger().log(Level.SEVERE, "Failed to read data!", e);
	m_Owner.setLastError("Failed to read data!\n" + Utils.throwableToString(e));
      }

      return result;
    }
    
    /**
     * Returns whether there is more data to be read.
     * 
     * @return		true if more data available
     */
    public boolean hasNext() {
      return (m_Reader != null);
    }
    
    /**
     * Reads the spreadsheet content from the specified reader.
     *
     * @param r		the reader to read from
     * @param chunkSize	the number of rows to read at a time
     * @return		the spreadsheet or null in case of an error
     */
    public SpreadSheet read(Reader r, int chunkSize) {
      if (r instanceof BufferedReader)
        m_Reader = (BufferedReader) r;
      else
        m_Reader = new BufferedReader(r);

      m_Header          = null;
      m_HeaderCells     = null;
      m_ChunkSize       = chunkSize;
      
      return next();
    }
  }
  
  /** the comma-separated list of column header names. */
  protected String m_CustomColumnHeaders;

  /** the timezone to use. */
  protected TimeZone m_TimeZone;
  
  /** the chunk size to use. */
  protected int m_ChunkSize;
  
  /** whether to use backslashes for escaping. */
  protected boolean m_UseBackslashes;

  /** for reading the actual data. */
  protected ChunkReader m_Reader;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads in SQL dump files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "custom-column-headers", "customColumnHeaders",
	    "");

    m_OptionManager.add(
	    "time-zone", "timeZone",
	    TimeZone.getDefault(), false);

    m_OptionManager.add(
	    "use-backslashes", "useBackslashes",
	    false);

    m_OptionManager.add(
	    "chunk-size", "chunkSize",
	    -1, -1, null);
  }

  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  @Override
  public String getFormatDescription() {
    return "SQL dump";
  }

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  @Override
  public String[] getFormatExtensions() {
    return new String[]{"sql"};
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
    return "The custom headers to use for the columns (comma-separated list).";
  }

  /**
   * Sets the time zone to use.
   *
   * @param value	the time zone
   */
  public void setTimeZone(TimeZone value) {
    m_TimeZone = value;
    reset();
  }

  /**
   * Returns the time zone in use.
   *
   * @return		the time zone
   */
  public TimeZone getTimeZone() {
    return m_TimeZone;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String timeZoneTipText() {
    return "The time zone to use for interpreting dates/times; default is the system-wide defined one.";
  }

  /**
   * Sets whether to use backslashes for escaping quotes rather than doubling
   * them.
   *
   * @param value	if true then backslashes are used
   */
  public void setUseBackslashes(boolean value) {
    m_UseBackslashes = value;
    reset();
  }

  /**
   * Returns whether to use backslashes for escaping quotes rather than doubling
   * them.
   *
   * @return		true if backslashes are used
   */
  public boolean getUseBackslashes() {
    return m_UseBackslashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useBackslashesTipText() {
    return
        "If enabled, any output file that exists when the writer is executed "
      + "for the first time won't get replaced with the current header; "
      + "useful when outputting data in multiple locations in the flow, but "
      + "one needs to be cautious as to not stored mixed content (eg varying "
      + "number of columns, etc).";
  }

  /**
   * Sets the maximum chunk size.
   * 
   * @param value	the size of the chunks, &lt; 1 denotes infinity
   */
  public void setChunkSize(int value) {
    if (value < 1)
      value = -1;
    m_ChunkSize = value;
    reset();
  }
  
  /**
   * Returns the current chunk size.
   * 
   * @param value	the size of the chunks, &lt; 1 denotes infinity
   */
  public int getChunkSize() {
    return m_ChunkSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String chunkSizeTipText() {
    return "The maximum number of rows per chunk; using -1 will read put all data into a single spreadsheet object.";
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  @Override
  protected InputType getInputType() {
    return InputType.READER;
  }
  
  /**
   * Performs the actual reading.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   */
  @Override
  protected SpreadSheet doRead(Reader r) {
    m_Reader = new ChunkReader(this);
    return m_Reader.read(r, m_ChunkSize);
  }
  
  /**
   * Checks whether there is more data to read.
   * 
   * @return		true if there is more data available
   */
  public boolean hasMoreChunks() {
    return (m_Reader != null) && m_Reader.hasNext();
  }
  
  /**
   * Returns the next chunk.
   * 
   * @return		the next chunk, null if no data available
   */
  public SpreadSheet nextChunk() {
    if ((m_Reader == null) || !m_Reader.hasNext())
      return null;
    else
      return m_Reader.next();
  }
}

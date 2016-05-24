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
 * AbstractMultiSheetSpreadSheetReader.java
 * Copyright (C) 2013-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.Range;
import adams.core.Utils;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.data.spreadsheet.SpreadSheet;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Ancestor for spreadsheet readers that can read multiple sheets from a
 * document in one go.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractMultiSheetSpreadSheetReader
  extends AbstractSpreadSheetReader
  implements MultiSheetSpreadSheetReader {

  /** for serialization. */
  private static final long serialVersionUID = -7995524981221985397L;
  
  /** the range of sheets to load. */
  protected Range m_SheetRange;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "sheets", "sheetRange",
	    new Range(Range.FIRST));
  }

  /**
   * Sets the range of the sheets to load.
   *
   * @param value	the range (1-based)
   */
  @Override
  public void setSheetRange(Range value) {
    m_SheetRange = value;
    reset();
  }

  /**
   * Returns the range of the sheets to load.
   *
   * @return		the range (1-based)
   */
  @Override
  public Range getSheetRange() {
    return m_SheetRange;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   *         		displaying in the explorer/experimenter gui
   */
  @Override
  public String sheetRangeTipText() {
    return "The range of sheets to load.";
  }

  /**
   * Reads the spreadsheet from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the spreadsheet
   */
  @Override
  public List<SpreadSheet> readRange(File file) {
    return readRange(file.getAbsolutePath());
  }

  /**
   * Reads the spreadsheets from the given file.
   *
   * @param filename	the file to read from
   * @return		the spreadsheets or null in case of an error
   */
  @Override
  public List<SpreadSheet> readRange(String filename) {
    List<SpreadSheet>	result;
    BufferedReader	reader;
    InputStream		input;

    check();
    
    m_Stopped   = false;
    m_LastError = null;
    reader      = null;
    input       = null;
    try {
      switch (getInputType()) {
	case FILE:
	  result = doReadRange(new PlaceholderFile(filename));
	  break;
	case STREAM:
	  input = new FileInputStream(filename);
	  result = doReadRange(input);
	  break;
	case READER:
	  reader = new BufferedReader(new FileReader(filename));
	  result = doReadRange(reader);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      m_LastError = "Failed to read range '" + m_SheetRange + "' from '" + filename + "'!\n" + Utils.throwableToString(e);
      getLogger().severe(m_LastError);
    }
    finally {
      FileUtils.closeQuietly(reader);
      FileUtils.closeQuietly(input);
    }
    
    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Reads the spreadsheets from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the spreadsheets or null in case of an error
   */
  @Override
  public List<SpreadSheet> readRange(InputStream stream) {
    List<SpreadSheet>	result;

    check();
    
    m_Stopped   = false;
    m_LastError = null;

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not input streams!");
	case STREAM:
	  result = doReadRange(stream);
	  break;
	case READER:
	  result = doReadRange(new BufferedReader(new InputStreamReader(stream)));
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      m_LastError = "Failed to read range '" + m_SheetRange + "' from stream!\n" + Utils.throwableToString(e);
      getLogger().severe(m_LastError);
    }
    
    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Reads the spreadsheets from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the spreadsheets or null in case of an error
   */
  @Override
  public List<SpreadSheet> readRange(Reader r) {
    List<SpreadSheet>	result;

    check();

    m_Stopped   = false;
    m_LastError = null;

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not input streams!");
	case STREAM:
	  result = doReadRange(new ReaderInputStream(r));
	  break;
	case READER:
	  result = doReadRange(r);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      m_LastError = "Failed to read range '" + m_SheetRange + "' from stream!\n" + Utils.throwableToString(e);
      getLogger().severe(m_LastError);
    }
    
    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param file	the file to read from
   * @return		the spreadsheets or null in case of an error
   * @see		#getInputType()
   */
  protected List<SpreadSheet> doReadRange(File file) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param r		the reader to read from
   * @return		the spreadsheets or null in case of an error
   * @see		#getInputType()
   */
  protected List<SpreadSheet> doReadRange(Reader r) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param in		the input stream to read from
   * @return		the spreadsheets or null in case of an error
   * @see		#getInputType()
   */
  protected List<SpreadSheet> doReadRange(InputStream in) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Reads all defined sheets and returns only the first that it found.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected SpreadSheet doRead(File file) {
    List<SpreadSheet>	result;
    
    result = doReadRange(file);
    if ((result != null) && (result.size() > 0))
      return result.get(0);
    
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Reads all defined sheets and returns only the first that it found.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected SpreadSheet doRead(Reader r) {
    List<SpreadSheet>	result;
    
    result = doReadRange(r);
    if ((result != null) && (result.size() > 0))
      return result.get(0);
    
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Reads all defined sheets and returns only the first that it found.
   *
   * @param in		the input stream to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  @Override
  protected SpreadSheet doRead(InputStream in) {
    List<SpreadSheet>	result;
    
    result = doReadRange(in);
    if ((result != null) && (result.size() > 0))
      return result.get(0);
    
    return null;
  }
}

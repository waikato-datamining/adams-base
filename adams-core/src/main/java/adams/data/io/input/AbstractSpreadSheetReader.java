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
 * AbstractSpreadSheetReader.java
 * Copyright (C) 2010-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.ClassLister;
import adams.core.Utils;
import adams.core.base.BaseCharset;
import adams.core.io.FileEncodingSupporter;
import adams.core.io.FileFormatHandler;
import adams.core.io.FileUtils;
import adams.core.io.PlaceholderFile;
import adams.core.option.AbstractOptionHandler;
import adams.data.spreadsheet.DataRow;
import adams.data.spreadsheet.DefaultSpreadSheet;
import adams.data.spreadsheet.DenseDataRow;
import adams.data.spreadsheet.SpreadSheet;
import adams.data.spreadsheet.SpreadSheetTypeHandler;
import org.apache.commons.io.input.ReaderInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.zip.GZIPInputStream;

/**
 * Ancestor for classes that can read spreadsheets.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSpreadSheetReader
  extends AbstractOptionHandler 
  implements SpreadSheetReader, FileEncodingSupporter, FileFormatHandler,
             SpreadSheetTypeHandler {

  /** for serialization. */
  private static final long serialVersionUID = 4828477005893179066L;

  /**
   * How to read the data.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum InputType {
    /** read from a file. */
    FILE,
    /** read using a reader. */
    READER,
    /** read from a stream. */
    STREAM
  }

  /** the data row type to use. */
  protected DataRow m_DataRowType;

  /** the type of spreadsheet to use. */
  protected SpreadSheet m_SpreadSheetType;

  /** whether the read process was stopped through an external source. */
  protected boolean m_Stopped;
  
  /** the encoding to use. */
  protected BaseCharset m_Encoding;
  
  /** the last error that occurred. */
  protected String m_LastError;
  
  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "data-row-type", "dataRowType",
	    getDefaultDataRowType());

    m_OptionManager.add(
	    "spreadsheet-type", "spreadSheetType",
	    new DefaultSpreadSheet());
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_Encoding        = new BaseCharset();
    m_SpreadSheetType = getDefaultSpreadSheet();
    m_DataRowType     = getDefaultDataRowType();
  }
  
  /**
   * Returns a string describing the format (used in the file chooser).
   *
   * @return 			a description suitable for displaying in the
   * 				file chooser
   */
  public abstract String getFormatDescription();

  /**
   * Returns the extension(s) of the format.
   *
   * @return 			the extension (without the dot!)
   */
  public abstract String[] getFormatExtensions();

  /**
   * Returns the default extension of the format.
   *
   * @return 			the default extension (without the dot!)
   */
  public String getDefaultFormatExtension() {
    return getFormatExtensions()[0];
  }

  /**
   * Returns the default spreadsheet type.
   *
   * @return		the default
   */
  protected SpreadSheet getDefaultSpreadSheet() {
    return new DefaultSpreadSheet();
  }

  /**
   * Returns the default row type.
   * 
   * @return		the default
   */
  protected DataRow getDefaultDataRowType() {
    return new DenseDataRow();
  }
  
  /**
   * Sets the type of data row to use.
   *
   * @param value	the type
   */
  public void setDataRowType(DataRow value) {
    m_DataRowType = value;
    reset();
  }

  /**
   * Returns the type of data row to use.
   *
   * @return		the type
   */
  public DataRow getDataRowType() {
    return m_DataRowType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String dataRowTypeTipText() {
    return "The type of row to use for the data.";
  }

  /**
   * Sets the type of spreadsheet to use.
   *
   * @param value	the type
   */
  public void setSpreadSheetType(SpreadSheet value) {
    m_SpreadSheetType = value;
    reset();
  }

  /**
   * Returns the type of spreadsheet to use.
   *
   * @return		the type
   */
  public SpreadSheet getSpreadSheetType() {
    return m_SpreadSheetType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String spreadSheetTypeTipText() {
    return "The type of spreadsheet to use for the data.";
  }

  /**
   * Sets the encoding to use.
   * 
   * @param value	the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public void setEncoding(BaseCharset value) {
    m_Encoding = value;
    reset();
  }
  
  /**
   * Returns the encoding to use.
   * 
   * @return		the encoding, e.g. "UTF-8" or "UTF-16", empty string for default
   */
  public BaseCharset getEncoding() {
    return m_Encoding;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String encodingTipText() {
    return "The type of encoding to use when reading using a reader, leave empty for default.";
  }

  /**
   * Reads the spreadsheet from the specified file.
   *
   * @param file	the file to read from
   * @return		null in case of an error, otherwise the spreadsheet
   */
  public SpreadSheet read(File file) {
    return read(file.getAbsolutePath());
  }

  /**
   * Returns how to read the data, from a file, stream or reader.
   *
   * @return		how to read the data
   */
  protected abstract InputType getInputType();

  /**
   * Returns whether to automatically handle gzip compressed files
   * ({@link InputType#READER}, {@link InputType#STREAM}).
   * <br><br>
   * Default implementation returns false.
   * 
   * @return		true if to automatically decompress
   */
  protected boolean supportsCompressedInput() {
    return false;
  }
  
  /**
   * Returns whether the file should get decompressed, i.e., 
   * {@link #supportsCompressedInput()} returns true and the filename ends
   * with ".gz".
   * 
   * @param filename	the filename to check
   * @return		true if decompression should occur
   */
  protected boolean canDecompress(String filename) {
    return supportsCompressedInput() && filename.toLowerCase().endsWith(".gz");
  }
  
  /**
   * Hook method to perform some checks before performing the actual read.
   * <br><br>
   * Default implementation does nothing.
   */
  protected void check() {
  }

  /**
   * Reads the spreadsheet from the given file.
   * For input types {@link InputType#STREAM} and {@link InputType#READER},
   * decompression (ie gzip) is automatically handled if the filename
   * ends in ".gz".
   *
   * @param filename	the file to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#supportsCompressedInput()
   */
  public SpreadSheet read(String filename) {
    SpreadSheet		result;
    BufferedReader	reader;
    InputStream		input;

    check();
    
    m_LastError = null;
    m_Stopped   = false;
    reader      = null;
    input       = null;
    try {
      switch (getInputType()) {
	case FILE:
	  result = doRead(new PlaceholderFile(filename));
	  break;
	case STREAM:
	  input = new FileInputStream(filename);
	  if (canDecompress(filename))
	    input = new GZIPInputStream(input);
	  result = doRead(input);
	  break;
	case READER:
	  input = new FileInputStream(filename);
	  if (canDecompress(filename))
	    input = new GZIPInputStream(input);
	  if (m_Encoding != null)
	    reader = new BufferedReader(new InputStreamReader(input, getEncoding().charsetValue()));
	  else
	    reader = new BufferedReader(new InputStreamReader(input));
	  result = doRead(reader);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Throwable e) {
      result = null;
      m_LastError = "Failed to read '" + filename + "'!\n" + Utils.throwableToString(e);
      getLogger().severe(m_LastError);
    }
    finally {
      if (!(this instanceof ChunkedSpreadSheetReader)) {
        FileUtils.closeQuietly(reader);
        FileUtils.closeQuietly(input);
      }
    }
    
    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Reads the spreadsheet from the stream. The caller must ensure to
   * close the stream.
   *
   * @param stream	the stream to read from
   * @return		the spreadsheet or null in case of an error
   */
  public SpreadSheet read(InputStream stream) {
    SpreadSheet		result;

    check();
    
    m_Stopped   = false;
    m_LastError = null;

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not input streams!");
	case STREAM:
	  result = doRead(stream);
	  break;
	case READER:
	  result = doRead(new BufferedReader(new InputStreamReader(stream)));
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      m_LastError = "Failed to read from stream!\n" + Utils.throwableToString(e);
      getLogger().severe(m_LastError);
    }
    
    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Reads the spreadsheet from the given reader. The caller must ensure to
   * close the reader.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   */
  public SpreadSheet read(Reader r) {
    SpreadSheet		result;

    check();

    m_Stopped   = false;
    m_LastError = null;

    try {
      switch (getInputType()) {
	case FILE:
	  throw new IllegalStateException("Only supports reading from files, not input streams!");
	case STREAM:
	  result = doRead(new ReaderInputStream(r));
	  break;
	case READER:
	  result = doRead(r);
	  break;
	default:
	  throw new IllegalStateException("Unhandled input type: " + getInputType());
      }
    }
    catch (Exception e) {
      result = null;
      m_LastError = "Failed to read from reader!\n" + Utils.throwableToString(e);
      getLogger().severe(m_LastError);
    }
    
    if (m_Stopped)
      result = null;

    return result;
  }

  /**
   * Performs the actual reading. Must handle compression itself, if 
   * {@link #supportsCompressedInput()} returns true.
   * <br><br>
   * Default implementation returns null.
   *
   * @param file	the file to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   * @see		#supportsCompressedInput()
   */
  protected SpreadSheet doRead(File file) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param r		the reader to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  protected SpreadSheet doRead(Reader r) {
    return null;
  }

  /**
   * Performs the actual reading.
   * <br><br>
   * Default implementation returns null.
   *
   * @param in		the input stream to read from
   * @return		the spreadsheet or null in case of an error
   * @see		#getInputType()
   */
  protected SpreadSheet doRead(InputStream in) {
    return null;
  }

  /**
   * Stops the reading (might not be immediate, depending on reader).
   */
  public void stopExecution() {
    m_Stopped = true;
  }
  
  /**
   * Returns whether the reading was stopped.
   * 
   * @return		true if stopped
   */
  public boolean isStopped() {
    return m_Stopped;
  }

  /**
   * Returns whether an error was encountered during the last read.
   * 
   * @return		true if an error occurred
   */
  public boolean hasLastError() {
    return (m_LastError != null);
  }
  
  /**
   * Sets the value for the last error that occurred during read.
   * 
   * @param value	the error string, null if none occurred
   */
  protected void setLastError(String value) {
    m_LastError = value;
  }
  
  /**
   * Returns the error that occurred during the last read.
   * 
   * @return		the error string, null if none occurred
   */
  public String getLastError() {
    return m_LastError;
  }
  
  /**
   * Returns a list with classnames of readers.
   *
   * @return		the reader classnames
   */
  public static String[] getReaders() {
    return ClassLister.getSingleton().getClassnames(SpreadSheetReader.class);
  }
}

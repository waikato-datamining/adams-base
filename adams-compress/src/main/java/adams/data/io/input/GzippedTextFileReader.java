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
 * GzippedTextFileReader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.compress.compressors.CompressorInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseCharset;

/**
 <!-- globalinfo-start -->
 * Reads content from gzipped text files.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-text-reader &lt;adams.data.io.input.AbstractTextReader&gt; (property: textReader)
 * &nbsp;&nbsp;&nbsp;The reader to use for reading the decompressed content.
 * &nbsp;&nbsp;&nbsp;default: adams.data.io.input.LineArrayTextReader
 * </pre>
 * 
 * <pre>-encoding &lt;adams.core.base.BaseCharset&gt; (property: encoding)
 * &nbsp;&nbsp;&nbsp;The type of encoding to use when reading the file, use empty string for 
 * &nbsp;&nbsp;&nbsp;default.
 * &nbsp;&nbsp;&nbsp;default: Default
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GzippedTextFileReader
  extends AbstractTextReader {

  /** for serialization. */
  private static final long serialVersionUID = 35626483638973054L;
  
  /** the reader for reading the decompressed content. */
  protected AbstractTextReader m_TextReader;
  
  /** the encoding to use. */
  protected BaseCharset m_Encoding;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Reads content from gzipped text files.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "text-reader", "textReader",
	    new LineArrayTextReader());

    m_OptionManager.add(
	    "encoding", "encoding",
	    new BaseCharset());
  }

  /**
   * Sets the reader.
   *
   * @param value	the reader
   */
  public void setTextReader(AbstractTextReader value) {
    m_TextReader = value;
    reset();
  }

  /**
   * Returns the reader.

   * @return		the reader
   */
  public AbstractTextReader getTextReader() {
    return m_TextReader;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String textReaderTipText() {
    return "The reader to use for reading the decompressed content.";
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
    return "The type of encoding to use when reading the file, use empty string for default.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "reader", m_Reader);
  }
  
  /**
   * Returns the class of the data that it returns.
   * 
   * @return		the generated data type
   */
  @Override
  public Class generates() {
    return m_TextReader.generates();
  }

  /**
   * Whether to use a {@link Reader} or an {@link InputStream}.
   * 
   * @return		true if using reader
   */
  @Override
  public boolean useReader() {
    return false;
  }
  
  /**
   * Initializes the input stream to read the content from.
   * 
   * @param stream	the input stream to use
   */
  @Override
  public void initialize(InputStream stream) {
    CompressorInputStream	gis;
    
    try {
      gis = new CompressorStreamFactory().createCompressorInputStream(CompressorStreamFactory.GZIP, stream);
      super.initialize(gis);
      m_TextReader.initialize(new BufferedReader(new InputStreamReader(gis, m_Encoding.charsetValue())));
    }
    catch (Exception e) {
      throw new IllegalStateException("Failed to initialize gzip stream!", e);
    }
  }
  
  /**
   * Returns whether more data is available.
   * 
   * @return		true if more data is available 
   */
  @Override
  public boolean hasNext() {
    return m_TextReader.hasNext();
  }
  
  /**
   * Returns the next lot of data.
   * 
   * @return		the next amount of data, null if failed to read
   */
  @Override
  protected Object doNext() {
    return m_TextReader.doNext();
  }
}

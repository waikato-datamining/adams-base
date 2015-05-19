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
 * AbstractCompressedTextReader.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.QuickInfoHelper;

/**
 * Ancestor for text readers of compressed files.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractCompressedTextReader
  extends AbstractTextReader {
  
  /** for serialization. */
  private static final long serialVersionUID = -7256819627812267170L;
  
  /** the reader for reading the decompressed content. */
  protected AbstractTextReader m_TextReader;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "text-reader", "textReader",
	    getDefaultTextReader());
  }
  
  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_TextReader = getDefaultTextReader();
  }

  /**
   * Returns the default text reader to use.
   * 
   * @return		the default
   */
  protected AbstractTextReader getDefaultTextReader() {
    return new LineArrayTextReader();
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
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    
    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "reader", m_TextReader, ", reader: ");
    
    return result;
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

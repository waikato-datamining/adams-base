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
 * AbstractTextReader.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.io.input;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

import java.io.InputStream;

/**
 * Ancestor for readers for text streams.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of data to return
 */
public abstract class AbstractTextReader<T>
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  /** for serialization. */
  private static final long serialVersionUID = 1002106529556316198L;

  /** the stream in use. */
  protected InputStream m_Stream;

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   * <br><br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Returns the class of the data that it returns.
   * 
   * @return		the generated data type
   */
  public abstract Class generates();

  /**
   * Initializes the input stream to read the content from.
   * 
   * @param stream	the input stream to use
   */
  public void initialize(InputStream stream) {
    m_Stream = stream;
  }
  
  /**
   * Resets the reader.
   */
  @Override
  public void reset() {
    m_Stream = null;
  }
  
  /**
   * Returns whether more data is available.
   * 
   * @return		true if more data is available 
   */
  public boolean hasNext() {
    return (m_Stream != null);
  }
  
  /**
   * Returns the next lot of data.
   * 
   * @return		the next amount of data, null if failed to read
   */
  protected abstract T doNext();
  
  /**
   * Returns the next lot of data.
   * 
   * @return		the next amount of data, null if failed to read
   */
  public T next() {
    if (m_Stream == null)
      return null;
    else
      return doNext();
  }
}

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
 * SafeArffLoader.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package weka.core.converters;

import java.io.IOException;
import java.io.Reader;

import weka.core.Instances;

/**
 * Safe version of the {@link ArffLoader}, always retaining string values.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AArffLoader
  extends ArffLoader {

  /** for serialization. */
  private static final long serialVersionUID = 8739929496992782311L;

  public static class AArffReader
    extends ArffReader {

    /**
     * Reads the data completely from the reader. The data can be accessed via
     * the <code>getData()</code> method.
     * 
     * @param reader the reader to use
     * @throws IOException if something goes wrong
     * @see #getData()
     */
    public AArffReader(Reader reader) throws IOException {
      super(reader);
      setRetainStringValues(true);
    }

    public AArffReader(Reader reader, int capacity) throws IOException {
      super(reader, capacity);
      setRetainStringValues(true);
    }

    /**
     * Reads only the header and reserves the specified space for instances.
     * Further instances can be read via <code>readInstance()</code>.
     * 
     * @param reader the reader to use
     * @param capacity the capacity of the new dataset
     * @throws IOException if something goes wrong
     * @throws IllegalArgumentException if capacity is negative
     * @see #getStructure()
     * @see #readInstance(Instances)
     */
    public AArffReader(Reader reader, int capacity, boolean batch) throws IOException {
      super(reader, capacity, batch);
      setRetainStringValues(true);
    }

    /**
     * Reads the data without header according to the specified template. The
     * data can be accessed via the <code>getData()</code> method.
     * 
     * @param reader the reader to use
     * @param template the template header
     * @param lines the lines read so far
     * @throws IOException if something goes wrong
     * @see #getData()
     */
    public AArffReader(Reader reader, Instances template, int lines) throws IOException {
      super(reader, template, lines);
      setRetainStringValues(true);
    }

    /**
     * Initializes the reader without reading the header according to the
     * specified template. The data must be read via the
     * <code>readInstance()</code> method.
     * 
     * @param reader the reader to use
     * @param template the template header
     * @param lines the lines read so far
     * @param capacity the capacity of the new dataset
     * @throws IOException if something goes wrong
     * @see #getData()
     */
    public AArffReader(Reader reader, Instances template, int lines, int capacity) throws IOException {
      super(reader, template, lines, capacity);
      setRetainStringValues(true);
    }

    /**
     * Initializes the reader without reading the header according to the
     * specified template. The data must be read via the
     * <code>readInstance()</code> method.
     * 
     * @param reader the reader to use
     * @param template the template header
     * @param lines the lines read so far
     * @param capacity the capacity of the new dataset
     * @param batch true if the values of string attributes should be collected
     *          in the header
     * @throws IOException if something goes wrong
     * @see #getData()
     */
    public AArffReader(Reader reader, Instances template, int lines, int capacity, boolean batch) throws IOException {
      super(reader, template, lines, capacity, batch);
      setRetainStringValues(true);
    }

    /**
     * Sets whether to retain string values (safe) or not.
     * 
     * @param value	true if to retain string values
     */
    public void setRetainStringValues(boolean value) {
      m_retainStringValues = value;
    }
    
    /**
     * Returns whether to retain string values.
     * 
     * @return		true if to retain string values
     */
    public boolean getRetainStringValues() {
      return m_retainStringValues;
    }
  }

  /**
   * Determines and returns (if possible) the structure (internally the header)
   * of the data set as an empty set of instances.
   * 
   * @return the structure of the data set as an empty set of Instances
   * @throws IOException if an error occurs
   */
  @Override
  public Instances getStructure() throws IOException {
    if (m_structure == null) {
      if (m_sourceReader == null) {
        throw new IOException("No source has been specified");
      }

      try {
        m_ArffReader =
          new AArffReader(m_sourceReader, 1, (getRetrieval() == BATCH));
        m_structure = m_ArffReader.getStructure();
      } catch (Exception ex) {
        throw new IOException("Unable to determine structure as arff (Reason: "
          + ex.toString() + ").");
      }
    }

    return new Instances(m_structure, 0);
  }
}

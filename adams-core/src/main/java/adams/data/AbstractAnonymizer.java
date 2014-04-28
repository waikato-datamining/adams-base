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
 * AbstractAnonymizer.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Random;

/**
 * Allows anonymizing of values.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractAnonymizer<T extends Object>
  implements Serializable {
  
  /** for serialization. */
  private static final long serialVersionUID = -7508140332366459841L;

  /** the ID of the anonymizer (used for hashcode). */
  protected String m_ID;
  
  /** the seed value. */
  protected long m_Seed;
  
  /** for shuffling the IDs. */
  protected Random m_Random;
  
  /** the buffer size. */
  protected int m_BufferSize;
  
  /** the buffer of unused random IDs. */
  protected ArrayList<Integer> m_Buffer;
  
  /** the current mapping (actual value &lt;-&gt; random). */
  protected Hashtable<T,Integer> m_Mapping;

  /**
   * Default constructor. Uses a random ID and buffer size 100.
   */
  protected AbstractAnonymizer() {
    this("" + Math.random(), 100);
  }
  
  /**
   * Initializes the anonymizer with a random seed value.
   * 
   * @param id		the ID of the anonymizer
   * @param bufferSize	the size of the buffer for unused IDs
   */
  protected AbstractAnonymizer(String id, int bufferSize) {
    this(id, (long) (Math.random() * 10000), bufferSize);
  }

  /**
   * Initializes the anonymizer.
   * 
   * @param id		the ID of the anonymizer
   * @param seed	the seed value for the random number generator
   * @param bufferSize	the size of the buffer for unused IDs
   */
  protected AbstractAnonymizer(String id, long seed, int bufferSize) {
    super();
    
    if (id == null)
      throw new IllegalArgumentException("ID cannot be null!");
    if (bufferSize < 1)
      throw new IllegalArgumentException("Buffer size must >0, provided: " + bufferSize);
    
    m_ID         = id;
    m_Seed       = seed;
    m_Random     = new Random(seed);
    m_BufferSize = bufferSize;
    m_Mapping    = new Hashtable<T,Integer>();
    m_Buffer     = new ArrayList<Integer>();
  }
  
  /**
   * Returns the ID of this anonymizer.
   * 
   * @return		the ID
   */
  public String getID() {
    return m_ID;
  }
  
  /**
   * Returns the seed of this anonymizer.
   * 
   * @return		the seed
   */
  public long getSeed() {
    return m_Seed;
  }
  
  /**
   * Returns the buffer size of this anonymizer.
   * 
   * @return		the buffer size
   */
  public int getBufferSize() {
    return m_BufferSize;
  }
  
  /**
   * Turns the anonymous integer ID into the appropriate data type.
   * 
   * @param id		the ID to convert
   * @return		the final result
   */
  protected abstract T toAnonymized(Integer id);
  
  /**
   * Fills up the buffer with random values again.
   */
  protected void fillBuffer() {
    int		i;
    
    for (i = 0; i < m_BufferSize; i++)
      m_Buffer.add(new Integer(m_Mapping.size() + i));
    Collections.shuffle(m_Buffer, m_Random);
  }
  
  /**
   * Returns an anonymized value.
   * 
   * @param value	the value to anonymize
   * @return		the anonymous value
   */
  public synchronized T anonymize(T value) {
    Integer	next;
    
    if (value == null)
      return null;
    
    if (m_Mapping.containsKey(value))
      return toAnonymized(m_Mapping.get(value));

    if (m_Buffer.size() == 0)
      fillBuffer();
    
    next = m_Buffer.get(0);
    m_Buffer.remove(0);
    m_Mapping.put(value, next);
    
    return toAnonymized(next);
  }
  
  /**
   * Returns the hashcode for this anonymizer (based on its ID).
   * 
   * @return		the hashcode
   */
  @Override
  public int hashCode() {
    return m_ID.hashCode();
  }
  
  /**
   * Returns a short description of the anonymizer.
   * 
   * @return		the description
   */
  @Override
  public String toString() {
    return "id=" + m_ID + ", seed=" + m_Seed + ", bufferSize=" + m_BufferSize + ", #mappings=" + m_Mapping.size();
  }
}

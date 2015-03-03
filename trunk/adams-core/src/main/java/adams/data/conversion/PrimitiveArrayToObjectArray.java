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
 * PrimitiveArrayToObjectArray.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Utils;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Converts a primitive array to its object counterpart.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PrimitiveArrayToObjectArray
  extends AbstractConversion {

  /** for serialization. */
  private static final long serialVersionUID = 7012073882235453335L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Converts a primitive array to its object counterpart.";
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return Unknown.class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    return Unknown.class;
  }

  /**
   * Converts byte arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected Byte[] convert(byte[] array) {
    Byte[]	result;
    int		i;
    
    result = new Byte[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts short arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected Short[] convert(short[] array) {
    Short[]	result;
    int		i;
    
    result = new Short[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts int arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected Integer[] convert(int[] array) {
    Integer[]	result;
    int		i;
    
    result = new Integer[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts long arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected Long[] convert(long[] array) {
    Long[]	result;
    int		i;
    
    result = new Long[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts float arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected Float[] convert(float[] array) {
    Float[]	result;
    int		i;
    
    result = new Float[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts double arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected Double[] convert(double[] array) {
    Double[]	result;
    int		i;
    
    result = new Double[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts char arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected Character[] convert(char[] array) {
    Character[]	result;
    int		i;
    
    result = new Character[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }
  
  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    // common ones first
    if (m_Input instanceof byte[])
      return convert((byte[]) m_Input);
    else if (m_Input instanceof int[])
      return convert((int[]) m_Input);
    else if (m_Input instanceof long[])
      return convert((long[]) m_Input);
    else if (m_Input instanceof double[])
      return convert((double[]) m_Input);
    // less common ones last
    else if (m_Input instanceof float[])
      return convert((float[]) m_Input);
    else if (m_Input instanceof short[])
      return convert((short[]) m_Input);
    else if (m_Input instanceof char[])
      return convert((char[]) m_Input);

    getLogger().warning("Failed to convert array: " + Utils.classToString(m_Input.getClass()));
    return m_Input;
  }
}

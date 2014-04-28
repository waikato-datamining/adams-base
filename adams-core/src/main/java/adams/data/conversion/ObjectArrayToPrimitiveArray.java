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
 * ObjectArrayToPrimitiveArray.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

import adams.core.Utils;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Converts an object array to its primitive counterpart.
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
public class ObjectArrayToPrimitiveArray
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
    return "Converts an object array to its primitive counterpart.";
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
   * Converts {@link Byte} arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected byte[] convert(Byte[] array) {
    byte[]	result;
    int		i;
    
    result = new byte[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts {@link Short} arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected short[] convert(Short[] array) {
    short[]	result;
    int		i;
    
    result = new short[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts {@link Integer} arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected int[] convert(Integer[] array) {
    int[]	result;
    int		i;
    
    result = new int[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts {@link Long} arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected long[] convert(Long[] array) {
    long[]	result;
    int		i;
    
    result = new long[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts {@link Float} arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected float[] convert(Float[] array) {
    float[]	result;
    int		i;
    
    result = new float[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts {@link Double} arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected double[] convert(Double[] array) {
    double[]	result;
    int		i;
    
    result = new double[array.length];
    for (i = 0; i < array.length; i++)
      result[i] = array[i];
    
    return result;
  }

  /**
   * Converts {@link Character} arrays.
   * 
   * @param array	the array to convert
   * @return		the converted array
   */
  protected char[] convert(Character[] array) {
    char[]	result;
    int		i;
    
    result = new char[array.length];
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
    if (m_Input instanceof Byte[])
      return convert((Byte[]) m_Input);
    else if (m_Input instanceof Integer[])
      return convert((Integer[]) m_Input);
    else if (m_Input instanceof Long[])
      return convert((Long[]) m_Input);
    else if (m_Input instanceof Double[])
      return convert((Double[]) m_Input);
    // less common ones last
    else if (m_Input instanceof Float[])
      return convert((Float[]) m_Input);
    else if (m_Input instanceof Short[])
      return convert((Short[]) m_Input);
    else if (m_Input instanceof Character[])
      return convert((Character[]) m_Input);

    getLogger().warning("Failed to convert array: " + Utils.classToString(m_Input.getClass()));
    return m_Input;
  }
}

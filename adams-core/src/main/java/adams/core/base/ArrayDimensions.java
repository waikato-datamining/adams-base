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
 * ArrayDimensions.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.base;

import java.util.ArrayList;

import adams.core.Variables;

/**
 * For defining dimensions for an array: eg [2] or [2][4][6] or [@{variable}].
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ArrayDimensions
  extends AbstractBaseString {

  /** for serialization. */
  private static final long serialVersionUID = 7100089984419357692L;
  
  /** the parsed dimensions. */
  protected int[] m_Dimensions;
  
  /**
   * Initializes the string with length 0.
   */
  public ArrayDimensions() {
    this("[2]");
  }

  /**
   * Initializes the object with the string to parse.
   *
   * @param s		the string to parse
   */
  public ArrayDimensions(String s) {
    super(s);
  }

  /**
   * Converts the string according to the specified conversion.
   * <p/>
   * Simply unsets the {@link #m_Dimensions} member.
   *
   * @param value	the string to convert
   * @return		the converted string
   */
  @Override
  protected String convert(String value) {
    String	result;
    
    result       = super.convert(value);
    m_Dimensions = null;
    
    return result;
  }

  /**
   * Parses the dimension string.
   * 
   * @param s		the string to parse
   * @return		the dimensions or null if failed to parse
   */
  protected int[] parse(String s) {
    int[]		result;
    ArrayList<String> 	parts;
    String		part;
    int			i;

    parts = new ArrayList<String>();
    while (s.length() > 0) {
      if ((s.indexOf('[') == -1) || ((s.indexOf(']') == -1)))
	return null;
      part = s.substring(0, s.indexOf(']') + 1);
      if (!part.startsWith("[") || !part.endsWith("]"))
	return null;
      if (part.length() <= 2)
	return null;
      parts.add(part.substring(1, part.length() - 1));
      s = s.substring(s.indexOf(']') + 1);
    }
    
    result = new int[parts.size()];
    for (i = 0; i < parts.size(); i++) {
      try {
	part = parts.get(i);
	// variable? use dummy value
	if (part.startsWith(Variables.START) && part.endsWith(Variables.END))
	  part = "1";
	result[i] = Integer.parseInt(part);
      }
      catch (Exception e) {
	return null;
      }
    }
    
    return result;
  }
  
  /**
   * Checks whether the string value is a valid presentation for this class.
   *
   * @param value	the string value to check
   * @return		true if parseable
   */
  @Override
  public boolean isValid(String value) {
    return (value != null) && (parse(value) != null);
  }

  /**
   * Returns the dimensions.
   *
   * @return		the dimensions
   */
  public int[] dimensionsValue() {
    if (m_Dimensions == null)
      m_Dimensions = parse(getValue());
    return m_Dimensions;
  }

  /**
   * Returns a tool tip for the GUI editor (ignored if null is returned).
   *
   * @return		the tool tip
   */
  @Override
  public String getTipText() {
    return "Array dimensions";
  }
}

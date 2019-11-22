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

/*
 * ByteArrayToPrimitiveArray.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.QuickInfoHelper;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 <!-- globalinfo-start -->
 * Turns a byte array (IEE754) into a primitive array.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-num-bytes &lt;int&gt; (property: numBytes)
 * &nbsp;&nbsp;&nbsp;The number of bytes to convert into a primitive.
 * &nbsp;&nbsp;&nbsp;default: 4
 * &nbsp;&nbsp;&nbsp;minimum: 1
 * </pre>
 *
 * <pre>-byte-order &lt;java.nio.ByteOrder&gt; (property: byteOrder)
 * &nbsp;&nbsp;&nbsp;The byte order to use for the conversion.
 * &nbsp;&nbsp;&nbsp;default: java.nio.ByteOrder
 * </pre>
 *
 * <pre>-type &lt;CHAR|SHORT|INT|LONG|FLOAT|DOUBLE&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The primitive type to generate.
 * &nbsp;&nbsp;&nbsp;default: FLOAT
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ByteArrayToPrimitiveArray
  extends AbstractConversion {

  private static final long serialVersionUID = -4513056216008959668L;

  /**
   * The type of primitive array to generate.
   */
  public enum PrimitiveType {
    CHAR,
    SHORT,
    INT,
    LONG,
    FLOAT,
    DOUBLE
  }

  /**
   * The byte order type.
   */
  public enum ByteOrderType {
    LITTLE_ENDIAN,
    BIG_ENDIAN,
  }

  /** the number of bytes to convert. */
  protected int m_NumBytes;

  /** the byte order. */
  protected ByteOrderType m_ByteOrder;

  /** the primitive to output. */
  protected PrimitiveType m_Type;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns a byte array (IEE754) into a primitive array.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-bytes", "numBytes",
      4, 1, null);

    m_OptionManager.add(
      "byte-order", "byteOrder",
      ByteOrderType.LITTLE_ENDIAN);

    m_OptionManager.add(
      "type", "type",
      PrimitiveType.FLOAT);
  }

  /**
   * Sets the number of bytes to convert into a primitive.
   *
   * @param value	the number
   */
  public void setNumBytes(int value) {
    if (getOptionManager().isValid("numBytes", value)) {
      m_NumBytes = value;
      reset();
    }
  }

  /**
   * Returns the number of bytes to convert into a primitive.
   *
   * @return		the number
   */
  public int getNumBytes() {
    return m_NumBytes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBytesTipText() {
    return "The number of bytes to convert into a primitive.";
  }

  /**
   * Sets the byte order to use for the conversion.
   *
   * @param value	the order
   */
  public void setByteOrder(ByteOrderType value) {
    m_ByteOrder = value;
    reset();
  }

  /**
   * Returns the byte order to use for the conversion.
   *
   * @return		the order
   */
  public ByteOrderType getByteOrder() {
    return m_ByteOrder;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String byteOrderTipText() {
    return "The byte order to use for the conversion.";
  }

  /**
   * Sets the type of primitive to generate.
   *
   * @param value	the type
   */
  public void setType(PrimitiveType value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type of primitive to generate.
   *
   * @return		the type
   */
  public PrimitiveType getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The primitive type to generate.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "numBytes", m_NumBytes, "#bytes: ");
    result += QuickInfoHelper.toString(this, "byteOrder", m_ByteOrder, ", order: ");
    result += QuickInfoHelper.toString(this, "type", m_Type, ", type: ");

    return result;
  }

  /**
   * Returns the class that is accepted as input.
   *
   * @return		the class
   */
  @Override
  public Class accepts() {
    return byte[].class;
  }

  /**
   * Returns the class that is generated as output.
   *
   * @return		the class
   */
  @Override
  public Class generates() {
    switch (m_Type) {
      case CHAR:
        return char[].class;
      case SHORT:
        return short[].class;
      case INT:
        return int[].class;
      case LONG:
        return long[].class;
      case FLOAT:
        return float[].class;
      case DOUBLE:
        return double[].class;
      default:
        throw new IllegalStateException("Unhandled primitive type: " + m_Type);
    }
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    byte[]	input;
    int		len;
    int		i;
    Object	result;
    ByteOrder	byteOrder;

    input = (byte[]) m_Input;
    len   = input.length / m_NumBytes;
    switch (m_Type) {
      case CHAR:
        result = Array.newInstance(Character.TYPE, len);
        break;
      case SHORT:
        result = Array.newInstance(Short.TYPE, len);
        break;
      case INT:
        result = Array.newInstance(Integer.TYPE, len);
        break;
      case LONG:
        result = Array.newInstance(Long.TYPE, len);
        break;
      case FLOAT:
        result = Array.newInstance(Float.TYPE, len);
        break;
      case DOUBLE:
        result = Array.newInstance(Double.TYPE, len);
        break;
      default:
        throw new IllegalStateException("Unhandled primitive type: " + m_Type);
    }

    switch (m_ByteOrder) {
      case LITTLE_ENDIAN:
        byteOrder = ByteOrder.LITTLE_ENDIAN;
        break;
      case BIG_ENDIAN:
        byteOrder = ByteOrder.BIG_ENDIAN;
        break;
      default:
        throw new IllegalStateException("Unhandled byte order type: " + m_ByteOrder);
    }

    for (i = 0; i < len; i++) {
      switch (m_Type) {
	case CHAR:
	  Array.set(result, i, ByteBuffer.wrap(input, i * m_NumBytes, m_NumBytes).order(byteOrder).getChar());
	  break;
	case SHORT:
	  Array.set(result, i, ByteBuffer.wrap(input, i * m_NumBytes, m_NumBytes).order(byteOrder).getShort());
	  break;
	case INT:
	  Array.set(result, i, ByteBuffer.wrap(input, i * m_NumBytes, m_NumBytes).order(byteOrder).getInt());
	  break;
	case LONG:
	  Array.set(result, i, ByteBuffer.wrap(input, i * m_NumBytes, m_NumBytes).order(byteOrder).getLong());
	  break;
	case FLOAT:
	  Array.set(result, i, ByteBuffer.wrap(input, i * m_NumBytes, m_NumBytes).order(byteOrder).getFloat());
	  break;
	case DOUBLE:
	  Array.set(result, i, ByteBuffer.wrap(input, i * m_NumBytes, m_NumBytes).order(byteOrder).getDouble());
	  break;
	default:
	  throw new IllegalStateException("Unhandled primitive type: " + m_Type);
      }
    }

    return result;
  }
}

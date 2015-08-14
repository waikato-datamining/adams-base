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
 * AbstractGeneticIntegerDiscoveryHandler.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.core.discovery;

/**
 * Ancestor for genetic discovery handlers that handle integer array properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticIntegerArrayDiscoveryHandler
  extends AbstractGeneticDiscoveryHandler {

  private static final long serialVersionUID = 765007046767066355L;
  /** the minimum. */
  protected int m_Minimum;

  /** the maximum. */
  protected int m_Maximum;

  /** size of the array */
  protected int m_Size;

  /** numbits for each integer. */
  protected int m_numBits;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "minimum", "minimum",
      getDefaultMinimum());

    m_OptionManager.add(
      "maximum", "maximum",
      getDefaultMaximum());

    m_OptionManager.add(
      "size", "size",
      getDefaultSize());
  }

  /**
   * Returns the default size.
   *
   * @return		the default
   */
  protected abstract int getDefaultSize();

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  protected abstract int getDefaultMinimum();

  /**
   * Sets the size of array.
   *
   * @param value	the size
   */
  public void setSize(int value) {
    if (getOptionManager().isValid("size", value)) {
      m_Size = value;
      reset();
    }
  }

  /**
   * Returns the size.
   *
   * @return		the size
   */
  public int getSize() {
    return m_Size;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sizeTipText() {
    return "The size to use.";
  }

  /**
   * Sets the minimum.
   *
   * @param value	the minimum
   */
  public void setMinimum(int value) {
    if (getOptionManager().isValid("minimum", value)) {
      m_Minimum = value;
      calcNumBits();
      reset();
    }
  }

  /**
   * Returns the minimum.
   *
   * @return		the minimum
   */
  public int getMinimum() {
    return m_Minimum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minimumTipText() {
    return "The minimum to use.";
  }


  /**
   * Returns the default maximum.
   *
   * @return		the default
   */
  protected abstract int getDefaultMaximum();

  /**
   * Sets the maximum.
   *
   * @param value	the maximum
   */
  public void setMaximum(int value) {
    if (getOptionManager().isValid("maximum", value)) {
      m_Maximum = value;
      calcNumBits();
      reset();
    }
  }

  /**
   * Returns the maximum.
   *
   * @return		the maximum
   */
  public int getMaximum() {
    return m_Maximum;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maximumTipText() {
    return "The maximum to use.";
  }

  /**
   * Calculates the number of bits that are required.
   *
   * @return		the number of bits
   */
  protected int calcNumBits(){
    return calcNumBits(getMinimum(), getMaximum());
  }

  /**
   * Calculates the number of bits that are required.
   *
   * @param min		the minimum
   * @param max		the maximum
   * @return		the number of bits
   */
  protected static int calcNumBits(int min, int max){
    int range = max - min;
    return (int) (Math.floor(log2(range)) + 1);
  }

  public static int[] bitsToIntArray(String bits, int min, int max, int size){
    int ret[] = new int[size];
    int numbits = calcNumBits(min,max);
    for (int k = 0; k < size; k++) {
      int start = numbits * k;
      double j = 0;
      for (int i = start; i < start + numbits; i++) {
        if (bits.charAt(i) == '1') {
          j = j + Math.pow(2, start+numbits - i - 1);
        }
      }
      j += min;
      ret[k] = (Math.min((int)j,max));
    }
    return ret;
  }

  public int[] bitsToIntArray(String bits){
    int ret[] = new int[getSize()];
    for (int k = 0; k < getSize(); k++) {
      int start = m_numBits * k;
      double j = 0;
      for (int i = start; i < start + m_numBits; i++) {
        if (bits.charAt(i) == '1') {
          j = j + Math.pow(2, start + m_numBits - i - 1);
        }
      }
      j += getMinimum();
      ret[k] = (Math.min((int)j, getMaximum()));
    }
    return(ret);
  }

  public String intArrayToBits(int[] ina){
    StringBuilder buff = new StringBuilder();
    for (int i = 0; i < getSize(); i++) {
      int in = ina[i];
      in = in - getMinimum();
      in = Math.min(in, getMaximum() - getMinimum());
      String bits = Integer.toBinaryString(in);
      while (bits.length() < m_numBits) {
        bits = "0" + bits;
      }
      buff.append(bits);
    }
    return buff.toString();
  }

  public static String intArrayToBits(int[] ina,int min, int max, int size){
    StringBuilder buff = new StringBuilder();
    for (int i = 0; i < size; i++) {
      int in=ina[i];
      in = in - min;
      in = Math.min(in, max - min);
      String bits = Integer.toBinaryString(in);
      while (bits.length() < calcNumBits(min, max)) {
        bits = "0" + bits;
      }
      buff.append(bits);
    }
    return(buff.toString());
  }

  public static double log2(int n) {
    return (Math.log(n) / Math.log(2));
  }

  /**
   * Only for testing.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    int[] i = {1,2,4,5,129};
    String s = intArrayToBits(i, 1, 128, i.length);
    System.err.println(s);
    i = bitsToIntArray(s,1,128,i.length);
    for (int k = 0; k < i.length; k++){
      System.err.print(i[k] + " ");
    }
    System.err.println();
  }
}

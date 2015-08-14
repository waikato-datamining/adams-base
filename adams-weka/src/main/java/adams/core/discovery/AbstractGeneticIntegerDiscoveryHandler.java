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
 * Ancestor for genetic discovery handlers that handle integer properties.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractGeneticIntegerDiscoveryHandler
  extends AbstractGeneticDiscoveryHandler {

  private static final long serialVersionUID = -5442076178374142588L;

  /** the minimum. */
  protected int m_Minimum;

  /** the maximum. */
  protected int m_Maximum;

  /** numbits. */
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
  }

  public static double log2(int n) {
    return (Math.log(n) / Math.log(2));
  }

  protected int calcNumBits(){
    int range=getMaximum()-getMinimum();
    return((int)(Math.floor(log2(range))+1));
  }

  /**
   * Returns the default minimum.
   *
   * @return		the default
   */
  protected abstract int getDefaultMinimum();

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


  public static int bitsToInt(String bits, int min, int max){
    double j=0;

    for (int i=0;i<bits.length();i++) {
      if (bits.charAt(i)=='1') {
        j = j + Math.pow(2, bits.length()-i-1);
      }
    }
    j+=min;
    return(Math.min((int) j, max));
  }

  public int bitsToInt(String bits){
    double j=0;
    for (int i=0;i<bits.length();i++) {
      if (bits.charAt(i)=='1') {
        j = j + Math.pow(2, bits.length()-i-1);
      }
    }
    j+=getMinimum();
    return(Math.min((int) j, getMaximum()));
  }

  public String intToBits(int in){
    in=in-getMinimum();
    in=Math.min(in, getMaximum()-getMinimum());
    String bits = Integer.toBinaryString(in);
    while (bits.length() < m_numBits){
      bits="0"+bits;
    }
    return(bits);
  }

  protected static int calcNumBits(int min, int max){
    int range=max-min;
    return((int)(Math.floor(log2(range))+1));
  }

  public static String intToBits(int in,int min, int max){
    in=in-min;
    in=Math.min(in, max-min);
    String bits = Integer.toBinaryString(in);
    while (bits.length() <calcNumBits(min,max)){
      bits="0"+bits;
    }
    return(bits);
  }


  public static void main(String[] args) {
    //runGeneticAlgorithm(Environment.class, DarkLord.class, args);
    int i= 55;
    String s=intToBits(i,1,128);
    System.err.println(s);
    i=bitsToInt(s,1,128);
    System.err.println(i);

  }
}

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
 * LeftPad.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.conversion;

/**
 <!-- globalinfo-start -->
 * Left pads a string up to a maximum number of characters.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-width &lt;int&gt; (property: width)
 * &nbsp;&nbsp;&nbsp;The maximum width of the padded string.
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-pad-char &lt;java.lang.String&gt; (property: padCharacter)
 * &nbsp;&nbsp;&nbsp;The character to pad with.
 * &nbsp;&nbsp;&nbsp;default: 0
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LeftPad
  extends AbstractStringConversion {

  /** for serialization. */
  private static final long serialVersionUID = -9142177169642814841L;

  /** the width in chars to pad up to. */
  protected int m_Width;
  
  /** the padding character. */
  protected String m_PadCharacter;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Left pads a string up to a maximum number of characters.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "width", "width",
	    0, 0, null);

    m_OptionManager.add(
	    "pad-char", "padCharacter",
	    "0");
  }

  /**
   * Sets the width of the padded string.
   *
   * @param value	the width
   */
  public void setWidth(int value) {
    if (value >= 0) {
      m_Width = value;
      reset();
    }
    else {
      System.err.println("Width cannot be negative!");
    }
  }

  /**
   * Returns the width of the padded string.
   *
   * @return 		the width
   */
  public int getWidth() {
    return m_Width;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String widthTipText() {
    return "The maximum width of the padded string.";
  }

  /**
   * Sets the character to pad with.
   *
   * @param value	the character
   */
  public void setPadCharacter(String value) {
    if (value.length() == 1) {
      m_PadCharacter = value;
      reset();
    }
    else {
      System.err.println("Only single character allowed!");
    }
  }

  /**
   * Returns the character to pad width.
   *
   * @return 		the width
   */
  public String getPadCharacter() {
    return m_PadCharacter;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String padCharacterTipText() {
    return "The character to pad with.";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  protected Object doConvert() throws Exception {
    StringBuilder	result;
    
    result = new StringBuilder((String) m_Input);
    while (result.length() < m_Width)
      result.insert(0, m_PadCharacter);
    
    return result.toString();
  }
}

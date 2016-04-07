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
 * SimpleAsciiToUnicode.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

/**
 <!-- globalinfo-start -->
 * Turns an ASCII string into a Unicode one, by replacing hexadecimal unicode character representations like '\xf3' with their unicode characters.<br>
 * For instance, "D'\xe1'cil" becomes "Dácil".
 * <br><br>
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
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleAsciiToUnicode
  extends AbstractStringConversion {

  private static final long serialVersionUID = -8897439898825066112L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Turns an ASCII string into a Unicode one, by replacing hexadecimal unicode "
      + "character representations like '\\xf3' with their unicode characters.\n"
      + "For instance, \"D'\\xe1'cil\" becomes \"Dácil\".";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    StringBuilder	result;
    String		input;
    int 		start;
    int			end;
    char		c;
    String		sub;

    result = new StringBuilder();
    input  = (String) m_Input;

    while ((start = input.indexOf("'\\x")) > -1) {
      end = input.indexOf("'", start + 1);
      if (end > -1) {
	if (start > 0)
	  result.append(input.substring(0, start));
	sub = input.substring(start + 3, end);
	result.append((char) Integer.parseUnsignedInt(sub, 16));
	input = input.substring(end + 1);
      }
      else {
	result.append(input);
	input = "";
      }
    }
    if (input.length() > 0)
      result.append(input);

    return result.toString();
  }
}

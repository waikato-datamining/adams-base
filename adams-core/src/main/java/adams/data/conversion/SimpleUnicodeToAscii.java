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
 * SimpleUnicodeToAscii.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

/**
 <!-- globalinfo-start -->
 * Turns a Unicode string into an ASCII one, by replacing the Unicode characters with something like '\xf3' (including the single quotes).<br>
 * For instance, "Dácil" becomes "D'\xe1'cil".
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
public class SimpleUnicodeToAscii
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
      "Turns a Unicode string into an ASCII one, by replacing the Unicode "
      + "characters with something like '\\xf3' (including the single quotes).\n"
      + "For instance, \"Dácil\" becomes \"D'\\xe1'cil\".";
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
    int			i;
    char		c;

    result = new StringBuilder();
    input  = (String) m_Input;

    for (i = 0; i < input.length(); i++) {
      c = input.charAt(i);
      if ((int) c < 128)
	result.append(c);
      else
	result.append("'\\x").append(Integer.toHexString(c)).append("'");
    }

    return result.toString();
  }
}

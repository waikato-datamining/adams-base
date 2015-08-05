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
 * EscapeLatexCharacters.java
 * Copyright (C) 2015 University of Waikato, Hamilton, NZ
 */

package adams.data.conversion;

import adams.core.Utils;

import java.util.Arrays;

/**
 <!-- globalinfo-start -->
 * Simply escapes [C&#64;4f4a7090 with a backslash.
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
public class EscapeLatexCharacters
  extends AbstractStringConversion {

  /** the characters to escape. */
  public final static char[] CHARACTERS = {'%', '_'};

  /** the escaped characters. */
  public final static String[] ESCAPED = {"\\%", "\\_"};

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simply escapes " + Utils.flatten(Arrays.asList(CHARACTERS), ", ") + " with a backslash.";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    String	input;
    String result;

    input  = (String) m_Input;
    result = Utils.backQuoteChars(input, CHARACTERS, ESCAPED);

    return result;
  }
}

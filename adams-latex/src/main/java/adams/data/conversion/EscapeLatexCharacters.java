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
import gnu.trove.list.array.TCharArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Turns the selected characters into their LaTeX representation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-character &lt;PERCENTAGE|UNDERSCORE|DOLLAR|AMPERSAND|CARET|BACKSLASH&gt; [-character ...] (property: characters)
 * &nbsp;&nbsp;&nbsp;The characters to escape.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class EscapeLatexCharacters
  extends AbstractStringConversion {

  private static final long serialVersionUID = -8987744505136943381L;

  /**
   * The characters to escape.
   *
   * @author FracPete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Characters {
    PERCENTAGE,
    UNDERSCORE,
    DOLLAR,
    AMPERSAND,
    CARET,
    BACKSLASH
  }

  /** the characters to escaped. */
  protected Characters[] m_Characters;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Turns the selected characters into their LaTeX representation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "character", "characters",
	    new Characters[0]);
  }

  /**
   * Sets the characters to escape.
   *
   * @param value	the characters
   */
  public void setCharacters(Characters[] value) {
    m_Characters = value;
    reset();
  }

  /**
   * Returns the characters to escape.
   *
   * @return 		the characters
   */
  public Characters[] getCharacters() {
    return m_Characters;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String charactersTipText() {
    return "The characters to escape.";
  }

  /**
   * Performs the actual conversion.
   *
   * @return		the converted data
   * @throws Exception	if something goes wrong with the conversion
   */
  @Override
  protected Object doConvert() throws Exception {
    TCharArrayList	chars;
    List<String>	escaped;
    String		input;
    String 		result;

    input   = (String) m_Input;
    chars   = new TCharArrayList();
    escaped = new ArrayList<>();
    for (Characters ch: m_Characters) {
      switch (ch) {
	case AMPERSAND:
	  chars.add('&');
	  escaped.add("\\&");
	  break;
	case BACKSLASH:
	  chars.add('\\');
	  escaped.add("\\textbackslash ");
	  break;
	case DOLLAR:
	  chars.add('$');
	  escaped.add("\\$");
	  break;
	case UNDERSCORE:
	  chars.add('_');
	  escaped.add("\\_");
	  break;
	case CARET:
	  chars.add('^');
	  escaped.add("\\^");
	  break;
	case PERCENTAGE:
	  chars.add('%');
	  escaped.add("\\%");
	  break;
	default:
	  throw new IllegalStateException("Unhandled character: " + ch);
      }
    }
    result = Utils.backQuoteChars(input, chars.toArray(), escaped.toArray(new String[escaped.size()]));

    return result;
  }
}

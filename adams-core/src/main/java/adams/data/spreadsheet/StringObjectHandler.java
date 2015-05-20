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
 * StringObjectHandler.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.data.spreadsheet;

/**
 <!-- globalinfo-start -->
 * Dummy handler for strings.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringObjectHandler
  extends AbstractObjectHandler<String> {

  /** for serialization. */
  private static final long serialVersionUID = -6046786448098226273L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy handler for strings.";
  }

  /**
   * Checks whether the handler can process the given object.
   * 
   * @param cls		the class to check
   * @return		true if handler can process the object
   */
  @Override
  public boolean handles(Class cls) {
    return (cls == String.class);
  }

  /**
   * Parses the given string.
   * 
   * @param s		the string
   * @return		the generated object, null if failed to convert
   */
  @Override
  public String parse(String s) {
    return s;
  }

  /**
   * Turns the given object back into a string.
   * 
   * @param obj		the object to convert into a string
   * @return		the string representation
   */
  @Override
  public String format(String obj) {
    return obj;
  }
}

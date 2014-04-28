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
 * StringTrim.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

/**
 <!-- globalinfo-start -->
 * Trims strings, i.e., removes leading/trailing whitespaces.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input/output:<br/>
 * - accepts:<br/>
 * <pre>   java.lang.String</pre>
 * <pre>   java.lang.String[]</pre>
 * - generates:<br/>
 * <pre>   java.lang.String</pre>
 * <pre>   java.lang.String[]</pre>
 * <p/>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: StringTrim
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 *         The annotations to attach to this actor.
 *         default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringTrim
  extends AbstractStringOperation {

  /** for serialization. */
  private static final long serialVersionUID = 9030574317512531337L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Trims strings, i.e., removes leading/trailing whitespaces.";
  }

  /**
   * Processes the string.
   *
   * @param s		the string to process
   * @return		the processed string
   */
  protected String process(String s) {
    return s.trim();
  }
}

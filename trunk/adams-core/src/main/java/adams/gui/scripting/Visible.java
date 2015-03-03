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
 * Visible.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.scripting;

/**
 <!-- scriptlet-parameters-start -->
 * Action parameters:<br/>
 * <pre>   visible &lt;comma-separated list of 1-based indices&gt;</pre>
 * <p/>
 <!-- scriptlet-parameters-end -->
 *
 <!-- scriptlet-description-start -->
 * Description:
 * <pre>   Sets the visibility of the specified spectrums to true.
 *    NB: index is based on the order the spectrums haven beeen loaded into the
 *    system, includes all spectrums, not just visible ones.</pre>
 * <p/>
 <!-- scriptlet-description-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class Visible
  extends AbstractVisibilityScriplet {

  /** for serialization. */
  private static final long serialVersionUID = -1762450264044102197L;

  /** the action to execute. */
  public final static String ACTION = "visible";

  /**
   * Returns the action string used in the command processor.
   *
   * @return		the action string
   */
  public String getAction() {
    return ACTION;
  }

  /**
   * Returns the full description of the action.
   *
   * @return		the full description
   */
  public String getDescription() {
    return
        "Sets the visibility of the specified spectrums to true.\n"
      + "NB: index is based on the order the spectrums haven beeen loaded "
      + "into the system, includes all spectrums, not just visible ones.";
  }

  /**
   * Processes the options.
   *
   * @param options	additional/optional options for the action
   * @return		null if no error, otherwise error message
   * @throws Exception 	if something goes wrong
   */
  public String process(String options) throws Exception {
    return process(options, true);
  }
}

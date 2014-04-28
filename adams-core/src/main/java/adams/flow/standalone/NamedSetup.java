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
 * NamedSetup.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.flow.core.AbstractNamedSetup;
import adams.flow.core.ActorUtils;

/**
 <!-- globalinfo-start -->
 * Standalone that executes a standalone actor referenced by the specified named setup.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: NamedSetup
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-setup &lt;adams.core.NamedSetup&gt; (property: setup)
 * &nbsp;&nbsp;&nbsp;The named setup to use.
 * &nbsp;&nbsp;&nbsp;default: name_of_setup
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NamedSetup
  extends AbstractNamedSetup {

  /** for serialization. */
  private static final long serialVersionUID = 2796181462840896142L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Standalone that executes a standalone actor referenced by the specified named setup.";
  }

  /**
   * Sets up the actor referenced by the named setup.
   *
   * @return		null if everything is fine, otherwise error message
   */
  protected String setUpNamedSetupActor() {
    String	result;

    result = super.setUpNamedSetupActor();

    if (result == null) {
      if (!ActorUtils.isStandalone(m_NamedSetupActor))
	result = "Named setup actor referenced by '" + m_Setup.getName() + "' is not a singleton!";
    }

    return result;
  }
}

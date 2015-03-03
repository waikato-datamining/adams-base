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
 * ExternalStandalone.java
 * Copyright (C) 2009-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.ClassCrossReference;
import adams.flow.control.Flow;
import adams.flow.core.AbstractExternalActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.TriggerableEvent;

/**
 <!-- globalinfo-start -->
 * Standalone that executes an external standalone actor stored on disk.<br/>
 * For executing whole flows, use the adams.flow.standalone.ExternalFlow standalone actor instead.<br/>
 * <br/>
 * See also:<br/>
 * adams.flow.standalone.ExternalFlow
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: ExternalStandalone
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-file &lt;adams.core.io.FlowFile&gt; (property: actorFile)
 * &nbsp;&nbsp;&nbsp;The file containing the external actor.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExternalStandalone
  extends AbstractExternalActor
  implements TriggerableEvent, ClassCrossReference {

  /** for serialization. */
  private static final long serialVersionUID = -2418751418803032871L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Standalone that executes an external standalone actor stored on disk.\n"
	+ "For executing whole flows, use the " + ExternalFlow.class.getName() 
	+ " standalone actor instead.";
  }

  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUpExternalActor() {
    String	result;

    result = super.setUpExternalActor();

    if (result == null) {
      if (!ActorUtils.isStandalone(m_ExternalActor))
	result = "External actor '" + m_ActorFile.getAbsolutePath() + "' is not a standalone!";
      else if (m_ExternalActor instanceof Flow)
	result = "You cannot execute a " + Flow.class.getName() + " actor, use " + ExternalFlow.class.getName() + " instead!";
    }

    return result;
  }

  /**
   * Returns the cross-referenced classes.
   *
   * @return		the classes
   */
  @Override
  public Class[] getClassCrossReferences() {
    return new Class[]{ExternalFlow.class};
  }
}

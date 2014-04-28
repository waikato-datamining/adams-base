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
 * ExternalSource.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.flow.core.AbstractExternalActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;
import adams.flow.core.Unknown;

/**
 <!-- globalinfo-start -->
 * Source that executes an external source actor stored on disk.
 * <p/>
 <!-- globalinfo-end -->
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
 *         default: ExternalSource
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseString&gt; [-annotation ...] (property: annotations)
 *         The annotations to attach to this actor.
 * </pre>
 *
 * <pre>-skip (property: skip)
 *         If set to true, transformation is skipped and the input token is just forwarded
 *          as it is.
 * </pre>
 *
 * <pre>-file &lt;adams.core.io.FlowFile&gt; (property: actorFile)
 *         The file containing the external actor.
 *         default: .
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ExternalSource
  extends AbstractExternalActor
  implements OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = 5125350715606672813L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Source that executes an external source actor stored on disk.";
  }

  /**
   * Sets up the external actor.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUpExternalActor() {
    String	result;

    result = super.setUpExternalActor();

    if (result == null) {
      if (!ActorUtils.isSource(m_ExternalActor))
	result = "External actor '" + m_ActorFile.getAbsolutePath() + "' is not a source!";
    }

    return result;
  }

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (m_ExternalActor != null)
      return ((OutputProducer) m_ExternalActor).generates();
    else
      return new Class[]{Unknown.class};
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    return ((OutputProducer) m_ExternalActor).output();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return ((OutputProducer) m_ExternalActor).hasPendingOutput();
  }
}

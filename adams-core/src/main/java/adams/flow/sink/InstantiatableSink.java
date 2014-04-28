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
 * InstantiatableSink.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.sink;

import adams.flow.control.AbstractInstantiatableActor;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Wrapper around a sink actor to be instantiatable in the flow editor as root node.
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
 *         default: InstantiatableSink
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
 * <pre>-actor &lt;adams.flow.core.AbstractActor [options]&gt; (property: actor)
 *         The base sink to use.
 *         default: adams.flow.sink.Display -name Display -width 640 -height 480 -x -1 -y -1
 * </pre>
 *
 * Default options for adams.flow.sink.Display (-actor/actor):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: Display
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
 * <pre>-width &lt;int&gt; (property: width)
 *         The width of the dialog.
 *         default: 640
 * </pre>
 *
 * <pre>-height &lt;int&gt; (property: height)
 *         The height of the dialog.
 *         default: 480
 * </pre>
 *
 * <pre>-x &lt;int&gt; (property: x)
 *         The X position of the dialog (&gt;=0: absolute, -1: left, -2: center, -3: right
 *         ).
 *         default: -1
 * </pre>
 *
 * <pre>-y &lt;int&gt; (property: y)
 *         The Y position of the dialog (&gt;=0: absolute, -1: top, -2: center, -3: bottom
 *         ).
 *         default: -1
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstantiatableSink
  extends AbstractInstantiatableActor
  implements InputConsumer {

  /** for serialization. */
  private static final long serialVersionUID = -6772006127722264274L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Wrapper around a sink actor to be instantiatable in the flow editor as root node.";
  }

  /**
   * Returns the default actor to use.
   *
   * @return		the default actor
   */
  protected AbstractActor getDefaultActor() {
    return new Display();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorTipText() {
    return "The base sink to use.";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return		the info
   */
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo(false, ActorExecution.UNDEFINED, true);
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      if (!ActorUtils.isSink(m_BaseActor))
	result = "Base actor is not a sink!";
    }

    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  public Class[] accepts() {
    if (m_BaseActor != null)
      return ((InputConsumer) m_BaseActor).accepts();
    else
      return new Class[0];
  }

  /**
   * The method that accepts the input token and then processes it.
   *
   * @param token	the token to accept and process
   */
  public void input(Token token) {
    ((InputConsumer) m_BaseActor).input(token);
  }
}

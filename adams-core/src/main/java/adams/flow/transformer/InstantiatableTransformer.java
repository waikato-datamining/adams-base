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
 * InstantiatableTransformer.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.flow.control.AbstractInstantiatableActor;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.ActorUtils;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;
import adams.flow.core.Token;

/**
 <!-- globalinfo-start -->
 * Wrapper around a transformer actor to be instantiatable in the flow editor as root node.
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
 *         default: InstantiatableTransformer
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
 * <pre>-actor &lt;adams.flow.core.AbstractActor [options]&gt; (property: actor)
 *         The base transformer to use.
 *         default: adams.flow.transformer.Filter -name Filter -filter adams.data.filter.PassThrough
 * </pre>
 *
 * Default options for adams.flow.transformer.Filter (-actor/actor):
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 *         The name of the actor.
 *         default: Filter
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
 * <pre>-filter &lt;adams.data.filter.AbstractFilter [options]&gt; (property: filter)
 *         The filter to use for filtering the chromatogram.
 *         default: adams.data.filter.PassThrough
 * </pre>
 *
 * <pre>Default options for adams.data.filter.PassThrough (-filter/filter):
 * </pre>
 *
 * <pre>-D (property: debug)
 *         If set to true, scheme may output additional info to the console.
 * </pre>
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class InstantiatableTransformer
  extends AbstractInstantiatableActor
  implements InputConsumer, OutputProducer {

  /** for serialization. */
  private static final long serialVersionUID = 8368893400510024005L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Wrapper around a transformer actor to be instantiatable in the flow editor as root node.";
  }

  /**
   * Returns the default actor to use.
   *
   * @return		the default actor
   */
  protected AbstractActor getDefaultActor() {
    return new PassThrough();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String actorTipText() {
    return "The base transformer to use.";
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
      if (!ActorUtils.isTransformer(m_BaseActor))
	result = "Base actor is not a transformer!";
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

  /**
   * Returns the class of objects that it generates.
   *
   * @return		the Class of the generated tokens
   */
  public Class[] generates() {
    if (m_BaseActor != null)
      return ((OutputProducer) m_BaseActor).generates();
    else
      return new Class[0];
  }

  /**
   * Returns the generated token.
   *
   * @return		the generated token
   */
  public Token output() {
    return ((OutputProducer) m_BaseActor).output();
  }

  /**
   * Checks whether there is pending output to be collected after
   * executing the flow item.
   *
   * @return		true if there is pending output
   */
  public boolean hasPendingOutput() {
    return ((OutputProducer) m_BaseActor).hasPendingOutput();
  }
}

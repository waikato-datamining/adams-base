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
 * FreeMemory.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.flow.core.ControlActor;
import adams.flow.core.SubFlowWrapUp;
import adams.flow.core.Unknown;
import adams.flow.sink.AbstractSink;

/**
 <!-- globalinfo-start -->
 * Attempts to free up memory of the sub-flow that it belongs to.<br/>
 * This actor is useful in case when sub-flows are only executed once, but still keep their data-structures and gobble up unnecessary memory. One scenario is having a adams.flow.control.Branch actor with lots of sequences as branches, which only get executed once.<br/>
 * The parent must implement adams.flow.core.SubFlowWrapUp in order for this to work.<br/>
 * CAUTION: Behind the scenes, all actors below this actor's parent will call their wrapUp() methods to conserve space. Therefore, use this actor with caution, as it will basically reset the actors.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br/>
 * - accepts:<br/>
 * &nbsp;&nbsp;&nbsp;adams.flow.core.Unknown<br/>
 * <p/>
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
 * &nbsp;&nbsp;&nbsp;default: FreeMemory
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
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FreeMemory
  extends AbstractSink
  implements ControlActor {

  /** for serialization. */
  private static final long serialVersionUID = 2371563734394740356L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Attempts to free up memory of the sub-flow that it belongs to.\n"
	+ "This actor is useful in case when sub-flows are only executed once, "
	+ "but still keep their data-structures and gobble up unnecessary "
	+ "memory. One scenario is having a " + Branch.class.getName() + " "
	+ "actor with lots of sequences as branches, which only get executed once.\n"
	+ "The parent must implement " + SubFlowWrapUp.class.getName() + " "
	+ "in order for this to work.\n"
	+ "CAUTION: Behind the scenes, all actors below this actor's parent "
	+ "will call their wrapUp() methods to conserve space. Therefore, "
	+ "use this actor with caution, as it will basically reset the actors.";
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		<!-- flow-accepts-start -->adams.flow.core.Unknown.class<!-- flow-accepts-end -->
   */
  public Class[] accepts() {
    return new Class[]{Unknown.class};
  }

  /**
   * Initializes the item for flow execution. Also calls the reset() method
   * first before anything else.
   *
   * @return		null if everything is fine, otherwise error message
   * @see		#reset()
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();
    
    if (result == null) {
      if (getParent() == null)
	result = "No parent set!";
      else if (!(getParent() instanceof SubFlowWrapUp))
	result = "Parent does not implement " + SubFlowWrapUp.class.getName() + "!";
    }
    
    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    if (isLoggingEnabled())
      getLogger().info("Freeing up memory: " + getParent().getFullName());
    
    ((SubFlowWrapUp) getParent()).wrapUpSubFlow();
    
    return null;
  }
}

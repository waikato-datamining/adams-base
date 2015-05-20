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
 * RemoveBreakpoints.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.flow.control.Breakpoint;
import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandler;
import adams.flow.core.MutableActorHandler;

/**
 <!-- globalinfo-start -->
 * A meta-processor that processes the actor sequentially with all sub-processors.
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
 * <pre>-processor &lt;adams.flow.processor.AbstractActorProcessor&gt; [-processor ...] (property: subProcessors)
 * &nbsp;&nbsp;&nbsp;The array of processors to use.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class RemoveBreakpoints
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -8658024993875114313L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes all breakpoint actors. If a breakpoint cannot be removed, it gets disabled.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   * @return		the processed actor
   */
  @Override
  protected void processActor(AbstractActor actor) {
    ActorHandler	handler;
    MutableActorHandler	mutable;
    int			i;

    if (actor instanceof ActorHandler) {
      handler = (ActorHandler) actor;

      // remove disabled actors
      if (handler instanceof MutableActorHandler) {
	mutable = (MutableActorHandler) handler;
	i       = 0;
	while (i < mutable.size()) {
	  if (mutable.get(i) instanceof Breakpoint) {
	    mutable.remove(i);
	    m_Modified = true;
	  }
	  else {
	    i++;
	  }
	}
      }
      else {
	for (i = 0; i < handler.size(); i++) {
	  if (handler.get(i) instanceof Breakpoint) {
	    handler.get(i).setSkip(true);
	    m_Modified = true;
	  }
	}
      }

      // recurse
      for (i = 0; i < handler.size(); i++)
	processActor(handler.get(i));
    }
  }
}

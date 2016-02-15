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
 * RemoveDisabledActors.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.flow.core.Actor;
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
public class RemoveDisabledActors
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 3045991817176858251L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Removes all disabled actors (if possible).";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  protected void processActor(Actor actor) {
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
	  if (mutable.get(i).getSkip()) {
	    mutable.remove(i);
	    m_Modified = true;
	  }
	  else {
	    i++;
	  }
	}
      }

      // recurse
      for (i = 0; i < handler.size(); i++)
	processActor(handler.get(i));
    }
  }
}

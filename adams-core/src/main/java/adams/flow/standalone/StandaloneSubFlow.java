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
 * StandaloneSubFlow.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.standalone;

import adams.core.Utils;
import adams.flow.control.MutableConnectedControlActor;
import adams.flow.control.Trigger;
import adams.flow.core.ActorExecution;
import adams.flow.core.ActorHandlerInfo;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class StandaloneSubFlow
  extends MutableConnectedControlActor {

  private static final long serialVersionUID = -7337655514770590168L;

  /**
   * Default constructor.
   */
  public StandaloneSubFlow() {
    super();
  }

  /**
   * Initializes the actor with the specified name.
   *
   * @param name	the name to use
   */
  public StandaloneSubFlow(String name) {
    this();
    setName(name);
  }

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Mini-subflow, similar to the " + Utils.classToString(Trigger.class) + " actor but can be called via "
      + Utils.classToString(TriggerCallableStandalone.class) + ".";
  }

  /**
   * Returns some information about the actor handler, e.g., whether it can
   * contain standalones and the actor execution.
   *
   * @return the info
   */
  @Override
  public ActorHandlerInfo getActorHandlerInfo() {
    return new ActorHandlerInfo()
	     .allowStandalones(true)
	     .allowSource(true)
	     .actorExecution(ActorExecution.SEQUENTIAL)
	     .forwardsInput(false);
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  @Override
  public String actorsTipText() {
    return "The actors of the sub-flow.";
  }
}

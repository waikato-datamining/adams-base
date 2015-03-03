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
 * FixDeprecatedWekaClusterer.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.util.List;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorUtils;
import adams.flow.core.CallableActorReference;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.MutableActorHandler;
import adams.flow.source.WekaClustererSetup;
import adams.flow.standalone.CallableActors;
import adams.flow.transformer.WekaClusterer;
import adams.flow.transformer.WekaTrainClusterer;

/**
 * Replaces the {@link WekaClusterer} transformer with {@link WekaClustererSetup}
 * and {@link WekaTrainClusterer} instances.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
@SuppressWarnings("deprecation")
public class FixDeprecatedWekaClusterer
  extends AbstractModifyingProcessor 
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -4170658262349662939L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Replaces the WekaClusterer transformer with WekaClustererSetup "
	+ "and WekaTrainClusterer instances";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(AbstractActor actor) {
    List<AbstractActor> 	clusterers;
    WekaClusterer		cactor;
    String			var;
    
    clusterers = ActorUtils.enumerate(actor, new Class[]{WekaClusterer.class});
    for (AbstractActor clusterer: clusterers) {
      cactor = (WekaClusterer) clusterer;
      var    = cactor.getOptionManager().getVariableForProperty("clusterer");
      if (cactor.getParent() instanceof CallableActors) {
	WekaClustererSetup setup = new WekaClustererSetup();
	setup.setClusterer(cactor.getClusterer());
	if (var != null)
	  setup.getOptionManager().setVariableForProperty("clusterer", var);
	setup.setName(cactor.getName());
	((MutableActorHandler) cactor.getParent()).set(cactor.index(), setup);
	m_Modified = true;
      }
      else {
	CallableActors callable = CallableActorHelper.createCallableActors(cactor, true);
	WekaClustererSetup setup = new WekaClustererSetup();
	setup.setClusterer(cactor.getClusterer());
	if (var != null)
	  setup.getOptionManager().setVariableForProperty("clusterer", var);
	callable.add(setup);
	WekaTrainClusterer train = new WekaTrainClusterer();
	train.setClusterer(new CallableActorReference(setup.getName()));
	train.setName(cactor.getName());
	((MutableActorHandler) cactor.getParent()).set(cactor.index(), train);
	m_Modified = true;
      }
    }
  }
}

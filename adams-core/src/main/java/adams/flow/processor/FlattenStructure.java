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
 * FlattenStructure.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.control.AbstractTee;
import adams.flow.control.Branch;
import adams.flow.control.LoadBalancer;
import adams.flow.control.Sequence;
import adams.flow.control.WhileLoop;
import adams.flow.core.Actor;
import adams.flow.core.ActorHandler;

import java.lang.reflect.Array;

/**
 <!-- globalinfo-start -->
 * Tries to flatten the flow structure wherever possible.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlattenStructure
  extends AbstractModifyingProcessor
  implements CleanUpProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -5327018527621230693L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Tries to flatten the flow structure wherever possible.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(Actor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void flattenTee(AbstractTee tee) {
	Actor[] actors = ((Sequence) tee.get(0)).getActors();
	tee.setActors(actors);
	m_Modified = true;
      }
      protected void flattenLoadBalancer(LoadBalancer load) {
	Actor[] actors = ((Sequence) load.get(0)).getActors();
	load.setLoadActors(actors);
	m_Modified = true;
      }
      protected void flattenBranch(Branch branch) {
	ActorHandler parent = (ActorHandler) branch.getParent();
	int index = parent.indexOf(branch.getName());
	parent.set(index, branch.getBranches()[0]);
	branch.setParent(null);
	m_Modified = true;
      }
      protected void flattenWhileLoop(WhileLoop load) {
	Actor[] actors = ((Sequence) load.get(0)).getActors();
	load.setActors(actors);
	m_Modified = true;
      }
      protected void flatten(Object current) {
	if (current instanceof AbstractTee) {
	  AbstractTee tee = (AbstractTee) current;
	  if ((tee.size() == 1) && (tee.get(0) instanceof Sequence))
	    flattenTee(tee);
	}
	else if (current instanceof LoadBalancer) {
	  LoadBalancer load = (LoadBalancer) current;
	  if ((load.size() == 1) && (load.get(0) instanceof Sequence))
	    flattenLoadBalancer(load);
	}
	else if (current instanceof Branch) {
	  Branch branch = (Branch) current;
	  if (branch.size() == 1)
	    flattenBranch(branch);
	}
	else if (current instanceof WhileLoop) {
	  WhileLoop loop = (WhileLoop) current;
	  if (loop.size() == 1)
	    flattenWhileLoop(loop);
	}
	// TODO further flattening
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	Object current = option.getCurrentValue();
	if (option.isMultiple()) {
	  Object element;
	  for (int i = 0; i < Array.getLength(current); i++) {
	    element = Array.get(current, i);
	    flatten(element);
	  }
	}
	else {
	  flatten(current);
	}
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// ignored
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return true;
      }
      public boolean canRecurse(Object obj) {
	return canRecurse(obj.getClass());
      }
    });
  }
}

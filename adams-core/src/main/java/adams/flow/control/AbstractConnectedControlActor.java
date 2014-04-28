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
 * AbstractConnectedControlActor.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.control;

import adams.flow.core.AbstractActor;
import adams.flow.core.ActorHandlerInfo;
import adams.flow.core.InputConsumer;
import adams.flow.core.OutputProducer;

/**
 * Ancestor for all actors that control (connected) sub-actors in some way.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConnectedControlActor
  extends AbstractDirectedControlActor {

  /** for serialization. */
  private static final long serialVersionUID = 5833691705690296758L;

  /**
   * Turns the class array into a string.
   *
   * @param classes	the classes to turn into a string
   * @return		the generated string
   */
  protected String classesToString(Class[] classes) {
    String	result;
    int		i;

    result = "[";
    for (i = 0; i < classes.length; i++) {
      if (i > 0)
	result += ", ";
      if (classes[i].isArray())
	result += classes[i].getComponentType() + "[]";
      else
	result += classes[i].getName();
    }
    result += "]";

    return result;
  }

  /**
   * Checks whether all the connections are valid, i.e., the input and
   * output types fit and whether the flow chain is connected properly.
   *
   * @return		null if everything is fine, otherwise the offending
   * 			connection
   */
  protected String checkConnections() {
    String		result;
    int			i;
    AbstractActor	curr;
    AbstractActor	prev;
    boolean		singletons;
    ActorHandlerInfo	info;

    result     = null;
    curr       = null;
    prev       = null;
    info       = getActorHandlerInfo();
    singletons = info.canContainStandalones();   // singletons are only allowed at start (if group allows them at all)
    for (i = 0; i < size(); i++) {
      if ((curr != null) && (!curr.getSkip()))
	prev = curr;
      curr = get(i);
      if (curr.getSkip())
	continue;

      if (isLoggingEnabled())
	getLogger().fine(getFullName() + ".checkConnections/" + i + ": curr=" + curr.getFullName() + ", prev=" + ((prev == null) ? "-null-" : prev.getFullName()));

      // all singletons have to be at start!
      if (singletons) {
	// no more singletons allowed
	if ((curr instanceof InputConsumer) || (curr instanceof OutputProducer)) {
	  singletons = false;
	  if (!(curr instanceof OutputProducer) && (i < size() - 1)) {
	    result =   "First non-singleton actor has to be a '" + OutputProducer.class.getName() + "'. "
     	             + "'" + curr.getFullName() + "' isn't one!";
	    break;
	  }
	  continue;
	}
      }
      else {
	if (!(curr instanceof InputConsumer) && !(curr instanceof OutputProducer)) {
	  if (info.canContainStandalones())
	    result =   "The singleton '" + curr.getFullName() + "' "
	             + "has to be listed before all other types of actors!";
	  else
	    result = "No singletons allowed!";
	  break;
	}
      }

      // are connections compatible?
      if ((prev != null) && !singletons) {
	if ((prev instanceof OutputProducer) && (curr instanceof InputConsumer)) {
	  if (!m_Compatibility.isCompatible((OutputProducer) prev, (InputConsumer) curr)) {
	    result =   "Actor '" + prev.getFullName() + "' "
	             + "outputs '" + classesToString(((OutputProducer) prev).generates()) + "',\n"
	             + "but actor '" + curr.getFullName() + "' "
	             + "only accepts '" + classesToString(((InputConsumer) curr).accepts()) + "'!";
	    break;
	  }
	}
	else if (!(prev instanceof OutputProducer)) {
	  result = "Actor '" + prev.getFullName() + "' does not generate any output!";
	  break;
	}
	else if (!(curr instanceof InputConsumer)) {
	  result = "Actor '" + curr.getFullName() + "' does not accept any input!";
	  break;
	}
      }
    }

    return result;
  }

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null
   */
  @Override
  public String check() {
    String	result;

    result = super.check();

    if (result == null)
      result = checkConnections();

    return result;
  }
}

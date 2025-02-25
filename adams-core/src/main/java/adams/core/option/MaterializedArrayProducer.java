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
 * MaterializedArrayProducer.java
 * Copyright (C) 2011-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.flow.core.Actor;
import adams.flow.core.CallableActorHelper;
import adams.flow.core.CallableActorReference;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Specialized ArrayProducer that resolves "callable actor references" into
 * concrete options.
 * <br><br>
 * Assumes that the callable actor forwards a setup if merely executed, without
 * any input.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MaterializedArrayProducer
  extends ArrayProducer {

  /** for serialization. */
  private static final long serialVersionUID = -1441936327738738725L;

  /** the helper for locating callable actors. */
  protected CallableActorHelper m_Helper;

  /**
   * Initializes the visitor.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Helper = new CallableActorHelper();
  }

  /**
   * Hook method for performing checks on the input. May throw exceptions
   * if object doesn't pass test(s).
   * <br><br>
   * Makes sure that the input is derived from Actor.
   *
   * @param object	the objec to check
   * @return		the checked object
   * @see		Actor
   */
  @Override
  protected OptionHandler checkInput(OptionHandler object) {
    if (object instanceof Actor)
      return object;
    else
      throw new IllegalArgumentException(
	  "Input needs to be derived from " + Actor.class.getName() + "!");
  }

  /**
   * Returns the setup represented by the callable actor.
   *
   * @param ref		the callable reference
   * @return		the commandline setup
   */
  protected String materialize(CallableActorReference ref) {
    String	result;
    Actor	actor;

    result = null;
    actor  = m_Helper.findCallableActorRecursive((Actor) m_Input, ref);
    if (actor != null)
      result = OptionUtils.getCommandLine(actor);
    else
      result = "Failed to locate/process callable reference '" + ref + "'!";

    return result;
  }

  /**
   * Visits an argument option.
   *
   * @param option	the argument option
   * @return		the last internal data structure that was generated
   */
  @Override
  public ArrayList<String> processOption(AbstractArgumentOption option) {
    ArrayList<String>	result;
    Object		currValue;
    Object		currValues;
    int			i;

    if ((option.isVariableAttached() && !m_OutputVariableValues) || !(option.getBaseClass() == CallableActorReference.class)) {
      result = super.processOption(option);
    }
    else {
      result = new ArrayList<String>();

      currValue  = getCurrentValue(option);
      currValues = null;

      if (currValue != null) {
	if (!option.isMultiple()) {
	  currValues = Array.newInstance(option.getBaseClass(), 1);
	  Array.set(currValues, 0, currValue);
	}
	else {
	  currValues = currValue;
	}

	for (i = 0; i < Array.getLength(currValues); i++) {
	  result.add(getOptionIdentifier(option));
	  result.add(materialize((CallableActorReference) Array.get(currValues, i)));
	}
      }

      if (m_Nesting.empty())
        m_OutputList.addAll(result);
      else
        ((ArrayList) m_Nesting.peek()).addAll(result);
    }

    return result;
  }
  
  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(MaterializedArrayProducer.class, args);
  }
}

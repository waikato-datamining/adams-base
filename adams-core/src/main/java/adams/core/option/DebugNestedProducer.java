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
 * DebugNestedProducer.java
 * Copyright (C) 2011-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.base.BaseAnnotation;
import adams.core.option.NestedFormatHelper.Line;
import adams.flow.control.Sequence;
import adams.flow.control.SubProcess;
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.flow.core.ExternalActorHandler;
import adams.flow.core.InternalActorHandler;
import adams.flow.core.MutableActorHandler;
import adams.flow.source.SequenceSource;
import adams.flow.standalone.Standalones;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Nested producer that outputs format useful for debugging purposes.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DebugNestedProducer
  extends NestedProducer
  implements DebugOptionProducer {

  /** for serialization. */
  private static final long serialVersionUID = 931016182843089428L;

  /**
   * Visits a class option.
   *
   * @param option	the class option
   * @return		the last internal data structure that was generated
   */
  @Override
  public List processOption(ClassOption option) {
    ArrayList			result;
    Object			currValue;
    Object			currValues;
    Object			value;
    int				i;
    ArrayList			nested;
    ArrayList			nestedDeeper;
    AbstractCommandLineHandler	handler;
    Actor			actor;
    Actor			wrapper;

    result = new ArrayList();

    if (option.isVariableAttached() && !m_OutputVariableValues) {
      result.add(new Line(getOptionIdentifier(option)));
      result.add(new Line(option.getVariable()));
    }
    else {
      currValue = getCurrentValue(option);

      if (!isDefaultValue(option, currValue)) {
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
	    value = Array.get(currValues, i);
	    actor = null;
            if (value instanceof ExternalActorHandler) {
              if (((ExternalActorHandler) value).getExternalActor() != null) {
		actor = ((ExternalActorHandler) value).getExternalActor();
		// insert fake level to make fullnames work in debugger
		wrapper = null;
		if (ActorUtils.isStandalone(actor))
		  wrapper = new Standalones();
		else if (ActorUtils.isSource(actor))
		  wrapper = new SequenceSource();
		else if (ActorUtils.isTransformer(actor))
		  wrapper = new SubProcess();
		else if (ActorUtils.isSink(actor))
		  wrapper = new Sequence();
		else
		  getLogger().severe("Failed to find wrapper for actor class: " + actor.getClass().getName());
		if (wrapper != null) {
		  ((MutableActorHandler) wrapper).removeAll();
		  ((MutableActorHandler) wrapper).add(actor);
		  wrapper.setName(((Actor) value).getName());
		  wrapper.setAnnotations(new BaseAnnotation("dummy wrapper required for debugging when using external actors"));
		  wrapper.setParent(((Actor) value).getParent());
		  actor = wrapper;
		}
	      }
            }
            else if (value instanceof InternalActorHandler) {
              if (((InternalActorHandler) value).getInternalActor() != null)
                actor = ((InternalActorHandler) value).getInternalActor();
            }
	    if (actor != null)
	      value = actor;
	    result.add(new Line(getOptionIdentifier(option)));
	    nested = new ArrayList();
	    result.add(nested);
	    nested.add(new Line(value.getClass().getName()));
	    nestedDeeper = new ArrayList();
	    nested.add(nestedDeeper);
	    if (value instanceof OptionHandler) {
	      m_Nesting.push(nested);
	      m_Nesting.push(nestedDeeper);
	      doProduce(((OptionHandler) value).getOptionManager());
	      m_Nesting.pop();
	      m_Nesting.pop();
	    }
	    else {
	      handler = AbstractCommandLineHandler.getHandler(value);
	      for (String line: handler.getOptions(value))
		nestedDeeper.add(new Line(line));
	    }
	  }
	}
      }
    }

    if (m_Nesting.empty())
      m_Output.addAll(result);
    else
      m_Nesting.peek().addAll(result);

    return result;
  }

  /**
   * Executes the producer from commandline.
   * 
   * @param args	the commandline arguments, use -help for help
   */
  public static void main(String[] args) {
    runProducer(DebugNestedProducer.class, args);
  }
}

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
 * RemoveUnusedReferenceableActors.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.NamedCounter;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActorReference;
import adams.flow.core.Actor;
import adams.flow.core.ActorReferenceHandler;
import adams.flow.core.MutableActorHandler;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Removes all referenceable actors (eg callable actors) that are not used.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class RemoveUnusedReferenceableActors
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 737084782888325641L;

  /** the usage counter. */
  protected NamedCounter m_ReferenceCount;

  /** the definition counter. */
  protected NamedCounter m_DefinitionCount;

  /** the referenceable actors. */
  protected Map<String,Actor> m_ReferenceableActors;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Removes all referenceable actors (eg callable actors) that are not used.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ReferenceCount      = new NamedCounter();
    m_DefinitionCount     = new NamedCounter();
    m_ReferenceableActors = new HashMap<>();
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(Actor actor) {
    Set<String> 	defined;
    List<String> 	names;
    Actor 		unused;

    m_ReferenceCount.clear();
    m_DefinitionCount.clear();

    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void incrementDefinitionCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementDefinitionCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof Actor) {
	    Actor actor = (Actor) obj;
	    m_DefinitionCount.next(actor.getName());
	    m_ReferenceableActors.put(actor.getName(), actor);
	  }
	}
      }
      protected void incrementReferenceCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementReferenceCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof AbstractActorReference)
	    m_ReferenceCount.next(((AbstractActorReference) obj).getValue());
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
        // definition
        if ((ClassLocator.isSubclass(Actor.class, option.getBaseClass())) && (option.getOptionHandler() instanceof ActorReferenceHandler))
          incrementDefinitionCount(option.getCurrentValue());
        // reference
	if (ClassLocator.isSubclass(AbstractActorReference.class, option.getBaseClass())) {
	  incrementReferenceCount(option.getCurrentValue());
	}
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return true;
      }
      public boolean canRecurse(Object obj) {
	if (obj instanceof Actor)
	  return !((Actor) obj).getSkip() && canRecurse(obj.getClass());
	else
	  return canRecurse(obj.getClass());
      }
    });

    // unused
    defined = m_DefinitionCount.nameSet();
    names   = new ArrayList<>();
    for (String def: defined) {
      if (!m_ReferenceCount.has(def))
	names.add(def);
    }
    if (names.size() > 0) {
      for (String name: names) {
        unused = m_ReferenceableActors.get(name);
        if (unused.getParent() instanceof MutableActorHandler) {
          m_Modified = true;
	  ((MutableActorHandler) unused.getParent()).remove(unused.index());
	}
      }
    }
  }
}

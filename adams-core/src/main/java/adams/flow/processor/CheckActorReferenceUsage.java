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
 * CheckActorReferenceUsage.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.NamedCounter;
import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActorReference;
import adams.flow.core.Actor;
import adams.flow.core.ActorReferenceHandler;
import adams.gui.dialog.TextPanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.awt.Component;
import java.awt.Dimension;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class CheckActorReferenceUsage
  extends AbstractActorProcessor 
  implements GraphicalOutputProducingProcessor, CheckProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 737084782888325641L;

  /** whether to output counts regardless of warnings. */
  protected boolean m_OutputCounts;

  /** the usage counter. */
  protected NamedCounter m_ReferenceCount;

  /** the definition counter. */
  protected NamedCounter m_DefinitionCount;
  
  /** the warnings that were produced. */
  protected StringBuilder m_Warnings;
  
  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Performs a 'soft' check on the usage of actor references, like "
	  + "callable actors.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_ReferenceCount = new NamedCounter();
    m_DefinitionCount = new NamedCounter();
    m_Warnings        = null;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "output-counts", "outputCounts",
	    false);
  }

  /**
   * Sets whether to output the counts regardless of warnings.
   *
   * @param value	true if to output counts
   */
  public void setOutputCounts(boolean value) {
    m_OutputCounts = value;
    reset();
  }

  /**
   * Returns whether to output the counts regardless of warnings.
   *
   * @return		true if to output counts
   */
  public boolean getOutputCounts() {
    return m_OutputCounts;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String outputCountsTipText() {
    return "If enabled, the counts get output regardless of warnings.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(Actor actor) {
    Set<String> 	undefined;
    Set<String> 	defined;
    List<String> 	names;
    Set<String>		combined;

    m_ReferenceCount.clear();
    m_DefinitionCount.clear();
    m_Warnings = null;
    
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void incrementDefinitionCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementDefinitionCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof Actor)
	    m_DefinitionCount.next(((Actor) obj).getName());
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

    // undefined
    undefined = new HashSet<>(m_ReferenceCount.nameSet());
    defined   = m_DefinitionCount.nameSet();
    undefined.removeAll(defined);
    if (undefined.size() > 0) {
      names = new ArrayList<>();
      for (String undef: undefined) {
        if (undef.isEmpty())
          continue;
        names.add(undef);
      }
      Collections.sort(names);
      m_Warnings = new StringBuilder(Utils.flatten(names, "\n"));
    }

    // unused
    names = new ArrayList<>();
    for (String def: defined) {
      if (!m_ReferenceCount.has(def))
	names.add(def);
    }
    Collections.sort(names);
    if (names.size() > 0) {
      if (m_Warnings == null)
        m_Warnings = new StringBuilder();
      else
        m_Warnings.append("\n\n");
      m_Warnings.append("The following references were never used:\n");
      m_Warnings.append(Utils.flatten(names, "\n"));
    }

    if (m_OutputCounts) {
      if (m_Warnings == null)
        m_Warnings = new StringBuilder();
      else
        m_Warnings.append("\n\n");
      m_Warnings.append("Name: #Definition/#Reference\n");
      combined = new HashSet<>();
      combined.addAll(m_ReferenceCount.nameSet());
      combined.addAll(m_DefinitionCount.nameSet());
      names = new ArrayList<>(combined);
      Collections.sort(names);
      for (String var: names) {
        if (var.isEmpty())
          continue;
	m_Warnings.append(var + ": " + m_DefinitionCount.current(var) + "/" + m_ReferenceCount.current(var) + "\n");
      }
    }
  }

  /**
   * Returns the string that explains the warnings.
   * 
   * @return		the heading for the warnings, null if not available
   */
  public String getWarningHeader() {
    return "The following actor references were never defined:";
  }

  /**
   * Returns the warnings, if any, on variables that might never get set.
   * 
   * @return		the warnings
   */
  public String getWarnings() {
    return m_Warnings.toString();
  }
  
  /**
   * Returns whether graphical output was generated.
   *
   * @return		true if graphical output was generated
   */
  public boolean hasGraphicalOutput() {
    return (m_Warnings != null);
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Actor reference check";
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  public Component getGraphicalOutput() {
    TextPanel	result;
    
    result = new TextPanel();
    result.setTitle(getTitle());
    result.setPreferredSize(new Dimension(400, 300));
    result.setEditable(false);
    if (m_Warnings != null)
      result.setContent(m_Warnings.toString());
    result.setInfoText(getWarningHeader());
    
    return result;
  }
}

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
 * ListAllVariables.java
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.ClassLocator;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.VariableUpdater;
import adams.core.VariableUser;
import adams.core.Variables;
import adams.core.base.BaseObject;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActor;
import adams.flow.core.Actor;
import adams.gui.dialog.TextPanel;

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
 * Lists all variable occurrences in the flow whether being set or used.
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
 * @version $Revision$
 */
public class ListAllVariables
  extends AbstractActorProcessor
  implements GraphicalOutputProducingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 737084782888325641L;

  /** the variables in use. */
  protected List<String> m_Variables;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists all variable occurrences in the flow whether being set or used.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Variables = new ArrayList<>();
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(AbstractActor actor) {
    final Set<String>   variables;

    m_Variables.clear();
    variables = new HashSet<>();

    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void incrementSetCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementSetCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof VariableName)
	    variables.add(((VariableName) obj).getValue());
	}
      }
      protected void incrementUsageCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementUsageCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof VariableName)
	    variables.add(((VariableName) obj).getValue());
	}
      }
      protected void extractVariables(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    extractVariables(Array.get(obj, i));
	}
	else {
	  String val = null;
	  if (obj instanceof String)
	    val = (String) obj;
	  else if (obj instanceof BaseObject)
	    val = ((BaseObject) obj).getValue();
	  if (val != null) {
	    String[] vars = Variables.extractNames(val);
	    for (String var: vars)
	      variables.add(var);
	  }
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	if (option.isVariableAttached())
	  variables.add(option.getVariableName());
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// variable attached?
	if (option.isVariableAttached()) {
	  variables.add(option.getVariableName());
	}
	else {
	  // updater
	  if ((option.getOptionHandler() instanceof VariableUpdater) && (option.getBaseClass() == VariableName.class)) {
	    if (((VariableUpdater) option.getOptionHandler()).isUpdatingVariables())
	      incrementSetCount(option.getCurrentValue());
	    return;
	  }
	  // user
	  else if ((option.getOptionHandler() instanceof VariableUser) && (option.getBaseClass() == VariableName.class)) {
	    if (((VariableUser) option.getOptionHandler()).isUsingVariables())
	      incrementUsageCount(option.getCurrentValue());
	    return;
	  }
	  // string?
	  if (option.getBaseClass() == String.class)
	    extractVariables(option.getCurrentValue());
	    // baseobject?
	  else if (ClassLocator.isSubclass(BaseObject.class, option.getBaseClass()))
	    extractVariables(option.getCurrentValue());
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

    m_Variables.addAll(variables);
    Collections.sort(m_Variables);
  }

  /**
   * Returns whether graphical output was generated.
   *
   * @return		true if graphical output was generated
   */
  public boolean hasGraphicalOutput() {
    return (m_Variables.size() > 0);
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Variables";
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  public Component getGraphicalOutput() {
    TextPanel	result;

    result = new TextPanel();
    result.setTitle("Variables");
    result.setPreferredSize(new Dimension(400, 300));
    result.setEditable(false);
    result.setContent(Utils.flatten(m_Variables, "\n"));

    return result;
  }

  /**
   * Returns the variables.
   *
   * @return		the variables
   */
  public List<String> getVariables() {
    return m_Variables;
  }
}

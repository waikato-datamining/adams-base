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
 * CheckVariableUsage.java
 * Copyright (C) 2012-2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.NamedCounter;
import adams.core.Utils;
import adams.core.VariableName;
import adams.core.VariableNameValuePair;
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
import adams.flow.core.Actor;
import adams.flow.core.ActorUtils;
import adams.gui.core.GUIHelper;
import adams.gui.dialog.TextPanel;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.awt.Component;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 <!-- globalinfo-start -->
 * Performs a 'soft' check whether variables in use are actually set somewhere in the flow.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-output-counts &lt;boolean&gt; (property: outputCounts)
 * &nbsp;&nbsp;&nbsp;If enabled, the counts get output regardless of warnings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CheckVariableUsage
  extends AbstractActorProcessor 
  implements GraphicalOutputProducingProcessor, CheckProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 737084782888325641L;

  /** whether to output counts regardless of warnings. */
  protected boolean m_OutputCounts;
  
  /** the usage counter. */
  protected NamedCounter m_UsageCount;

  /** the set counter. */
  protected NamedCounter m_SetCount;
  
  /** the warnings that were produced. */
  protected String m_Warnings;

  /** the variables to skip. */
  protected Set<String> m_Skip;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Performs a 'soft' check whether variables in use are actually set "
	+ "somewhere in the flow.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_UsageCount = new NamedCounter();
    m_SetCount   = new NamedCounter();
    m_Skip       = new HashSet<>();
    m_Warnings   = null;
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
    Set<String>		used;
    Set<String>		set;
    List<String>	vars;
    Set<String>		combined;
    
    m_UsageCount.clear();
    m_SetCount.clear();
    m_Skip.clear();
    m_Skip.add(ActorUtils.FLOW_DIR);
    m_Skip.add(ActorUtils.FLOW_FILENAME_LONG);
    m_Skip.add(ActorUtils.FLOW_FILENAME_SHORT);
    m_Skip.add(ActorUtils.FLOW_FILENAME_RESTORE);
    m_Skip.add(ActorUtils.FLOW_ID);
    m_Warnings = null;
    
    actor.getOptionManager().traverse(new OptionTraverser() {
      protected void incrementSetCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementSetCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof VariableName) {
            VariableName var = (VariableName) obj;
            if (!m_Skip.contains(var.getValue()))
              m_SetCount.next(var.getValue());
          }
	  else if (obj instanceof VariableNameValuePair) {
            VariableNameValuePair var = (VariableNameValuePair) obj;
            if (!m_Skip.contains(var.varName().getValue()))
              m_SetCount.next(var.varName().getValue());
          }
	}
      }
      protected void incrementUsageCount(Object obj) {
	if (obj.getClass().isArray()) {
	  for (int i = 0; i < Array.getLength(obj); i++)
	    incrementUsageCount(Array.get(obj, i));
	}
	else {
	  if (obj instanceof VariableName) {
            VariableName var = (VariableName) obj;
            if (!m_Skip.contains(var.getValue()))
              m_UsageCount.next(var.getValue());
          }
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
	    for (String var: vars) {
              if (!m_Skip.contains(var))
                m_UsageCount.next(var);
            }
	  }
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	if (option.isVariableAttached())
	  m_UsageCount.next(option.getVariableName());
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	handleArgumentOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	// variable attached?
	if (option.isVariableAttached()) {
          if (!m_Skip.contains(option.getVariableName()))
            m_UsageCount.next(option.getVariableName());
	}
	else {
	  // updater
	  if ((option.getOptionHandler() instanceof VariableUpdater) && (option.getBaseClass() == VariableName.class)) {
	    if (((VariableUpdater) option.getOptionHandler()).isUpdatingVariables())
              incrementSetCount(option.getCurrentValue());
	    return;
	  }
	  else if ((option.getOptionHandler() instanceof VariableUpdater) && (option.getBaseClass() == VariableNameValuePair.class)) {
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
	  return (obj != null) && canRecurse(obj.getClass());
      }
    });

    used = new HashSet<>(m_UsageCount.nameSet());
    set  = m_SetCount.nameSet();
    used.removeAll(set);
    if (used.size() > 0) {
      vars = new ArrayList<>(used);
      Collections.sort(vars);
      m_Warnings = Utils.flatten(vars, "\n");
    }
    if (m_OutputCounts) {
      if (m_Warnings == null)
	m_Warnings = "";
      else
	m_Warnings += "\n\n";
      m_Warnings += "Name: #Set/#Use\n";
      combined = new HashSet<>();
      combined.addAll(m_UsageCount.nameSet());
      combined.addAll(m_SetCount.nameSet());
      vars = new ArrayList<>(combined);
      Collections.sort(vars);
      for (String var: vars)
	m_Warnings += var + ": " + m_SetCount.current(var) + "/" + m_UsageCount.current(var) + "\n";
    }
  }

  /**
   * Returns the string that explains the warnings.
   * 
   * @return		the heading for the warnings, null if not available
   */
  public String getWarningHeader() {
    if (m_OutputCounts)
      return "Overview of variables being set and used:";
    else
      return "The following variables were never set:";
  }

  /**
   * Returns the warnings, if any, on variables that might never get set.
   * 
   * @return		the warnings
   */
  public String getWarnings() {
    return m_Warnings;
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
    return "Variable check";
  }

  /**
   * Returns the graphical output that was generated.
   *
   * @return		the graphical output
   */
  public Component getGraphicalOutput() {
    TextPanel	result;
    
    result = new TextPanel();
    result.setTitle("Variable check");
    result.setPreferredSize(GUIHelper.getDefaultTinyDialogDimension());
    result.setEditable(false);
    if (m_Warnings != null)
      result.setContent(m_Warnings);
    result.setInfoText(getWarningHeader());
    
    return result;
  }
}

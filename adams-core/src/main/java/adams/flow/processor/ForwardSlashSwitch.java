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
 * ForwardSlashSwitch.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Utils;
import adams.core.io.ForwardSlashSupporter;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.Actor;
import adams.flow.source.EnterManyValues;
import adams.flow.source.valuedefinition.DefaultValueDefinition;

/**
 * Processor that switches the flag of all {@link adams.core.io.ForwardSlashSupporter} objects.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ForwardSlashSwitch
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;

  /** whether to use forward slashes. */
  protected boolean m_UseForwardSlashes;

  /** whether to exclude the EnterManyValues' DefaultValueDefinition. */
  protected boolean m_ExcludeDefaultValueDefinition;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Processor that switches the flag of " + Utils.classToString(ForwardSlashSupporter.class) + " objects.\n"
      + "It is possible to exclude instances of " + Utils.classToString(DefaultValueDefinition.class)
	+ " instances as used by the " + Utils.classToString(EnterManyValues.class) + " actor.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "use-forward-slashes", "useForwardSlashes",
      false);

    m_OptionManager.add(
      "exclude-default-value-definition", "excludeDefaultValueDefinition",
      true);
  }

  /**
   * Sets what to update the {@link ForwardSlashSupporter} objects to.
   *
   * @param value	if true then use forward slashes
   */
  public void setUseForwardSlashes(boolean value) {
    m_UseForwardSlashes = value;
    reset();
  }

  /**
   * Returns what to update the {@link ForwardSlashSupporter} objects to.
   *
   * @return		true if forward slashes are used
   */
  public boolean getUseForwardSlashes() {
    return m_UseForwardSlashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useForwardSlashesTipText() {
    return "What to update the " + Utils.classToString(ForwardSlashSupporter.class) + " objects to.";
  }

  /**
   * Sets whether to exclude {@link DefaultValueDefinition} instances as used by {@link EnterManyValues}.
   *
   * @param value	true if to exclude
   */
  public void setExcludeDefaultValueDefinition(boolean value) {
    m_ExcludeDefaultValueDefinition = value;
    reset();
  }

  /**
   * Returns whether to exclude {@link DefaultValueDefinition} instances as used by {@link EnterManyValues}.
   *
   * @return		true if to exclude
   */
  public boolean getExcludeDefaultValueDefinition() {
    return m_ExcludeDefaultValueDefinition;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String excludeDefaultValueDefinitionTipText() {
    return "Whether to exclude " + Utils.classToString(DefaultValueDefinition.class)
      + " instances as used by the " + Utils.classToString(EnterManyValues.class)
      + " actor.";
  }

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(Actor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
        // ignored
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	Boolean current = (Boolean) option.getCurrentValue();
	if (current != m_UseForwardSlashes) {
	  option.setCurrentValue(m_UseForwardSlashes);
	  m_Modified = true;
	}
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
        // ignored
      }
      public boolean canHandle(AbstractOption option) {
        boolean		result;

	result = (option.getOwner().getOwner() instanceof ForwardSlashSupporter)
	  && option.getProperty().equals("useForwardSlashes");

	if (result && m_ExcludeDefaultValueDefinition) {
	  if (option.getOwner().getOwner() instanceof DefaultValueDefinition)
	    result = false;
	}

	return result;
      }
      public boolean canRecurse(Class cls) {
	return true;
      }
      public boolean canRecurse(Object obj) {
	return (obj != null) && canRecurse(obj.getClass());
      }
    });
  }
}

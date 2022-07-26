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
 * ChangeDisplayType.java
 * Copyright (C) 2019 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Utils;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.Actor;
import adams.flow.core.DisplayTypeSupporter;
import adams.flow.core.displaytype.AbstractDisplayType;
import adams.flow.core.displaytype.Background;
import adams.flow.core.displaytype.Default;
import nz.ac.waikato.cms.locator.ClassLocator;

import java.lang.reflect.Array;

/**
 * Processor that updates the display type of {@link adams.flow.core.DisplayTypeSupporter} actors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeDisplayType
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;
  
  /** the old display type. */
  protected AbstractDisplayType m_OldType;

  /** whether to update any type. */
  protected boolean m_UpdateAnyType;

  /** the new display type. */
  protected AbstractDisplayType m_NewType;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates all display types of actors implementing "
      + Utils.classToString(DisplayTypeSupporter.class)
      + " that match the user-provided old type.\n"
      + "It is also possible to replace just any display type with the new one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "old-type", "oldType",
      new Default());

    m_OptionManager.add(
      "update-any-type", "updateAnyType",
      false);

    m_OptionManager.add(
      "new-type", "newType",
      new Background());
  }

  /**
   * Sets the old display type to replace.
   *
   * @param value	the old type
   */
  public void setOldType(AbstractDisplayType value) {
    m_OldType = value;
    reset();
  }

  /**
   * Returns the old display type to replace.
   *
   * @return		the old type
   */
  public AbstractDisplayType getOldType() {
    return m_OldType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldTypeTipText() {
    return "The old display type to replace.";
  }

  /**
   * Sets whether to replace any display type with the new one.
   *
   * @param value	true if to replace any type
   */
  public void setUpdateAnyType(boolean value) {
    m_UpdateAnyType = value;
    reset();
  }

  /**
   * Returns whether to replace any display type with the new one.
   *
   * @return		true if to replace any type
   */
  public boolean getUpdateAnyType() {
    return m_UpdateAnyType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String updateAnyTypeTipText() {
    return "If enabled, any display type will get replaced with the new one, not just ones matching the old one.";
  }

  /**
   * Sets the new display type to replace.
   *
   * @param value	the new type
   */
  public void setNewType(AbstractDisplayType value) {
    m_NewType = value;
    reset();
  }

  /**
   * Returns the old display type to replace.
   *
   * @return		the new type
   */
  public AbstractDisplayType getNewType() {
    return m_NewType;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newTypeTipText() {
    return "The new display type to use.";
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
      protected boolean requiresUpdate(AbstractDisplayType current) {
        if (m_UpdateAnyType && (current.getClass() != m_NewType.getClass()))
	  return true;
        else
	  return (current.getClass() == m_OldType.getClass());
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	if (ClassLocator.matches(AbstractDisplayType.class, option.getBaseClass())) {
	  Object current = option.getCurrentValue();
	  if (option.isMultiple()) {
	    for (int i = 0; i < Array.getLength(current); i++) {
	      if (requiresUpdate((AbstractDisplayType) Array.get(current, i))) {
		Array.set(current, i, m_NewType);
		m_Modified = true;
	      }
	    }
	  }
	  else {
	    if (requiresUpdate((AbstractDisplayType) current)) {
	      option.setCurrentValue(m_NewType);
	      m_Modified = true;
	    }
	  }
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
	return (obj != null) && canRecurse(obj.getClass());
      }
    });
  }
}

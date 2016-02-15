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
 * ChangeDatabaseConditionLimit.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.base.BaseRegExp;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.Actor;

import java.lang.reflect.Array;

/**
 * Processor that the limit of a database condition.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7042 $
 */
public class ChangeDatabaseConditionLimit
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;
  
  /** the old limit. */
  protected int m_OldLimit;
  
  /** whether to change all limits. */
  protected boolean m_ChangeAll;
  
  /** the classnames to limit the change to. */
  protected BaseRegExp m_RegExp;
  
  /** the new limit. */
  protected int m_NewLimit;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the limits of all database condition objects.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "old-limit", "oldLimit",
	    -1, -1, null);

    m_OptionManager.add(
	    "change-all", "changeAll",
	    false);

    m_OptionManager.add(
	    "reg-exp", "regExp",
	    new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
	    "new-limit", "newLimit",
	    -1, -1, null);
  }

  /**
   * Sets the old limit to replace.
   *
   * @param value	the old limit
   */
  public void setOldLimit(int value) {
    m_OldLimit = value;
    reset();
  }

  /**
   * Returns the old limit to replace.
   *
   * @return		the old limit
   */
  public int getOldLimit() {
    return m_OldLimit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldLimitTipText() {
    return "The old limit to replace.";
  }

  /**
   * Sets whether to change all limits, not just the ones that match the 
   * specified old limit.
   *
   * @param value	true if change all
   */
  public void setChangeAll(boolean value) {
    m_ChangeAll = value;
    reset();
  }

  /**
   * Returns whether to change all limits, not just the ones that match the 
   * specified old limit.
   *
   * @return		true if to change all
   */
  public boolean getChangeAll() {
    return m_ChangeAll;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String changeAllTipText() {
    return "If enabled, any limit will get replaced with the new one, not just the ones that match the old limit.";
  }

  /**
   * Sets the regular expression to apply to the classname of the conditions
   * object for limiting the scope of the replacement.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the classname of the conditions
   * object for limiting the scope of the replacement.
   *
   * @return		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String regExpTipText() {
    return "The regular expression for the classnames to limit the update to.";
  }

  /**
   * Sets the new Limit to replace.
   *
   * @param value	the new Limit
   */
  public void setNewLimit(int value) {
    m_NewLimit = value;
    reset();
  }

  /**
   * Returns the new Limit to replace.
   *
   * @return		the new Limit
   */
  public int getNewLimit() {
    return m_NewLimit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newLimitTipText() {
    return "The new limit to set.";
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
      protected void process(Object obj) {
	if (obj instanceof adams.db.AbstractLimitedConditions) {
	  if (!m_RegExp.isMatch(obj.getClass().getName()))
	    return;
	  adams.db.AbstractLimitedConditions cond = (adams.db.AbstractLimitedConditions) obj;
	  if (m_ChangeAll || (!m_ChangeAll && (m_OldLimit == cond.getLimit()))) {
	    cond.setLimit(m_NewLimit);
	    m_Modified = true;
	  }
	}
      }
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	Object current = option.getCurrentValue();
	if (option.isMultiple()) {
	  for (int i = 0; i < Array.getLength(current); i++)
	    process(Array.get(current, i));
	}
	else {
	  process(current);
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

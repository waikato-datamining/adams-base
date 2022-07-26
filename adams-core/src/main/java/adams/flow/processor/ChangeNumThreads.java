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
 * ChangeNumThreads.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Performance;
import adams.core.ThreadLimiter;
import adams.core.Utils;
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
 * Processor that changes the number of threads used for classes that implement
 * {@link ThreadLimiter}.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeNumThreads
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;

  /** the old setting. */
  protected int m_OldNumThreads;

  /** whether to change all settings. */
  protected boolean m_ChangeAll;

  /** the classnames to limit the change to. */
  protected BaseRegExp m_RegExp;
  
  /** the setting. */
  protected int m_NewNumThreads;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Updates the number of threads to use for all classes implementing " + Utils.classToString(ThreadLimiter.class) + ".";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "old-num-threads", "oldNumThreads",
      -1, -1, null);

    m_OptionManager.add(
      "change-all", "changeAll",
      false);

    m_OptionManager.add(
      "reg-exp", "regExp",
      new BaseRegExp(BaseRegExp.MATCH_ALL));

    m_OptionManager.add(
      "new-num-threads", "newNumThreads",
      -1, -1, null);
  }

  /**
   * Sets the old setting to replace.
   *
   * @param value	the old setting
   */
  public void setOldNumThreads(int value) {
    m_OldNumThreads = value;
    reset();
  }

  /**
   * Returns the old setting to replace.
   *
   * @return		the old setting
   */
  public int getOldNumThreads() {
    return m_OldNumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String oldNumThreadsTipText() {
    return "The old limit to replace; " + Performance.getNumThreadsHelp();
  }

  /**
   * Sets whether to change all settings, not just the ones that match the
   * specified old setting.
   *
   * @param value	true if change all
   */
  public void setChangeAll(boolean value) {
    m_ChangeAll = value;
    reset();
  }

  /**
   * Returns whether to change all settings, not just the ones that match the
   * specified old setting.
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
    return "If enabled, any setting will get replaced with the new one, not just the ones that match the old one.";
  }

  /**
   * Sets the regular expression to apply to the classname of the
   * object for limiting the scope of the replacement.
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to apply to the classname of the
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
   * Sets the new setting to use.
   *
   * @param value	the new setting
   */
  public void setNewNumThreads(int value) {
    m_NewNumThreads = value;
    reset();
  }

  /**
   * Returns the new setting to use.
   *
   * @return		the new setting
   */
  public int getNewNumThreads() {
    return m_NewNumThreads;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the gui
   */
  public String newNumThreadsTipText() {
    return "The new setting to use; " + Performance.getNumThreadsHelp();
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
	if (obj instanceof ThreadLimiter) {
	  if (!m_RegExp.isMatch(obj.getClass().getName()))
	    return;
	  ThreadLimiter limiter = (ThreadLimiter) obj;
	  if (m_ChangeAll || (m_OldNumThreads == limiter.getNumThreads())) {
	    limiter.setNumThreads(m_NewNumThreads);
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
	return (obj != null) && canRecurse(obj.getClass());
      }
    });
  }
}

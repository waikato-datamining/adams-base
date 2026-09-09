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
 * ChangeRegExps.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.Utils;
import adams.core.base.BaseRegExp;
import adams.core.discovery.PropertyPath.Path;
import adams.core.discovery.PropertyTraversal;
import adams.core.discovery.PropertyTraversal.Observer;
import adams.flow.core.Actor;

import java.beans.PropertyDescriptor;

/**
 * Replaces regular expressions that match an old expression with the corresponding new one.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class ChangeRegExps
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = -3031404150902143297L;

  /** the old expressions to replace. */
  protected BaseRegExp[] m_OldExpressions;

  /** the new expressions to replace with. */
  protected BaseRegExp[] m_NewExpressions;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Replaces regular expression objects (" + Utils.classToString(BaseRegExp.class) + ") that match an old expression with the corresponding new one.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "old-expression", "oldExpressions",
      new BaseRegExp[0]);

    m_OptionManager.add(
      "new-expression", "newExpressions",
      new BaseRegExp[0]);
  }

  /**
   * Sets the old expressions to replace.
   *
   * @param value 	the old expressions
   */
  public void setOldExpressions(BaseRegExp[] value) {
    m_OldExpressions = value;
    m_NewExpressions = (BaseRegExp[]) Utils.adjustArray(m_NewExpressions, m_OldExpressions.length, new BaseRegExp());
    reset();
  }

  /**
   * Returns the old expressions to replace.
   *
   * @return 		the old expressions
   */
  public BaseRegExp[] getOldExpressions() {
    return m_OldExpressions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String oldExpressionsTipText() {
    return "The old expressions to replace.";
  }

  /**
   * Sets the new expressions to replace with.
   *
   * @param value 	the new expressions
   */
  public void setNewExpressions(BaseRegExp[] value) {
    m_NewExpressions = value;
    m_OldExpressions = (BaseRegExp[]) Utils.adjustArray(m_OldExpressions, m_NewExpressions.length, new BaseRegExp());
    reset();
  }

  /**
   * Returns the new expressions to replace with.
   *
   * @return 		the new expressions
   */
  public BaseRegExp[] getNewExpressions() {
    return m_NewExpressions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String newExpressionsTipText() {
    return "The new expressions to replace with.";
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
    PropertyTraversal traversal;

    Observer observer = new Observer() {
      @Override
      public boolean observe(Path path, PropertyDescriptor desc, Object parent, Object child) {
	if (child.getClass() == BaseRegExp.class) {
	  BaseRegExp regExp = (BaseRegExp) child;
	  for (int i = 0; i < m_OldExpressions.length; i++) {
	    if (regExp.equals(m_OldExpressions[i])) {
	      regExp.setValue(m_NewExpressions[i].getValue());
	      m_Modified = true;
	    }
	  }
	}
	return true;
      }
    };
    traversal = new PropertyTraversal();
    traversal.traverse(observer, actor);
  }
}

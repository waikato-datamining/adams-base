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
 * AbstractModifyingInteractiveProcessor.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.core.AbstractActor;

/**
 * Ancestor for processors that interact with the user and potentially
 * modify the flow.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractModifyingInteractiveProcessor
  extends AbstractModifyingProcessor
  implements InteractiveProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 6521398271115318357L;
  
  /** whether to continue with the processing. */
  protected boolean m_ContinueInteraction;
  
  /**
   * Interacts with the user on this boolean option if necessary.
   * 
   * @param option	the current option
   * @param path	the current traversal path
   */
  protected void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
    if (canInteract(option.getCurrentValue()))
      m_ContinueInteraction = doInteract(option.getCurrentValue());
  }
  
  /**
   * Interacts with the user on this argument option if necessary.
   * 
   * @param option	the current option
   * @param path	the current traversal path
   */
  protected void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
    if (canInteract(option.getCurrentValue()))
      m_ContinueInteraction = doInteract(option.getCurrentValue());
  }

  /**
   * Interacts with the user on this class option if necessary.
   * 
   * @param option	the current option
   * @param path	the current traversal path
   */
  protected void handleClassOption(ClassOption option, OptionTraversalPath path) {
    if (canInteract(option.getCurrentValue()))
      m_ContinueInteraction = doInteract(option.getCurrentValue());
  }
  
  /**
   * Returns whether we can recurse into this class.
   * 
   * @param cls		the class to check
   * @return		true if recursion is allowed
   */
  protected abstract boolean canRecurse(Class cls);

  /**
   * Returns whether we can recurse into this object.
   * 
   * @param obj		the object to check
   * @return		true if recursion is allowed
   */
  protected abstract boolean canRecurse(Object obj);

  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process (is a copy of original for
   * 			processors implementing ModifyingProcessor)
   * @see		ModifyingProcessor
   */
  @Override
  protected void processActor(AbstractActor actor) {
    m_ContinueInteraction = true;
    
    actor.getOptionManager().traverse(new OptionTraverser() {
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	if (m_ContinueInteraction)
	  AbstractModifyingInteractiveProcessor.this.handleClassOption(option, path);
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	if (m_ContinueInteraction)
	  AbstractModifyingInteractiveProcessor.this.handleBooleanOption(option, path);
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	if (m_ContinueInteraction)
	  AbstractModifyingInteractiveProcessor.this.handleArgumentOption(option, path);
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return m_ContinueInteraction && AbstractModifyingInteractiveProcessor.this.canRecurse(cls);
      }
      public boolean canRecurse(Object obj) {
        return m_ContinueInteraction && AbstractModifyingInteractiveProcessor.this.canRecurse(obj);
      }
    });

    // cancel? -> ignore changes
    if (!m_ContinueInteraction)
      m_Modified = false;
  }
}

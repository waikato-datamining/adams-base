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
 * AbstractNameUpdater.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.processor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.logging.Level;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionTraverser;
import adams.flow.control.Flow;
import adams.flow.core.AbstractActor;

/**
 * Ancestor for processors that update names of actors.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of
 */
public abstract class AbstractNameUpdater<T>
  extends AbstractModifyingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 7133896476260133469L;

  /** the old name. */
  protected String m_OldName;

  /** the new name. */
  protected String m_NewName;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public abstract String globalInfo();

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"old-name", "oldName",
	"");

    m_OptionManager.add(
	"new-name", "newName",
	"");
  }

  /**
   * Sets the old name to replace.
   *
   * @param value 	the old name
   */
  public void setOldName(String value) {
    m_OldName = value;
    reset();
  }

  /**
   * Returns the the old name to replace.
   *
   * @return 		the old name
   */
  public String getOldName() {
    return m_OldName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String oldNameTipText();

  /**
   * Sets the new name that replaces the old one.
   *
   * @param value 	the new name
   */
  public void setNewName(String value) {
    m_NewName = value;
    reset();
  }

  /**
   * Returns the the new name that replaces the old one.
   *
   * @return 		the new name
   */
  public String getNewName() {
    return m_NewName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String newNameTipText();

  /**
   * Returns whether the base class that we're looking for to perform the
   * replacement on is a match.
   *
   * @param cls		the class to check
   * @return		true if a match
   */
  protected abstract boolean isBaseClassMatch(Class cls);

  /**
   * Checks whether the located object matches the old name that requires
   * replacement.
   *
   * @param old		the old object to check
   * @param oldName	the old name to look for
   * @return		true if a match
   */
  protected abstract boolean isNameMatch(T old, String oldName);

  /**
   * Returns the replacement object.
   *
   * @param old		the old object
   * @param newName	the new name to use
   * @return		the replacement object, null in case of error
   */
  protected abstract T getReplacement(T old, String newName);
  
  /**
   * Processes the specified argument option.
   * 
   * @param option	the option to process
   */
  protected void processArgumentOption(AbstractArgumentOption option) {
    T 		current;
    T		element;
    Method 	method;
    int		i;
    boolean	update;
    
    if (isBaseClassMatch(option.getBaseClass())) {
      current = (T) option.getCurrentValue();
      method  = option.getDescriptor().getWriteMethod();
      update  = false;
      if (option.isMultiple()) {
	for (i = 0; i < Array.getLength(current); i++) {
	  element = (T) Array.get(current, i);
	  if (isNameMatch(element, m_OldName)) {
	    element = getReplacement(element, m_NewName);
	    if (element != null) {
	      Array.set(current, i, element);
	      update = true;
	    }
	  }
	}
      }
      else {
	if (isNameMatch(current, m_OldName)) {
	  current = getReplacement(current, m_NewName);
	  update  = (current != null);
	}
      }
      
      if (update) {
	try {
	  method.invoke(option.getOptionHandler(), current);
	  m_Modified = true;
	}
	catch (Exception e) {
	  getLogger().log(Level.SEVERE, "Failed to update property '" + option.getProperty() + "' of '" + option.getOptionHandler().getClass().getName() + "':", e);
	}
      }
    }
  }
  
  /**
   * Returns whether the traverser can recurse the specified class
   * (base class from a ClassOption).
   * <br><br>
   * Default implementation does not recurse into {@link Flow} actors.
   *
   * @param cls		the class to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  public boolean canRecurse(Class cls) {
    return !cls.equals(Flow.class);
  }

  /**
   * Returns whether the traverser can recurse the specified object.
   * <br><br>
   * Default implementation always returns return value of 
   * {@link #canRecurse(Class)}.
   *
   * @param obj		the Object to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  public boolean canRecurse(Object obj) {
    return canRecurse(obj.getClass());
  }
  
  /**
   * Returns whether class options are treated as argument options and 
   * processed as well.
   * <br><br>
   * Default implementation returns false.
   * 
   * @return		true if can be processed
   */
  protected boolean canProcessClassOptions() {
    return false;
  }
  
  /**
   * Performs the actual processing.
   *
   * @param actor	the actor to process
   */
  @Override
  protected void processActor(AbstractActor actor) {
    actor.getOptionManager().traverse(new OptionTraverser() {
      public void handleClassOption(ClassOption option, OptionTraversalPath path) {
	if (canProcessClassOptions())
	  processArgumentOption(option);
      }
      public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
	// ignored
      }
      public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
	processArgumentOption(option);
      }
      public boolean canHandle(AbstractOption option) {
	return true;
      }
      public boolean canRecurse(Class cls) {
        return AbstractNameUpdater.this.canRecurse(cls);
      }
      public boolean canRecurse(Object obj) {
        return AbstractNameUpdater.this.canRecurse(obj);
      }
    });
  }
}

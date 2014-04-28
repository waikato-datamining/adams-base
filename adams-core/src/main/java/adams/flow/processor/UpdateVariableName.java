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
 * UpdateVariableName.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import adams.core.ClassLocator;
import adams.core.VariableName;
import adams.core.Variables;
import adams.core.base.BaseObject;
import adams.core.option.AbstractArgumentOption;
import adams.flow.control.Flow;
import adams.flow.control.LocalScope;

/**
 <!-- globalinfo-start -->
 * Updates all occurrences of the old variable name with the new one.<br/>
 * Processes either attached variables or variables that are a part of an object that is derived from adams.core.base.BaseObject.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-old-name &lt;java.lang.String&gt; (property: oldName)
 * &nbsp;&nbsp;&nbsp;The old variable name to replace with the new one.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-new-name &lt;java.lang.String&gt; (property: newName)
 * &nbsp;&nbsp;&nbsp;The new variable name that replaces the old one.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateVariableName
  extends AbstractNameUpdater<VariableName> {

  /** for serialization. */
  private static final long serialVersionUID = -5355023022079902959L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
        "Updates all occurrences of the old variable name with the new one.\n"
	+ "Processes either attached variables or variables that are a part "
        + "of an object that is derived from " + BaseObject.class.getName() + ".";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String oldNameTipText() {
    return "The old variable name to replace with the new one.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String newNameTipText() {
    return "The new variable name that replaces the old one.";
  }

  /**
   * Returns whether the base class that we're looking for to perform the
   * replacement on is a match.
   *
   * @param cls		the class to check
   * @return		true if a match
   */
  @Override
  protected boolean isBaseClassMatch(Class cls) {
    return cls.equals(VariableName.class);
  }
  
  /**
   * Returns whether the traverser can recurse the specified class
   * (base class from a ClassOption).
   * <p/>
   * Does not recurse into {@link LocalScope} and {@link Flow} actors.
   *
   * @param cls		the class to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  @Override
  public boolean canRecurse(Class cls) {
    return !cls.equals(LocalScope.class) && !cls.equals(Flow.class);
  }
  
  /**
   * Returns whether class options are treated as argument options and 
   * processed as well.
   * 
   * @return		true if can be processed
   */
  @Override
  protected boolean canProcessClassOptions() {
    return true;
  }

  /**
   * Checks whether the located object matches the old name that requires
   * replacement.
   *
   * @param old		the old object to check
   * @param oldName	the old name to look for
   * @return		true if a match
   */
  @Override
  protected boolean isNameMatch(VariableName old, String oldName) {
    return old.getValue().equals(oldName);
  }

  /**
   * Returns the replacement object.
   *
   * @param old		the old object
   * @param newName	the new name to use
   * @return		the replacement object, null in case of error
   */
  @Override
  protected VariableName getReplacement(VariableName old, String newName) {
    return new VariableName(newName);
  }

  /**
   * Updates variables in BaseObject derived values.
   * 
   * @param option	the option to process
   * @return		true if modified
   */
  protected boolean processBaseObject(AbstractArgumentOption option) {
    boolean		result;
    String		oldName;
    String		newName;
    BaseObject		obj;
    Object		array;
    int			i;
    Method		method;
    boolean		modified;

    result  = false;
    oldName = Variables.padName(m_OldName);
    newName = Variables.padName(m_NewName);

    if (option.isMultiple()) {
      array    = option.getCurrentValue();
      modified = false;
      for (i = 0; i < Array.getLength(array); i++) {
	obj = (BaseObject) Array.get(array, i);
	if (obj.getValue().indexOf(oldName) > -1) {
	  modified = true;
	  obj      = BaseObject.newInstance(option.getBaseClass(), obj.getValue().replace(oldName, newName));
	  Array.set(array, i, obj);
	}
      }
      if (modified) {
	try {
	  method = option.getDescriptor().getWriteMethod();
	  method.invoke(
	      option.getOptionHandler(),
	      new Object[]{array});
	  result = true;
	}
	catch (Exception e) {
	  System.err.println("Failed to update variable names in option " + option.getProperty() + "!");
	  e.printStackTrace();
	}
      }
    }
    else {
      obj = (BaseObject) option.getCurrentValue();
      if (obj.getValue().indexOf(oldName) > -1) {
	obj = BaseObject.newInstance(option.getBaseClass(), obj.getValue().replace(oldName, newName));
	try {
	  method = option.getDescriptor().getWriteMethod();
	  method.invoke(
	      option.getOptionHandler(),
	      new Object[]{obj});
	  result = true;
	}
	catch (Exception e) {
	  System.err.println("Failed to update variable names in option " + option.getProperty() + "!");
	  e.printStackTrace();
	}
      }
    }
    
    return result;
  }
  
  /**
   * Processes the specified argument option.
   * 
   * @param option	the option to process
   */
  @Override
  protected void processArgumentOption(AbstractArgumentOption option) {
    super.processArgumentOption(option);
    
    if (option.isVariableAttached() && option.getVariableName().equals(m_OldName)) {
      option.setVariable(m_NewName);
      m_Modified = true;
    }
    
    // update the string of BaseObject-derived classes
    else if (ClassLocator.isSubclass(BaseObject.class, option.getBaseClass())) {
      m_Modified = m_Modified | processBaseObject(option);
    }
  }
}

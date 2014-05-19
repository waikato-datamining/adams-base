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
 * UpdateStorageName.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.processor;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import adams.core.ClassLocator;
import adams.core.base.BaseObject;
import adams.core.option.AbstractArgumentOption;
import adams.flow.control.Flow;
import adams.flow.control.LocalScopeTrigger;
import adams.flow.control.StorageName;

/**
 <!-- globalinfo-start -->
 * Updates all occurrences of the old storage name with the new one.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 * 
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to 
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 * 
 * <pre>-old-name &lt;java.lang.String&gt; (property: oldName)
 * &nbsp;&nbsp;&nbsp;The old storage name to replace with the new one.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-new-name &lt;java.lang.String&gt; (property: newName)
 * &nbsp;&nbsp;&nbsp;The new storage name that replaces the old one.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class UpdateStorageName
  extends AbstractNameUpdater<StorageName> {

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
        "Updates all occurrences of the old storage name with the new one.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String oldNameTipText() {
    return "The old storage name to replace with the new one.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String newNameTipText() {
    return "The new storage name that replaces the old one.";
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
    return cls.equals(StorageName.class);
  }
  
  /**
   * Returns whether the traverser can recurse the specified class
   * (base class from a ClassOption).
   * <p/>
   * Does not recurse into {@link LocalScopeTrigger} and {@link Flow} actors.
   *
   * @param cls		the class to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  @Override
  public boolean canRecurse(Class cls) {
    return !cls.equals(LocalScopeTrigger.class) && !cls.equals(Flow.class);
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
  protected boolean isNameMatch(StorageName old, String oldName) {
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
  protected StorageName getReplacement(StorageName old, String newName) {
    return new StorageName(newName);
  }

  /**
   * Updates storage names in BaseObject derived values.
   * 
   * @param option	the option to process
   * @return		true if modified
   */
  protected boolean processBaseObject(AbstractArgumentOption option) {
    boolean		result;
    StorageName		oldName;
    BaseObject		obj;
    Object		array;
    int			i;
    Method		method;
    boolean		modified;

    result  = false;
    oldName = new StorageName(m_OldName);

    if (option.isMultiple()) {
      array    = option.getCurrentValue();
      modified = false;
      for (i = 0; i < Array.getLength(array); i++) {
	obj = (BaseObject) Array.get(array, i);
	if (obj.equals(oldName)) {
	  modified = true;
	  obj      = BaseObject.newInstance(option.getBaseClass(), obj.getValue().replace(m_OldName, m_NewName));
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
	  System.err.println("Failed to update storage names in option " + option.getProperty() + "!");
	  e.printStackTrace();
	}
      }
    }
    else {
      obj = (BaseObject) option.getCurrentValue();
      if (obj.equals(oldName)) {
	obj = BaseObject.newInstance(option.getBaseClass(), obj.getValue().replace(m_OldName, m_NewName));
	try {
	  method = option.getDescriptor().getWriteMethod();
	  method.invoke(
	      option.getOptionHandler(),
	      new Object[]{obj});
	  result = true;
	}
	catch (Exception e) {
	  System.err.println("Failed to update storage names in option " + option.getProperty() + "!");
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
    
    // update the string of BaseObject-derived classes
    if (ClassLocator.isSubclass(BaseObject.class, option.getBaseClass())) {
      m_Modified = m_Modified | processBaseObject(option);
    }
  }
}

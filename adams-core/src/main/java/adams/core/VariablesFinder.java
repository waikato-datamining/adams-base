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
 * VariablesFinder.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core;

import java.util.HashSet;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.AbstractOptionTraverserWithResult;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;

/**
 * Option traverser for locating variables.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariablesFinder
  extends AbstractOptionTraverserWithResult<HashSet<String>> {
  
  /** for serialization. */
  private static final long serialVersionUID = 7547795348352685187L;

  /** for storing the variables. */
  protected HashSet<String> variables;

  /** the inspection class. */
  protected VariablesInspectionHandler m_Inspection;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Option traverser for locating variables.";
  }

  /**
   * Initializes the members.
   */
  @Override
  @SuppressWarnings("serial")
  protected void initialize() {
    super.initialize();
    
    m_Inspection = new VariablesInspectionHandler() {
      @Override
      public boolean canInspectOptions(Class cls) {
        return true;
      }
    };
  }
  
  /**
   * Sets the inspection handler.
   */
  public void setInspection(VariablesInspectionHandler value) {
    m_Inspection = value;
  }
  
  /**
   * Resets the result before traversing.
   */
  @Override
  public void resetResult() {
    variables = new HashSet<String>();
  }
  
  /**
   * Handles the encountered boolean option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  @Override
  public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
    handleArgumentOption(option, path);
  }
  
  /**
   * Handles the encountered class option. Precedence over argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  @Override
  public void handleClassOption(ClassOption option, OptionTraversalPath path) {
    handleArgumentOption(option, path);
  }
  
  /**
   * Handles the encountered argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  @Override
  public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
    if (option.isVariableAttached() && !isSkipped(option))
      variables.add(option.getVariableName());
  }
  
  /**
   * Returns whether the traverser is allowed to "handle" this option.
   *
   * @param option	the option to check whether it can be handled
   * @return		true if handling via 
   * 			{@link #handleArgumentOption(AbstractArgumentOption, OptionTraversalPath)},
   * 			{@link #handleClassOption(ClassOption, OptionTraversalPath)} or
   * 			{@link #handleBooleanOption(BooleanOption, OptionTraversalPath)} 
   * 			is allowed
   */
  @Override
  public boolean canHandle(AbstractOption option) {
    return true;
  }
  
  /**
   * Returns whether the traverser can recurse the specified class
   * (base class from a ClassOption).
   *
   * @param cls		the class to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  @Override
  public boolean canRecurse(Class cls) {
    return m_Inspection.canInspectOptions(cls);
  }
  
  /**
   * Returns whether the traverser can recurse the specified object.
   *
   * @param obj		the Object to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  @Override
  public boolean canRecurse(Object obj) {
    return canRecurse(obj.getClass());
  }
  
  /**
   * Checks whether to skip this option.
   */
  protected boolean isSkipped(AbstractOption option) {
    return false;
  }

  /**
   * Returns the result of the traversal.
   *
   * @return		the result
   */
  @Override
  public HashSet<String> getResult() {
    return variables;
  }
}

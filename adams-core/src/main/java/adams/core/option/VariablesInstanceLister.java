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
 * VariablesInstanceLister.java
 * Copyright (C) 2012-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import adams.core.Variables;
import adams.flow.core.AbstractActor;

/**
 <!-- globalinfo-start -->
 * Lists the hashcodes of the adams.core.Variables objects in use by the adams.core.option.OptionManager objects.
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
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class VariablesInstanceLister
  extends AbstractOptionTraverserWithResult<String> {

  /** for serialization. */
  private static final long serialVersionUID = -6561961647781822476L;
  
  /** for storing the result. */
  protected StringBuilder m_Result;
  
  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Lists the hashcodes of the " + Variables.class.getName() + " objects "
	+ "in use by the " + OptionManager.class.getName() + " objects.";
  }
  
  /**
   * Resets the result before traversing.
   */
  @Override
  public void resetResult() {
    m_Result = new StringBuilder();
    m_Result.append("Path");
    m_Result.append("\t");
    m_Result.append("Class");
    m_Result.append("\t");
    m_Result.append("Property");
    m_Result.append("\t");
    m_Result.append("Variables (OptionManager/Actor)");
    m_Result.append("\n");
  }

  /**
   * Handles the encountered boolean option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  @Override
  public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
    m_Result.append(path.getPath());
    m_Result.append("\t");
    m_Result.append(option.getOptionHandler().getClass().getName());
    m_Result.append("\t");
    m_Result.append(option.getProperty());
    m_Result.append("\t");
    m_Result.append("-/-");
    m_Result.append("\n");
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
    Variables	vars;
    
    m_Result.append(path.getPath());
    m_Result.append("\t");
    m_Result.append(option.getOptionHandler().getClass().getName());
    m_Result.append("\t");
    m_Result.append(option.getProperty());
    m_Result.append("\t");
    m_Result.append(option.getOwner().getVariables().hashCode());
    if (option.getOptionHandler() instanceof AbstractActor) {
      vars = ((AbstractActor) option.getOptionHandler()).getVariables();
      if (vars != null)
	m_Result.append("/" + vars.hashCode());
      else
	m_Result.append("/-");
    }
    else {
      m_Result.append("/-");
    }
    m_Result.append("\n");
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
    return true;
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
   * Returns the result of the traversal.
   *
   * @return		the result
   */
  @Override
  public String getResult() {
    return m_Result.toString();
  }
}

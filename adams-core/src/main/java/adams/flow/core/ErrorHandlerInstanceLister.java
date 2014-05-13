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
 * ErrorHandlerInstance.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.core;

import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.AbstractOptionTraverserWithResult;
import adams.core.option.BooleanOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;

/**
 <!-- globalinfo-start -->
 * Lists the hashcodes of the adams.flow.core.ErrorHandler objects in use by actors.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 7042 $
 */
public class ErrorHandlerInstanceLister
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
	"Lists the hashcodes of the " + ErrorHandler.class.getName() + " objects "
	+ "in use by actors.";
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
    m_Result.append("ErrorHandler (class)");
    m_Result.append("\t");
    m_Result.append("ErrorHandler (hashcode)");
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
    if (option.getOptionHandler() instanceof Actor) {
      m_Result.append(path.getPath());
      m_Result.append("\t");
      m_Result.append(option.getOptionHandler().getClass().getName());
      m_Result.append("\t");
      m_Result.append(option.getProperty());
      m_Result.append("\t");
      m_Result.append(((ErrorHandler) option.getOptionHandler()).getClass().getName());
      m_Result.append("\t");
      m_Result.append(((ErrorHandler) option.getOptionHandler()).hashCode());
      m_Result.append("\n");
    }
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

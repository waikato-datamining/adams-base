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
 * FileLister.java
 * Copyright (C) 2012-2013 University of Waikato, Hamilton, New Zealand
 */
package adams.core.option;

import java.io.File;
import java.lang.reflect.Array;

import adams.core.ClassLocator;
import adams.core.VariablesHandler;

/**
 <!-- globalinfo-start -->
 * Lists all file objects.
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
public class FileLister
  extends AbstractOptionHandler
  implements OptionTraverserWithResult<String> {

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
    return "Lists all file objects.";
  }

  /**
   * Resets the object.
   */
  @Override
  protected void reset() {
    super.reset();
    
    resetResult();
  }
  
  /**
   * Resets the result before traversing.
   */
  public void resetResult() {
    m_Result = new StringBuilder();
    m_Result.append("Path");
    m_Result.append("\t");
    m_Result.append("Class");
    m_Result.append("\t");
    m_Result.append("Property");
    m_Result.append("\t");
    m_Result.append("File");
    m_Result.append("\n");
  }

  /**
   * Handles the encountered boolean option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  public void handleBooleanOption(BooleanOption option, OptionTraversalPath path) {
    // ignored
  }

  /**
   * Handles the encountered class option. Precedence over argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  public void handleClassOption(ClassOption option, OptionTraversalPath path) {
    // ignored
  }

  /**
   * Handles the encountered argument option.
   *
   * @param option	the option to handle
   * @param path	the property path so far
   */
  public void handleArgumentOption(AbstractArgumentOption option, OptionTraversalPath path) {
    Object	array;
    int		i;
    
    if (ClassLocator.isSubclass(File.class, option.getBaseClass())) {
      if (option.isMultiple()) {
	array = option.getCurrentValue();
	for (i = 0; i < Array.getLength(array); i++) {
	  m_Result.append(path.getPath());
	  m_Result.append("\t");
	  m_Result.append(option.getOptionHandler().getClass().getName());
	  m_Result.append("\t");
	  m_Result.append(option.getProperty() + "[" + i + "]");
	  m_Result.append("\t");
	  m_Result.append(option.getCurrentValue());
	  m_Result.append("\n");
	}
      }
      else {
	m_Result.append(path.getPath());
	m_Result.append("\t");
	m_Result.append(option.getOptionHandler().getClass().getName());
	m_Result.append("\t");
	m_Result.append(option.getProperty());
	m_Result.append("\t");
	m_Result.append(option.getCurrentValue());
	m_Result.append("\n");
      }
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
  public boolean canRecurse(Class cls) {
    return !ClassLocator.hasInterface(VariablesHandler.class, cls);
  }

  /**
   * Returns whether the traverser can recurse the specified object.
   *
   * @param obj		the Object to determine for whether recursing is
   * 			possible or not
   * @return		true if to traverse the options recursively
   */
  public boolean canRecurse(Object obj) {
    return canRecurse(obj.getClass());
  }

  /**
   * Returns the result of the traversal.
   *
   * @return		the result
   */
  public String getResult() {
    return m_Result.toString();
  }
}

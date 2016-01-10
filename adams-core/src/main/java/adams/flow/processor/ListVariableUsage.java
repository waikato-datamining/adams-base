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
 * ListVariableUsage.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.VariableName;
import adams.core.Variables;
import adams.core.base.BaseObject;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionTraversalPath;

/**
 <!-- globalinfo-start -->
 * Lists all the actors where the specified variable is used.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The variable name to look for.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListVariableUsage
  extends AbstractListNameUsage<VariableName> {

  private static final long serialVersionUID = -6340700367008421185L;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists all the actors where the specified variable is used.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String nameTipText() {
    return "The variable name to look for.";
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Variable '" + m_Name + "'";
  }

  /**
   * Checks whether the located object matches the name that we are looking for.
   *
   * @param obj		the object to check
   * @return		true if a match
   */
  @Override
  protected boolean isNameMatch(Object obj) {
    if (obj instanceof VariableName) {
      return ((VariableName) obj).getValue().equals(m_Name);
    }
    else if (obj instanceof String) {
      return ((String) obj).contains(Variables.padName(m_Name));
    }
    else if (obj instanceof BaseObject) {
      return ((BaseObject) obj).getValue().contains(Variables.padName(m_Name));
    }
    else {
      return false;
    }
  }

  /**
   * Checks whether the object is valid and should be added to the list.
   *
   * @param option	the current option
   * @param obj		the object to check
   * @param path	the traversal path of properties
   * @return		true if valid
   */
  protected boolean isValid(AbstractOption option, Object obj, OptionTraversalPath path) {
    boolean			result;
    AbstractArgumentOption	arg;

    result = super.isValid(option, obj, path);

    if (!result && (option instanceof AbstractArgumentOption)) {
      arg    = (AbstractArgumentOption) option;
      result = (arg.isVariableAttached() && isNameMatch(arg.getVariable()));
    }

    return result;
  }

  @Override
  protected String getHeader() {
    return "Locations referencing variable '" + m_Name + "':";
  }
}

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
 * ListTODOs.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.base.BaseRegExp;
import adams.core.option.AbstractOption;
import adams.core.option.OptionTraversalPath;
import adams.flow.core.Actor;

/**
 <!-- globalinfo-start -->
 * Lists all the actors with TODOs in either name or annotation.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression to use for locating the TODOs in name&#47;annotation.
 * &nbsp;&nbsp;&nbsp;default: .*TODO.*
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class ListTODOs
  extends AbstractListingProcessor {

  private static final long serialVersionUID = -6340700367008421185L;

  /** the regular expression to use for matching the TODOs. */
  protected BaseRegExp m_RegExp;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists all the actors with TODOs in either name or annotation.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(".*TODO.*"));
  }

  /**
   * Sets the regular expression to use for locating TODOs.
   *
   * @param value 	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression to use for locating TODOs.
   *
   * @return 		the expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression to use for locating the TODOs in name/annotation.";
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "TODOs";
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
    return (obj instanceof Actor)
      && (m_RegExp.isMatch(((Actor) obj).getName()) || m_RegExp.isMatch(((Actor) obj).getAnnotations().getValue()));
  }

  /**
   * Returns the string representation of the object that is added to the list.
   * <br><br>
   * Default implementation only calls the <code>toString()</code> method.
   *
   * @param option	the current option
   * @param obj		the object to turn into a string
   * @param path	the traversal path of properties
   * @return		the string representation, null if to ignore the item
   */
  protected String objectToString(AbstractOption option, Object obj, OptionTraversalPath path) {
    if (obj instanceof Actor)
      return ((Actor) obj).getFullName();
    else
      return obj.toString();
  }

  /**
   * Returns whether the list should be sorted.
   *
   * @return		true if the list should get sorted
   */
  @Override
  protected boolean isSortedList() {
    return true;
  }

  /**
   * Returns whether the list should not contain any duplicates.
   *
   * @return		true if the list contains no duplicates
   */
  @Override
  protected boolean isUniqueList() {
    return true;
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   *
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Current TODOs ('" + m_RegExp.getValue() + "'):";
  }
}

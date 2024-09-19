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

/*
 * ListClassUsage.java
 * Copyright (C) 2024 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.base.BaseClassname;
import adams.core.option.AbstractOption;
import adams.core.option.ClassOption;
import adams.core.option.OptionTraversalPath;
import adams.flow.condition.bool.BooleanCondition;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 <!-- globalinfo-start -->
 * Looks for all the occurrences of the specified class.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 * <pre>-find &lt;adams.core.base.BaseClassname&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The class to look for.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.condition.bool.BooleanCondition
 * </pre>
 *
 * <pre>-allow-derived-classes &lt;boolean&gt; (property: allowDerivedClasses)
 * &nbsp;&nbsp;&nbsp;Whether to include derived classes as well or only exact class matches.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ListClassUsage
  extends AbstractActorListingProcessor {

  private static final long serialVersionUID = 3925071321732277210L;

  /** the class to look for. */
  protected BaseClassname m_Find;

  /** the actual class to look for. */
  protected transient Class m_Actual;

  /** whether to allow derived classes. */
  protected boolean m_AllowDerivedClasses;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Looks for all the occurrences of the specified class.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "find", "find",
      new BaseClassname(BooleanCondition.class));

    m_OptionManager.add(
      "allow-derived-classes", "allowDerivedClasses",
      false);
  }

  /**
   * Sets the class to look for.
   *
   * @param value 	the class
   */
  public void setFind(BaseClassname value) {
    if (value != null) {
      m_Find = value;
      reset();
    }
  }

  /**
   * Returns the class to look for.
   *
   * @return 		the class
   */
  public BaseClassname getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The class to look for.";
  }

  /**
   * Sets whether derived classes can be listed as well.
   *
   * @param value 	true if allowed
   */
  public void setAllowDerivedClasses(boolean value) {
    m_AllowDerivedClasses = value;
    reset();
  }

  /**
   * Returns whether derived classes can be listed as well.
   *
   * @return 		true if allowed
   */
  public boolean getAllowDerivedClasses() {
    return m_AllowDerivedClasses;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String allowDerivedClassesTipText() {
    return "Whether to include derived classes as well or only exact class matches.";
  }

  /**
   * Checks whether the object is valid and should be added to the list.
   *
   * @param option	the current option
   * @param obj		the object to check
   * @param path	the traversal path of properties
   * @return		true if valid
   */
  @Override
  protected boolean isValid(AbstractOption option, Object obj, OptionTraversalPath path) {
    if (option instanceof ClassOption) {
      if (m_Actual == null)
	m_Actual = m_Find.classValue();
      if (m_AllowDerivedClasses)
	return ClassLocator.matches(m_Actual, ((ClassOption) option).getBaseClass());
      else
	return m_Actual.equals(((ClassOption) option).getBaseClass());
    }
    else {
      return false;
    }
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   *
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Locations where " + m_Find + " was found:";
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Locations for " + m_Find;
  }
}

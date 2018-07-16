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
 * ListObjectCommandlines.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.base.BaseClassname;
import adams.core.option.AbstractOption;
import adams.core.option.OptionTraversalPath;
import adams.core.option.OptionUtils;
import adams.flow.core.Actor;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ListObjectCommandlines
  extends AbstractListingProcessor {

  private static final long serialVersionUID = -7043901878842702355L;

  /** the classname to look for. */
  protected BaseClassname m_Classname;

  /** the class we are looking for. */
  protected transient Class m_Class;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Lists the command-lines of the objects of the specified superclass.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"classname", "classname",
	new BaseClassname(Actor.class));
  }

  /**
   * Resets the scheme.
   */
  @Override
  public void reset() {
    super.reset();

    m_Class = null;
  }

  /**
   * Sets the class to look for.
   *
   * @param value 	the class
   */
  public void setClassname(BaseClassname value) {
    m_Classname = value;
    reset();
  }

  /**
   * Returns the class to look for.
   *
   * @return 		the class
   */
  public BaseClassname getClassname() {
    return m_Classname;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String classnameTipText() {
    return "The class to look for.";
  }

  /**
   * Returns the string representation of the object that is to be added to the list.
   *
   * @param option	the current option
   * @param obj		the object to turn into a string
   * @param path	the traversal path of properties
   * @return		the string representation, null if to ignore the item
   */
  protected String objectToString(AbstractOption option, Object obj, OptionTraversalPath path) {
    return OptionUtils.getCommandLine(obj);
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
    if (m_Class == null)
      m_Class = m_Classname.classValue();

    return ClassLocator.matches(m_Class, obj.getClass());
  }

  /**
   * Returns whether the list should be sorted.
   *
   * @return		true if the list should get sorted
   */
  @Override
  protected boolean isSortedList() {
    return false;
  }

  /**
   * Returns whether the list should not contain any duplicates.
   *
   * @return		true if the list contains no duplicates
   */
  @Override
  protected boolean isUniqueList() {
    return false;
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  @Override
  public String getTitle() {
    return "Object command-lines";
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   *
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Command-lines of " + m_Classname + " objects";
  }
}

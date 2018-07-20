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
 * Lists the command-lines of the objects of the specified superclass.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-classname &lt;adams.core.base.BaseClassname&gt; (property: classname)
 * &nbsp;&nbsp;&nbsp;The class to look for.
 * &nbsp;&nbsp;&nbsp;default: adams.flow.core.Actor
 * </pre>
 *
 * <pre>-add-full-actor-name &lt;boolean&gt; (property: addFullActorName)
 * &nbsp;&nbsp;&nbsp;Whether to add the full actor name (separated by tab), if the enclosing
 * &nbsp;&nbsp;&nbsp;object is of type adams.flow.core.Actor.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ListObjectCommandlines
  extends AbstractListingProcessor {

  private static final long serialVersionUID = -7043901878842702355L;

  /** the classname to look for. */
  protected BaseClassname m_Classname;

  /** whether to add the full name of an enclosing actor (if available). */
  protected boolean m_AddFullActorName;

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

    m_OptionManager.add(
	"add-full-actor-name", "addFullActorName",
	false);
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
   * Sets whether to add the full name of the enclosing actor (if available).
   *
   * @param value 	the class
   */
  public void setAddFullActorName(boolean value) {
    m_AddFullActorName = value;
    reset();
  }

  /**
   * Returns whether to add the full name of the enclosing actor (if available).
   *
   * @return 		true if to add
   */
  public boolean getAddFullActorName() {
    return m_AddFullActorName;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String addFullActorNameTipText() {
    return "Whether to add the full actor name (separated by tab), if the enclosing object is of type " + Actor.class.getName() + ".";
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
    String	result;

    result = "";
    if (m_AddFullActorName) {
      if (option.getOptionHandler() instanceof Actor)
	result += ((Actor) option.getOptionHandler()).getFullName();
      else
        result += "-";
      result += "\t";
    }
    result += OptionUtils.getCommandLine(obj);

    return result;
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

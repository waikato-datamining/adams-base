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
 * FindText.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.processor;

import adams.core.base.BaseObject;
import adams.core.option.AbstractArgumentOption;
import adams.core.option.AbstractOption;
import adams.core.option.OptionTraversalPath;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 <!-- globalinfo-start -->
 * Lists all the actors where the specified text is found in one of the options (case-insensitive). Uses searches String and BaseObject derived options
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-find &lt;java.lang.String&gt; (property: find)
 * &nbsp;&nbsp;&nbsp;The text to look for.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FindText
  extends AbstractActorListingProcessor {

  private static final long serialVersionUID = -6340700367008421185L;

  /** the text to find. */
  protected String m_Find;

  /** the lower case search string. */
  protected String m_ActualFind;

  /**
   * Returns a string describing the object.
   *
   * @return 		a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Lists all the actors where the specified text is found in one of "
	+ "the options (case-insensitive). Uses searches String and "
	+ "BaseObject derived options";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"find", "find",
	"");
  }

  /**
   * Sets the text to look for.
   *
   * @param value 	the text
   */
  public void setFind(String value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the text to look for.
   *
   * @return 		the text
   */
  public String getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The text to look for.";
  }

  /**
   * Returns the title for the dialog.
   *
   * @return		the title
   */
  public String getTitle() {
    return "Locations for '" + m_Find + "'";
  }

  /**
   * Initializes the list.
   */
  @Override
  protected void initializeList() {
    super.initializeList();
    m_ActualFind = m_Find.toLowerCase();
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
    String			text;

    result = false;

    if (option instanceof AbstractArgumentOption) {
      arg  = (AbstractArgumentOption) option;
      text = null;
      if (ClassLocator.isSubclass(BaseObject.class, arg.getBaseClass()))
	text = ((BaseObject) obj).getValue().toLowerCase();
      else if (ClassLocator.isSubclass(String.class, arg.getBaseClass()))
	text = ((String) obj).toLowerCase();
      if (text != null)
	result = text.contains(m_ActualFind);
    }

    return result;
  }

  /**
   * Returns the header to use in the dialog, i.e., the one-liner that
   * explains the output.
   *
   * @return		the header, null if no header available
   */
  @Override
  protected String getHeader() {
    return "Actors with options containing text '" + m_Find + "':";
  }
}

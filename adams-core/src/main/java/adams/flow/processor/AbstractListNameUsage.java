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
 * AbstractListNameUsage.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton,  Zealand
 */
package adams.flow.processor;

import adams.core.option.AbstractOption;
import adams.core.option.OptionTraversalPath;

/**
 * Ancestor for processors that locate usages of a certain name.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <T> the type of
 */
public abstract class AbstractListNameUsage<T>
  extends AbstractActorListingProcessor {

  /** for serialization. */
  private static final long serialVersionUID = 7133896476260133469L;

  /** the name to look for. */
  protected String m_Name;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	"name", "name",
	"");
  }

  /**
   * Sets the name to look for.
   *
   * @param value 	the name
   */
  public void setName(String value) {
    m_Name = value;
    reset();
  }

  /**
   * Returns the name to look for.
   *
   * @return 		the name
   */
  public String getName() {
    return m_Name;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public abstract String nameTipText();

  /**
   * Checks whether the located object matches the name that we are looking for.
   *
   * @param obj		the object to check
   * @return		true if a match
   */
  protected abstract boolean isNameMatch(Object obj);

  /**
   * Checks whether the object is valid and should be added to the list.
   *
   * @param option	the current option
   * @param obj		the object to check
   * @param path	the traversal path of properties
   * @return		true if valid
   */
  protected boolean isValid(AbstractOption option, Object obj, OptionTraversalPath path) {
    return isNameMatch(obj);
  }
}

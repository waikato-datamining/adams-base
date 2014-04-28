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
 * AbstractConditionalIdSupplier.java
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.ClassLocator;
import adams.core.QuickInfoHelper;
import adams.db.AbstractConditions;
import adams.db.AbstractLimitedConditions;

/**
 * Abstract ancestor for ID suppliers that use a conditions object.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractConditionalIdSupplier
  extends AbstractDatabaseIdSupplier 
  implements ConditionalIdSupplier {

  /** for serialization. */
  private static final long serialVersionUID = -8462709950859959951L;

  /** the retrieval conditions. */
  protected AbstractConditions m_Conditions;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "conditions", "conditions",
	    getDefaultConditions());
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;
    int		limit;
    String	value;

    result  = super.getQuickInfo();
    result += QuickInfoHelper.toString(this, "conditions", m_Conditions, ", using ");

    if ((m_Conditions instanceof AbstractLimitedConditions) && !QuickInfoHelper.hasVariable(this, "conditions")) {
      limit = ((AbstractLimitedConditions) m_Conditions).getLimit();
      if (limit == -1)
	value = ", unlimited";
      else
	value = ", max: " + limit;
      result += QuickInfoHelper.toString(m_Conditions, "limit", value);
    }

    return result;
  }

  /**
   * Returns the default conditions.
   *
   * @return		the default conditions
   */
  protected abstract AbstractConditions getDefaultConditions();

  /**
   * Returns the accepted classes for condition objects.
   * <p/>
   * The default implementation just returns the class of the default condition
   * object.
   *
   * @return		the accepted classes
   * @see		#getDefaultConditions()
   */
  protected Class[] getAcceptedConditions() {
    return new Class[]{getDefaultConditions().getClass()};
  }

  /**
   * Sets the conditions container to use for retrieving the spectra.
   *
   * @param value 	the conditions
   */
  @Override
  public void setConditions(AbstractConditions value) {
    Class[]	classes;
    int		i;
    boolean	found;
    String	classesStr;

    classes = getAcceptedConditions();
    found   = false;
    for (i = 0; i < classes.length; i++) {
      if (classes[i].isInterface()) {
	if (ClassLocator.hasInterface(classes[i], value.getClass()))
	  found = true;
      }
      else {
	if (ClassLocator.isSubclass(classes[i], value.getClass()))
	  found = true;
      }
      if (found) {
	m_Conditions = value;
	reset();
      }
    }

    if (!found) {
      classesStr = "";
      for (i = 0; i < classes.length; i++) {
	if (i > 0)
	  classesStr += ", ";
	classesStr += classes[i].getName();
      }
      getLogger().severe(
	  "Only the folloing container(s) are allowed: " + classesStr);
    }
  }

  /**
   * Returns the conditions container to use for retrieving the spectra.
   *
   * @return 		the conditions
   */
  @Override
  public AbstractConditions getConditions() {
    return m_Conditions;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String conditionsTipText() {
    return "The conditions for retrieving the data from the database.";
  }
}

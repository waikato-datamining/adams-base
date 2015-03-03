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
 * AbstractLimitedConditions.java
 * Copyright (C) 2010-2013 University of Waikato, Hamilton, New Zealand
 */

package adams.db;

/**
 * Abstract ancestor for conditions that limit the number of records retrieved.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractLimitedConditions
  extends AbstractConditions {

  /** for serialization. */
  private static final long serialVersionUID = -8685225144623746238L;

  /** the maximum number of records. */
  protected int m_Limit;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "limit", "limit",
	    getDefaultLimit(), -1, null);
  }

  /**
   * Returns the default limit.
   * 
   * @return 		the default limit
   */
  protected int getDefaultLimit() {
    return 10000;
  }
  
  /**
   * Sets the maximum number of records to retrieve.
   *
   * @param value 	the limit to use
   */
  public void setLimit(int value) {
    if (value >= -1) {
      m_Limit = value;
      reset();
    }
    else {
      getLogger().severe(
	  "The limit has to be at least -1 (unlimited), provided: " + value);
    }
  }

  /**
   * Returns the maximum number of records to retrieve.
   *
   * @return 		the limit
   */
  public int getLimit() {
    return m_Limit;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String limitTipText() {
    return "The maximum number of records to retrieve.";
  }
}

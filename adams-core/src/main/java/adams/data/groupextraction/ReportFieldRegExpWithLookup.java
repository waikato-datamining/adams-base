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
 * ReportFieldRegExpWithLookup.java
 * Copyright (C) 2026 University of Waikato, Hamilton, New Zealand
 */

package adams.data.groupextraction;

import adams.core.base.BaseKeyValuePair;

import java.util.HashMap;
import java.util.Map;

/**
 * The initial group is the value of the regexp group applied to the specified report field.
 * This group then gets pushed through the supplied lookups.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class ReportFieldRegExpWithLookup
  extends ReportFieldRegExp {

  private static final long serialVersionUID = -3257088551372058788L;

  /** the lookup mapping. */
  protected BaseKeyValuePair[] m_Lookups;

  /** the lookup. */
  protected transient Map<String, String> m_Mapping;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "The initial group is the value of the regexp group applied to the specified report field. This group then gets pushed through the supplied lookups.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "lookup", "lookups",
      new BaseKeyValuePair[0]);
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();

    m_Mapping = null;
  }

  /**
   * Sets the lookups to apply to the initially extracted group.
   *
   * @param value	the lookups
   */
  public void setLookups(BaseKeyValuePair[] value) {
    m_Lookups = value;
    reset();
  }

  /**
   * Returns the lookups to apply to the initially extracted group.
   *
   * @return		the lookups
   */
  public BaseKeyValuePair[] getLookups() {
    return m_Lookups;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String lookupsTipText() {
    return "The lookups to apply to the initially extracted group: if the key matches the group, the associated value is used instead.";
  }

  /**
   * Hook method for post-processing the group.
   *
   * @param group	the extract group
   * @return		the (potentially) updated group
   */
  @Override
  protected String postProcessGroup(String group) {
    String	result;

    result = super.postProcessGroup(group);

    if (result != null) {
      if (m_Mapping == null) {
	m_Mapping = new HashMap<>();
	for (BaseKeyValuePair lookup: m_Lookups)
	  m_Mapping.put(lookup.getPairKey(), lookup.getPairValue());
      }

      if (m_Mapping.containsKey(result))
	result = m_Mapping.get(result);
    }

    return result;
  }
}

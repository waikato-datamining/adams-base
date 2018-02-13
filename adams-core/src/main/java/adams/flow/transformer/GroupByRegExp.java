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
 * GroupByRegExp.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 <!-- globalinfo-start -->
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 <!-- options-end -->
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GroupByRegExp
  extends AbstractArrayProvider {

  private static final long serialVersionUID = -35393265150652536L;

  /** the regular expression. */
  protected BaseRegExp m_Find;

  /** the grouping. */
  protected String m_Grouping;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Groups the incoming strings into sub-groups. For that purpose, "
      + "the regular expression is applied and specified grouping expression "
      + "is used as key for each group.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "find", "find",
      new BaseRegExp("(.*)"));

    m_OptionManager.add(
      "grouping", "grouping",
      "$1");
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "If enabled, the groups are output as array instead of one-by-one.";
  }

  /**
   * Sets the string to find (regular expression).
   *
   * @param value	the string
   */
  public void setFind(BaseRegExp value) {
    m_Find = value;
    reset();
  }

  /**
   * Returns the string to find (regular expression).
   *
   * @return		the string
   */
  public BaseRegExp getFind() {
    return m_Find;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String findTipText() {
    return "The string to find (a regular expression).";
  }

  /**
   * Sets the string to generate the grouping string from, can contain
   * regexp groups like $0..$n.
   *
   * @param value	the string
   */
  public void setGrouping(String value) {
    m_Grouping = value;
    reset();
  }

  /**
   * Returns the string to generate the grouping string from, can contain
   * regexp groups like $0..$n..
   *
   * @return		the string
   */
  public String getGrouping() {
    return m_Grouping;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupingTipText() {
    return "The expression to generate the grouping string from; can contain regexp groups like $0..$n.";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;

    result = QuickInfoHelper.toString(this, "find", m_Find, "find: ");
    result += QuickInfoHelper.toString(this, "grouping", m_Grouping, ", grouping: ");
    
    return result;
  }

  /**
   * Returns the class that the consumer accepts.
   *
   * @return		the Class of objects that can be processed
   */
  @Override
  public Class[] accepts() {
    return new Class[]{String[].class};
  }

  /**
   * Returns the base class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String[].class;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String			result;
    String[]			items;
    Map<String,List<String>> 	groups;
    String			group;

    result = null;

    m_Queue.clear();

    // generate groups
    items  = m_InputToken.getPayload(String[].class);
    groups = new HashMap<>();
    try {
      for (String item : items) {
	group = item.replaceAll(m_Find.getValue(), m_Grouping);
	if (!groups.containsKey(group))
	  groups.put(group, new ArrayList<>());
	groups.get(group).add(item);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to group strings, using regexp '" + m_Find + "' and grouping '" + m_Grouping + "'!", e);
    }

    // fill queue
    if (result == null) {
      for (String key : groups.keySet())
	m_Queue.add(groups.get(key).toArray(new String[0]));
    }

    return result;
  }
}

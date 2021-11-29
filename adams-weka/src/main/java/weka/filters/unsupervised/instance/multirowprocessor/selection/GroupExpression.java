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
 * GroupExpression.java
 * Copyright (C) 2021 University of Waikato, Hamilton, New Zealand
 */

package weka.filters.unsupervised.instance.multirowprocessor.selection;

import adams.core.base.BaseRegExp;
import adams.data.weka.WekaAttributeIndex;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instances;
import weka.core.WekaOptionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Identifies groups in strings using regular expressions.
 * If the group attribute is numeric, then the values get turned into strings first.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class GroupExpression
    extends AbstractRowSelection {

  private static final long serialVersionUID = -8519118208205929299L;

  protected static String INDEX = "index";

  /** the index to use for grouping. */
  protected WekaAttributeIndex m_Index = getDefaultIndex();

  protected static String REGEXP = "regexp";

  /** the regular expression for the nominal/string attribute. */
  protected BaseRegExp m_RegExp = getDefaultRegExp();

  protected static String GROUP = "group";

  public static final String DEFAULT_GROUP = "$0";

  /** the group expression. */
  protected String m_Group = DEFAULT_GROUP;

  /**
   * Returns a string describing the row selection scheme.
   *
   * @return a description suitable for displaying in the explorer/experimenter
   * gui
   */
  @Override
  public String globalInfo() {
    return "Identifies groups in strings using regular expressions.\n"
	+ "If the group attribute is numeric, then the values get turned into strings first.";
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return 		an enumeration of all the available options.
   */
  public Enumeration listOptions() {
    Vector result;

    result = new Vector();

    WekaOptionUtils.addOption(result, indexTipText(), getDefaultIndex(), INDEX);
    WekaOptionUtils.addOption(result, regExpTipText(), getDefaultRegExp().getValue(), REGEXP);
    WekaOptionUtils.addOption(result, groupTipText(), DEFAULT_GROUP, GROUP);
    WekaOptionUtils.add(result, super.listOptions());

    return WekaOptionUtils.toEnumeration(result);
  }

  /**
   * Parses a given list of options.
   *
   * @param options 	the list of options as an array of strings
   * @throws Exception 	if an option is not supported
   */
  public void setOptions(String[] options) throws Exception {
    setIndex((WekaAttributeIndex) WekaOptionUtils.parse(options, INDEX, getDefaultIndex()));
    setRegExp((BaseRegExp) WekaOptionUtils.parse(options, REGEXP, getDefaultRegExp()));
    setGroup(WekaOptionUtils.parse(options, GROUP, DEFAULT_GROUP));
    super.setOptions(options);
  }

  /**
   * Gets the current settings of the classifier.
   *
   * @return 		an array of strings suitable for passing to setOptions
   */
  public String [] getOptions() {
    List<String> result = new ArrayList<>();
    WekaOptionUtils.add(result, INDEX, getIndex());
    WekaOptionUtils.add(result, REGEXP, getRegExp());
    WekaOptionUtils.add(result, GROUP, getGroup());
    WekaOptionUtils.add(result, super.getOptions());
    return WekaOptionUtils.toArray(result);
  }

  /**
   * Returns the default index.
   *
   * @return		the default index
   */
  protected WekaAttributeIndex getDefaultIndex() {
    return new WekaAttributeIndex(WekaAttributeIndex.FIRST);
  }

  /**
   * Sets the attribute index to use for grouping.
   *
   * @param value	the index
   */
  public void setIndex(WekaAttributeIndex value) {
    m_Index = value;
    reset();
  }

  /**
   * Returns the attribute index to use for grouping.
   *
   * @return		the index
   */
  public WekaAttributeIndex getIndex() {
    return m_Index;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String indexTipText() {
    return "The index of the attribute to determine the group from.";
  }

  /**
   * Returns the default regular expression.
   *
   * @return		the default
   */
  protected BaseRegExp getDefaultRegExp() {
    return new BaseRegExp(BaseRegExp.MATCH_ALL);
  }

  /**
   * Sets the regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').
   *
   * @param value	the expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').
   *
   * @return		the expression
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
    return "The regular expression for identifying the group (eg '^(.*)-([0-9]+)-(.*)$').";
  }

  /**
   * Sets the replacement string to use as group (eg '$2').
   *
   * @param value	the group
   */
  public void setGroup(String value) {
    m_Group = value;
    reset();
  }

  /**
   * Returns the replacement string to use as group (eg '$2').
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String groupTipText() {
    return "The replacement string to use as group (eg '$2').";
  }

  /**
   * Returns the list of row indices generated from the data.
   *
   * @param data the data to generate row selections from
   * @throws Exception if checks or selection failed
   * @return the list of selections
   */
  @Override
  protected List<int[]> doSelectRows(Instances data) throws Exception {
    ArrayList<int[]>		result;
    int				index;
    int				i;
    Map<String, TIntList> 	groups;
    String			group;
    boolean			all;
    boolean			numeric;

    m_Index.setData(data);
    index = m_Index.getIntIndex();
    if (index == -1)
      throw new Exception("Group attribute not found: " + m_Index.getIndex());
    numeric = data.attribute(index).isNumeric();

    result = new ArrayList<>();
    groups = new HashMap<>();
    all    = m_Group.equals("$0");
    for (i = 0; i < data.numInstances(); i++) {
      if (numeric)
	group = "" + data.instance(i).value(index);
      else
	group = data.instance(i).stringValue(index);
      if (!all)
	group = group.replaceAll(m_RegExp.getValue(), m_Group);
      if (!groups.containsKey(group))
	groups.put(group, new TIntArrayList());
      groups.get(group).add(i);
    }
    if (getDebug())
      debugMsg("Groups: " + groups);

    for (String g: groups.keySet())
      result.add(groups.get(g).toArray());

    // sort by the first row in each group
    Collections.sort(result, Comparator.comparingInt(o -> o[0]));

    return result;
  }
}

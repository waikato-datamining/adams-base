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
 * InstanceGrouping.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package weka.core;

import adams.core.base.BaseRegExp;
import adams.core.logging.LoggingObject;
import adams.data.weka.WekaAttributeIndex;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Groups rows in a dataset using a regular expression on a nominal or string
 * attribute.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class InstanceGrouping
  extends LoggingObject {

  private static final long serialVersionUID = 7047752529464358361L;

  /** the original data. */
  protected Instances m_Data;

  /** the attribute index. */
  protected WekaAttributeIndex m_Index;

  /** the regular expression. */
  protected BaseRegExp m_RegExp;

  /** the replacement string, using the groups from the regexp. */
  protected String m_Group;

  /** the groups. */
  protected Map<String, TIntList> m_Groups;

  /**
   * Initializes the object.
   *
   * @param data	the data to group
   * @param index	the index
   * @param regExp	the regular expression (eg '(.*)-([0-9]+)-(.*)')
   * @param group 	the replacement string, using the groups from the regexp (eg '$2')
   */
  public InstanceGrouping(Instances data, WekaAttributeIndex index, BaseRegExp regExp, String group) {
    m_Data   = new Instances(data);
    m_RegExp = regExp;
    m_Index  = index;
    m_Group  = group;
    initialize();
    process();
  }

  /**
   * Initializes the grouping.
   */
  protected void initialize() {
    int		col;

    m_Groups = new HashMap<>();

    // no data?
    if (m_Data == null)
      throw new IllegalArgumentException("No data provided!");

    m_Index.setData(m_Data);

    // column present?
    col = m_Index.getIntIndex();
    if (col == -1)
      throw new IllegalArgumentException(
        "Failed to locate attribute using index: " + m_Index.getIndex());

    // nominal or string?
    if (!(m_Data.attribute(col).isNominal() || m_Data.attribute(col).isString()))
      throw new IllegalArgumentException(
        "Attribute '" + m_Index.getIndex() + "' can only be nominal or string, found: "
	  + Attribute.typeToString(m_Data.attribute(col).type()));

    // group?
    if (m_Group.isEmpty())
      throw new IllegalArgumentException("No replacement string provided to from the groups!");
  }

  /**
   * Performs the grouping.
   */
  protected void process() {
    int			col;
    int			i;
    Instance		inst;
    String		id;
    String		group;

    col = m_Index.getIntIndex();
    for (i = 0; i < m_Data.numInstances(); i++) {
      inst  = m_Data.instance(i);
      id    = inst.stringValue(col);
      group = id.replaceAll(m_RegExp.getValue(), m_Group);
      if (!m_Groups.containsKey(group))
        m_Groups.put(group, new TIntArrayList());
      m_Groups.get(group).add(i);
    }
  }

  /**
   * Creates the header for the collapsed data.
   *
   * @return		the header
   */
  protected Instances collapsedHeader() {
    Instances			result;
    int				col;
    List<String> 		groups;
    ArrayList<Attribute>	atts;
    Attribute			att;
    boolean			nominal;

    col    = m_Index.getIntIndex();
    groups = new ArrayList<>(m_Groups.keySet());
    Collections.sort(groups);

    // header
    atts    = new ArrayList<>();
    att     = m_Data.attribute(col);
    nominal = att.isNominal();
    if (nominal)
      atts.add(new Attribute(att.name(), groups));
    else
      atts.add(new Attribute(att.name(), (ArrayList<String>) null));
    if (m_Data.classIndex() != -1)
      atts.add((Attribute) m_Data.classAttribute().copy());
    result = new Instances(m_Data.relationName(), atts, size());
    if (m_Data.classIndex() != -1)
      result.setClassIndex(result.numAttributes() - 1);

    return result;
  }

  /**
   * Collapses the data into a fake dataset with only the the group and
   * the class attribute.
   *
   * @param data	the data to collapse
   * @return		the collapsed dataset
   */
  public Instances collapse(Instances data) {
    Instances			result;
    int				col;
    int				i;
    Instance			inst;
    String			id;
    String			group;
    Set<String>			added;
    double[]			values;
    boolean			nominal;

    // header
    result  = collapsedHeader();
    nominal = result.attribute(0).isNominal();

    // add data
    col   = m_Index.getIntIndex();
    added = new HashSet<>();
    for (i = 0; i < data.numInstances(); i++) {
      inst  = data.instance(i);
      id    = inst.stringValue(col);
      group = id.replaceAll(m_RegExp.getValue(), m_Group);
      if (!m_Groups.containsKey(group))
        throw new IllegalStateException("Unknown group generated from instance #" + (i+1) + " using '" + id + "': " + group);
      if (added.contains(group))
        continue;
      values = new double[(result.classIndex() != -1) ? 2 : 1];
      if (nominal)
	values[0] = result.attribute(0).indexOfValue(group);
      else
	values[0] = result.attribute(0).addStringValue(group);
      if (result.classIndex() != -1)
	values[1] = inst.classValue();
      result.add(new DenseInstance(inst.weight(), values));
      added.add(group);
    }

    // compress
    result.compactify();

    return result;
  }

  /**
   * Ensures that the data to expand is in the right format.
   *
   * @param data	the data to check
   * @throws IllegalArgumentException	if checks fail
   */
  protected void expandCheck(Instances data) {
    if (m_Data.classIndex() != -1) {
      if (data.numAttributes() != 2)
	throw new IllegalArgumentException(
	  "Expected dataset with two attributes, but got: " + data.numAttributes());
    }
    else {
      if (data.numAttributes() != 1)
	throw new IllegalArgumentException(
	  "Expected dataset with one attribute, but got: " + data.numAttributes());
    }
    if (!(data.attribute(0).isNominal() || data.attribute(0).isString()))
      throw new IllegalArgumentException(
        "First attribute is neither nominal nor string: " + Attribute.typeToString(data.attribute(0).type()));
  }

  /**
   * Expands the fake data into the original dataset space.
   *
   * @param data	the data to expand
   * @return		the expanded dataset
   */
  public TIntList expand(Instances data, TIntList subset) {
    TIntList 		result;
    int			i;
    Instance		inst;
    String		group;
    TIntList		members;

    expandCheck(data);

    result = new TIntArrayList();
    for (i = 0; i < subset.size(); i++) {
      inst  = data.instance(subset.get(i));
      group = inst.stringValue(0);
      if (!m_Groups.containsKey(group))
        throw new IllegalStateException("Unknown group: " + group);
      members = m_Groups.get(group);
      result.addAll(members);
    }

    return result;
  }

  /**
   * Expands the fake data into the original dataset space.
   *
   * @param data	the data to expand
   * @param useView	whether to use a view
   * @return		the expanded dataset
   */
  public Instances expand(Instances data, boolean useView) {
    Instances		result;
    int			i;
    Instance		inst;
    String		group;
    TIntList		members;
    TIntList		expanded;

    expandCheck(data);

    expanded = new TIntArrayList();
    for (i = 0; i < data.numInstances(); i++) {
      inst  = data.instance(i);
      group = inst.stringValue(0);
      if (!m_Groups.containsKey(group))
        throw new IllegalStateException("Unknown group: " + group);
      members = m_Groups.get(group);
      expanded.addAll(members);
    }

    if (useView) {
      result = new InstancesView(m_Data, expanded.toArray());
    }
    else {
      result = new Instances(m_Data, expanded.size());
      for (i = 0; i < expanded.size(); i++)
	result.add((Instance) m_Data.instance(expanded.get(i)).copy());
    }

    return result;
  }

  /**
   * Returns the underlying data.
   *
   * @return		the data
   */
  public Instances getData() {
    return m_Data;
  }

  /**
   * Returns the regular expression in use (eg '(.*)-([0-9]+)-(.*)').
   *
   * @return		the regexp
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * The group expression, i.e., replacement string (eg '$2').
   *
   * @return		the group
   */
  public String getGroup() {
    return m_Group;
  }

  /**
   * Returns the number of groups.
   *
   * @return		the number of groups
   */
  public int size() {
    return m_Groups.size();
  }

  /**
   * Returns the groups.
   *
   * @return		the groups
   */
  public Set<String> groups() {
    return m_Groups.keySet();
  }

  /**
   * Returns the group.
   *
   * @param group	the group to return
   * @return		the indices in the original dataset that form this group
   */
  public TIntList get(String group) {
    return m_Groups.get(group);
  }

  /**
   * Returns the groups and their indices.
   *
   * @return		the generated string
   */
  @Override
  public String toString() {
    return "" + m_Groups;
  }
}

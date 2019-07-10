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
 * Group.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For grouping Binnable and ungrouping BinnableGroup objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Grouping {

  public interface GroupExtractor<T> {

    /**
     * Extracts the group from the binnable object.
     *
     * @param item	the item to extract the group from
     * @return		the extracted group
     */
    public String extractGroup(Binnable<T> item);
  }

  /**
   * Combines the binnable items into groups based on the groups extracted.
   *
   * @param data	the data to group
   * @param extractor 	for extracting the group
   * @param <T>		the payload type
   * @return		the order of the groups and group map
   */
  protected static <T> Struct2<List<String>,Map<String,BinnableGroup<T>>> group(List<Binnable<T>> data, GroupExtractor<T> extractor) {
    Map<String,BinnableGroup<T>> 	map;
    List<String>			order;
    String				group;

    map   = new HashMap<>();
    order = new ArrayList<>();
    for (Binnable<T> item: data) {
      group = extractor.extractGroup(item);
      if (!map.containsKey(group)) {
	map.put(group, new BinnableGroup<>(group));
	order.add(group);
      }
      map.get(group).add(item);
    }

    return new Struct2<>(order, map);
  }

  /**
   * Combines the binnable items into groups based on the groups extracted.
   *
   * @param data	the data to group
   * @param extractor 	for extracting the group
   * @param <T>		the payload type
   * @return		the groups
   */
  public static <T> Map<String,BinnableGroup<T>> groupAsMap(List<Binnable<T>> data, GroupExtractor<T> extractor) {
    return group(data, extractor).value2;
  }

  /**
   * Combines the binnable items into groups based on the groups extracted.
   * The generated list consists of the sorted groups.
   *
   * @param data	the data to group
   * @param extractor 	for extracting the group
   * @param <T>		the payload type
   * @return		the groups
   */
  public static <T> List<BinnableGroup<T>> groupAsList(List<Binnable<T>> data, GroupExtractor<T> extractor) {
    List<BinnableGroup<T>>				result;
    Struct2<List<String>,Map<String,BinnableGroup<T>>>	all;
    Map<String,BinnableGroup<T>> 			map;
    List<String>					order;

    result = new ArrayList<>();
    all    = group(data, extractor);
    order  = all.value1;
    map    = all.value2;
    for (String group: order)
      result.add(map.get(group));

    return result;
  }

  /**
   * Unravels the grouped binnable items into a single list again.
   *
   * @param data	the data to ungroup
   * @param sortByGroup 	whether to sort by group
   * @param <T>		the payload type
   * @return		the generated list
   */
  public static <T> List<Binnable<T>> ungroupMap(Map<String,BinnableGroup<T>> data, boolean sortByGroup) {
    List<Binnable<T>>	result;
    List<String>	groups;

    result = new ArrayList<>();
    groups = new ArrayList<>(data.keySet());
    if (sortByGroup)
      Collections.sort(groups);
    for (String group: groups)
      result.addAll(data.get(group).get());

    return result;
  }

  /**
   * Unravels the grouped binnable items into a single list again.
   *
   * @param data	the data to ungroup
   * @param <T>		the payload type
   * @return		the generated list
   */
  public static <T> List<Binnable<T>> ungroup(BinnableGroup<T> data) {
    List<Binnable<T>>	result;

    result = new ArrayList<>();
    result.addAll(data.get());

    return result;
  }
}

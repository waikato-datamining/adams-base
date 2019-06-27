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
 * Ungroup.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * For ungrouping binnable group data.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Ungroup {

  /**
   * Unravels the grouped binnable items into a single list again.
   *
   * @param data	the data to ungroup
   * @param sortByGroup 	whether to sort by group
   * @param <T>		the payload type
   * @return		the generated list
   */
  public static <T> List<Binnable<T>> ungroup(Map<String,BinnableGroup<T>> data, boolean sortByGroup) {
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

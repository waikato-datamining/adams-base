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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * For grouping Binnable objects.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Group {

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
   * @return		the groups
   */
  public static <T> Map<String,BinnableGroup<T>> group(List<Binnable<T>> data, GroupExtractor<T> extractor) {
    Map<String,BinnableGroup<T>>	result;
    String				group;

    result = new HashMap<>();
    for (Binnable<T> item: data) {
      group = extractor.extractGroup(item);
      if (!result.containsKey(group))
        result.put(group, new BinnableGroup<>(group));
      result.get(group).add(item);
    }

    return result;
  }
}

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
 * BinnableInstances.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning;

import adams.data.binning.operation.Grouping.GroupExtractor;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.operation.Wrapping.BinValueExtractor;
import adams.data.binning.operation.Wrapping.IndexedBinValueExtractor;
import adams.ml.splitgenerator.generic.core.Subset;
import com.github.fracpete.javautils.struct.Struct2;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import weka.core.Instance;
import weka.core.Instances;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for binning instances.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BinnableInstances {

  /**
   * Uses the class value as bin value.
   */
  public static class ClassValueBinValueExtractor
    implements BinValueExtractor<Instance>, Serializable {

    private static final long serialVersionUID = -2287393293543008133L;

    /**
     * Extracts the numeric value to use for binning from the object.
     *
     * @param object	the object to process
     * @return		the extracted value
     */
    @Override
    public double extractBinValue(Instance object) {
      return object.classValue();
    }
  }

  /**
   * Uses the class value of the first instance in the group as bin value.
   */
  public static class GroupedClassValueBinValueExtractor
    implements BinValueExtractor<BinnableGroup<Instance>>, Serializable {

    private static final long serialVersionUID = -2287393293543008133L;

    /**
     * Extracts the numeric value to use for binning from the object.
     *
     * @param object	the object to process
     * @return		the extracted value
     */
    @Override
    public double extractBinValue(BinnableGroup<Instance> object) {
      return object.get().get(0).getPayload().classValue();
    }
  }

  /**
   * Group extractor for string attributes.
   */
  public static class StringAttributeGroupExtractor
    implements GroupExtractor<Instance>, Serializable {

    private static final long serialVersionUID = -2381541290397169468L;

    /** the attribute index. */
    protected int m_Index;

    /** the regular expression. */
    protected String m_RegExp;

    /** the group to extract. */
    protected String m_Group;

    /**
     * Initializes the extractor.
     *
     * @param index 	the index (0-based) of the string attribute
     * @param regExp	the regular expression to apply to the strings
     * @param group	the regexp group to extract as group
     */
    public StringAttributeGroupExtractor(int index, String regExp, String group) {
      m_Index  = index;
      m_RegExp = regExp;
      m_Group  = group;
    }

    /**
     * Extracts the group from the binnable object.
     *
     * @param item	the item to extract the group from
     * @return		the extracted group
     */
    @Override
    public String extractGroup(Binnable<Instance> item) {
      return item.getPayload().stringValue(m_Index).replaceAll(m_RegExp, m_Group);
    }
  }


  /**
   * Turns Instances into a list of binnables using the class value.
   *
   * @param data	the instances to convert
   * @return		the generated list
   * @throws Exception	if extraction of class value fails
   */
  public static List<Binnable<Instance>> toBinnableUsingClass(Instances data) throws Exception {
    return Wrapping.wrap(data, new ClassValueBinValueExtractor());
  }

  /**
   * Turns Instances into a list of binnables using the instance index.
   *
   * @param data	the instances to convert
   * @return		the generated list
   * @throws Exception	if extraction of index fails
   */
  public static List<Binnable<Instance>> toBinnableUsingIndex(Instances data) throws Exception {
    return Wrapping.wrap(data, new IndexedBinValueExtractor<>());
  }

  /**
   * Turns a binnable list back into Instances.
   *
   * @param list	the list to convert
   * @return		the generated instances
   */
  public static Instances toInstances(List<Binnable<Instance>> list) {
    Instances result;

    result = new Instances(list.get(0).getPayload().dataset(), list.size());
    result.addAll(Wrapping.unwrap(list));

    return result;
  }

  /**
   * Extracts row indices and binnable list from grouped subset.
   *
   * @param subset	the subset to process
   * @return		the indices and list
   */
  public static Struct2<TIntList,List<Binnable<Instance>>> extractIndicesAndBinnable(Subset<Binnable<BinnableGroup<Instance>>> subset) {
    TIntList 				rows;
    List<BinnableGroup<Instance>> 	grouped;
    List<Binnable<Instance>> 		binned;

    grouped = Wrapping.unwrap(subset.getData());
    rows    = new TIntArrayList();
    binned  = new ArrayList<>();
    for (BinnableGroup<Instance> group: grouped) {
      for (Binnable<Instance> item: group.get()) {
	rows.add((Integer) item.getMetaData(Wrapping.TMP_INDEX));
	binned.add(item);
      }
    }

    return new Struct2<>(rows, binned);
  }
}

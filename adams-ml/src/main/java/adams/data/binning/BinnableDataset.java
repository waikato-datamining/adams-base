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
 * BinnableDataset.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning;

import adams.data.binning.operation.Grouping.GroupExtractor;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.operation.Wrapping.BinValueExtractor;
import adams.data.binning.operation.Wrapping.IndexedBinValueExtractor;
import adams.data.spreadsheet.DataRow;
import adams.ml.data.Dataset;

import java.io.Serializable;
import java.util.List;

/**
 * Helper class for binning Datasets.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BinnableDataset {

  /**
   * Uses the specified class value as bin value.
   */
  public static class ClassValueBinValueExtractor
    implements BinValueExtractor<DataRow>, Serializable {

    private static final long serialVersionUID = -2287393293543008133L;

    /** the column index. */
    protected int m_Index;

    /**
     * Initializes the extractor.
     *
     * @param index 	the index (0-based) of the class column
     */
    public ClassValueBinValueExtractor(int index) {
      m_Index = index;
    }

    /**
     * Extracts the numeric value to use for binning from the object.
     *
     * @param object	the object to process
     * @return		the extracted value
     */
    @Override
    public double extractBinValue(DataRow object) {
      return object.getCell(m_Index).toDouble();
    }
  }

  /**
   * Uses the class value of the first data row in the group as bin value.
   */
  public static class GroupedClassValueBinValueExtractor
    implements BinValueExtractor<BinnableGroup<DataRow>>, Serializable {

    private static final long serialVersionUID = -2287393293543008133L;

    /** the column index. */
    protected int m_Index;

    /**
     * Initializes the extractor.
     *
     * @param index 	the index (0-based) of the class column
     */
    public GroupedClassValueBinValueExtractor(int index) {
      m_Index = index;
    }

    /**
     * Extracts the numeric value to use for binning from the object.
     *
     * @param object	the object to process
     * @return		the extracted value
     */
    @Override
    public double extractBinValue(BinnableGroup<DataRow> object) {
      return object.get().get(0).getPayload().getCell(m_Index).toDouble();
    }
  }

  /**
   * Group extractor for string columns.
   */
  public static class StringAttributeGroupExtractor
    implements GroupExtractor<DataRow>, Serializable {

    private static final long serialVersionUID = -2381541290397169468L;

    /** the column index. */
    protected int m_Index;

    /** the regular expression. */
    protected String m_RegExp;

    /** the group to extract. */
    protected String m_Group;

    /**
     * Initializes the extractor.
     *
     * @param index 	the index (0-based) of the string column
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
    public String extractGroup(Binnable<DataRow> item) {
      return item.getPayload().getCell(m_Index).getContent().replace(m_RegExp, m_Group);
    }
  }


  /**
   * Turns Rows into a list of binnables using the class value.
   *
   * @param data	the data rows to convert
   * @param index 	the index of the column to act as class
   * @return		the generated list
   * @throws Exception	if extraction of class value fails
   */
  public static List<Binnable<DataRow>> toBinnableUsingClass(Dataset data, int index) throws Exception {
    return Wrapping.wrap(data.rows(), new ClassValueBinValueExtractor(index));
  }

  /**
   * Turns Rows into a list of binnables using the data row index.
   *
   * @param data	the data rows to convert
   * @return		the generated list
   * @throws Exception	if extraction of index fails
   */
  public static List<Binnable<DataRow>> toBinnableUsingIndex(Dataset data) throws Exception {
    return Wrapping.wrap(data.rows(), new IndexedBinValueExtractor<>());
  }

  /**
   * Turns a binnable list back into Rows.
   *
   * @param list	the list to convert
   * @return		the generated data rows
   */
  public static Dataset toDataset(List<Binnable<DataRow>> list) {
    Dataset result;

    result = (Dataset) list.get(0).getPayload().getOwner().getHeader();
    for (DataRow row: Wrapping.unwrap(list))
      result.addRow().assign(row);

    return result;
  }
}

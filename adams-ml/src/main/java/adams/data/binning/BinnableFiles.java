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
 * BinnableFiles.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning;

import adams.core.io.FileUtils;
import adams.data.binning.operation.Grouping.GroupExtractor;
import adams.data.binning.operation.Wrapping;
import adams.data.binning.operation.Wrapping.IndexedBinValueExtractor;

import java.io.File;
import java.io.Serializable;
import java.util.List;

/**
 * Helper class for binning files.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class BinnableFiles {

  /**
   * Allows to extract groups either from full name or from just filename.
   */
  public static class FileGroupExtractor
    implements GroupExtractor<String>, Serializable {

    private static final long serialVersionUID = -2456087580932522565L;

    /** whether to use only the name, not the path. */
    protected boolean m_OnlyName;

    /** whether to remove the extension. */
    protected boolean m_RemoveExtension;

    /** the regular expression. */
    protected String m_RegExp;

    /** the group to extract. */
    protected String m_Group;

    /**
     * Initializes the extractor.
     *
     * @param onlyName	whether to only consider the name, excluding the path
     * @param removeExtension 	whether to remove the extension
     * @param regExp	the regular expression to apply to the strings
     * @param group	the regexp group to extract as group
     */
    public FileGroupExtractor(boolean onlyName, boolean removeExtension, String regExp, String group) {
      m_OnlyName        = onlyName;
      m_RemoveExtension = removeExtension;
      m_RegExp          = regExp;
      m_Group           = group;
    }

    /**
     * Extracts the group from the binnable object.
     *
     * @param item	the item to extract the group from
     * @return		the extracted group
     */
    @Override
    public String extractGroup(Binnable<String> item) {
      String	value;

      value = item.getPayload();
      if (m_RemoveExtension)
        value = FileUtils.replaceExtension(value, "");
      if (m_OnlyName)
        value = new File(value).getName();

      return value.replaceAll(m_RegExp, m_Group);
    }
  }

  /**
   * Turns strings into a list of binnables using the array index.
   *
   * @param data	the strings to convert
   * @return		the generated list
   * @throws Exception	if extraction of index fails
   */
  public static List<Binnable<String>> toBinnableUsingIndex(String[] data) throws Exception {
    return Wrapping.wrap(data, new IndexedBinValueExtractor<>());
  }

  /**
   * Turns files into a list of binnables using the array index.
   *
   * @param data	the files to convert
   * @return		the generated list
   * @throws Exception	if extraction of index fails
   */
  public static List<Binnable<File>> toBinnableUsingIndex(File[] data) throws Exception {
    return Wrapping.wrap(data, new IndexedBinValueExtractor<>());
  }

  /**
   * Turns a binnable list back into a string array.
   *
   * @param list	the list to convert
   * @return		the generated array
   */
  public static String[] toStringArray(List<Binnable<String>> list) {
    return Wrapping.unwrap(list).toArray(new String[0]);
  }

  /**
   * Turns a binnable list back into a file array.
   *
   * @param list	the list to convert
   * @return		the generated array
   */
  public static File[] toFileArray(List<Binnable<File>> list) {
    return Wrapping.unwrap(list).toArray(new File[0]);
  }
}

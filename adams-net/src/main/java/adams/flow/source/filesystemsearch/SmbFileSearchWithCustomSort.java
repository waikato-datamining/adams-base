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

/**
 * SmbFileSearchWithCustomSort.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

/**
 <!-- globalinfo-start -->
 * Searches only for files, but uses a regular expression to reassemble the name and perform the sorting.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-directory &lt;adams.core.io.PlaceholderDirectory&gt; (property: directory)
 * &nbsp;&nbsp;&nbsp;The directory to search for directories.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-max-items &lt;int&gt; (property: maxItems)
 * &nbsp;&nbsp;&nbsp;The maximum number of dirs to return (&lt;= 0 is unlimited).
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the dirs must match (empty string matches all
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-sort-find &lt;adams.core.base.BaseRegExp&gt; (property: sortFind)
 * &nbsp;&nbsp;&nbsp;The regular expression that extracts groups to be used in reassembling the 
 * &nbsp;&nbsp;&nbsp;string for sorting.
 * &nbsp;&nbsp;&nbsp;default: ([\\\\s\\\\S]+)
 * </pre>
 * 
 * <pre>-sort-replace &lt;java.lang.String&gt; (property: sortReplace)
 * &nbsp;&nbsp;&nbsp;The reassmbly string making use of the groups extracted with the regular 
 * &nbsp;&nbsp;&nbsp;expression.
 * &nbsp;&nbsp;&nbsp;default: $0
 * </pre>
 * 
 * <pre>-descending &lt;boolean&gt; (property: sortDescending)
 * &nbsp;&nbsp;&nbsp;If set to true, the directories are sorted in descending manner.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;Whether to search recursively or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-max-depth &lt;int&gt; (property: maxDepth)
 * &nbsp;&nbsp;&nbsp;The maximum depth to search in recursive mode (1 = only search directory,
 * &nbsp;&nbsp;&nbsp; -1 = infinite).
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SmbFileSearchWithCustomSort
  extends SmbDirectorySearchWithCustomSort {

  /** for serialization. */
  private static final long serialVersionUID = 3229293554987103145L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Searches only for files, but uses a regular expression to reassemble the name and perform the sorting.";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister.setListDirs(false);
    m_Lister.setListFiles(true);
  }
}

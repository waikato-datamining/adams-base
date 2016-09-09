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
 * FileSearchWithTimestampConstraints.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import adams.core.base.BaseDateTime;
import adams.core.io.lister.LocalDirectoryLister;

/**
 <!-- globalinfo-start -->
 * Searches only for files.<br>
 * Allows to further restrict the search using a timestamp window ('last modified') that the files must satisfy.
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
 * &nbsp;&nbsp;&nbsp;The directory to search for files.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-max-items &lt;int&gt; (property: maxItems)
 * &nbsp;&nbsp;&nbsp;The maximum number of files to return (&lt;= 0 is unlimited).
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the files must match (empty string matches all
 * &nbsp;&nbsp;&nbsp;).
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-sorting &lt;NO_SORTING|SORT_BY_NAME|SORT_BY_LAST_MODIFIED&gt; (property: sorting)
 * &nbsp;&nbsp;&nbsp;The type of sorting to perform.
 * &nbsp;&nbsp;&nbsp;default: NO_SORTING
 * </pre>
 * 
 * <pre>-descending &lt;boolean&gt; (property: sortDescending)
 * &nbsp;&nbsp;&nbsp;If set to true, the files are sorted in descending manner.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;Whether to search recursively or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-max-depth &lt;int&gt; (property: maxDepth)
 * &nbsp;&nbsp;&nbsp;The maximum depth to search in recursive mode (1 = only watch directory, 
 * &nbsp;&nbsp;&nbsp;-1 = infinite).
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-min-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: minTimestamp)
 * &nbsp;&nbsp;&nbsp;The minimum 'last modified'  timestamp that the files can have.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 * 
 * <pre>-max-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: maxTimestamp)
 * &nbsp;&nbsp;&nbsp;The maximum 'last modified'  timestamp that the files can have.
 * &nbsp;&nbsp;&nbsp;default: +INF
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FileSearchWithTimestampConstraints
  extends FileSearch {

  /** for serialization. */
  private static final long serialVersionUID = 7165242885787887905L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	super.globalInfo() + "\n"
	+ "Allows to further restrict the search using a timestamp window "
	+ "('last modified') that the files must satisfy.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "min-timestamp", "minTimestamp",
	    new BaseDateTime(BaseDateTime.INF_PAST));

    m_OptionManager.add(
	    "max-timestamp", "maxTimestamp",
	    new BaseDateTime(BaseDateTime.INF_FUTURE));
  }

  /**
   * Sets the minimum 'last modified'  timestamp that the files have to have.
   *
   * @param value	the minimum timestamp
   * @see		LocalDirectoryLister#setMinFileTimestamp(BaseDateTime)
   */
  public void setMinTimestamp(BaseDateTime value) {
    m_Lister.setMinFileTimestamp(value);
    reset();
  }

  /**
   * Returns the minimum 'last modified'  timestamp that the files have to have.
   *
   * @return		the minimum timestamp
   * @see		LocalDirectoryLister#getMinFileTimestamp()
   */
  public BaseDateTime getMinTimestamp() {
    return m_Lister.getMinFileTimestamp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minTimestampTipText() {
    return "The minimum 'last modified'  timestamp that the files can have.";
  }

  /**
   * Sets the maximum 'last modified'  timestamp that the files have to have.
   *
   * @param value	the maximum timestamp
   * @see		LocalDirectoryLister#setMaxFileTimestamp(BaseDateTime)
   */
  public void setMaxTimestamp(BaseDateTime value) {
    m_Lister.setMaxFileTimestamp(value);
    reset();
  }

  /**
   * Returns the maximum file timestamp that the files have to have.
   *
   * @return		the maximum timestamp
   * @see		LocalDirectoryLister#getMaxFileTimestamp()
   */
  public BaseDateTime getMaxTimestamp() {
    return m_Lister.getMaxFileTimestamp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxTimestampTipText() {
    return "The maximum 'last modified'  timestamp that the files can have.";
  }
}

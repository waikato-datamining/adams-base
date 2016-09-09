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
 * SftpDirectorySearch.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.core.io.lister.Sorting;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Searches only for directories.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-directory &lt;java.io.File&gt; (property: directory)
 * &nbsp;&nbsp;&nbsp;The directory to search for directories.
 * &nbsp;&nbsp;&nbsp;default: .
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
 * <pre>-sorting &lt;NO_SORTING|SORT_BY_NAME|SORT_BY_LAST_MODIFIED&gt; (property: sorting)
 * &nbsp;&nbsp;&nbsp;The type of sorting to perform.
 * &nbsp;&nbsp;&nbsp;default: NO_SORTING
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
public class SftpDirectorySearch
  extends AbstractSftpDirectoryListerBasedSearchlet {

  /** for serialization. */
  private static final long serialVersionUID = 3229293554987103145L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Searches only for directories.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "directory", "directory",
      new File("."));

    m_OptionManager.add(
      "max-items", "maxItems",
      -1);

    m_OptionManager.add(
      "regexp", "regExp",
      new BaseRegExp(""));

    m_OptionManager.add(
      "sorting", "sorting",
      Sorting.NO_SORTING);

    m_OptionManager.add(
      "descending", "sortDescending",
      false);

    m_OptionManager.add(
      "recursive", "recursive",
      false);

    m_OptionManager.add(
      "max-depth", "maxDepth",
      -1);
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister.setListDirs(true);
    m_Lister.setListFiles(false);
  }

  /**
   * Sets the directory to search.
   *
   * @param value	the directory
   */
  public void setDirectory(File value) {
    m_Lister.setWatchDir(value);
  }

  /**
   * Returns the directory to search.
   *
   * @return		the directory.
   */
  public File getDirectory() {
    return m_Lister.getWatchDir();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String directoryTipText() {
    return "The directory to search for directories.";
  }

  /**
   * Sets the maximum number of items to return.
   *
   * @param value	the maximum number
   */
  public void setMaxItems(int value) {
    m_Lister.setMaxItems(value);
    reset();
  }

  /**
   * Returns the maximum number of items to return.
   *
   * @return		the maximum number
   */
  public int getMaxItems() {
    return m_Lister.getMaxItems();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxItemsTipText() {
    return "The maximum number of dirs to return (<= 0 is unlimited).";
  }

  /**
   * Sets the regular expression for the files/dirs.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_Lister.setRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression for the files/dirs.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_Lister.getRegExp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the dirs must match (empty string matches all).";
  }

  /**
   * Sets the type of sorting to perform.
   *
   * @param value	the type of sorting
   */
  public void setSorting(Sorting value) {
    m_Lister.setSorting(value);
    reset();
  }

  /**
   * Returns the type of sorting to perform.
   *
   * @return		the type of sorting
   */
  public Sorting getSorting() {
    return m_Lister.getSorting();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortingTipText() {
    return "The type of sorting to perform.";
  }

  /**
   * Sets whether to sort descendingly.
   *
   * @param value	true if sorting in descending order
   */
  public void setSortDescending(boolean value) {
    m_Lister.setSortDescending(value);
    reset();
  }

  /**
   * Returns whether to sort descendingly.
   *
   * @return		true if sorting in descending order
   */
  public boolean getSortDescending() {
    return m_Lister.getSortDescending();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortDescendingTipText() {
    return "If set to true, the directories are sorted in descending manner.";
  }

  /**
   * Sets whether to search recursively.
   *
   * @param value	true if search is recursively
   */
  public void setRecursive(boolean value) {
    m_Lister.setRecursive(value);
    reset();
  }

  /**
   * Returns whether search is recursively.
   *
   * @return		true if search is recursively
   */
  public boolean getRecursive() {
    return m_Lister.getRecursive();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String recursiveTipText() {
    return "Whether to search recursively or not.";
  }

  /**
   * Sets the maximum depth to search (in recursive mode). 1 = only watch
   * directory, -1 = infinite.
   *
   * @param value	the maximum number of directory levels to traverse
   */
  public void setMaxDepth(int value) {
    m_Lister.setMaxDepth(value);
    reset();
  }

  /**
   * Returns the maximum depth to search (in recursive mode). 1 = only watch
   * directory, -1 = infinite.
   *
   * @return		the maximum number of directory levels to traverse
   */
  public int getMaxDepth() {
    return m_Lister.getMaxDepth();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxDepthTipText() {
    return
      "The maximum depth to search in recursive mode (1 = only search "
        + "directory, -1 = infinite).";
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String		result;
    List<String>	options;

    result = QuickInfoHelper.toString(this, "directory", getDirectory());

    // further options
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "sorting", getSorting()));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "sortDescending", (getSorting() != Sorting.NO_SORTING) && getSortDescending(), "descending"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "recursive", getRecursive(), "recursive"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "maxItems", (getMaxItems() > 0 ? getMaxItems() : null), "max="));
    result += QuickInfoHelper.flatten(options);

    return result;
  }
}

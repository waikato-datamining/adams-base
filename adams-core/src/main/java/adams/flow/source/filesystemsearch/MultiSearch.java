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
 * MultiSearch.java
 * Copyright (C) 2014-2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import adams.flow.core.Actor;

import java.util.ArrayList;
import java.util.List;

/**
 <!-- globalinfo-start -->
 * Combines the search results of multiple search algorithms.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-search &lt;adams.flow.source.filesystemsearch.AbstractFileSystemSearchlet&gt; [-search ...] (property: searches)
 * &nbsp;&nbsp;&nbsp;The search algorithms to execute.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiSearch
  extends AbstractFileSystemSearchlet {

  /** for serialization. */
  private static final long serialVersionUID = -3189597528629633942L;

  /** the search algorithms to execute. */
  protected AbstractFileSystemSearchlet[] m_Searches;

  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Combines the search results of multiple search algorithms.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "search", "searches",
      new AbstractFileSystemSearchlet[0]);
  }

  /**
   * Sets the search algorithms to use.
   *
   * @param value the algorithms
   */
  public void setSearches(AbstractFileSystemSearchlet[] value) {
    m_Searches = value;
    reset();
  }

  /**
   * Returns the search algorithms in use.
   *
   * @return the algorithms
   */
  public AbstractFileSystemSearchlet[] getSearches() {
    return m_Searches;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return tip text for this property suitable for
   * displaying in the GUI or for listing the options.
   */
  public String searchesTipText() {
    return "The search algorithms to execute.";
  }

  /**
   * Sets the flow context.
   *
   * @param value	the actor
   */
  @Override
  public void setFlowContext(Actor value) {
    super.setFlowContext(value);
    for (AbstractFileSystemSearchlet search: m_Searches)
      search.setFlowContext(value);
  }

  /**
   * Performs a setup check before search.
   *
   * @throws Exception if checks failed
   */
  @Override
  protected void check() throws Exception {
    super.check();

    if (m_Searches.length == 0)
      throw new IllegalStateException("At least one search algorithm must be supplied!");
  }

  /**
   * Performs the actual search.
   *
   * @throws Exception if search failed
   * @return the search result
   */
  @Override
  protected List<String> doSearch() throws Exception {
    List<String> result;
    int i;

    result = new ArrayList<>();
    for (i = 0; i < m_Searches.length; i++) {
      try {
	result.addAll(m_Searches[i].search());
      }
      catch (Exception e) {
	throw new Exception("Failed to execute search #" + (i + 1), e);
      }
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    int i;

    m_Stopped = true;

    for (i = 0; i < m_Searches.length; i++) {
      m_Searches[i].stopExecution();
    }
  }
}
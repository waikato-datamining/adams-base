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
 * BlockingSearch.java
 * Copyright (C) 2024 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source.filesystemsearch;

import adams.core.QuickInfoHelper;
import adams.core.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Executes the base search till there is a non-empty list returned or, if specified, the timeout reached.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public class BlockingSearch
  extends AbstractFileSystemSearchlet {

  private static final long serialVersionUID = -2652975788946866774L;

  /** the shortest wait time. */
  public final static int MIN_WAIT = 50;

  /** the base search. */
  protected AbstractFileSystemSearchlet m_Search;

  /** the interval in milliseconds. */
  protected int m_Interval;

  /** the timeout in milliseconds (<=0 never time out). */
  protected int m_Timeout;
  
  /**
   * Returns a string describing the object.
   *
   * @return a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Executes the base search till there is a non-empty list returned or, if specified, the timeout reached.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "search", "search",
      new LocalFileSearch());

    m_OptionManager.add(
      "interval", "interval",
      1000, MIN_WAIT, null);

    m_OptionManager.add(
      "timeout", "timeout",
      -1, -1, null);
  }

  /**
   * Sets the base search to use.
   *
   * @param value	the search plugin
   */
  public void setSearch(AbstractFileSystemSearchlet value) {
    m_Search = value;
    reset();
  }

  /**
   * Returns the base search in use.
   *
   * @return		the search plugin
   */
  public AbstractFileSystemSearchlet getSearch() {
    return m_Search;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String searchTipText() {
    return "The base search to use.";
  }

  /**
   * Sets the interval in milliseconds to wait.
   *
   * @param value	the interval
   */
  public void setInterval(int value) {
    if (getOptionManager().isValid("interval", value)) {
      m_Interval = value;
      reset();
    }
  }

  /**
   * Returns the interval to wait in milliseconds.
   *
   * @return		the interval
   */
  public int getInterval() {
    return m_Interval;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String intervalTipText() {
    return "The interval in milliseconds to wait before attempting the next search.";
  }

  /**
   * Sets the timeout in milliseconds.
   *
   * @param value	the timeout (<= 0 no timeout)
   */
  public void setTimeout(int value) {
    if (getOptionManager().isValid("timeout", value)) {
      m_Timeout = value;
      reset();
    }
  }

  /**
   * Returns the timeout in milliseconds.
   *
   * @return		the timeout (<= 0 no timeout)
   */
  public int getTimeout() {
    return m_Timeout;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String timeoutTipText() {
    return "The timeout in milliseconds before returning an empty result.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result = QuickInfoHelper.toString(this, "interval", m_Interval, "interval: ");
    result += QuickInfoHelper.toString(this, "timeout", m_Timeout, ", timeout: ");
    result += QuickInfoHelper.toString(this, "search", m_Search, ", search: ");

    return result;
  }

  /**
   * Performs the actual search.
   *
   * @throws Exception if search failed
   * @return the search result
   */
  @Override
  protected List<String> doSearch() throws Exception {
    List<String>	result;
    long		start;

    result = new ArrayList<>();
    start  = System.currentTimeMillis();

    while (!m_Stopped) {
      result = m_Search.search();

      // found something?
      if (!result.isEmpty()) {
	if (isLoggingEnabled())
	  getLogger().info("Base search found # items: " + result.size());
	break;
      }

      // timeout?
      if (m_Timeout > 0) {
	if (System.currentTimeMillis() >= start + m_Timeout) {
	  if (isLoggingEnabled())
	    getLogger().info("Timeout of " + m_Timeout + " msecs reached, exiting...");
	  break;
	}
      }

      // wait before next search attempt
      if (isLoggingEnabled())
	getLogger().info("Waiting " + m_Interval + " msecs...");
      Utils.wait(this, m_Interval, MIN_WAIT);
    }

    return result;
  }

  /**
   * Stops the execution.
   */
  @Override
  public void stopExecution() {
    m_Search.stopExecution();
    m_Stopped = true;
  }
}

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
 * LogEntryConditions.java
 * Copyright (C) 2010-2011 University of Waikato, Hamilton, New Zealand
 */
package adams.db;

import adams.core.base.BaseDateTime;
import adams.core.base.BaseRegExp;

/**
 <!-- globalinfo-start -->
 * Conditions for retrieving log entries.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-limit &lt;int&gt; (property: limit)
 * &nbsp;&nbsp;&nbsp;The maximum number of records to retrieve.
 * &nbsp;&nbsp;&nbsp;default: 10000
 * &nbsp;&nbsp;&nbsp;minimum: -1
 * </pre>
 *
 * <pre>-host &lt;adams.core.base.BaseRegExp&gt; (property: host)
 * &nbsp;&nbsp;&nbsp;The host name to use in the search (regular expression).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-ip &lt;adams.core.base.BaseRegExp&gt; (property: IP)
 * &nbsp;&nbsp;&nbsp;The IP address to use in the search (regular expression).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-type &lt;adams.core.base.BaseRegExp&gt; (property: type)
 * &nbsp;&nbsp;&nbsp;The log entry type to use in the search (regular expression).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-status &lt;adams.core.base.BaseRegExp&gt; (property: status)
 * &nbsp;&nbsp;&nbsp;The log entry status to use in the search (regular expression).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-source &lt;adams.core.base.BaseRegExp&gt; (property: source)
 * &nbsp;&nbsp;&nbsp;The log entry type to use in the search (regular expression).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-generation-start &lt;adams.core.base.BaseDateTime&gt; (property: generationStartDate)
 * &nbsp;&nbsp;&nbsp;The start date for the log entry generation.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 *
 * <pre>-generation-end &lt;adams.core.base.BaseDateTime&gt; (property: generationEndDate)
 * &nbsp;&nbsp;&nbsp;The end date for the log entry generation.
 * &nbsp;&nbsp;&nbsp;default: +INF
 * </pre>
 *
 * <pre>-latest (property: latest)
 * &nbsp;&nbsp;&nbsp;Returns only the latest entries, i.e., works backwards from the generation
 * &nbsp;&nbsp;&nbsp;end date.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LogEntryConditions
  extends AbstractLimitedConditions {

  /** for serialization. */
  private static final long serialVersionUID = 7496947748107342392L;

  /** the host name (regexp). */
  protected BaseRegExp m_Host;

  /** the IP address (regexp). */
  protected BaseRegExp m_IP;

  /** the type of the log entries (regexp). */
  protected BaseRegExp m_Type;

  /** the status of the log entries (regexp). */
  protected BaseRegExp m_Status;

  /** the source of the log entries (regexp). */
  protected BaseRegExp m_Source;

  /** the start date of the log entries. */
  protected BaseDateTime m_GenerationStartDate;

  /** the end date of the log entries. */
  protected BaseDateTime m_GenerationEndDate;

  /** whether to return the latest entries. */
  protected boolean m_Latest;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Conditions for retrieving log entries.";
  }

  /**
   * Adds options to the internal list of options.
   */
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "host", "host",
	    new BaseRegExp(""));

    m_OptionManager.add(
    	    "ip", "IP",
	    new BaseRegExp(""));

    m_OptionManager.add(
	    "type", "type",
	    new BaseRegExp(""));

    m_OptionManager.add(
	    "status", "status",
	    new BaseRegExp(""));

    m_OptionManager.add(
	    "source", "source",
	    new BaseRegExp(""));

    m_OptionManager.add(
	    "generation-start", "generationStartDate",
	    new BaseDateTime(BaseDateTime.INF_PAST));

    m_OptionManager.add(
	    "generation-end", "generationEndDate",
	    new BaseDateTime(BaseDateTime.INF_FUTURE));

    m_OptionManager.add(
	    "latest", "latest",
	    false);
  }

  /**
   * Sets the host name to use in the search.
   *
   * @param value 	the host name
   */
  public void setHost(BaseRegExp value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the host name to use in the search.
   *
   * @return 		the host name
   */
  public BaseRegExp getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host name to use in the search (regular expression).";
  }

  /**
   * Sets the IP address to use in the search.
   *
   * @param value 	the IP address
   */
  public void setIP(BaseRegExp value) {
    m_IP = value;
    reset();
  }

  /**
   * Returns the IP address to use in the search.
   *
   * @return 		the IP address
   */
  public BaseRegExp getIP() {
    return m_IP;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String IPTipText() {
    return "The IP address to use in the search (regular expression).";
  }

  /**
   * Sets the type to use in the search.
   *
   * @param value 	the type
   */
  public void setType(BaseRegExp value) {
    m_Type = value;
    reset();
  }

  /**
   * Returns the type used in the search.
   *
   * @return 		the type
   */
  public BaseRegExp getType() {
    return m_Type;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String typeTipText() {
    return "The log entry type to use in the search (regular expression).";
  }

  /**
   * Sets the status to use in the search.
   *
   * @param value 	the status
   */
  public void setStatus(BaseRegExp value) {
    m_Status = value;
    reset();
  }

  /**
   * Returns the status used in the search.
   *
   * @return 		the status
   */
  public BaseRegExp getStatus() {
    return m_Status;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String statusTipText() {
    return "The log entry status to use in the search (regular expression).";
  }

  /**
   * Sets the source to use in the search.
   *
   * @param value 	the source
   */
  public void setSource(BaseRegExp value) {
    m_Source = value;
    reset();
  }

  /**
   * Returns the source used in the search.
   *
   * @return 		the source
   */
  public BaseRegExp getSource() {
    return m_Source;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sourceTipText() {
    return "The log entry type to use in the search (regular expression).";
  }

  /**
   * Sets the generation start date.
   *
   * @param value 	the start date
   */
  public void setGenerationStartDate(BaseDateTime value) {
    m_GenerationStartDate = value;
    reset();
  }

  /**
   * Returns the generation start date.
   *
   * @return 		the start date
   */
  public BaseDateTime getGenerationStartDate() {
    return m_GenerationStartDate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generationStartDateTipText() {
    return "The start date for the log entry generation.";
  }

  /**
   * Sets the generation end date.
   *
   * @param value 	the end date
   */
  public void setGenerationEndDate(BaseDateTime value) {
    m_GenerationEndDate = value;
    reset();
  }

  /**
   * Returns the generation end date.
   *
   * @return 		the end date
   */
  public BaseDateTime getGenerationEndDate() {
    return m_GenerationEndDate;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String generationEndDateTipText() {
    return "The end date for the log entry generation.";
  }

  /**
   * Sets whether to return the latest (most recent) entries only.
   *
   * @param value 	true if the latest entries are to be returned
   */
  public void setLatest(boolean value) {
    m_Latest = value;
    reset();
  }

  /**
   * Returns whether to return only the latest (most recent) entries only.
   *
   * @return 		true if the latest entries are returned
   */
  public boolean getLatest() {
    return m_Latest;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String latestTipText() {
    return "Returns only the latest entries, i.e., works backwards from the generation end date.";
  }

  /**
   * Automatically corrects values.
   */
  protected void update() {
    if (m_Host == null)
      m_Host = new BaseRegExp("");

    if (m_IP == null)
      m_IP = new BaseRegExp("");

    if (m_Type == null)
      m_Type = new BaseRegExp("");

    if (m_Status == null)
      m_Status = new BaseRegExp("");

    if (m_Source == null)
      m_Status = new BaseRegExp("");
  }
}

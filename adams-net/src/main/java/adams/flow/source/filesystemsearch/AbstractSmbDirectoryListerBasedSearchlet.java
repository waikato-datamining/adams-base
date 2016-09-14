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
 * AbstractSmbDirectoryListerBasedSearchlet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import adams.core.io.lister.SmbDirectoryLister;
import adams.core.logging.LoggingLevel;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.SMBConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for search algorithms that use {@link SmbDirectoryLister}
 * under the hood.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractSmbDirectoryListerBasedSearchlet
  extends AbstractFileSystemSearchlet {
  
  /** for serialization. */
  private static final long serialVersionUID = -240436041323613527L;

  /** for listing the contents. */
  protected SmbDirectoryLister m_Lister;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "host", "host",
      "");
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister = new SmbDirectoryLister();
  }

  /**
   * Sets the logging level.
   *
   * @param value 	the level
   */
  @Override
  public synchronized void setLoggingLevel(LoggingLevel value) {
    super.setLoggingLevel(value);
    m_Lister.setLoggingLevel(value);
  }

  /**
   * Sets the host to connect to.
   *
   * @param value	the host
   */
  public void setHost(String value) {
    m_Lister.setHost(value);
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the hsot
   */
  public String getHost() {
    return m_Lister.getHost();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host to connect to.";
  }

  /**
   * Performs a setup check before search.
   *
   * @throws Exception	if checks failed
   */
  @Override
  protected void check() throws Exception {
    SMBConnection conn;

    super.check();

    if (m_FlowContext == null)
      throw new IllegalStateException(
	"No flow context provided, cannot obtain " + SMBConnection.class.getName() + "!");

    conn = (SMBConnection) ActorUtils.findClosestType(m_FlowContext, SMBConnection.class);
    if (conn == null)
      throw new IllegalStateException("No " + SMBConnection.class.getName() + " actor found!");
    m_Lister.setAuthenticationType(conn.getSession());
  }

  /**
   * Performs the actual search.
   * 
   * @return		the search result
   * @throws Exception	if search failed
   */
  @Override
  protected List<String> doSearch() throws Exception {
    List<String>	result;
    
    result = new ArrayList<>();
    result.addAll(Arrays.asList(m_Lister.list()));
    
    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
    m_Lister.stopExecution();
  }
}

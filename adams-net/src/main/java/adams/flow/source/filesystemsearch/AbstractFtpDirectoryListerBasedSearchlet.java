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
 * AbstractFtpDirectoryListerBasedSearchlet.java
 * Copyright (C) 2016 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import adams.core.io.lister.FtpDirectoryLister;
import adams.core.logging.LoggingLevel;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.FTPConnection;
import adams.flow.standalone.SSHConnection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for search algorithms that use {@link FtpDirectoryLister}
 * under the hood.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public abstract class AbstractFtpDirectoryListerBasedSearchlet
  extends AbstractFileSystemSearchlet {
  
  /** for serialization. */
  private static final long serialVersionUID = -240436041323613527L;

  /** for listing the contents. */
  protected FtpDirectoryLister m_Lister;

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister = new FtpDirectoryLister();
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
   * Performs a setup check before search.
   *
   * @throws Exception	if checks failed
   */
  @Override
  protected void check() throws Exception {
    FTPConnection	conn;

    super.check();

    if (m_FlowContext == null)
      throw new IllegalStateException(
	"No flow context provided, cannot obtain " + SSHConnection.class.getName() + "!");

    conn = (FTPConnection) ActorUtils.findClosestType(m_FlowContext, FTPConnection.class);
    if (conn == null)
      throw new IllegalStateException("No " + FTPConnection.class.getName() + " actor found!");
    m_Lister.setClient(conn.getFTPClient());
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

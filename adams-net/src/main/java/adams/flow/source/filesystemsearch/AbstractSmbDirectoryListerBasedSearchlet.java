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
 * AbstractSmbDirectoryListerBasedSearchlet.java
 * Copyright (C) 2016-2025 University of Waikato, Hamilton, New Zealand
 */
package adams.flow.source.filesystemsearch;

import adams.core.io.lister.SmbDirectoryLister;
import adams.core.logging.LoggingLevel;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.SMBConnection;
import com.hierynomus.smbj.share.DiskShare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Ancestor for search algorithms that use {@link SmbDirectoryLister}
 * under the hood.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractSmbDirectoryListerBasedSearchlet
  extends AbstractFileSystemSearchlet {
  
  /** for serialization. */
  private static final long serialVersionUID = -240436041323613527L;

  /** for listing the contents. */
  protected SmbDirectoryLister m_Lister;

  /** the diskshare instance. */
  protected transient DiskShare m_DiskShare;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "share", "share",
      "");
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    cleanUpSmb();
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
   * Sets the share to access.
   *
   * @param value	the share
   */
  public void setShare(String value) {
    m_Lister.setShare(value);
    reset();
  }

  /**
   * Returns the share to access.
   *
   * @return		the share
   */
  public String getShare() {
    return m_Lister.getShare();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String shareTipText() {
    return "The share to access.";
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
    m_Lister.setSessionProvider(conn);
    cleanUpSmb();
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

    result = new ArrayList<>(Arrays.asList(m_Lister.list()));
    cleanUpSmb();

    return result;
  }

  /**
   * Stops the execution.
   */
  public void stopExecution() {
    m_Stopped = true;
    m_Lister.stopExecution();
  }

  /**
   * Cleans up SMB resources.
   */
  protected void cleanUpSmb() {
    if (m_DiskShare != null) {
      try {
	m_DiskShare.close();
      }
      catch (Exception e) {
	// ignored
      }
      m_DiskShare = null;
    }
  }
}

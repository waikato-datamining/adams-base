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
 * SmbRemoteDirectory.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.gui.chooser;

import adams.core.base.BasePassword;
import adams.core.net.SMBAuthenticationProvider;
import adams.core.option.AbstractOptionHandler;
import jcifs.smb.NtlmPasswordAuthentication;

/**
 * For configuring an SMB connection and remote directory.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 14538 $
 */
public class SmbRemoteDirectorySetup
  extends AbstractOptionHandler
  implements SMBAuthenticationProvider, RemoteDirectorySetup {

  private static final long serialVersionUID = -8429471751146663032L;

  /** the domain. */
  protected String m_Domain;

  /** the SMB user to use. */
  protected String m_User;

  /** the SMB password to use. */
  protected BasePassword m_Password;

  /** the host. */
  protected String m_Host;

  /** the directory to list. */
  protected String m_RemoteDir;

  /** the SMB authentication. */
  protected transient NtlmPasswordAuthentication m_Session;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "For configuring access to a remote directory via SFTP.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "domain", "domain",
      "");

    m_OptionManager.add(
      "user", "user",
      System.getProperty("user.name"), false);

    m_OptionManager.add(
      "password", "password",
      new BasePassword(""), false);

    m_OptionManager.add(
      "host", "host",
      "");

    m_OptionManager.add(
      "remote-dir", "remoteDir",
      "");
  }

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    reset();
  }

  /**
   * Returns the host to connect to.
   *
   * @return		the host name/ip
   */
  public String getHost() {
    return m_Host;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String hostTipText() {
    return "The host (name/IP address) to connect to.";
  }

  /**
   * Sets the domain to connect to.
   *
   * @param value	the domain name
   */
  public void setDomain(String value) {
    m_Domain = value;
    reset();
  }

  /**
   * Returns the domain to connect to.
   *
   * @return		the domain name
   */
  public String getDomain() {
    return m_Domain;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String domainTipText() {
    return "The domain name to connect to.";
  }

  /**
   * Sets the SMB user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
    reset();
  }

  /**
   * Returns the SMB user name to use.
   *
   * @return		the user name
   */
  public String getUser() {
    return m_User;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String userTipText() {
    return "The SMB user to use for connecting.";
  }

  /**
   * Sets the SMB password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
    reset();
  }

  /**
   * Returns the SMB password to use.
   *
   * @return		the password
   */
  public BasePassword getPassword() {
    return m_Password;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String passwordTipText() {
    return "The password of the SMB user to use for connecting.";
  }

  /**
   * Sets the remote directory.
   *
   * @param value	the remote directory
   */
  public void setRemoteDir(String value) {
    m_RemoteDir = value;
    reset();
  }

  /**
   * Returns the remote directory.
   *
   * @return		the remote directory.
   */
  public String getRemoteDir() {
    return m_RemoteDir;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String remoteDirTipText() {
    return "The SFTP directory to download the file from.";
  }

  /**
   * Returns the SMB authentication.
   *
   * @return		the SMB authentication, null if not connected
   */
  public synchronized NtlmPasswordAuthentication getAuthentication() {
    if (m_Session == null)
      m_Session = newAuthentication();
    return m_Session;
  }

  /**
   * Returns a new SMB authentication.
   *
   * @return		the SMB authentication
   */
  public NtlmPasswordAuthentication newAuthentication() {
    return new NtlmPasswordAuthentication(m_Domain, m_User, m_Password.getValue());
  }

  /**
   * Cleans up data structures, frees up memory.
   */
  public void cleanUp() {
    m_Session = null;
  }

  /**
   * Returns whether the setup needs to be configured by the user or whether
   * it can be used straight away.
   *
   * @return		true if user needs to configure first
   */
  public boolean requiresInitialization() {
    return true;
  }
}

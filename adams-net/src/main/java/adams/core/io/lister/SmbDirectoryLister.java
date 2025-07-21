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
 * SmbDirectoryLister.java
 * Copyright (C) 2016-2019 University of Waikato, Hamilton, NZ
 */

package adams.core.io.lister;

import adams.core.PasswordSupporter;
import adams.core.base.BasePassword;
import adams.core.io.FileObject;
import adams.core.io.SmbFileObject;
import adams.core.net.SMB;
import adams.core.net.SMBSessionProvider;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Lists files/dirs on a remote server using SMB.
 * The authentication object takes precedence over domain/user/password.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SmbDirectoryLister
  extends AbstractRecursiveDirectoryLister
  implements PasswordSupporter {

  private static final long serialVersionUID = 2687222234652386893L;

  /** the SMB host. */
  protected String m_Host;

  /** the share to access. */
  protected String m_Share;

  /** the SMB domain. */
  protected String m_Domain;

  /** the SMB user to use. */
  protected String m_User;

  /** the SMB password to use. */
  protected BasePassword m_Password;

  /** whether to show hidden files/dirs. */
  protected boolean m_ListHidden;

  /** the session provider to use. */
  protected SMBSessionProvider m_SessionProvider;

  /** the SMB client. */
  protected transient SMBClient m_Client;

  /** the SMB connection. */
  protected transient Connection m_Connection;

  /** the session to use. */
  protected transient Session m_Session;

  /** the diskshare in use. */
  protected transient DiskShare m_DiskShare;

  /**
   * Sets the host to connect to.
   *
   * @param value	the host name/ip
   */
  public void setHost(String value) {
    m_Host = value;
    cleanUpSmb();
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
   * Sets the domain to use.
   *
   * @param value	the domain
   */
  public void setDomain(String value) {
    m_Domain = value;
  }

  /**
   * Returns the domain to use.
   *
   * @return 		the domain
   */
  public String getDomain() {
    return m_Domain;
  }

  /**
   * Sets the share to use.
   *
   * @param value	the share
   */
  public void setShare(String value) {
    m_Share = value;
  }

  /**
   * Returns the share to access.
   *
   * @return		the share
   */
  public String getShare() {
    return m_Share;
  }

  /**
   * Sets the SMB user to use.
   *
   * @param value	the user name
   */
  public void setUser(String value) {
    m_User = value;
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
   * Sets the SMB password to use.
   *
   * @param value	the password
   */
  public void setPassword(BasePassword value) {
    m_Password = value;
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
   * Sets whether to list hidden dirs/files.
   *
   * @param value 	true if to list
   */
  public void setListHidden(boolean value) {
    m_ListHidden = value;
  }

  /**
   * Returns whether to list hidden dirs/files.
   *
   * @return 		true if to list
   */
  public boolean getListHidden() {
    return m_ListHidden;
  }

  /**
   * Sets the session provider to use.
   *
   * @param value	the provider
   */
  public void setSessionProvider(SMBSessionProvider value) {
    m_SessionProvider = value;
  }

  /**
   * Returns the session provider to use.
   *
   * @return		the provider
   */
  public SMBSessionProvider getSessionProvider() {
    return m_SessionProvider;
  }

  /**
   * Obtains the share instance to use.
   *
   * @return		the share
   */
  protected DiskShare getDiskShare() {
    AuthenticationContext	context;

    if (m_DiskShare == null) {
      if (m_Session == null) {
	if (m_SessionProvider != null) {
	  m_Session = m_SessionProvider.getSession();
	}
	else {
	  if (m_Client == null)
	    m_Client = new SMBClient();
	  if (m_Connection == null) {
	    try {
	      m_Connection = m_Client.connect(m_Host);
	    }
	    catch (Exception e) {
	      getLogger().log(Level.SEVERE, "Failed to connect to: " + m_Host);
	      return null;
	    }
	  }
	  context   = new AuthenticationContext(m_User, m_Password.getValue().toCharArray(), m_Domain);
	  m_Session = m_Connection.authenticate(context);
	}
      }

      m_DiskShare = (DiskShare) m_Session.connectShare(m_Share);
    }
    return m_DiskShare;
  }

  /**
   * Returns whether the directory lister operates locally or remotely.
   *
   * @return		true if local lister
   */
  public boolean isLocal() {
    return false;
  }

  /**
   * Returns whether the watch directory has a parent directory.
   *
   * @return		true if parent directory available
   */
  public boolean hasParentDirectory() {
    return (new File(m_WatchDir).getParentFile() != null);
  }

  /**
   * Returns a new directory relative to the watch directory.
   *
   * @param dir		the directory name
   * @return		the new wrapper
   */
  public SmbFileObject newDirectory(String dir) {
    return newDirectory(m_WatchDir, dir);
  }

  /**
   * Returns a new directory relative to the parent directory.
   *
   * @param parent 	the parent directory
   * @param dir		the directory name
   * @return		the new wrapper
   */
  public SmbFileObject newDirectory(String parent, String dir) {
    String 		pdir;
    String		ddir;
    String[]		parts;

    try {
      if (dir.equals("..")) {
	parts = SMB.splitPath(parent);
	pdir  = SMB.getParent(parent);
	ddir  = SMB.fixSubDir(parts[parts.length - 1]);
	for (FileIdBothDirectoryInformation info : getDiskShare().list(pdir, parts[parts.length - 1]))
	  return new SmbFileObject(getDiskShare(), pdir + ddir, info, "..");
      }
      else {
	pdir = SMB.fixDir(parent);
	ddir = SMB.fixSubDir(dir);
	// can we find the dir via its parent?
	for (FileIdBothDirectoryInformation info : getDiskShare().list(pdir, dir))
	  return new SmbFileObject(getDiskShare(), pdir, info);
	// the directory does not yet exist, maybe in the process of being created
	return new SmbFileObject(getDiskShare(), pdir + ddir, null);
      }
      getLogger().warning("Parent directory not found? parent=" + parent + ", dir=" + dir);
      return null;
    }
    catch (Exception e) {
      getLogger().warning("Failed to construct new dir for: parent=" + parent + ", dir=" + dir);
      return null;
    }
  }

  /**
   * Performs the recursive search. Search goes deeper if != 0 (use -1 to
   * start with for infinite search).
   *
   * @param dir		the dir to search
   * @param files	the files collected so far
   * @param depth	the depth indicator (searched no deeper, if 0)
   * @throws Exception	if listing fails
   */
  protected void search(String dir, List<SortContainer> files, int depth) throws Exception {
    List<FileIdBothDirectoryInformation>	currFiles;
    FileIdBothDirectoryInformation		entry;
    int						i;

    if (depth == 0)
      return;

    if (getDebug())
      getLogger().info("search: parentDir=" + dir + ", depth=" + depth);

    currFiles = getDiskShare().list(dir);
    if ((currFiles == null) || currFiles.isEmpty()) {
      if (getDebug())
	getLogger().info("No files listed!");
      return;
    }

    for (i = 0; i < currFiles.size(); i++) {
      // do we have to stop?
      if (m_Stopped)
	break;

      entry = currFiles.get(i);

      // hidden?
      if (SMB.isHidden(entry) && !m_ListHidden)
	continue;

      // directory?
      if (SMB.isDirectory(entry)) {
	// ignore "." and ".."
	if (entry.getFileName().equals(".") || entry.getFileName().equals(".."))
	  continue;

	// search recursively?
	if (m_Recursive)
	  search(dir + entry.getFileName() + "/", files, depth - 1);

	if (m_ListDirs) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(entry.getFileName()))
	    continue;

	  files.add(new SortContainer(new SmbFileObject(getDiskShare(), dir, entry), m_Sorting));
	}
      }
      else {
	if (m_ListFiles) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(entry.getFileName()))
	    continue;

	  files.add(new SortContainer(new SmbFileObject(getDiskShare(), dir, entry), m_Sorting));
	}
      }
    }
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns empty list.
   *
   * @param dir		the directory to search
   * @return		the list of absolute file/directory names
   * @throws Exception	if listing fails
   */
  public List<SmbFileObject> search(String dir) throws Exception {
    List<SmbFileObject>		result;
    List<SortContainer>		list;
    SortContainer		cont;
    int				i;

    result    = new ArrayList<>();
    m_Stopped = false;

    if (m_ListFiles || m_ListDirs) {
      if (getDebug())
	getLogger().info("watching '" + dir + "'");

      if (getDebug())
	getLogger().info("before search(...)");
      list = new ArrayList<>();
      search(dir, list, m_MaxDepth);

      // sort files ascendingly regarding lastModified
      if (getDebug())
	getLogger().info("before obtaining last modified timestamps");

      if (!m_Stopped && (m_Sorting != Sorting.NO_SORTING)) {
	if (getDebug())
	  getLogger().info("before sorting");
	Collections.sort(list);
	if (m_SortDescending) {
	  for (i = 0; i < list.size() / 2; i++) {
	    cont = list.get(i);
	    list.set(i, list.get(list.size() - 1 - i));
	    list.set(list.size() - 1 - i, cont);
	  }
	}
      }

      // match filenames and them to the result
      if (!m_Stopped) {
	if (getDebug())
	  getLogger().info("before matching");
	for (i = 0; i < list.size(); i++) {
	  result.add((SmbFileObject) list.get(i).getFile());

	  // maximum reached?
	  if (m_MaxItems > 0) {
	    if (result.size() == m_MaxItems) {
	      if (getDebug())
		getLogger().info("max size reached");
	      break;
	    }
	  }

	  // do we have to stop?
	  if (m_Stopped)
	    break;
	}
      }
    }

    // do we have to stop?
    if (m_Stopped)
      result.clear();

    return result;
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the array of absolute file/directory names
   */
  @Override
  public String[] list() {
    String[]		result;
    FileObject[]	wrappers;
    int			i;

    wrappers = listObjects();
    result   = new String[wrappers.length];
    for (i = 0; i < wrappers.length; i++)
      result[i] = wrappers[i].toString();

    return result;
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the array of file/directory wrappers
   */
  public SmbFileObject[] listObjects() {
    List<SmbFileObject> 	result;
    AuthenticationContext 	context;

    result    = new ArrayList<>();
    m_Stopped = false;
    if (m_Session == null) {
      if (m_SessionProvider != null) {
	m_Session = m_SessionProvider.getSession();
      }
      else {
	if (m_Client == null)
	  m_Client = new SMBClient();

	if (m_Connection == null) {
	  try {
	    m_Connection = m_Client.connect(m_Host);
	  }
	  catch (Exception e) {
	    getLogger().log(Level.SEVERE, "Failed to connect to: " + m_Host, e);
	  }
	}

	context = new AuthenticationContext(m_User, m_Password.getValue().toCharArray(), m_Domain);
	m_Session  = m_Connection.authenticate(context);
      }

      if (m_Session == null)
	throw new IllegalStateException("Failed to open session!");
    }

    try {
      result.addAll(search(SMB.fixDir(m_WatchDir)));
    }
    catch (Exception e) {
      getLogger().log(Level.SEVERE, "Failed to create search context!", e);
    }

    return result.toArray(new SmbFileObject[0]);
  }

  /**
   * Cleans up the SMB components.
   */
  protected void cleanUpSmb() {
    if (m_SessionProvider == null) {
      if (m_Client != null)
	m_Client.close();
      m_Client = null;

      if (m_Connection != null) {
	try {
	  m_Connection.close();
	}
	catch (Exception e) {
	  // ignored
	}
	m_Connection = null;
      }

      if (m_Session != null) {
	try {
	  m_Session.close();
	}
	catch (Exception e) {
	  // ignored
	}
	m_Session = null;
      }
    }

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

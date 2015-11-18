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
 * FTPLister.java
 * Copyright (C) 2011-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseRegExp;
import adams.flow.core.ActorUtils;
import adams.flow.standalone.FTPConnection;

/**
 <!-- globalinfo-start -->
 * Returns the contents of a FTP directory (files&#47;dirs).
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- flow-summary-start -->
 * Input&#47;output:<br>
 * - generates:<br>
 * &nbsp;&nbsp;&nbsp;java.lang.String<br>
 * <br><br>
 <!-- flow-summary-end -->
 *
 <!-- options-start -->
 * Valid options are: <br><br>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: FTPLister
 * </pre>
 *
 * <pre>-annotation &lt;adams.core.base.BaseText&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-skip (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded
 * &nbsp;&nbsp;&nbsp;as it is.
 * </pre>
 *
 * <pre>-stop-flow-on-error (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow gets stopped in case this actor encounters an error;
 * &nbsp;&nbsp;&nbsp; useful for critical actors.
 * </pre>
 *
 * <pre>-output-array (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the files as array or as single strings.
 * </pre>
 *
 * <pre>-remote-dir &lt;java.lang.String&gt; (property: remoteDir)
 * &nbsp;&nbsp;&nbsp;The FTP directory to list the files for.
 * &nbsp;&nbsp;&nbsp;default: &#47;pub
 * </pre>
 *
 * <pre>-list-dirs (property: listDirs)
 * &nbsp;&nbsp;&nbsp;Whether to include directories in the output.
 * </pre>
 *
 * <pre>-list-files (property: listFiles)
 * &nbsp;&nbsp;&nbsp;Whether to include files in the output.
 * </pre>
 *
 * <pre>-regexp &lt;adams.core.base.BaseRegExp&gt; (property: regExp)
 * &nbsp;&nbsp;&nbsp;The regular expression that the files&#47;dirs must match (empty string matches
 * &nbsp;&nbsp;&nbsp;all).
 * &nbsp;&nbsp;&nbsp;default:
 * </pre>
 *
 * <pre>-sorting &lt;NO_SORTING|SORT_BY_NAME|SORT_BY_LAST_MODIFIED&gt; (property: sorting)
 * &nbsp;&nbsp;&nbsp;The type of sorting to perform.
 * &nbsp;&nbsp;&nbsp;default: NO_SORTING
 * </pre>
 *
 * <pre>-descending (property: sortDescending)
 * &nbsp;&nbsp;&nbsp;If set to true, the files are sorted in descending manner.
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FTPLister
  extends AbstractArrayProvider {

  /** for serialization. */
  private static final long serialVersionUID = -5015637337437403790L;

  /**
   * The type of sorting.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public enum Sorting {
    /** no sorting. */
    NO_SORTING,
    /** sort by name. */
    SORT_BY_NAME,
    /** sort by last mod. */
    SORT_BY_LAST_MODIFIED
  }

  /** the directory to list. */
  protected String m_RemoteDir;

  /** the regular expression on the file/dir names. */
  protected BaseRegExp m_RegExp;

  /** whether to list directories. */
  protected boolean m_ListDirs;

  /** whether to list files. */
  protected boolean m_ListFiles;

  /** the sorting. */
  protected Sorting m_Sorting;

  /** whether to sort descending instead of ascending. */
  protected boolean m_SortDescending;

  /** the FTP connection to use. */
  protected FTPConnection m_Connection;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the contents of a FTP directory (files/dirs).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "remote-dir", "remoteDir",
	    "/pub");

    m_OptionManager.add(
	    "list-dirs", "listDirs",
	    false);

    m_OptionManager.add(
	    "list-files", "listFiles",
	    false);

    m_OptionManager.add(
	    "regexp", "regExp",
	    new BaseRegExp(""));

    m_OptionManager.add(
	    "sorting", "sorting",
	    Sorting.NO_SORTING);

    m_OptionManager.add(
	    "descending", "sortDescending",
	    false);
  }

  /**
   * Returns a quick info about the actor, which will be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    String	result;

    result  = QuickInfoHelper.toString(this, "remoteDir", m_RemoteDir);
    result += QuickInfoHelper.toString(this, "outputArray", (m_OutputArray ? "as array" : "one by one"), ", ");

    return result;
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
    return "The FTP directory to list the files for.";
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String outputArrayTipText() {
    return "Whether to output the files as array or as single strings.";
  }

  /**
   * Sets the regular expression for the files/dirs.
   *
   * @param value	the regular expression
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
    reset();
  }

  /**
   * Returns the regular expression for the files/dirs.
   *
   * @return		the regular expression
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String regExpTipText() {
    return "The regular expression that the files/dirs must match (empty string matches all).";
  }

  /**
   * Sets whether to list directories.
   *
   * @param value	true if directories are to be listed
   */
  public void setListDirs(boolean value) {
    m_ListDirs = value;
    reset();
  }

  /**
   * Returns whether directories are listed.
   *
   * @return		true if directories are listed
   */
  public boolean getListDirs() {
    return m_ListDirs;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String listDirsTipText() {
    return "Whether to include directories in the output.";
  }

  /**
   * Sets whether to list files.
   *
   * @param value	true if files are to be listed
   */
  public void setListFiles(boolean value) {
    m_ListFiles = value;
    reset();
  }

  /**
   * Returns whether directories are listed.
   *
   * @return		true if directories are listed
   */
  public boolean getListFiles() {
    return m_ListFiles;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String listFilesTipText() {
    return "Whether to include files in the output.";
  }

  /**
   * Sets the type of sorting to perform.
   *
   * @param value	the type of sorting
   */
  public void setSorting(Sorting value) {
    m_Sorting = value;
    reset();
  }

  /**
   * Returns the type of sorting to perform.
   *
   * @return		the type of sorting
   */
  public Sorting getSorting() {
    return m_Sorting;
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
    m_SortDescending = value;
    reset();
  }

  /**
   * Returns whether to sort descendingly.
   *
   * @return		true if sorting in descending order
   */
  public boolean getSortDescending() {
    return m_SortDescending;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String sortDescendingTipText() {
    return "If set to true, the files are sorted in descending manner.";
  }

  /**
   * Returns the based class of the items.
   *
   * @return		the class
   */
  @Override
  protected Class getItemClass() {
    return String.class;
  }

  /**
   * Initializes the item for flow execution.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  public String setUp() {
    String	result;

    result = super.setUp();

    if (result == null) {
      m_Connection = (FTPConnection) ActorUtils.findClosestType(this, FTPConnection.class);
      if (m_Connection == null)
	result = "No " + FTPConnection.class.getName() + " actor found!";
    }

    return result;
  }

  /**
   * Executes the flow item.
   *
   * @return		null if everything is fine, otherwise error message
   */
  @Override
  protected String doExecute() {
    String		result;
    FTPClient		client;
    FTPFile[]		files;

    result = null;

    m_Queue.clear();
    client = m_Connection.getFTPClient();
    if (m_ListDirs) {
      try {
	if (m_RemoteDir.length() > 0)
	  client.changeWorkingDirectory(m_RemoteDir);
	files = client.listDirectories();
	for (FTPFile file: files) {
	  if (isStopped())
	    break;
	  if (file == null)
	    continue;
	  if (m_RegExp.isEmpty() || m_RegExp.isMatchAll() || m_RegExp.isMatch(file.getName()))
	    m_Queue.add(file.getName());
	}
      }
      catch (Exception e) {
	result = handleException("Failed to list directories in '" + m_RemoteDir + "': ", e);
      }
    }

    if (result == null) {
      if (m_ListFiles) {
	try {
	  if (m_RemoteDir.length() > 0)
	    client.changeWorkingDirectory(m_RemoteDir);
	  files = client.listFiles();
	  for (FTPFile file: files) {
	    if (isStopped())
	      break;
	    if (file == null)
	      continue;
	    if (file.isDirectory())
	      continue;
	    if (m_RegExp.isEmpty() || m_RegExp.isMatchAll() || m_RegExp.isMatch(file.getName()))
	      m_Queue.add(file.getName());
	  }
	}
	catch (Exception e) {
	  result = handleException("Failed to list files in '" + m_RemoteDir + "': ", e);
	}
      }
    }

    if (isStopped())
      m_Queue.clear();

    return result;
  }
}

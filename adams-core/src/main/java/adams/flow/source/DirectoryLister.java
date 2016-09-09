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
 * DirectoryLister.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.source;

import adams.core.QuickInfoHelper;
import adams.core.base.BaseDateTime;
import adams.core.base.BaseRegExp;
import adams.core.io.lister.LocalDirectoryLister;
import adams.core.io.lister.Sorting;
import adams.core.io.FileUtils;
import adams.core.io.ForwardSlashSupporter;
import adams.core.io.PlaceholderDirectory;
import adams.core.logging.LoggingLevel;

import java.util.ArrayList;
import java.util.List;


/**
 <!-- globalinfo-start -->
 * Returns the contents of a directory (files&#47;dirs).
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
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 * <pre>-name &lt;java.lang.String&gt; (property: name)
 * &nbsp;&nbsp;&nbsp;The name of the actor.
 * &nbsp;&nbsp;&nbsp;default: DirectoryLister
 * </pre>
 * 
 * <pre>-annotation &lt;adams.core.base.BaseAnnotation&gt; (property: annotations)
 * &nbsp;&nbsp;&nbsp;The annotations to attach to this actor.
 * &nbsp;&nbsp;&nbsp;default: 
 * </pre>
 * 
 * <pre>-skip &lt;boolean&gt; (property: skip)
 * &nbsp;&nbsp;&nbsp;If set to true, transformation is skipped and the input token is just forwarded 
 * &nbsp;&nbsp;&nbsp;as it is.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-stop-flow-on-error &lt;boolean&gt; (property: stopFlowOnError)
 * &nbsp;&nbsp;&nbsp;If set to true, the flow execution at this level gets stopped in case this 
 * &nbsp;&nbsp;&nbsp;actor encounters an error; the error gets propagated; useful for critical 
 * &nbsp;&nbsp;&nbsp;actors.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-silent &lt;boolean&gt; (property: silent)
 * &nbsp;&nbsp;&nbsp;If enabled, then no errors are output in the console; Note: the enclosing 
 * &nbsp;&nbsp;&nbsp;actor handler must have this enabled as well.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-output-array &lt;boolean&gt; (property: outputArray)
 * &nbsp;&nbsp;&nbsp;Whether to output the files as array or as single strings.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-dir &lt;adams.core.io.PlaceholderDirectory&gt; (property: watchDir)
 * &nbsp;&nbsp;&nbsp;The directory to watch for files&#47;directories.
 * &nbsp;&nbsp;&nbsp;default: ${CWD}
 * </pre>
 * 
 * <pre>-list-dirs &lt;boolean&gt; (property: listDirs)
 * &nbsp;&nbsp;&nbsp;Whether to include directories in the output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-list-files &lt;boolean&gt; (property: listFiles)
 * &nbsp;&nbsp;&nbsp;Whether to include files in the output.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-max-items &lt;int&gt; (property: maxItems)
 * &nbsp;&nbsp;&nbsp;The maximum number of items (files&#47;dirs) to return (&lt;= 0 is unlimited).
 * &nbsp;&nbsp;&nbsp;default: -1
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
 * <pre>-descending &lt;boolean&gt; (property: sortDescending)
 * &nbsp;&nbsp;&nbsp;If set to true, the files are sorted in descending manner.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-recursive &lt;boolean&gt; (property: recursive)
 * &nbsp;&nbsp;&nbsp;Whether to search recursively or not.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-max-depth &lt;int&gt; (property: maxDepth)
 * &nbsp;&nbsp;&nbsp;The maximum depth to search in recursive mode (1 = only watch directory, 
 * &nbsp;&nbsp;&nbsp;-1 = infinite).
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-stop-file &lt;java.lang.String&gt; (property: stopFile)
 * &nbsp;&nbsp;&nbsp;The name of the file, that finishes the watching.
 * &nbsp;&nbsp;&nbsp;default: STOP.txt
 * </pre>
 * 
 * <pre>-wait &lt;int&gt; (property: wait)
 * &nbsp;&nbsp;&nbsp;The number of seconds to wait before polling the directory again if no elements 
 * &nbsp;&nbsp;&nbsp;were retrieved; a value of -1 indicates that polling happens only once.
 * &nbsp;&nbsp;&nbsp;default: -1
 * </pre>
 * 
 * <pre>-always-wait &lt;boolean&gt; (property: alwaysWait)
 * &nbsp;&nbsp;&nbsp;If set to true, then the waiting period is enforced between polls, even 
 * &nbsp;&nbsp;&nbsp;if there are files&#47;dirs that could get processed.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-skip-locked &lt;boolean&gt; (property: skipLockedFiles)
 * &nbsp;&nbsp;&nbsp;If set to true, locked files are skipped. Depends on the underlying OS how 
 * &nbsp;&nbsp;&nbsp;this is implemented. Under Linux, a JVM would have to lock the file explicitly 
 * &nbsp;&nbsp;&nbsp;via java.nio.channels.FileChannel.lock(). Simply opening it for writing 
 * &nbsp;&nbsp;&nbsp;does not lock the file.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 * <pre>-min-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: minFileTimestamp)
 * &nbsp;&nbsp;&nbsp;The minimum file timestamp that the files can have.
 * &nbsp;&nbsp;&nbsp;default: -INF
 * </pre>
 * 
 * <pre>-max-timestamp &lt;adams.core.base.BaseDateTime&gt; (property: maxFileTimestamp)
 * &nbsp;&nbsp;&nbsp;The maximum file timestamp that the files can have.
 * &nbsp;&nbsp;&nbsp;default: +INF
 * </pre>
 * 
 * <pre>-use-forward-slashes &lt;boolean&gt; (property: useForwardSlashes)
 * &nbsp;&nbsp;&nbsp;If enabled, forward slashes are used in the output (but the '\\' prefix 
 * &nbsp;&nbsp;&nbsp;of UNC paths is not converted).
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DirectoryLister
  extends AbstractArrayProvider
  implements ForwardSlashSupporter {

  /** for serialization. */
  private static final long serialVersionUID = -5015637337437403790L;

  /** for listing the contents. */
  protected LocalDirectoryLister m_Lister;

  /** the number of seconds to wait between polls without no entries. */
  protected int m_Wait;

  /** whether to always wait between polls, even if other files are still present. */
  protected boolean m_AlwaysWait;

  /** whether a pause before the next polling is required. */
  protected boolean m_PauseRequired;

  /** whether to output forward slashes. */
  protected boolean m_UseForwardSlashes;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Returns the contents of a directory (files/dirs).";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
	    "dir", "watchDir",
	    new PlaceholderDirectory("."));

    m_OptionManager.add(
	    "list-dirs", "listDirs",
	    false);

    m_OptionManager.add(
	    "list-files", "listFiles",
	    false);

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

    m_OptionManager.add(
	    "stop-file", "stopFile",
	    "STOP.txt");

    m_OptionManager.add(
	    "wait", "wait",
	    -1);

    m_OptionManager.add(
	    "always-wait", "alwaysWait",
	    false);

    m_OptionManager.add(
	    "skip-locked", "skipLockedFiles",
	    false);

    m_OptionManager.add(
	    "min-timestamp", "minFileTimestamp",
	    new BaseDateTime(BaseDateTime.INF_PAST));

    m_OptionManager.add(
	    "max-timestamp", "maxFileTimestamp",
	    new BaseDateTime(BaseDateTime.INF_FUTURE));

    m_OptionManager.add(
	    "use-forward-slashes", "useForwardSlashes",
	    false);
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
    String		value;

    result = "";
    value = QuickInfoHelper.toString(this, "listFiles", getListFiles(), "files");
    if (value.length() > 0)
      result += value;
    value = QuickInfoHelper.toString(this, "listDirs", getListDirs(), "dirs");
    if (value.length() > 0) {
      if (result.length() > 0)
	result += "/";
      result += value;
    }
    if (result.length() == 0)
      result = "nothing";

    result += " from ";
    result += QuickInfoHelper.toString(this, "watchDir", getWatchDir());

    // further options
    options = new ArrayList<>();
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "sorting", getSorting()));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "sortDescending", (getSorting() != Sorting.NO_SORTING) && getSortDescending(), "descending"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "recursive", getRecursive(), "recursive"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "maxItems", (getMaxItems() > 0 ? getMaxItems() : null), "max="));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "wait", (getWait() > 0 ? getWait() : null), "polling="));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "outputArray", getOutputArray(), "array"));
    QuickInfoHelper.add(options, QuickInfoHelper.toString(this, "useForwardSlashes", m_UseForwardSlashes, "forward slashes"));
    result += QuickInfoHelper.flatten(options);

    return result;
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();

    m_Lister = new LocalDirectoryLister();
  }

  /**
   * Resets the actor.
   */
  @Override
  protected void reset() {
    super.reset();

    m_PauseRequired = false;
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
   * Sets the incoming directory.
   *
   * @param value	the incoming directory
   */
  public void setWatchDir(PlaceholderDirectory value) {
    m_Lister.setWatchDir(value);
    reset();
  }

  /**
   * Returns the incoming directory.
   *
   * @return		the incoming directory.
   */
  public PlaceholderDirectory getWatchDir() {
    return m_Lister.getWatchDir();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String watchDirTipText() {
    return "The directory to watch for files/directories.";
  }

  /**
   * Sets the maximum number of items to return.
   *
   * @param value	the maximum number
   * @see		LocalDirectoryLister#setMaxItems(int)
   */
  public void setMaxItems(int value) {
    m_Lister.setMaxItems(value);
    reset();
  }

  /**
   * Returns the maximum number of items to return.
   *
   * @return		the maximum number
   * @see		LocalDirectoryLister#getMaxItems()
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
    return "The maximum number of items (files/dirs) to return (<= 0 is unlimited).";
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
   * @see		LocalDirectoryLister#setRegExp(String)
   */
  public void setRegExp(BaseRegExp value) {
    m_Lister.setRegExp(value);
    reset();
  }

  /**
   * Returns the regular expression for the files/dirs.
   *
   * @return		the regular expression
   * @see		LocalDirectoryLister#getRegExp()
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
    return "The regular expression that the files/dirs must match (empty string matches all).";
  }

  /**
   * Sets the name of the stop file.
   *
   * @param value	the regular expression
   * @see		LocalDirectoryLister#setStopFile(String)
   */
  public void setStopFile(String value) {
    m_Lister.setStopFile(value);
    reset();
  }

  /**
   * Returns the name of the stop file.
   *
   * @return		the name
   * @see		LocalDirectoryLister#getStopFile()
   */
  public String getStopFile() {
    return m_Lister.getStopFile();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String stopFileTipText() {
    return "The name of the file, that finishes the watching.";
  }

  /**
   * Sets whether to list directories.
   *
   * @param value	true if directories are to be listed
   * @see		LocalDirectoryLister#setListDirs(boolean)
   */
  public void setListDirs(boolean value) {
    m_Lister.setListDirs(value);
    reset();
  }

  /**
   * Returns whether directories are listed.
   *
   * @return		true if directories are listed
   * @see		LocalDirectoryLister#getListDirs()
   */
  public boolean getListDirs() {
    return m_Lister.getListDirs();
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
   * @see		LocalDirectoryLister#setListFiles(boolean)
   */
  public void setListFiles(boolean value) {
    m_Lister.setListFiles(value);
    reset();
  }

  /**
   * Returns whether directories are listed.
   *
   * @return		true if directories are listed
   * @see		LocalDirectoryLister#getListFiles()
   */
  public boolean getListFiles() {
    return m_Lister.getListFiles();
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
   * @see		LocalDirectoryLister#setSorting(Sorting)
   */
  public void setSorting(Sorting value) {
    m_Lister.setSorting(value);
    reset();
  }

  /**
   * Returns the type of sorting to perform.
   *
   * @return		the type of sorting
   * @see		LocalDirectoryLister#getSorting()
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
   * @see		LocalDirectoryLister#setSortDescending(boolean)
   */
  public void setSortDescending(boolean value) {
    m_Lister.setSortDescending(value);
    reset();
  }

  /**
   * Returns whether to sort descendingly.
   *
   * @return		true if sorting in descending order
   * @see		LocalDirectoryLister#getSortDescending()
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
    return "If set to true, the files are sorted in descending manner.";
  }

  /**
   * Sets the number of seconds to wait before polling the directory again if
   * no elements were retrieved.
   *
   * @param value	the number of seconds
   */
  public void setWait(int value) {
    if ((value > 0) || (value == -1))
      m_Wait = value;
    else
      getLogger().severe(
	  "Number of seconds to wait must be larger than 0 or -1 (provided: "
	  + value + ")!");
  }

  /**
   * Returns the number of seconds to wait before polling the directory again
   * if no elements were retrieved.
   *
   * @return		the number of seconds
   */
  public int getWait() {
    return m_Wait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String waitTipText() {
    return
        "The number of seconds to wait before polling the directory again if "
      + "no elements were retrieved; a value of -1 indicates that polling "
      + "happens only once.";
  }

  /**
   * Sets whether to always wait in between polls, even if there are still
   * other files that could get processed.
   *
   * @param value	if true then waiting period is enforced between polls
   */
  public void setAlwaysWait(boolean value) {
    m_AlwaysWait = value;
    reset();
  }

  /**
   * Returns whether to always wait in between polls, even if there are still
   * other files that could get processed.
   *
   * @return		true if waiting period is enforced between polls
   */
  public boolean getAlwaysWait() {
    return m_AlwaysWait;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String alwaysWaitTipText() {
    return
        "If set to true, then the waiting period is enforced between polls, "
      + "even if there are files/dirs that could get processed.";
  }

  /**
   * Sets whether to search recursively.
   *
   * @param value	true if search is recursively
   * @see		LocalDirectoryLister#setRecursive(boolean)
   */
  public void setRecursive(boolean value) {
    m_Lister.setRecursive(value);
    reset();
  }

  /**
   * Returns whether search is recursively.
   *
   * @return		true if search is recursively
   * @see		LocalDirectoryLister#getRecursive()
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
        "The maximum depth to search in recursive mode (1 = only watch "
       + "directory, -1 = infinite).";
  }

  /**
   * Sets whether to skip locked files. Depends on the underlying OS, whether
   * a file is flagged as locked. E.g., in a JVM under Linux, one would have to
   * lock the file explicitly using <code>java.nio.channels.FileChannel.lock()</code>,
   * since simply opening it for writing does not lock it.
   *
   * @param value	if true then locked files are skipped
   * @see		LocalDirectoryLister#setSkipLockedFiles(boolean)
   */
  public void setSkipLockedFiles(boolean value) {
    m_Lister.setSkipLockedFiles(value);
    reset();
  }

  /**
   * Returns whether to skip locked files.
   *
   * @return		true if locked files are skipped
   * @see		LocalDirectoryLister#getSkipLockedFiles()
   */
  public boolean getSkipLockedFiles() {
    return m_Lister.getSkipLockedFiles();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String skipLockedFilesTipText() {
    return
        "If set to true, locked files are skipped. Depends on the underlying "
      + "OS how this is implemented. Under Linux, a JVM would have to lock the "
      + "file explicitly via java.nio.channels.FileChannel.lock(). Simply "
      + "opening it for writing does not lock the file.";
  }

  /**
   * Sets the minimum file timestamp that the files have to have.
   *
   * @param value	the minimum file timestamp
   * @see		LocalDirectoryLister#setMinFileTimestamp(BaseDateTime)
   */
  public void setMinFileTimestamp(BaseDateTime value) {
    m_Lister.setMinFileTimestamp(value);
    reset();
  }

  /**
   * Returns the minimum file timestamp that the files have to have.
   *
   * @return		the minimum file timestamp
   * @see		LocalDirectoryLister#getMinFileTimestamp()
   */
  public BaseDateTime getMinFileTimestamp() {
    return m_Lister.getMinFileTimestamp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minFileTimestampTipText() {
    return "The minimum file timestamp that the files can have.";
  }

  /**
   * Sets the maximum file timestamp that the files have to have.
   *
   * @param value	the maximum file timestamp
   * @see		LocalDirectoryLister#setMaxFileTimestamp(BaseDateTime)
   */
  public void setMaxFileTimestamp(BaseDateTime value) {
    m_Lister.setMaxFileTimestamp(value);
    reset();
  }

  /**
   * Returns the maximum file timestamp that the files have to have.
   *
   * @return		the maximum file timestamp
   * @see		LocalDirectoryLister#getMaxFileTimestamp()
   */
  public BaseDateTime getMaxFileTimestamp() {
    return m_Lister.getMaxFileTimestamp();
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String maxFileTimestampTipText() {
    return "The maximum file timestamp that the files can have.";
  }

  /**
   * Sets whether to use forward slashes in the output.
   *
   * @param value	if true then use forward slashes
   */
  public void setUseForwardSlashes(boolean value) {
    m_UseForwardSlashes = value;
    reset();
  }

  /**
   * Returns whether to use forward slashes in the output.
   *
   * @return		true if forward slashes are used
   */
  public boolean getUseForwardSlashes() {
    return m_UseForwardSlashes;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String useForwardSlashesTipText() {
    return
	"If enabled, forward slashes are used in the output (but "
	+ "the '\\\\' prefix of UNC paths is not converted).";
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
      if (!getListFiles() && !getListDirs())
	result = "Neither files nor directories are being listed - choose at least one option!";
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
    String	result;
    int		i;

    result = null;

    if (m_PauseRequired) {
      for (i = 0; i < m_Wait*10; i++) {
	try {
	  synchronized(this) {
	    wait(100);
	  }
	}
	catch (Exception e) {
	  // ignored
	}

	if (isStopped())
	  break;
      }
    }

    try {
      for (String item: m_Lister.list()) {
        if (m_UseForwardSlashes)
          item = FileUtils.useForwardSlashes(item);
        m_Queue.add(item);
      }
    }
    catch (Exception e) {
      result = handleException("Failed to list/add items:", e);
    }

    // do we have to wait before polling again?
    m_PauseRequired =    (m_AlwaysWait && (m_Wait != -1))
                      || ((m_Queue.size() == 0) && !m_Lister.hasStopFileEncountered() && (m_Wait != -1));

    return result;
  }

  /**
   * Returns whether the item has finished. The <code>execute()</code> will be
   * called as long as the <code>isFinished()</code> method returns false.
   *
   * @return		true if finished, false if further calls to execute()
   * 			are necessary. Only stops when directory lister
   * 			encountered the stop file or m_Wait=-1.
   */
  @Override
  public boolean isFinished() {
    return (m_Lister.hasStopFileEncountered() || (m_Wait == -1));
  }

  /**
   * Stops the execution. No message set.
   */
  @Override
  public void stopExecution() {
    if (m_Lister != null)
      m_Lister.stop();

    super.stopExecution();
  }
}

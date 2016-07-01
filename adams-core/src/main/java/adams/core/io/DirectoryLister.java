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

package adams.core.io;

import adams.core.base.BaseDateTime;
import adams.core.base.BaseRegExp;
import adams.core.logging.CustomLoggingLevelObject;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

/**
 * Lists files/dirs in a directory.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DirectoryLister
  extends CustomLoggingLevelObject {

  /** for serialization. */
  private static final long serialVersionUID = -1846677500660003814L;

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

  /**
   * A helper class for sorting files.
   *
   * @author  fracpete (fracpete at waikato dot ac dot nz)
   * @version $Revision$
   */
  public static class SortContainer
    implements Comparable<SortContainer> {

    /** the file to be sorted. */
    protected File m_File;

    /** used for sorting. */
    protected Comparable m_Sort;

    /**
     * Initializes the sort container.
     *
     * @param file	the file to sort
     * @param sorting	the type of sorting to perform
     */
    public SortContainer(File file, Sorting sorting) {
      super();

      m_File = file;

      if (sorting == Sorting.NO_SORTING)
	m_Sort = null;
      else if (sorting == Sorting.SORT_BY_NAME)
	m_Sort = file.getAbsolutePath();
      else if (sorting == Sorting.SORT_BY_LAST_MODIFIED)
	m_Sort = new Long(file.lastModified());
      else
	throw new IllegalArgumentException("Unhandled sorting: " + sorting);
    }

    /**
     * Returns the stored file.
     *
     * @return		the stored file
     */
    public File getFile() {
      return m_File;
    }

    /**
     * Compares this container with the specified container for order. Returns a
     * negative integer, zero, or a positive integer as this container is less
     * than, equal to, or greater than the specified container.
     *
     * @param   o the subrange to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *		is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(SortContainer o) {
      // no sorting?
      if (m_Sort == null)
	return 0;
      // some kind of sorting
      else
	return m_Sort.compareTo(o.m_Sort);
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     *
     * @param obj		the reference object with which to compare.
     * @return		true if this object is the same as the obj argument;
     * 			false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof SortContainer))
	return false;
      else
	return (compareTo((SortContainer) obj) == 0);
    }

    /**
     * Hashcode so can be used as hashtable key. Returns the hashcode of the
     * file.
     *
     * @return		the hashcode
     */
    @Override
    public int hashCode() {
      return m_File.hashCode();
    }

    /**
     * Returns a string representation of the file and the object used for
     * sorting.
     *
     * @return		the representation
     */
    @Override
    public String toString() {
      return "file=" + m_File.toString() + ", sorting=" + m_Sort;
    }
  }

  /** the directory to watch. */
  protected PlaceholderDirectory m_WatchDir;

  /** whether to list directories. */
  protected boolean m_ListDirs;

  /** whether to list files. */
  protected boolean m_ListFiles;

  /** the type of sorting to perform. */
  protected Sorting m_Sorting;

  /** whether to sort descending. */
  protected boolean m_SortDescending;

  /** the maximum number of files/dirs to return. */
  protected int m_MaxItems;

  /** the regular expression for the files/dirs to match. */
  protected BaseRegExp m_RegExp;

  /** the name of the "stop file" which results in an empty list to be returned. */
  protected String m_StopFile;

  /** whether the stop file was encountered. */
  protected boolean m_StopFileEncountered;

  /** whether to stop the currently listing. */
  protected boolean m_Stopped;

  /** whether to look for files/dirs recursively. */
  protected boolean m_Recursive;

  /** the maximum depth to look recursively (0 = only watch dir, -1 = infinite). */
  protected int m_MaxDepth;

  /** whether to skip locked files. */
  protected boolean m_SkipLockedFiles;

  /** the minimum file timestamp ("last modified"). */
  protected BaseDateTime m_MinFileTimestamp;

  /** the maximum file timestamp ("last modified"). */
  protected BaseDateTime m_MaxFileTimestamp;

  /**
   * Initializes the object.
   */
  public DirectoryLister() {
    super();

    m_WatchDir            = new PlaceholderDirectory(".");
    m_ListDirs            = false;
    m_ListFiles           = true;
    m_Sorting             = Sorting.NO_SORTING;
    m_SortDescending      = false;
    m_MaxItems            = -1;
    m_RegExp              = new BaseRegExp("");
    m_StopFile            = "STOP.txt";
    m_Stopped             = false;
    m_StopFileEncountered = false;
    m_Recursive           = false;
    m_MaxDepth            = -1;
    m_SkipLockedFiles     = false;
    m_MinFileTimestamp    = new BaseDateTime(BaseDateTime.INF_PAST);
    m_MaxFileTimestamp    = new BaseDateTime(BaseDateTime.INF_FUTURE);
  }

  /**
   * Set debugging mode.
   *
   * @param value 	true if debug output should be printed
   */
  public void setDebug(boolean value) {
    getLogger().setLevel(value ? Level.INFO : Level.OFF);
  }

  /**
   * Returns whether debugging is turned on.
   *
   * @return 		true if debugging output is on
   */
  public boolean getDebug() {
    return (getLogger().getLevel() != Level.OFF);
  }

  /**
   * Sets the directory to watch.
   *
   * @param value 	the directory
   */
  public void setWatchDir(PlaceholderDirectory value) {
    m_WatchDir = value;
  }

  /**
   * Returns the directory to watch.
   *
   * @return 		the directory
   */
  public PlaceholderDirectory getWatchDir() {
    return m_WatchDir;
  }

  /**
   * Sets whether to list directories or not.
   *
   * @param value 	true if directories are included in the list
   */
  public void setListDirs(boolean value) {
    m_ListDirs = value;
  }

  /**
   * Returns whether to list directories or not.
   *
   * @return 		true if directories are listed
   */
  public boolean getListDirs() {
    return m_ListDirs;
  }

  /**
   * Sets whether to list files or not.
   *
   * @param value 	true if files are included in the list
   */
  public void setListFiles(boolean value) {
    m_ListFiles = value;
  }

  /**
   * Returns whether to list files or not.
   *
   * @return 		true if files are listed
   */
  public boolean getListFiles() {
    return m_ListFiles;
  }

  /**
   * Sets the sorting type.
   *
   * @param value 	the sorting
   */
  public void setSorting(Sorting value) {
    m_Sorting = value;
  }

  /**
   * Returns the sorting type.
   *
   * @return 		the sorting
   */
  public Sorting getSorting() {
    return m_Sorting;
  }

  /**
   * Sets whether to sort in descending manner.
   *
   * @param value 	true if desending sort manner
   */
  public void setSortDescending(boolean value) {
    m_SortDescending = value;
  }

  /**
   * Returns whether to sort in descending manner.
   *
   * @return 		true if descending sort manner
   */
  public boolean getSortDescending() {
    return m_SortDescending;
  }

  /**
   * Sets the maximum number of items to return.
   *
   * @param value 	the maximum number, &lt;=0 means unbounded
   */
  public void setMaxItems(int value) {
    m_MaxItems = value;
  }

  /**
   * Returns the maximum number of items to return.
   *
   * @return 		the maximum number, &lt;=0 means unbounded
   */
  public int getMaxItems() {
    return m_MaxItems;
  }

  /**
   * Sets the regular expressions that the items have to match.
   *
   * @param value 	the regular expression, "" matches all
   */
  public void setRegExp(BaseRegExp value) {
    m_RegExp = value;
  }

  /**
   * Returns the regular expression that the items have to match.
   *
   * @return 		the regular expression, "" matches all
   */
  public BaseRegExp getRegExp() {
    return m_RegExp;
  }

  /**
   * Sets the name of the stop file which results in returning an empty list.
   *
   * @param value 	the name of the file
   */
  public void setStopFile(String value) {
    m_StopFile = value;
  }

  /**
   * Returns the name of the stop file which results in returning an empty list.
   *
   * @return 		the name of the file
   */
  public String getStopFile() {
    return m_StopFile;
  }

  /**
   * Sets whether to search recursively.
   *
   * @param value 	true if to search recursively
   */
  public void setRecursive(boolean value) {
    m_Recursive = value;
  }

  /**
   * Returns whether to search recursively.
   *
   * @return 		true if search is recursively
   */
  public boolean getRecursive() {
    return m_Recursive;
  }

  /**
   * Sets the maximum depth to search (1 = only watch dir, -1 = infinite).
   *
   * @param value 	the maximum depth
   */
  public void setMaxDepth(int value) {
    m_MaxDepth = value;
  }

  /**
   * Returns the maximum depth to search (1 = only watch dir, -1 = infinite).
   *
   * @return 		the maximum depth
   */
  public int getMaxDepth() {
    return m_MaxDepth;
  }

  /**
   * Sets whether to skip locked files. Depends on the underlying OS, whether
   * a file is flagged as locked. E.g., in a JVM under Linux, one would have to
   * lock the file explicitly using <code>java.nio.channels.FileChannel.lock()</code>,
   * since simply opening it for writing does not lock it.
   *
   * @param value 	if true then locked files will be skipped
   */
  public void setSkipLockedFiles(boolean value) {
    m_SkipLockedFiles = value;
  }

  /**
   * Returns whether locked files are skipped.
   *
   * @return 		true if locked files are skipped
   */
  public boolean getSkipLockedFiles() {
    return m_SkipLockedFiles;
  }

  /**
   * Sets the minimum file timestamp ("last modified") that the files need to have.
   *
   * @param value 	the minimum timestamp
   */
  public void setMinFileTimestamp(BaseDateTime value) {
    m_MinFileTimestamp = value;
  }

  /**
   * Returns the minimum file timestamp ("last modified") that the files need to have.
   *
   * @return 		the minimum timestamp
   */
  public BaseDateTime getMinFileTimestamp() {
    return m_MinFileTimestamp;
  }

  /**
   * Sets the maximum file timestamp ("last modified") that the files need to have.
   *
   * @param value 	the maximum timestamp
   */
  public void setMaxFileTimestamp(BaseDateTime value) {
    m_MaxFileTimestamp = value;
  }

  /**
   * Returns the maximum file timestamp ("last modified") that the files need to have.
   *
   * @return 		the maximum timestamp
   */
  public BaseDateTime getMaxFileTimestamp() {
    return m_MaxFileTimestamp;
  }

  /**
   * Returns whether the stop file was encountered or not.
   *
   * @return		true if stop file encountered
   */
  public boolean hasStopFileEncountered() {
    return m_StopFileEncountered;
  }

  /**
   * Stops the current list generation.
   */
  public void stop() {
    m_Stopped = true;
  }

  /**
   * Checks whether the file is currently locked by another process.
   *
   * @param file	the file to check
   * @return		true if the file is locked
   */
  protected boolean isFileLocked(File file) {
    boolean		result;
    FileOutputStream 	fos;
    FileLock 		fl;

    result = false;

    fos = null;
    try {
      fos = new FileOutputStream(file, true);
      fl  = fos.getChannel().tryLock();
      if (fl == null)
	result = true;
      else
	fl.release();
    }
    catch (Exception e) {
      // ignored
    }
    finally {
      FileUtils.closeQuietly(fos);
    }

    return result;
  }

  /**
   * Performs the recursive search. Search goes deeper if != 0 (use -1 to
   * start with for infinite search).
   *
   * @param current	the current directory
   * @param files	the files collected so far
   * @param depth	the depth indicator (searched no deeper, if 0)
   */
  protected void search(File current, List<SortContainer> files, int depth) {
    File[]	currFiles;
    int		i;
    long	minFileAge;
    long	maxFileAge;

    if (depth == 0)
      return;

    if (getDebug())
      getLogger().info("search: current=" + current + ", depth=" + depth);

    currFiles = current.listFiles();
    if (currFiles == null) {
      getLogger().severe("No files listed!");
      return;
    }

    if (m_MinFileTimestamp.isInfinityPast() || m_MinFileTimestamp.isInfinityFuture())
      minFileAge = -1;
    else
      minFileAge = m_MinFileTimestamp.dateValue().getTime();
    if (m_MaxFileTimestamp.isInfinityPast() || m_MaxFileTimestamp.isInfinityFuture())
      maxFileAge = -1;
    else
      maxFileAge = m_MaxFileTimestamp.dateValue().getTime();

    for (i = 0; i < currFiles.length; i++) {
      // stop file?
      if (currFiles[i].isFile() && currFiles[i].getName().equals(m_StopFile)) {
	m_StopFileEncountered = true;
	if (getDebug())
	  getLogger().info("stop file encountered");
	break;
      }

      // do we have to stop?
      if (m_Stopped)
	break;

      // directory?
      if (currFiles[i].isDirectory()) {
	// ignore "." and ".."
	if (currFiles[i].getName().equals(".") || currFiles[i].getName().equals(".."))
	  continue;

	// search recursively?
	if (m_Recursive)
	  search(currFiles[i], files, depth - 1);

	if (m_ListDirs) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(currFiles[i].getName()))
	    continue;

	  // too old?
	  if ((minFileAge != -1) && (minFileAge > currFiles[i].lastModified()))
	    continue;

	  // too new?
	  if ((maxFileAge != -1) && (maxFileAge < currFiles[i].lastModified()))
	    continue;

	  // file locked?
	  if (m_SkipLockedFiles && isFileLocked(currFiles[i])) {
	    if (getDebug())
	      getLogger().info("file locked, skipping: " + currFiles[i]);
	    continue;
	  }

	  files.add(new SortContainer(currFiles[i], m_Sorting));
	}
      }
      else {
	if (m_ListFiles) {
	  // does name match?
	  if (!m_RegExp.isEmpty() && !m_RegExp.isMatch(currFiles[i].getName()))
	    continue;

	  // too old?
	  if ((minFileAge != -1) && (minFileAge > currFiles[i].lastModified()))
	    continue;

	  // too new?
	  if ((maxFileAge != -1) && (maxFileAge < currFiles[i].lastModified()))
	    continue;

	  // file locked?
	  if (m_SkipLockedFiles && isFileLocked(currFiles[i])) {
	    if (getDebug())
	      getLogger().info("file locked, skipping: " + currFiles[i]);
	    continue;
	  }

	  files.add(new SortContainer(currFiles[i], m_Sorting));
	}
      }
    }
  }

  /**
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the list of absolute file/directory names
   */
  public String[] list() {
    List<String>		result;
    List<SortContainer>		list;
    SortContainer		cont;
    int				i;

    result                = new ArrayList<String>();
    m_Stopped             = false;
    m_StopFileEncountered = false;

    if (m_ListFiles || m_ListDirs) {
      if (getDebug())
	getLogger().info("watching '" + m_WatchDir + "'");

      if (getDebug())
	getLogger().info("before search(...)");
      list = new ArrayList<SortContainer>();
      search(new File(m_WatchDir.getAbsolutePath()), list, m_MaxDepth);

      // sort files ascendingly regarding lastModified
      if (getDebug())
	getLogger().info("before obtaining last modified timestamps");

      if (!m_Stopped && !m_StopFileEncountered && (m_Sorting != Sorting.NO_SORTING)) {
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
      if (!m_StopFileEncountered && !m_Stopped) {
	if (getDebug())
	  getLogger().info("before matching");
	for (i = 0; i < list.size(); i++) {
	  result.add(list.get(i).getFile().getAbsolutePath());

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

    return result.toArray(new String[result.size()]);
  }

  /**
   * A string representation of the object.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;

    result  = "WatchDir=" + m_WatchDir + ", ";
    result += "ListDirs=" + m_ListDirs + ", ";
    result += "ListFiles=" + m_ListFiles + ", ";
    result += "MaxItems=" + m_MaxItems + ", ";
    result += "RegExp=" + m_RegExp + ", ";
    result += "Sorting=" + m_Sorting + ", ";
    result += "Descending=" + m_SortDescending + ", ";
    result += "StopFile=" + m_StopFile;

    return result;
  }

  /**
   * For testing only.
   *
   * @param args	ignored
   * @throws Exception	if something goes wrong
   */
  public static void main(String[] args) throws Exception {
    DirectoryLister lister = new DirectoryLister();
    //lister.setListDirs(true);
    //lister.setListFiles(false);
    //lister.setRegExp(".svn");
    //lister.setRecursive(true);
    //lister.setSorting(Sorting.SORT_BY_LAST_MODIFIED);
    String[] list = lister.list();
    for (int i = 0; i < list.length; i++)
      System.out.println((i+1) + ". " + list[i]);
    System.out.println(lister);
  }
}

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
 * LocalDirectoryLister.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io.lister;

import adams.core.base.BaseDateTime;
import adams.core.io.FileUtils;
import adams.core.io.FileWrapper;
import adams.core.io.LocalFileWrapper;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lists files/dirs in a directory.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class LocalDirectoryLister
  extends AbstractDirectoryLister {

  /** for serialization. */
  private static final long serialVersionUID = -1846677500660003814L;

  /** the name of the "stop file" which results in an empty list to be returned. */
  protected String m_StopFile;

  /** whether the stop file was encountered. */
  protected boolean m_StopFileEncountered;

  /** whether to skip locked files. */
  protected boolean m_SkipLockedFiles;

  /** the minimum file timestamp ("last modified"). */
  protected BaseDateTime m_MinFileTimestamp;

  /** the maximum file timestamp ("last modified"). */
  protected BaseDateTime m_MaxFileTimestamp;

  /**
   * Initializes the object.
   */
  public LocalDirectoryLister() {
    super();

    m_StopFile            = "STOP.txt";
    m_StopFileEncountered = false;
    m_MinFileTimestamp    = new BaseDateTime(BaseDateTime.INF_PAST);
    m_MaxFileTimestamp    = new BaseDateTime(BaseDateTime.INF_FUTURE);
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
   * Returns whether the watch directory has a parent directory.
   *
   * @return		true if parent directory available
   */
  public boolean hasParentDirectory() {
    return (m_WatchDir.getAbsoluteFile().getParentFile() != null);
  }

  /**
   * Returns a new directory relative to the watch directory.
   *
   * @param dir		the directory name
   * @return		the new wrapper
   */
  public LocalFileWrapper newDirectory(String dir) {
    return new LocalFileWrapper(new File(m_WatchDir.getAbsolutePath() + File.separator + dir));
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

    result                = new ArrayList<>();
    m_Stopped             = false;
    m_StopFileEncountered = false;

    if (m_ListFiles || m_ListDirs) {
      if (getDebug())
	getLogger().info("watching '" + m_WatchDir + "'");

      if (getDebug())
	getLogger().info("before search(...)");
      list = new ArrayList<>();
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
   * Returns the list of files/directories in the watched directory. In case
   * the execution gets stopped, this method returns a 0-length array.
   *
   * @return		 the list of file/directory wrappers
   */
  public FileWrapper[] listWrappers() {
    FileWrapper[]	result;
    String[]		files;
    int			i;

    files = list();
    result = new FileWrapper[files.length];
    for (i = 0; i < files.length; i++)
      result[i] = new LocalFileWrapper(new File(files[i]));

    return result;
  }

  /**
   * A string representation of the object.
   *
   * @return		the string representation
   */
  @Override
  public String toString() {
    String	result;

    result  = super.toString() + ", ";
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
    LocalDirectoryLister lister = new LocalDirectoryLister();
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

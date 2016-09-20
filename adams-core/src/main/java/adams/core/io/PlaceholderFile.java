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
 * PlaceholderFile.java
 * Copyright (C) 2009-2016 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import adams.core.Placeholders;
import adams.core.management.OS;

/**
 * A specialized File class makes use of system-wide defined placeholders.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @see Placeholders
 */
public class PlaceholderFile
  extends File {

  /** for serialziation. */
  private static final long serialVersionUID = 4767449993057576987L;

  /**
   * Creates a new <code>PlaceholderFile</code> instance by using the given file.
   */
  public PlaceholderFile() {
    this(Placeholders.PLACEHOLDER_START + Placeholders.CWD + Placeholders.PLACEHOLDER_END);
  }

  /**
   * Creates a new <code>PlaceholderFile</code> instance by using the given file.
   *
   * @param   file	the file to use
   */
  public PlaceholderFile(File file) {
    super(Placeholders.collapseStr(fixSeparator(file)));
  }

  /**
   * Creates a new <code>File</code> instance by converting the given
   * pathname string into an abstract pathname.  If the given string is
   * the empty string, then the result is the empty abstract pathname.
   *
   * @param   pathname  A pathname string
   */
  public PlaceholderFile(String pathname) {
    super(Placeholders.collapseStr(fixSeparator(pathname)));
  }

  /**
   * Creates a new <code>File</code> instance from a parent pathname string
   * and a child pathname string.
   *
   * <p> If <code>parent</code> is <code>null</code> then the new
   * <code>File</code> instance is created as if by invoking the
   * single-argument <code>File</code> constructor on the given
   * <code>child</code> pathname string.
   *
   * <p> Otherwise the <code>parent</code> pathname string is taken to denote
   * a directory, and the <code>child</code> pathname string is taken to
   * denote either a directory or a file.  If the <code>child</code> pathname
   * string is absolute then it is converted into a relative pathname in a
   * system-dependent way.  If <code>parent</code> is the empty string then
   * the new <code>File</code> instance is created by converting
   * <code>child</code> into an abstract pathname and resolving the result
   * against a system-dependent default directory.  Otherwise each pathname
   * string is converted into an abstract pathname and the child abstract
   * pathname is resolved against the parent.
   *
   * @param   parent  The parent pathname string
   * @param   child   The child pathname string
   */
  public PlaceholderFile(String parent, String child) {
    super(Placeholders.collapseStr(fixSeparator(parent)), child);
  }

  /**
   * Creates a new <code>File</code> instance from a parent abstract
   * pathname and a child pathname string.
   *
   * <p> If <code>parent</code> is <code>null</code> then the new
   * <code>File</code> instance is created as if by invoking the
   * single-argument <code>File</code> constructor on the given
   * <code>child</code> pathname string.
   *
   * <p> Otherwise the <code>parent</code> abstract pathname is taken to
   * denote a directory, and the <code>child</code> pathname string is taken
   * to denote either a directory or a file.  If the <code>child</code>
   * pathname string is absolute then it is converted into a relative
   * pathname in a system-dependent way.  If <code>parent</code> is the empty
   * abstract pathname then the new <code>File</code> instance is created by
   * converting <code>child</code> into an abstract pathname and resolving
   * the result against a system-dependent default directory.  Otherwise each
   * pathname string is converted into an abstract pathname and the child
   * abstract pathname is resolved against the parent.
   *
   * @param   parent  The parent abstract pathname
   * @param   child   The child pathname string
   */
  public PlaceholderFile(File parent, String child) {
    super(Placeholders.collapseStr(fixSeparator(parent)), child);
  }

  /**
   * Creates a new <tt>File</tt> instance by converting the given
   * <tt>file:</tt> URI into an abstract pathname.
   *
   * <p> The exact form of a <tt>file:</tt> URI is system-dependent, hence
   * the transformation performed by this constructor is also
   * system-dependent.
   *
   * <p> For a given abstract pathname <i>f</i> it is guaranteed that
   *
   * <blockquote><tt>
   * new File(</tt><i>&nbsp;f</i><tt>.{@link #toURI() toURI}()).equals(</tt><i>&nbsp;f</i><tt>.{@link #getAbsoluteFile() getAbsoluteFile}())
   * </tt></blockquote>
   *
   * so long as the original abstract pathname, the URI, and the new abstract
   * pathname are all created in (possibly different invocations of) the same
   * Java virtual machine.  This relationship typically does not hold,
   * however, when a <tt>file:</tt> URI that is created in a virtual machine
   * on one operating system is converted into an abstract pathname in a
   * virtual machine on a different operating system.
   *
   * @param  uri
   *         An absolute, hierarchical URI with a scheme equal to
   *         <tt>"file"</tt>, a non-empty path component, and undefined
   *         authority, query, and fragment components
   */
  public PlaceholderFile(URI uri) {
    super(uri);
  }

  /**
   * Tests whether the application can execute the file denoted by this
   * abstract pathname.
   *
   * @return  <code>true</code> if and only if the abstract pathname exists
   *          <em>and</em> the application is allowed to execute the file
   */
  @Override
  public boolean canExecute() {
    return new File(expand(getPath())).canExecute();
  }

  /**
   * Tests whether the application can read the file denoted by this
   * abstract pathname.
   *
   * @return  <code>true</code> if and only if the file specified by this
   *          abstract pathname exists <em>and</em> can be read by the
   *          application; <code>false</code> otherwise
   */
  @Override
  public boolean canRead() {
    return new File(expand(getPath())).canRead();
  }

  /**
   * Tests whether the application can modify the file denoted by this
   * abstract pathname.
   *
   * @return  <code>true</code> if and only if the file system actually
   *          contains a file denoted by this abstract pathname <em>and</em>
   *          the application is allowed to write to the file;
   *          <code>false</code> otherwise.
   */
  @Override
  public boolean canWrite() {
    return new File(expand(getPath())).canWrite();
  }

  /**
   * Compares two abstract pathnames lexicographically.  The ordering
   * defined by this method depends upon the underlying system.  On UNIX
   * systems, alphabetic case is significant in comparing pathnames; on Microsoft Windows
   * systems it is not.
   *
   * @param   pathname  The abstract pathname to be compared to this abstract
   *                    pathname
   *
   * @return  Zero if the argument is equal to this abstract pathname, a
   *		value less than zero if this abstract pathname is
   *		lexicographically less than the argument, or a value greater
   *		than zero if this abstract pathname is lexicographically
   *		greater than the argument
   */
  @Override
  public int compareTo(File pathname) {
    return getAbsoluteFile().compareTo(pathname.getAbsoluteFile());
  }

  /**
   * Atomically creates a new, empty file named by this abstract pathname if
   * and only if a file with this name does not yet exist.  The check for the
   * existence of the file and the creation of the file if it does not exist
   * are a single operation that is atomic with respect to all other
   * filesystem activities that might affect the file.
   * <P>
   * Note: this method should <i>not</i> be used for file-locking, as
   * the resulting protocol cannot be made to work reliably. The
   * {@link java.nio.channels.FileLock FileLock}
   * facility should be used instead.
   *
   * @return  <code>true</code> if the named file does not exist and was
   *          successfully created; <code>false</code> if the named file
   *          already exists
   *
   * @throws  IOException
   *          If an I/O error occurred
   */
  @Override
  public boolean createNewFile() throws IOException {
    return new File(expand(getPath())).createNewFile();
  }

  /**
   * Deletes the file or directory denoted by this abstract pathname.  If
   * this pathname denotes a directory, then the directory must be empty in
   * order to be deleted.
   *
   * @return  <code>true</code> if and only if the file or directory is
   *          successfully deleted; <code>false</code> otherwise
   */
  @Override
  public boolean delete() {
    return new File(expand(getPath())).delete();
  }

  /**
   * Requests that the file or directory denoted by this abstract
   * pathname be deleted when the virtual machine terminates.
   * Files (or directories) are deleted in the reverse order that
   * they are registered. Invoking this method to delete a file or
   * directory that is already registered for deletion has no effect.
   * Deletion will be attempted only for normal termination of the
   * virtual machine, as defined by the Java Language Specification.
   *
   * <p> Once deletion has been requested, it is not possible to cancel the
   * request.  This method should therefore be used with care.
   *
   * <P>
   * Note: this method should <i>not</i> be used for file-locking, as
   * the resulting protocol cannot be made to work reliably. The
   * {@link java.nio.channels.FileLock FileLock}
   * facility should be used instead.
   */
  @Override
  public void deleteOnExit() {
    new File(expand(getPath())).deleteOnExit();
  }

  /**
   * Tests whether the file or directory denoted by this abstract pathname
   * exists.
   *
   * @return  <code>true</code> if and only if the file or directory denoted
   *          by this abstract pathname exists; <code>false</code> otherwise
   */
  @Override
  public boolean exists() {
    return new File(expand(getPath())).exists();
  }

  /**
   * Returns the canonical pathname string of this abstract pathname.
   *
   * <p> A canonical pathname is both absolute and unique.  The precise
   * definition of canonical form is system-dependent.  This method first
   * converts this pathname to absolute form if necessary, as if by invoking the
   * {@link #getAbsolutePath} method, and then maps it to its unique form in a
   * system-dependent way.  This typically involves removing redundant names
   * such as <tt>"."</tt> and <tt>".."</tt> from the pathname, resolving
   * symbolic links (on UNIX platforms), and converting drive letters to a
   * standard case (on Microsoft Windows platforms).
   *
   * <p> Every pathname that denotes an existing file or directory has a
   * unique canonical form.  Every pathname that denotes a nonexistent file
   * or directory also has a unique canonical form.  The canonical form of
   * the pathname of a nonexistent file or directory may be different from
   * the canonical form of the same pathname after the file or directory is
   * created.  Similarly, the canonical form of the pathname of an existing
   * file or directory may be different from the canonical form of the same
   * pathname after the file or directory is deleted.
   *
   * @return  The canonical pathname string denoting the same file or
   *          directory as this abstract pathname
   *
   * @throws  IOException
   *          If an I/O error occurs, which is possible because the
   *          construction of the canonical pathname may require
   *          filesystem queries
   */
  @Override
  public String getCanonicalPath() throws IOException {
    return new File(expand(getPath())).getCanonicalPath();
  }

  /**
   * Returns the number of unallocated bytes in the partition <a
   * href="#partName">named</a> by this abstract path name.
   *
   * <p> The returned number of unallocated bytes is a hint, but not
   * a guarantee, that it is possible to use most or any of these
   * bytes.  The number of unallocated bytes is most likely to be
   * accurate immediately after this call.  It is likely to be made
   * inaccurate by any external I/O operations including those made
   * on the system outside of this virtual machine.  This method
   * makes no guarantee that write operations to this file system
   * will succeed.
   *
   * @return  The number of unallocated bytes on the partition <tt>0L</tt>
   *          if the abstract pathname does not name a partition.  This
   *          value will be less than or equal to the total file system size
   *          returned by {@link #getTotalSpace}.
   */
  @Override
  public long getFreeSpace() {
    return new File(expand(getPath())).getFreeSpace();
  }

  /**
   * Returns the size of the partition <a href="#partName">named</a> by this
   * abstract pathname.
   *
   * @return  The size, in bytes, of the partition or <tt>0L</tt> if this
   *          abstract pathname does not name a partition
   */
  @Override
  public long getTotalSpace() {
    return new File(expand(getPath())).getTotalSpace();
  }

  /**
   * Returns the number of bytes available to this virtual machine on the
   * partition <a href="#partName">named</a> by this abstract pathname.  When
   * possible, this method checks for write permissions and other operating
   * system restrictions and will therefore usually provide a more accurate
   * estimate of how much new data can actually be written than {@link
   * #getFreeSpace}.
   *
   * <p> The returned number of available bytes is a hint, but not a
   * guarantee, that it is possible to use most or any of these bytes.  The
   * number of unallocated bytes is most likely to be accurate immediately
   * after this call.  It is likely to be made inaccurate by any external
   * I/O operations including those made on the system outside of this
   * virtual machine.  This method makes no guarantee that write operations
   * to this file system will succeed.
   *
   * @return  The number of available bytes on the partition or <tt>0L</tt>
   *          if the abstract pathname does not name a partition.  On
   *          systems where this information is not available, this method
   *          will be equivalent to a call to {@link #getFreeSpace}.
   */
  @Override
  public long getUsableSpace() {
    return new File(expand(getPath())).getUsableSpace();
  }

  /**
   * Tests whether this abstract pathname is absolute.  The definition of
   * absolute pathname is system dependent.  On UNIX systems, a pathname is
   * absolute if its prefix is <code>"/"</code>.  On Microsoft Windows systems, a
   * pathname is absolute if its prefix is a drive specifier followed by
   * <code>"\\"</code>, or if its prefix is <code>"\\\\"</code>.
   *
   * @return  <code>true</code> if this abstract pathname is absolute,
   *          <code>false</code> otherwise
   */
  @Override
  public boolean isAbsolute() {
    return new File(expand(getPath())).isAbsolute();
  }

  /**
   * Tests whether the file denoted by this abstract pathname is a
   * directory.
   *
   * @return <code>true</code> if and only if the file denoted by this
   *          abstract pathname exists <em>and</em> is a directory;
   *          <code>false</code> otherwise
   */
  @Override
  public boolean isDirectory() {
    return new File(expand(getPath())).isDirectory();
  }

  /**
   * Tests whether the file denoted by this abstract pathname is a normal
   * file.  A file is <em>normal</em> if it is not a directory and, in
   * addition, satisfies other system-dependent criteria.  Any non-directory
   * file created by a Java application is guaranteed to be a normal file.
   *
   * @return  <code>true</code> if and only if the file denoted by this
   *          abstract pathname exists <em>and</em> is a normal file;
   *          <code>false</code> otherwise
   */
  @Override
  public boolean isFile() {
    return new File(expand(getPath())).isFile();
  }

  /**
   * Tests whether the file named by this abstract pathname is a hidden
   * file.  The exact definition of <em>hidden</em> is system-dependent.  On
   * UNIX systems, a file is considered to be hidden if its name begins with
   * a period character (<code>'.'</code>).  On Microsoft Windows systems, a file is
   * considered to be hidden if it has been marked as such in the filesystem.
   *
   * @return  <code>true</code> if and only if the file denoted by this
   *          abstract pathname is hidden according to the conventions of the
   *          underlying platform
   */
  @Override
  public boolean isHidden() {
    return new File(expand(getPath())).isHidden();
  }

  /**
   * Returns the time that the file denoted by this abstract pathname was
   * last modified.
   *
   * @return  A <code>long</code> value representing the time the file was
   *          last modified, measured in milliseconds since the epoch
   *          (00:00:00 GMT, January 1, 1970), or <code>0L</code> if the
   *          file does not exist or if an I/O error occurs
   */
  @Override
  public long lastModified() {
    return new File(expand(getPath())).lastModified();
  }

  /**
   * Returns the length of the file denoted by this abstract pathname.
   * The return value is unspecified if this pathname denotes a directory.
   *
   * @return  The length, in bytes, of the file denoted by this abstract
   *          pathname, or <code>0L</code> if the file does not exist.  Some
   *          operating systems may return <code>0L</code> for pathnames
   *          denoting system-dependent entities such as devices or pipes.
   */
  @Override
  public long length() {
    return new File(expand(getPath())).length();
  }

  /**
   * Returns an array of strings naming the files and directories in the
   * directory denoted by this abstract pathname.
   *
   * <p> If this abstract pathname does not denote a directory, then this
   * method returns <code>null</code>.  Otherwise an array of strings is
   * returned, one for each file or directory in the directory.  Names
   * denoting the directory itself and the directory's parent directory are
   * not included in the result.  Each string is a file name rather than a
   * complete path.
   *
   * <p> There is no guarantee that the name strings in the resulting array
   * will appear in any specific order; they are not, in particular,
   * guaranteed to appear in alphabetical order.
   *
   * @return  An array of strings naming the files and directories in the
   *          directory denoted by this abstract pathname.  The array will be
   *          empty if the directory is empty.  Returns <code>null</code> if
   *          this abstract pathname does not denote a directory, or if an
   *          I/O error occurs.
   */
  @Override
  public String[] list() {
    return new File(expand(getPath())).list();
  }

  /**
   * Returns an array of strings naming the files and directories in the
   * directory denoted by this abstract pathname, as long as they match
   * the regular expression.
   *
   * <p> If this abstract pathname does not denote a directory, then this
   * method returns <code>null</code>.  Otherwise an array of strings is
   * returned, one for each file or directory in the directory.  Names
   * denoting the directory itself and the directory's parent directory are
   * not included in the result.  Each string is a file name rather than a
   * complete path.
   *
   * <p> There is no guarantee that the name strings in the resulting array
   * will appear in any specific order; they are not, in particular,
   * guaranteed to appear in alphabetical order.
   *
   * @param regExp The regular expression that the filenames must match.
   * @return  An array of strings naming the files and directories in the
   *          directory denoted by this abstract pathname.  The array will be
   *          empty if the directory is empty.  Returns <code>null</code> if
   *          this abstract pathname does not denote a directory, or if an
   *          I/O error occurs.
   */
  public String[] list(String regExp) {
    String[]		list;
    List<String>	result;
    int			i;

    result = new ArrayList<String>();
    list   = new File(expand(getPath())).list();
    for (i = 0; i < list.length; i++) {
      if (list[i].matches(regExp))
	result.add(list[i]);
    }

    return result.toArray(new String[result.size()]);
  }

  /**
   * Turns the regular {@link File} objects into {@link PlaceholderFile} ones.
   *
   * @param files	the files to convert
   * @return		the converted files
   */
  protected PlaceholderFile[] toPlaceholderFiles(File[] files) {
    PlaceholderFile[]	result;
    int			i;

    if (files == null)
      return null;

    result = new PlaceholderFile[files.length];
    for (i = 0; i < files.length; i++)
      result[i] = new PlaceholderFile(files[i]);

    return result;
  }

  /**
   * Returns an array of abstract pathnames denoting the files in the
   * directory denoted by this abstract pathname.
   *
   * <p> If this abstract pathname does not denote a directory, then this
   * method returns <code>null</code>.  Otherwise an array of
   * <code>File</code> objects is returned, one for each file or directory in
   * the directory.  Pathnames denoting the directory itself and the
   * directory's parent directory are not included in the result.  Each
   * resulting abstract pathname is constructed from this abstract pathname
   * using the <code>{@link #File(java.io.File, java.lang.String)
   * File(File,&nbsp;String)}</code> constructor.  Therefore if this pathname
   * is absolute then each resulting pathname is absolute; if this pathname
   * is relative then each resulting pathname will be relative to the same
   * directory.
   *
   * <p> There is no guarantee that the name strings in the resulting array
   * will appear in any specific order; they are not, in particular,
   * guaranteed to appear in alphabetical order.
   *
   * @return  An array of abstract pathnames denoting the files and
   *          directories in the directory denoted by this abstract
   *          pathname.  The array will be empty if the directory is
   *          empty.  Returns <code>null</code> if this abstract pathname
   *          does not denote a directory, or if an I/O error occurs.
   */
  @Override
  public PlaceholderFile[] listFiles() {
    return toPlaceholderFiles(new File(expand(getPath())).listFiles());
  }

  /**
   * Returns an array of abstract pathnames denoting the files and
   * directories in the directory denoted by this abstract pathname that
   * satisfy the specified filter.  The behavior of this method is the
   * same as that of the <code>{@link #listFiles()}</code> method, except
   * that the pathnames in the returned array must satisfy the filter.
   * If the given <code>filter</code> is <code>null</code> then all
   * pathnames are accepted.  Otherwise, a pathname satisfies the filter
   * if and only if the value <code>true</code> results when the
   * <code>{@link FilenameFilter#accept}</code> method of the filter is
   * invoked on this abstract pathname and the name of a file or
   * directory in the directory that it denotes.
   *
   * @param  filter  A filename filter
   *
   * @return  An array of abstract pathnames denoting the files and
   *          directories in the directory denoted by this abstract
   *          pathname.  The array will be empty if the directory is
   *          empty.  Returns <code>null</code> if this abstract pathname
   *          does not denote a directory, or if an I/O error occurs.
   *
   * @throws  SecurityException
   *          If a security manager exists and its <code>{@link
   *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
   *          method denies read access to the directory
   *
   * @since 1.2
   */
  @Override
  public PlaceholderFile[] listFiles(FilenameFilter filter) {
    return toPlaceholderFiles(new File(expand(getPath())).listFiles(filter));
  }

  /**
   * Returns an array of abstract pathnames denoting the files and
   * directories in the directory denoted by this abstract pathname that
   * satisfy the specified filter.  The behavior of this method is the
   * same as that of the <code>{@link #listFiles()}</code> method, except
   * that the pathnames in the returned array must satisfy the filter.
   * If the given <code>filter</code> is <code>null</code> then all
   * pathnames are accepted.  Otherwise, a pathname satisfies the filter
   * if and only if the value <code>true</code> results when the
   * <code>{@link FileFilter#accept(java.io.File)}</code> method of
   * the filter is invoked on the pathname.
   *
   * @param  filter  A file filter
   *
   * @return  An array of abstract pathnames denoting the files and
   *          directories in the directory denoted by this abstract
   *          pathname.  The array will be empty if the directory is
   *          empty.  Returns <code>null</code> if this abstract pathname
   *          does not denote a directory, or if an I/O error occurs.
   *
   * @throws  SecurityException
   *          If a security manager exists and its <code>{@link
   *          java.lang.SecurityManager#checkRead(java.lang.String)}</code>
   *          method denies read access to the directory
   *
   * @since 1.2
   */
  @Override
  public PlaceholderFile[] listFiles(FileFilter filter) {
    return toPlaceholderFiles(new File(expand(getPath())).listFiles(filter));
  }

  /**
   * Creates the directory named by this abstract pathname.
   *
   * @return  <code>true</code> if and only if the directory was
   *          created; <code>false</code> otherwise
   */
  @Override
  public boolean mkdir() {
    return new File(expand(getPath())).mkdir();
  }

  /**
   * Renames the file denoted by this abstract pathname.
   *
   * <p> Many aspects of the behavior of this method are inherently
   * platform-dependent: The rename operation might not be able to move a
   * file from one filesystem to another, it might not be atomic, and it
   * might not succeed if a file with the destination abstract pathname
   * already exists.  The return value should always be checked to make sure
   * that the rename operation was successful.
   *
   * @param  dest  The new abstract pathname for the named file
   *
   * @return  <code>true</code> if and only if the renaming succeeded;
   *          <code>false</code> otherwise
   */
  @Override
  public boolean renameTo(File dest) {
    File from = new File(expand(getPath()));
    File to = new File(expand(dest.getPath()));
    return from.renameTo(to);
  }

  /**
   * Sets the owner's or everybody's execute permission for this abstract
   * pathname.
   *
   * @param   executable
   *          If <code>true</code>, sets the access permission to allow execute
   *          operations; if <code>false</code> to disallow execute operations
   *
   * @param   ownerOnly
   *          If <code>true</code>, the execute permission applies only to the
   *          owner's execute permission; otherwise, it applies to everybody.
   *          If the underlying file system can not distinguish the owner's
   *          execute permission from that of others, then the permission will
   *          apply to everybody, regardless of this value.
   *
   * @return  <code>true</code> if and only if the operation succeeded.  The
   *          operation will fail if the user does not have permission to
   *          change the access permissions of this abstract pathname.  If
   *          <code>executable</code> is <code>false</code> and the underlying
   *          file system does not implement an execute permission, then the
   *          operation will fail.
   */
  @Override
  public boolean setExecutable(boolean executable, boolean ownerOnly) {
    return new File(expand(getPath())).setExecutable(executable, ownerOnly);
  }

  /**
   * Sets the last-modified time of the file or directory named by this
   * abstract pathname.
   *
   * <p> All platforms support file-modification times to the nearest second,
   * but some provide more precision.  The argument will be truncated to fit
   * the supported precision.  If the operation succeeds and no intervening
   * operations on the file take place, then the next invocation of the
   * <code>{@link #lastModified}</code> method will return the (possibly
   * truncated) <code>time</code> argument that was passed to this method.
   *
   * @param  time  The new last-modified time, measured in milliseconds since
   *               the epoch (00:00:00 GMT, January 1, 1970)
   *
   * @return <code>true</code> if and only if the operation succeeded;
   *          <code>false</code> otherwise
   */
  @Override
  public boolean setLastModified(long time) {
    return new File(expand(getPath())).setLastModified(time);
  }

  /**
   * Sets the owner's or everybody's read permission for this abstract
   * pathname.
   *
   * @param   readable
   *          If <code>true</code>, sets the access permission to allow read
   *          operations; if <code>false</code> to disallow read operations
   *
   * @param   ownerOnly
   *          If <code>true</code>, the read permission applies only to the
   *          owner's read permission; otherwise, it applies to everybody.  If
   *          the underlying file system can not distinguish the owner's read
   *          permission from that of others, then the permission will apply to
   *          everybody, regardless of this value.
   *
   * @return  <code>true</code> if and only if the operation succeeded.  The
   *          operation will fail if the user does not have permission to
   *          change the access permissions of this abstract pathname.  If
   *          <code>readable</code> is <code>false</code> and the underlying
   *          file system does not implement a read permission, then the
   *          operation will fail.
   */
  @Override
  public boolean setReadable(boolean readable, boolean ownerOnly) {
    return new File(expand(getPath())).setReadable(readable, ownerOnly);
  }

  /**
   * Marks the file or directory named by this abstract pathname so that
   * only read operations are allowed.  After invoking this method the file
   * or directory is guaranteed not to change until it is either deleted or
   * marked to allow write access.  Whether or not a read-only file or
   * directory may be deleted depends upon the underlying system.
   *
   * @return <code>true</code> if and only if the operation succeeded;
   *          <code>false</code> otherwise
   */
  @Override
  public boolean setReadOnly() {
    return new File(expand(getPath())).setReadOnly();
  }

  /**
    * Sets the owner's or everybody's write permission for this abstract
    * pathname.
    *
    * @param   writable
    *          If <code>true</code>, sets the access permission to allow write
    *          operations; if <code>false</code> to disallow write operations
    *
    * @param   ownerOnly
    *          If <code>true</code>, the write permission applies only to the
    *          owner's write permission; otherwise, it applies to everybody.  If
    *          the underlying file system can not distinguish the owner's write
    *          permission from that of others, then the permission will apply to
    *          everybody, regardless of this value.
    *
    * @return  <code>true</code> if and only if the operation succeeded. The
    *          operation will fail if the user does not have permission to change
    *          the access permissions of this abstract pathname.
    */
   @Override
  public boolean setWritable(boolean writable, boolean ownerOnly) {
     return new File(expand(getPath())).setWritable(writable, ownerOnly);
   }

  /**
   * Returns the pathname string of this abstract pathname's parent, or
   * <code>null</code> if this pathname does not name a parent directory.
   *
   * <p> The <em>parent</em> of an abstract pathname consists of the
   * pathname's prefix, if any, and each name in the pathname's name
   * sequence except for the last.  If the name sequence is empty then
   * the pathname does not name a parent directory.
   *
   * @return  The pathname string of the parent directory named by this
   *          abstract pathname, or <code>null</code> if this pathname
   *          does not name a parent
   */
  @Override
  public String getParent() {
    return new File(expand(getPath())).getParent();
  }

  /**
   * Returns the abstract pathname of this abstract pathname's parent,
   * or <code>null</code> if this pathname does not name a parent
   * directory.
   *
   * <p> The <em>parent</em> of an abstract pathname consists of the
   * pathname's prefix, if any, and each name in the pathname's name
   * sequence except for the last.  If the name sequence is empty then
   * the pathname does not name a parent directory.
   *
   * @return  The abstract pathname of the parent directory named by this
   *          abstract pathname, or <code>null</code> if this pathname
   *          does not name a parent
   *
   * @since 1.2
   */
  @Override
  public File getParentFile() {
    String p = getParent();
    if (p == null)
      return null;
    return new PlaceholderFile(p);
  }

  /**
   * Expands any placeholders, if necessary.
   *
   * @param path	the path to expand (if necessary)
   * @return		the expanded path
   */
  protected String expand(String path) {
    String	result;

    result = path;

    if (result.indexOf(Placeholders.PLACEHOLDER_START) > -1)
      result = Placeholders.getSingleton().expand(path);

    return result;
  }

  /**
   * Returns the absolute pathname string of this abstract pathname.
   * This also replaces existing placeholders with the actual values.
   *
   * @return  The absolute pathname string denoting the same file or
   *          directory as this abstract pathname
   */
  @Override
  public String getAbsolutePath() {
    String	result;
    File	file;

    file   = new File(expand(getPath()));
    result = file.getAbsolutePath();

    return result;
  }

  /**
   * Fixes the separator in the given file, changing backslashes to forward
   * slashes under Linux and vice versa under Windows.
   *
   * @param file	the path to process
   * @return		the fixed path
   */
  protected static String fixSeparator(File file) {
    try {
      return fixSeparator(file.getCanonicalPath());
    }
    catch (Exception e) {
      return fixSeparator(file.getAbsolutePath());
    }
  }

  /**
   * Fixes the separator in the given string, changing backslashes to forward
   * slashes under Linux and vice versa under Windows.
   *
   * @param path		the path to process
   * @return		the fixed path
   */
  protected static String fixSeparator(String path) {
    String	prefix;

    if (path == null)
      return null;
    
    // UNC?
    prefix = "";
    if (path.startsWith("\\\\")) {
      prefix = "\\\\";
      path   = path.substring(2);
    }
    else if (path.startsWith("//")) {
      prefix = "\\\\";
      path   = path.substring(2);
    }

    if (OS.isWindows())
      path = path.replace('/', '\\');
    else
      path = path.replace('\\', '/');

    return prefix + path;
  }

  /**
   * Checks whether the placeholer file is valid.
   *
   * @param file	the file to check
   * @return		true if valid
   */
  public static boolean isValid(String file) {
    return isValid(new PlaceholderFile(file));
  }

  /**
   * Checks whether the placeholer file is valid.
   *
   * @param file	the file to check
   * @return		true if valid
   */
  public static boolean isValid(PlaceholderFile file) {
    try {
      file.getAbsolutePath();
      return true;
    }
    catch (Exception e) {
      return false;
    }
  }

  /**
   * Returns the extension, i.e., the string after the last dot. If extension
   * is from a compressed file, like "file.tar.gz", this will return only "gz".
   *
   * @return		the extension (without the dot), empty string if not available
   * @see		#getExtension(String)
   */
  public String getExtension() {
    return getExtension("");
  }

  /**
   * Returns the extension, i.e., the string after the last dot. Skips the
   * suffix (for instance for compressed files, this could be ".gz" or "gz")
   * before locating the next ".".
   *
   * @param suffix	the suffix to skip. with or without preceding dot, as it
   * 			gets automatically prepended if missing
   * @return		the extension (without the dot), empty string if not available
   */
  public String getExtension(String suffix) {
    int		pos;
    int		start;

    // make sure the suffix starts
    if ((suffix.length() > 0) && !suffix.startsWith("."))
      suffix = "." + suffix;

    start = getName().length() - suffix.length() - 1;
    pos   = getName().lastIndexOf('.', start);
    if (pos > -1)
      return getName().substring(pos + 1);
    else
      return "";
  }

  /**
   * Swaps the current extension with the new one. Uses {@link #getExtension()}
   * to determine the old extension.
   *
   * @param newExt	the new extension to use
   * @return		the file with the new extension
   * @see		#changeExtension(String, String)
   */
  public PlaceholderFile changeExtension(String newExt) {
    return changeExtension(getExtension(), newExt);
  }

  /**
   * Swaps the specified old extension with the new one.
   *
   * @param oldExt	the old extension to replace
   * @param newExt	the new extension to use
   * @return		the file with the new extension
   */
  public PlaceholderFile changeExtension(String oldExt, String newExt) {
    return new PlaceholderFile(getPath() + separator + getName().replaceAll(oldExt + "$", newExt));
  }
  
  /**
   * Turns the file object into a {@link Path} object.
   * 
   * @return		the path object
   */
  @Override
  public Path toPath() {
    return Paths.get(toURI());
  }
}

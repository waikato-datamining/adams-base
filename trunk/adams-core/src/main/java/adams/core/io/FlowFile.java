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
 * FlowFile.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.core.io;

import java.io.File;
import java.net.URI;

/**
 * A specialized File class used for flows.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FlowFile
  extends PlaceholderFile {

  /** for serialziation. */
  private static final long serialVersionUID = 4767449993057576987L;

  /**
   * Creates a new <code>FlowFile</code> instance by using the given file.
   *
   * @param   file	the file to use
   */
  public FlowFile(File file) {
    super(file.getPath());
  }

  /**
   * Creates a new <code>File</code> instance by converting the given
   * pathname string into an abstract pathname.  If the given string is
   * the empty string, then the result is the empty abstract pathname.
   *
   * @param   pathname  A pathname string
   */
  public FlowFile(String pathname) {
    super(pathname);
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
  public FlowFile(String parent, String child) {
    super(parent, child);
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
  public FlowFile(File parent, String child) {
    super(parent, child);
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
  public FlowFile(URI uri) {
    super(uri);
  }
}

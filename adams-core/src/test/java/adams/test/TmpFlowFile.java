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
 * TmpFlowFile.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.test;

import adams.core.io.FlowFile;

/**
 * A simple file handler class (for flow files) that automatically places the
 * file in the system's tmp directory. But the user still needs to delete the
 * file manually when it's no longer used.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class TmpFlowFile
  extends FlowFile {

  /** for serialization. */
  private static final long serialVersionUID = 7689124528783850812L;

  /**
   * Creates a new <code>File</code> instance by converting the given
   * pathname string into an abstract pathname.  If the given string is
   * the empty string, then the result is the empty abstract pathname.
   *
   * @param   pathname  A pathname string
   */
  public TmpFlowFile(String pathname) {
    super(System.getProperty("java.io.tmpdir") + separator + pathname);
  }
}

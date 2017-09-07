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
 * Size.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.core.io.filechanged;

import java.io.File;

/**
 * Uses the file size.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class Size
  extends AbstractFileChangeMonitor {

  private static final long serialVersionUID = 7861456311356953324L;

  /** the size. */
  protected long m_Size;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Just uses the 'size' information of the file.";
  }

  /**
   * Resets the scheme.
   */
  @Override
  protected void reset() {
    super.reset();
    m_Size = 0;
  }

  /**
   * Performs the actual initialization of the monitor with the specified file.
   *
   * @param file	the file to initialize with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doInitialize(File file) {
    m_Size = file.length();
    return null;
  }

  /**
   * Performs the actual check whether the file has changed.
   *
   * @param file	the file to check
   * @return		true if changed
   */
  @Override
  protected boolean checkChange(File file) {
    return (m_Size != file.length());
  }

  /**
   * Performs the actual updating of the monitor with the specified file.
   *
   * @param file	the file to update with
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doUpdate(File file) {
    return doInitialize(file);
  }
}

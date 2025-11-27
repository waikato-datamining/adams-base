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
 * MessageDigest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.core.io.dirchanged;

import adams.core.MessageCollection;

import java.io.File;

/**
 * Generates a message digest and uses that for comparison.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MessageDigest
  extends AbstractMessageDigestBasedMonitor {

  private static final long serialVersionUID = 7861456311356953324L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Generates a message digest and uses that for comparison.";
  }

  /**
   * Generates the message digest, if possible.
   *
   * @param file	the file to generate the digest for
   * @param errors	for collecting any errors
   * @return		the digest
   */
  @Override
  protected String computeDigest(File file, MessageCollection errors) {
    return m_Type.digest(file, errors);
  }
}

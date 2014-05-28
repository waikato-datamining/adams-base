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
 * NullFilenameGenerator.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

/**
 * Dummy filename generator, always outputs null.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NullFilenameGenerator
  extends AbstractFilenameGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 4174627296132350412L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Dummy generator, always outputs null.";
  }
  
  /**
   * Returns whether we actually need an object to generate the filename.
   * 
   * @return		true if object required
   */
  @Override
  public boolean canHandleNullObject() {
    return true;
  }

  /**
   * Performs the actual generation of the filename.
   *
   * @param obj		the object to generate the filename for
   * @return		always null
   */
  @Override
  protected String doGenerate(Object obj) {
    return null;
  }
}

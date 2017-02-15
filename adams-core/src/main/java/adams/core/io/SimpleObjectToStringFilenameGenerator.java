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
 * SimpleObjectToStringFilenameGenerator.java
 * Copyright (C) 2017 University of Waikato, Hamilton, New Zealand
 */
package adams.core.io;

/**
 <!-- globalinfo-start -->
 * Simple generator that just turns the incoming object into a string using the 'toString()' method.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 * 
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class SimpleObjectToStringFilenameGenerator
  extends AbstractFilenameGenerator {

  /** for serialization. */
  private static final long serialVersionUID = 6313170021657883586L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return 
	"Simple generator that just turns the incoming object into a string using the 'toString()' method.";
  }

  /**
   * Returns whether we actually need an object to generate the filename.
   * 
   * @return		true if object required
   */
  @Override
  public boolean canHandleNullObject() {
    return false;
  }

  /**
   * Performs the actual generation of the filename.
   *
   * @param obj		the object to generate the filename for
   * @return		the generated filename
   */
  @Override
  protected String doGenerate(Object obj) {
    return obj.toString();
  }
}

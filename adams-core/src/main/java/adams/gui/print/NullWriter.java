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
 * NullWriter.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.print;

/**
 * Dummy - generates no output.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class NullWriter
  extends JComponentWriter {

  /** for serialization. */
  private static final long serialVersionUID = 6067496733898037178L;

  /**
   * Returns a string describing the object.
   * 
   * @return 		a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Dummy - generates no output.";
  }

  /**
   * returns the name of the writer, to display in the FileChooser.
   * 
   * @return 		the name of the writer
   */
  public String getDescription() {
    return "";
  }
  
  /**
   * returns no extensions.
   * 
   * @return 		zero-length array
   */
  public String[] getExtensions() {
    return new String[]{};
  }
  
  /**
   * generates the actual output.
   * 
   * @throws Exception	if something goes wrong
   */
  public void generateOutput() throws Exception {
  }
}

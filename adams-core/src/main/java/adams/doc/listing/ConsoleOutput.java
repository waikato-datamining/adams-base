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
 * ConsoleOutput.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.doc.listing;

import java.util.List;
import java.util.Map;

/**
 * Outputs the listings in simple plain text in the console.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ConsoleOutput
  extends AbstractListingOutput {

  private static final long serialVersionUID = 3968266411637947086L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Outputs the listings in simple plain text in the console.";
  }

  /**
   * Outputs the supplied listing.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing to output (module -> classnames)
   * @return		null if successful, otherwise error message
   */
  @Override
  protected String doGenerate(Class superclass, Map<String, List<String>> listing) {
    for (String module: getModules(listing)) {
      System.out.println(module);
      for (String classname: listing.get(module))
        System.out.println("- " + classname);
      System.out.println();
    }
    return null;
  }
}

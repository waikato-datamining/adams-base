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
 * AbstractListingOutput.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.doc.listing;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Ancestor for classes that output the generated lists.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractListingOutput
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = -424271238836477812L;

  /**
   * Returns a quick info about the class, which will be displayed in the GUI.
   * <br>
   * Default implementation just returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  @Override
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks before generating the output.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing data to check
   * @return		null if successful, otherwise error message
   */
  protected String check(Class superclass, Map<String,List<String>> listing) {
    if (superclass == null)
      return "No superclass provided!";
    if (listing == null)
      return "No listing provided!";
    return null;
  }

  /**
   * Returns the modules in a sorted list.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing to get the modules from
   * @return		the sorted list
   */
  protected List<String> getModules(Map<String,List<String>> listing) {
    List<String>	result;

    result = new ArrayList<>(listing.keySet());
    Collections.sort(result);

    return result;
  }

  /**
   * Outputs the supplied listing.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing to output (module -> classnames)
   * @return		null if successful, otherwise error message
   */
  protected abstract String doGenerate(Class superclass, Map<String,List<String>> listing);

  /**
   * Outputs the supplied listing.
   *
   * @param superclass 	the superclass this listing is for
   * @param listing	the listing to output (module -> classnames)
   * @return		null if successful, otherwise error message
   */
  public String generate(Class superclass, Map<String,List<String>> listing) {
    String	result;

    result = check(superclass, listing);
    if (result == null)
      result = doGenerate(superclass, listing);

    return result;
  }
}

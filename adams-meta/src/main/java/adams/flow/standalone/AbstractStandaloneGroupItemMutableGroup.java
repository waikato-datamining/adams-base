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
 * AbstractStandaloneGroupItemMutableGroup.java
 * Copyright (C) 2015-2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.core.Utils;
import adams.flow.core.Actor;
import nz.ac.waikato.cms.locator.ClassLocator;

/**
 * Ancestor for group items that form a group themselves.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 * @param <S> the type of the accepted sub-actors
 * @param <E> the type of the enclosing actor
 */
public abstract class AbstractStandaloneGroupItemMutableGroup<S extends Actor, E extends Actor>
  extends AbstractStandaloneMutableGroup<S>
  implements StandaloneGroupItem<E> {

  private static final long serialVersionUID = -7663947357164807918L;

  /**
   * Performs checks on the "sub-actors".
   *
   * @return		null if everything is fine, otherwise the error
   */
  @Override
  public String check() {
    String	result;
    int		i;
    Class[]	filter;
    int		n;
    boolean	match;

    result = null;

    filter = getActorFilter();
    for (i = 0; i < size(); i++) {
      if (get(i).getSkip())
	continue;
      match = false;
      for (n = 0; n < filter.length; n++) {
        if (ClassLocator.isSubclass(filter[n], get(i).getClass())) {
          match = true;
          break;
	}
      }
      if (!match)
	result = "Not allowed actor type: " + get(i).getClass().getName() + "\nAllowed: " + Utils.classesToString(filter);
      break;
    }

    return result;
  }
}

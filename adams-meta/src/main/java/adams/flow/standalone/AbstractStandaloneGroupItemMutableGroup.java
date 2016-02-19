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
 * AbstractStandaloneGroupItemMutableGroup.java
 * Copyright (C) 2015-2016 University of Waikato, Hamilton, NZ
 */

package adams.flow.standalone;

import adams.flow.core.Actor;

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
}

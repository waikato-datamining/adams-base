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
 * DefaultActorComparator.java
 * Copyright (C) 2018 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Simply uses the (lowercase) name of the actors for comparison.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultActorComparator
  implements ActorComparator {

  /**
   * For comparing actors.
   *
   * @param o1		the first actor
   * @param o2		the second actor
   * @return		less than, equal to, or greater than if the first actor
   * 			is less than, equal to, or greater than the second actor
   */
  @Override
  public int compare(Actor o1, Actor o2) {
    return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
  }
}

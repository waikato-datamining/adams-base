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
 * LocatedObjectFilter.java
 * Copyright (C) 2021 University of Waikato, Hamilton, NZ
 */

package adams.flow.transformer.locateobjects;

/**
 * Interface for filters that either accept or reject a located object.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface LocatedObjectFilter {

  /**
   * Whether to accept the located object.
   *
   * @param obj		the object to check
   * @return		true if accepted
   */
  public boolean accept(LocatedObject obj);
}

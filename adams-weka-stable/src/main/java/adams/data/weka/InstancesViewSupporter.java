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
 * InstancesViewSupporter.java
 * Copyright (C) 2016 University of Waikato, Hamilton, NZ
 */

package adams.data.weka;

/**
 * Interface for classes that support Weka Instances views.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface InstancesViewSupporter {

  /**
   * Sets whether to uses views.
   *
   * @param value	true if to use view
   */
  public void setUseViews(boolean value);

  /**
   * Returns whether to use views.
   *
   * @return		true if to use views
   */
  public boolean getUseViews();
}

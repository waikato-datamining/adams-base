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
 * ReloadableContainerManager.java
 * Copyright (C) 2009 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.container;

/**
 * For container managers that can may be able to reload their contents
 * from the database.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public interface ReloadableContainerManager {
  
  /**
   * Sets whether the spectrums are reloadable (from the database) or not.
   * 
   * @param value	true if the spectrums can be reloaded
   */
  public void setReloadable(boolean value);
  
  /**
   * Returns whether the spectrums can be reloaded from the database or not.
   * 
   * @return		true if the spectrums can be reloaded
   */
  public boolean isReloadable();
}

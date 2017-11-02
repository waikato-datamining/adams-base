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
 * DynamicInitializer.java
 * Copyright (C) 2017 University of Waikato, Hamilton, NZ
 */

package adams.flow.core;

/**
 * Interface for actors that can be initialized at runtime and files may not
 * be present at startup time.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public interface DynamicInitializer
  extends Actor {

  /**
   * Sets whether the initialization happens at runtime and may not be present
   * at startup time.
   *
   * @param value	true if initializing at runtime
   */
  public void setOnTheFly(boolean value);

  /**
   * Returns whether the initialization happens at runtime and may not be present
   * at startup time.
   *
   * @return		true if initializing at runtime
   */
  public boolean getOnTheFly();

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String onTheFlyTipText();
}

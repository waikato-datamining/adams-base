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
 * InteractionDisplayLocationSupporter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.core;

import adams.core.option.OptionHandler;

/**
 * Interface for classes that allow the user to choose where to display their interaction.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface InteractionDisplayLocationSupporter
  extends OptionHandler {

  /**
   * Sets where the interaction is being displayed.
   *
   * @param value	the location
   */
  public void setDisplayLocation(InteractionDisplayLocation value);

  /**
   * Returns where the interaction is being displayed.
   *
   * @return 		the location
   */
  public InteractionDisplayLocation getDisplayLocation();

  /**
   * Returns the tip text for this property.
   *
   * @return		tip text for this property suitable for
   *             	displaying in the GUI or for listing the options.
   */
  public String displayLocationTipText();
}

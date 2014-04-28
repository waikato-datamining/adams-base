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
 * PeriodicityColorProvider.java
 * Copyright (C) 2013 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.timeseries;

import adams.gui.core.ColorHelper;
import adams.gui.visualization.core.AbstractColorProvider;

/**
 * Simple color provider to highlight periodicity. Just has two grey colors.
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class PeriodicityColorProvider
  extends AbstractColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = 6995476780170054237L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Simple color provider for the periodicity background (two grey colors).";
  }

  /**
   * Initializes the members.
   */
  @Override
  protected void initialize() {
    super.initialize();
    
    m_AllowDarkening = false;
    
    m_DefaultColors.add(ColorHelper.valueOf("#CCCCCC"));
    m_DefaultColors.add(ColorHelper.valueOf("#DDDDDD"));
  }
}

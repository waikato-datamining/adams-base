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
 * DefaultColorProvider.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * The default color provider, which is used in various plots.
 * <p/>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * Valid options are: <p/>
 *
 * <pre>-D &lt;int&gt; (property: debugLevel)
 * &nbsp;&nbsp;&nbsp;The greater the number the more additional info the scheme may output to
 * &nbsp;&nbsp;&nbsp;the console (0 = off).
 * &nbsp;&nbsp;&nbsp;default: 0
 * &nbsp;&nbsp;&nbsp;minimum: 0
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class DefaultColorProvider
  extends AbstractColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "The default color provider, which is used in various plots.";
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_DefaultColors.add(Color.BLUE);
    m_DefaultColors.add(Color.CYAN);
    m_DefaultColors.add(Color.GREEN);
    m_DefaultColors.add(Color.MAGENTA);
    m_DefaultColors.add(Color.ORANGE);
    m_DefaultColors.add(Color.PINK);
    m_DefaultColors.add(Color.RED);
  }
}

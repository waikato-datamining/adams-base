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
 * FluoroColorProvider.java
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */
package adams.gui.visualization.core;

import java.awt.Color;

/**
 <!-- globalinfo-start -->
 * Uses colors defined here:<br>
 * https:&#47;&#47;github.com&#47;jfree&#47;flowplot&#47;blob&#47;main&#47;src&#47;main&#47;java&#47;org&#47;jfree&#47;chart&#47;plot&#47;flow&#47;demo&#47;FlowPlotDemo1.java
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * &nbsp;&nbsp;&nbsp;min-user-mode: Expert
 * </pre>
 *
 <!-- options-end -->
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class FluoroColorProvider
  extends AbstractColorProvider {

  /** for serialization. */
  private static final long serialVersionUID = -6184352647827352221L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  public String globalInfo() {
    return "Uses colors defined here:\n"
	     + "https://github.com/jfree/flowplot/blob/main/src/main/java/org/jfree/chart/plot/flow/demo/FlowPlotDemo1.java";
  }

  /**
   * Initializes the members.
   */
  protected void initialize() {
    super.initialize();

    m_DefaultColors.add(new Color(108, 236, 137));
    m_DefaultColors.add(new Color(253, 187, 46));
    m_DefaultColors.add(new Color(56, 236, 216));
    m_DefaultColors.add(new Color(171, 231, 51));
    m_DefaultColors.add(new Color(221, 214, 74));
    m_DefaultColors.add(new Color(106, 238, 70));
    m_DefaultColors.add(new Color(172, 230, 100));
    m_DefaultColors.add(new Color(242, 191, 82));
    m_DefaultColors.add(new Color(221, 233, 56));
    m_DefaultColors.add(new Color(242, 206, 47));
  }
}

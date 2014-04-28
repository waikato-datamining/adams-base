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
 * HistogramPanel.java
 * Copyright (C) 2011 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.stats.histogram;

import adams.gui.visualization.core.PlotPanel;
import adams.gui.visualization.core.axis.Visibility;
import adams.gui.visualization.core.plot.Axis;

/**
 * Panel for displaying the histogram data.
 *
 * @author msf8
 * @version $Revision$
 */
public class HistogramPanel
extends PlotPanel{

  /** for serialization */
  private static final long serialVersionUID = -5092764988729474925L;

  @Override
  protected void initGUI() {
    super.initGUI();
    setAxisVisibility(Axis.LEFT, Visibility.VISIBLE);
    setAxisVisibility(Axis.BOTTOM, Visibility.VISIBLE);
    setPanningEnabled(false);
    setZoomingEnabled(false);
  }
}
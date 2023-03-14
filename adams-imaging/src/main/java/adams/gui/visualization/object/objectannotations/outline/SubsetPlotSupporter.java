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
 * SubsetPlotSupporter.java
 * Copyright (C) 2023 University of Waikato, Hamilton, New Zealand
 */

package adams.gui.visualization.object.objectannotations.outline;

import adams.flow.transformer.locateobjects.LocatedObject;

import java.util.List;

/**
 * Interface for plotters that allow limiting the objects to be plotted.
 *
 * @author fracpete (fracpete at waikato dot ac dot nz)
 */
public interface SubsetPlotSupporter
  extends OutlinePlotter {

  /**
   * Sets the object to limit the plotting to.
   *
   * @param obj		the object
   */
  public void setPlotSubset(LocatedObject obj);

  /**
   * Sets the objects to limit the plotting to.
   *
   * @param objs	the objects
   */
  public void setPlotSubset(LocatedObject[] objs);

  /**
   * Sets the objects to limit the plotting to.
   *
   * @param objs	the objects
   */
  public void setPlotSubset(List<LocatedObject> objs);

  /**
   * Returns the current subset to plot.
   *
   * @return		the objects
   */
  public LocatedObject[] getPlotSubset();

  /**
   * Clears the objects to limit plotting to.
   */
  public void clearPlotSubset();
}

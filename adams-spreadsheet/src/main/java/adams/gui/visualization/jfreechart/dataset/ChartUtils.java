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
 * DiagonalUtils.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.jfreechart.dataset;

import adams.gui.visualization.core.ColorProvider;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesDataset;
import org.jfree.data.xy.DefaultXYDataset;

import java.awt.Color;
import java.awt.Shape;

/**
 * Helper methods for charts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChartUtils {

  public static final String KEY_DIAGONAL = "$$$Diagonal$$$";

  /**
   * Adds a diagonal to the dataset.
   *
   * @param dataset	the dataset to add the diagonal to
   * @param min		the minimum Y
   * @param max		the maximum Y
   */
  public static void addDiagonal(DefaultXYDataset dataset, double min, double max) {
    dataset.addSeries(KEY_DIAGONAL, new double[][]{new double[]{min, max}, new double[]{min, max}});
  }

  /**
   * Returns the index of the series represnting a diagonal.
   *
   * @param dataset	the dataset
   * @return		the 0-based index, -1 if none present
   */
  public static int getDiagonalIndex(SeriesDataset dataset) {
    int		i;

    for (i = 0; i < dataset.getSeriesCount(); i++) {
      if (dataset.getSeriesKey(i).toString().equals(KEY_DIAGONAL))
	return i;
    }

    return -1;
  }

  /**
   * Checks the dataset whether a diagonal series is present.
   *
   * @param dataset	the dataset to check
   * @return		true if present
   */
  public static boolean hasDiagonal(SeriesDataset dataset) {
    return (getDiagonalIndex(dataset) != -1);
  }

  /**
   * Checks whether the specified series represents a diagonal.
   *
   * @param dataset	the dataset to check
   * @param index	the index of the series to check
   * @return		true if diagonal
   */
  public static boolean isDiagonal(SeriesDataset dataset, int index) {
    return dataset.getSeriesKey(index).toString().equals(KEY_DIAGONAL);
  }

  /**
   * Sets the shape for all series except the diagonal.
   *
   * @param plot	the plot to update
   * @param shape	the shape to set, ignored if null
   */
  public static void applyShape(XYPlot plot, Shape shape) {
    int		i;

    if (shape == null)
      return;

    for (i = 0; i < plot.getSeriesCount(); i++) {
      if (!isDiagonal(plot.getDataset(), i))
	plot.getRenderer().setSeriesShape(i, shape);
    }
  }

  /**
   * Applies the color to the series.
   *
   * @param plot	the plot to update
   * @param plotColor	the plot color to use if just one series (or series with diagonal), ignored if null
   * @param diagonalColor 	the color for the diagonal (if present)
   * @param colorProvider	the color provider to get the colors from (is not reset before calling "next()")
   */
  public static void applyColor(XYPlot plot, Color plotColor, Color diagonalColor, ColorProvider colorProvider) {
    int			i;
    int			diagonal;
    boolean		usePlotColor;

    diagonal     = getDiagonalIndex(plot.getDataset());
    usePlotColor = (plotColor != null) && (plot.getSeriesCount() == 1) || ((plot.getSeriesCount() == 2) && (diagonal != -1));
    for (i = 0; i < plot.getSeriesCount(); i++) {
      if (diagonal == i)
	plot.getRenderer().setSeriesPaint(i, diagonalColor);
      else if (usePlotColor)
	plot.getRenderer().setSeriesPaint(i, plotColor);
      else
	plot.getRenderer().setSeriesPaint(i, colorProvider.next());
    }
  }
}

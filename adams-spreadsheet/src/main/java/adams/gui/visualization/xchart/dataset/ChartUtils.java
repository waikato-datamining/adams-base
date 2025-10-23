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
 * ChartUtils.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package adams.gui.visualization.xchart.dataset;

import adams.gui.visualization.xchart.marker.AbstractMarkerGenerator;
import gnu.trove.list.TDoubleList;
import gnu.trove.list.array.TDoubleArrayList;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.internal.series.Series;
import org.knowm.xchart.style.markers.Marker;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods for charts.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class ChartUtils {

  public static final String KEY_DIAGONAL = "Diagonal ";

  /**
   * Adds a diagonal to the dataset in the first position.
   *
   * @param dataset	the dataset to add the diagonal to
   * @param min		the minimum Y
   * @param max		the maximum Y
   */
  public static void addDiagonal(Datasets<XYDataset> dataset, double min, double max) {
    int		num;
    double	inc;
    TDoubleList	values;
    int		i;

    num    = 1000;
    inc    = (max - min) / (num - 1);
    values = new TDoubleArrayList();
    for (i = 0; i < num - 1; i++)
      values.add(min + i*inc);
    values.add(max);
    dataset.add(0, new XYDataset(KEY_DIAGONAL, values.toArray(), values.toArray()));
  }

  /**
   * Checks the chart whether a diagonal series is present.
   *
   * @param chart	the chart to check
   * @return		true if present
   */
  public static boolean hasDiagonal(Chart chart) {
    return chart.getSeriesMap().containsKey(KEY_DIAGONAL);
  }

  /**
   * Checks the datasets whether a diagonal series is present.
   *
   * @param datasets	the datasets to check
   * @return		true if present
   */
  public static boolean hasDiagonal(Datasets<? extends Dataset> datasets) {
    boolean	result;

    result = false;
    for (Dataset dataset: datasets) {
      result = isDiagonal(dataset);
      if (result)
	break;
    }

    return result;
  }

  /**
   * Checks the dataset whether it is the one for the diagonal.
   *
   * @param dataset	the dataset to check
   * @return		true if present
   */
  public static boolean isDiagonal(Dataset dataset) {
    return dataset.getName().equals(KEY_DIAGONAL);
  }

  /**
   * Checks the series whether it is the one for the diagonal.
   *
   * @param series	the series to check
   * @return		true if present
   */
  public static boolean isDiagonal(Series series) {
    return series.getName().equals(KEY_DIAGONAL);
  }

  /**
   * Sets the markers.
   *
   * @param chart	the chart to update
   * @param generator	the marker generator
   */
  public static void setMarkers(Chart chart, AbstractMarkerGenerator generator) {
    List<Marker>	markers;
    Series		series;

    // default?
    if (generator.generate() == null)
      return;

    markers = new ArrayList<>();
    for (Object key: chart.getSeriesMap().keySet()) {
      series = (Series) chart.getSeriesMap().get(key);
      if (isDiagonal(series))
	markers.add(null);
      else
	markers.add(generator.generate());
    }
    chart.getStyler().setSeriesMarkers(markers.toArray(new Marker[0]));
  }
}

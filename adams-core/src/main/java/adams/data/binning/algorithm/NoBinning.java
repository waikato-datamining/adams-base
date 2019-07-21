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
 * NoBinning.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.core.base.BaseInterval;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.binning.operation.Statistics;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs no real binning, just places all items in one bin.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class NoBinning
  extends AbstractBinningAlgorithm {

  private static final long serialVersionUID = -1486327441961729111L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs no real binning, just places all items in one bin.";
  }

  /**
   * Performs the actual bin generation on the provided objects.
   *
   * @param objects	the objects to bin
   * @return		the generated bins
   * @throws IllegalStateException	if binning fails
   */
  @Override
  protected <T> List<Bin<T>> doGenerateBins(List<Binnable<T>> objects) {
    List<Bin<T>>		result;
    Struct2<Double,Double> 	minMax;
    double			min;
    double			max;

    result  = new ArrayList<>();
    minMax  = Statistics.minMax(objects);
    min     = minMax.value1;
    max     = minMax.value2;
    result.add(new Bin<>(0, min, max, new BaseInterval(min, true, max, true)));

    return result;
  }
}

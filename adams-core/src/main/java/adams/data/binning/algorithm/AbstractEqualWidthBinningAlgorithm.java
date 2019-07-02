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
 * AbstractEqualWidthBinningAlgorithm.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.core.base.BaseInterval;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.ArrayList;
import java.util.List;

/**
 * Ancestor for algorithms that use bins with the same width.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractEqualWidthBinningAlgorithm<T>
  extends AbstractBinningAlgorithm<T> {

  private static final long serialVersionUID = 6165394321339796917L;

  /**
   * Generates equal-width bins. Determines the min/max from the objects.
   *
   * @param numBins	the number of equal-width bins to generate
   * @return		the bins
   */
  protected List<Bin<T>> doGenerateBins(List<Binnable<T>> objects, int numBins) {
    return doGenerateBins(getMinMax(objects), numBins);
  }

  /**
   * Generates equal-width bins.
   *
   * @param minMax	the minimum/maximum to use
   * @param numBins	the number of equal-width bins to generate
   * @return		the bins
   */
  protected List<Bin<T>> doGenerateBins(Struct2<Double,Double> minMax, int numBins) {
    return doGenerateBins(minMax.value1, minMax.value2, numBins);
  }

  /**
   * Generates equal-width bins.
   *
   * @param min		the minimum to use
   * @param max		the maximum to use
   * @param numBins	the number of equal-width bins to generate
   * @return		the bins
   */
  protected List<Bin<T>> doGenerateBins(double min, double max, int numBins) {
    List<Bin<T>>	result;
    double		binWidth;
    double[]		binStart;
    Bin<T>		bin;
    int			i;

    binWidth = (max - min) / numBins;
    if (isLoggingEnabled()) {
      getLogger().info("min=" + min + ", max=" + max);
      getLogger().info("# bins=" + numBins + ", width=" + binWidth);
    }

    // calculate bin starts
    binStart = new double[numBins + 1];
    for (i = 0; i < numBins; i++)
      binStart[i] = min + i*binWidth;
    binStart[binStart.length - 1] = max;

    // create bins
    result = new ArrayList<>();
    for (i = 0; i < numBins; i++) {
      bin = new Bin<>(
        i,
	binStart[i],
	binStart[i + 1],
	new BaseInterval(
              binStart[i], true,
              binStart[i + 1], (i == numBins - 1),
              m_NumDecimals));
      result.add(bin);
    }

    return result;
  }

  /**
   * Generates bins with the specified width. Determines the min/max from the objects.
   *
   * @param binWidth	the bin width
   * @return		the bins
   */
  protected List<Bin<T>> doGenerateBins(List<Binnable<T>> objects, double binWidth) {
    return doGenerateBins(getMinMax(objects), binWidth);
  }

  /**
   * Generates bins with the specified width.
   *
   * @param minMax	the minimum/maximum to use
   * @param binWidth	the bin width
   * @return		the bins
   */
  protected List<Bin<T>> doGenerateBins(Struct2<Double,Double> minMax, double binWidth) {
    return doGenerateBins(minMax.value1, minMax.value2, binWidth);
  }

  /**
   * Generates bins with the specified width.
   *
   * @param min		the minimum to use
   * @param max		the maximum to use
   * @param binWidth	the bin width
   * @return		the bins
   */
  protected List<Bin<T>> doGenerateBins(double min, double max, double binWidth) {
    List<Bin<T>>	result;
    int 		numBins;
    double[]		binStart;
    Bin<T>		bin;
    int			i;

    numBins = (int) Math.ceil((max - min) / binWidth);
    if (isLoggingEnabled()) {
      getLogger().info("min=" + min + ", max=" + max);
      getLogger().info("# bins=" + numBins + ", width=" + binWidth);
    }

    // calculate bin starts
    binStart = new double[numBins + 1];
    for (i = 0; i < numBins; i++)
      binStart[i] = min + i*binWidth;
    binStart[binStart.length - 1] = max;

    // create bins
    result = new ArrayList<>();
    for (i = 0; i < numBins; i++) {
      bin = new Bin<T>(
        i,
	binStart[i],
	binStart[i + 1],
	new BaseInterval(
              binStart[i], true,
              binStart[i + 1], (i == numBins - 1),
              m_NumDecimals));
      result.add(bin);
    }

    return result;
  }
}

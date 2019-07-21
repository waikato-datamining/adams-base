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
 * AbstractBinningAlgorithm.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.binning.operation.Statistics;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.List;

/**
 * Ancestor for binning algorithms.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBinningAlgorithm
  extends AbstractOptionHandler
  implements BinningAlgorithm, QuickInfoSupporter {

  private static final long serialVersionUID = -7234721005632544790L;

  /** the number of decimals to show. */
  protected int m_NumDecimals;

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-decimals", "numDecimals",
      3, 0, null);
  }

  /**
   * Sets the number of decimals to show in intervals.
   *
   * @param value 	the number of decimals
   */
  public void setNumDecimals(int value) {
    m_NumDecimals = value;
    reset();
  }

  /**
   * Returns the number of decimals to show in the intervals.
   *
   * @return 		the number of decimals
   */
  public int getNumDecimals() {
    return m_NumDecimals;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numDecimalsTipText() {
    return "The number of decimals to show in the intervals.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   * <br>
   * Default implementation returns null.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return null;
  }

  /**
   * Hook method for performing checks before binning.
   *
   * @param objects	the objects to bin
   * @return		null if successful, otherwise error message
   */
  protected <T> String check(List<Binnable<T>> objects) {
    if ((objects == null) || (objects.size() == 0))
      return "No objects provided!";
    return null;
  }

  /**
   * Determines the min/max from the binnable objects.
   *
   * @param objects 	the objects to determine the min/max from (using their associated value)
   * @return		the min/max
   */
  protected <T> Struct2<Double,Double> getMinMax(List<Binnable<T>> objects) {
    return Statistics.minMax(objects);
  }

  /**
   * Performs the actual bin generation on the provided objects.
   *
   * @param objects	the objects to bin
   * @return		the generated bins
   * @throws IllegalStateException	if binning fails
   */
  protected abstract <T> List<Bin<T>> doGenerateBins(List<Binnable<T>> objects);

  /**
   * Places the binnable objects in the respective bins.
   *
   * @param bins	the bins to fill
   * @param objects	the objects to distribute
   */
  protected <T> void fillBins(List<Bin<T>> bins, List<Binnable<T>> objects) {
    for (Binnable<T> object: objects) {
      for (Bin<T> bin: bins) {
        if (bin.fits(object)) {
          bin.add(object);
          break;
	}
      }
    }
  }

  /**
   * Performs the bin generation on the provided objects.
   *
   * @param objects	the objects to bin
   * @return		the generated bins
   * @throws IllegalStateException	if check or binning fails
   */
  @Override
  public <T> List<Bin<T>> generateBins(List<Binnable<T>> objects) {
    List<Bin<T>>	result;
    String		msg;

    msg = check(objects);
    if (msg != null)
      throw new IllegalStateException(msg);
    result = doGenerateBins(objects);
    fillBins(result, objects);

    if (isLoggingEnabled()) {
      getLogger().info("Data: " + objects);
      getLogger().info("Bins: " + result);
    }

    return result;
  }
}

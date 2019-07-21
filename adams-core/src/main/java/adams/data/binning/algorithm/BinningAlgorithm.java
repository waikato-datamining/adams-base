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
 * BinningAlgorithm.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.core.QuickInfoSupporter;
import adams.core.option.OptionHandler;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;

import java.util.List;

/**
 * Interface for binning algorithms.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of payload
 */
public interface BinningAlgorithm
  extends OptionHandler, QuickInfoSupporter {

  /**
   * Performs the bin generation on the provided objects.
   *
   * @param objects	the objects to bin
   * @return		the generated bins
   * @throws IllegalStateException	if check or binning fails
   */
  public <T> List<Bin<T>> generateBins(List<Binnable<T>> objects);
}

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
 * PassThrough.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.postprocessing;

import adams.data.binning.Bin;

import java.util.List;

/**
 * Performs no post-processing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of payload
 */
public class PassThrough<T>
  extends AbstractBinPostProcessing<T> {

  private static final long serialVersionUID = 5892185341343555075L;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs no post-processing.";
  }

  /**
   * Post-processes the bins.
   *
   * @param bins	the bins to post-process
   * @return		the updated bins
   * @throws IllegalStateException	if post-processing failed
   */
  @Override
  protected List<Bin<T>> doPostProcessBins(List<Bin<T>> bins) {
    return bins;
  }
}

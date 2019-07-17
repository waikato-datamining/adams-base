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
 * AbstractBinPostProcessing.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.postprocessing;

import adams.core.QuickInfoSupporter;
import adams.core.option.AbstractOptionHandler;
import adams.data.binning.Bin;

import java.util.List;

/**
 * Ancestor for schemes for post-processing bins.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public abstract class AbstractBinPostProcessing
  extends AbstractOptionHandler
  implements QuickInfoSupporter {

  private static final long serialVersionUID = 4458109523375257552L;

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
   * Hook method for performing checks before post-processing.
   *
   * @param bins	the bins to check
   * @return		null if successful, otherwise error message
   */
  protected <T> String check(List<Bin<T>> bins) {
    if ((bins == null) || (bins.size() == 0))
      return "No bins provided!";
    return null;
  }

  /**
   * Post-processes the bins.
   *
   * @param bins	the bins to post-process
   * @return		the updated bins
   * @throws IllegalStateException	if post-processing failed
   */
  protected abstract <T> List<Bin<T>> doPostProcessBins(List<Bin<T>> bins);

  /**
   * Post-processes the bins.
   *
   * @param bins	the bins to post-process
   * @return		the updated bins
   * @throws IllegalStateException	if check or post-processing failed
   */
  public <T> List<Bin<T>> postProcessBins(List<Bin<T>> bins) {
    String	msg;

    msg = check(bins);
    if (msg != null)
      throw new IllegalStateException(msg);
    return doPostProcessBins(bins);
  }
}

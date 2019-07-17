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
 * MinBinSize.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.postprocessing;

import adams.core.QuickInfoHelper;
import adams.data.binning.Bin;

import java.util.ArrayList;
import java.util.List;

/**
 * Ensures that bins have the specified minimum number of objects stored.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MinBinSize
  extends AbstractBinPostProcessing {

  private static final long serialVersionUID = 5892185341343555075L;

  /** the minimum size. */
  protected int m_MinSize;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Ensures that bins have the specified minimum number of objects stored.\n"
      + "If a bin has too few items, it gets merged with the right one.\n"
      + "If the last bin has too few items, it gets merged with the left bin.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "min-size", "minSize",
      1, 0, null);
  }

  /**
   * Sets the minimum number of objects in a bin.
   *
   * @param value 	the minimum
   */
  public void setMinSize(int value) {
    if (getOptionManager().isValid("minSize", value)) {
      m_MinSize = value;
      reset();
    }
  }

  /**
   * Returns the minimum number of objects in a bin.
   *
   * @return 		the minimum
   */
  public int getMinSize() {
    return m_MinSize;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String minSizeTipText() {
    return "The minimum number of objects in a bin.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "minSize", m_MinSize, "min size: ");
  }

  /**
   * Post-processes the bins.
   *
   * @param bins	the bins to post-process
   * @return		the updated bins
   * @throws IllegalStateException	if post-processing failed
   */
  @Override
  protected <T> List<Bin<T>> doPostProcessBins(List<Bin<T>> bins) {
    List<Bin<T>>	result;
    int			i;
    boolean		required;

    // check
    required = false;
    for (Bin<T> bin: bins) {
      if (bin.size() < m_MinSize) {
        required = true;
        break;
      }
    }
    if (!required)
      return bins;

    // clone bins
    result = new ArrayList<>();
    for (Bin<T> bin: bins)
      result.add(bin.getClone());

    i = 0;
    while (i < result.size()) {
      if (result.get(i).size() >= m_MinSize) {
        i++;
        continue;
      }

      if (i == result.size() - 1) {
        if (result.size() > 1) {
          result.get(i - 1).mergeWith(result.get(i));
          result.remove(i);
          i++;
        }
        else {
          throw new IllegalStateException("Only single bin with too few objects, cannot merge!");
        }
      }
      else {
        result.get(i).mergeWith(result.get(i+1));
        result.remove(i+1);
      }
    }

    return result;
  }
}

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
 * FrequencyBinning.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.core.base.BaseInterval;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.data.statistics.StatUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Performs frequency binning.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @param <T> the type of payload
 */
public class FrequencyBinning<T>
  extends AbstractBinningAlgorithm<T>
  implements TechnicalInformationHandler {

  private static final long serialVersionUID = -3591626302229910556L;

  /** the number of bins. */
  protected int m_NumBins;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs frequency binning.\n\n"
      + "For more information see:\n"
      + getTechnicalInformation();
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return 		the technical information about this class
   */
  public TechnicalInformation getTechnicalInformation() {
    TechnicalInformation 	result;

    result = new TechnicalInformation(Type.MISC);
    result.setValue(Field.YEAR, "2010");
    result.setValue(Field.AUTHOR, "WikiPedia");
    result.setValue(Field.TITLE, "Histogram");
    result.setValue(Field.HTTP, "http://en.wikipedia.org/wiki/Histogram");

    return result;
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "num-bins", "numBins",
      10, 1, null);
  }

  /**
   * Sets the number of bins to use.
   *
   * @param value 	the number of bins
   */
  public void setNumBins(int value) {
    if (getOptionManager().isValid("numBins", value)) {
      m_NumBins = value;
      reset();
    }
  }

  /**
   * Returns the number of bins to use.
   *
   * @return 		the number of bins
   */
  public int getNumBins() {
    return m_NumBins;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String numBinsTipText() {
    return "The number of bins to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "numBins", m_NumBins, "#bins: ");
  }

  /**
   * Performs the actual bin generation on the provided objects.
   *
   * @param objects	the objects to bin
   * @return		the generated bins
   * @throws IllegalStateException	if binning fails
   */
  @Override
  protected List<Bin<T>> doGenerateBins(List<Binnable<T>> objects) {
    List<Bin<T>>	result;
    Number[]		array;
    Number[]		sorted;
    double[]		binStart;
    int			i;
    Bin			bin;

    array = Binnable.valuesToNumberArray(objects);
    // calculate bin starts
    binStart = new double[m_NumBins + 1];
    sorted = StatUtils.sort(array, true);
    for (i = 0; i < m_NumBins; i++)
      binStart[i] = sorted[i * (int)((double) sorted.length) / m_NumBins].doubleValue();
    binStart[binStart.length - 1] = StatUtils.max(array).doubleValue();

    // create bins
    result = new ArrayList<>();
    for (i = 0; i < m_NumBins; i++) {
      bin = new Bin<T>(
        i,
	binStart[i],
	new BaseInterval(
              binStart[i], true,
              binStart[i + 1], (i == m_NumBins - 1),
              m_NumDecimals));
      result.add(bin);
    }

    return result;
  }
}

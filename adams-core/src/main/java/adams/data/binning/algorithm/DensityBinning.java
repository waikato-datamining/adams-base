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
 * DensityBinning.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.algorithm;

import adams.core.QuickInfoHelper;
import adams.core.TechnicalInformation;
import adams.core.TechnicalInformation.Field;
import adams.core.TechnicalInformation.Type;
import adams.core.TechnicalInformationHandler;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;

import java.util.List;

/**
 * Performs density-based binning.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DensityBinning
  extends AbstractEqualWidthBinningAlgorithm
  implements TechnicalInformationHandler, FixedBinWidthBinningAlgorithm {

  private static final long serialVersionUID = -3591626302229910556L;

  /** the bin width. */
  protected double m_BinWidth;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Performs density-based binning.\n\n"
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
      "bin-width", "binWidth",
      1.0, 0.00001, null);
  }

  /**
   * Sets the bin width to use.
   *
   * @param value 	the bin width
   */
  @Override
  public void setBinWidth(double value) {
    if (getOptionManager().isValid("binWidth", value)) {
      m_BinWidth = value;
      reset();
    }
  }

  /**
   * Returns the bin width in use.
   *
   * @return 		the bin width
   */
  @Override
  public double getBinWidth() {
    return m_BinWidth;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  @Override
  public String binWidthTipText() {
    return "The bin width to use.";
  }

  /**
   * Returns a quick info about the object, which can be displayed in the GUI.
   *
   * @return		null if no info available, otherwise short string
   */
  public String getQuickInfo() {
    return QuickInfoHelper.toString(this, "binWidth", m_BinWidth, "bin width: ");
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
    return doGenerateBins(objects, m_BinWidth);
  }
}

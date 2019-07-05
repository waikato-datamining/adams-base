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
 * DefaultSplitter.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.ml.splitgenerator.generic.splitter;

import adams.data.binning.Binnable;
import adams.data.binning.operation.Split;
import com.github.fracpete.javautils.struct.Struct2;

import java.util.List;

/**
 * Splits the data using the specified percentage for training and rest for testing.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class DefaultSplitter
  extends AbstractSplitter {

  private static final long serialVersionUID = 3058580709807088718L;

  /** the percentage (0-1). */
  protected double m_Percentage;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return "Splits the data using the specified percentage for training and rest for testing.";
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add(
      "percentage", "percentage",
      0.66, 0.0, 1.0);
  }

  /**
   * Sets the split percentage.
   *
   * @param value	the percentage (0-1)
   */
  public void setPercentage(double value) {
    if (getOptionManager().isValid("percentage", value)) {
      m_Percentage = value;
      reset();
    }
  }

  /**
   * Returns the split percentage.
   *
   * @return		the percentage (0-1)
   */
  public double getPercentage() {
    return m_Percentage;
  }

  /**
   * Returns the tip text for this property.
   *
   * @return 		tip text for this property suitable for
   * 			displaying in the GUI or for listing the options.
   */
  public String percentageTipText() {
    return "The percentage (0-1) to use for training.";
  }

  /**
   * Splits the data into two.
   *
   * @param data	the data to split
   * @param <T>		the payload type
   * @return		the split data
   */
  @Override
  protected <T> Struct2<List<Binnable<T>>, List<Binnable<T>>> doSplit(List<Binnable<T>> data) {
    return Split.split(data, m_Percentage);
  }
}

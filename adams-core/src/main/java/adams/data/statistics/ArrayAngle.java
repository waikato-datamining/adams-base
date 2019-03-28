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
 * ArrayAngle.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.statistics;

import adams.core.TechnicalInformation;
import adams.core.TechnicalInformationHandler;

/**
 <!-- globalinfo-start -->
 * Calculates the angle between the first array and the remaining arrays. The arrays must be numeric.<br>
 * <br>
 * For more information see:<br>
 * <br>
 * Kruse, Fred A, Lefkoff, AB, Boardman, JW, Heidebrecht, KB, Shapiro, AT, Barloon, PJ, Goetz, AFH (1993). The spectral image processing system (SIPS)—interactive visualization and analysis of imaging spectrometer data. Remote sensing of environment. 44(2-3):145--163.<br>
 * <br>
 * Oshigami, Shoko, Yamaguchi, Yasushi, Uezato, Tatsumi, Momose, Atsushi, Arvelyna, Yessy, Kawakami, Yuu, Yajima, Taro, Miyatake, Shuichi, Nguno, Anna (2013). Mineralogical mapping of southern Namibia by application of continuum-removal MSAM method to the HyMap data. International journal of remote sensing. 34(15):5282--5295.
 * <br><br>
 <!-- globalinfo-end -->
 *
 <!-- technical-bibtex-start -->
 * <pre>
 * &#64;article{Kruse1993,
 *    author = {Kruse, Fred A and Lefkoff, AB and Boardman, JW and Heidebrecht, KB and Shapiro, AT and Barloon, PJ and Goetz, AFH},
 *    journal = {Remote sensing of environment},
 *    number = {2-3},
 *    pages = {145--163},
 *    publisher = {Elsevier},
 *    title = {The spectral image processing system (SIPS)—interactive visualization and analysis of imaging spectrometer data},
 *    volume = {44},
 *    year = {1993}
 * }
 *
 * &#64;article{Oshigami2013,
 *    author = {Oshigami, Shoko and Yamaguchi, Yasushi and Uezato, Tatsumi and Momose, Atsushi and Arvelyna, Yessy and Kawakami, Yuu and Yajima, Taro and Miyatake, Shuichi and Nguno, Anna},
 *    journal = {International journal of remote sensing},
 *    number = {15},
 *    pages = {5282--5295},
 *    publisher = {Taylor &amp; Francis},
 *    title = {Mineralogical mapping of southern Namibia by application of continuum-removal MSAM method to the HyMap data},
 *    volume = {34},
 *    year = {2013}
 * }
 * </pre>
 * <br><br>
 <!-- technical-bibtex-end -->
 *
 <!-- options-start -->
 * <pre>-logging-level &lt;OFF|SEVERE|WARNING|INFO|CONFIG|FINE|FINER|FINEST&gt; (property: loggingLevel)
 * &nbsp;&nbsp;&nbsp;The logging level for outputting errors and debugging output.
 * &nbsp;&nbsp;&nbsp;default: WARNING
 * </pre>
 *
 * <pre>-modified &lt;boolean&gt; (property: modified)
 * &nbsp;&nbsp;&nbsp;Whether to use the modified algorithm described in Oshigami et al.
 * &nbsp;&nbsp;&nbsp;default: false
 * </pre>
 *
 <!-- options-end -->
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class ArrayAngle<T extends Number>
  extends AbstractArrayStatistic<T>
  implements EqualLengthArrayStatistic, TechnicalInformationHandler {

  /** Auto-generated serialisation UID#. */
  private static final long serialVersionUID = 5466289966807424233L;

  /** Whether to use the modified algorithm described in Oshigami et al. */
  protected boolean m_UseModifiedAlgorithm;

  /**
   * Returns a string describing the object.
   *
   * @return 			a description suitable for displaying in the gui
   */
  @Override
  public String globalInfo() {
    return
      "Calculates the angle between the first array and "
        + "the remaining arrays. The arrays must be numeric.\n\n"
        + "For more information see:\n\n"
        + getTechnicalInformation().toString();
  }

  /**
   * Adds options to the internal list of options.
   */
  @Override
  public void defineOptions() {
    super.defineOptions();

    m_OptionManager.add("modified", "modified", false);
  }

  /**
   * Gets whether to use the modified algorithm.
   *
   * @return Whether to use the modified algorithm.
   */
  public boolean getModified() {
    return m_UseModifiedAlgorithm;
  }

  /**
   * Sets whether to use the modified algorithm.
   *
   * @param value Whether to use the modified algorithm.
   */
  public void setModified(boolean value) {
    m_UseModifiedAlgorithm = value;
    reset();
  }

  /**
   * Gets the tip-text for the modified option.
   *
   * @return  The tip-text as a string.
   */
  public String modifiedTipText() {
    return "Whether to use the modified algorithm described in Oshigami et al.";
  }

  /**
   * Returns an instance of a TechnicalInformation object, containing
   * detailed information about the technical background of this class,
   * e.g., paper reference or book this class is based on.
   *
   * @return the technical information about this class
   */
  @Override
  public TechnicalInformation getTechnicalInformation() {
    return SpectralAngleMapperUtils.getTechnicalInformation();
  }

  /**
   * Returns the minimum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the minimum number, -1 for unbounded
   */
  @Override
  public int getMin() {
    return 2;
  }

  /**
   * Returns the maximum number of arrays that need to be present.
   * -1 for unbounded.
   *
   * @return		the maximum number, -1 for unbounded
   */
  @Override
  public int getMax() {
    return -1;
  }

  /**
   * Returns the length of the stored arrays.
   *
   * @return		the length of the arrays, -1 if none stored
   */
  @Override
  public int getLength() {
    if (size() > 0)
      return get(0).length;
    else
      return -1;
  }

  /**
   * Generates the actual result.
   *
   * @return		the generated result
   */
  @Override
  protected StatisticContainer doCalculate() {
    // Calculate the number of reference arrays we have
    int numReferences = size() - 1;

    // Create the empty result container
    StatisticContainer<Double> result = new StatisticContainer<>(1, numReferences);

    // Create a double array for each reference
    double[][] references = new double[numReferences][];
    for (int i = 0; i < numReferences; i++) {
      references[i] = toDoubleArray(get(i + 1));
    }

    // Create a double array for the test array
    double[] test = toDoubleArray(get(0));

    // Perform angle mapping
    double[] angles = SpectralAngleMapperUtils.sam(test, references, m_UseModifiedAlgorithm);

    // Format the angles into the result container
    for (int i = 0; i < numReferences; i++) {
      result.setHeader(i, "angle 1-" + (i+2));
      result.setCell(0, i, angles[i]);
    }

    // Return the result container
    return result;
  }

  /**
   * Converts an array of Numbers into an array of doubles.
   *
   * @param input	The Number array.
   * @return	The double array.
   */
  protected double[] toDoubleArray(T[] input) {
    // Create the double array
    double[] result = new double[input.length];

    // Copy each value from the input
    for (int i = 0; i < input.length; i++) {
      result[i] = input[i].doubleValue();
    }

    // Return the double array
    return result;
  }
}
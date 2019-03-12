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
 * CheckAdamsClassifier.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import weka.core.Instances;
import weka.core.Option;
import weka.core.Utils;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

/**
 * <!-- options-start -->
 * <!-- options-end -->
 * <p>
 * Extension of weka.classifiers.CheckClassifier that allows
 * the option of skipping the comparison with the ZeroR classifier.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class CheckAdamsClassifier extends CheckClassifier {

  /** Whether the classifier should be compared to ZeroR. */
  protected boolean m_IgnoreTestWRTZeroR;

  /**
   * Sets whether the comparison between the classifier and
   * ZeroR should be skipped.
   *
   * @param value True to skip the comparison, false to not.
   */
  public void setIgnoreTestWRTZeroR(boolean value) {
    m_IgnoreTestWRTZeroR = value;
  }

  /**
   * Determine whether the scheme performs worse than ZeroR during testing
   *
   * @param classifier the pre-trained classifier
   * @param evaluation the classifier evaluation object
   * @param train      the training data
   * @param test       the test data
   * @return index 0 is true if the scheme performs better than ZeroR
   * @throws Exception if there was a problem during the scheme's testing
   */
  @Override
  protected boolean[] testWRTZeroR(Classifier classifier,
				   Evaluation evaluation, Instances train, Instances test) throws Exception {
    // Return a pass for the ZeroR-comparison if we are skipping it
    if (m_IgnoreTestWRTZeroR) {
      boolean[] result = new boolean[2];
      result[0] = true;
      return result;
    }

    // Otherwise perform the test as normal
    return super.testWRTZeroR(classifier, evaluation, train, test);
  }

  /**
   * Returns an enumeration describing the available options.
   *
   * @return an enumeration of all the available options.
   */
  @Override
  public Enumeration<Option> listOptions() {
    Vector<Option> result = new Vector<>(Collections.list(super.listOptions()));

    result.add(new Option("\tWhether to ignore the comparison to ZeroR.\n",
      "ignore-zeror", 0, "-ignore-zeror"));

    return result.elements();
  }

  /**
   * Parses a given list of options.
   * <p>
   * <!-- options-start -->
   * <!-- options-end -->
   *
   * @param options the list of options as an array of strings
   * @throws Exception if an option is not supported
   */
  @Override
  public void setOptions(String[] options) throws Exception {
    super.setOptions(options);

    m_IgnoreTestWRTZeroR = (Utils.getOptionPos("ignore-zeror", options) > -1);
  }

  /**
   * Gets the current settings of the CheckClassifier.
   *
   * @return an array of strings suitable for passing to setOptions
   */
  @Override
  public String[] getOptions() {
    Vector<String> result;
    String[] options;

    result = new Vector<>();

    Collections.addAll(result, super.getOptions());

    if (m_IgnoreTestWRTZeroR) result.add("-ignore-zeror");

    return result.toArray(new String[0]);
  }
}

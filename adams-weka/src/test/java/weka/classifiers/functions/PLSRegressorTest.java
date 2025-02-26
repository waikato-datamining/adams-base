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
 * Copyright (C) 2025 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.functions;

import adams.data.instancesanalysis.pls.PreprocessingType;
import adams.data.instancesanalysis.pls.SIMPLS;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.AbstractAdamsClassifierTest;
import weka.classifiers.CheckClassifier;
import weka.classifiers.Classifier;

/**
 * Tests PLSRegressor. Run from the command line with:<br><br>
 * java weka.classifiers.functions.PLSRegressorTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PLSRegressorTest
  extends AbstractAdamsClassifierTest {

  /** the number of PLS components to generate. */
  public final static int NUM_COMPONENTS = 5;

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public PLSRegressorTest(String name) {
    super(name);
  }

  /**
   * configures the CheckClassifier instance used throughout the tests.
   *
   * @return	the fully configured CheckClassifier instance used for testing
   */
  @Override
  protected CheckClassifier getTester() {
    CheckClassifier	result;

    result = super.getTester();
    result.setNumNominal(NUM_COMPONENTS * 2);
    result.setNumNumeric(NUM_COMPONENTS * 2);
    result.setNumString(NUM_COMPONENTS * 2);
    result.setNumDate(NUM_COMPONENTS * 2);
    result.setNumRelational(NUM_COMPONENTS * 2);

    return result;
  }

  /**
   * Creates a default PLSClassifier.
   *
   * @return		the classifier
   */
  @Override
  public Classifier getClassifier() {
    PLSRegressor classifier = new PLSRegressor();
    SIMPLS algorithm = new SIMPLS();
    algorithm.setNumComponents(NUM_COMPONENTS);
    algorithm.setPreprocessingType(PreprocessingType.CENTER);
    algorithm.setReplaceMissing(true);

    try {
      classifier.setAlgorithm(algorithm);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    return classifier;
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(PLSRegressorTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}

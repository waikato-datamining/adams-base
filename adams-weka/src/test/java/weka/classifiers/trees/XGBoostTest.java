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
 * Copyright (C) 2012-2022 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.trees;

import adams.core.Utils;
import adams.core.management.LDD;
import adams.core.management.OS;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.AbstractAdamsClassifierTest;
import weka.classifiers.CheckAdamsClassifier;
import weka.classifiers.CheckClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.trees.XGBoost.Verbosity;

/**
 * Tests the XGBoost wrapper classifier.
 *
 * @author Corey Sterling (csterlin at waikato dot ac dot nz)
 */
public class XGBoostTest
  extends AbstractAdamsClassifierTest {

  /**
   * Initializes the test.
   *
   * @param name the name of the test
   */
  public XGBoostTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method. This implementation creates the
   * default classifier to test and loads a test set of Instances.
   *
   * @throws Exception if an error occurs reading the example instances.
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * configures the CheckClassifier instance used throughout the tests
   *
   * @return the fully configured CheckClassifier instance used for testing
   */
  @Override
  protected CheckClassifier getTester() {
    CheckClassifier result = super.getTester();

    // Disable comparison to ZeroR (not meaningful with
    // generated datasets)
    ((CheckAdamsClassifier) result).setIgnoreTestWRTZeroR(true);

    return result;
  }

  /**
   * Creates an XGBoost classifier for testing.
   *
   * @return the configured classifier
   */
  @Override
  public Classifier getClassifier() {
    XGBoost result;

    try {
      result = new XGBoost();
      result.setVerbosity(Verbosity.SILENT);
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return the suite
   */
  public static Test suite() {
    if (OS.isLinux()) {
      if (LDD.compareTo(XGBoost.MIN_GLIBC_VERSION) < 0) {
        System.err.println("glibc too old (" + Utils.flatten(LDD.version(), ".") + "), minimum required: " + Utils.flatten(XGBoost.MIN_GLIBC_VERSION, "."));
        return new TestSuite();
      }
    }
    return new TestSuite(XGBoostTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args ignored
   */
  public static void main(String[] args) {
    TestRunner.run(suite());
  }
}

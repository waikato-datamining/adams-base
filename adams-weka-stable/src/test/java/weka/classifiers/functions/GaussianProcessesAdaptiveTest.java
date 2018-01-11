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
 * Copyright (C) 2009-2013 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.functions;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.AbstractAdamsClassifierTest;
import weka.classifiers.Classifier;

/**
 * Tests GaussianProcessesAdaptive. Run from the command line with:<br><br>
 * java weka.classifiers.functions.GaussianProcessesAdaptiveTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class GaussianProcessesAdaptiveTest
  extends AbstractAdamsClassifierTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public GaussianProcessesAdaptiveTest(String name) {
    super(name);
  }

  /**
   * Creates a default GaussianProcessesAdaptive.
   *
   * @return		the configured classifier
   */
  @Override
  public Classifier getClassifier() {
    GaussianProcessesAdaptive	result;

    result = new GaussianProcessesAdaptive();
    result.m_checksTurnedOff = false;

    return result;
  }

  /**
   * Returns a test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(GaussianProcessesAdaptiveTest.class);
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

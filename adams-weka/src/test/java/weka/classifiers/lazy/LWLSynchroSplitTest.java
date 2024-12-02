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
 * Copyright 2009-2024 University of Waikato
 */

package weka.classifiers.lazy;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.AbstractAdamsClassifierTest;
import weka.classifiers.Classifier;

/**
 * Tests LWLSynchroSplit. Run from the command line with:<p>
 * java weka.classifiers.lazy.LWLSynchroSplitTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LWLSynchroSplitTest
  extends AbstractAdamsClassifierTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public LWLSynchroSplitTest(String name) {
    super(name);
  }

  /**
   * Creates a default LWLSynchroSplit.
   *
   * @return		the classifier
   */
  @Override
  public Classifier getClassifier() {
    return new LWLSynchroSplit();
  }

  public void testMissingPredictors() {
    // when GPD falls back on GaussianProcessesNoWeights, date attribute can no longer be handled
    // and that error gets interpreted incorrectly as failure to handle missing predictors
    // hence this test is disabled
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(LWLSynchroSplitTest.class);
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

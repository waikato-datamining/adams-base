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
 * Copyright (C) 2015 University of Waikato, Hamilton, New Zealand
 */

package weka.classifiers.meta;

import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import weka.classifiers.AbstractAdamsClassifierTest;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;

/**
 * Tests AbstainingClassifierWrapper. Run from the command line with:<br><br>
 * java weka.classifiers.meta.AbstainingClassifierWrapperTest
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class AbstainingClassifierWrapperTest
  extends AbstractAdamsClassifierTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public AbstainingClassifierWrapperTest(String name) {
    super(name);
  }

  /**
   * Creates a default AbstainingClassifierWrapper.
   *
   * @return		the configured classifier
   */
  @Override
  public Classifier getClassifier() {
    AbstainingClassifierWrapper result;
    AbstainMinimumProbability   min;

    try {
      result = new AbstainingClassifierWrapper();
      min    = new AbstainMinimumProbability();
      min.setClassifier(new J48());
    }
    catch (Exception e) {
      result = null;
    }

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(AbstainingClassifierWrapperTest.class);
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

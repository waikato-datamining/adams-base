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
 * LeaveOneOutByValueGeneratorTest.java
 * Copyright (C) 2025 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.data.weka.WekaAttributeIndex;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;

/**
 * Tests weka.classifiers.LeaveOneOutByValueGenerator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class LeaveOneOutByValueGeneratorTest
  extends AbstractSplitGeneratorTestCase {

  /**
   * Initializes the test.
   *
   * @param name 	the name of the test
   */
  public LeaveOneOutByValueGeneratorTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractSplitGenerator[] getRegressionSetups() {
    LeaveOneOutByValueGenerator[]	result;
    Instances				anneal;
    Instances				bodyfat;

    anneal  = load("anneal_with_group.arff", 50);
    bodyfat = load("bodyfat_with_group.arff", 50);

    result    = new LeaveOneOutByValueGenerator[3];
    result[0] = new LeaveOneOutByValueGenerator(anneal, 10, true, new WekaAttributeIndex("1"));
    result[1] = new LeaveOneOutByValueGenerator(anneal, 10, false, new WekaAttributeIndex("1"));
    result[2] = new LeaveOneOutByValueGenerator(bodyfat, 3, false, new WekaAttributeIndex("1"));

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(LeaveOneOutByValueGeneratorTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    runTest(suite());
  }
}

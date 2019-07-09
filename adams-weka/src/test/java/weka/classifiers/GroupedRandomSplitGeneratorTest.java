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
 * GroupedRandomSplitGeneratorTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package weka.classifiers;

import adams.core.base.BaseRegExp;
import adams.data.weka.WekaAttributeIndex;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.core.Instances;

/**
 * Tests weka.classifiers.GroupedRandomSplitGenerator.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class GroupedRandomSplitGeneratorTest
  extends AbstractSplitGeneratorTestCase {

  /**
   * Initializes the test.
   *
   * @param name 	the name of the test
   */
  public GroupedRandomSplitGeneratorTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractSplitGenerator[] getRegressionSetups() {
    GroupedRandomSplitGenerator[]	result;
    Instances				anneal;
    Instances				bodyfat;

    anneal  = load("anneal_with_group.arff");
    bodyfat = load("bodyfat_with_group.arff");

    result    = new GroupedRandomSplitGenerator[3];
    result[0] = new GroupedRandomSplitGenerator(anneal, 42, 0.66, false, new WekaAttributeIndex("1"), new BaseRegExp("(.*)"), "$1");
    result[1] = new GroupedRandomSplitGenerator(bodyfat, 42, 0.33, false, new WekaAttributeIndex("1"), new BaseRegExp("(.*)"), "$1");
    result[2] = new GroupedRandomSplitGenerator(bodyfat, 42, 0.33, true, new WekaAttributeIndex("1"), new BaseRegExp("(.*)"), "$1");

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(GroupedRandomSplitGeneratorTest.class);
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

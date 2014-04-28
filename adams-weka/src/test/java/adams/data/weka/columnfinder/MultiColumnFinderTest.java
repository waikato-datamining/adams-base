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

/**
 * MultiColumnFinderTest.java
 * Copyright (C) 2012 University of Waikato, Hamilton, New Zealand
 */
package adams.data.weka.columnfinder;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.weka.columnfinder.MultiColumnFinder.Combination;
import adams.env.Environment;

/**
 * Test class for the MultiColumnFinder finder.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class MultiColumnFinderTest
  extends AbstractColumnFinderTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MultiColumnFinderTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"bolts.arff",
	"bolts.arff",
	"bolts.arff"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected ColumnFinder[] getRegressionSetups() {
    MultiColumnFinder[]	result;

    result = new MultiColumnFinder[3];

    result[0] = new MultiColumnFinder();

    result[1] = new MultiColumnFinder();
    result[1].setCombination(Combination.JOIN);
    result[1].setFinders(new ColumnFinder[]{new NullFinder(), new NullFinder()});

    result[2] = new MultiColumnFinder();
    result[2].setCombination(Combination.INTERSECT);
    result[2].setFinders(new ColumnFinder[]{new NullFinder(), new NullFinder()});

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MultiColumnFinderTest.class);
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

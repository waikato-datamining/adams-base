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
 * CauchyTest.java
 * Copyright (C) 2010-2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.random;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;

/**
 * Test class for the Cauchy random number generator filter. Run from the command line with: <p/>
 * java adams.data.random.CauchyTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class CauchyTest
  extends AbstractRandomNumberGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public CauchyTest(String name) {
    super(name);
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected RandomNumberGenerator[] getRegressionSetups() {
    Cauchy[]	result;

    result = new Cauchy[4];

    result[0] = new Cauchy();

    result[1] = new Cauchy();
    result[1].setMean(3.2);

    result[2] = new Cauchy();
    result[2].setStdev(2.1);

    result[3] = new Cauchy();
    result[3].setMean(3.2);
    result[3].setStdev(2.1);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(CauchyTest.class);
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

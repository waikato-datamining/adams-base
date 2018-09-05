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
 * SwapPLSTest.java
 * Copyright (C) 2018 University of Waikato, Hamilton, New Zealand
 */

package adams.data.conversion;

import adams.data.instancesanalysis.pls.PRM;
import adams.env.Environment;
import adams.flow.transformer.WekaFilter;
import junit.framework.Test;
import junit.framework.TestSuite;
import weka.filters.supervised.attribute.PLS;
import weka.filters.supervised.attribute.PLSFilter;
import weka.filters.supervised.attribute.PLSFilterExtended;

/**
 * Tests the SwapPLS conversion.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class SwapPLSTest
  extends AbstractConversionTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public SwapPLSTest(String name) {
    super(name);
  }

  /**
   * Returns the input data to use in the regression test.
   *
   * @return		the objects
   */
  @Override
  protected Object[] getRegressionInput() {
    Object[]  	result;
    WekaFilter 	filter;
    PLSFilter	plsf;
    PLS		pls;

    result = new Object[4];

    filter = new WekaFilter();
    filter.setFilter(new PLSFilter());
    result[0] = filter;

    plsf = new PLSFilter();
    plsf.setNumComponents(5);
    filter = new WekaFilter();
    filter.setFilter(plsf);
    result[1] = filter;

    filter = new WekaFilter();
    filter.setFilter(new PLSFilterExtended());
    result[2] = filter;

    pls = new PLS();
    pls.setAlgorithm(new PRM());
    filter = new WekaFilter();
    filter.setFilter(pls);
    result[3] = filter;

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected Conversion[] getRegressionSetups() {
    SwapPLS[]	result;

    result = new SwapPLS[3];
    result[0] = new SwapPLS();
    result[1] = new SwapPLS();
    result[1].setExactMatch(true);
    result[2] = new SwapPLS();
    result[2].setKeepNumComponents(false);

    return result;
  }

  /**
   * Returns the ignored line indices to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected int[] getRegressionIgnoredLineIndices() {
    return new int[0];
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(SwapPLSTest.class);
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

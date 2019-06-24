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
 * PassThroughTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.postprocessing;

import adams.core.base.BaseInterval;
import adams.data.binning.Bin;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests PassThrough.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class PassThroughTest
  extends AbstractBinPostProcessingTestCase<Integer> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public PassThroughTest(String name) {
    super(name);
  }

  /**
   * Returns the data to use in the regression test.
   *
   * @return		the data
   */
  @Override
  protected List<Bin<Integer>>[] getRegressionInputData() {
    List<Bin<Integer>>[]	result;

    result = new List[1];
    result[0] = new ArrayList<>();
    result[0].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, true)));

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBinPostProcessing<Integer>[] getRegressionSetups() {
    PassThrough<Integer>[]	result;

    result = new PassThrough[1];
    result[0] = new PassThrough<>();

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(PassThroughTest.class);
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

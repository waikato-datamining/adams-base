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
 * MinBinSizeTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.postprocessing;

import adams.core.base.BaseInterval;
import adams.data.binning.Bin;
import adams.data.binning.Binnable;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests MinBinSize.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class MinBinSizeTest
  extends AbstractBinPostProcessingTestCase<Integer> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public MinBinSizeTest(String name) {
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

    result = new List[7];

    result[0] = new ArrayList<>();
    result[0].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, false)));
    result[0].get(0).add(new Binnable<>(1, 0.1));

    result[1] = new ArrayList<>();
    result[1].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, false)));
    result[1].get(0).add(new Binnable<>(1, 0.1));
    result[1].add(new Bin(1, 1.0, 2.0, new BaseInterval(1.0, true, 2.0, false)));
    result[1].get(1).add(new Binnable<>(2, 1.1));

    result[2] = new ArrayList<>();
    result[2].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, false)));
    result[2].get(0).add(new Binnable<>(1, 0.1));
    result[2].get(0).add(new Binnable<>(2, 0.2));
    result[2].add(new Bin(1, 1.0, 2.0, new BaseInterval(1.0, true, 2.0, false)));
    result[2].get(1).add(new Binnable<>(3, 1.1));

    result[3] = new ArrayList<>();
    result[3].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, false)));
    result[3].get(0).add(new Binnable<>(1, 0.1));
    result[3].add(new Bin(1, 1.0, 2.0, new BaseInterval(1.0, true, 2.0, false)));
    result[3].get(1).add(new Binnable<>(2, 1.1));
    result[3].get(1).add(new Binnable<>(3, 1.2));

    result[4] = new ArrayList<>();
    result[4].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, false)));
    result[4].get(0).add(new Binnable<>(1, 0.1));
    result[4].get(0).add(new Binnable<>(2, 0.2));
    result[4].add(new Bin(1, 1.0, 2.0, new BaseInterval(1.0, true, 2.0, false)));
    result[4].get(1).add(new Binnable<>(2, 1.1));
    result[4].get(1).add(new Binnable<>(3, 1.2));
    result[4].add(new Bin(2, 2.0, 3.0, new BaseInterval(2.0, true, 3.0, false)));
    result[4].get(2).add(new Binnable<>(2, 2.1));
    result[4].get(2).add(new Binnable<>(3, 2.2));

    result[5] = new ArrayList<>();
    result[5].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, false)));
    result[5].get(0).add(new Binnable<>(1, 0.1));
    result[5].get(0).add(new Binnable<>(2, 0.1));
    result[5].add(new Bin(1, 1.0, 2.0, new BaseInterval(1.0, true, 2.0, false)));
    result[5].get(1).add(new Binnable<>(3, 1.1));
    result[5].add(new Bin(2, 2.0, 3.0, new BaseInterval(2.0, true, 3.0, false)));
    result[5].get(2).add(new Binnable<>(2, 2.1));
    result[5].get(2).add(new Binnable<>(3, 2.2));

    result[6] = new ArrayList<>();
    result[6].add(new Bin(0, 0.0, 1.0, new BaseInterval(0.0, true, 1.0, false)));
    result[6].get(0).add(new Binnable<>(1, 0.1));
    result[6].get(0).add(new Binnable<>(2, 0.1));
    result[6].add(new Bin(1, 1.0, 2.0, new BaseInterval(1.0, true, 2.0, false)));
    result[6].get(1).add(new Binnable<>(3, 1.1));
    result[6].add(new Bin(2, 2.0, 3.0, new BaseInterval(2.0, true, 3.0, false)));
    result[6].get(2).add(new Binnable<>(2, 2.1));

    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBinPostProcessing[] getRegressionSetups() {
    MinBinSize[]	result;

    result = new MinBinSize[2];
    result[0] = new MinBinSize();
    result[1] = new MinBinSize();
    result[1].setMinSize(2);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MinBinSizeTest.class);
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

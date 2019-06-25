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
 * WrapTest.java
 * Wrapright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tests Wrap.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class WrapTest
  extends AbstractOperationTestCase<Integer> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public WrapTest(String name) {
    super(name);
  }

  /**
   * Generates the output data and returns the filenames.
   *
   * @return		the filenames of the generated output, no path
   */
  @Override
  protected List<String> generateOutput() {
    List<String> 		result;
    String			fname;
    List<Binnable<Integer>> 	data;
    Random			rand;
    int				num;
    double[]			numbers;
    int				i;

    result = new ArrayList<>();

    fname   = createOutputFilename(0);
    num     = 20;
    numbers = new double[num];
    rand    = new Random(1);
    for (i = 0; i < num; i++)
      numbers[i] = rand.nextDouble();
    saveObject(numbers, fname);
    result.add(fname);

    fname = createOutputFilename(1);
    try {
      saveObject(Wrap.wrap(numbers), fname);
    }
    catch (Exception e) {
      fail("Wrapping of numbers failed: " + e.getMessage());
    }
    result.add(fname);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(WrapTest.class);
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

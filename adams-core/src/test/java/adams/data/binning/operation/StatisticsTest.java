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
 * StatisticsTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.data.binning.operation.Copy.CopyType;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests Statistics.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class StatisticsTest
  extends AbstractOperationTestCase<Integer> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public StatisticsTest(String name) {
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
    List<Binnable<Integer>> 	sorted;

    result = new ArrayList<>();

    fname = createOutputFilename(0);
    data = generateDoubles(20);
    saveData(data, fname);
    result.add(fname);

    fname = createOutputFilename(1);
    sorted = Copy.copyData(data, CopyType.LIST);
    Sort.sort(sorted);
    saveData(sorted, fname);
    result.add(fname);

    fname = createOutputFilename(2);
    saveObject(Statistics.minMax(data), fname);
    result.add(fname);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(StatisticsTest.class);
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

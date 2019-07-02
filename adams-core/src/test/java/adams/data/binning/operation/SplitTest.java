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
 * SortTest.java
 * Copyright (C) 2019 University of Waikato, Hamilton, NZ
 */

package adams.data.binning.operation;

import adams.data.binning.Binnable;
import adams.data.binning.BinnableGroup;
import adams.data.binning.operation.Grouping.GroupExtractor;
import adams.env.Environment;
import com.github.fracpete.javautils.struct.Struct2;
import junit.framework.Test;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests Split.
 *
 * @author FracPete (fracpete at waikato dot ac dot nz)
 */
public class SplitTest
  extends AbstractOperationTestCase<Integer> {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name the name of the test
   */
  public SplitTest(String name) {
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
    Struct2<List<Binnable<Integer>>,List<Binnable<Integer>>> splits;
    GroupExtractor<Integer> 	extractor;
    List<BinnableGroup<Integer>> groups;
    Struct2<List<BinnableGroup<Integer>>,List<BinnableGroup<Integer>>> groupSplits;

    result = new ArrayList<>();

    // binnable
    fname = createOutputFilename(0);
    data = generateDoubles(10);
    saveData(data, fname);
    result.add(fname);

    splits = Split.split(data, 0.66);

    fname = createOutputFilename(1);
    saveData(splits.value1, fname);
    result.add(fname);

    fname = createOutputFilename(2);
    saveData(splits.value2, fname);
    result.add(fname);

    // binnable groups
    fname = createOutputFilename(3);
    data    = generateIntegers(20, 3);
    saveData(data, fname);
    result.add(fname);

    fname = createOutputFilename(4);
    extractor = new GroupExtractor<Integer>() {
      @Override
      public String extractGroup(Binnable<Integer> item) {
        return "" + (int) item.getValue();
      }
    };
    try {
      groups = Grouping.groupAsList(data, extractor);
      saveGroups(groups, fname);
    }
    catch (Exception e) {
      groups = null;
      fail("Grouping of numbers failed: " + e.getMessage());
    }
    result.add(fname);

    groupSplits = Split.splitGroups(groups, 0.66);

    fname = createOutputFilename(5);
    saveGroups(groupSplits.value1, fname);
    result.add(fname);

    fname = createOutputFilename(6);
    saveGroups(groupSplits.value2, fname);
    result.add(fname);

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(SplitTest.class);
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

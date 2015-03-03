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
 * IDGeneratorPostProcessorTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.id;

import java.util.Arrays;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.env.Environment;
import adams.test.Platform;

/**
 * Test class for the IDGeneratorPostProcessor filter. Run from the command line with: <p/>
 * java adams.data.filter.IDGeneratorPostProcessorTest
 * <p/>
 * Note: dummy test
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class IDGeneratorPostProcessorTest
  extends AbstractInstanceIDGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public IDGeneratorPostProcessorTest(String name) {
    super(name);
  }

  /**
   * Returns the platform this test class is for.
   * 
   * @return		the platform.
   */
  protected HashSet<Platform> getPlatforms() {
    return new HashSet<Platform>(Arrays.asList(new Platform[]{Platform.LINUX}));
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected String[] getRegressionInputFiles() {
    return new String[] {
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
  protected AbstractIDGenerator[] getRegressionSetups() {
    IDGeneratorPostProcessor[]	result;
    SimpleIDGenerator		simple;

    result = new IDGeneratorPostProcessor[3];

    result[0] = new IDGeneratorPostProcessor();

    simple = new SimpleIDGenerator();
    simple.setFormat("{ID}");
    simple.setFilenameReplaceChar("_");
    simple.setMakeFilename(true);
    result[1] = new IDGeneratorPostProcessor();
    result[1].setGenerator(simple);
    result[1].setFind("\\.arff");
    result[1].setReplace("");

    simple = new SimpleIDGenerator();
    simple.setFilenameReplaceChar("_");
    simple.setMakeFilename(true);
    result[2] = new IDGeneratorPostProcessor();
    result[2].setGenerator(simple);
    result[2].setFind("\\.arff");
    result[2].setReplace("");

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(IDGeneratorPostProcessorTest.class);
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

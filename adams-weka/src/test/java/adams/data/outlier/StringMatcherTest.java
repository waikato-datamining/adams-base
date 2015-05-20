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
 * StringMatcherTest.java
 * Copyright (C) 2010 University of Waikato, Hamilton, New Zealand
 */
package adams.data.outlier;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.core.base.BaseRegExp;
import adams.data.instance.Instance;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.env.Environment;

/**
 * Test class for the StringMatcher outlier detector. Run from the command line with: <br><br>
 * java adams.data.outlier.StringMatcherTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class StringMatcherTest
  extends AbstractInstanceOutlierDetectorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public StringMatcherTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"vote1.arff",
	"vote1.arff",
	"vote1.arff",
	"vote1.arff"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  protected AbstractOutlierDetector[] getRegressionSetups() {
    StringMatcher[]	result;

    result = new StringMatcher[4];

    result[0] = new StringMatcher();
    result[0].setField(new Field(Instance.REPORT_CLASS, DataType.STRING));

    result[1] = new StringMatcher();
    result[1].setField(new Field("blah", DataType.STRING));

    result[2] = new StringMatcher();
    result[2].setField(new Field(Instance.REPORT_CLASS, DataType.STRING));
    result[2].setRegExp(new BaseRegExp("rep.*"));

    result[3] = new StringMatcher();
    result[3].setField(new Field(Instance.REPORT_CLASS, DataType.STRING));
    result[3].setRegExp(new BaseRegExp("demo.*"));

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(StringMatcherTest.class);
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

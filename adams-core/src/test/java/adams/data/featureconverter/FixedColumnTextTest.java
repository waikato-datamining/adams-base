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
 * FixedColumnTextTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.report.DataType;
import adams.env.Environment;

/**
 * Test class for the FixedColumnText feature converter. Run from the command line with: <p/>
 * java adams.data.featureconverter.FixedColumnTextTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedColumnTextTest
  extends AbstractFeatureConverterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public FixedColumnTextTest(String name) {
    super(name);
  }

  /**
   * Returns the header definitions to use in the regression test.
   *
   * @return		the header definitions
   */
  @Override
  protected HeaderDefinition[] getRegressionHeaderDefinitions() {
    HeaderDefinition[]	result;
    
    result    = new HeaderDefinition[1];
    result[0] = new HeaderDefinition();
    result[0].add("string", DataType.STRING);
    result[0].add("boolean", DataType.BOOLEAN);
    result[0].add("number", DataType.NUMERIC);
    
    return result;
  }

  /**
   * Returns the data rows to use in the regression test.
   *
   * @return		the data rows
   */
  @Override
  protected List[][] getRegressionRows() {
    List[][]	result;
    
    result = new List[1][3];
    
    result[0][0] = new ArrayList();
    result[0][0].add("hello world");
    result[0][0].add(true);
    result[0][0].add(3.1415);

    result[0][1] = new ArrayList();
    result[0][1].add("bye bye");
    result[0][1].add(false);
    result[0][1].add(2.7);

    result[0][2] = new ArrayList();
    result[0][2].add(null);
    result[0][2].add(null);
    result[0][2].add(null);
    
    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFeatureConverter[] getRegressionSetups() {
    return new FixedColumnText[]{
	new FixedColumnText()
    };
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
    return new TestSuite(FixedColumnTextTest.class);
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

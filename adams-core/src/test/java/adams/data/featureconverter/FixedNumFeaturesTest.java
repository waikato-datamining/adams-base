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
 * FixedNumFeaturesTest.java
 * Copyright (C) 2014 University of Waikato, Hamilton, New Zealand
 */
package adams.data.featureconverter;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.featureconverter.FixedNumFeatures.FillerType;
import adams.data.report.DataType;
import adams.env.Environment;

/**
 * Test class for the FixedNumFeatures feature converter. Run from the command line with: <br><br>
 * java adams.data.featureconverter.FixedNumFeaturesTest
 * 
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class FixedNumFeaturesTest
  extends AbstractFeatureConverterTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public FixedNumFeaturesTest(String name) {
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
    
    result    = new HeaderDefinition[2];
    result[0] = new HeaderDefinition();
    result[0].add("att1", DataType.NUMERIC);
    result[0].add("att2", DataType.NUMERIC);
    result[0].add("att3", DataType.NUMERIC);
    result[0].add("att4", DataType.NUMERIC);
    result[0].add("att5", DataType.NUMERIC);
    result[0].add("att6", DataType.NUMERIC);
    result[0].add("att7", DataType.NUMERIC);
    
    result[1] = new HeaderDefinition();
    result[1].add("att1", DataType.STRING);
    result[1].add("att2", DataType.STRING);
    result[1].add("att3", DataType.STRING);
    result[1].add("att4", DataType.STRING);
    result[1].add("att5", DataType.STRING);
    result[1].add("att6", DataType.STRING);
    result[1].add("att7", DataType.STRING);
    
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
    
    result = new List[2][3];
    
    result[0][0] = new ArrayList();
    result[0][0].add(1.0);
    result[0][0].add(2.0);
    result[0][0].add(3.0);

    result[0][1] = new ArrayList();
    result[0][1].add(1.0);
    result[0][1].add(2.0);
    result[0][1].add(3.0);
    result[0][1].add(4.0);
    result[0][1].add(5.0);

    result[0][2] = new ArrayList();
    result[0][2].add(1.0);
    result[0][2].add(2.0);
    result[0][2].add(3.0);
    result[0][2].add(4.0);
    result[0][2].add(5.0);
    result[0][2].add(6.0);
    result[0][2].add(7.0);
    
    result[1][0] = new ArrayList();
    result[1][0].add("1.0");
    result[1][0].add("2.0");
    result[1][0].add("3.0");

    result[1][1] = new ArrayList();
    result[1][1].add("1.0");
    result[1][1].add("2.0");
    result[1][1].add("3.0");
    result[1][1].add("4.0");
    result[1][1].add("5.0");

    result[1][2] = new ArrayList();
    result[1][2].add("1.0");
    result[1][2].add("2.0");
    result[1][2].add("3.0");
    result[1][2].add("4.0");
    result[1][2].add("5.0");
    result[1][2].add("6.0");
    result[1][2].add("7.0");
    
    return result;
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractFeatureConverter[] getRegressionSetups() {
    FixedNumFeatures[]	result;
    
    result    = new FixedNumFeatures[2];
    
    result[0] = new FixedNumFeatures();
    result[0].setNumFeatures(5);
    result[0].setFillerNumeric(-1);
    result[0].setFillerType(FillerType.FILLER_NUMERIC);

    result[1] = new FixedNumFeatures();
    result[1].setNumFeatures(5);
    result[1].setFillerType(FillerType.MISSING_STRING);
    
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
    return new TestSuite(FixedNumFeaturesTest.class);
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

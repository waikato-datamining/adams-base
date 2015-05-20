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
 * MultiBoofCVFeatureGeneratorTest.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.boofcv.features;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.env.Environment;

/**
 * Test class for the MultiBoofCVFeatureGenerator flattener. Run from the command line with: <p/>
 * java adams.data.boofcv.features.MultiBoofCVFeatureGeneratorTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4649 $
 */
public class MultiBoofCVFeatureGeneratorTest
  extends AbstractBoofCVFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MultiBoofCVFeatureGeneratorTest(String name) {
    super(name);
  }

  /**
   * Returns the filenames (without path) of the input data files to use
   * in the regression test.
   *
   * @return		the filenames
   */
  @Override
  protected String[] getRegressionInputFiles() {
    return new String[]{
	"adams_icon.png",
	"adams_icon.png",
	"adams_icon.png",
	"adams_icon.png"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBoofCVFeatureGenerator[] getRegressionSetups() {
    MultiBoofCVFeatureGenerator[]	result;
    AbstractBoofCVFeatureGenerator[]	sub;

    result = new MultiBoofCVFeatureGenerator[4];

    result[0] = new MultiBoofCVFeatureGenerator();

    result[1] = new MultiBoofCVFeatureGenerator();
    sub       = new AbstractBoofCVFeatureGenerator[1];
    sub[0]    = new Pixels();
    result[1].setSubGenerators(sub);

    result[2] = new MultiBoofCVFeatureGenerator();
    sub       = new AbstractBoofCVFeatureGenerator[2];
    sub[0]    = new Pixels();
    sub[1]    = new Pixels();
    result[2].setSubGenerators(sub);
    result[2].setPrefix("#-");

    result[3] = new MultiBoofCVFeatureGenerator();
    sub       = new AbstractBoofCVFeatureGenerator[1];
    sub[0]    = new Pixels();
    result[3].setSubGenerators(sub);
    result[3].setFields(new Field[]{new Field(BufferedImageContainer.FIELD_FILENAME, DataType.STRING)});

    return result;
  }

  /**
   * Returns the test suite.
   *
   * @return		the suite
   */
  public static Test suite() {
    return new TestSuite(MultiBoofCVFeatureGeneratorTest.class);
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

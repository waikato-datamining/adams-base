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
 * MultiOpenCVFeatureGeneratorTest.java
 * Copyright (C) 2022 University of Waikato, Hamilton, New Zealand
 */
package adams.data.opencv.features;

import adams.data.image.BufferedImageContainer;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.env.Environment;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Test class for the MultiOpenCVFeatureGenerator feature generator.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 */
public class MultiOpenCVFeatureGeneratorTest
  extends AbstractOpenCVFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MultiOpenCVFeatureGeneratorTest(String name) {
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
  protected AbstractOpenCVFeatureGenerator[] getRegressionSetups() {
    MultiOpenCVFeatureGenerator[]	result;
    AbstractOpenCVFeatureGenerator[]	sub;

    result = new MultiOpenCVFeatureGenerator[4];

    result[0] = new MultiOpenCVFeatureGenerator();

    result[1] = new MultiOpenCVFeatureGenerator();
    sub       = new AbstractOpenCVFeatureGenerator[1];
    sub[0]    = new Otsu();
    result[1].setSubGenerators(sub);

    result[2] = new MultiOpenCVFeatureGenerator();
    sub       = new AbstractOpenCVFeatureGenerator[2];
    sub[0]    = new Otsu();
    sub[1]    = new Otsu();
    result[2].setSubGenerators(sub);
    result[2].setPrefix("#-");

    result[3] = new MultiOpenCVFeatureGenerator();
    sub       = new AbstractOpenCVFeatureGenerator[1];
    sub[0]    = new Otsu();
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
    return new TestSuite(MultiOpenCVFeatureGeneratorTest.class);
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

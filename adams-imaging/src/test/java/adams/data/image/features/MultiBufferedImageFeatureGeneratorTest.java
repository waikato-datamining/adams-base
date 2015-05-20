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
 * MultiBufferedImageFeatureGeneratorTest.java
 * Copyright (C) 2014-2015 University of Waikato, Hamilton, New Zealand
 */
package adams.data.image.features;

import junit.framework.Test;
import junit.framework.TestSuite;
import adams.data.image.BufferedImageContainer;
import adams.data.jai.features.Histogram;
import adams.data.jai.features.Histogram.HistogramType;
import adams.data.report.DataType;
import adams.data.report.Field;
import adams.env.Environment;

/**
 * Test class for the MultiJAIFlattener flattener. Run from the command line with: <br><br>
 * java adams.data.adams.features.MultiBufferedImageFeatureGeneratorTest
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision: 4649 $
 */
public class MultiBufferedImageFeatureGeneratorTest
  extends AbstractBufferedImageFeatureGeneratorTestCase {

  /**
   * Constructs the test case. Called by subclasses.
   *
   * @param name 	the name of the test
   */
  public MultiBufferedImageFeatureGeneratorTest(String name) {
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
	"adams_logo.png",
	"adams_logo.png",
	"adams_logo.png",
	"adams_logo.png"
    };
  }

  /**
   * Returns the setups to use in the regression test.
   *
   * @return		the setups
   */
  @Override
  protected AbstractBufferedImageFeatureGenerator[] getRegressionSetups() {
    MultiBufferedImageFeatureGenerator[]	result;
    AbstractBufferedImageFeatureGenerator[]	sub;

    result = new MultiBufferedImageFeatureGenerator[4];

    result[0] = new MultiBufferedImageFeatureGenerator();

    result[1] = new MultiBufferedImageFeatureGenerator();
    sub       = new AbstractBufferedImageFeatureGenerator[1];
    sub[0]    = new Histogram();
    result[1].setSubGenerators(sub);

    result[2] = new MultiBufferedImageFeatureGenerator();
    sub       = new AbstractBufferedImageFeatureGenerator[2];
    sub[0]    = new Histogram();
    sub[1]    = new Histogram();
    ((Histogram) sub[1]).setHistogramType(HistogramType.EIGHT_BIT);
    result[2].setSubGenerators(sub);
    result[2].setPrefix("#-");

    result[3] = new MultiBufferedImageFeatureGenerator();
    sub       = new AbstractBufferedImageFeatureGenerator[1];
    sub[0]    = new Histogram();
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
    return new TestSuite(MultiBufferedImageFeatureGeneratorTest.class);
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

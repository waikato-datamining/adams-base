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
 * BoofCVTransformerTest.java
 * Copyright (C) 2013-2015 University of Waikato, Hamilton, New Zealand
 */

package adams.flow.transformer;

import adams.core.io.PlaceholderFile;
import adams.data.boofcv.BoofCVImageType;
import adams.data.boofcv.features.Pixels;
import adams.data.boofcv.transformer.Gray8;
import adams.data.conversion.BufferedImageToBoofCV;
import adams.data.jai.transformer.Resize;
import adams.env.Environment;
import adams.flow.AbstractFlowTest;
import adams.flow.control.Flow;
import adams.flow.core.Actor;
import adams.flow.sink.DumpFile;
import adams.flow.source.FileSupplier;
import adams.test.AdamsTestCase;
import adams.test.TmpFile;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Tests the BoofCVTransformer actor.
 *
 * @author  fracpete (fracpete at waikato dot ac dot nz)
 * @version $Revision$
 */
public class BoofCVTransformerTest
  extends AbstractFlowTest {

  /**
   * Initializes the test.
   *
   * @param name	the name of the test
   */
  public BoofCVTransformerTest(String name) {
    super(name);
  }

  /**
   * Called by JUnit before each test method.
   *
   * @throws Exception if an error occurs
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();

    m_TestHelper.copyResourceToTmp("adams_logo.png");
    m_TestHelper.copyResourceToTmp("adams_icon.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");
  }

  /**
   * Called by JUnit after each test method.
   *
   * @throws Exception	if tear-down fails
   */
  @Override
  protected void tearDown() throws Exception {
    m_TestHelper.deleteFileFromTmp("adams_logo.png");
    m_TestHelper.deleteFileFromTmp("adams_icon.png");
    m_TestHelper.deleteFileFromTmp("dumpfile.csv");

    super.tearDown();
  }

  /**
   * Used to create an instance of a specific actor.
   *
   * @return a suitably configured <code>Actor</code> value
   */
  @Override
  public Actor getActor() {
    FileSupplier mfs = new FileSupplier();
    mfs.setFiles(
	new PlaceholderFile[]{
	    new TmpFile("adams_logo.png"),
	    new TmpFile("adams_icon.png")
	});

    ImageFileReader ir = new ImageFileReader();

    BufferedImageTransformer itr = new BufferedImageTransformer();
    Resize res = new Resize();
    res.setWidth(16);
    res.setHeight(16);
    itr.setTransformAlgorithm(res);

    BufferedImageToBoofCV bi2bcv = new BufferedImageToBoofCV();
    bi2bcv.setImageType(BoofCVImageType.GRAYS16);
    Convert con = new Convert();
    con.setConversion(bi2bcv);

    BoofCVTransformer btr = new BoofCVTransformer();
    btr.setTransformAlgorithm(new Gray8());

    BoofCVFeatureGenerator ifl = new BoofCVFeatureGenerator();
    Pixels pix = new Pixels();
    ifl.setAlgorithm(pix);

    DumpFile df = new DumpFile();
    df.setAppend(true);
    df.setOutputFile(new TmpFile("dumpfile.csv"));

    Flow flow = new Flow();
    flow.setActors(new Actor[]{mfs, ir, itr, con, btr, ifl, df});

    return flow;
  }

  /**
   * Performs a regression test, comparing against previously generated output.
   */
  public void testRegression() {
    performRegressionTest(
	new TmpFile("dumpfile.csv"));
  }

  /**
   * Returns a test suite.
   *
   * @return		the test suite
   */
  public static Test suite() {
    return new TestSuite(BoofCVTransformerTest.class);
  }

  /**
   * Runs the test from commandline.
   *
   * @param args	ignored
   */
  public static void main(String[] args) {
    Environment.setEnvironmentClass(Environment.class);
    AdamsTestCase.runTest(suite());
  }
}
